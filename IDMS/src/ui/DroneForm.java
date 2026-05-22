package ui;

import dao.DroneDAO;
import model.Drone;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DroneForm extends JFrame implements ActionListener {

    // ── Input Fields ─────────────────────────────────────────────────────────
    private JTextField txtDroneId;
    private JTextField txtDroneName;
    private JTextField txtDroneType;
    private JTextField txtStatus;
    private JTextField txtRangeKm;
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
    private final DroneDAO dao = new DroneDAO();

    // ── Constructor ──────────────────────────────────────────────────────────
    public DroneForm() {
        setTitle("Drone Management");
        setSize(860, 660);
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
        header.setBackground(new Color(120, 60, 0));
        header.setBounds(0, 0, 860, 50);
        header.setLayout(null);
        add(header);

        JLabel lbl = new JLabel("DRONE MANAGEMENT");
        lbl.setBounds(0, 10, 860, 30);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setForeground(Color.BLACK);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(lbl);
    }

    private void buildInputPanel() {
        // Left column
        addLabel("Drone ID (auto):", 30, 75);
        txtDroneId = addField(200, 75);
        txtDroneId.setEditable(false);
        txtDroneId.setBackground(new Color(220, 220, 220));

        addLabel("Drone Name *:", 30, 115);
        txtDroneName = addField(200, 115);

        addLabel("Drone Type:", 30, 155);
        txtDroneType = addField(200, 155);

        // Right column
        addLabel("Status:", 460, 75);
        txtStatus = addFieldAt(590, 75);

        addLabel("Range (km):", 460, 115);
        txtRangeKm = addFieldAt(590, 115);

        addLabel("Org ID (0 = none):", 460, 155);
        txtOrgId = addFieldAt(590, 155);
    }

    private void buildButtons() {
        btnInsert  = addButton("Insert",   30, 205, new Color(34, 139, 34));
        btnUpdate  = addButton("Update",  165, 205, new Color(30, 100, 180));
        btnDelete  = addButton("Delete",  300, 205, new Color(180, 30, 30));
        btnViewAll = addButton("View All",435, 205, new Color(100, 60, 160));
        btnClear   = addButton("Clear",   570, 205, new Color(100, 100, 100));
    }

    private void buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"Drone ID", "Drone Name", "Drone Type", "Status", "Range (km)", "Org ID"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(120, 60, 0));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(22);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 255, 810, 370);
        add(scroll);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtDroneId.setText(safeGet(row, 0));
                    txtDroneName.setText(safeGet(row, 1));
                    txtDroneType.setText(safeGet(row, 2));
                    txtStatus.setText(safeGet(row, 3));
                    txtRangeKm.setText(safeGet(row, 4));
                    txtOrgId.setText(safeGet(row, 5));
                }
            }
        });
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void addLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 155, 25);
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
        tf.setBounds(x, y, 220, 28);
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

    private void loadData() {
        tableModel.setRowCount(0);
        List<Drone> list = dao.getAll();
        for (Drone d : list) {
            tableModel.addRow(new Object[]{
                    d.getDroneId(), d.getDroneName(), d.getDroneType(),
                    d.getStatus(), d.getRangeKm(), d.getOrgId()
            });
        }
    }

    private void clearFields() {
        txtDroneId.setText("");
        txtDroneName.setText("");
        txtDroneType.setText("");
        txtStatus.setText("");
        txtRangeKm.setText("");
        txtOrgId.setText("");
        table.clearSelection();
    }

    private int parseIntField(JTextField tf) {
        try { return Integer.parseInt(tf.getText().trim()); }
        catch (NumberFormatException ex) { return 0; }
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
        String name = txtDroneName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Drone Name is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Drone drone = new Drone(0, name,
                txtDroneType.getText().trim(),
                txtStatus.getText().trim(),
                parseIntField(txtRangeKm),
                parseIntField(txtOrgId));

        if (dao.insert(drone)) {
            JOptionPane.showMessageDialog(this, "Drone inserted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Insert failed. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doUpdate() {
        String idStr = txtDroneId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a record first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String name = txtDroneName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Drone Name is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Drone drone = new Drone(Integer.parseInt(idStr), name,
                txtDroneType.getText().trim(),
                txtStatus.getText().trim(),
                parseIntField(txtRangeKm),
                parseIntField(txtOrgId));

        if (dao.update(drone)) {
            JOptionPane.showMessageDialog(this, "Drone updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        String idStr = txtDroneId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a record first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this drone record?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(Integer.parseInt(idStr))) {
                JOptionPane.showMessageDialog(this, "Drone deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData(); clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}