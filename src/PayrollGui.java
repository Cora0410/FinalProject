import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PayrollGui extends JFrame {

    // Employee information fields
    JLabel employeeName, employeeId, hourlyRateLabel, maxHoursLabel, hoursWorkedLabel, dateLabel;
    JTextField employeeNameField, employeeIdField, hourlyRateField, maxHoursField, hoursWorkedField, dateField;
    
    // Deduction fields
    JLabel sssLabel, philhealthLabel, pagibigLabel, taxLabel;
    JTextField sssField, philhealthField, pagibigField, taxField;
    
    // Calculation fields
    JLabel totalDeductionLabel, grossPayLabel, netPayLabel;
    JTextField totalDeductionField, grossPayField, netPayField;
    
    JToggleButton allowOvertimeToggle;
    
    // Buttons
    JButton addEmployee, updateEmployee, deleteEmployee, processPayroll, generateReport;
    JButton calculateOvertime, viewAttendance, generatePayslip, newEmployeeButton, calculateDeductionsButton;
    JButton viewByDateButton;
    
    Container container;
    GridBagLayout layout;
    
    JTable employeeTable;
    EmployeeTableModel tableModel;
    EmployeeDAO employeeDAO;
    
    // Date formatter
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public PayrollGui() {
        setTitle("Philippine Payroll Management System");
        
        // Initialize DAO with database connection
        employeeDAO = new EmployeeDAO();
        
        // Initialize basic UI components
        employeeName = new JLabel("Employee Name:");
        employeeId = new JLabel("Employee ID:");
        hourlyRateLabel = new JLabel("Hourly Rate (PHP):");
        maxHoursLabel = new JLabel("Maximum Hours:");
        hoursWorkedLabel = new JLabel("Hours Worked:");
        dateLabel = new JLabel("Date (YYYY-MM-DD):");
        
        employeeNameField = new JTextField(20);
        employeeIdField = new JTextField(20);
        hourlyRateField = new JTextField(20);
        maxHoursField = new JTextField(20);
        hoursWorkedField = new JTextField(20);
        dateField = new JTextField(20);
        
        // Set today's date as default
        dateField.setText(LocalDate.now().format(dateFormatter));
        
        // Initialize deduction fields
        sssLabel = new JLabel("SSS Deduction:");
        philhealthLabel = new JLabel("PhilHealth Deduction:");
        pagibigLabel = new JLabel("Pag-IBIG Deduction:");
        taxLabel = new JLabel("Tax Deduction:");
        
        sssField = new JTextField(20);
        philhealthField = new JTextField(20);
        pagibigField = new JTextField(20);
        taxField = new JTextField(20);
        
        // Initialize calculation fields
        totalDeductionLabel = new JLabel("Total Deduction:");
        grossPayLabel = new JLabel("Gross Pay:");
        netPayLabel = new JLabel("Net Pay:");
        
        totalDeductionField = new JTextField(20);
        grossPayField = new JTextField(20);
        netPayField = new JTextField(20);
        
        // Make calculation fields read-only
        totalDeductionField.setEditable(false);
        grossPayField.setEditable(false);
        netPayField.setEditable(false);
        
        // Default values
        maxHoursField.setText("40"); // Default max hours (standard work week)
        
        // Overtime toggle
        allowOvertimeToggle = new JToggleButton("Allow Overtime");
        allowOvertimeToggle.setSelected(false); // Default: no overtime
        
        // Buttons
        newEmployeeButton = new JButton("New Employee");
        addEmployee = new JButton("Add Employee");
        updateEmployee = new JButton("Update Employee");
        deleteEmployee = new JButton("Delete Employee");
        processPayroll = new JButton("Process Payroll");
        generateReport = new JButton("Generate Reports");
        calculateOvertime = new JButton("Calculate Overtime");
        viewAttendance = new JButton("View Attendance");
        generatePayslip = new JButton("Generate Payslip");
        calculateDeductionsButton = new JButton("Calculate Pay & Deductions");
        viewByDateButton = new JButton("View Records by Date");
        
        // Table
        tableModel = new EmployeeTableModel();
        employeeTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(employeeTable);
        
        // Layout
        container = this.getContentPane();
        layout = new GridBagLayout();
        container.setLayout(layout);
        
        // Create employee info panel
        JPanel employeeInfoPanel = new JPanel(new GridBagLayout());
        employeeInfoPanel.setBorder(BorderFactory.createTitledBorder("Employee Information"));
        
        // Create deduction panel
        JPanel deductionPanel = new JPanel(new GridBagLayout());
        deductionPanel.setBorder(BorderFactory.createTitledBorder("Deductions"));
        
        // Add components to employee info panel
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
        
        gbcInfo.gridx = 0; gbcInfo.gridy = 6; gbcInfo.gridwidth = 2;
        gbcInfo.fill = GridBagConstraints.NONE; gbcInfo.anchor = GridBagConstraints.CENTER;
        employeeInfoPanel.add(allowOvertimeToggle, gbcInfo);
        
        // Add components to deduction panel
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
        
        // Add total calculation fields to deduction panel
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
        
        // Add panels to main container
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        container.add(employeeInfoPanel, gbc);
        
        gbc.gridx = 1;
        container.add(deductionPanel, gbc);
        
        // Add action buttons
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        actionPanel.add(newEmployeeButton);
        actionPanel.add(viewByDateButton);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(actionPanel, gbc);
        
        // Add button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        buttonPanel.add(addEmployee);
        buttonPanel.add(updateEmployee);
        buttonPanel.add(deleteEmployee);
        buttonPanel.add(processPayroll);
        buttonPanel.add(generateReport);
        buttonPanel.add(calculateOvertime);
        buttonPanel.add(viewAttendance);
        buttonPanel.add(generatePayslip);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(buttonPanel, gbc);
        
        // Add table
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        container.add(tableScrollPane, gbc);
        
        // Calculate Deductions button action
        calculateDeductionsButton.addActionListener(e -> {
            try {
                // Get input values
                double hourlyRate = Double.parseDouble(hourlyRateField.getText());
                double maxHours = Double.parseDouble(maxHoursField.getText());
                double hoursWorked = Double.parseDouble(hoursWorkedField.getText());
                
                // Calculate actual hours and overtime
                double hoursForPayment;
                double overtimeHours = 0;
                
                if (allowOvertimeToggle.isSelected()) {
                    // Overtime allowed - pay for all hours
                    hoursForPayment = hoursWorked;
                    // Calculate overtime hours
                    if (hoursWorked > maxHours) {
                        overtimeHours = hoursWorked - maxHours;
                    }
                } else {
                    // Overtime not allowed - cap at maximum hours
                    hoursForPayment = Math.min(hoursWorked, maxHours);
                }
                
                // Calculate gross pay
                double wage = hourlyRate * hoursForPayment;
                grossPayField.setText(String.format("%.2f", wage));
                
                // Get deduction inputs
                double sss = 0;
                double philhealth = 0;
                double pagibig = 0;
                double tax = 0;
                
                if (!sssField.getText().isEmpty()) {
                    sss = Double.parseDouble(sssField.getText());
                }
                if (!philhealthField.getText().isEmpty()) {
                    philhealth = Double.parseDouble(philhealthField.getText());
                }
                if (!pagibigField.getText().isEmpty()) {
                    pagibig = Double.parseDouble(pagibigField.getText());
                }
                if (!taxField.getText().isEmpty()) {
                    tax = Double.parseDouble(taxField.getText());
                }
                
                // Calculate total deduction
                double totalDeduction = sss + philhealth + pagibig + tax;
                totalDeductionField.setText(String.format("%.2f", totalDeduction));
                
                // Calculate net pay
                double netPay = wage - totalDeduction;
                netPayField.setText(String.format("%.2f", netPay));
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter valid numbers for all fields.", 
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // New button action listener
        newEmployeeButton.addActionListener(e -> {
            // Clear table selection
            employeeTable.clearSelection();
            
            // Clear all input fields
            clearFields();
            
            // Reset defaults
            maxHoursField.setText("40");
            allowOvertimeToggle.setSelected(false);
            dateField.setText(LocalDate.now().format(dateFormatter)); // Reset to today
        });
        
        // View by date button
        viewByDateButton.addActionListener(e -> {
            try {
                String dateStr = dateField.getText();
                LocalDate localDate = LocalDate.parse(dateStr, dateFormatter);
                
                // Load employees for the selected date
                tableModel.clearAll();
                List<Employee> employees = employeeDAO.getEmployeesForDate(localDate);
                for (Employee employee : employees) {
                    tableModel.addEmployee(employee);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Loaded " + employees.size() + " employee records for " + localDate, 
                    "Records Loaded", JOptionPane.INFORMATION_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid date in YYYY-MM-DD format.", 
                    "Invalid Date Format", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        addEmployee.addActionListener(e -> {
            String name = employeeNameField.getText();
            String id = employeeIdField.getText();
            String hourlyRateText = hourlyRateField.getText();
            String maxHoursText = maxHoursField.getText();
            String hoursWorkedText = hoursWorkedField.getText();
            String dateText = dateField.getText();
            
            try {
                LocalDate localDate = LocalDate.parse(dateText, dateFormatter);
                
                if (!name.isEmpty() && !id.isEmpty() && !hourlyRateText.isEmpty() 
                    && !maxHoursText.isEmpty() && !hoursWorkedText.isEmpty()) {
                    try {
                        double rate = Double.parseDouble(hourlyRateField.getText());
                        double maxHours = Double.parseDouble(maxHoursField.getText());
                        double hoursWorked = Double.parseDouble(hoursWorkedField.getText());
                        
                        // Calculate actual hours to pay based on overtime setting
                        double hoursForPayment;
                        double overtimeHours = 0;
                        
                        if (allowOvertimeToggle.isSelected()) {
                            // Overtime allowed - pay for all hours
                            hoursForPayment = hoursWorked;
                            // Calculate overtime hours (hours beyond the maximum)
                            if (hoursWorked > maxHours) {
                                overtimeHours = hoursWorked - maxHours;
                            }
                        } else {
                            // Overtime not allowed - cap at maximum hours
                            hoursForPayment = Math.min(hoursWorked, maxHours);
                        }
                        
                        // Calculate wage: hourly rate × hours for payment
                        double wage = rate * hoursForPayment;
                        
                        // Get deduction values
                        double sss = 0;
                        double philhealth = 0;
                        double pagibig = 0;
                        double tax = 0;
                        
                        if (!sssField.getText().isEmpty()) {
                            sss = Double.parseDouble(sssField.getText());
                        }
                        if (!philhealthField.getText().isEmpty()) {
                            philhealth = Double.parseDouble(philhealthField.getText());
                        }
                        if (!pagibigField.getText().isEmpty()) {
                            pagibig = Double.parseDouble(pagibigField.getText());
                        }
                        if (!taxField.getText().isEmpty()) {
                            tax = Double.parseDouble(taxField.getText());
                        }
                        
                        Employee employee = new Employee(id, name, hoursForPayment, overtimeHours, 
                                               wage, localDate, sss, philhealth, pagibig, tax, "Present");
                        
                        // Save to database and update table model
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
                
                try {
                    LocalDate localDate = LocalDate.parse(dateText, dateFormatter);
                    
                    if (!name.isEmpty() && !id.isEmpty() && !hourlyRateText.isEmpty() 
                        && !maxHoursText.isEmpty() && !hoursWorkedText.isEmpty()) {
                        try {
                            double rate = Double.parseDouble(hourlyRateText);
                            double maxHours = Double.parseDouble(maxHoursText);
                            double hoursWorked = Double.parseDouble(hoursWorkedText);
                            
                            // Calculate actual hours to pay based on overtime setting
                            double hoursForPayment;
                            double overtimeHours = 0;
                            
                            if (allowOvertimeToggle.isSelected()) {
                                // Overtime allowed - pay for all hours
                                hoursForPayment = hoursWorked;
                                // Calculate overtime hours (hours beyond the maximum)
                                if (hoursWorked > maxHours) {
                                    overtimeHours = hoursWorked - maxHours;
                                }
                            } else {
                                // Overtime not allowed - cap at maximum hours
                                hoursForPayment = Math.min(hoursWorked, maxHours);
                            }
                            
                            // Calculate wage: hourly rate × hours for payment
                            double wage = rate * hoursForPayment;
                            
                            // Get deduction values
                            double sss = 0;
                            double philhealth = 0;
                            double pagibig = 0;
                            double tax = 0;
                            
                            if (!sssField.getText().isEmpty()) {
                                sss = Double.parseDouble(sssField.getText());
                            }
                            if (!philhealthField.getText().isEmpty()) {
                                philhealth = Double.parseDouble(philhealthField.getText());
                            }
                            if (!pagibigField.getText().isEmpty()) {
                                pagibig = Double.parseDouble(pagibigField.getText());
                            }
                            if (!taxField.getText().isEmpty()) {
                                tax = Double.parseDouble(taxField.getText());
                            }
                            
                            Employee employee = tableModel.getEmployee(selectedRow);
                            employee.setName(name);
                            employee.setId(id);
                            employee.setTotalHours(hoursForPayment);
                            employee.setOvertime(overtimeHours);
                            employee.setWage(wage);
                            employee.setDate(localDate);
                            employee.setSssDeduction(sss);
                            employee.setPhilhealthDeduction(philhealth);
                            employee.setPagibigDeduction(pagibig);
                            employee.setTaxDeduction(tax);
                            
                            // Update database and table model
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
                
                // Ask if user wants to delete the entire employee or just this date's record
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
                    // Delete all employee records
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
                    // Delete only this date's record
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
        
        processPayroll.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Payroll processing functionality will be implemented in future updates.");
        });
        
        generateReport.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Report generation functionality will be implemented in future updates.");
        });
        
        calculateOvertime.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Overtime calculation functionality will be implemented in future updates.");
        });
        
        viewAttendance.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Attendance viewing functionality will be implemented in future updates.");
        });
        
        generatePayslip.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Payslip generation functionality will be implemented in future updates.");
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
                    
                    // Calculate hourly rate from wage and total hours
                    double hourlyRate = 0;
                    if (totalHours > 0) {
                        hourlyRate = wage / totalHours;
                    }
                    
                    hourlyRateField.setText(String.format("%.2f", hourlyRate));
                    
                    // Set hours worked as the sum of regular hours and overtime
                    hoursWorkedField.setText(String.format("%.2f", totalHours + overtime));
                    
                    // Set the date field
                    if (employee.getDate() != null) {
                        dateField.setText(employee.getDate().format(dateFormatter));
                    } else {
                        dateField.setText(LocalDate.now().format(dateFormatter));
                    }
                    
                    // Keep the current max hours value
                    if (maxHoursField.getText().isEmpty()) {
                        maxHoursField.setText("40"); // Default if not set
                    }
                    
                    // Set overtime toggle based on whether there's overtime
                    allowOvertimeToggle.setSelected(overtime > 0);
                    
                    // Populate deduction fields
                    sssField.setText(String.format("%.2f", employee.getSssDeduction()));
                    philhealthField.setText(String.format("%.2f", employee.getPhilhealthDeduction()));
                    pagibigField.setText(String.format("%.2f", employee.getPagibigDeduction()));
                    taxField.setText(String.format("%.2f", employee.getTaxDeduction()));
                    
                    // Update calculated fields
                    totalDeductionField.setText(String.format("%.2f", employee.getTotalDeduction()));
                    grossPayField.setText(String.format("%.2f", employee.getGrossPay()));
                    netPayField.setText(String.format("%.2f", employee.getNetPay()));
                }
            }
        });
        
        setSize(950, 800); // Increased size to accommodate new fields
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Load employees from database on startup
        loadEmployeesFromDatabase();
        
        setVisible(true);
        
        employeeTable.setRowSelectionAllowed(true);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Test database connection
        if (DatabaseConfig.getInstance().testConnection()) {
            setTitle("Philippine Payroll Management System - Connected to Database");
        } else {
            setTitle("Philippine Payroll Management System - Database Connection Failed");
        }
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
        // Keep date as is
    }
    
    private void loadEmployeesFromDatabase() {
        // Clear existing data
        tableModel.clearAll();
        
        // Load employees from database - most recent records
        List<Employee> employees = employeeDAO.getEmployeesWithPayroll();
        for (Employee employee : employees) {
            tableModel.addEmployee(employee);
        }
        
        System.out.println("Loaded " + employees.size() + " employees from database.");
    }
}