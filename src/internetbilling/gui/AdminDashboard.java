package internetbilling.gui;

import internetbilling.dao.BillDAO;
import internetbilling.dao.CustomerDAO;
import internetbilling.database.DBConnection;
import internetbilling.model.Admin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Modern ISP-Themed Administrator Dashboard.
 * Features a Dark Navy sidebar (#0F2747) for navigation, Light Blue-Gray background (#F4F7FB),
 * White KPI cards (#FFFFFF) with subtle borders (#D9E2EC), and Professional Blue buttons (#1976D2).
 */
public class AdminDashboard extends JFrame {

    private final Admin admin;
    private final CustomerDAO customerDAO;
    private final BillDAO billDAO;

    // Stat card value labels
    private JLabel lblTotalCustomers;
    private JLabel lblActiveCustomers;
    private JLabel lblTotalBills;
    private JLabel lblPaidBills;
    private JLabel lblUnpaidBills;
    private JLabel lblOverdueBills;
    private JLabel lblTotalRevenue;

    public AdminDashboard(Admin admin) {
        this.admin = admin;
        this.customerDAO = new CustomerDAO();
        this.billDAO = new BillDAO();
        initComponents();
        loadDashboardStats();
    }

    private void initComponents() {
        setTitle("NETVEX Admin Console - Internet Billing Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.BG_LIGHT);
        setLayout(new BorderLayout());

        // =====================================================================
        // 1. TOP HEADER (Navy Blue #12355B)
        // =====================================================================
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(UIUtils.HEADER_NAVY);
        topHeader.setBorder(new EmptyBorder(12, 24, 12, 24));

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);

        JLabel lblLogo = new JLabel(UIUtils.getGlobeIcon(26, Color.WHITE));

        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGroup.setOpaque(false);

        JLabel lblAppTitle = new JLabel("NETVEX  |  ADMIN CONSOLE");
        lblAppTitle.setFont(UIUtils.FONT_HEADER);
        lblAppTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("ISP Operations, Subscriber Management & Real-time Billing");
        lblSub.setFont(UIUtils.FONT_SMALL);
        lblSub.setForeground(new Color(0xD9, 0xE2, 0xEC));

        titleGroup.add(lblAppTitle);
        titleGroup.add(lblSub);
        brandPanel.add(lblLogo);
        brandPanel.add(titleGroup);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        userPanel.setOpaque(false);

        JLabel lblUserBadge = new JLabel(" " + (admin != null ? admin.getUsername() : "admin") + " (Administrator)");
        lblUserBadge.setIcon(UIUtils.getUserIcon(16, Color.WHITE));
        lblUserBadge.setFont(UIUtils.FONT_REGULAR_BOLD);
        lblUserBadge.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        UIUtils.styleSecondaryButton(btnLogout);
        btnLogout.setIcon(UIUtils.getLogoutIcon(14, UIUtils.TEXT_DARK));
        btnLogout.setIconTextGap(6);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new WelcomeFrame().setVisible(true);
            }
        });

        userPanel.add(lblUserBadge);
        userPanel.add(btnLogout);

        topHeader.add(brandPanel, BorderLayout.WEST);
        topHeader.add(userPanel, BorderLayout.EAST);
        add(topHeader, BorderLayout.NORTH);

        // =====================================================================
        // 2. DARK NAVY SIDEBAR (#0F2747)
        // =====================================================================
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIUtils.PRIMARY_DARK); // Dark Navy Blue #0F2747
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel lblNavHeading = new JLabel("  NAVIGATION");
        lblNavHeading.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNavHeading.setForeground(UIUtils.TEXT_MUTED);
        lblNavHeading.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnNavDashboard = createSidebarButton("Dashboard", UIUtils.getDashboardIcon(18, Color.WHITE), true, null);
        JButton btnNavCustomers = createSidebarButton("Customers", UIUtils.getCustomersIcon(18, Color.WHITE), false, e -> openCustomerManagement());
        JButton btnNavPlans = createSidebarButton("Internet Plans", UIUtils.getPlansIcon(18, Color.WHITE), false, e -> openPlanManagement());
        JButton btnNavUsage = createSidebarButton("Monthly Usage", UIUtils.getUsageIcon(18, Color.WHITE), false, e -> openUsageManagement());
        JButton btnNavBills = createSidebarButton("Invoices & Bills", UIUtils.getBillsIcon(18, Color.WHITE), false, e -> openBillManagement());
        JButton btnNavPayments = createSidebarButton("Payment Records", UIUtils.getPaymentsIcon(18, Color.WHITE), false, e -> openPaymentHistory());
        JButton btnNavDBSettings = createSidebarButton("Database Settings", UIUtils.getDatabaseIcon(18, Color.WHITE), false, e -> openDBSettingsDialog());

        sidebar.add(lblNavHeading);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnNavDashboard);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavCustomers);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavPlans);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavUsage);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavBills);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavPayments);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavDBSettings);

        sidebar.add(Box.createVerticalGlue());

        // Sidebar Bottom Actions
        JButton btnRefreshStats = new JButton("Refresh Data");
        UIUtils.styleSecondaryButton(btnRefreshStats);
        btnRefreshStats.setIcon(UIUtils.getRefreshIcon(14, UIUtils.TEXT_DARK));
        btnRefreshStats.setIconTextGap(8);
        btnRefreshStats.setMaximumSize(new Dimension(210, 36));
        btnRefreshStats.setAlignmentX(LEFT_ALIGNMENT);
        btnRefreshStats.addActionListener(e -> loadDashboardStats());

        sidebar.add(btnRefreshStats);
        sidebar.add(Box.createVerticalStrut(10));

        add(sidebar, BorderLayout.WEST);

        // =====================================================================
        // 3. MAIN DASHBOARD CONTENT (Light Blue-Gray #F4F7FB)
        // =====================================================================
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(UIUtils.BG_LIGHT);
        mainContent.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Section Title
        JLabel lblStatsHeader = new JLabel("System Overview & Live Financial Metrics");
        lblStatsHeader.setFont(UIUtils.FONT_HEADER);
        lblStatsHeader.setForeground(UIUtils.TEXT_DARK);
        mainContent.add(lblStatsHeader);
        mainContent.add(Box.createVerticalStrut(14));

        // KPI Cards Grid (2 rows x 4 columns)
        JPanel statsGrid = new JPanel(new GridLayout(2, 4, 16, 16));
        statsGrid.setOpaque(false);
        statsGrid.setPreferredSize(new Dimension(800, 200));

        lblTotalCustomers = new JLabel("0");
        lblActiveCustomers = new JLabel("0");
        lblTotalBills = new JLabel("0");
        lblPaidBills = new JLabel("0");
        lblUnpaidBills = new JLabel("0");
        lblOverdueBills = new JLabel("0");
        lblTotalRevenue = new JLabel(UIUtils.formatCurrency(0.0));

        statsGrid.add(createDashboardCard("Total Customers", lblTotalCustomers, UIUtils.SECONDARY_BLUE));
        statsGrid.add(createDashboardCard("Active Customers", lblActiveCustomers, UIUtils.SUCCESS_GREEN));
        statsGrid.add(createDashboardCard("Total Invoices", lblTotalBills, UIUtils.HEADER_NAVY));
        statsGrid.add(createDashboardCard("Paid Invoices", lblPaidBills, UIUtils.SUCCESS_GREEN));
        statsGrid.add(createDashboardCard("Unpaid Invoices", lblUnpaidBills, UIUtils.WARNING_ORANGE));
        statsGrid.add(createDashboardCard("Overdue Invoices", lblOverdueBills, UIUtils.DANGER_RED));
        statsGrid.add(createDashboardCard("Total Revenue Collected", lblTotalRevenue, UIUtils.SUCCESS_GREEN));

        JLabel lblDbStatus = new JLabel(DBConnection.getDbEngine() + " Active");
        lblDbStatus.setFont(UIUtils.FONT_REGULAR_BOLD);
        lblDbStatus.setForeground(UIUtils.SUCCESS_GREEN);
        statsGrid.add(createDashboardCard("Database Link", lblDbStatus, UIUtils.SECONDARY_BLUE));

        mainContent.add(statsGrid);
        mainContent.add(Box.createVerticalStrut(28));

        // Section 2: Quick Management Modules
        JLabel lblQuickHeader = new JLabel("Quick Management Modules");
        lblQuickHeader.setFont(UIUtils.FONT_HEADER);
        lblQuickHeader.setForeground(UIUtils.TEXT_DARK);
        mainContent.add(lblQuickHeader);
        mainContent.add(Box.createVerticalStrut(14));

        JPanel quickGrid = new JPanel(new GridLayout(1, 5, 14, 14));
        quickGrid.setOpaque(false);
        quickGrid.setPreferredSize(new Dimension(800, 115));

        quickGrid.add(createQuickActionCard("Customers", "Add, edit & deactivate", e -> openCustomerManagement()));
        quickGrid.add(createQuickActionCard("Plans", "Speed & monthly tariffs", e -> openPlanManagement()));
        quickGrid.add(createQuickActionCard("Usage", "Record monthly data", e -> openUsageManagement()));
        quickGrid.add(createQuickActionCard("Bills", "Calculate & print invoices", e -> openBillManagement()));
        quickGrid.add(createQuickActionCard("Payments", "Transaction audits", e -> openPaymentHistory()));

        mainContent.add(quickGrid);
        add(mainContent, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(UIUtils.BG_LIGHT);
        JLabel lblFoot = new JLabel("Internet Billing Management System | Next-Gen ISP Platform");
        lblFoot.setFont(UIUtils.FONT_SMALL);
        lblFoot.setForeground(UIUtils.TEXT_MUTED);
        footer.add(lblFoot);
        add(footer, BorderLayout.SOUTH);
    }

    private JButton createSidebarButton(String text, Icon icon, boolean active, ActionListener listener) {
        JButton btn = new JButton(text);
        UIUtils.styleSidebarButton(btn, icon, active);
        if (listener != null) {
            btn.addActionListener(listener);
        }
        return btn;
    }

    private JPanel createDashboardCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(UIUtils.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
            new EmptyBorder(14, 16, 14, 16)
        ));

        // Top accent line
        JPanel topBar = new JPanel();
        topBar.setBackground(accent);
        topBar.setPreferredSize(new Dimension(0, 4));
        card.add(topBar, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(2, 1, 2, 2));
        content.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIUtils.FONT_SMALL);
        lblTitle.setForeground(UIUtils.TEXT_MUTED);

        valueLabel.setFont(UIUtils.FONT_STAT_NUMBER);
        valueLabel.setForeground(UIUtils.TEXT_DARK);

        content.add(lblTitle);
        content.add(valueLabel);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JButton createQuickActionCard(String title, String subtitle, ActionListener listener) {
        JButton btn = new JButton("<html><center><b><font color='#0F2747' size='+1'>" + title + "</font></b><br/><font color='#667085'>" + subtitle + "</font></center></html>");
        btn.setFont(UIUtils.FONT_REGULAR);
        btn.setBackground(UIUtils.CARD_BG);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
            new EmptyBorder(16, 12, 16, 12)
        ));
        btn.addActionListener(listener);
        return btn;
    }

    public void loadDashboardStats() {
        try {
            int totalCust = customerDAO.getTotalCustomersCount();
            int activeCust = customerDAO.getActiveCustomersCount();
            int totalBills = billDAO.getTotalBillsCount();
            int paidBills = billDAO.getPaidBillsCount();
            int unpaidBills = billDAO.getUnpaidBillsCount();
            int overdueBills = billDAO.getOverdueBillsCount();
            BigDecimal revenue = billDAO.getTotalRevenue();

            lblTotalCustomers.setText(String.valueOf(totalCust));
            lblActiveCustomers.setText(String.valueOf(activeCust));
            lblTotalBills.setText(String.valueOf(totalBills));
            lblPaidBills.setText(String.valueOf(paidBills));
            lblUnpaidBills.setText(String.valueOf(unpaidBills));
            lblOverdueBills.setText(String.valueOf(overdueBills));
            lblTotalRevenue.setText(UIUtils.formatCurrency(revenue));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Unable to retrieve live statistics: " + ex.getMessage(),
                "Database Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void openCustomerManagement() {
        new CustomerManagement(this).setVisible(true);
    }

    private void openPlanManagement() {
        new PlanManagement(this).setVisible(true);
    }

    private void openUsageManagement() {
        new UsageManagement(this).setVisible(true);
    }

    private void openBillManagement() {
        new BillManagement(this).setVisible(true);
    }

    private void openPaymentHistory() {
        new PaymentHistory(null).setVisible(true);
    }

    private void openDBSettingsDialog() {
        JTextField txtHost = new JTextField(DBConnection.getHost());
        JTextField txtPort = new JTextField(String.valueOf(DBConnection.getPort()));
        JTextField txtDb = new JTextField(DBConnection.getDatabase());
        JTextField txtUser = new JTextField(DBConnection.getUsername());
        JPasswordField txtPass = new JPasswordField(DBConnection.getPassword());

        JPanel panel = new JPanel(java.awt.GridLayout.class.isAssignableFrom(JPanel.class) ? new java.awt.GridLayout(5, 2, 8, 8) : new java.awt.GridLayout(5, 2, 8, 8));
        panel.setLayout(new java.awt.GridLayout(5, 2, 8, 8));
        panel.add(new JLabel("MySQL Host / Server:"));
        panel.add(txtHost);
        panel.add(new JLabel("MySQL Port (e.g. 3307 or 3306):"));
        panel.add(txtPort);
        panel.add(new JLabel("Database Name:"));
        panel.add(txtDb);
        panel.add(new JLabel("MySQL Username:"));
        panel.add(txtUser);
        panel.add(new JLabel("MySQL Root Password:"));
        panel.add(txtPass);

        int result = JOptionPane.showConfirmDialog(
            this, panel, "MySQL Database Connection Settings",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                DBConnection.setDbEngine("MYSQL");
                DBConnection.setHost(txtHost.getText().trim());
                DBConnection.setPort(Integer.parseInt(txtPort.getText().trim()));
                DBConnection.setDatabase(txtDb.getText().trim());
                DBConnection.setUsername(txtUser.getText().trim());
                DBConnection.setPassword(new String(txtPass.getPassword()));
                DBConnection.saveConfiguration();

                if (DBConnection.testConnection()) {
                    JOptionPane.showMessageDialog(this,
                        "Connected successfully to MySQL Server at " + DBConnection.getHost() + ":" + DBConnection.getPort() + "!",
                        "Connection Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Saved settings, but connection test failed.\nPlease make sure MySQL Server is running and InternetBillingDB exists.",
                        "Connection Test Warning", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error in DB settings: " + ex.getMessage(),
                    "Configuration Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new AdminDashboard(new Admin(1, "admin", "admin123")).setVisible(true));
    }
}
