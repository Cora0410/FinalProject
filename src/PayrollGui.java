import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PayrollGui extends JFrame {
    JLabel employeeName, employeeId, hourlyRateLabel, maxHoursLabel, hoursWorkedLabel, dateLabel;
    JTextField employeeNameField, employeeIdField, hourlyRateField, maxHoursField, hoursWorkedField, dateField;
    
    JLabel sssLabel, philhealthLabel, pagibigLabel, taxLabel;
    JTextField sssField, philhealthField, pagibigField, taxField;
    
    JLabel totalDeductionLabel, grossPayLabel, netPayLabel;
    JTextField totalDeductionField, grossPayField, netPayField;
    
    JToggleButton allowOvertimeToggle;
    
    JButton addEmployee, updateEmployee, deleteEmployee, generateReport;
    JButton viewAttendanceByDate, viewAllAttendance, generatePayslip, newEmployeeButton, calculateDeductionsButton;
    
    // New components for filtering
    JTextField filterDateField;
    JLabel filterDateLabel;
    JButton clearFilterButton;
    
    Container container;
    GridBagLayout layout;
    
    JTable employeeTable;
    EmployeeTableModel tableModel;
    EmployeeDAO employeeDAO;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public PayrollGui() {
        setTitle("Philippine Payroll Management System");
        
        employeeDAO = new EmployeeDAO();
        
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
        
        dateField.setText(LocalDate.now().format(dateFormatter));
        
        sssLabel = new JLabel("SSS Deduction:");
        philhealthLabel = new JLabel("PhilHealth Deduction:");
        pagibigLabel = new JLabel("Pag-IBIG Deduction:");
        taxLabel = new JLabel("Tax Deduction:");
        
        sssField = new JTextField(20);
        philhealthField = new JTextField(20);
        pagibigField = new JTextField(20);
        taxField = new JTextField(20);
        
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
        generatePayslip = new JButton("Generate Payslip");
        calculateDeductionsButton = new JButton("Calculate Pay & Deductions");
        
        // Initialize new filter components
        filterDateLabel = new JLabel("Filter Date:");
        filterDateField = new JTextField(10);
        filterDateField.setText(LocalDate.now().format(dateFormatter));
        clearFilterButton = new JButton("Clear Filter");
        
        tableModel = new EmployeeTableModel();
        employeeTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(employeeTable);
        
        container = this.getContentPane();
        layout = new GridBagLayout();
        container.setLayout(layout);
        
        JPanel employeeInfoPanel = new JPanel(new GridBagLayout());
        employeeInfoPanel.setBorder(BorderFactory.createTitledBorder("Employee Information"));
        
        JPanel deductionPanel = new JPanel(new GridBagLayout());
        deductionPanel.setBorder(BorderFactory.createTitledBorder("Deductions"));
        
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
        
        // Create a separate panel for filter controls
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filterPanel.add(filterDateLabel);
        filterPanel.add(filterDateField);
        filterPanel.add(viewAttendanceByDate);
        filterPanel.add(clearFilterButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(filterPanel, gbc);
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        buttonPanel.add(addEmployee);
        buttonPanel.add(updateEmployee);
        buttonPanel.add(deleteEmployee);
        buttonPanel.add(generateReport);
        buttonPanel.add(viewAllAttendance);
        buttonPanel.add(generatePayslip);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        container.add(buttonPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        container.add(tableScrollPane, gbc);
        
        calculateDeductionsButton.addActionListener(e -> {
            try {
                double hourlyRate = Double.parseDouble(hourlyRateField.getText());
                double maxHours = Double.parseDouble(maxHoursField.getText());
                double hoursWorked = Double.parseDouble(hoursWorkedField.getText());
                
                double hoursForPayment;
                double overtimeHours = 0;
                
                if (allowOvertimeToggle.isSelected()) {
                    hoursForPayment = hoursWorked;
                    if (hoursWorked > maxHours) {
                        overtimeHours = hoursWorked - maxHours;
                    }
                } else {
                    hoursForPayment = Math.min(hoursWorked, maxHours);
                }
                
                double wage = hourlyRate * hoursForPayment;
                grossPayField.setText(String.format("%.2f", wage));
                
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
                
                double totalDeduction = sss + philhealth + pagibig + tax;
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
        
        viewAllAttendance.addActionListener(e -> {
            tableModel.clearFilter(); // Clear any existing filter
            loadEmployeesFromDatabase();
            JOptionPane.showMessageDialog(this, 
                "Showing all employee records", 
                "All Records", JOptionPane.INFORMATION_MESSAGE);
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
                
                // Check if employee with same ID already exists for this date
                Employee existingEmployee = employeeDAO.getEmployeeForDate(id, localDate);
                if (existingEmployee != null) {
                    JOptionPane.showMessageDialog(this, 
                        "An employee with ID '" + id + "' already exists for date " + localDate, 
                        "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (!name.isEmpty() && !id.isEmpty() && !hourlyRateText.isEmpty() 
                    && !maxHoursText.isEmpty() && !hoursWorkedText.isEmpty()) {
                    try {
                        double rate = Double.parseDouble(hourlyRateField.getText());
                        double maxHours = Double.parseDouble(maxHoursField.getText());
                        double hoursWorked = Double.parseDouble(hoursWorkedField.getText());
                        
                        double hoursForPayment;
                        double overtimeHours = 0;
                        
                        if (allowOvertimeToggle.isSelected()) {
                            hoursForPayment = hoursWorked;
                            if (hoursWorked > maxHours) {
                                overtimeHours = hoursWorked - maxHours;
                            }
                        } else {
                            hoursForPayment = Math.min(hoursWorked, maxHours);
                        }
                        
                        double wage = rate * hoursForPayment;
                        
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
                    
                    if (!name.isEmpty() && !id.isEmpty() && !hourlyRateText.isEmpty() 
                        && !maxHoursText.isEmpty() && !hoursWorkedText.isEmpty()) {
                        try {
                            double rate = Double.parseDouble(hourlyRateText);
                            double maxHours = Double.parseDouble(maxHoursText);
                            double hoursWorked = Double.parseDouble(hoursWorkedText);
                            
                            double hoursForPayment;
                            double overtimeHours = 0;
                            
                            if (allowOvertimeToggle.isSelected()) {
                                hoursForPayment = hoursWorked;
                                if (hoursWorked > maxHours) {
                                    overtimeHours = hoursWorked - maxHours;
                                }
                            } else {
                                hoursForPayment = Math.min(hoursWorked, maxHours);
                            }
                            
                            double wage = rate * hoursForPayment;
                            
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
        
        setSize(950, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        loadEmployeesFromDatabase();
        
        setVisible(true);
        
        employeeTable.setRowSelectionAllowed(true);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
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