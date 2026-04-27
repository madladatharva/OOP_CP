package com.assessment.service;

import com.assessment.model.Question;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service to handle PDF parsing and Quiz Generation via Gemini API
 */
public class PdfQuizService {

    private static final Logger LOGGER = Logger.getLogger(PdfQuizService.class.getName());
    // TODO: The user must provide their Gemini API Key here or via environment variable
    private static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY") != null ? System.getenv("GEMINI_API_KEY") : "YOUR_GEMINI_API_KEY_HERE";
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + GEMINI_API_KEY;

    private final QuestionService questionService;

    public PdfQuizService(QuestionService questionService) {
        this.questionService = questionService;
    }

    /**
     * Extracts text from an uploaded PDF InputStream
     */
    public String extractTextFromPdf(InputStream pdfStream) throws Exception {
        try (PDDocument document = PDDocument.load(pdfStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            // Limit to first 10 pages to avoid massive prompts
            stripper.setEndPage(10);
            return stripper.getText(document);
        }
    }

    /**
     * Generates questions from text using Gemini API
     */
    public List<Question> generateQuestionsFromText(String text, int numberOfQuestions, int difficulty, String category, int adminId) throws Exception {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Extracted text is empty.");
        }

        // Prepare the prompt
        String difficultyLevelStr = difficulty == 1 ? "Easy" : difficulty == 2 ? "Medium" : "Hard";
        String prompt = "You are an expert quiz generator. Generate " + numberOfQuestions + " multiple-choice questions based on the following text. " +
                "The difficulty level should be " + difficultyLevelStr + ". " +
                "Respond ONLY with a valid JSON array of objects. Do not include markdown formatting like ```json. " +
                "Each object must have the following exact keys: " +
                "\"question_text\" (string), \"option_a\" (string), \"option_b\" (string), \"option_c\" (string), \"option_d\" (string), \"correct_option\" (string, exactly one of 'A', 'B', 'C', or 'D').\n\n" +
                "Text:\n" + text.substring(0, Math.min(text.length(), 25000)); // Limit text size

        // Create JSON payload
        JsonObject payload = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject partsObj = new JsonObject();
        JsonArray partsArray = new JsonArray();
        JsonObject textObj = new JsonObject();
        textObj.addProperty("text", prompt);
        partsArray.add(textObj);
        partsObj.add("parts", partsArray);
        contents.add(partsObj);
        payload.add("contents", contents);

        Gson gson = new Gson();
        String jsonPayload = gson.toJson(payload);

        // Make HTTP Request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            LOGGER.severe("Gemini API Error: " + response.body());
            throw new Exception("Failed to generate questions. API returned status " + response.statusCode());
        }

        // Parse Response
        JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
        String generatedText = jsonResponse.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();

        // Clean up markdown if AI included it
        if (generatedText.startsWith("```json")) {
            generatedText = generatedText.substring(7);
        }
        if (generatedText.startsWith("```")) {
            generatedText = generatedText.substring(3);
        }
        if (generatedText.endsWith("```")) {
            generatedText = generatedText.substring(0, generatedText.length() - 3);
        }

        JsonArray questionsArray = gson.fromJson(generatedText, JsonArray.class);
        List<Question> generatedQuestions = new ArrayList<>();

        for (JsonElement el : questionsArray) {
            JsonObject qObj = el.getAsJsonObject();
            Question q = new Question();
            q.setQuestionText(qObj.get("question_text").getAsString());
            q.setOptionA(qObj.get("option_a").getAsString());
            q.setOptionB(qObj.get("option_b").getAsString());
            q.setOptionC(qObj.get("option_c").getAsString());
            q.setOptionD(qObj.get("option_d").getAsString());
            q.setCorrectOption(qObj.get("correct_option").getAsString());
            q.setDifficultyLevel(difficulty);
            q.setCategory(category != null && !category.trim().isEmpty() ? category : "PDF Generated");
            q.setCreatedBy(adminId);
            generatedQuestions.add(q);
            
            // Save to DB
            questionService.addQuestion(q.getQuestionText(), q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD(), q.getCorrectOption(), q.getDifficultyLevel(), q.getCategory(), q.getCreatedBy());
        }

        return generatedQuestions;
    }
}
