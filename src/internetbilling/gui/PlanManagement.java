package internetbilling.gui;

import internetbilling.dao.PlanDAO;
import internetbilling.model.Plan;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
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
 * Internet Plan Management Module for Administrator.
 */
public class PlanManagement extends JFrame {

    private final PlanDAO planDAO;
    private final AdminDashboard parentDashboard;

    // Form inputs
    private JTextField txtPlanId;
    private JTextField txtPlanName;
    private JTextField txtSpeedTier;
    private JTextField txtMonthlyTariff;
    private JTextField txtDataLimit;
    private JTextField txtExtraDataCharge;
    private JTextField txtEquipmentRental;
    private JTextField txtLatePaymentFine;

    // Buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnRefresh;

    // Table
    private JTable tblPlans;
    private DefaultTableModel tableModel;

    public PlanManagement(AdminDashboard parent) {
        this.parentDashboard = parent;
        this.planDAO = new PlanDAO();
        initComponents();
        loadPlansTable();
    }

    private void initComponents() {
        setTitle("Internet Plan Management - Internet Billing Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.BG_LIGHT);
        setLayout(new BorderLayout());

        // Header
        JPanel header = UIUtils.createHeaderPanel(
            "INTERNET PLAN MANAGEMENT",
            "Configure broadband packages, bandwidth speed tiers, data quotas, and rental pricing"
        );
        add(header, BorderLayout.NORTH);

        // Center split: Form on West, Table on Center
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

        JLabel lblFormTitle = new JLabel("Plan Parameters");
        lblFormTitle.setFont(UIUtils.FONT_HEADER);
        lblFormTitle.setForeground(UIUtils.PRIMARY);
        formCard.add(lblFormTitle, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtPlanId = new JTextField(15);
        UIUtils.styleTextField(txtPlanId);
        txtPlanName = new JTextField(15);
        UIUtils.styleTextField(txtPlanName);
        txtSpeedTier = new JTextField(15);
        UIUtils.styleTextField(txtSpeedTier);
        txtMonthlyTariff = new JTextField(15);
        UIUtils.styleTextField(txtMonthlyTariff);
        txtDataLimit = new JTextField(15);
        UIUtils.styleTextField(txtDataLimit);
        txtExtraDataCharge = new JTextField(15);
        UIUtils.styleTextField(txtExtraDataCharge);
        txtEquipmentRental = new JTextField(15);
        UIUtils.styleTextField(txtEquipmentRental);
        txtLatePaymentFine = new JTextField(15);
        UIUtils.styleTextField(txtLatePaymentFine);

        addFormField(formGrid, gbc, 0, "Plan ID *:", txtPlanId);
        addFormField(formGrid, gbc, 1, "Plan Name *:", txtPlanName);
        addFormField(formGrid, gbc, 2, "Speed Tier *:", txtSpeedTier);
        addFormField(formGrid, gbc, 3, "Monthly Tariff (" + UIUtils.CURRENCY_SYMBOL + ") *:", txtMonthlyTariff);
        addFormField(formGrid, gbc, 4, "Data Limit (GB) *:", txtDataLimit);
        addFormField(formGrid, gbc, 5, "Extra Charge/GB (" + UIUtils.CURRENCY_SYMBOL + ") *:", txtExtraDataCharge);
        addFormField(formGrid, gbc, 6, "Equipment Rental (" + UIUtils.CURRENCY_SYMBOL + ") *:", txtEquipmentRental);
        addFormField(formGrid, gbc, 7, "Late Payment Fine (" + UIUtils.CURRENCY_SYMBOL + ") *:", txtLatePaymentFine);

        formCard.add(formGrid, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        btnPanel.setOpaque(false);

        btnAdd = new JButton("ADD");
        UIUtils.styleSuccessButton(btnAdd);
        btnAdd.setIcon(UIUtils.getPlusIcon(13, Color.WHITE));
        btnAdd.setIconTextGap(6);
        btnAdd.addActionListener(e -> addPlan());

        btnUpdate = new JButton("UPDATE");
        UIUtils.stylePrimaryButton(btnUpdate);
        btnUpdate.setIcon(UIUtils.getCheckIcon(13, Color.WHITE));
        btnUpdate.setIconTextGap(6);
        btnUpdate.addActionListener(e -> updatePlan());

        btnDelete = new JButton("DELETE");
        UIUtils.styleDangerButton(btnDelete);
        btnDelete.addActionListener(e -> deletePlan());

        btnClear = new JButton("CLEAR");
        UIUtils.styleSecondaryButton(btnClear);
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        formCard.add(btnPanel, BorderLayout.SOUTH);
        centerPanel.add(formCard, BorderLayout.WEST);

        // RIGHT: Table
        JPanel tableContainer = new JPanel(new BorderLayout(10, 10));
        tableContainer.setOpaque(false);

        // Table Header row with Refresh
        JPanel tableTop = new JPanel(new BorderLayout());
        tableTop.setOpaque(false);
        JLabel lblTableTitle = new JLabel("Available Internet Plans");
        lblTableTitle.setFont(UIUtils.FONT_HEADER);
        lblTableTitle.setForeground(UIUtils.TEXT_DARK);

        btnRefresh = new JButton("REFRESH");
        UIUtils.styleSecondaryButton(btnRefresh);
        btnRefresh.setIcon(UIUtils.getRefreshIcon(13, UIUtils.TEXT_DARK));
        btnRefresh.setIconTextGap(6);
        btnRefresh.addActionListener(e -> loadPlansTable());

        tableTop.add(lblTableTitle, BorderLayout.WEST);
        tableTop.add(btnRefresh, BorderLayout.EAST);
        tableContainer.add(tableTop, BorderLayout.NORTH);

        String[] columns = {"Plan ID", "Plan Name", "Speed", "Tariff (" + UIUtils.CURRENCY_SYMBOL + ")", "Limit (GB)", "Extra/GB (" + UIUtils.CURRENCY_SYMBOL + ")", "Rental (" + UIUtils.CURRENCY_SYMBOL + ")", "Fine (" + UIUtils.CURRENCY_SYMBOL + ")"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblPlans = new JTable(tableModel);
        UIUtils.formatTable(tblPlans);

        tblPlans.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblPlans.getSelectedRow();
                if (row >= 0) {
                    populateFormFromRow(row);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblPlans);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableContainer, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int y, String label, JTextField comp) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0.45;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_REGULAR_BOLD);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.weightx = 0.55;
        comp.setFont(UIUtils.FONT_REGULAR);
        comp.setPreferredSize(new Dimension(170, 28));
        comp.setMinimumSize(new Dimension(170, 28));
        panel.add(comp, gbc);
    }

    private void loadPlansTable() {
        tableModel.setRowCount(0);
        try {
            List<Plan> list = planDAO.getAllPlans();
            for (Plan p : list) {
                tableModel.addRow(new Object[]{
                    p.getPlanId(),
                    p.getPlanName(),
                    p.getSpeedTier(),
                    p.getMonthlyTariff(),
                    p.getDataLimit(),
                    p.getExtraDataCharge(),
                    p.getEquipmentRental(),
                    p.getLatePaymentFine()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load plans: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromRow(int row) {
        String planId = (String) tableModel.getValueAt(row, 0);
        try {
            Plan p = planDAO.getPlanById(planId);
            if (p != null) {
                txtPlanId.setText(p.getPlanId());
                txtPlanId.setEditable(false);
                txtPlanName.setText(p.getPlanName());
                txtSpeedTier.setText(p.getSpeedTier());
                txtMonthlyTariff.setText(p.getMonthlyTariff().toString());
                txtDataLimit.setText(p.getDataLimit().toString());
                txtExtraDataCharge.setText(p.getExtraDataCharge().toString());
                txtEquipmentRental.setText(p.getEquipmentRental().toString());
                txtLatePaymentFine.setText(p.getLatePaymentFine().toString());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPlan() {
        if (!validateInputs()) return;

        String id = txtPlanId.getText().trim();
        try {
            if (planDAO.getPlanById(id) != null) {
                JOptionPane.showMessageDialog(this, "Plan ID '" + id + "' already exists.", "Duplicate Plan ID", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Plan p = buildPlanFromForm();
            boolean success = planDAO.addPlan(p);
            if (success) {
                JOptionPane.showMessageDialog(this, "Internet Plan added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadPlansTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding plan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePlan() {
        if (!validateInputs()) return;

        try {
            Plan p = buildPlanFromForm();
            boolean success = planDAO.updatePlan(p);
            if (success) {
                JOptionPane.showMessageDialog(this, "Internet Plan updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadPlansTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating plan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePlan() {
        String id = txtPlanId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a plan to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (planDAO.isPlanInUse(id)) {
                JOptionPane.showMessageDialog(this,
                    "Cannot delete Plan '" + id + "' because customers are currently subscribed to it.\nPlease reassign customers first.",
                    "Plan In Use", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete Plan '" + id + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                boolean success = planDAO.deletePlan(id);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Plan deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadPlansTable();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting plan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Plan buildPlanFromForm() {
        return new Plan(
            txtPlanId.getText().trim(),
            txtPlanName.getText().trim(),
            txtSpeedTier.getText().trim(),
            new BigDecimal(txtMonthlyTariff.getText().trim()),
            new BigDecimal(txtDataLimit.getText().trim()),
            new BigDecimal(txtExtraDataCharge.getText().trim()),
            new BigDecimal(txtEquipmentRental.getText().trim()),
            new BigDecimal(txtLatePaymentFine.getText().trim())
        );
    }

    private boolean validateInputs() {
        if (txtPlanId.getText().trim().isEmpty() ||
            txtPlanName.getText().trim().isEmpty() ||
            txtSpeedTier.getText().trim().isEmpty() ||
            txtMonthlyTariff.getText().trim().isEmpty() ||
            txtDataLimit.getText().trim().isEmpty() ||
            txtExtraDataCharge.getText().trim().isEmpty() ||
            txtEquipmentRental.getText().trim().isEmpty() ||
            txtLatePaymentFine.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            BigDecimal tariff = new BigDecimal(txtMonthlyTariff.getText().trim());
            BigDecimal limit = new BigDecimal(txtDataLimit.getText().trim());
            BigDecimal extra = new BigDecimal(txtExtraDataCharge.getText().trim());
            BigDecimal rental = new BigDecimal(txtEquipmentRental.getText().trim());
            BigDecimal fine = new BigDecimal(txtLatePaymentFine.getText().trim());

            if (tariff.compareTo(BigDecimal.ZERO) < 0 || limit.compareTo(BigDecimal.ZERO) < 0 ||
                extra.compareTo(BigDecimal.ZERO) < 0 || rental.compareTo(BigDecimal.ZERO) < 0 ||
                fine.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Values cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric decimal amounts.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtPlanId.setText("");
        txtPlanId.setEditable(true);
        txtPlanName.setText("");
        txtSpeedTier.setText("");
        txtMonthlyTariff.setText("");
        txtDataLimit.setText("");
        txtExtraDataCharge.setText("");
        txtEquipmentRental.setText("");
        txtLatePaymentFine.setText("");
        tblPlans.clearSelection();
    }
}
