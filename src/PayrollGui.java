import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class PayrollGui extends JFrame {

    JLabel employeeName, employeeId, hourlyRateLabel, maxHoursLabel, hoursWorkedLabel;
    JTextField employeeNameField, employeeIdField, hourlyRateField, maxHoursField, hoursWorkedField;
    
    JToggleButton allowOvertimeToggle;
    
    JButton addEmployee, updateEmployee, deleteEmployee, processPayroll, generateReport;
    JButton calculateOvertime, viewAttendance, generatePayslip, newEmployeeButton;
    
    Container container;
    GridBagLayout layout;
    
    JTable employeeTable;
    EmployeeTableModel tableModel;
    EmployeeDAO employeeDAO;
    
    public PayrollGui() {
        setTitle("Philippine Payroll Management System");
        
        // Initialize DAO with database connection
        employeeDAO = new EmployeeDAO();
        
        // Initialize UI components
        employeeName = new JLabel("Employee Name:");
        employeeId = new JLabel("Employee ID:");
        hourlyRateLabel = new JLabel("Hourly Rate (PHP):");
        maxHoursLabel = new JLabel("Maximum Hours:");
        hoursWorkedLabel = new JLabel("Hours Worked:");
        
        employeeNameField = new JTextField(20);
        employeeIdField = new JTextField(20);
        hourlyRateField = new JTextField(20);
        maxHoursField = new JTextField(20);
        hoursWorkedField = new JTextField(20);
        
        // Default values
        maxHoursField.setText("40"); // Default max hours (standard work week)
        
        // Overtime toggle
        allowOvertimeToggle = new JToggleButton("Allow Overtime");
        allowOvertimeToggle.setSelected(false); // Default: no overtime
        
        // New button for clearing selection
        newEmployeeButton = new JButton("New Employee");
        
        addEmployee = new JButton("Add Employee");
        updateEmployee = new JButton("Update Employee");
        deleteEmployee = new JButton("Delete Employee");
        processPayroll = new JButton("Process Payroll");
        generateReport = new JButton("Generate Reports");
        calculateOvertime = new JButton("Calculate Overtime");
        viewAttendance = new JButton("View Attendance");
        generatePayslip = new JButton("Generate Payslip");
        
        tableModel = new EmployeeTableModel();
        employeeTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(employeeTable);
        
        container = this.getContentPane();
        layout = new GridBagLayout();
        container.setLayout(layout);
        
        // Add components to container with grid layout
        addToContainer(employeeName, 0, 0, 1, 1);
        addToContainer(employeeNameField, 1, 0, 2, 1);
        
        addToContainer(employeeId, 0, 1, 1, 1);
        addToContainer(employeeIdField, 1, 1, 2, 1);
        
        addToContainer(hourlyRateLabel, 0, 2, 1, 1);
        addToContainer(hourlyRateField, 1, 2, 2, 1);
        
        addToContainer(maxHoursLabel, 0, 3, 1, 1);
        addToContainer(maxHoursField, 1, 3, 2, 1);
        
        addToContainer(hoursWorkedLabel, 0, 4, 1, 1);
        addToContainer(hoursWorkedField, 1, 4, 2, 1);
        
        addToContainer(allowOvertimeToggle, 0, 5, 3, 1);
        
        // Add the New Employee button in its own row
        addToContainer(newEmployeeButton, 0, 6, 3, 1);
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        buttonPanel.add(addEmployee);
        buttonPanel.add(updateEmployee);
        buttonPanel.add(deleteEmployee);
        buttonPanel.add(processPayroll);
        buttonPanel.add(generateReport);
        buttonPanel.add(calculateOvertime);
        buttonPanel.add(viewAttendance);
        buttonPanel.add(generatePayslip);
        
        addToContainer(buttonPanel, 0, 7, 3, 1);
        addToContainer(tableScrollPane, 0, 8, 3, 3);
        
        // New button action listener
        newEmployeeButton.addActionListener(e -> {
            // Clear table selection
            employeeTable.clearSelection();
            
            // Clear all input fields
            clearFields();
            
            // Reset defaults
            maxHoursField.setText("40");
            allowOvertimeToggle.setSelected(false);
        });
        
        addEmployee.addActionListener(e -> {
            String name = employeeNameField.getText();
            String id = employeeIdField.getText();
            String hourlyRateText = hourlyRateField.getText();
            String maxHoursText = maxHoursField.getText();
            String hoursWorkedText = hoursWorkedField.getText();
            
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
                    
                    Employee employee = new Employee(id, name, hoursForPayment, overtimeHours, 
                                           wage, LocalDate.now(), 0, "Present");
                    
                    // Save to database and update table model
                    if (employeeDAO.addEmployee(employee)) {
                        tableModel.addEmployee(employee);
                        JOptionPane.showMessageDialog(this, "Employee added successfully!");
                        clearFields();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add employee to database.", 
                                               "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Please enter valid numbers for hourly rate, maximum hours, and hours worked.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
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
                        
                        Employee employee = tableModel.getEmployee(selectedRow);
                        employee.setName(name);
                        employee.setId(id);
                        employee.setTotalHours(hoursForPayment);
                        employee.setOvertime(overtimeHours);
                        employee.setWage(wage);
                        employee.setDate(LocalDate.now());
                        
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
                            "Please enter valid numbers for hourly rate, maximum hours, and hours worked.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee to update.");
            }
        });
        
        deleteEmployee.addActionListener(e -> {
            int selectedRow = employeeTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete this employee?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Employee employee = tableModel.getEmployee(selectedRow);
                    
                    // Delete from database and update table model
                    if (employeeDAO.deleteEmployee(employee.getId())) {
                        tableModel.removeEmployee(selectedRow);
                        JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                        clearFields();
                        employeeTable.clearSelection();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete employee from database.", 
                                               "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
            }
        });
        
        processPayroll.addActionListener(e -> {
            System.out.println("Process Payroll clicked");
            JOptionPane.showMessageDialog(this, "Payroll processing functionality will be implemented in future updates.");
        });
        
        generateReport.addActionListener(e -> {
            System.out.println("Generate Reports clicked");
            JOptionPane.showMessageDialog(this, "Report generation functionality will be implemented in future updates.");
        });
        
        calculateOvertime.addActionListener(e -> {
            System.out.println("Calculate Overtime clicked");
            JOptionPane.showMessageDialog(this, "Overtime calculation functionality will be implemented in future updates.");
        });
        
        viewAttendance.addActionListener(e -> {
            System.out.println("View Attendance clicked");
            JOptionPane.showMessageDialog(this, "Attendance viewing functionality will be implemented in future updates.");
        });
        
        generatePayslip.addActionListener(e -> {
            System.out.println("Generate Payslip clicked");
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
                    
                    // Keep the current max hours value
                    if (maxHoursField.getText().isEmpty()) {
                        maxHoursField.setText("40"); // Default if not set
                    }
                    
                    // Set overtime toggle based on whether there's overtime
                    allowOvertimeToggle.setSelected(overtime > 0);
                }
            }
        });
        
        setSize(800, 700); // Increased height to accommodate new fields and button
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
    
    private void addToContainer(Component component, int gridx, int gridy, int gridwidth, int gridheight) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        if (component instanceof JScrollPane || component instanceof JPanel) {
            gbc.weighty = 1.0;
        }
        if (component instanceof JToggleButton) {
            // Center the toggle button
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
        }
        if (component == newEmployeeButton) {
            // Make the New Employee button stand out
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            ((JButton)component).setBackground(new Color(230, 230, 250)); // Light lavender
            ((JButton)component).setFont(new Font("Arial", Font.BOLD, 12));
        }
        container.add(component, gbc);
    }
    
    private void clearFields() {
        employeeNameField.setText("");
        employeeIdField.setText("");
        hourlyRateField.setText("");
        hoursWorkedField.setText("");
        // Keep the default max hours
    }
    
    private void loadEmployeesFromDatabase() {
        // Clear existing data
        tableModel.clearAll();
        
        // Load employees from database
        List<Employee> employees = employeeDAO.getEmployeesWithPayroll();
        for (Employee employee : employees) {
            tableModel.addEmployee(employee);
        }
        
        System.out.println("Loaded " + employees.size() + " employees from database.");
    }
}