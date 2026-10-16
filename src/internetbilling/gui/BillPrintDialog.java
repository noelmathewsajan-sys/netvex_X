package internetbilling.gui;

import internetbilling.model.Bill;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * Modal dialog to view and print official formatted Internet Invoices.
 * Uses Java Printing APIs (JTextArea.print()).
 */
public class BillPrintDialog extends JDialog {

    private final Bill bill;
    private JTextArea txtReceipt;
    private JButton btnPrint;
    private JButton btnClose;

    public BillPrintDialog(JFrame parent, Bill bill) {
        super(parent, "Internet Bill Receipt - " + (bill != null ? bill.getBillId() : ""), true);
        this.bill = bill;
        initComponents();
        generateReceiptText();
    }

    private void initComponents() {
        setSize(540, 680);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIUtils.BG_LIGHT);

        // Header
        JPanel header = UIUtils.createHeaderPanel("INVOICE VIEWER & PRINT", "Official ISP Customer Bill Statement");
        add(header, BorderLayout.NORTH);

        // Receipt Area
        txtReceipt = new JTextArea();
        txtReceipt.setEditable(false);
        txtReceipt.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtReceipt.setBackground(Color.WHITE);
        txtReceipt.setForeground(Color.BLACK);
        txtReceipt.setBorder(new EmptyBorder(15, 20, 15, 20));

        JScrollPane scrollPane = new JScrollPane(txtReceipt);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Actions
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnPanel.setBackground(UIUtils.BG_LIGHT);

        btnPrint = new JButton("PRINT BILL");
        UIUtils.stylePrimaryButton(btnPrint);
        btnPrint.setIcon(UIUtils.getPrintIcon(14, Color.WHITE));
        btnPrint.setIconTextGap(6);
        btnPrint.addActionListener(e -> printBill());

        btnClose = new JButton("CLOSE");
        UIUtils.styleSecondaryButton(btnClose);
        btnClose.setIcon(UIUtils.getBackIcon(12, UIUtils.TEXT_DARK));
        btnClose.setIconTextGap(6);
        btnClose.addActionListener(e -> dispose());

        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void generateReceiptText() {
        if (bill == null) {
            txtReceipt.setText("No bill information available.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================\n");
        sb.append("                     NETVEX BILL                        \n");
        sb.append("          NETVEX INTERNET BILLING MANAGEMENT SYSTEM     \n");
        sb.append("========================================================\n\n");

        sb.append(String.format("Bill ID:          %s\n", bill.getBillId()));
        sb.append(String.format("Customer ID:      %s\n", bill.getCustomerId()));
        sb.append(String.format("Customer Name:    %s\n", bill.getCustomerName() != null ? bill.getCustomerName() : "N/A"));
        sb.append(String.format("Address:          %s\n\n", bill.getCustomerAddress() != null ? bill.getCustomerAddress() : "N/A"));

        sb.append(String.format("Internet Plan:    %s\n", bill.getPlanName() != null ? bill.getPlanName() : "Broadband Plan"));
        sb.append(String.format("Bandwidth Speed:  %s\n", bill.getSpeedTier() != null ? bill.getSpeedTier() : "Standard"));
        sb.append(String.format("Billing Month:    %s\n\n", bill.getBillingMonth()));

        if (bill.getDataUsed() != null) {
            sb.append(String.format("Total Data Used:  %.2f GB\n", bill.getDataUsed()));
            sb.append(String.format("Extra Data Used:  %.2f GB\n", bill.getExtraData() != null ? bill.getExtraData() : 0.0));
        }

        sb.append("--------------------------------------------------------\n");
        sb.append(String.format("Plan Tariff:              Rs.%10.2f\n", bill.getPlanTariff()));
        sb.append(String.format("Equipment Rental:         Rs.%10.2f\n", bill.getEquipmentRental()));
        sb.append(String.format("Extra Usage Charge:       Rs.%10.2f\n", bill.getExtraUsageCharge()));
        sb.append(String.format("Late Payment Fine:        Rs.%10.2f\n", bill.getLateFine()));
        sb.append("--------------------------------------------------------\n");
        sb.append(String.format("TOTAL AMOUNT:             Rs.%10.2f\n\n", bill.getTotalAmount()));

        sb.append(String.format("Due Date:                 %s\n", bill.getDueDate() != null ? bill.getDueDate().toString() : "Immediate"));
        sb.append(String.format("Payment Status:           [%s]\n\n", bill.getBillStatus()));

        sb.append("========================================================\n");
        sb.append("            Thank You For Choosing NETVEX!              \n");
        sb.append("         For 24/7 Support: support@netvex.com           \n");
        sb.append("========================================================\n");

        txtReceipt.setText(sb.toString());
        txtReceipt.setCaretPosition(0);
    }

    private void printBill() {
        try {
            MessageFormat header = new MessageFormat("NETVEX Bill - " + bill.getBillId());
            MessageFormat footer = new MessageFormat("Page {0}");
            boolean complete = txtReceipt.print(header, footer, true, null, null, true);
            if (complete) {
                JOptionPane.showMessageDialog(this, "Bill printed successfully!", "Print", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Printing Error: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
