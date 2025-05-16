import javax.swing.*;
import java.awt.*;

public class PayrollGui extends JFrame {


    JLabel employeeName, employeeId, hourlyRate, hoursWorked;
    JTextField employeeNameField, employeeIdField, hourlyRateField, hoursWorkedField;
    

    JButton addEmployee, updateEmployee, deleteEmployee, processPayroll, generateReport;
    JButton calculateOvertime, viewAttendance, generatePayslip;
    

    Container container;
    GridBagLayout layout;
    

    JTable employeeTable;

    
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
        

        String[] columnNames = {"ID", "Name", "Total Hours", "Overtime", "Wage Today", "Tax Deductions", "Attendance"};
        Object[][] data = {}; 
        employeeTable = new JTable(data, columnNames);
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

            System.out.println("Add Employee: " + name + " (ID: " + id + ")");
            clearFields();
        });
        
        updateEmployee.addActionListener(e -> {
     
            System.out.println("Update Employee clicked");
        });
        
        deleteEmployee.addActionListener(e -> {
     
            System.out.println("Delete Employee clicked");
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