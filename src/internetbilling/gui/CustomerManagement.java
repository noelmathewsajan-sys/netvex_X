package internetbilling.gui;

import internetbilling.dao.CustomerDAO;
import internetbilling.dao.PlanDAO;
import internetbilling.model.Customer;
import internetbilling.model.Plan;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Customer Management Module for Administrator.
 * Full CRUD, search, validation, and table listing.
 */
public class CustomerManagement extends JFrame {

    private final CustomerDAO customerDAO;
    private final PlanDAO planDAO;
    private final AdminDashboard parentDashboard;

    // Form inputs
    private JTextField txtCustomerId;
    private JTextField txtName;
    private JTextField txtAddress;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<PlanItem> cmbPlan;
    private JTextField txtConnectionDate;
    private JComboBox<String> cmbStatus;

    // Search & Table
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnRefresh;
    private JTable tblCustomers;
    private DefaultTableModel tableModel;

    // Action buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    public CustomerManagement(AdminDashboard parent) {
        this.parentDashboard = parent;
        this.customerDAO = new CustomerDAO();
        this.planDAO = new PlanDAO();
        initComponents();
        loadPlansComboBox();
        loadCustomersTable();
    }

    private void initComponents() {
        setTitle("Customer Management - Internet Billing Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1180, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.BG_LIGHT);
        setLayout(new BorderLayout());

        // Header
        JPanel header = UIUtils.createHeaderPanel("CUSTOMER MANAGEMENT", "Register, update, and manage ISP subscriber accounts");
        add(header, BorderLayout.NORTH);

        // Center split: Form on West, Table & Search on Center
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // LEFT: Form Panel
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(UIUtils.CARD_BG);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        formCard.setPreferredSize(new Dimension(380, 0));

        JLabel lblFormTitle = new JLabel("Customer Details");
        lblFormTitle.setFont(UIUtils.FONT_HEADER);
        lblFormTitle.setForeground(UIUtils.PRIMARY);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtCustomerId = new JTextField(15);
        UIUtils.styleTextField(txtCustomerId);
        txtName = new JTextField(15);
        UIUtils.styleTextField(txtName);
        txtAddress = new JTextField(15);
        UIUtils.styleTextField(txtAddress);
        txtPhone = new JTextField(15);
        UIUtils.styleTextField(txtPhone);
        txtEmail = new JTextField(15);
        UIUtils.styleTextField(txtEmail);
        txtUsername = new JTextField(15);
        UIUtils.styleTextField(txtUsername);
        txtPassword = new JPasswordField(15);
        UIUtils.styleTextField(txtPassword);
        cmbPlan = new JComboBox<>();
        txtConnectionDate = new JTextField(LocalDate.now().toString());
        UIUtils.styleTextField(txtConnectionDate);
        cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "SUSPENDED"});

        addFormField(formGrid, gbc, 0, "Customer ID *:", txtCustomerId);
        addFormField(formGrid, gbc, 1, "Customer Name *:", txtName);
        addFormField(formGrid, gbc, 2, "Address *:", txtAddress);
        addFormField(formGrid, gbc, 3, "Phone Number *:", txtPhone);
        addFormField(formGrid, gbc, 4, "Email Address *:", txtEmail);
        addFormField(formGrid, gbc, 5, "Username *:", txtUsername);
        addFormField(formGrid, gbc, 6, "Password *:", txtPassword);
        addFormField(formGrid, gbc, 7, "Internet Plan *:", cmbPlan);
        addFormField(formGrid, gbc, 8, "Connection Date:", txtConnectionDate);
        addFormField(formGrid, gbc, 9, "Account Status:", cmbStatus);

        formCard.add(formGrid, BorderLayout.CENTER);

        // Buttons under Form
        JPanel formBtnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        formBtnRow.setOpaque(false);

        btnAdd = new JButton("ADD");
        UIUtils.styleSuccessButton(btnAdd);
        btnAdd.setIcon(UIUtils.getPlusIcon(13, Color.WHITE));
        btnAdd.setIconTextGap(6);
        btnAdd.addActionListener(e -> addCustomer());

        btnUpdate = new JButton("UPDATE");
        UIUtils.stylePrimaryButton(btnUpdate);
        btnUpdate.setIcon(UIUtils.getCheckIcon(13, Color.WHITE));
        btnUpdate.setIconTextGap(6);
        btnUpdate.addActionListener(e -> updateCustomer());

        btnDelete = new JButton("DELETE");
        UIUtils.styleDangerButton(btnDelete);
        btnDelete.addActionListener(e -> deleteCustomer());

        btnClear = new JButton("CLEAR");
        UIUtils.styleSecondaryButton(btnClear);
        btnClear.addActionListener(e -> clearForm());

        formBtnRow.add(btnAdd);
        formBtnRow.add(btnUpdate);
        formBtnRow.add(btnDelete);
        formBtnRow.add(btnClear);

        formCard.add(formBtnRow, BorderLayout.SOUTH);
        centerPanel.add(formCard, BorderLayout.WEST);

        // RIGHT: Search Bar + Table
        JPanel tableContainer = new JPanel(new BorderLayout(10, 10));
        tableContainer.setOpaque(false);

        // Search Bar Panel
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchBar.setBackground(UIUtils.CARD_BG);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblSearch = new JLabel("Search Customer:");
        lblSearch.setFont(UIUtils.FONT_REGULAR_BOLD);
        txtSearch = new JTextField(20);
        UIUtils.styleTextField(txtSearch);
        txtSearch.setPreferredSize(new Dimension(220, 32));

        btnSearch = new JButton("SEARCH");
        UIUtils.stylePrimaryButton(btnSearch);
        btnSearch.setIcon(UIUtils.getSearchIcon(13, Color.WHITE));
        btnSearch.setIconTextGap(6);
        btnSearch.addActionListener(e -> performSearch());

        btnRefresh = new JButton("REFRESH");
        UIUtils.styleSecondaryButton(btnRefresh);
        btnRefresh.setIcon(UIUtils.getRefreshIcon(13, UIUtils.TEXT_DARK));
        btnRefresh.setIconTextGap(6);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadCustomersTable();
        });

        searchBar.add(lblSearch);
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);
        searchBar.add(btnRefresh);

        tableContainer.add(searchBar, BorderLayout.NORTH);

        // Customers Table
        String[] columns = {"ID", "Name", "Phone", "Email", "Username", "Plan", "Conn Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblCustomers = new JTable(tableModel);
        UIUtils.formatTable(tblCustomers);

        tblCustomers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblCustomers.getSelectedRow();
                if (row >= 0) {
                    populateFormFromRow(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblCustomers);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableContainer, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int y, String label, javax.swing.JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_REGULAR_BOLD);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.weightx = 0.65;
        comp.setFont(UIUtils.FONT_REGULAR);
        comp.setPreferredSize(new Dimension(180, 28));
        comp.setMinimumSize(new Dimension(180, 28));
        panel.add(comp, gbc);
    }

    private void loadPlansComboBox() {
        cmbPlan.removeAllItems();
        try {
            List<Plan> plans = planDAO.getAllPlans();
            for (Plan p : plans) {
                cmbPlan.addItem(new PlanItem(p.getPlanId(), p.getPlanName() + " (" + p.getSpeedTier() + ")"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load plans: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCustomersTable() {
        tableModel.setRowCount(0);
        try {
            List<Customer> list = customerDAO.getAllCustomers();
            for (Customer c : list) {
                tableModel.addRow(new Object[]{
                    c.getCustomerId(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getUsername(),
                    c.getPlanName() != null ? c.getPlanName() : c.getPlanId(),
                    c.getConnectionDate(),
                    c.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load customers: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (parentDashboard != null) {
            parentDashboard.loadDashboardStats();
        }
    }

    private void populateFormFromRow(int row) {
        String customerId = (String) tableModel.getValueAt(row, 0);
        try {
            Customer c = customerDAO.getCustomerById(customerId);
            if (c != null) {
                txtCustomerId.setText(c.getCustomerId());
                txtCustomerId.setEditable(false);
                txtName.setText(c.getName());
                txtAddress.setText(c.getAddress());
                txtPhone.setText(c.getPhone());
                txtEmail.setText(c.getEmail());
                txtUsername.setText(c.getUsername());
                txtPassword.setText(c.getPassword());
                txtConnectionDate.setText(c.getConnectionDate().toString());
                cmbStatus.setSelectedItem(c.getStatus());

                // Select plan in combo
                for (int i = 0; i < cmbPlan.getItemCount(); i++) {
                    if (cmbPlan.getItemAt(i).getId().equalsIgnoreCase(c.getPlanId())) {
                        cmbPlan.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching customer details: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCustomer() {
        if (!validateInputs(true)) return;

        String id = txtCustomerId.getText().trim();
        String username = txtUsername.getText().trim();

        try {
            if (customerDAO.isCustomerIdExists(id)) {
                JOptionPane.showMessageDialog(this, "Customer ID already exists. Please choose another.",
                    "Duplicate Customer ID", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (customerDAO.isUsernameExists(username, null)) {
                JOptionPane.showMessageDialog(this, "Username '" + username + "' is already taken. Please choose another.",
                    "Duplicate Username", JOptionPane.WARNING_MESSAGE);
                return;
            }

            PlanItem selectedPlan = (PlanItem) cmbPlan.getSelectedItem();
            Date connDate = Date.valueOf(txtConnectionDate.getText().trim());

            Customer c = new Customer(
                id,
                txtName.getText().trim(),
                txtAddress.getText().trim(),
                txtPhone.getText().trim(),
                txtEmail.getText().trim(),
                username,
                new String(txtPassword.getPassword()).trim(),
                selectedPlan != null ? selectedPlan.getId() : "PLAN01",
                connDate,
                (String) cmbStatus.getSelectedItem()
            );

            boolean success = customerDAO.addCustomer(c);
            if (success) {
                JOptionPane.showMessageDialog(this, "Customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadCustomersTable();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date format. Use YYYY-MM-DD.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        if (!validateInputs(false)) return;

        String id = txtCustomerId.getText().trim();
        String username = txtUsername.getText().trim();

        try {
            if (customerDAO.isUsernameExists(username, id)) {
                JOptionPane.showMessageDialog(this, "Username '" + username + "' is already taken by another customer.",
                    "Duplicate Username", JOptionPane.WARNING_MESSAGE);
                return;
            }

            PlanItem selectedPlan = (PlanItem) cmbPlan.getSelectedItem();
            Date connDate = Date.valueOf(txtConnectionDate.getText().trim());

            Customer c = new Customer(
                id,
                txtName.getText().trim(),
                txtAddress.getText().trim(),
                txtPhone.getText().trim(),
                txtEmail.getText().trim(),
                username,
                new String(txtPassword.getPassword()).trim(),
                selectedPlan != null ? selectedPlan.getId() : "PLAN01",
                connDate,
                (String) cmbStatus.getSelectedItem()
            );

            boolean success = customerDAO.updateCustomer(c);
            if (success) {
                JOptionPane.showMessageDialog(this, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadCustomersTable();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date format. Use YYYY-MM-DD.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        String id = txtCustomerId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete/deactivate.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete customer '" + id + "'?\nThis will also remove associated usage and bill references.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = customerDAO.deleteCustomer(id);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Customer removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadCustomersTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Customer could not be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete customer with active billing history:\n" + ex.getMessage() +
                    "\nTip: Consider setting status to INACTIVE instead.", "Constraint Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadCustomersTable();
            return;
        }

        tableModel.setRowCount(0);
        try {
            List<Customer> results = customerDAO.searchCustomers(keyword);
            for (Customer c : results) {
                tableModel.addRow(new Object[]{
                    c.getCustomerId(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getUsername(),
                    c.getPlanName() != null ? c.getPlanName() : c.getPlanId(),
                    c.getConnectionDate(),
                    c.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtCustomerId.setText("");
        txtCustomerId.setEditable(true);
        txtName.setText("");
        txtAddress.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtConnectionDate.setText(LocalDate.now().toString());
        if (cmbPlan.getItemCount() > 0) cmbPlan.setSelectedIndex(0);
        cmbStatus.setSelectedItem("ACTIVE");
        tblCustomers.clearSelection();
    }

    private boolean validateInputs(boolean isAdd) {
        if (txtCustomerId.getText().trim().isEmpty() ||
            txtName.getText().trim().isEmpty() ||
            txtAddress.getText().trim().isEmpty() ||
            txtPhone.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty() ||
            txtUsername.getText().trim().isEmpty() ||
            new String(txtPassword.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String email = txtEmail.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String phone = txtPhone.getText().trim();
        if (!phone.matches("^[0-9+ -]{7,15}$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid phone number (digits and optional +).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    // Helper item for JComboBox
    private static class PlanItem {
        private final String id;
        private final String label;

        public PlanItem(String id, String label) {
            this.id = id;
            this.label = label;
        }

        public String getId() { return id; }

        @Override
        public String toString() { return label; }
    }
}
