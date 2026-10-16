package internetbilling.gui;

import internetbilling.database.DBConnection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Welcome Screen for Internet Billing Management System.
 */
public class WelcomeFrame extends JFrame {

    private JButton btnAdminLogin;
    private JButton btnUserLogin;
    private JButton btnExit;
    private JLabel lblTitle;
    private JLabel lblSubtitle;
    private JLabel lblVersion;

    public WelcomeFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("NETVEX - Internet Billing Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 540);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UIUtils.PRIMARY_DARK); // Dark navy background #0F2747
        setLayout(new BorderLayout());

        // Center Content with Modern Card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel cardPanel = new JPanel();
        cardPanel.setPreferredSize(new Dimension(540, 380));
        cardPanel.setBackground(UIUtils.CARD_BG);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
            new EmptyBorder(30, 40, 30, 40)
        ));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));

        JLabel lblBrand = new JLabel("  NETVEX", SwingConstants.CENTER);
        lblBrand.setIcon(UIUtils.getGlobeIcon(16, UIUtils.SECONDARY_BLUE));
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBrand.setForeground(UIUtils.SECONDARY_BLUE);
        lblBrand.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Internet Billing Management", SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.TEXT_DARK);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Select an authorized portal to sign in", SwingConstants.CENTER);
        lblSub.setFont(UIUtils.FONT_SMALL);
        lblSub.setForeground(UIUtils.TEXT_MUTED);
        lblSub.setAlignmentX(CENTER_ALIGNMENT);

        btnAdminLogin = new JButton("ADMINISTRATOR PORTAL (ISP Office)");
        UIUtils.stylePrimaryButton(btnAdminLogin);
        btnAdminLogin.setIcon(UIUtils.getUserIcon(18, Color.WHITE));
        btnAdminLogin.setIconTextGap(10);
        btnAdminLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdminLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnAdminLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnAdminLogin.addActionListener(e -> openAdminLogin());

        btnUserLogin = new JButton("SUBSCRIBER PORTAL (Customer Login)");
        UIUtils.stylePrimaryButton(btnUserLogin);
        btnUserLogin.setIcon(UIUtils.getCustomersIcon(18, Color.WHITE));
        btnUserLogin.setIconTextGap(10);
        btnUserLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUserLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnUserLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnUserLogin.addActionListener(e -> openUserLogin());

        JPanel bottomBtnRow = new JPanel(new GridLayout(1, 1, 0, 0));
        bottomBtnRow.setOpaque(false);
        bottomBtnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        btnExit = new JButton("Exit System");
        UIUtils.styleDangerButton(btnExit);
        btnExit.setIcon(UIUtils.getLogoutIcon(14, Color.WHITE));
        btnExit.setIconTextGap(8);
        btnExit.addActionListener(e -> System.exit(0));

        bottomBtnRow.add(btnExit);

        JLabel lblFootNote = new JLabel("  Connected to: " + DBConnection.getDbEngine() + " Database", SwingConstants.CENTER);
        lblFootNote.setIcon(UIUtils.getCheckIcon(12, UIUtils.SUCCESS_GREEN));
        lblFootNote.setFont(UIUtils.FONT_SMALL);
        lblFootNote.setForeground(UIUtils.SUCCESS_GREEN);
        lblFootNote.setAlignmentX(CENTER_ALIGNMENT);

        cardPanel.add(lblBrand);
        cardPanel.add(Box.createVerticalStrut(6));
        cardPanel.add(lblTitle);
        cardPanel.add(Box.createVerticalStrut(2));
        cardPanel.add(lblSub);
        cardPanel.add(Box.createVerticalStrut(24));
        cardPanel.add(btnAdminLogin);
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(btnUserLogin);
        cardPanel.add(Box.createVerticalStrut(18));
        cardPanel.add(bottomBtnRow);
        cardPanel.add(Box.createVerticalStrut(16));
        cardPanel.add(lblFootNote);

        centerPanel.add(cardPanel, new GridBagConstraints());
        add(centerPanel, BorderLayout.CENTER);

        // Subtle bottom tag
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(UIUtils.PRIMARY_DARK);
        lblVersion = new JLabel("Enterprise ISP Management System | Professional Edition");
        lblVersion.setFont(UIUtils.FONT_SMALL);
        lblVersion.setForeground(new Color(0x66, 0x70, 0x85));
        footerPanel.add(lblVersion);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void openAdminLogin() {
        this.dispose();
        new AdminLogin().setVisible(true);
    }

    private void openUserLogin() {
        this.dispose();
        new UserLogin().setVisible(true);
    }



    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new WelcomeFrame().setVisible(true));
    }
}
