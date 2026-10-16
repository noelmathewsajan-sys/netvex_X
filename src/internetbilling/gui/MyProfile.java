package internetbilling.gui;

import internetbilling.dao.CustomerDAO;
import internetbilling.model.Customer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Customer Profile View and Contact Information Update.
 */
public class MyProfile extends JFrame {

    private final Customer customer;
    private final CustomerDAO customerDAO;

    private JTextField txtCustomerId;
    private JTextField txtName;
    private JTextField txtAddress;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtUsername;
    private JTextField txtPlan;
    private JTextField txtConnDate;
    private JTextField txtStatus;

    private JButton btnUpdateContact;
    private JButton btnClose;

    public MyProfile(Customer customer) {
        this.customer = customer;
        this.customerDAO = new CustomerDAO();
        initComponents();
        loadProfileData();
    }

    private void initComponents() {
        setTitle("My Profile - Internet Billing Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UIUtils.BG_LIGHT);
        setLayout(new BorderLayout());

        // Header
        JPanel header = UIUtils.createHeaderPanel("SUBSCRIBER PROFILE", "View your registered ISP account information");
        add(header, BorderLayout.NORTH);

        // Center Profile Card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UIUtils.CARD_BG);
        card.setPreferredSize(new Dimension(540, 380));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
            new EmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        txtCustomerId = createField(false);
        txtName = createField(false);
        txtAddress = createField(true);
        txtPhone = createField(true);
        txtEmail = createField(true);
        txtUsername = createField(false);
        txtPlan = createField(false);
        txtConnDate = createField(false);
        txtStatus = createField(false);

        addRow(card, gbc, 0, "Customer ID:", txtCustomerId);
        addRow(card, gbc, 1, "Full Name:", txtName);
        addRow(card, gbc, 2, "Address:", txtAddress);
        addRow(card, gbc, 3, "Phone Number:", txtPhone);
        addRow(card, gbc, 4, "Email Address:", txtEmail);
        addRow(card, gbc, 5, "Portal Username:", txtUsername);
        addRow(card, gbc, 6, "Subscribed Plan:", txtPlan);
        addRow(card, gbc, 7, "Connection Date:", txtConnDate);
        addRow(card, gbc, 8, "Account Status:", txtStatus);

        centerPanel.add(card, new GridBagConstraints());
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Actions
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        btnPanel.setBackground(UIUtils.BG_LIGHT);

        btnUpdateContact = new JButton("UPDATE CONTACT INFO");
        UIUtils.stylePrimaryButton(btnUpdateContact);
        btnUpdateContact.setIcon(UIUtils.getCheckIcon(13, Color.WHITE));
        btnUpdateContact.setIconTextGap(6);
        btnUpdateContact.addActionListener(e -> updateContactInfo());

        btnClose = new JButton("CLOSE");
        UIUtils.styleSecondaryButton(btnClose);
        btnClose.setIcon(UIUtils.getBackIcon(12, UIUtils.TEXT_DARK));
        btnClose.setIconTextGap(6);
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnUpdateContact);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JTextField createField(boolean editable) {
        JTextField tf = new JTextField(25); // Added columns
        tf.setPreferredSize(new Dimension(280, 34)); // Fixed squished width issue
        tf.setMinimumSize(new Dimension(280, 34));   // Force minimum size to prevent layout squishing
        tf.setFont(UIUtils.FONT_REGULAR);
        tf.setEditable(editable);
        if (!editable) {
            tf.setBackground(new Color(241, 245, 249));
            tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
            ));
        } else {
            UIUtils.styleTextField(tf);
        }
        return tf;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int y, String label, JTextField tf) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_REGULAR_BOLD);
        p.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.weightx = 0.65;
        p.add(tf, gbc);
    }

    private void loadProfileData() {
        if (customer != null) {
            txtCustomerId.setText(customer.getCustomerId());
            txtName.setText(customer.getName());
            txtAddress.setText(customer.getAddress());
            txtPhone.setText(customer.getPhone());
            txtEmail.setText(customer.getEmail());
            txtUsername.setText(customer.getUsername());
            txtPlan.setText(customer.getPlanName() != null ? customer.getPlanName() : customer.getPlanId());
            txtConnDate.setText(customer.getConnectionDate() != null ? customer.getConnectionDate().toString() : "N/A");
            txtStatus.setText(customer.getStatus());
        }
    }

    private void updateContactInfo() {
        String newAddr = txtAddress.getText().trim();
        String newPhone = txtPhone.getText().trim();
        String newEmail = txtEmail.getText().trim();

        if (newAddr.isEmpty() || newPhone.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Address, Phone, and Email cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newEmail.contains("@") || !newEmail.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        customer.setAddress(newAddr);
        customer.setPhone(newPhone);
        customer.setEmail(newEmail);

        try {
            boolean success = customerDAO.updateCustomer(customer);
            if (success) {
                JOptionPane.showMessageDialog(this, "Contact information updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating contact info: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
