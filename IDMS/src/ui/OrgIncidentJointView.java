package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OrgIncidentJointView extends JFrame {

    private JTable            table;
    private DefaultTableModel tableModel;

    // ── SQL JOIN Query ──────────────────────────────────────────────────────
    private static final String JOINT_SQL =
            "SELECT o.org_name, i.incident_date, i.description " +
            "FROM Organization o " +
            "INNER JOIN System_Table s  ON s.org_id = o.org_id " +
            "INNER JOIN Incident i      ON i.system_id = s.system_id " +
            "UNION " +
            "SELECT o.org_name, i.incident_date, i.description " +
            "FROM Organization o " +
            "INNER JOIN Satellite sat   ON sat.org_id = o.org_id " +
            "INNER JOIN Incident i      ON i.satellite_id = sat.satellite_id " +
            "UNION " +
            "SELECT o.org_name, i.incident_date, i.description " +
            "FROM Organization o " +
            "INNER JOIN Drone d          ON d.org_id = o.org_id " +
            "INNER JOIN Incident i       ON i.drone_id = d.drone_id " +
            "ORDER BY incident_date DESC";

    public OrgIncidentJointView() {
        setTitle("Organization – Incident Joint View");
        setSize(850, 550);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        buildHeader();
        buildTable();
        loadJointData();

        setVisible(true);
    }

    private void buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(new Color(30, 60, 114));
        header.setBounds(0, 0, 850, 50);
        header.setLayout(null);
        add(header);

        JLabel lbl = new JLabel("ORGANIZATION – INCIDENT JOINT VIEW");
        lbl.setBounds(0, 10, 850, 30);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setForeground(Color.BLACK);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(lbl);
    }

    private void buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"Organization Name", "Incident Date", "Description"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(30, 60, 114));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(22);
        table.setFont(new Font("Arial", Font.PLAIN, 12));

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(400);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 65, 805, 440);
        add(scroll);
    }

    private void loadJointData() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(JOINT_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("org_name"),
                        rs.getTimestamp("incident_date"),
                        rs.getString("description")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to load joint data. Check console for details.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
