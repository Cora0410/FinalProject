import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportGenerator extends JFrame {
    private EmployeeDAO employeeDAO;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    private JComboBox<String> reportTypeComboBox;
    private JComboBox<String> monthComboBox;
    private JComboBox<String> yearComboBox;
    private JButton generateButton, exportButton, closeButton;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JPanel reportPanel;
    private JScrollPane scrollPane;
    
    public ReportGenerator(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
        
        setTitle("Generate Reports");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize components
        initComponents();
        
        // Layout components
        layoutComponents();
        
        // Add listeners
        addEventListeners();
    }
    
    private void initComponents() {
        // Report types
        String[] reportTypes = {
            "Payroll Summary Report",
            "SSS Contribution Report",
            "PhilHealth Contribution Report",
            "Pag-IBIG Contribution Report",
            "Tax Withholding Report",
            "Employee Attendance Report",
            "Total Deductions Report",
            "Department-wise Salary Report",
            "13th Month Pay Calculation"
        };
        
        reportTypeComboBox = new JComboBox<>(reportTypes);
        
        // Month selection
        String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        monthComboBox = new JComboBox<>(months);
        
        // Set current month as default
        int currentMonth = LocalDate.now().getMonthValue() - 1; // 0-based index
        monthComboBox.setSelectedIndex(currentMonth);
        
        // Year selection
        String[] years = new String[5];
        int currentYear = LocalDate.now().getYear();
        for (int i = 0; i < 5; i++) {
            years[i] = String.valueOf(currentYear - i);
        }
        yearComboBox = new JComboBox<>(years);
        
        // Buttons
        generateButton = new JButton("Generate Report");
        exportButton = new JButton("Export to CSV");
        closeButton = new JButton("Close");
        
        exportButton.setEnabled(false);
        
        // Table setup with empty model
        tableModel = new DefaultTableModel();
        reportTable = new JTable(tableModel);
        scrollPane = new JScrollPane(reportTable);
        
        // Report panel (will be populated when a report is generated)
        reportPanel = new JPanel(new BorderLayout());
        reportPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Control panel at the top
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Report Options"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // First row
        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Report Type:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 3;
        controlPanel.add(reportTypeComboBox, gbc);
        
        // Second row
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0; gbc.gridwidth = 1;
        controlPanel.add(new JLabel("Month:"), gbc);
        
        gbc.gridx = 1;
        controlPanel.add(monthComboBox, gbc);
        
        gbc.gridx = 2;
        controlPanel.add(new JLabel("Year:"), gbc);
        
        gbc.gridx = 3;
        controlPanel.add(yearComboBox, gbc);
        
        // Third row - Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(generateButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        controlPanel.add(buttonPanel, gbc);
        
        // Add panels to frame
        add(controlPanel, BorderLayout.NORTH);
        add(reportPanel, BorderLayout.CENTER);
    }
    
    private void addEventListeners() {
        generateButton.addActionListener(e -> generateReport());
        
        exportButton.addActionListener(e -> exportReportToCSV());
        
        closeButton.addActionListener(e -> dispose());
    }
    
    private void generateReport() {
        String reportType = (String) reportTypeComboBox.getSelectedItem();
        int month = monthComboBox.getSelectedIndex() + 1;
        int year = Integer.parseInt((String) yearComboBox.getSelectedItem());
        
        // Clear previous table data
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        
        // Get the start and end dates for the selected month
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        // Get employees for the selected month
        List<Employee> employees = employeeDAO.getEmployeesForDateRange(startDate, endDate);
        
        if (employees.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No employee records found for " + monthYearFormatter.format(startDate),
                "No Data", JOptionPane.INFORMATION_MESSAGE);
            exportButton.setEnabled(false);
            return;
        }
        
        // Generate the selected report
        switch (reportType) {
            case "Payroll Summary Report":
                generatePayrollSummaryReport(employees, startDate);
                break;
            case "SSS Contribution Report":
                generateSSSReport(employees, startDate);
                break;
            case "PhilHealth Contribution Report":
                generatePhilHealthReport(employees, startDate);
                break;
            case "Pag-IBIG Contribution Report":
                generatePagIBIGReport(employees, startDate);
                break;
            case "Tax Withholding Report":
                generateTaxReport(employees, startDate);
                break;
            case "Employee Attendance Report":
                generateAttendanceReport(employees, startDate);
                break;
            case "Total Deductions Report":
                generateDeductionsReport(employees, startDate);
                break;
            case "Department-wise Salary Report":
                generateDepartmentReport(employees, startDate);
                break;
            case "13th Month Pay Calculation":
                generate13thMonthReport(employees, year);
                break;
        }
        
        exportButton.setEnabled(true);
    }
    
    private void generatePayrollSummaryReport(List<Employee> employees, LocalDate reportDate) {
        // Define columns for payroll summary report
        String[] columns = {
            "Employee ID", "Name", "Total Hours", "Overtime Hours", 
            "Wage Rate", "Gross Pay", "Total Deductions", "Net Pay"
        };
        
        // Set up the table model with columns
        setupTableModel(columns);
        
        // Fill data
        double totalGrossPay = 0;
        double totalDeductions = 0;
        double totalNetPay = 0;
        
        for (Employee employee : employees) {
            // Skip if employee is not for the specific month
            if (employee.getDate() == null || 
                !YearMonth.from(employee.getDate()).equals(YearMonth.from(reportDate))) {
                continue;
            }
            
            Object[] rowData = {
                employee.getId(),
                employee.getName(),
                employee.getTotalHours(),
                employee.getOvertime(),
                String.format("%.2f", employee.getWage() / employee.getTotalHours()),
                String.format("%.2f", employee.getGrossPay()),
                String.format("%.2f", employee.getTotalDeduction()),
                String.format("%.2f", employee.getNetPay())
            };
            
            tableModel.addRow(rowData);
            
            totalGrossPay += employee.getGrossPay();
            totalDeductions += employee.getTotalDeduction();
            totalNetPay += employee.getNetPay();
        }
        
        // Add a summary row
        tableModel.addRow(new Object[] {
            "TOTAL", "", "", "", "",
            String.format("%.2f", totalGrossPay),
            String.format("%.2f", totalDeductions),
            String.format("%.2f", totalNetPay)
        });
        
        // Set the report title
        setTitle("Payroll Summary Report - " + monthYearFormatter.format(reportDate));
    }
    
    private void generateSSSReport(List<Employee> employees, LocalDate reportDate) {
        // Define columns for SSS report
        String[] columns = {
            "Employee ID", "Name", "Monthly Salary", "Employee Contribution (4.5%)",
            "Employer Contribution (9.5%)", "Total Contribution (14%)"
        };
        
        // Set up the table model with columns
        setupTableModel(columns);
        
        // Fill data
        double totalEmployeeContribution = 0;
        double totalEmployerContribution = 0;
        double totalContribution = 0;
        
        for (Employee employee : employees) {
            // Skip if employee is not for the specific month
            if (employee.getDate() == null || 
                !YearMonth.from(employee.getDate()).equals(YearMonth.from(reportDate))) {
                continue;
            }
            
            double monthlySalary = employee.getWage();
            double employeeContribution = employee.getSssDeduction();
            double employerContribution = calculateEmployerSSS(monthlySalary);
            double totalSSSContribution = employeeContribution + employerContribution;
            
            Object[] rowData = {
                employee.getId(),
                employee.getName(),
                String.format("%.2f", monthlySalary),
                String.format("%.2f", employeeContribution),
                String.format("%.2f", employerContribution),
                String.format("%.2f", totalSSSContribution)
            };
            
            tableModel.addRow(rowData);
            
            totalEmployeeContribution += employeeContribution;
            totalEmployerContribution += employerContribution;
            totalContribution += totalSSSContribution;
        }
        
        // Add a summary row
        tableModel.addRow(new Object[] {
            "TOTAL", "", "",
            String.format("%.2f", totalEmployeeContribution),
            String.format("%.2f", totalEmployerContribution),
            String.format("%.2f", totalContribution)
        });
        
        // Set the report title
        setTitle("SSS Contribution Report - " + monthYearFormatter.format(reportDate));
    }
    
    private void generatePhilHealthReport(List<Employee> employees, LocalDate reportDate) {
        // Define columns for PhilHealth report
        String[] columns = {
            "Employee ID", "Name", "Monthly Salary", "Employee Contribution (2%)",
            "Employer Contribution (2%)", "Total Contribution (4%)"
        };
        
        // Set up the table model with columns
        setupTableModel(columns);
        
        // Fill data
        double totalEmployeeContribution = 0;
        double totalEmployerContribution = 0;
        double totalContribution = 0;
        
        for (Employee employee : employees) {
            // Skip if employee is not for the specific month
            if (employee.getDate() == null || 
                !YearMonth.from(employee.getDate()).equals(YearMonth.from(reportDate))) {
                continue;
            }
            
            double monthlySalary = employee.getWage();
            double employeeContribution = employee.getPhilhealthDeduction();
            double employerContribution = employeeContribution; // Usually equal for PhilHealth
            double totalPhilHealthContribution = employeeContribution + employerContribution;
            
            Object[] rowData = {
                employee.getId(),
                employee.getName(),
                String.format("%.2f", monthlySalary),
                String.format("%.2f", employeeContribution),
                String.format("%.2f", employerContribution),
                String.format("%.2f", totalPhilHealthContribution)
            };
            
            tableModel.addRow(rowData);
            
            totalEmployeeContribution += employeeContribution;
            totalEmployerContribution += employerContribution;
            totalContribution += totalPhilHealthContribution;
        }
        
        // Add a summary row
        tableModel.addRow(new Object[] {
            "TOTAL", "", "",
            String.format("%.2f", totalEmployeeContribution),
            String.format("%.2f", totalEmployerContribution),
            String.format("%.2f", totalContribution)
        });
        
        // Set the report title
        setTitle("PhilHealth Contribution Report - " + monthYearFormatter.format(reportDate));
    }
    
    private void generatePagIBIGReport(List<Employee> employees, LocalDate reportDate) {
        // Define columns for Pag-IBIG report
        String[] columns = {
            "Employee ID", "Name", "Monthly Salary", "Employee Contribution (2%)",
            "Employer Contribution (2%)", "Total Contribution (4%)"
        };
        
        // Set up the table model with columns
        setupTableModel(columns);
        
        // Fill data
        double totalEmployeeContribution = 0;
        double totalEmployerContribution = 0;
        double totalContribution = 0;
        
        for (Employee employee : employees) {
            // Skip if employee is not for the specific month
            if (employee.getDate() == null || 
                !YearMonth.from(employee.getDate()).equals(YearMonth.from(reportDate))) {
                continue;
            }
            
            double monthlySalary = employee.getWage();
            double employeeContribution = employee.getPagibigDeduction();
            double employerContribution = employeeContribution; // Usually equal for Pag-IBIG
            double totalPagIBIGContribution = employeeContribution + employerContribution;
            
            Object[] rowData = {
                employee.getId(),
                employee.getName(),
                String.format("%.2f", monthlySalary),
                String.format("%.2f", employeeContribution),
                String.format("%.2f", employerContribution),
                String.format("%.2f", totalPagIBIGContribution)
            };
            
            tableModel.addRow(rowData);
            
            totalEmployeeContribution += employeeContribution;
            totalEmployerContribution += employerContribution;
            totalContribution += totalPagIBIGContribution;
        }
        
        // Add a summary row
        tableModel.addRow(new Object[] {
            "TOTAL", "", "",
            String.format("%.2f", totalEmployeeContribution),
            String.format("%.2f", totalEmployerContribution),
            String.format("%.2f", totalContribution)
        });
        
        // Set the report title
        setTitle("Pag-IBIG Contribution Report - " + monthYearFormatter.format(reportDate));
    }
    
    private void generateTaxReport(List<Employee> employees, LocalDate reportDate) {
        // Define columns for Tax report
        String[] columns = {
            "Employee ID", "Name", "Monthly Salary", "Tax Deduction", 
            "Taxable Income", "Tax Rate (%)"
        };
        
        // Set up the table model with columns
        setupTableModel(columns);
        
        // Fill data
        double totalTaxDeduction = 0;
        
        for (Employee employee : employees) {
            // Skip if employee is not for the specific month
            if (employee.getDate() == null || 
                !YearMonth.from(employee.getDate()).equals(YearMonth.from(reportDate))) {
                continue;
            }
            
            double monthlySalary = employee.getWage();
            double taxDeduction = employee.getTaxDeduction();
            double taxableIncome = monthlySalary - 
                (employee.getSssDeduction() + employee.getPhilhealthDeduction() + employee.getPagibigDeduction());
            double taxRate = (taxDeduction / taxableIncome) * 100;
            
            Object[] rowData = {
                employee.getId(),
                employee.getName(),
                String.format("%.2f", monthlySalary),
                String.format("%.2f", taxDeduction),
                String.format("%.2f", taxableIncome),
                String.format("%.2f", taxRate)
            };
            
            tableModel.addRow(rowData);
            
            totalTaxDeduction += taxDeduction;
        }
        
        // Add a summary row
        tableModel.addRow(new Object[] {
            "TOTAL", "", "", String.format("%.2f", totalTaxDeduction), "", ""
        });
        
        // Set the report title
        setTitle("Tax Withholding Report - " + monthYearFormatter.format(reportDate));
    }
    
    private void generateAttendanceReport(List<Employee> employees, LocalDate reportDate) {
        // Define columns for Attendance report
        String[] columns = {
            "Employee ID", "Name", "Date", "Total Hours", "Overtime Hours", "Status"
        };
        
        // Set up the table model with columns
        setupTableModel(columns);
        
        // Fill data
        for (Employee employee : employees) {
            // Skip if employee is not for the specific month
            if (employee.getDate() == null || 
                !YearMonth.from(employee.getDate()).equals(YearMonth.from(reportDate))) {
                continue;
            }
            
            Object[] rowData = {
                employee.getId(),
                employee.getName(),
                employee.getDate().format(dateFormatter),
                employee.getTotalHours(),
                employee.getOvertime(),
                employee.getAttendance()
            };
            
            tableModel.addRow(rowData);
        }
        
        // Set the report title
        setTitle("Employee Attendance Report - " + monthYearFormatter.format(reportDate));
    }
    
    private void generateDeductionsReport(List<Employee> employees, LocalDate reportDate) {
        // Define columns for Deductions report
        String[] columns = {
            "Employee ID", "Name", "SSS", "PhilHealth", "Pag-IBIG", "Tax",
            "Total Deductions", "Gross Pay", "Net Pay"
        };
        
        // Set up the table model with columns
        setupTableModel(columns);
        
        // Fill data
        double totalSSS = 0;
        double totalPhilHealth = 0;
        double totalPagIBIG = 0;
        double totalTax = 0;
        double totalDeductions = 0;
        double totalGrossPay = 0;
        double totalNetPay = 0;
        
        for (Employee employee : employees) {
            // Skip if employee is not for the specific month
            if (employee.getDate() == null || 
                !YearMonth.from(employee.getDate()).equals(YearMonth.from(reportDate))) {
                continue;
            }
            
            Object[] rowData = {
                employee.getId(),
                employee.getName(),
                String.format("%.2f", employee.getSssDeduction()),
                String.format("%.2f", employee.getPhilhealthDeduction()),
                String.format("%.2f", employee.getPagibigDeduction()),
                String.format("%.2f", employee.getTaxDeduction()),
                String.format("%.2f", employee.getTotalDeduction()),
                String.format("%.2f", employee.getGrossPay()),
                String.format("%.2f", employee.getNetPay())
            };
            
            tableModel.addRow(rowData);
            
            totalSSS += employee.getSssDeduction();
            totalPhilHealth += employee.getPhilhealthDeduction();
            totalPagIBIG += employee.getPagibigDeduction();
            totalTax += employee.getTaxDeduction();
            totalDeductions += employee.getTotalDeduction();
            totalGrossPay += employee.getGrossPay();
            totalNetPay += employee.getNetPay();
        }
        
        // Add a summary row
        tableModel.addRow(new Object[] {
            "TOTAL", "",
            String.format("%.2f", totalSSS),
            String.format("%.2f", totalPhilHealth),
            String.format("%.2f", totalPagIBIG),
            String.format("%.2f", totalTax),
            String.format("%.2f", totalDeductions),
            String.format("%.2f", totalGrossPay),
            String.format("%.2f", totalNetPay)
        });
        
        // Set the report title
        setTitle("Total Deductions Report - " + monthYearFormatter.format(reportDate));
    }
    
    private void generateDepartmentReport(List<Employee> employees, LocalDate reportDate) {
        // For this demo, we'll simulate departments by grouping employees by the first letter of their ID
        // In a real application, you would have a department field in the Employee class
        
        // Define columns
        String[] columns = {
            "Department", "Number of Employees", "Total Hours", "Total Overtime",
            "Total Gross Pay", "Total Deductions", "Total Net Pay"
        };
        
        // Set up the table model with columns
        setupTableModel(columns);
        
        // Group employees by "department" (first letter of ID in this demo)
        Map<String, List<Employee>> departmentMap = new HashMap<>();
        
        for (Employee employee : employees) {
            // Skip if employee is not for the specific month
            if (employee.getDate() == null || 
                !YearMonth.from(employee.getDate()).equals(YearMonth.from(reportDate))) {
                continue;
            }
            
            String deptKey = "Dept " + employee.getId().substring(0, 1).toUpperCase();
            
            if (!departmentMap.containsKey(deptKey)) {
                departmentMap.put(deptKey, new ArrayList<>());
            }
            
            departmentMap.get(deptKey).add(employee);
        }
        
        // Fill data
        double grandTotalHours = 0;
        double grandTotalOvertime = 0;
        double grandTotalGrossPay = 0;
        double grandTotalDeductions = 0;
        double grandTotalNetPay = 0;
        int grandTotalEmployees = 0;
        
        for (Map.Entry<String, List<Employee>> entry : departmentMap.entrySet()) {
            String department = entry.getKey();
            List<Employee> deptEmployees = entry.getValue();
            
            double totalHours = 0;
            double totalOvertime = 0;
            double totalGrossPay = 0;
            double totalDeductions = 0;
            double totalNetPay = 0;
            
            for (Employee employee : deptEmployees) {
                totalHours += employee.getTotalHours();
                totalOvertime += employee.getOvertime();
                totalGrossPay += employee.getGrossPay();
                totalDeductions += employee.getTotalDeduction();
                totalNetPay += employee.getNetPay();
            }
            
            Object[] rowData = {
                department,
                deptEmployees.size(),
                String.format("%.2f", totalHours),
                String.format("%.2f", totalOvertime),
                String.format("%.2f", totalGrossPay),
                String.format("%.2f", totalDeductions),
                String.format("%.2f", totalNetPay)
            };
            
            tableModel.addRow(rowData);
            
            grandTotalHours += totalHours;
            grandTotalOvertime += totalOvertime;
            grandTotalGrossPay += totalGrossPay;
            grandTotalDeductions += totalDeductions;
            grandTotalNetPay += totalNetPay;
            grandTotalEmployees += deptEmployees.size();
        }
        
        // Add a summary row
        tableModel.addRow(new Object[] {
            "ALL DEPARTMENTS",
            grandTotalEmployees,
            String.format("%.2f", grandTotalHours),
            String.format("%.2f", grandTotalOvertime),
            String.format("%.2f", grandTotalGrossPay),
            String.format("%.2f", grandTotalDeductions),
            String.format("%.2f", grandTotalNetPay)
        });
        
        // Set the report title
        setTitle("Department-wise Salary Report - " + monthYearFormatter.format(reportDate));
    }
    
    private void generate13thMonthReport(List<Employee> employees, int year) {
        // Define columns for 13th Month Pay report
        String[] columns = {
            "Employee ID", "Name", "Date Hired", "Total Months", "Total Basic Pay",
            "13th Month Pay"
        };
        
        // Set up the table model with columns
        setupTableModel(columns);
        
        // Group employees by ID to calculate year-long data
        Map<String, Map<String, Object>> employeeYearData = new HashMap<>();
        
        for (Employee employee : employees) {
            if (employee.getDate() == null || employee.getDate().getYear() != year) {
                continue;
            }
            
            String id = employee.getId();
            
            if (!employeeYearData.containsKey(id)) {
                employeeYearData.put(id, new HashMap<>());
                employeeYearData.get(id).put("name", employee.getName());
                employeeYearData.get(id).put("dateHired", employee.getDate());
                employeeYearData.get(id).put("totalMonths", 1);
                employeeYearData.get(id).put("totalBasicPay", employee.getWage());
            } else {
                int months = (int) employeeYearData.get(id).get("totalMonths");
                double basicPay = (double) employeeYearData.get(id).get("totalBasicPay");
                
                employeeYearData.get(id).put("totalMonths", months + 1);
                employeeYearData.get(id).put("totalBasicPay", basicPay + employee.getWage());
                
                // Update date hired if this date is earlier
                LocalDate currentDateHired = (LocalDate) employeeYearData.get(id).get("dateHired");
                if (employee.getDate().isBefore(currentDateHired)) {
                    employeeYearData.get(id).put("dateHired", employee.getDate());
                }
            }
        }
        
        // Fill data
        double grandTotal13thMonth = 0;
        
        for (Map.Entry<String, Map<String, Object>> entry : employeeYearData.entrySet()) {
            String id = entry.getKey();
            Map<String, Object> data = entry.getValue();
            
            String name = (String) data.get("name");
            LocalDate dateHired = (LocalDate) data.get("dateHired");
            int totalMonths = (int) data.get("totalMonths");
            double totalBasicPay = (double) data.get("totalBasicPay");
            
            // 13th month pay is total basic pay divided by 12
            double thirteenthMonthPay = totalBasicPay / 12.0;
            
            Object[] rowData = {
                id,
                name,
                dateHired.format(dateFormatter),
                totalMonths,
                String.format("%.2f", totalBasicPay),
                String.format("%.2f", thirteenthMonthPay)
            };
            
            tableModel.addRow(rowData);
            
            grandTotal13thMonth += thirteenthMonthPay;
        }
        
        // Add a summary row
        tableModel.addRow(new Object[] {
            "TOTAL", "", "", "", "",
            String.format("%.2f", grandTotal13thMonth)
        });
        
        // Set the report title
        setTitle("13th Month Pay Calculation - " + year);
    }
    
    private void setupTableModel(String[] columns) {
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        
        reportTable.setModel(tableModel);
        
        // Set preferred column widths
        reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        reportTable.getTableHeader().setReorderingAllowed(false);
    }
    
    private void exportReportToCSV() {
        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report As CSV");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        // Get the report title to suggest a file name
        String reportTitle = getTitle().replace(" - ", "_").replace(" ", "_");
        fileChooser.setSelectedFile(new File(reportTitle + ".csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Append .csv extension if not present
            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }
            
            try (FileWriter csvWriter = new FileWriter(fileToSave)) {
                // Write report title
                csvWriter.append(getTitle());
                csvWriter.append("\n\n");
                
                // Write column headers
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    csvWriter.append(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) {
                        csvWriter.append(",");
                    }
                }
                csvWriter.append("\n");
                
                // Write row data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Object value = tableModel.getValueAt(i, j);
                        csvWriter.append(value != null ? value.toString() : "");
                        if (j < tableModel.getColumnCount() - 1) {
                            csvWriter.append(",");
                        }
                    }
                    csvWriter.append("\n");
                }
                
                JOptionPane.showMessageDialog(this,
                    "Report exported successfully to:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    // Helper method to calculate employer SSS contribution
    private double calculateEmployerSSS(double monthlySalary) {
        // Employer SSS is usually 9.5% of monthly salary
        // In a real system, this would use the official SSS contribution table
        double rate = 0.095; // 9.5%
        return monthlySalary * rate;
    }
}