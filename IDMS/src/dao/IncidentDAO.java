package dao;

import db.DBConnection;
import model.Incident;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentDAO {

    // ── INSERT ──────────────────────────────────────────────────────────────
    public boolean insert(Incident inc) {
        String sql = "INSERT INTO Incident (incident_type, severity, incident_date, description, status, system_id, satellite_id, drone_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, inc.getIncidentType());
            ps.setString(2, inc.getSeverity());
            if (inc.getIncidentDate() != null) {
                ps.setTimestamp(3, inc.getIncidentDate());
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            ps.setString(4, inc.getDescription());
            ps.setString(5, inc.getStatus());

            // Nullable FKs
            if (inc.getSystemId() != null && inc.getSystemId() > 0) {
                ps.setInt(6, inc.getSystemId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            if (inc.getSatelliteId() != null && inc.getSatelliteId() > 0) {
                ps.setInt(7, inc.getSatelliteId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            if (inc.getDroneId() != null && inc.getDroneId() > 0) {
                ps.setInt(8, inc.getDroneId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── UPDATE ──────────────────────────────────────────────────────────────
    public boolean update(Incident inc) {
        String sql = "UPDATE Incident SET incident_type=?, severity=?, incident_date=?, description=?, status=?, system_id=?, satellite_id=?, drone_id=? WHERE incident_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, inc.getIncidentType());
            ps.setString(2, inc.getSeverity());
            if (inc.getIncidentDate() != null) {
                ps.setTimestamp(3, inc.getIncidentDate());
            } else {
                ps.setNull(3, Types.TIMESTAMP);
            }
            ps.setString(4, inc.getDescription());
            ps.setString(5, inc.getStatus());

            if (inc.getSystemId() != null && inc.getSystemId() > 0) {
                ps.setInt(6, inc.getSystemId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            if (inc.getSatelliteId() != null && inc.getSatelliteId() > 0) {
                ps.setInt(7, inc.getSatelliteId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            if (inc.getDroneId() != null && inc.getDroneId() > 0) {
                ps.setInt(8, inc.getDroneId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.setInt(9, inc.getIncidentId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── DELETE ──────────────────────────────────────────────────────────────
    public boolean delete(int incidentId) {
        String sql = "DELETE FROM Incident WHERE incident_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, incidentId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── GET ALL ─────────────────────────────────────────────────────────────
    public List<Incident> getAll() {
        List<Incident> list = new ArrayList<>();
        String sql = "SELECT * FROM Incident ORDER BY incident_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Integer sysId = rs.getObject("system_id")    != null ? rs.getInt("system_id")    : null;
                Integer satId = rs.getObject("satellite_id") != null ? rs.getInt("satellite_id") : null;
                Integer drnId = rs.getObject("drone_id")     != null ? rs.getInt("drone_id")     : null;

                list.add(new Incident(
                        rs.getInt("incident_id"),
                        rs.getString("incident_type"),
                        rs.getString("severity"),
                        rs.getTimestamp("incident_date"),
                        rs.getString("description"),
                        rs.getString("status"),
                        sysId,
                        satId,
                        drnId
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}