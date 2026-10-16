package internetbilling.gui;

import internetbilling.model.Bill;
import internetbilling.model.Customer;
import internetbilling.model.Payment;
import internetbilling.service.PaymentService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Simulated Online Payment Form for college project.
 * Supports UPI, Debit Card, Credit Card, and Net Banking.
 */
public class PaymentForm extends JFrame {

    private final Customer customer;
    private final Bill bill;
    private final PaymentService paymentService;
    private final Runnable onPaymentSuccessCallback;

    private JTextField txtBillId;
    private JTextField txtCustomerId;
    private JTextField txtAmount;
    private JComboBox<String> cmbPaymentMethod;
    private JTextField txtReference;
    private JLabel lblRefPrompt;
    private JButton btnPayNow;
    private JButton btnCancel;

    public PaymentForm(Customer customer, Bill bill, Runnable callback) {
        this.customer = customer;
        this.bill = bill;
        this.paymentService = new PaymentService();
        this.onPaymentSuccessCallback = callback;
        initComponents();
        populateBillDetails();
    }

    private void initComponents() {
        setTitle("NETVEX Simulated Online Payment - Internet Billing Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(580, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UIUtils.BG_LIGHT);
        setLayout(new BorderLayout());

        // Header
        JPanel header = UIUtils.createHeaderPanel(
            "ONLINE PAYMENT GATEWAY (SIMULATED)",
            "Instant & secure bill clearance for NETVEX high-speed broadband"
        );
        add(header, BorderLayout.NORTH);

        // Center Card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UIUtils.CARD_BG);
        card.setPreferredSize(new Dimension(480, 310));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
            new EmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        txtBillId = new JTextField();
        txtBillId.setEditable(false);
        txtBillId.setBackground(new Color(241, 245, 249));

        txtCustomerId = new JTextField();
        txtCustomerId.setEditable(false);
        txtCustomerId.setBackground(new Color(241, 245, 249));

        txtAmount = new JTextField();
        txtAmount.setEditable(false);
        txtAmount.setFont(UIUtils.FONT_TITLE);
        txtAmount.setForeground(UIUtils.SUCCESS_GREEN);
        txtAmount.setBackground(new Color(241, 245, 249));

        cmbPaymentMethod = new JComboBox<>(new String[]{"UPI", "Debit Card", "Credit Card", "Net Banking"});
        cmbPaymentMethod.setFont(UIUtils.FONT_REGULAR);
        cmbPaymentMethod.addActionListener(e -> updateMethodPrompt());

        txtReference = new JTextField();
        UIUtils.styleTextField(txtReference);
        txtReference.setPreferredSize(new Dimension(200, 32));

        lblRefPrompt = new JLabel("Payment Reference (UPI ID / VPA):");
        lblRefPrompt.setFont(UIUtils.FONT_REGULAR_BOLD);

        addRow(card, gbc, 0, "Bill ID:", txtBillId);
        addRow(card, gbc, 1, "Customer ID:", txtCustomerId);
        addRow(card, gbc, 2, "Payable Amount (" + UIUtils.CURRENCY_SYMBOL + "):", txtAmount);
        addRow(card, gbc, 3, "Payment Method:", cmbPaymentMethod);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.4;
        card.add(lblRefPrompt, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0.6;
        card.add(txtReference, gbc);

        centerPanel.add(card, new GridBagConstraints());
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Action Buttons
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        bottomBar.setBackground(UIUtils.BG_LIGHT);

        btnPayNow = new JButton("PAY NOW");
        UIUtils.styleSuccessButton(btnPayNow);
        btnPayNow.setIcon(UIUtils.getPaymentsIcon(14, Color.WHITE));
        btnPayNow.setIconTextGap(6);
        btnPayNow.addActionListener(e -> processPayment());

        btnCancel = new JButton("CANCEL");
        UIUtils.styleSecondaryButton(btnCancel);
        btnCancel.setIcon(UIUtils.getBackIcon(12, UIUtils.TEXT_DARK));
        btnCancel.setIconTextGap(6);
        btnCancel.addActionListener(e -> dispose());

        bottomBar.add(btnPayNow);
        bottomBar.add(btnCancel);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int y, String label, javax.swing.JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0.4;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIUtils.FONT_REGULAR_BOLD);
        p.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.weightx = 0.6;
        comp.setFont(UIUtils.FONT_REGULAR);
        comp.setPreferredSize(new Dimension(200, 30));
        comp.setMinimumSize(new Dimension(200, 30));
        p.add(comp, gbc);
    }

    private void updateMethodPrompt() {
        String method = (String) cmbPaymentMethod.getSelectedItem();
        txtReference.setText(""); // Start with empty field for realism
        
        if ("UPI".equalsIgnoreCase(method)) {
            lblRefPrompt.setText("Enter UPI ID (e.g. name@bank):");
        } else if ("Debit Card".equalsIgnoreCase(method) || "Credit Card".equalsIgnoreCase(method)) {
            lblRefPrompt.setText("Enter 16-Digit Card Number:");
        } else {
            lblRefPrompt.setText("Select Bank (or enter Bank Code):");
        }
    }

    private void populateBillDetails() {
        if (bill != null) {
            txtBillId.setText(bill.getBillId());
            txtCustomerId.setText(bill.getCustomerId());
            txtAmount.setText(bill.getTotalAmount().toString());
            updateMethodPrompt();
        } else if (customer != null) {
            txtCustomerId.setText(customer.getCustomerId());
        }
    }

    private void processPayment() {
        String billId = txtBillId.getText().trim();
        String customerId = txtCustomerId.getText().trim();
        String method = (String) cmbPaymentMethod.getSelectedItem();
        String reference = txtReference.getText().trim();

        if (billId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid bill selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (reference.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide payment reference / details.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Basic validation for realism
        if (("Debit Card".equalsIgnoreCase(method) || "Credit Card".equalsIgnoreCase(method)) && reference.length() < 12) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 16-digit card number.", "Invalid Card", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(txtAmount.getText().trim());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Simulate realistic gateway processing delay
        btnPayNow.setEnabled(false);
        btnPayNow.setText("Processing...");
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

        javax.swing.Timer timer = new javax.swing.Timer(2000, e -> {
            try {
                Payment payment = paymentService.processPayment(billId, customerId, amount, method, reference);
                setCursor(java.awt.Cursor.getDefaultCursor());
                
                JOptionPane.showMessageDialog(this,
                    "Payment Successful!\n\n" +
                    "Payment ID:       " + payment.getPaymentId() + "\n" +
                    "Bill ID:          " + payment.getBillId() + "\n" +
                    "Amount Paid:      " + UIUtils.formatCurrency(payment.getAmount()) + "\n" +
                    "Payment Method:   " + payment.getPaymentMethod() + "\n" +
                    "Transaction Date: " + payment.getPaymentDate() + "\n\n" +
                    "Thank you! Your bill status is now PAID.",
                    "Payment Confirmation", JOptionPane.INFORMATION_MESSAGE);

                dispose();
                if (onPaymentSuccessCallback != null) {
                    onPaymentSuccessCallback.run();
                }
            } catch (Exception ex) {
                setCursor(java.awt.Cursor.getDefaultCursor());
                btnPayNow.setEnabled(true);
                btnPayNow.setText("PAY NOW");
                JOptionPane.showMessageDialog(this, "Payment Failed: " + ex.getMessage(), "Transaction Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
}
