package ui;

import dao.IncidentDAO;
import model.Incident;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.util.List;

public class IncidentForm extends JFrame implements ActionListener {

    // ── Input Fields ─────────────────────────────────────────────────────────
    private JTextField txtIncidentId;
    private JTextField txtIncidentType;
    private JTextField txtSeverity;
    private JTextField txtIncidentDate;   // format: YYYY-MM-DD HH:MM:SS
    private JTextField txtDescription;
    private JTextField txtStatus;
    private JTextField txtSystemId;       // nullable FK
    private JTextField txtSatelliteId;    // nullable FK
    private JTextField txtDroneId;        // nullable FK

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
    private final IncidentDAO dao = new IncidentDAO();

    // ── Constructor ──────────────────────────────────────────────────────────
    public IncidentForm() {
        setTitle("Incident Management");
        setSize(1050, 730);
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
        header.setBackground(new Color(160, 40, 40));
        header.setBounds(0, 0, 1050, 50);
        header.setLayout(null);
        add(header);

        JLabel lbl = new JLabel("INCIDENT MANAGEMENT");
        lbl.setBounds(0, 10, 1050, 30);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setForeground(Color.WHITE);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(lbl);
    }

    private void buildInputPanel() {
        // ── Column 1 (x=30) ─────────────────────────────────────────────────
        addLabel("Incident ID (auto):", 30, 70);
        txtIncidentId = addField(200, 70, 150);
        txtIncidentId.setEditable(false);
        txtIncidentId.setBackground(new Color(220, 220, 220));

        addLabel("Incident Type:", 30, 110);
        txtIncidentType = addField(200, 110, 200);

        addLabel("Severity:", 30, 150);
        txtSeverity = addField(200, 150, 150);

        addLabel("Description:", 30, 190);
        txtDescription = addField(200, 190, 300);

        addLabel("Status:", 30, 230);
        txtStatus = addField(200, 230, 150);

        // ── Column 2 (x=550) ────────────────────────────────────────────────
        addLabel("Incident Date:", 550, 70);
        txtIncidentDate = addField(700, 70, 250);
        // Hint label
        JLabel hint = new JLabel("(YYYY-MM-DD HH:MM:SS)");
        hint.setBounds(700, 96, 230, 16);
        hint.setFont(new Font("Arial", Font.ITALIC, 10));
        hint.setForeground(Color.GRAY);
        add(hint);

        addLabel("System ID (0=none):", 550, 125);
        txtSystemId = addField(730, 125, 120);

        addLabel("Satellite ID (0=none):", 550, 165);
        txtSatelliteId = addField(740, 165, 120);

        addLabel("Drone ID (0=none):", 550, 205);
        txtDroneId = addField(720, 205, 120);
    }

    private void buildButtons() {
        btnInsert  = addButton("Insert",   30, 280, new Color(34, 139, 34));
        btnUpdate  = addButton("Update",  175, 280, new Color(30, 100, 180));
        btnDelete  = addButton("Delete",  320, 280, new Color(180, 30, 30));
        btnViewAll = addButton("View All",465, 280, new Color(100, 60, 160));
        btnClear   = addButton("Clear",   610, 280, new Color(100, 100, 100));
    }

    private void buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Type", "Severity", "Date", "Description", "Status", "SysID", "SatID", "DrnID"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(160, 40, 40));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        table.setRowHeight(22);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(3).setPreferredWidth(140);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 335, 1005, 355);
        add(scroll);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtIncidentId.setText(safeGet(row, 0));
                    txtIncidentType.setText(safeGet(row, 1));
                    txtSeverity.setText(safeGet(row, 2));
                    txtIncidentDate.setText(safeGet(row, 3));
                    txtDescription.setText(safeGet(row, 4));
                    txtStatus.setText(safeGet(row, 5));
                    txtSystemId.setText(safeGet(row, 6));
                    txtSatelliteId.setText(safeGet(row, 7));
                    txtDroneId.setText(safeGet(row, 8));
                }
            }
        });
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void addLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 195, 25);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        add(lbl);
    }

    private JTextField addField(int x, int y, int width) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, width, 28);
        add(tf);
        return tf;
    }

    private JButton addButton(String label, int x, int y, Color bg) {
        JButton btn = new JButton(label);
        btn.setBounds(x, y, 130, 32);
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

    /** Parse "YYYY-MM-DD HH:MM:SS" → Timestamp, null on blank, shows error on bad format */
    private Timestamp parseTimestamp() {
        String raw = txtIncidentDate.getText().trim();
        if (raw.isEmpty()) return null;
        try {
            return Timestamp.valueOf(raw);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date-time format.\nUse: YYYY-MM-DD HH:MM:SS  (e.g. 2024-06-15 09:30:00)",
                    "Date Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    /** Parse a nullable FK text field → Integer (null if blank or 0) */
    private Integer parseNullableId(JTextField tf) {
        String raw = tf.getText().trim();
        if (raw.isEmpty()) return null;
        try {
            int val = Integer.parseInt(raw);
            return val > 0 ? val : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Incident> list = dao.getAll();
        for (Incident inc : list) {
            tableModel.addRow(new Object[]{
                    inc.getIncidentId(),
                    inc.getIncidentType(),
                    inc.getSeverity(),
                    inc.getIncidentDate(),
                    inc.getDescription(),
                    inc.getStatus(),
                    inc.getSystemId(),
                    inc.getSatelliteId(),
                    inc.getDroneId()
            });
        }
    }

    private void clearFields() {
        txtIncidentId.setText("");
        txtIncidentType.setText("");
        txtSeverity.setText("");
        txtIncidentDate.setText("");
        txtDescription.setText("");
        txtStatus.setText("");
        txtSystemId.setText("");
        txtSatelliteId.setText("");
        txtDroneId.setText("");
        table.clearSelection();
    }

    /** Build Incident from current field values */
    private Incident buildFromFields(int id) {
        Timestamp ts = parseTimestamp();
        String raw = txtIncidentDate.getText().trim();
        if (!raw.isEmpty() && ts == null) return null; // bad timestamp already notified

        return new Incident(id,
                txtIncidentType.getText().trim(),
                txtSeverity.getText().trim(),
                ts,
                txtDescription.getText().trim(),
                txtStatus.getText().trim(),
                parseNullableId(txtSystemId),
                parseNullableId(txtSatelliteId),
                parseNullableId(txtDroneId));
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
        Incident inc = buildFromFields(0);
        if (inc == null) return; // timestamp parsing failed

        if (dao.insert(inc)) {
            JOptionPane.showMessageDialog(this, "Incident inserted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Insert failed. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doUpdate() {
        String idStr = txtIncidentId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a record first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Incident inc = buildFromFields(Integer.parseInt(idStr));
        if (inc == null) return;

        if (dao.update(inc)) {
            JOptionPane.showMessageDialog(this, "Incident updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        String idStr = txtIncidentId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a record first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this incident record?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(Integer.parseInt(idStr))) {
                JOptionPane.showMessageDialog(this, "Incident deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData(); clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}