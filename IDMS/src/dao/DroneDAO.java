package dao;

import db.DBConnection;
import model.Drone;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DroneDAO {

    // ── INSERT ──────────────────────────────────────────────────────────────
    public boolean insert(Drone drone) {
        String sql = "INSERT INTO Drone (drone_name, drone_type, status, range_km, org_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, drone.getDroneName());
            ps.setString(2, drone.getDroneType());
            ps.setString(3, drone.getStatus());
            ps.setInt(4, drone.getRangeKm());
            if (drone.getOrgId() > 0) {
                ps.setInt(5, drone.getOrgId());
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
    public boolean update(Drone drone) {
        String sql = "UPDATE Drone SET drone_name=?, drone_type=?, status=?, range_km=?, org_id=? WHERE drone_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, drone.getDroneName());
            ps.setString(2, drone.getDroneType());
            ps.setString(3, drone.getStatus());
            ps.setInt(4, drone.getRangeKm());
            if (drone.getOrgId() > 0) {
                ps.setInt(5, drone.getOrgId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setInt(6, drone.getDroneId());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── DELETE ──────────────────────────────────────────────────────────────
    public boolean delete(int droneId) {
        String sql = "DELETE FROM Drone WHERE drone_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, droneId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── GET ALL ─────────────────────────────────────────────────────────────
    public List<Drone> getAll() {
        List<Drone> list = new ArrayList<>();
        String sql = "SELECT * FROM Drone ORDER BY drone_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Drone(
                        rs.getInt("drone_id"),
                        rs.getString("drone_name"),
                        rs.getString("drone_type"),
                        rs.getString("status"),
                        rs.getInt("range_km"),
                        rs.getInt("org_id")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}