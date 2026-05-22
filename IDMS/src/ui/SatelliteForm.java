package ui;

import dao.SatelliteDAO;
import model.Satellite;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.util.List;

public class SatelliteForm extends JFrame implements ActionListener {

    // ── Input Fields ─────────────────────────────────────────────────────────
    private JTextField txtSatelliteId;
    private JTextField txtSatelliteName;
    private JTextField txtOrbitType;
    private JTextField txtLaunchDate;     // format: YYYY-MM-DD
    private JTextField txtMonitoringRegion;
    private JTextField txtOrgId;

    // ── Buttons ──────────────────────────────────────────────────────────────
    private JButton btnInsert;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnViewAll;
    private JButton btnClear;

    // ── Table ────────────────────────────────────────────────────────────────
    private JTable            table;
    private DefaultTableModel tableModel;

    // ── DAO ──────────────────────────────────────────────────────────────────
    private final SatelliteDAO dao = new SatelliteDAO();

    // ── Constructor ──────────────────────────────────────────────────────────
    public SatelliteForm() {
        setTitle("Satellite Management");
        setSize(900, 670);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        buildHeader();
        buildInputPanel();
        buildButtons();
        buildTable();

        loadData();
        setVisible(true);
    }

    // ── UI Builders ──────────────────────────────────────────────────────────

    private void buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(new Color(70, 0, 120));
        header.setBounds(0, 0, 900, 50);
        header.setLayout(null);
        add(header);

        JLabel lbl = new JLabel("SATELLITE MANAGEMENT");
        lbl.setBounds(0, 10, 900, 30);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setForeground(Color.BLACK);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(lbl);
    }

    private void buildInputPanel() {
        // Left column
        addLabel("Satellite ID (auto):", 30, 75);
        txtSatelliteId = addField(210, 75);
        txtSatelliteId.setEditable(false);
        txtSatelliteId.setBackground(new Color(220, 220, 220));

        addLabel("Satellite Name *:", 30, 115);
        txtSatelliteName = addField(210, 115);

        addLabel("Orbit Type:", 30, 155);
        txtOrbitType = addField(210, 155);

        // Right column
        addLabel("Launch Date (YYYY-MM-DD):", 470, 75);
        txtLaunchDate = addFieldAt(700, 75);

        addLabel("Monitoring Region:", 470, 115);
        txtMonitoringRegion = addFieldAt(700, 115);

        addLabel("Org ID (0 = none):", 470, 155);
        txtOrgId = addFieldAt(700, 155);
    }

    private void buildButtons() {
        btnInsert  = addButton("Insert",   30, 210, new Color(34, 139, 34));
        btnUpdate  = addButton("Update",  165, 210, new Color(30, 100, 180));
        btnDelete  = addButton("Delete",  300, 210, new Color(180, 30, 30));
        btnViewAll = addButton("View All",435, 210, new Color(100, 60, 160));
        btnClear   = addButton("Clear",   570, 210, new Color(100, 100, 100));
    }

    private void buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"Satellite ID", "Satellite Name", "Orbit Type", "Launch Date", "Region", "Org ID"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(70, 0, 120));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(22);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 260, 850, 375);
        add(scroll);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtSatelliteId.setText(safeGet(row, 0));
                    txtSatelliteName.setText(safeGet(row, 1));
                    txtOrbitType.setText(safeGet(row, 2));
                    txtLaunchDate.setText(safeGet(row, 3));
                    txtMonitoringRegion.setText(safeGet(row, 4));
                    txtOrgId.setText(safeGet(row, 5));
                }
            }
        });
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void addLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 220, 25);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        add(lbl);
    }

    private JTextField addField(int x, int y) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, 220, 28);
        add(tf);
        return tf;
    }

    private JTextField addFieldAt(int x, int y) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, 155, 28);
        add(tf);
        return tf;
    }

    private JButton addButton(String label, int x, int y, Color bg) {
        JButton btn = new JButton(label);
        btn.setBounds(x, y, 120, 32);
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.addActionListener(this);
        add(btn);
        return btn;
    }

    private String safeGet(int row, int col) {
        Object val = tableModel.getValueAt(row, col);
        return val == null ? "" : val.toString();
    }

    /** Parse date string YYYY-MM-DD → java.sql.Date, or null on blank/error */
    private Date parseLaunchDate() {
        String raw = txtLaunchDate.getText().trim();
        if (raw.isEmpty()) return null;
        try {
            return Date.valueOf(raw);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Use YYYY-MM-DD (e.g. 2024-06-15).",
                    "Date Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private int parseOrgId() {
        try { return Integer.parseInt(txtOrgId.getText().trim()); }
        catch (NumberFormatException ex) { return 0; }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Satellite> list = dao.getAll();
        for (Satellite s : list) {
            tableModel.addRow(new Object[]{
                    s.getSatelliteId(), s.getSatelliteName(), s.getOrbitType(),
                    s.getLaunchDate(), s.getMonitoringRegion(), s.getOrgId()
            });
        }
    }

    private void clearFields() {
        txtSatelliteId.setText("");
        txtSatelliteName.setText("");
        txtOrbitType.setText("");
        txtLaunchDate.setText("");
        txtMonitoringRegion.setText("");
        txtOrgId.setText("");
        table.clearSelection();
    }

    // ── Action Handler ───────────────────────────────────────────────────────

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if      (src == btnInsert)  doInsert();
        else if (src == btnUpdate)  doUpdate();
        else if (src == btnDelete)  doDelete();
        else if (src == btnViewAll) loadData();
        else if (src == btnClear)   clearFields();
    }

    private void doInsert() {
        String name = txtSatelliteName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Satellite Name is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Date launchDate = parseLaunchDate();
        // If user typed something but it was invalid, parseLaunchDate shows error and returns null
        // We still allow null (blank date); only block if user typed a bad value
        String raw = txtLaunchDate.getText().trim();
        if (!raw.isEmpty() && launchDate == null) return; // parsing failed, already showed message

        Satellite sat = new Satellite(0, name,
                txtOrbitType.getText().trim(), launchDate,
                txtMonitoringRegion.getText().trim(), parseOrgId());

        if (dao.insert(sat)) {
            JOptionPane.showMessageDialog(this, "Satellite inserted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Insert failed. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doUpdate() {
        String idStr = txtSatelliteId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a record first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String name = txtSatelliteName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Satellite Name is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Date launchDate = parseLaunchDate();
        String raw = txtLaunchDate.getText().trim();
        if (!raw.isEmpty() && launchDate == null) return;

        Satellite sat = new Satellite(Integer.parseInt(idStr), name,
                txtOrbitType.getText().trim(), launchDate,
                txtMonitoringRegion.getText().trim(), parseOrgId());

        if (dao.update(sat)) {
            JOptionPane.showMessageDialog(this, "Satellite updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        String idStr = txtSatelliteId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a record first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this satellite record?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(Integer.parseInt(idStr))) {
                JOptionPane.showMessageDialog(this, "Satellite deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData(); clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}