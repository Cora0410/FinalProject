import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PayslipGenerator implements Printable {
    private Employee employee;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private JFrame parentFrame;
    private EmployeeDAO employeeDAO;
    private boolean isYearlyPayslip = false;
    private List<Employee> yearlyRecords;
    private Employee aggregatedEmployee; // For yearly totals
    
    public PayslipGenerator(Employee employee, JFrame parentFrame) {
        this.employee = employee;
        this.parentFrame = parentFrame;
        this.employeeDAO = new EmployeeDAO(); // Initialize DAO
    }
    
    public void generateAndShowPayslip() {
        if (employee == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                "No employee selected. Please select an employee first.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show payslip type selection dialog
        String[] options = {"Monthly Payslip", "Yearly Payslip", "Cancel"};
        int choice = JOptionPane.showOptionDialog(parentFrame,
            "Select payslip type:",
            "Payslip Type",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == 2 || choice == -1) { // Cancel or closed
            return;
        }
        
        isYearlyPayslip = (choice == 1);
        
        if (isYearlyPayslip) {
            // Get year input from user
            String yearStr = JOptionPane.showInputDialog(parentFrame, 
                "Enter year for yearly payslip:", 
                String.valueOf(employee.getDate().getYear()));
            
            if (yearStr == null || yearStr.trim().isEmpty()) {
                return; // User cancelled
            }
            
            try {
                int year = Integer.parseInt(yearStr.trim());
                prepareYearlyPayslip(year);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Invalid year format. Please enter a valid year.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
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
                String payslipType = isYearlyPayslip ? "Yearly" : "Monthly";
                JOptionPane.showMessageDialog(parentFrame, 
                    payslipType + " payslip generated successfully for " + employee.getName(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Error printing payslip: " + e.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void prepareYearlyPayslip(int year) {
        // Get all records for this employee for the specified year
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        
        // Get all employee records for the year
        List<Employee> allYearRecords = employeeDAO.getEmployeesForDateRange(startDate, endDate);
        yearlyRecords = allYearRecords.stream()
                .filter(emp -> emp.getId().equals(employee.getId()))
                .toList();
        
        if (yearlyRecords.isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, 
                "No records found for employee " + employee.getName() + " in year " + year,
                "No Data", JOptionPane.INFORMATION_MESSAGE);
            isYearlyPayslip = false; // Fall back to monthly
            return;
        }
        
        // Create aggregated employee data
        aggregatedEmployee = createAggregatedEmployee(yearlyRecords, year);
    }
    
    private Employee createAggregatedEmployee(List<Employee> records, int year) {
        if (records.isEmpty()) return null;
        
        Employee first = records.get(0);
        double totalHours = 0;
        double totalOvertime = 0;
        double totalWage = 0;
        double totalSss = 0;
        double totalPhilhealth = 0;
        double totalPagibig = 0;
        double totalTax = 0;
        int presentDays = 0;
        int leaveDays = 0;
        int absentDays = 0;
        
        for (Employee emp : records) {
            totalHours += emp.getTotalHours();
            totalOvertime += emp.getOvertime();
            totalWage += emp.getWage();
            totalSss += emp.getSssDeduction();
            totalPhilhealth += emp.getPhilhealthDeduction();
            totalPagibig += emp.getPagibigDeduction();
            totalTax += emp.getTaxDeduction();
            
            // Count attendance types
            String attendance = emp.getAttendance();
            if ("Present".equals(attendance)) {
                presentDays++;
            } else if ("Absent".equals(attendance)) {
                absentDays++;
            } else {
                leaveDays++; // All leave types
            }
        }
        
        // Create aggregated employee
        Employee aggregated = new Employee(
            first.getId(),
            first.getName(),
            totalHours,
            totalOvertime,
            totalWage,
            LocalDate.of(year, 12, 31), // Use end of year as date
            totalSss,
            totalPhilhealth,
            totalPagibig,
            totalTax,
            presentDays + " Present, " + leaveDays + " Leave, " + absentDays + " Absent"
        );
        
        return aggregated;
    }
    
    private void showPayslipPreview() {
        String title = isYearlyPayslip ? "Yearly Payslip Preview" : "Monthly Payslip Preview";
        JDialog previewDialog = new JDialog(parentFrame, title, true);
        previewDialog.setLayout(new BorderLayout());
        
        // Create a panel that displays the payslip
        JPanel payslipPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPayslip(g);
            }
        };
        
        int panelHeight = isYearlyPayslip ? 900 : 800; // Taller for yearly payslip
        payslipPanel.setPreferredSize(new Dimension(600, panelHeight));
        
        JScrollPane scrollPane = new JScrollPane(payslipPanel);
        previewDialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close Preview");
        closeButton.addActionListener(e -> previewDialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        previewDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        previewDialog.setSize(650, panelHeight + 100);
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
        
        Employee currentEmployee = isYearlyPayslip ? aggregatedEmployee : employee;
        if (currentEmployee == null) return;
        
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
        String payslipTitle = isYearlyPayslip ? "YEARLY PAYSLIP" : "MONTHLY PAYSLIP";
        g2d.drawString(payslipTitle, leftMargin, y);
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
        g2d.drawString(currentEmployee.getId(), leftMargin + 120, y);
        
        if (isYearlyPayslip) {
            g2d.drawString("Year:", rightCol, y);
            g2d.drawString(String.valueOf(currentEmployee.getDate().getYear()), rightCol + 120, y);
        } else {
            g2d.drawString("Pay Period:", rightCol, y);
            String payPeriod = "";
            if (currentEmployee.getDate() != null) {
                LocalDate startDate = currentEmployee.getDate().withDayOfMonth(1);
                LocalDate endDate = currentEmployee.getDate().withDayOfMonth(currentEmployee.getDate().lengthOfMonth());
                payPeriod = startDate.format(dateFormatter) + " to " + endDate.format(dateFormatter);
            }
            g2d.drawString(payPeriod, rightCol + 120, y);
        }
        y += 20;
        
        g2d.drawString("Employee Name:", leftMargin, y);
        g2d.drawString(currentEmployee.getName(), leftMargin + 120, y);
        
        if (isYearlyPayslip) {
            g2d.drawString("Total Records:", rightCol, y);
            g2d.drawString(String.valueOf(yearlyRecords.size()), rightCol + 120, y);
        } else {
            g2d.drawString("Pay Date:", rightCol, y);
            String payDate = currentEmployee.getDate() != null ? currentEmployee.getDate().format(dateFormatter) : "";
            g2d.drawString(payDate, rightCol + 120, y);
        }
        y += 30;
        
        // Draw horizontal line
        g2d.drawLine(leftMargin, y, 550, y);
        y += 20;
        
        if (isYearlyPayslip) {
            // Yearly attendance summary
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("Attendance Summary", leftMargin, y);
            y += 25;
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("Attendance:", leftMargin, y);
            g2d.drawString(currentEmployee.getAttendance(), rightCol, y);
            y += 30;
            
            // Draw horizontal line
            g2d.drawLine(leftMargin, y, 550, y);
            y += 20;
        }
        
        // Earnings section
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String earningsTitle = isYearlyPayslip ? "Annual Earnings" : "Earnings";
        g2d.drawString(earningsTitle, leftMargin, y);
        y += 25;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        String hoursLabel = isYearlyPayslip ? "Total Hours Worked:" : "Regular Hours:";
        g2d.drawString(hoursLabel, leftMargin, y);
        g2d.drawString(String.format("%.2f", currentEmployee.getTotalHours()), rightCol, y);
        y += 20;
        
        if (currentEmployee.getOvertime() > 0) {
            String overtimeLabel = isYearlyPayslip ? "Total Overtime Hours:" : "Overtime Hours:";
            g2d.drawString(overtimeLabel, leftMargin, y);
            g2d.drawString(String.format("%.2f", currentEmployee.getOvertime()), rightCol, y);
            y += 20;
        }
        
        String grossLabel = isYearlyPayslip ? "Total Gross Pay:" : "Gross Pay:";
        g2d.drawString(grossLabel, leftMargin, y);
        g2d.drawString(String.format("₱ %.2f", currentEmployee.getGrossPay()), rightCol, y);
        y += 30;
        
        // Draw horizontal line
        g2d.drawLine(leftMargin, y, 550, y);
        y += 20;
        
        // Deductions section
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String deductionsTitle = isYearlyPayslip ? "Annual Deductions" : "Deductions";
        g2d.drawString(deductionsTitle, leftMargin, y);
        y += 25;
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        String sssLabel = isYearlyPayslip ? "Total SSS Deduction:" : "SSS Deduction:";
        g2d.drawString(sssLabel, leftMargin, y);
        g2d.drawString(String.format("₱ %.2f", currentEmployee.getSssDeduction()), rightCol, y);
        y += 20;
        
        String philhealthLabel = isYearlyPayslip ? "Total PhilHealth Deduction:" : "PhilHealth Deduction:";
        g2d.drawString(philhealthLabel, leftMargin, y);
        g2d.drawString(String.format("₱ %.2f", currentEmployee.getPhilhealthDeduction()), rightCol, y);
        y += 20;
        
        String pagibigLabel = isYearlyPayslip ? "Total Pag-IBIG Deduction:" : "Pag-IBIG Deduction:";
        g2d.drawString(pagibigLabel, leftMargin, y);
        g2d.drawString(String.format("₱ %.2f", currentEmployee.getPagibigDeduction()), rightCol, y);
        y += 20;
        
        String taxLabel = isYearlyPayslip ? "Total Tax Deduction:" : "Tax Deduction:";
        g2d.drawString(taxLabel, leftMargin, y);
        g2d.drawString(String.format("₱ %.2f", currentEmployee.getTaxDeduction()), rightCol, y);
        y += 20;
        
        String totalDeductionLabel = isYearlyPayslip ? "Total Annual Deductions:" : "Total Deductions:";
        g2d.drawString(totalDeductionLabel, leftMargin, y);
        g2d.drawString(String.format("₱ %.2f", currentEmployee.getTotalDeduction()), rightCol, y);
        y += 30;
        
        // Draw horizontal line
        g2d.drawLine(leftMargin, y, 550, y);
        y += 20;
        
        // Net pay section
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String netPayLabel = isYearlyPayslip ? "Total Net Pay:" : "Net Pay:";
        g2d.drawString(netPayLabel, leftMargin, y);
        g2d.drawString(String.format("₱ %.2f", currentEmployee.getNetPay()), rightCol, y);
        y += 40;
        
        if (isYearlyPayslip) {
            // Add year-end summary
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("Year-End Summary", leftMargin, y);
            y += 20;
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            double monthlyAverage = currentEmployee.getNetPay() / 12;
            g2d.drawString("Average Monthly Net Pay: ₱" + String.format("%.2f", monthlyAverage), leftMargin, y);
            y += 15;
            
            // Calculate 13th month pay (1/12 of total basic pay)
            double thirteenthMonth = currentEmployee.getGrossPay() / 12;
            g2d.drawString("Estimated 13th Month Pay: ₱" + String.format("%.2f", thirteenthMonth), leftMargin, y);
            y += 25;
        }
        
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
        String footerText = isYearlyPayslip ? 
            "This is a computer-generated yearly payslip summary and does not require a signature." :
            "This is a computer-generated payslip and does not require a signature.";
        g2d.drawString(footerText, leftMargin, y);
        y += 15;
        g2d.drawString("Any questions regarding this payslip should be directed to the HR Department.", leftMargin, y);
        
        if (isYearlyPayslip) {
            y += 15;
            g2d.drawString("All amounts are based on official 2025 Philippine deduction rates.", leftMargin, y);
        }
    }
}