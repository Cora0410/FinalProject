import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CastroPayrollSystemGUI {
    private static ArrayList<String> employeeList = new ArrayList<>(); 
    private static ArrayList<Double> hoursWorkedList = new ArrayList<>();
    private static ArrayList<Double> hourlyRateList = new ArrayList<>();

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Payroll System - Philippines");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create a panel for employee management
        JPanel employeePanel = new JPanel();
        employeePanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Input fields
        JLabel nameLabel = new JLabel("Employee Name:");
        JTextField nameField = new JTextField(20);
        employeePanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        employeePanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel idLabel = new JLabel("Employee ID:");
        JTextField idField = new JTextField(20);
        employeePanel.add(idLabel, gbc);
        gbc.gridx = 1;
        employeePanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel hourlyRateLabel = new JLabel("Hourly Rate:");
        JTextField hourlyRateField = new JTextField(20);
        employeePanel.add(hourlyRateLabel, gbc);
        gbc.gridx = 1;
        employeePanel.add(hourlyRateField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel hoursWorkedLabel = new JLabel("Hours Worked:");
        JTextField hoursWorkedField = new JTextField(20);
        employeePanel.add(hoursWorkedLabel, gbc);
        gbc.gridx = 1;
        employeePanel.add(hoursWorkedField, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2; // Span two columns
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); 
        JButton addButton = new JButton("Add Employee");
        JButton deleteButton = new JButton("Delete Employee");
        JButton viewListButton = new JButton("View Employee List");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewListButton);
        employeePanel.add(buttonPanel, gbc);

        frame.add(employeePanel, BorderLayout.CENTER);

        // Create a panel for payroll processing
        JPanel payrollPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel payrollLabel = new JLabel("Click to Process Payroll");
        payrollPanel.add(payrollLabel);
        JButton processButton = new JButton("Process Payroll");
        payrollPanel.add(processButton);

        frame.add(payrollPanel, BorderLayout.SOUTH);

        // Action listeners
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String id = idField.getText();
                double hourlyRate = Double.parseDouble(hourlyRateField.getText());
                double hoursWorked = Double.parseDouble(hoursWorkedField.getText());

                String employeeDetails = "Name: " + name + ", ID: " + id;
                employeeList.add(employeeDetails);
                hoursWorkedList.add(hoursWorked); 
                hourlyRateList.add(hourlyRate);
                JOptionPane.showMessageDialog(frame, "Employee Added:\n" + employeeDetails + "\nHours Worked: " + hoursWorked);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numeric values for hourly rate and hours worked.");
            }
        });

        deleteButton.addActionListener(e -> {
            if (employeeList.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No employees to delete.");
                return;
            }

            JFrame deleteFrame = new JFrame("Delete Employee");
            deleteFrame.setSize(400, 300);
            deleteFrame.setLayout(new BorderLayout());

            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (String employee : employeeList) {
                listModel.addElement(employee);
            }

            JList<String> employeeJList = new JList<>(listModel);
            JScrollPane scrollPane = new JScrollPane(employeeJList);
            deleteFrame.add(scrollPane, BorderLayout.CENTER);

            JButton confirmDeleteButton = new JButton("Delete Selected Employee");
            confirmDeleteButton.addActionListener(e1 -> {
                int selectedIndex = employeeJList.getSelectedIndex();
                if (selectedIndex != -1) {
                    employeeList.remove(selectedIndex);
                    hoursWorkedList.remove(selectedIndex);
                    hourlyRateList.remove(selectedIndex); 
                    listModel.remove(selectedIndex);
                    JOptionPane.showMessageDialog(deleteFrame, "Employee Deleted.");
                } else {
                    JOptionPane.showMessageDialog(deleteFrame, "No employee selected.");
                }
            });

            deleteFrame.add(confirmDeleteButton, BorderLayout.SOUTH);
            deleteFrame.setVisible(true);
        });

        viewListButton.addActionListener(e -> {
            JFrame listFrame = new JFrame("Employee List");
            listFrame.setSize(400, 300);
            listFrame.setLayout(new BorderLayout());

            JTextArea listArea = new JTextArea();
            listArea.setEditable(false);
            for (int i = 0; i < employeeList.size(); i++) {
                double wage = hourlyRateList.get(i) * hoursWorkedList.get(i);
                listArea.append(employeeList.get(i) + ", Hours Worked: " + hoursWorkedList.get(i) + ", Daily Wage: PHP " + wage + "\n");
            }

            JScrollPane scrollPane = new JScrollPane(listArea);
            listFrame.add(scrollPane, BorderLayout.CENTER);

            listFrame.setVisible(true);
        });

        processButton.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Payroll Processed!"));

        
        frame.setVisible(true);
    }
}

