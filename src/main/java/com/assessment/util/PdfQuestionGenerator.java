package com.assessment.util;

import com.assessment.model.Question;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts theory text from PDFs and converts it into multiple-choice questions.
 */
public final class PdfQuestionGenerator {

    private static final Pattern SENTENCE_SPLIT = Pattern.compile("(?<=[.!?])\\s+");
    private static final Pattern UPPERCASE_TERM =
            Pattern.compile("\\b[A-Z]{2,}(?:\\s+[A-Z]{2,}){0,2}\\b");
    private static final Pattern TITLE_CASE_TERM =
            Pattern.compile("\\b[A-Z][a-zA-Z]+(?:\\s+[A-Z][a-zA-Z]+){1,3}\\b");
    private static final Pattern WORD_PATTERN =
            Pattern.compile("\\b[A-Za-z][A-Za-z0-9-]{4,}\\b");

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "about", "above", "after", "again", "against", "along", "also", "among",
            "because", "being", "below", "between", "cannot", "could", "different",
            "during", "each", "every", "first", "from", "further", "having", "into",
            "itself", "least", "might", "other", "otherwise", "should", "since",
            "there", "their", "these", "those", "through", "under", "until", "using",
            "where", "which", "while", "within", "would", "whose", "system", "method",
            "process", "example", "theory", "topic", "concept", "study", "material"));

    private PdfQuestionGenerator() {
    }

    public static String extractText(Path pdfPath) throws IOException {
        try (PDDocument document = PDDocument.load(pdfPath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return normalizeText(text);
        }
    }

    public static List<Question> generateQuestions(String extractedText, String category,
            int requestedCount) {
        if (extractedText == null || extractedText.trim().isEmpty()) {
            throw new IllegalArgumentException("The uploaded PDF did not contain readable text.");
        }

        List<String> sentences = extractCandidateSentences(extractedText);
        if (sentences.size() < 4) {
            throw new IllegalArgumentException(
                    "The PDF needs more theory-style text before quiz questions can be generated.");
        }

        LinkedHashMap<String, String> answerBySentence = new LinkedHashMap<>();
        LinkedHashSet<String> phrasePool = new LinkedHashSet<>();
        Set<String> seenPairs = new HashSet<>();

        for (String sentence : sentences) {
            String answerPhrase = extractAnswerPhrase(sentence);
            if (answerPhrase == null) {
                continue;
            }

            String uniqueKey = sentence.toLowerCase(Locale.ROOT) + "::"
                    + answerPhrase.toLowerCase(Locale.ROOT);
            if (seenPairs.add(uniqueKey)) {
                answerBySentence.put(sentence, answerPhrase);
                phrasePool.add(answerPhrase);
            }
        }

        if (phrasePool.size() < 4) {
            throw new IllegalArgumentException(
                    "The PDF did not contain enough distinct concepts to generate four-option questions.");
        }

        List<Map.Entry<String, String>> entries = new ArrayList<>(answerBySentence.entrySet());
        Collections.shuffle(entries);

        List<Question> generatedQuestions = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries) {
            if (generatedQuestions.size() >= requestedCount) {
                break;
            }

            String sentence = entry.getKey();
            String answerPhrase = entry.getValue();
            String questionText = buildQuestionText(sentence, answerPhrase);
            if (questionText == null) {
                continue;
            }

            List<String> options = buildOptions(answerPhrase, phrasePool);
            if (options.size() < 4) {
                continue;
            }

            String correctOption = findCorrectOption(options, answerPhrase);
            int difficulty = inferDifficulty(sentence, answerPhrase);

            Question question = new Question(questionText,
                    options.get(0), options.get(1), options.get(2), options.get(3),
                    correctOption, difficulty, category);
            generatedQuestions.add(question);
        }

        if (generatedQuestions.isEmpty()) {
            throw new IllegalArgumentException(
                    "The PDF text could be extracted, but it was too noisy to generate quiz questions.");
        }

        return generatedQuestions;
    }

    private static String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        return text
                .replaceAll("-\\s+", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static List<String> extractCandidateSentences(String extractedText) {
        String normalized = normalizeText(extractedText);
        String[] rawSentences = SENTENCE_SPLIT.split(normalized);

        List<String> sentences = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String rawSentence : rawSentences) {
            String sentence = rawSentence.trim();
            if (!isLikelyTheorySentence(sentence)) {
                continue;
            }

            String dedupeKey = sentence.toLowerCase(Locale.ROOT);
            if (seen.add(dedupeKey)) {
                sentences.add(sentence);
            }
        }
        return sentences;
    }

    private static boolean isLikelyTheorySentence(String sentence) {
        if (sentence == null || sentence.isEmpty()) {
            return false;
        }
        if (sentence.length() < 50 || sentence.length() > 220) {
            return false;
        }
        String lower = sentence.toLowerCase(Locale.ROOT);
        if (lower.contains("http") || lower.contains("www.") || lower.contains("copyright")
                || lower.contains("figure ") || lower.contains("table ")) {
            return false;
        }

        int wordCount = sentence.split("\\s+").length;
        if (wordCount < 8 || wordCount > 28) {
            return false;
        }

        int letters = 0;
        int digits = 0;
        for (char c : sentence.toCharArray()) {
            if (Character.isLetter(c)) {
                letters++;
            } else if (Character.isDigit(c)) {
                digits++;
            }
        }

        if (letters < 35 || digits > Math.max(8, letters / 2)) {
            return false;
        }

        return !sentence.equals(sentence.toUpperCase(Locale.ROOT));
    }

    private static String extractAnswerPhrase(String sentence) {
        String answer = firstUsefulMatch(UPPERCASE_TERM.matcher(sentence));
        if (answer != null) {
            return answer;
        }

        answer = firstUsefulMatch(TITLE_CASE_TERM.matcher(sentence));
        if (answer != null) {
            return answer;
        }

        Matcher wordMatcher = WORD_PATTERN.matcher(sentence);
        String bestWord = null;
        while (wordMatcher.find()) {
            String candidate = wordMatcher.group();
            if (STOP_WORDS.contains(candidate.toLowerCase(Locale.ROOT))) {
                continue;
            }
            if (bestWord == null || candidate.length() > bestWord.length()) {
                bestWord = candidate;
            }
        }

        return bestWord;
    }

    private static String firstUsefulMatch(Matcher matcher) {
        while (matcher.find()) {
            String candidate = matcher.group().trim();
            String lower = candidate.toLowerCase(Locale.ROOT);
            if (candidate.length() < 4 || STOP_WORDS.contains(lower)) {
                continue;
            }
            if ("The".equals(candidate) || "This".equals(candidate) || "These".equals(candidate)) {
                continue;
            }
            return candidate;
        }
        return null;
    }

    private static String buildQuestionText(String sentence, String answerPhrase) {
        int index = sentence.toLowerCase(Locale.ROOT).indexOf(answerPhrase.toLowerCase(Locale.ROOT));
        if (index < 0) {
            return null;
        }

        String blanked = sentence.substring(0, index) + "_____"
                + sentence.substring(index + answerPhrase.length());
        if (!blanked.contains("_____")) {
            return null;
        }

        return "According to the uploaded study material, which option best completes: \""
                + blanked + "\"";
    }

    private static List<String> buildOptions(String correctAnswer, Set<String> phrasePool) {
        List<String> distractors = new ArrayList<>();
        int correctWordCount = correctAnswer.split("\\s+").length;

        for (String candidate : phrasePool) {
            if (candidate.equalsIgnoreCase(correctAnswer)) {
                continue;
            }

            int candidateWordCount = candidate.split("\\s+").length;
            if (Math.abs(candidateWordCount - correctWordCount) > 2) {
                continue;
            }

            distractors.add(candidate);
        }

        Collections.shuffle(distractors);

        LinkedHashSet<String> options = new LinkedHashSet<>();
        options.add(correctAnswer);
        for (String distractor : distractors) {
            options.add(distractor);
            if (options.size() == 4) {
                break;
            }
        }

        int fallbackIndex = 0;
        while (options.size() < 4) {
            String fallback = buildFallbackDistractor(correctAnswer, fallbackIndex++);
            options.add(fallback);
        }

        List<String> shuffledOptions = new ArrayList<>(options);
        Collections.shuffle(shuffledOptions);
        return shuffledOptions;
    }

    private static String buildFallbackDistractor(String correctAnswer, int variantIndex) {
        String[] suffixes = {" pattern", " model", " workflow", " structure"};
        if (correctAnswer.contains(" ")) {
            String[] tokens = correctAnswer.split("\\s+");
            int replaceIndex = Math.min(tokens.length - 1, variantIndex % tokens.length);
            tokens[replaceIndex] = variantIndex % 2 == 0 ? "framework" : "method";
            return String.join(" ", tokens);
        }
        return correctAnswer + suffixes[variantIndex % suffixes.length];
    }

    private static String findCorrectOption(List<String> options, String correctAnswer) {
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).equalsIgnoreCase(correctAnswer)) {
                return String.valueOf((char) ('A' + i));
            }
        }
        return "A";
    }

    private static int inferDifficulty(String sentence, String answerPhrase) {
        int wordCount = sentence.split("\\s+").length;
        int answerWords = answerPhrase.split("\\s+").length;

        if (wordCount <= 12 && answerWords <= 2) {
            return 1;
        }
        if (wordCount <= 18 && answerWords <= 3) {
            return 2;
        }
        return 3;
    }
}
