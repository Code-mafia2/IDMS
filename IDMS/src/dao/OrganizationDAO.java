package dao;

import db.DBConnection;
import model.Organization;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrganizationDAO {

    // ── INSERT ──────────────────────────────────────────────────────────────
    public boolean insert(Organization org) {
        String sql = "INSERT INTO Organization (org_name, location, contact_email) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, org.getOrgName());
            ps.setString(2, org.getLocation());
            ps.setString(3, org.getContactEmail());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── UPDATE ──────────────────────────────────────────────────────────────
    public boolean update(Organization org) {
        String sql = "UPDATE Organization SET org_name=?, location=?, contact_email=? WHERE org_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, org.getOrgName());
            ps.setString(2, org.getLocation());
            ps.setString(3, org.getContactEmail());
            ps.setInt(4, org.getOrgId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── DELETE ──────────────────────────────────────────────────────────────
    public boolean delete(int orgId) {
        String sql = "DELETE FROM Organization WHERE org_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orgId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── GET ALL ─────────────────────────────────────────────────────────────
    public List<Organization> getAll() {
        List<Organization> list = new ArrayList<>();
        String sql = "SELECT * FROM Organization ORDER BY org_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Organization(
                        rs.getInt("org_id"),
                        rs.getString("org_name"),
                        rs.getString("location"),
                        rs.getString("contact_email")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}