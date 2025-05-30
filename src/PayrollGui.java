import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PayrollGui extends JFrame {
    JLabel employeeName, employeeId, hourlyRateLabel, maxHoursLabel, hoursWorkedLabel, dateLabel, attendanceLabel;
    JTextField employeeNameField, employeeIdField, hourlyRateField, maxHoursField, hoursWorkedField, dateField;
    JComboBox<String> attendanceComboBox;
    
    JLabel sssLabel, philhealthLabel, pagibigLabel, taxLabel;
    JTextField sssField, philhealthField, pagibigField, taxField;
    
    JLabel totalDeductionLabel, grossPayLabel, netPayLabel;
    JTextField totalDeductionField, grossPayField, netPayField;
    
    JToggleButton allowOvertimeToggle;
    
    JButton addEmployee, updateEmployee, deleteEmployee, generateReport;
    JButton viewAttendanceByDate, viewAllAttendance, generatePayslip, newEmployeeButton, calculateDeductionsButton;
    JButton viewAttendanceByEmployee;
    
    // Filter and search components
    JTextField filterDateField;
    JLabel filterDateLabel;
    JButton clearFilterButton;
    
    // NEW: Search components
    JTextField searchEmployeeIdField;
    JLabel searchEmployeeIdLabel;
    JButton searchButton, clearSearchButton;
    
    Container container;
    GridBagLayout layout;
    
    JTable employeeTable;
    EmployeeTableModel tableModel;
    EmployeeDAO employeeDAO;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Attendance options
    private final String[] attendanceOptions = {
        "Present", "Sick Leave", "Vacation Leave", 
        "Maternity Leave", "Paternity Leave", "Absent"
    };
    
    // Leave types that are paid
    private final String[] paidLeaveTypes = {
        "Sick Leave", "Vacation Leave", "Maternity Leave", "Paternity Leave"
    };
    
    // Maximum leaves allowed per year
    private final int MAX_LEAVES_PER_YEAR = 6;
    
    public PayrollGui() {
        setTitle("Philippine Payroll Management System");
        
        employeeDAO = new EmployeeDAO();
        
        employeeName = new JLabel("Employee Name:");
        employeeId = new JLabel("Employee ID:");
        hourlyRateLabel = new JLabel("Hourly Rate (PHP):");
        maxHoursLabel = new JLabel("Maximum Hours:");
        hoursWorkedLabel = new JLabel("Hours Worked:");
        dateLabel = new JLabel("Date (YYYY-MM-DD):");
        attendanceLabel = new JLabel("Attendance:");
        
        employeeNameField = new JTextField(20);
        employeeIdField = new JTextField(20);
        hourlyRateField = new JTextField(20);
        maxHoursField = new JTextField(20);
        hoursWorkedField = new JTextField(20);
        dateField = new JTextField(20);
        attendanceComboBox = new JComboBox<>(attendanceOptions);
        
        dateField.setText(LocalDate.now().format(dateFormatter));
        
        sssLabel = new JLabel("SSS Deduction (5%):");
        philhealthLabel = new JLabel("PhilHealth Deduction (2.5%):");
        pagibigLabel = new JLabel("Pag-IBIG Deduction (2%):");
        taxLabel = new JLabel("Tax Deduction:");
        
        sssField = new JTextField(20);
        philhealthField = new JTextField(20);
        pagibigField = new JTextField(20);
        taxField = new JTextField(20);
        
        // Make deduction fields non-editable since they're calculated
        sssField.setEditable(false);
        philhealthField.setEditable(false);
        pagibigField.setEditable(false);
        taxField.setEditable(false);
        
        totalDeductionLabel = new JLabel("Total Deduction:");
        grossPayLabel = new JLabel("Gross Pay:");
        netPayLabel = new JLabel("Net Pay:");
        
        totalDeductionField = new JTextField(20);
        grossPayField = new JTextField(20);
        netPayField = new JTextField(20);
        
        totalDeductionField.setEditable(false);
        grossPayField.setEditable(false);
        netPayField.setEditable(false);
        
        maxHoursField.setText("40");
        
        allowOvertimeToggle = new JToggleButton("Allow Overtime");
        allowOvertimeToggle.setSelected(false);
        
        newEmployeeButton = new JButton("New Employee");
        addEmployee = new JButton("Add Employee");
        updateEmployee = new JButton("Update Employee");
        deleteEmployee = new JButton("Delete Employee");
        generateReport = new JButton("Generate Reports");
        viewAttendanceByDate = new JButton("View Attendance by Date");
        viewAllAttendance = new JButton("View All Attendance");
        viewAttendanceByEmployee = new JButton("View Attendance by Employee");
        generatePayslip = new JButton("Generate Payslip");
        calculateDeductionsButton = new JButton("Calculate Pay & Deductions");
        
        // Initialize filter components
        filterDateLabel = new JLabel("Filter Date:");
        filterDateField = new JTextField(10);
        filterDateField.setText(LocalDate.now().format(dateFormatter));
        clearFilterButton = new JButton("Clear Filter");
        
        // NEW: Initialize search components
        searchEmployeeIdLabel = new JLabel("Search Employee ID:");
        searchEmployeeIdField = new JTextField(10);
        searchButton = new JButton("Search");
        clearSearchButton = new JButton("Clear Search");
        
        tableModel = new EmployeeTableModel();
        employeeTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(employeeTable);
        
        container = this.getContentPane();
        layout = new GridBagLayout();
        container.setLayout(layout);
        
        JPanel employeeInfoPanel = new JPanel(new GridBagLayout());
        employeeInfoPanel.setBorder(BorderFactory.createTitledBorder("Employee Information"));
        
        JPanel deductionPanel = new JPanel(new GridBagLayout());
        deductionPanel.setBorder(BorderFactory.createTitledBorder("Deductions (Auto-calculated per 2025 Rates)"));
        
        GridBagConstraints gbcInfo = new GridBagConstraints();
        gbcInfo.insets = new Insets(3, 3, 3, 3);
        gbcInfo.anchor = GridBagConstraints.WEST;
        gbcInfo.fill = GridBagConstraints.HORIZONTAL;
        
        gbcInfo.gridx = 0; gbcInfo.gridy = 0;
        employeeInfoPanel.add(employeeName, gbcInfo);
        
        gbcInfo.gridx = 1; gbcInfo.weightx = 1.0;
        employeeInfoPanel.add(employeeNameField, gbcInfo);
        
        gbcInfo.gridx = 0; gbcInfo.gridy = 1; gbcInfo.weightx = 0.0;
        employeeInfoPanel.add(employeeId, gbcInfo);
        
        gbcInfo.gridx = 1; gbcInfo.weightx = 1.0;
        employeeInfoPanel.add(employeeIdField, gbcInfo);
        
        gbcInfo.gridx = 0; gbcInfo.gridy = 2; gbcInfo.weightx = 0.0;
        employeeInfoPanel.add(hourlyRateLabel, gbcInfo);
        
        gbcInfo.gridx = 1; gbcInfo.weightx = 1.0;
        employeeInfoPanel.add(hourlyRateField, gbcInfo);
        
        gbcInfo.gridx = 0; gbcInfo.gridy = 3; gbcInfo.weightx = 0.0;
        employeeInfoPanel.add(maxHoursLabel, gbcInfo);
        
        gbcInfo.gridx = 1; gbcInfo.weightx = 1.0;
        employeeInfoPanel.add(maxHoursField, gbcInfo);
        
        gbcInfo.gridx = 0; gbcInfo.gridy = 4; gbcInfo.weightx = 0.0;
        employeeInfoPanel.add(hoursWorkedLabel, gbcInfo);
        
        gbcInfo.gridx = 1; gbcInfo.weightx = 1.0;
        employeeInfoPanel.add(hoursWorkedField, gbcInfo);
        
        gbcInfo.gridx = 0; gbcInfo.gridy = 5; gbcInfo.weightx = 0.0;
        employeeInfoPanel.add(dateLabel, gbcInfo);
        
        gbcInfo.gridx = 1; gbcInfo.weightx = 1.0;
        employeeInfoPanel.add(dateField, gbcInfo);
        
        gbcInfo.gridx = 0; gbcInfo.gridy = 6; gbcInfo.weightx = 0.0;
        employeeInfoPanel.add(attendanceLabel, gbcInfo);
        
        gbcInfo.gridx = 1; gbcInfo.weightx = 1.0;
        employeeInfoPanel.add(attendanceComboBox, gbcInfo);
        
        gbcInfo.gridx = 0; gbcInfo.gridy = 7; gbcInfo.gridwidth = 2;
        gbcInfo.fill = GridBagConstraints.NONE; gbcInfo.anchor = GridBagConstraints.CENTER;
        employeeInfoPanel.add(allowOvertimeToggle, gbcInfo);
        
        GridBagConstraints gbcDeduct = new GridBagConstraints();
        gbcDeduct.insets = new Insets(3, 3, 3, 3);
        gbcDeduct.anchor = GridBagConstraints.WEST;
        gbcDeduct.fill = GridBagConstraints.HORIZONTAL;
        
        gbcDeduct.gridx = 0; gbcDeduct.gridy = 0;
        deductionPanel.add(sssLabel, gbcDeduct);
        
        gbcDeduct.gridx = 1; gbcDeduct.weightx = 1.0;
        deductionPanel.add(sssField, gbcDeduct);
        
        gbcDeduct.gridx = 0; gbcDeduct.gridy = 1; gbcDeduct.weightx = 0.0;
        deductionPanel.add(philhealthLabel, gbcDeduct);
        
        gbcDeduct.gridx = 1; gbcDeduct.weightx = 1.0;
        deductionPanel.add(philhealthField, gbcDeduct);
        
        gbcDeduct.gridx = 0; gbcDeduct.gridy = 2; gbcDeduct.weightx = 0.0;
        deductionPanel.add(pagibigLabel, gbcDeduct);
        
        gbcDeduct.gridx = 1; gbcDeduct.weightx = 1.0;
        deductionPanel.add(pagibigField, gbcDeduct);
        
        gbcDeduct.gridx = 0; gbcDeduct.gridy = 3; gbcDeduct.weightx = 0.0;
        deductionPanel.add(taxLabel, gbcDeduct);
        
        gbcDeduct.gridx = 1; gbcDeduct.weightx = 1.0;
        deductionPanel.add(taxField, gbcDeduct);
        
        gbcDeduct.gridx = 0; gbcDeduct.gridy = 4; gbcDeduct.weightx = 0.0;
        deductionPanel.add(totalDeductionLabel, gbcDeduct);
        
        gbcDeduct.gridx = 1; gbcDeduct.weightx = 1.0;
        deductionPanel.add(totalDeductionField, gbcDeduct);
        
        gbcDeduct.gridx = 0; gbcDeduct.gridy = 5; gbcDeduct.weightx = 0.0;
        deductionPanel.add(grossPayLabel, gbcDeduct);
        
        gbcDeduct.gridx = 1; gbcDeduct.weightx = 1.0;
        deductionPanel.add(grossPayField, gbcDeduct);
        
        gbcDeduct.gridx = 0; gbcDeduct.gridy = 6; gbcDeduct.weightx = 0.0;
        deductionPanel.add(netPayLabel, gbcDeduct);
        
        gbcDeduct.gridx = 1; gbcDeduct.weightx = 1.0;
        deductionPanel.add(netPayField, gbcDeduct);
        
        gbcDeduct.gridx = 0; gbcDeduct.gridy = 7; gbcDeduct.gridwidth = 2;
        gbcDeduct.fill = GridBagConstraints.NONE; gbcDeduct.anchor = GridBagConstraints.CENTER;
        deductionPanel.add(calculateDeductionsButton, gbcDeduct);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        container.add(employeeInfoPanel, gbc);
        
        gbc.gridx = 1;
        container.add(deductionPanel, gbc);
        
        JPanel actionPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        actionPanel.add(newEmployeeButton);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(actionPanel, gbc);
        
        // Create filter and search panel with both date filter and employee ID search
        JPanel filterSearchPanel = new JPanel(new GridBagLayout());
        filterSearchPanel.setBorder(BorderFactory.createTitledBorder("Filter & Search"));
        
        GridBagConstraints gbcFilter = new GridBagConstraints();
        gbcFilter.insets = new Insets(3, 3, 3, 3);
        gbcFilter.anchor = GridBagConstraints.WEST;
        
        // First row - Date filter
        gbcFilter.gridx = 0; gbcFilter.gridy = 0;
        filterSearchPanel.add(filterDateLabel, gbcFilter);
        
        gbcFilter.gridx = 1;
        filterSearchPanel.add(filterDateField, gbcFilter);
        
        gbcFilter.gridx = 2;
        filterSearchPanel.add(viewAttendanceByDate, gbcFilter);
        
        gbcFilter.gridx = 3;
        filterSearchPanel.add(clearFilterButton, gbcFilter);
        
        // Second row - Employee ID search
        gbcFilter.gridx = 0; gbcFilter.gridy = 1;
        filterSearchPanel.add(searchEmployeeIdLabel, gbcFilter);
        
        gbcFilter.gridx = 1;
        filterSearchPanel.add(searchEmployeeIdField, gbcFilter);
        
        gbcFilter.gridx = 2;
        filterSearchPanel.add(searchButton, gbcFilter);
        
        gbcFilter.gridx = 3;
        filterSearchPanel.add(clearSearchButton, gbcFilter);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(filterSearchPanel, gbc);
        
        // Button panel (3x3 grid)
        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        buttonPanel.add(addEmployee);
        buttonPanel.add(updateEmployee);
        buttonPanel.add(deleteEmployee);
        buttonPanel.add(generateReport);
        buttonPanel.add(viewAllAttendance);
        buttonPanel.add(viewAttendanceByEmployee);
        buttonPanel.add(generatePayslip);
        buttonPanel.add(new JLabel("")); // Empty cell
        buttonPanel.add(new JLabel("")); // Empty cell
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(buttonPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        container.add(tableScrollPane, gbc);
        
        // Add listener for attendance combo box to enable/disable overtime
        attendanceComboBox.addActionListener(e -> {
            String selectedAttendance = (String) attendanceComboBox.getSelectedItem();
            boolean isLeave = isLeaveType(selectedAttendance);
            allowOvertimeToggle.setEnabled(!isLeave);
            if (isLeave) {
                allowOvertimeToggle.setSelected(false);
            }
        });
        
        calculateDeductionsButton.addActionListener(e -> {
            try {
                String selectedAttendance = (String) attendanceComboBox.getSelectedItem();
                double hourlyRate = Double.parseDouble(hourlyRateField.getText());
                double maxHours = Double.parseDouble(maxHoursField.getText());
                double hoursWorked = Double.parseDouble(hoursWorkedField.getText());
                
                double hoursForPayment;
                double overtimeHours = 0;
                double wage;
                
                if ("Absent".equals(selectedAttendance)) {
                    // Absent = not paid, all values are 0
                    hoursForPayment = 0;
                    overtimeHours = 0;
                    wage = 0;
                } else if (isLeaveType(selectedAttendance)) {
                    // All leaves are paid to maximum hours only, no overtime
                    hoursForPayment = maxHours;
                    overtimeHours = 0;
                    wage = hourlyRate * hoursForPayment;
                } else {
                    // Present - normal calculation
                    if (allowOvertimeToggle.isSelected()) {
                        hoursForPayment = hoursWorked;
                        if (hoursWorked > maxHours) {
                            overtimeHours = hoursWorked - maxHours;
                        }
                    } else {
                        hoursForPayment = Math.min(hoursWorked, maxHours);
                    }
                    wage = hourlyRate * hoursForPayment;
                }
                
                grossPayField.setText(String.format("%.2f", wage));
                
                // Calculate percentage-based deductions using 2025 rates
                Employee.DeductionCalculation calc = Employee.calculateDeductions(wage);
                
                sssField.setText(String.format("%.2f", calc.sssDeduction));
                philhealthField.setText(String.format("%.2f", calc.philhealthDeduction));
                pagibigField.setText(String.format("%.2f", calc.pagibigDeduction));
                taxField.setText(String.format("%.2f", calc.taxDeduction));
                
                double totalDeduction = calc.getTotalDeduction();
                totalDeductionField.setText(String.format("%.2f", totalDeduction));
                
                double netPay = wage - totalDeduction;
                netPayField.setText(String.format("%.2f", netPay));
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter valid numbers for all fields.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        newEmployeeButton.addActionListener(e -> {
            employeeTable.clearSelection();
            clearFields();
            maxHoursField.setText("40");
            allowOvertimeToggle.setSelected(false);
            attendanceComboBox.setSelectedIndex(0); // Present
            dateField.setText(LocalDate.now().format(dateFormatter));
        });
        
        // Action listener for the View Attendance by Date button (modified to filter)
        viewAttendanceByDate.addActionListener(e -> {
            try {
                String dateStr = filterDateField.getText();
                LocalDate localDate = LocalDate.parse(dateStr, dateFormatter);
                
                // Filter the table model instead of clearing and reloading
                tableModel.filterByDate(localDate);
                
                int filteredCount = tableModel.getRowCount();
                JOptionPane.showMessageDialog(this, 
                    "Filtered to show " + filteredCount + " employee records for " + localDate, 
                    "Filtered Attendance Records", JOptionPane.INFORMATION_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid date in YYYY-MM-DD format.", 
                    "Invalid Date Format", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Action listener for the Clear Filter button
        clearFilterButton.addActionListener(e -> {
            tableModel.clearFilter();
            JOptionPane.showMessageDialog(this, 
                "Filter cleared, showing all employee records.", 
                "Filter Cleared", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // NEW: Action listener for the Search button
        searchButton.addActionListener(e -> {
            String searchId = searchEmployeeIdField.getText().trim();
            if (searchId.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter an Employee ID to search.", 
                    "Empty Search", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Filter the table model by employee ID
            tableModel.filterByEmployeeId(searchId);
            
            int filteredCount = tableModel.getRowCount();
            if (filteredCount == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No records found for Employee ID: " + searchId, 
                    "No Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Found " + filteredCount + " records for Employee ID: " + searchId, 
                    "Search Results", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        // NEW: Action listener for the Clear Search button
        clearSearchButton.addActionListener(e -> {
            searchEmployeeIdField.setText("");
            tableModel.clearFilter();
            JOptionPane.showMessageDialog(this, 
                "Search cleared, showing all employee records.", 
                "Search Cleared", JOptionPane.INFORMATION_MESSAGE);
        });
        
        viewAllAttendance.addActionListener(e -> {
            tableModel.clearFilter(); // Clear any existing filter
            searchEmployeeIdField.setText(""); // Clear search field
            loadEmployeesFromDatabase();
            JOptionPane.showMessageDialog(this, 
                "Showing all employee records", 
                "All Records", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Action listener for View Attendance by Employee button
        viewAttendanceByEmployee.addActionListener(e -> {
            showEmployeeAttendanceDialog();
        });
        
        addEmployee.addActionListener(e -> {
            String name = employeeNameField.getText();
            String id = employeeIdField.getText();
            String hourlyRateText = hourlyRateField.getText();
            String maxHoursText = maxHoursField.getText();
            String hoursWorkedText = hoursWorkedField.getText();
            String dateText = dateField.getText();
            String selectedAttendance = (String) attendanceComboBox.getSelectedItem();
            
            try {
                LocalDate localDate = LocalDate.parse(dateText, dateFormatter);
                
                // Check if employee with same ID already exists for this date
                Employee existingEmployee = employeeDAO.getEmployeeForDate(id, localDate);
                if (existingEmployee != null) {
                    JOptionPane.showMessageDialog(this, 
                        "An employee with ID '" + id + "' already exists for date " + localDate, 
                        "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Check leave limit if it's a leave type
                if (isLeaveType(selectedAttendance)) {
                    int currentLeaveCount = employeeDAO.getLeaveCountForYear(id, localDate.getYear());
                    if (currentLeaveCount >= MAX_LEAVES_PER_YEAR) {
                        JOptionPane.showMessageDialog(this, 
                            "Employee has reached maximum leave limit of " + MAX_LEAVES_PER_YEAR + " for this year.\n" +
                            "Current leave count: " + currentLeaveCount, 
                            "Leave Limit Exceeded", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                
                if (!name.isEmpty() && !id.isEmpty() && !hourlyRateText.isEmpty() 
                    && !maxHoursText.isEmpty() && !hoursWorkedText.isEmpty()) {
                    try {
                        double rate = Double.parseDouble(hourlyRateField.getText());
                        double maxHours = Double.parseDouble(maxHoursField.getText());
                        double hoursWorked = Double.parseDouble(hoursWorkedField.getText());
                        
                        double hoursForPayment;
                        double overtimeHours = 0;
                        double wage;
                        
                        if ("Absent".equals(selectedAttendance)) {
                            // Absent = not paid
                            hoursForPayment = 0;
                            overtimeHours = 0;
                            wage = 0;
                        } else if (isLeaveType(selectedAttendance)) {
                            // All leaves are paid to maximum hours only
                            hoursForPayment = maxHours;
                            overtimeHours = 0;
                            wage = rate * hoursForPayment;
                        } else {
                            // Present - normal calculation
                            if (allowOvertimeToggle.isSelected()) {
                                hoursForPayment = hoursWorked;
                                if (hoursWorked > maxHours) {
                                    overtimeHours = hoursWorked - maxHours;
                                }
                            } else {
                                hoursForPayment = Math.min(hoursWorked, maxHours);
                            }
                            wage = rate * hoursForPayment;
                        }
                        
                        // Calculate percentage-based deductions using 2025 rates
                        Employee.DeductionCalculation calc = Employee.calculateDeductions(wage);
                        
                        Employee employee = new Employee(id, name, hoursForPayment, overtimeHours, 
                                               wage, localDate, calc.sssDeduction, calc.philhealthDeduction, 
                                               calc.pagibigDeduction, calc.taxDeduction, selectedAttendance);
                        
                        if (employeeDAO.addEmployee(employee)) {
                            tableModel.addEmployee(employee);
                            JOptionPane.showMessageDialog(this, "Employee record added successfully!");
                            clearFields();
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to add employee to database.", 
                                                   "Database Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, 
                            "Please enter valid numbers for all fields.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid date in YYYY-MM-DD format.", 
                    "Invalid Date Format", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        updateEmployee.addActionListener(e -> {
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow >= 0) {
                String name = employeeNameField.getText();
                String id = employeeIdField.getText();
                String hourlyRateText = hourlyRateField.getText();
                String maxHoursText = maxHoursField.getText();
                String hoursWorkedText = hoursWorkedField.getText();
                String dateText = dateField.getText();
                String selectedAttendance = (String) attendanceComboBox.getSelectedItem();
                
                try {
                    LocalDate localDate = LocalDate.parse(dateText, dateFormatter);
                    Employee currentEmployee = tableModel.getEmployee(selectedRow);
                    
                    // Check if changing to a different ID/date that already exists
                    if (!id.equals(currentEmployee.getId()) || !localDate.equals(currentEmployee.getDate())) {
                        Employee existingEmployee = employeeDAO.getEmployeeForDate(id, localDate);
                        if (existingEmployee != null) {
                            JOptionPane.showMessageDialog(this, 
                                "An employee with ID '" + id + "' already exists for date " + localDate, 
                                "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                    
                    // Check leave limit if changing to a leave type and it's a different employee or different leave type
                    if (isLeaveType(selectedAttendance) && 
                        (!selectedAttendance.equals(currentEmployee.getAttendance()) || !id.equals(currentEmployee.getId()))) {
                        int currentLeaveCount = employeeDAO.getLeaveCountForYear(id, localDate.getYear());
                        if (currentLeaveCount >= MAX_LEAVES_PER_YEAR) {
                            JOptionPane.showMessageDialog(this, 
                                "Employee has reached maximum leave limit of " + MAX_LEAVES_PER_YEAR + " for this year.\n" +
                                "Current leave count: " + currentLeaveCount, 
                                "Leave Limit Exceeded", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                    
                    if (!name.isEmpty() && !id.isEmpty() && !hourlyRateText.isEmpty() 
                        && !maxHoursText.isEmpty() && !hoursWorkedText.isEmpty()) {
                        try {
                            double rate = Double.parseDouble(hourlyRateText);
                            double maxHours = Double.parseDouble(maxHoursText);
                            double hoursWorked = Double.parseDouble(hoursWorkedText);
                            
                            double hoursForPayment;
                            double overtimeHours = 0;
                            double wage;
                            
                            if ("Absent".equals(selectedAttendance)) {
                                // Absent = not paid
                                hoursForPayment = 0;
                                overtimeHours = 0;
                                wage = 0;
                            } else if (isLeaveType(selectedAttendance)) {
                                // All leaves are paid to maximum hours only
                                hoursForPayment = maxHours;
                                overtimeHours = 0;
                                wage = rate * hoursForPayment;
                            } else {
                                // Present - normal calculation
                                if (allowOvertimeToggle.isSelected()) {
                                    hoursForPayment = hoursWorked;
                                    if (hoursWorked > maxHours) {
                                        overtimeHours = hoursWorked - maxHours;
                                    }
                                } else {
                                    hoursForPayment = Math.min(hoursWorked, maxHours);
                                }
                                wage = rate * hoursForPayment;
                            }
                            
                            // Calculate percentage-based deductions using 2025 rates
                            Employee.DeductionCalculation calc = Employee.calculateDeductions(wage);
                            
                            Employee employee = tableModel.getEmployee(selectedRow);
                            employee.setName(name);
                            employee.setId(id);
                            employee.setTotalHours(hoursForPayment);
                            employee.setOvertime(overtimeHours);
                            employee.setWage(wage);
                            employee.setDate(localDate);
                            employee.setSssDeduction(calc.sssDeduction);
                            employee.setPhilhealthDeduction(calc.philhealthDeduction);
                            employee.setPagibigDeduction(calc.pagibigDeduction);
                            employee.setTaxDeduction(calc.taxDeduction);
                            employee.setAttendance(selectedAttendance);
                            
                            if (employeeDAO.updateEmployee(employee)) {
                                tableModel.updateEmployee(selectedRow, employee);
                                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
                                clearFields();
                                employeeTable.clearSelection();
                            } else {
                                JOptionPane.showMessageDialog(this, "Failed to update employee in database.", 
                                                       "Database Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(this, 
                                "Please enter valid numbers for all fields.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Please fill in all required fields.");
                    }
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter a valid date in YYYY-MM-DD format.", 
                        "Invalid Date Format", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee to update.");
            }
        });
        
        deleteEmployee.addActionListener(e -> {
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow >= 0) {
                Employee employee = tableModel.getEmployee(selectedRow);
                
                String[] options = {"Delete All Records", "Delete Only This Date", "Cancel"};
                int choice = JOptionPane.showOptionDialog(this,
                    "Do you want to delete all records for this employee or only this date's record?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);
                
                if (choice == 0) {
                    if (employeeDAO.deleteEmployee(employee.getId())) {
                        tableModel.removeEmployee(selectedRow);
                        JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                        clearFields();
                        employeeTable.clearSelection();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete employee from database.", 
                                               "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (choice == 1) {
                    if (employeeDAO.deleteEmployeeRecord(employee.getId(), employee.getDate())) {
                        tableModel.removeEmployee(selectedRow);
                        JOptionPane.showMessageDialog(this, "Employee record for this date deleted successfully!");
                        clearFields();
                        employeeTable.clearSelection();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete employee record from database.", 
                                               "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
            }
        });
        
        generateReport.addActionListener(e -> {
            ReportGenerator reportGenerator = new ReportGenerator(employeeDAO);
            reportGenerator.setVisible(true);
        });
        
        generatePayslip.addActionListener(e -> {
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow >= 0) {
                Employee employee = tableModel.getEmployee(selectedRow);
                PayslipGenerator payslipGenerator = new PayslipGenerator(employee, this);
                payslipGenerator.generateAndShowPayslip();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select an employee to generate a payslip.", 
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    Employee employee = tableModel.getEmployee(selectedRow);
                    employeeNameField.setText(employee.getName());
                    employeeIdField.setText(employee.getId());
                    
                    double totalHours = employee.getTotalHours();
                    double overtime = employee.getOvertime();
                    double wage = employee.getWage();
                    
                    double hourlyRate = 0;
                    if (totalHours > 0) {
                        hourlyRate = wage / totalHours;
                    }
                    
                    hourlyRateField.setText(String.format("%.2f", hourlyRate));
                    hoursWorkedField.setText(String.format("%.2f", totalHours + overtime));
                    
                    if (employee.getDate() != null) {
                        dateField.setText(employee.getDate().format(dateFormatter));
                    } else {
                        dateField.setText(LocalDate.now().format(dateFormatter));
                    }
                    
                    if (maxHoursField.getText().isEmpty()) {
                        maxHoursField.setText("40");
                    }
                    
                    // Set attendance combo box
                    attendanceComboBox.setSelectedItem(employee.getAttendance());
                    
                    allowOvertimeToggle.setSelected(overtime > 0);
                    
                    sssField.setText(String.format("%.2f", employee.getSssDeduction()));
                    philhealthField.setText(String.format("%.2f", employee.getPhilhealthDeduction()));
                    pagibigField.setText(String.format("%.2f", employee.getPagibigDeduction()));
                    taxField.setText(String.format("%.2f", employee.getTaxDeduction()));
                    
                    totalDeductionField.setText(String.format("%.2f", employee.getTotalDeduction()));
                    grossPayField.setText(String.format("%.2f", employee.getGrossPay()));
                    netPayField.setText(String.format("%.2f", employee.getNetPay()));
                }
            }
        });
        
        setSize(950, 850); // Increased height to accommodate new filter panel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        loadEmployeesFromDatabase();
        
        setVisible(true);
        
        employeeTable.setRowSelectionAllowed(true);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        if (DatabaseConfig.getInstance().testConnection()) {
            setTitle("Philippine Payroll Management System - Connected to Database (2025 Rates)");
        } else {
            setTitle("Philippine Payroll Management System - Database Connection Failed");
        }
    }
    
    // Show Employee Attendance Dialog method (same as before)
    private void showEmployeeAttendanceDialog() {
        // Get all unique employees
        List<Employee> allEmployees = employeeDAO.getAllEmployees();
        if (allEmployees.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No employees found in the database.", 
                "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create employee selection dialog
        String[] employeeOptions = allEmployees.stream()
                .map(emp -> emp.getId() + " - " + emp.getName())
                .toArray(String[]::new);
        
        String selectedEmployee = (String) JOptionPane.showInputDialog(
            this,
            "Select an employee to view attendance records:",
            "Select Employee",
            JOptionPane.QUESTION_MESSAGE,
            null,
            employeeOptions,
            employeeOptions[0]
        );
        
        if (selectedEmployee == null) {
            return; // User cancelled
        }
        
        // Extract employee ID from selection
        String employeeId = selectedEmployee.split(" - ")[0];
        String employeeName = selectedEmployee.split(" - ")[1];
        
        // Get all records for this employee
        List<Employee> employeeRecords = employeeDAO.getAllRecordsForEmployee(employeeId);
        
        if (employeeRecords.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No attendance records found for " + employeeName, 
                "No Records", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create and show attendance window
        showEmployeeAttendanceWindow(employeeId, employeeName, employeeRecords);
    }
    
    // Show Employee Attendance Window method (same as before)
    private void showEmployeeAttendanceWindow(String employeeId, String employeeName, List<Employee> records) {
        JFrame attendanceFrame = new JFrame("Attendance Records - " + employeeName + " (" + employeeId + ")");
        attendanceFrame.setSize(1000, 600);
        attendanceFrame.setLocationRelativeTo(this);
        
        // Create table model for attendance records
        String[] columnNames = {
            "Date", "Attendance", "Hours Worked", "Overtime", "Gross Pay", 
            "SSS", "PhilHealth", "Pag-IBIG", "Tax", "Net Pay"
        };
        
        Object[][] data = new Object[records.size()][columnNames.length];
        
        double totalHours = 0;
        double totalOvertime = 0;
        double totalGrossPay = 0;
        double totalNetPay = 0;
        int presentDays = 0;
        int leaveDays = 0;
        int absentDays = 0;
        
        for (int i = 0; i < records.size(); i++) {
            Employee emp = records.get(i);
            data[i][0] = emp.getDate().format(dateFormatter);
            data[i][1] = emp.getAttendance();
            data[i][2] = String.format("%.2f", emp.getTotalHours());
            data[i][3] = String.format("%.2f", emp.getOvertime());
            data[i][4] = String.format("₱%.2f", emp.getGrossPay());
            data[i][5] = String.format("₱%.2f", emp.getSssDeduction());
            data[i][6] = String.format("₱%.2f", emp.getPhilhealthDeduction());
            data[i][7] = String.format("₱%.2f", emp.getPagibigDeduction());
            data[i][8] = String.format("₱%.2f", emp.getTaxDeduction());
            data[i][9] = String.format("₱%.2f", emp.getNetPay());
            
            // Calculate totals
            totalHours += emp.getTotalHours();
            totalOvertime += emp.getOvertime();
            totalGrossPay += emp.getGrossPay();
            totalNetPay += emp.getNetPay();
            
            // Count attendance types
            String attendance = emp.getAttendance();
            if ("Present".equals(attendance)) {
                presentDays++;
            } else if ("Absent".equals(attendance)) {
                absentDays++;
            } else {
                leaveDays++;
            }
        }
        
        JTable attendanceTable = new JTable(data, columnNames);
        attendanceTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        attendanceTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        attendanceTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Date
        attendanceTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Attendance
        attendanceTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Hours
        attendanceTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Overtime
        attendanceTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Gross Pay
        attendanceTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // SSS
        attendanceTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // PhilHealth
        attendanceTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Pag-IBIG
        attendanceTable.getColumnModel().getColumn(8).setPreferredWidth(80);  // Tax
        attendanceTable.getColumnModel().getColumn(9).setPreferredWidth(100); // Net Pay
        
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        
        // Create summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(2, 5, 10, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        
        summaryPanel.add(new JLabel("Total Records: " + records.size()));
        summaryPanel.add(new JLabel("Present Days: " + presentDays));
        summaryPanel.add(new JLabel("Leave Days: " + leaveDays));
        summaryPanel.add(new JLabel("Absent Days: " + absentDays));
        summaryPanel.add(new JLabel(""));
        
        summaryPanel.add(new JLabel("Total Hours: " + String.format("%.2f", totalHours)));
        summaryPanel.add(new JLabel("Total Overtime: " + String.format("%.2f", totalOvertime)));
        summaryPanel.add(new JLabel("Total Gross Pay: ₱" + String.format("%.2f", totalGrossPay)));
        summaryPanel.add(new JLabel("Total Net Pay: ₱" + String.format("%.2f", totalNetPay)));
        summaryPanel.add(new JLabel(""));
        
        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton exportButton = new JButton("Export to CSV");
        JButton closeButton = new JButton("Close");
        
        exportButton.addActionListener(e -> exportEmployeeAttendance(employeeId, employeeName, records));
        closeButton.addActionListener(e -> attendanceFrame.dispose());
        
        buttonPanel.add(exportButton);
        buttonPanel.add(closeButton);
        
        // Layout the frame
        attendanceFrame.setLayout(new BorderLayout());
        attendanceFrame.add(summaryPanel, BorderLayout.NORTH);
        attendanceFrame.add(scrollPane, BorderLayout.CENTER);
        attendanceFrame.add(buttonPanel, BorderLayout.SOUTH);
        
        attendanceFrame.setVisible(true);
    }
    
    // Export Employee Attendance to CSV method (same as before)
    private void exportEmployeeAttendance(String employeeId, String employeeName, List<Employee> records) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Employee Attendance Report");
        fileChooser.setSelectedFile(new java.io.File(employeeId + "_" + employeeName.replaceAll("\\s+", "_") + "_Attendance.csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".csv")) {
                fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".csv");
            }
            
            try (java.io.FileWriter csvWriter = new java.io.FileWriter(fileToSave)) {
                // Write header
                csvWriter.append("Employee Attendance Report\n");
                csvWriter.append("Employee ID: " + employeeId + "\n");
                csvWriter.append("Employee Name: " + employeeName + "\n");
                csvWriter.append("Report Generated: " + LocalDate.now().format(dateFormatter) + "\n\n");
                
                // Write column headers
                csvWriter.append("Date,Attendance,Hours Worked,Overtime,Gross Pay,SSS,PhilHealth,Pag-IBIG,Tax,Net Pay\n");
                
                // Write data rows
                for (Employee emp : records) {
                    csvWriter.append(emp.getDate().format(dateFormatter)).append(",");
                    csvWriter.append(emp.getAttendance()).append(",");
                    csvWriter.append(String.format("%.2f", emp.getTotalHours())).append(",");
                    csvWriter.append(String.format("%.2f", emp.getOvertime())).append(",");
                    csvWriter.append(String.format("%.2f", emp.getGrossPay())).append(",");
                    csvWriter.append(String.format("%.2f", emp.getSssDeduction())).append(",");
                    csvWriter.append(String.format("%.2f", emp.getPhilhealthDeduction())).append(",");
                    csvWriter.append(String.format("%.2f", emp.getPagibigDeduction())).append(",");
                    csvWriter.append(String.format("%.2f", emp.getTaxDeduction())).append(",");
                    csvWriter.append(String.format("%.2f", emp.getNetPay())).append("\n");
                }
                
                JOptionPane.showMessageDialog(this,
                    "Employee attendance report exported successfully to:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean isLeaveType(String attendanceType) {
        for (String leave : paidLeaveTypes) {
            if (leave.equals(attendanceType)) {
                return true;
            }
        }
        return false;
    }
    
    private void clearFields() {
        employeeNameField.setText("");
        employeeIdField.setText("");
        hourlyRateField.setText("");
        hoursWorkedField.setText("");
        sssField.setText("");
        philhealthField.setText("");
        pagibigField.setText("");
        taxField.setText("");
        totalDeductionField.setText("");
        grossPayField.setText("");
        netPayField.setText("");
        attendanceComboBox.setSelectedIndex(0); // Present
    }
    
    private void loadEmployeesFromDatabase() {
        tableModel.clearAll();
        
        List<Employee> employees = employeeDAO.getEmployeesWithPayroll();
        for (Employee employee : employees) {
            tableModel.addEmployee(employee);
        }
        
        System.out.println("Loaded " + employees.size() + " employees from database.");
    }
}