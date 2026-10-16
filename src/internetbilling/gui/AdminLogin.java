package internetbilling.gui;

import internetbilling.model.Admin;
import internetbilling.service.LoginService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
 * Modern ISP-Themed Admin Login Screen.
 * Features Dark Navy background (#0F2747), centered crisp white login card,
 * and Professional Blue (#1976D2) action button.
 */
public class AdminLogin extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnClear;
    private JButton btnBack;
    private final LoginService loginService;

    public AdminLogin() {
        this.loginService = new LoginService();
        initComponents();
    }

    private void initComponents() {
        setTitle("NETVEX Admin Portal Login - Internet Billing Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 540);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UIUtils.PRIMARY_DARK); // Dark navy background
        setLayout(new BorderLayout());

        // Center Login Panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        // White Login Card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIUtils.CARD_BG);
        card.setPreferredSize(new Dimension(440, 420));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
            new EmptyBorder(30, 36, 30, 36)
        ));

        // ISP Brand Tag
        JLabel lblBrand = new JLabel("  NETVEX NETWORK OPERATIONS", SwingConstants.CENTER);
        lblBrand.setIcon(UIUtils.getGlobeIcon(14, UIUtils.SECONDARY_BLUE));
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblBrand.setForeground(UIUtils.SECONDARY_BLUE);
        lblBrand.setAlignmentX(CENTER_ALIGNMENT);

        // Card Title
        JLabel lblTitle = new JLabel("Administrator Login", SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.TEXT_DARK);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        // Subtitle
        JLabel lblSubtitle = new JLabel("Secure staff credentials required", SwingConstants.CENTER);
        lblSubtitle.setFont(UIUtils.FONT_SMALL);
        lblSubtitle.setForeground(UIUtils.TEXT_MUTED);
        lblSubtitle.setAlignmentX(CENTER_ALIGNMENT);

        // Form Fields Container
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIUtils.FONT_REGULAR_BOLD);
        lblUser.setForeground(UIUtils.TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblUser, gbc);

        txtUsername = new JTextField(20);
        txtUsername.setPreferredSize(new Dimension(360, 36));
        UIUtils.styleTextField(txtUsername);
        gbc.gridy = 1;
        formPanel.add(txtUsername, gbc);

        // Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UIUtils.FONT_REGULAR_BOLD);
        lblPass.setForeground(UIUtils.TEXT_DARK);
        gbc.gridy = 2;
        formPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setPreferredSize(new Dimension(360, 36));
        UIUtils.styleTextField(txtPassword);
        gbc.gridy = 3;
        formPanel.add(txtPassword, gbc);

        // Primary Login Button
        btnLogin = new JButton("SIGN IN TO ADMIN CONSOLE");
        UIUtils.stylePrimaryButton(btnLogin);
        btnLogin.setIcon(UIUtils.getUserIcon(16, Color.WHITE));
        btnLogin.setIconTextGap(8);
        btnLogin.setPreferredSize(new Dimension(360, 40));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> performLogin());

        // Secondary Buttons Row (Back & Clear)
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnRow.setOpaque(false);

        btnClear = new JButton("Clear");
        UIUtils.styleSecondaryButton(btnClear);
        btnClear.setIcon(UIUtils.getRefreshIcon(13, UIUtils.TEXT_DARK));
        btnClear.setIconTextGap(6);
        btnClear.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
            txtUsername.requestFocus();
        });

        btnBack = new JButton("Back to Welcome");
        UIUtils.styleSecondaryButton(btnBack);
        btnBack.setIcon(UIUtils.getBackIcon(13, UIUtils.TEXT_DARK));
        btnBack.setIconTextGap(6);
        btnBack.addActionListener(e -> {
            dispose();
            new WelcomeFrame().setVisible(true);
        });

        btnRow.add(btnClear);
        btnRow.add(btnBack);

        // Hint label
        JLabel lblHint = new JLabel("Default Access: admin / admin123", SwingConstants.CENTER);
        lblHint.setFont(UIUtils.FONT_SMALL);
        lblHint.setForeground(UIUtils.TEXT_MUTED);
        lblHint.setAlignmentX(CENTER_ALIGNMENT);

        // Assemble Card
        card.add(lblBrand);
        card.add(Box.createVerticalStrut(4));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(2));
        card.add(lblSubtitle);
        card.add(Box.createVerticalStrut(16));
        card.add(formPanel);
        card.add(Box.createVerticalStrut(14));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(10));
        card.add(btnRow);
        card.add(Box.createVerticalStrut(12));
        card.add(lblHint);

        centerPanel.add(card, new GridBagConstraints());
        add(centerPanel, BorderLayout.CENTER);

        // Enter key activates login
        getRootPane().setDefaultButton(btnLogin);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password.",
                "Input Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Admin admin = loginService.authenticateAdmin(username, password);
            if (admin != null) {
                this.dispose();
                new AdminDashboard(admin).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Database Connection Notice:\n" + ex.getMessage() +
                "\n\nPlease ensure your database server is running.",
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new AdminLogin().setVisible(true));
    }
}
