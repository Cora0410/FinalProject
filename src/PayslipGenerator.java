import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PayslipGenerator implements Printable {
    private Employee employee;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private JFrame parentFrame;
    
    public PayslipGenerator(Employee employee, JFrame parentFrame) {
        this.employee = employee;
        this.parentFrame = parentFrame;
    }
    
    public void generateAndShowPayslip() {
        if (employee == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                "No employee selected. Please select an employee first.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create a print job
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        
        // Show print dialog
        if (job.printDialog()) {
            try {
                // Preview the payslip first
                showPayslipPreview();
                
                // Print the payslip
                job.print();
                JOptionPane.showMessageDialog(parentFrame, 
                    "Payslip generated successfully for " + employee.getName(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Error printing payslip: " + e.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showPayslipPreview() {
        JDialog previewDialog = new JDialog(parentFrame, "Payslip Preview", true);
        previewDialog.setLayout(new BorderLayout());
        
        // Create a panel that displays the payslip
        JPanel payslipPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPayslip(g);
            }
        };
        
        payslipPanel.setPreferredSize(new Dimension(600, 800)); // A4 paper size scaled
        
        JScrollPane scrollPane = new JScrollPane(payslipPanel);
        previewDialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close Preview");
        closeButton.addActionListener(e -> previewDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        previewDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        previewDialog.setSize(650, 850);
        previewDialog.setLocationRelativeTo(parentFrame);
        previewDialog.setVisible(true);
    }
    
    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }
        
        // Get the printable area
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        
        // Draw the payslip
        drawPayslip(g);
        
        return PAGE_EXISTS;
    }
    
    private void drawPayslip(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Set rendering hints for better quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int y = 50; // Starting y position
        int leftMargin = 50;
        int rightCol = 350;
        
        // Draw company header
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("PHILIPPINE PAYROLL MANAGEMENT SYSTEM", leftMargin, y);
        y += 25;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("123 Business Street, Manila, Philippines", leftMargin, y);
        y += 20;
        
        // Draw payslip title
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("PAYSLIP", leftMargin, y);
        y += 30;
        
        // Draw horizontal line
        g2d.drawLine(leftMargin, y, 550, y);
        y += 20;
        
        // Employee information section
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Employee Information", leftMargin, y);
        y += 25;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Employee ID:", leftMargin, y);
        g2d.drawString(employee.getId(), leftMargin + 120, y);
        
        g2d.drawString("Pay Period:", rightCol, y);
        String payPeriod = "";
        if (employee.getDate() != null) {
            LocalDate startDate = employee.getDate().withDayOfMonth(1);
            LocalDate endDate = employee.getDate().withDayOfMonth(employee.getDate().lengthOfMonth());
            payPeriod = startDate.format(dateFormatter) + " to " + endDate.format(dateFormatter);
        }
        g2d.drawString(payPeriod, rightCol + 120, y);
        y += 20;
        
        g2d.drawString("Employee Name:", leftMargin, y);
        g2d.drawString(employee.getName(), leftMargin + 120, y);
        
        g2d.drawString("Pay Date:", rightCol, y);
        String payDate = employee.getDate() != null ? employee.getDate().format(dateFormatter) : "";
        g2d.drawString(payDate, rightCol + 120, y);
        y += 30;
        
        // Draw horizontal line
        g2d.drawLine(leftMargin, y, 550, y);
        y += 20;
        
        // Earnings section
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Earnings", leftMargin, y);
        y += 25;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Regular Hours:", leftMargin, y);
        g2d.drawString(String.format("%.2f", employee.getTotalHours()), rightCol, y);
        y += 20;
        
        if (employee.getOvertime() > 0) {
            g2d.drawString("Overtime Hours:", leftMargin, y);
            g2d.drawString(String.format("%.2f", employee.getOvertime()), rightCol, y);
            y += 20;
        }
        
        g2d.drawString("Gross Pay:", leftMargin, y);
        g2d.drawString(String.format("₱ %.2f", employee.getGrossPay()), rightCol, y);
        y += 30;
        
        // Draw horizontal line
        g2d.drawLine(leftMargin, y, 550, y);
        y += 20;
        
        // Deductions section
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Deductions", leftMargin, y);
        y += 25;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("SSS Deduction:", leftMargin, y);
        g2d.drawString(String.format("₱ %.2f", employee.getSssDeduction()), rightCol, y);
        y += 20;
        
        g2d.drawString("PhilHealth Deduction:", leftMargin, y);
        g2d.drawString(String.format("₱ %.2f", employee.getPhilhealthDeduction()), rightCol, y);
        y += 20;
        
        g2d.drawString("Pag-IBIG Deduction:", leftMargin, y);
        g2d.drawString(String.format("₱ %.2f", employee.getPagibigDeduction()), rightCol, y);
        y += 20;
        
        g2d.drawString("Tax Deduction:", leftMargin, y);
        g2d.drawString(String.format("₱ %.2f", employee.getTaxDeduction()), rightCol, y);
        y += 20;
        
        g2d.drawString("Total Deductions:", leftMargin, y);
        g2d.drawString(String.format("₱ %.2f", employee.getTotalDeduction()), rightCol, y);
        y += 30;
        
        // Draw horizontal line
        g2d.drawLine(leftMargin, y, 550, y);
        y += 20;
        
        // Net pay section
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Net Pay:", leftMargin, y);
        g2d.drawString(String.format("₱ %.2f", employee.getNetPay()), rightCol, y);
        y += 40;
        
        // Signature section
        g2d.drawLine(leftMargin, y, 250, y);
        g2d.drawLine(350, y, 550, y);
        y += 15;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("Employee Signature", leftMargin + 50, y);
        g2d.drawString("Authorized Signature", 350 + 50, y);
        y += 30;
        
        // Footer
        g2d.setFont(new Font("Arial", Font.ITALIC, 10));
        g2d.drawString("This is a computer-generated payslip and does not require a signature.", leftMargin, y);
        y += 15;
        g2d.drawString("Any questions regarding this payslip should be directed to the HR Department.", leftMargin, y);
    }
}