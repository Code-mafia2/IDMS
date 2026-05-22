package dao;

import db.DBConnection;
import model.Satellite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SatelliteDAO {

    // ── INSERT ──────────────────────────────────────────────────────────────
    public boolean insert(Satellite sat) {
        String sql = "INSERT INTO Satellite (satellite_name, orbit_type, launch_date, monitoring_region, org_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sat.getSatelliteName());
            ps.setString(2, sat.getOrbitType());
            if (sat.getLaunchDate() != null) {
                ps.setDate(3, sat.getLaunchDate());
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setString(4, sat.getMonitoringRegion());
            if (sat.getOrgId() > 0) {
                ps.setInt(5, sat.getOrgId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── UPDATE ──────────────────────────────────────────────────────────────
    public boolean update(Satellite sat) {
        String sql = "UPDATE Satellite SET satellite_name=?, orbit_type=?, launch_date=?, monitoring_region=?, org_id=? WHERE satellite_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sat.getSatelliteName());
            ps.setString(2, sat.getOrbitType());
            if (sat.getLaunchDate() != null) {
                ps.setDate(3, sat.getLaunchDate());
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setString(4, sat.getMonitoringRegion());
            if (sat.getOrgId() > 0) {
                ps.setInt(5, sat.getOrgId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setInt(6, sat.getSatelliteId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── DELETE ──────────────────────────────────────────────────────────────
    public boolean delete(int satelliteId) {
        String sql = "DELETE FROM Satellite WHERE satellite_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, satelliteId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── GET ALL ─────────────────────────────────────────────────────────────
    public List<Satellite> getAll() {
        List<Satellite> list = new ArrayList<>();
        String sql = "SELECT * FROM Satellite ORDER BY satellite_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Satellite(
                        rs.getInt("satellite_id"),
                        rs.getString("satellite_name"),
                        rs.getString("orbit_type"),
                        rs.getDate("launch_date"),
                        rs.getString("monitoring_region"),
                        rs.getInt("org_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}