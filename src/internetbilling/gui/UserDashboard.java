package internetbilling.gui;

import internetbilling.dao.BillDAO;
import internetbilling.dao.PlanDAO;
import internetbilling.model.Bill;
import internetbilling.model.Customer;
import internetbilling.model.Plan;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Modern ISP-Themed Subscriber / Customer Portal Dashboard.
 * Features a Dark Navy navigation sidebar (#0F2747), Light Blue-Gray background (#F4F7FB),
 * White cards (#FFFFFF) with subtle borders (#D9E2EC), and Professional Blue buttons (#1976D2).
 */
public class UserDashboard extends JFrame {

    private final Customer customer;
    private final PlanDAO planDAO;
    private final BillDAO billDAO;

    // Overview Labels
    private JLabel lblPlanName;
    private JLabel lblSpeed;
    private JLabel lblDataLimit;
    private JLabel lblCurrentDue;

    public UserDashboard(Customer customer) {
        this.customer = customer;
        this.planDAO = new PlanDAO();
        this.billDAO = new BillDAO();
        initComponents();
        loadCustomerOverview();
    }

    private void initComponents() {
        String name = (customer != null) ? customer.getName() : "Customer";
        setTitle("NETVEX Subscriber Portal - Welcome, " + name);
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

        JLabel lblAppTitle = new JLabel("NETVEX  |  SUBSCRIBER PORTAL");
        lblAppTitle.setFont(UIUtils.FONT_HEADER);
        lblAppTitle.setForeground(Color.WHITE);

        String subtitleText = "Account: " + (customer != null ? customer.getCustomerId() : "") +
            "  |  Status: " + (customer != null ? customer.getStatus() : "ACTIVE");
        JLabel lblSub = new JLabel(subtitleText);
        lblSub.setFont(UIUtils.FONT_SMALL);
        lblSub.setForeground(new Color(0xD9, 0xE2, 0xEC));

        titleGroup.add(lblAppTitle);
        titleGroup.add(lblSub);
        brandPanel.add(lblLogo);
        brandPanel.add(titleGroup);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        userPanel.setOpaque(false);

        JLabel lblUserBadge = new JLabel(" " + name);
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

        JLabel lblNavHeading = new JLabel("  MY ACCOUNT");
        lblNavHeading.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNavHeading.setForeground(UIUtils.TEXT_MUTED);
        lblNavHeading.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnNavOverview = createSidebarButton("Dashboard", UIUtils.getDashboardIcon(18, Color.WHITE), true, null);
        JButton btnNavProfile = createSidebarButton("My Profile", UIUtils.getUserIcon(18, Color.WHITE), false, e -> new MyProfile(customer).setVisible(true));
        JButton btnNavPlan = createSidebarButton("My Internet Plan", UIUtils.getPlansIcon(18, Color.WHITE), false, e -> showPlanDetailsDialog());
        JButton btnNavUsage = createSidebarButton("Monthly Usage", UIUtils.getUsageIcon(18, Color.WHITE), false, e -> new MyUsage(customer).setVisible(true));
        JButton btnNavBills = createSidebarButton("Current & Past Bills", UIUtils.getBillsIcon(18, Color.WHITE), false, e -> new MyBills(customer).setVisible(true));
        JButton btnNavPay = createSidebarButton("Pay Bills Online", UIUtils.getPaymentsIcon(18, Color.WHITE), false, e -> openPaymentDirectly());
        JButton btnNavHistory = createSidebarButton("Payment History", UIUtils.getHistoryIcon(18, Color.WHITE), false, e -> new PaymentHistory(customer).setVisible(true));
        JButton btnNavSupport = createSidebarButton("24/7 Support", UIUtils.getSupportIcon(18, Color.WHITE), false, e -> showSupportDialog());

        sidebar.add(lblNavHeading);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnNavOverview);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavProfile);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavPlan);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavUsage);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavBills);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavPay);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavHistory);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(btnNavSupport);

        sidebar.add(Box.createVerticalGlue());

        JButton btnRefreshData = new JButton("Refresh Data");
        UIUtils.styleSecondaryButton(btnRefreshData);
        btnRefreshData.setIcon(UIUtils.getRefreshIcon(14, UIUtils.TEXT_DARK));
        btnRefreshData.setIconTextGap(8);
        btnRefreshData.setMaximumSize(new Dimension(210, 36));
        btnRefreshData.setAlignmentX(LEFT_ALIGNMENT);
        btnRefreshData.addActionListener(e -> loadCustomerOverview());

        sidebar.add(btnRefreshData);
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
        JLabel lblOverviewHeader = new JLabel("My Active Broadband Subscription");
        lblOverviewHeader.setFont(UIUtils.FONT_HEADER);
        lblOverviewHeader.setForeground(UIUtils.TEXT_DARK);
        mainContent.add(lblOverviewHeader);
        mainContent.add(Box.createVerticalStrut(14));

        // KPI Summary Cards
        JPanel summaryGrid = new JPanel(new GridLayout(1, 4, 16, 16));
        summaryGrid.setOpaque(false);
        summaryGrid.setPreferredSize(new Dimension(800, 110));

        lblPlanName = new JLabel("Broadband");
        lblSpeed = new JLabel("Fast");
        lblDataLimit = new JLabel("0 GB");
        lblCurrentDue = new JLabel(UIUtils.formatCurrency(0.0));

        summaryGrid.add(createSummaryCard("Subscribed Plan", lblPlanName, UIUtils.SECONDARY_BLUE));
        summaryGrid.add(createSummaryCard("Bandwidth Speed", lblSpeed, UIUtils.SUCCESS_GREEN));
        summaryGrid.add(createSummaryCard("Monthly Data Limit", lblDataLimit, UIUtils.HEADER_NAVY));
        summaryGrid.add(createSummaryCard("Outstanding Balance", lblCurrentDue, UIUtils.DANGER_RED));

        mainContent.add(summaryGrid);
        mainContent.add(Box.createVerticalStrut(28));

        // Section 2: Self-Service Feature Cards
        JLabel lblNavHeader = new JLabel("Subscriber Self-Service Services");
        lblNavHeader.setFont(UIUtils.FONT_HEADER);
        lblNavHeader.setForeground(UIUtils.TEXT_DARK);
        mainContent.add(lblNavHeader);
        mainContent.add(Box.createVerticalStrut(14));

        JPanel navGrid = new JPanel(new GridLayout(2, 3, 16, 16));
        navGrid.setOpaque(false);
        navGrid.setPreferredSize(new Dimension(800, 220));

        navGrid.add(createNavCard("My Profile", "View & edit contact details", e -> new MyProfile(customer).setVisible(true)));
        navGrid.add(createNavCard("Internet Plan", "View speed, quota & monthly tariff", e -> showPlanDetailsDialog()));
        navGrid.add(createNavCard("Data Usage", "Check consumption & extra usage", e -> new MyUsage(customer).setVisible(true)));
        navGrid.add(createNavCard("View Invoices", "Inspect latest bills & itemization", e -> new MyBills(customer).setVisible(true)));
        navGrid.add(createNavCard("Pay Online", "Simulated UPI, card & netbanking", e -> openPaymentDirectly()));
        navGrid.add(createNavCard("Payment History", "View digital transaction receipts", e -> new PaymentHistory(customer).setVisible(true)));

        mainContent.add(navGrid);
        add(mainContent, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(UIUtils.BG_LIGHT);
        JLabel lblFoot = new JLabel("NETVEX | Customer Self-Service Portal");
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

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(UIUtils.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
            new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel bar = new JPanel();
        bar.setBackground(accent);
        bar.setPreferredSize(new Dimension(0, 4));
        card.add(bar, BorderLayout.NORTH);

        JPanel p = new JPanel(new GridLayout(2, 1, 2, 2));
        p.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(UIUtils.FONT_SMALL);
        t.setForeground(UIUtils.TEXT_MUTED);

        valueLabel.setFont(UIUtils.FONT_STAT_NUMBER);
        valueLabel.setForeground(UIUtils.TEXT_DARK);

        p.add(t);
        p.add(valueLabel);
        card.add(p, BorderLayout.CENTER);
        return card;
    }

    private JButton createNavCard(String title, String subtitle, ActionListener listener) {
        JButton btn = new JButton("<html><center><b><font color='#0F2747' size='+1'>" + title + "</font></b><br/><font color='#667085'>" + subtitle + "</font></center></html>");
        btn.setFont(UIUtils.FONT_REGULAR);
        btn.setBackground(UIUtils.CARD_BG);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
            new EmptyBorder(18, 14, 18, 14)
        ));
        btn.addActionListener(listener);
        return btn;
    }

    private void loadCustomerOverview() {
        if (customer == null) return;
        try {
            Plan plan = planDAO.getPlanById(customer.getPlanId());
            if (plan != null) {
                lblPlanName.setText(plan.getPlanName());
                lblSpeed.setText(plan.getSpeedTier());
                lblDataLimit.setText(plan.getDataLimit() + " GB");
            }

            Bill unpaidBill = billDAO.getCurrentUnpaidBill(customer.getCustomerId());
            if (unpaidBill != null) {
                lblCurrentDue.setText(UIUtils.formatCurrency(unpaidBill.getTotalAmount()));
                lblCurrentDue.setForeground(UIUtils.DANGER_RED);
            } else {
                lblCurrentDue.setText(UIUtils.formatCurrency(0.0));
                lblCurrentDue.setForeground(UIUtils.SUCCESS_GREEN);
            }
        } catch (SQLException ex) {
            System.err.println("Overview error: " + ex.getMessage());
        }
    }

    private void showPlanDetailsDialog() {
        if (customer == null) return;
        try {
            Plan plan = planDAO.getPlanById(customer.getPlanId());
            if (plan == null) {
                JOptionPane.showMessageDialog(this, "Plan details not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String msg = "========================================\n" +
                         "           MY INTERNET PLAN             \n" +
                         "========================================\n\n" +
                         "Plan Name:          " + plan.getPlanName() + "\n" +
                         "Speed Tier:         " + plan.getSpeedTier() + "\n" +
                         "Monthly Tariff:     " + UIUtils.formatCurrency(plan.getMonthlyTariff()) + " / month\n" +
                         "Monthly Data Limit: " + plan.getDataLimit() + " GB\n" +
                         "Extra Data Charge:  " + UIUtils.formatCurrency(plan.getExtraDataCharge()) + " / GB\n" +
                         "Equipment Rental:   " + UIUtils.formatCurrency(plan.getEquipmentRental()) + " / month\n" +
                         "Late Payment Fine:  " + UIUtils.formatCurrency(plan.getLatePaymentFine()) + "\n\n" +
                         "Unlimited Free Night Data Included (12 AM - 6 AM)\n" +
                         "========================================";

            JOptionPane.showMessageDialog(this, msg, "My Internet Plan Details", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching plan details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openPaymentDirectly() {
        if (customer == null) return;
        try {
            Bill unpaidBill = billDAO.getCurrentUnpaidBill(customer.getCustomerId());
            if (unpaidBill != null) {
                new PaymentForm(customer, unpaidBill, this::loadCustomerOverview).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Great news! You have no outstanding bills.\nAll your bills are paid in full.",
                    "No Outstanding Balance", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSupportDialog() {
        JOptionPane.showMessageDialog(this,
            "NETVEX 24/7 Customer Care\n\n" +
            "Toll-Free Helpline:  1800-NETVEX (1800-638-839)\n" +
            "Email Support:       support@netvex.com\n" +
            "Office Address:      100 Tech Park Blvd, Suite 400\n" +
            "Network Status:      Normal / Optimal",
            "Customer Support & Helpdesk", JOptionPane.INFORMATION_MESSAGE);
    }
}
