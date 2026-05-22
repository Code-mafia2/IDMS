package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame implements ActionListener {

    private JButton btnOrganization;
    private JButton btnSystem;
    private JButton btnDrone;
    private JButton btnSatellite;
    private JButton btnIncident;

    public Dashboard() {
        setTitle("Integrated Defense Monitoring System (IDMS)");
        setSize(620, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Header Panel ────────────────────────────────────────────────────
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(30, 60, 114));
        headerPanel.setBounds(0, 0, 620, 80);
        headerPanel.setLayout(null);
        add(headerPanel);

        JLabel lblTitle = new JLabel("INTEGRATED DEFENSE MONITORING SYSTEM");
        lblTitle.setBounds(40, 10, 540, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 17));
        lblTitle.setForeground(Color.BLACK);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Centralized Defense Operations Dashboard");
        lblSubtitle.setBounds(40, 45, 540, 22);
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(180, 210, 255));
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblSubtitle);

        // ── Section Label ────────────────────────────────────────────────────
        JLabel lblSelect = new JLabel("Select a Module to Manage:");
        lblSelect.setBounds(50, 105, 300, 25);
        lblSelect.setFont(new Font("Arial", Font.BOLD, 13));
        add(lblSelect);

        // ── Navigation Buttons ───────────────────────────────────────────────
        Color btnColor  = new Color(30, 60, 114);
        Color textColor = Color.BLACK;
        Font  btnFont   = new Font("Arial", Font.BOLD, 13);

        btnOrganization = createButton("🏢  Organization",  50, 150, btnColor, textColor, btnFont);
        btnSystem       = createButton("🖥  Systems",        50, 215, btnColor, textColor, btnFont);
        btnDrone        = createButton("🚁  Drones",         320, 150, btnColor, textColor, btnFont);
        btnSatellite    = createButton("🛰  Satellites",     320, 215, btnColor, textColor, btnFont);
        btnIncident     = createButton("⚠  Incidents",      185, 290, btnColor, textColor, btnFont);

        // ── Footer ───────────────────────────────────────────────────────────
        JLabel lblFooter = new JLabel("IDMS v1.0  |  PostgreSQL + JDBC + Java Swing");
        lblFooter.setBounds(0, 440, 620, 25);
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        lblFooter.setFont(new Font("Arial", Font.ITALIC, 11));
        lblFooter.setForeground(Color.GRAY);
        add(lblFooter);

        setVisible(true);
    }

    // Helper to create styled buttons
    private JButton createButton(String label, int x, int y, Color bg, Color fg, Font font) {
        JButton btn = new JButton(label);
        btn.setBounds(x, y, 245, 55);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(this);
        add(btn);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnOrganization) {
            new OrganizationForm();
        } else if (src == btnSystem) {
            new SystemForm();
        } else if (src == btnDrone) {
            new DroneForm();
        } else if (src == btnSatellite) {
            new SatelliteForm();
        } else if (src == btnIncident) {
            new IncidentForm();
        }
    }
}