package ui;

import dao.OrganizationDAO;
import model.Organization;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class OrganizationForm extends JFrame implements ActionListener {

    // ── Input Fields ─────────────────────────────────────────────────────────
    private JTextField txtOrgId;
    private JTextField txtOrgName;
    private JTextField txtLocation;
    private JTextField txtEmail;

    // ── Buttons ──────────────────────────────────────────────────────────────
    private JButton btnInsert;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnViewAll;
    private JButton btnClear;
    private JButton btnJointView;

    // ── Table ────────────────────────────────────────────────────────────────
    private JTable            table;
    private DefaultTableModel tableModel;

    // ── DAO ──────────────────────────────────────────────────────────────────
    private final OrganizationDAO dao = new OrganizationDAO();

    // ── Constructor ──────────────────────────────────────────────────────────
    public OrganizationForm() {
        setTitle("Organization Management");
        setSize(900, 600);
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
        header.setBackground(new Color(30, 60, 114));
        header.setBounds(0, 0, 900, 50);
        header.setLayout(null);
        add(header);

        JLabel lbl = new JLabel("ORGANIZATION MANAGEMENT");
        lbl.setBounds(0, 10, 900, 30);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        lbl.setForeground(Color.BLACK);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(lbl);
    }

    private void buildInputPanel() {
        // Org ID (auto – read only for display)
        addLabel("Org ID (auto):", 30, 75);
        txtOrgId = addField(180, 75);
        txtOrgId.setEditable(false);
        txtOrgId.setBackground(new Color(220, 220, 220));

        addLabel("Org Name *:", 30, 115);
        txtOrgName = addField(180, 115);

        addLabel("Location:", 30, 155);
        txtLocation = addField(180, 155);

        addLabel("Contact Email:", 30, 195);
        txtEmail = addField(180, 195);
    }

    private void buildButtons() {
        btnInsert  = addButton("Insert",   30, 245, new Color(34, 139, 34));
        btnUpdate  = addButton("Update",  155, 245, new Color(30, 100, 180));
        btnDelete  = addButton("Delete",  280, 245, new Color(180, 30, 30));
        btnViewAll = addButton("View All",405, 245, new Color(100, 60, 160));
        btnClear     = addButton("Clear",      530, 245, new Color(100, 100, 100));
        btnJointView = addButton("Joint View", 655, 245, new Color(0, 120, 120));
    }

    private void buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"Org ID", "Org Name", "Location", "Contact Email"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(30, 60, 114));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(22);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 290, 850, 260);
        add(scroll);

        // Click row → populate fields
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtOrgId.setText(safeGet(row, 0));
                    txtOrgName.setText(safeGet(row, 1));
                    txtLocation.setText(safeGet(row, 2));
                    txtEmail.setText(safeGet(row, 3));
                }
            }
        });
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void addLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 140, 25);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        add(lbl);
    }

    private JTextField addField(int x, int y) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, 250, 28);
        add(tf);
        return tf;
    }

    private JButton addButton(String label, int x, int y, Color bg) {
        JButton btn = new JButton(label);
        btn.setBounds(x, y, 115, 32);
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
        List<Organization> list = dao.getAll();
        for (Organization o : list) {
            tableModel.addRow(new Object[]{
                    o.getOrgId(), o.getOrgName(), o.getLocation(), o.getContactEmail()
            });
        }
    }

    private void clearFields() {
        txtOrgId.setText("");
        txtOrgName.setText("");
        txtLocation.setText("");
        txtEmail.setText("");
        table.clearSelection();
    }

    // ── Action Handler ───────────────────────────────────────────────────────

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnInsert) {
            doInsert();
        } else if (src == btnUpdate) {
            doUpdate();
        } else if (src == btnDelete) {
            doDelete();
        } else if (src == btnViewAll) {
            loadData();
        } else if (src == btnClear) {
            clearFields();
        } else if (src == btnJointView) {
            new OrgIncidentJointView();
        }
    }

    private void doInsert() {
        String name = txtOrgName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Org Name is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Organization org = new Organization(0, name,
                txtLocation.getText().trim(), txtEmail.getText().trim());

        if (dao.insert(org)) {
            JOptionPane.showMessageDialog(this, "Organization inserted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Insert failed. Check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doUpdate() {
        String idStr = txtOrgId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a record from the table first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String name = txtOrgName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Org Name is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(idStr);
        Organization org = new Organization(id, name,
                txtLocation.getText().trim(), txtEmail.getText().trim());

        if (dao.update(org)) {
            JOptionPane.showMessageDialog(this, "Organization updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Update failed. Check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        String idStr = txtOrgId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a record from the table first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this organization?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(idStr);
            if (dao.delete(id)) {
                JOptionPane.showMessageDialog(this, "Organization deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed. The organization may have linked records.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}