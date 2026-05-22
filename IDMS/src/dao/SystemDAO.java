package dao;

import db.DBConnection;
import model.SystemModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SystemDAO {

    // ── INSERT ──────────────────────────────────────────────────────────────
    public boolean insert(SystemModel system) {
        String sql = "INSERT INTO System_Table (system_name, system_type, status, org_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, system.getSystemName());
            ps.setString(2, system.getSystemType());
            ps.setString(3, system.getStatus());
            if (system.getOrgId() > 0) {
                ps.setInt(4, system.getOrgId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── UPDATE ──────────────────────────────────────────────────────────────
    public boolean update(SystemModel system) {
        String sql = "UPDATE System_Table SET system_name=?, system_type=?, status=?, org_id=? WHERE system_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, system.getSystemName());
            ps.setString(2, system.getSystemType());
            ps.setString(3, system.getStatus());
            if (system.getOrgId() > 0) {
                ps.setInt(4, system.getOrgId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setInt(5, system.getSystemId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── DELETE ──────────────────────────────────────────────────────────────
    public boolean delete(int systemId) {
        String sql = "DELETE FROM System_Table WHERE system_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, systemId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── GET ALL ─────────────────────────────────────────────────────────────
    public List<SystemModel> getAll() {
        List<SystemModel> list = new ArrayList<>();
        String sql = "SELECT * FROM System_Table ORDER BY system_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new SystemModel(
                        rs.getInt("system_id"),
                        rs.getString("system_name"),
                        rs.getString("system_type"),
                        rs.getString("status"),
                        rs.getInt("org_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}