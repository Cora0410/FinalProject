import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class PayrollGui extends JFrame {

    JLabel employeeName, employeeId, hourlyRate, hoursWorked;
    JTextField employeeNameField, employeeIdField, hourlyRateField, hoursWorkedField;
    
    JButton addEmployee, updateEmployee, deleteEmployee, processPayroll, generateReport;
    JButton calculateOvertime, viewAttendance, generatePayslip;
    
    Container container;
    GridBagLayout layout;
    
    JTable employeeTable;
    EmployeeTableModel tableModel;
    
    public PayrollGui() {
        setTitle("Philippine Payroll Management System");
        
        employeeName = new JLabel("Employee Name:");
        employeeId = new JLabel("Employee ID:");
        hourlyRate = new JLabel("Hourly Rate (PHP):");
        hoursWorked = new JLabel("Hours Worked:");
        
        employeeNameField = new JTextField(20);
        employeeIdField = new JTextField(20);
        hourlyRateField = new JTextField(20);
        hoursWorkedField = new JTextField(20);
        
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
        
        addToContainer(employeeName, 0, 0, 1, 1);
        addToContainer(employeeNameField, 1, 0, 2, 1);
        addToContainer(employeeId, 0, 1, 1, 1);
        addToContainer(employeeIdField, 1, 1, 2, 1);
        addToContainer(hourlyRate, 0, 2, 1, 1);
        addToContainer(hourlyRateField, 1, 2, 2, 1);
        addToContainer(hoursWorked, 0, 3, 1, 1);
        addToContainer(hoursWorkedField, 1, 3, 2, 1);
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        buttonPanel.add(addEmployee);
        buttonPanel.add(updateEmployee);
        buttonPanel.add(deleteEmployee);
        buttonPanel.add(processPayroll);
        buttonPanel.add(generateReport);
        buttonPanel.add(calculateOvertime);
        buttonPanel.add(viewAttendance);
        buttonPanel.add(generatePayslip);
        
        addToContainer(buttonPanel, 0, 4, 3, 1);
        addToContainer(tableScrollPane, 0, 5, 3, 3);
        
        addEmployee.addActionListener(e -> {
            String name = employeeNameField.getText();
            String id = employeeIdField.getText();
            String hourlyRateText = hourlyRateField.getText();
            String hoursWorkedText = hoursWorkedField.getText();
            
            if (!name.isEmpty() && !id.isEmpty() && !hourlyRateText.isEmpty() && !hoursWorkedText.isEmpty()) {
                try {
                    double rate = Double.parseDouble(hourlyRateText);
                    double hours = Double.parseDouble(hoursWorkedText);
                    double wage = rate * hours;
                    
                    Employee employee = new Employee(id, name, hours, 0, wage, LocalDate.now(), 0, "Present");
                    tableModel.addEmployee(employee);
                    clearFields();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter valid numbers for hourly rate and hours worked.");
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
                String hoursWorkedText = hoursWorkedField.getText();
                
                if (!name.isEmpty() && !id.isEmpty() && !hourlyRateText.isEmpty() && !hoursWorkedText.isEmpty()) {
                    try {
                        double rate = Double.parseDouble(hourlyRateText);
                        double hours = Double.parseDouble(hoursWorkedText);
                        double wage = rate * hours;
                        
                        Employee employee = tableModel.getEmployee(selectedRow);
                        employee.setName(name);
                        employee.setId(id);
                        employee.setTotalHours(hours);
                        employee.setWage(wage);
                        employee.setDate(LocalDate.now());
                        
                        tableModel.updateEmployee(selectedRow, employee);
                        clearFields();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Please enter valid numbers for hourly rate and hours worked.");
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
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    tableModel.removeEmployee(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
            }
        });
        
        processPayroll.addActionListener(e -> {
            System.out.println("Process Payroll clicked");
        });
        
        generateReport.addActionListener(e -> {
            System.out.println("Generate Reports clicked");
        });
        
        calculateOvertime.addActionListener(e -> {
            System.out.println("Calculate Overtime clicked");
        });
        
        viewAttendance.addActionListener(e -> {
            System.out.println("View Attendance clicked");
        });
        
        generatePayslip.addActionListener(e -> {
            System.out.println("Generate Payslip clicked");
        });
        
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    Employee employee = tableModel.getEmployee(selectedRow);
                    employeeNameField.setText(employee.getName());
                    employeeIdField.setText(employee.getId());
                    hoursWorkedField.setText(String.valueOf(employee.getTotalHours()));
                    if (employee.getTotalHours() > 0) {
                        hourlyRateField.setText(String.valueOf(employee.getWage() / employee.getTotalHours()));
                    }
                }
            }
        });
        
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        employeeTable.setRowSelectionAllowed(true);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        container.add(component, gbc);
    }
    
    private void clearFields() {
        employeeNameField.setText("");
        employeeIdField.setText("");
        hourlyRateField.setText("");
        hoursWorkedField.setText("");
    }
}