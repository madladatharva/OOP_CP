package com.assessment.dao;

import com.assessment.model.StudyMaterial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access for uploaded theory materials.
 */
public class StudyMaterialDAO extends BaseDAO<StudyMaterial> {

    @Override
    public StudyMaterial findById(int id) throws SQLException {
        String sql = "SELECT * FROM study_materials WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMaterial(rs);
            }
            return null;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public List<StudyMaterial> findAll() throws SQLException {
        String sql = "SELECT * FROM study_materials ORDER BY uploaded_at DESC";
        List<StudyMaterial> materials = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                materials.add(mapResultSetToMaterial(rs));
            }
            return materials;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    @Override
    public int insert(StudyMaterial material) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            return insert(material, conn);
        } finally {
            closeResources(conn, null, null);
        }
    }

    public int insert(StudyMaterial material, Connection conn) throws SQLException {
        String sql = "INSERT INTO study_materials (title, quiz_category, original_filename, "
                + "stored_path, content_type, file_size_bytes, extracted_text, "
                + "generated_question_count, uploaded_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, material.getTitle());
            stmt.setString(2, material.getQuizCategory());
            stmt.setString(3, material.getOriginalFilename());
            stmt.setString(4, material.getStoredPath());
            stmt.setString(5, material.getContentType());
            stmt.setLong(6, material.getFileSizeBytes());
            stmt.setString(7, material.getExtractedText());
            stmt.setInt(8, material.getGeneratedQuestionCount());
            stmt.setInt(9, material.getUploadedBy());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            return -1;
        } finally {
            closeResources(null, stmt, null);
        }
    }

    @Override
    public boolean update(StudyMaterial material) throws SQLException {
        String sql = "UPDATE study_materials SET title = ?, quiz_category = ?, "
                + "generated_question_count = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, material.getTitle());
            stmt.setString(2, material.getQuizCategory());
            stmt.setInt(3, material.getGeneratedQuestionCount());
            stmt.setInt(4, material.getId());
            return stmt.executeUpdate() > 0;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM study_materials WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } finally {
            closeResources(conn, stmt, null);
        }
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM study_materials";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }

    private StudyMaterial mapResultSetToMaterial(ResultSet rs) throws SQLException {
        return new StudyMaterial(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("quiz_category"),
                rs.getString("original_filename"),
                rs.getString("stored_path"),
                rs.getString("content_type"),
                rs.getLong("file_size_bytes"),
                rs.getString("extracted_text"),
                rs.getInt("generated_question_count"),
                rs.getInt("uploaded_by"),
                rs.getTimestamp("uploaded_at"));
    }
}
