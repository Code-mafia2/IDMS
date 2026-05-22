package ui;

import dao.SystemDAO;
import model.SystemModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SystemForm extends JFrame implements ActionListener {

    // ── Input Fields ─────────────────────────────────────────────────────────
    private JTextField txtSystemId;
    private JTextField txtSystemName;
    private JTextField txtSystemType;
    private JTextField txtStatus;
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
    private final SystemDAO dao = new SystemDAO();

    // ── Constructor ──────────────────────────────────────────────────────────
    public SystemForm() {
        setTitle("System Management");
        setSize(820, 630);
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
        header.setBackground(new Color(20, 90, 50));
        header.setBounds(0, 0, 820, 50);
        header.setLayout(null);
        add(header);

        JLabel lbl = new JLabel("SYSTEM MANAGEMENT");
        lbl.setBounds(0, 10, 820, 30);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setForeground(Color.BLACK);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(lbl);
    }

    private void buildInputPanel() {
        addLabel("System ID (auto):", 30, 75);
        txtSystemId = addField(200, 75);
        txtSystemId.setEditable(false);
        txtSystemId.setBackground(new Color(220, 220, 220));

        addLabel("System Name *:", 30, 115);
        txtSystemName = addField(200, 115);

        addLabel("System Type:", 30, 155);
        txtSystemType = addField(200, 155);

        addLabel("Status:", 30, 195);
        txtStatus = addField(200, 195);

        addLabel("Org ID (0 = none):", 30, 235);
        txtOrgId = addField(200, 235);
    }

    private void buildButtons() {
        btnInsert  = addButton("Insert",   30, 285, new Color(34, 139, 34));
        btnUpdate  = addButton("Update",  165, 285, new Color(30, 100, 180));
        btnDelete  = addButton("Delete",  300, 285, new Color(180, 30, 30));
        btnViewAll = addButton("View All",435, 285, new Color(100, 60, 160));
        btnClear   = addButton("Clear",   570, 285, new Color(100, 100, 100));
    }

    private void buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"System ID", "System Name", "System Type", "Status", "Org ID"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(20, 90, 50));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(22);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 330, 770, 260);
        add(scroll);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtSystemId.setText(safeGet(row, 0));
                    txtSystemName.setText(safeGet(row, 1));
                    txtSystemType.setText(safeGet(row, 2));
                    txtStatus.setText(safeGet(row, 3));
                    txtOrgId.setText(safeGet(row, 4));
                }
            }
        });
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void addLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 165, 25);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        add(lbl);
    }

    private JTextField addField(int x, int y) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, 260, 28);
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
        List<SystemModel> list = dao.getAll();
        for (SystemModel s : list) {
            tableModel.addRow(new Object[]{
                    s.getSystemId(), s.getSystemName(), s.getSystemType(), s.getStatus(), s.getOrgId()
            });
        }
    }

    private void clearFields() {
        txtSystemId.setText("");
        txtSystemName.setText("");
        txtSystemType.setText("");
        txtStatus.setText("");
        txtOrgId.setText("");
        table.clearSelection();
    }

    private int parseOrgId() {
        String val = txtOrgId.getText().trim();
        if (val.isEmpty()) return 0;
        try { return Integer.parseInt(val); } catch (NumberFormatException ex) { return 0; }
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
        String name = txtSystemName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "System Name is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SystemModel sys = new SystemModel(0, name,
                txtSystemType.getText().trim(), txtStatus.getText().trim(), parseOrgId());

        if (dao.insert(sys)) {
            JOptionPane.showMessageDialog(this, "System inserted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Insert failed. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doUpdate() {
        String idStr = txtSystemId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a record first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String name = txtSystemName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "System Name is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SystemModel sys = new SystemModel(Integer.parseInt(idStr), name,
                txtSystemType.getText().trim(), txtStatus.getText().trim(), parseOrgId());

        if (dao.update(sys)) {
            JOptionPane.showMessageDialog(this, "System updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData(); clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        String idStr = txtSystemId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a record first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this system record?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(Integer.parseInt(idStr))) {
                JOptionPane.showMessageDialog(this, "System deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData(); clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed. Check console.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}