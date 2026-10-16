package internetbilling.gui;

import internetbilling.dao.CustomerDAO;
import internetbilling.dao.PlanDAO;
import internetbilling.dao.UsageDAO;
import internetbilling.model.Bill;
import internetbilling.model.Customer;
import internetbilling.model.Plan;
import internetbilling.model.Usage;
import internetbilling.service.BillingService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Monthly Internet Usage Management Module for Administrator.
 * Automatically calculates Extra Data = Data Used - Data Limit.
 */
public class UsageManagement extends JFrame {

    private final UsageDAO usageDAO;
    private final CustomerDAO customerDAO;
    private final PlanDAO planDAO;
    private final BillingService billingService;
    private final AdminDashboard parentDashboard;

    // Form inputs
    private JComboBox<CustomerItem> cmbCustomer;
    private JTextField txtBillingMonth;
    private JTextField txtDataLimit;
    private JTextField txtDataUsed;
    private JTextField txtExtraData;

    // Buttons
    private JButton btnAddUsage;
    private JButton btnAddAndBill;
    private JButton btnUpdateUsage;
    private JButton btnClear;

    // Search & Table
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnRefresh;
    private JTable tblUsage;
    private DefaultTableModel tableModel;

    private int selectedUsageId = -1;

    public UsageManagement(AdminDashboard parent) {
        this.parentDashboard = parent;
        this.usageDAO = new UsageDAO();
        this.customerDAO = new CustomerDAO();
        this.planDAO = new PlanDAO();
        this.billingService = new BillingService();
        initComponents();
        loadCustomersComboBox();
        loadUsageTable();
    }

    private void initComponents() {
        setTitle("Monthly Usage Management - Internet Billing Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1140, 680);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.BG_LIGHT);
        setLayout(new BorderLayout());

        // Header
        JPanel header = UIUtils.createHeaderPanel(
            "MONTHLY INTERNET USAGE MANAGEMENT",
            "Monitor subscriber data consumption and calculate extra quota charges"
        );
        add(header, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // LEFT: Form Card
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(UIUtils.CARD_BG);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        formCard.setPreferredSize(new Dimension(400, 0));

        JLabel lblFormTitle = new JLabel("Enter Monthly Usage");
        lblFormTitle.setFont(UIUtils.FONT_HEADER);
        lblFormTitle.setForeground(UIUtils.PRIMARY);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);

        cmbCustomer = new JComboBox<>();
        cmbCustomer.addActionListener(e -> onCustomerSelected());

        LocalDate now = LocalDate.now();
        String currentMonth = String.format("%04d-%02d", now.getYear(), now.getMonthValue());
        txtBillingMonth = new JTextField(currentMonth);
        UIUtils.styleTextField(txtBillingMonth);

        txtDataLimit = new JTextField("0.00");
        txtDataLimit.setEditable(false);
        txtDataLimit.setBackground(new Color(241, 245, 249));
        UIUtils.styleTextField(txtDataLimit);

        txtDataUsed = new JTextField("0.00");
        UIUtils.styleTextField(txtDataUsed);
        txtDataUsed.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateExtraDataRealtime();
            }
        });

        txtExtraData = new JTextField("0.00");
        txtExtraData.setEditable(false);
        txtExtraData.setFont(UIUtils.FONT_REGULAR_BOLD);
        txtExtraData.setForeground(UIUtils.ACCENT_BLUE);
        txtExtraData.setBackground(new Color(241, 245, 249));

        addFormField(formGrid, gbc, 0, "Select Customer *:", cmbCustomer);
        addFormField(formGrid, gbc, 1, "Billing Month (YYYY-MM) *:", txtBillingMonth);
        addFormField(formGrid, gbc, 2, "Plan Data Limit (GB):", txtDataLimit);
        addFormField(formGrid, gbc, 3, "Data Used (GB) *:", txtDataUsed);
        addFormField(formGrid, gbc, 4, "Extra Data (GB):", txtExtraData);

        formCard.add(formGrid, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);

        btnAddUsage = new JButton("ADD USAGE");
        UIUtils.stylePrimaryButton(btnAddUsage);
        btnAddUsage.setIcon(UIUtils.getPlusIcon(13, Color.WHITE));
        btnAddUsage.setIconTextGap(6);
        btnAddUsage.addActionListener(e -> saveUsage(false));

        btnAddAndBill = new JButton("ADD & GENERATE BILL");
        UIUtils.styleSuccessButton(btnAddAndBill);
        btnAddAndBill.setIcon(UIUtils.getBillsIcon(13, Color.WHITE));
        btnAddAndBill.setIconTextGap(6);
        btnAddAndBill.addActionListener(e -> saveUsage(true));

        btnUpdateUsage = new JButton("UPDATE USAGE");
        UIUtils.styleWarningButton(btnUpdateUsage);
        btnUpdateUsage.setIcon(UIUtils.getCheckIcon(13, Color.WHITE));
        btnUpdateUsage.setIconTextGap(6);
        btnUpdateUsage.addActionListener(e -> updateUsage());

        btnClear = new JButton("CLEAR");
        UIUtils.styleSecondaryButton(btnClear);
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnAddUsage);
        btnPanel.add(btnAddAndBill);
        btnPanel.add(btnUpdateUsage);
        btnPanel.add(btnClear);

        formCard.add(btnPanel, BorderLayout.SOUTH);
        centerPanel.add(formCard, BorderLayout.WEST);

        // RIGHT: Search Bar + Table
        JPanel tableContainer = new JPanel(new BorderLayout(10, 10));
        tableContainer.setOpaque(false);

        // Search Bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchBar.setBackground(UIUtils.CARD_BG);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblSearch = new JLabel("Search Usage:");
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
            loadUsageTable();
        });

        searchBar.add(lblSearch);
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);
        searchBar.add(btnRefresh);
        tableContainer.add(searchBar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Customer ID", "Customer Name", "Billing Month", "Limit (GB)", "Used (GB)", "Extra (GB)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblUsage = new JTable(tableModel);
        UIUtils.formatTable(tblUsage);

        tblUsage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblUsage.getSelectedRow();
                if (row >= 0) {
                    populateFormFromRow(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblUsage);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableContainer, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int y, String label, javax.swing.JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0.4;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_REGULAR_BOLD);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.weightx = 0.6;
        comp.setFont(UIUtils.FONT_REGULAR);
        comp.setPreferredSize(new Dimension(190, 28));
        comp.setMinimumSize(new Dimension(190, 28));
        panel.add(comp, gbc);
    }

    private void loadCustomersComboBox() {
        cmbCustomer.removeAllItems();
        try {
            List<Customer> list = customerDAO.getAllCustomers();
            for (Customer c : list) {
                cmbCustomer.addItem(new CustomerItem(c.getCustomerId(), c.getName(), c.getPlanId()));
            }
            onCustomerSelected();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load customers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCustomerSelected() {
        CustomerItem item = (CustomerItem) cmbCustomer.getSelectedItem();
        if (item != null) {
            try {
                Plan p = planDAO.getPlanById(item.getPlanId());
                if (p != null) {
                    txtDataLimit.setText(p.getDataLimit().toString());
                    calculateExtraDataRealtime();
                }
            } catch (SQLException ex) {
                txtDataLimit.setText("0.00");
            }
        }
    }

    private void calculateExtraDataRealtime() {
        try {
            BigDecimal limit = new BigDecimal(txtDataLimit.getText().trim());
            BigDecimal used = new BigDecimal(txtDataUsed.getText().trim());
            if (used.compareTo(limit) > 0) {
                txtExtraData.setText(used.subtract(limit).toString());
            } else {
                txtExtraData.setText("0.00");
            }
        } catch (Exception e) {
            txtExtraData.setText("0.00");
        }
    }

    private void loadUsageTable() {
        tableModel.setRowCount(0);
        try {
            List<Usage> list = usageDAO.getAllUsage();
            for (Usage u : list) {
                tableModel.addRow(new Object[]{
                    u.getUsageId(),
                    u.getCustomerId(),
                    u.getCustomerName(),
                    u.getBillingMonth(),
                    u.getDataLimit() != null ? u.getDataLimit() : "0.00",
                    u.getDataUsed(),
                    u.getExtraData()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load usage table: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromRow(int row) {
        selectedUsageId = (int) tableModel.getValueAt(row, 0);
        String custId = (String) tableModel.getValueAt(row, 1);
        String month = (String) tableModel.getValueAt(row, 3);
        Object usedVal = tableModel.getValueAt(row, 5);

        for (int i = 0; i < cmbCustomer.getItemCount(); i++) {
            if (cmbCustomer.getItemAt(i).getId().equalsIgnoreCase(custId)) {
                cmbCustomer.setSelectedIndex(i);
                break;
            }
        }

        txtBillingMonth.setText(month);
        txtDataUsed.setText(usedVal != null ? usedVal.toString() : "0.00");
        calculateExtraDataRealtime();
    }

    private void saveUsage(boolean alsoGenerateBill) {
        CustomerItem item = (CustomerItem) cmbCustomer.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String month = txtBillingMonth.getText().trim();
        if (month.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Billing month is required (e.g. 2026-10).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal used;
        try {
            used = new BigDecimal(txtDataUsed.getText().trim());
            if (used.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Data usage cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric data usage.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        calculateExtraDataRealtime();
        BigDecimal extra = new BigDecimal(txtExtraData.getText().trim());

        try {
            if (usageDAO.isUsageExists(item.getId(), month)) {
                int opt = JOptionPane.showConfirmDialog(this,
                    "Usage already exists for customer " + item.getId() + " and month " + month + ".\nDo you want to update it?",
                    "Existing Usage", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    Usage u = new Usage(0, item.getId(), month, used, extra);
                    usageDAO.updateUsage(u);
                } else {
                    return;
                }
            } else {
                Usage u = new Usage(0, item.getId(), month, used, extra);
                usageDAO.addUsage(u);
            }

            if (alsoGenerateBill) {
                try {
                    Bill generatedBill = billingService.generateBillForCustomer(item.getId(), month, used);
                    JOptionPane.showMessageDialog(this,
                        "Usage recorded and Bill generated successfully!\n" +
                        "Bill ID: " + generatedBill.getBillId() + "\n" +
                        "Total Amount: " + UIUtils.formatCurrency(generatedBill.getTotalAmount()),
                        "Bill Generated", JOptionPane.INFORMATION_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Usage saved, but bill notice: " + ex.getMessage(),
                        "Notice", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Usage recorded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            clearForm();
            loadUsageTable();
            if (parentDashboard != null) parentDashboard.loadDashboardStats();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUsage() {
        CustomerItem item = (CustomerItem) cmbCustomer.getSelectedItem();
        if (item == null) return;
        String month = txtBillingMonth.getText().trim();
        BigDecimal used;
        try {
            used = new BigDecimal(txtDataUsed.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid data usage.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        calculateExtraDataRealtime();
        BigDecimal extra = new BigDecimal(txtExtraData.getText().trim());

        try {
            Usage u = new Usage(selectedUsageId, item.getId(), month, used, extra);
            boolean updated = usageDAO.updateUsage(u);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Usage updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadUsageTable();
            } else {
                JOptionPane.showMessageDialog(this, "Could not update usage.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadUsageTable();
            return;
        }

        tableModel.setRowCount(0);
        try {
            List<Usage> list = usageDAO.searchUsage(keyword);
            for (Usage u : list) {
                tableModel.addRow(new Object[]{
                    u.getUsageId(),
                    u.getCustomerId(),
                    u.getCustomerName(),
                    u.getBillingMonth(),
                    u.getDataLimit() != null ? u.getDataLimit() : "0.00",
                    u.getDataUsed(),
                    u.getExtraData()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        selectedUsageId = -1;
        if (cmbCustomer.getItemCount() > 0) cmbCustomer.setSelectedIndex(0);
        LocalDate now = LocalDate.now();
        txtBillingMonth.setText(String.format("%04d-%02d", now.getYear(), now.getMonthValue()));
        txtDataUsed.setText("0.00");
        txtExtraData.setText("0.00");
        tblUsage.clearSelection();
    }

    private static class CustomerItem {
        private final String id;
        private final String name;
        private final String planId;

        public CustomerItem(String id, String name, String planId) {
            this.id = id;
            this.name = name;
            this.planId = planId;
        }

        public String getId() { return id; }
        public String getPlanId() { return planId; }

        @Override
        public String toString() {
            return id + " - " + name;
        }
    }
}
