import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EmployeeTableModel extends AbstractTableModel {
    private final String[] columnNames = {"ID", "Name", "Total Hours", "Overtime", "Wage", "Date", "Tax Deductions", "Attendance"};
    private List<Employee> employees;
    
    public EmployeeTableModel() {
        this.employees = new ArrayList<>();
    }
    
    @Override
    public int getRowCount() {
        return employees.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Employee employee = employees.get(rowIndex);
        switch (columnIndex) {
            case 0: return employee.getId();
            case 1: return employee.getName();
            case 2: return employee.getTotalHours();
            case 3: return employee.getOvertime();
            case 4: return employee.getWage();
            case 5: return employee.getDate();
            case 6: return employee.getTaxDeductions();
            case 7: return employee.getAttendance();
            default: return null;
        }
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Employee employee = employees.get(rowIndex);
        switch (columnIndex) {
            case 0: employee.setId((String) aValue); break;
            case 1: employee.setName((String) aValue); break;
            case 2: employee.setTotalHours((Double) aValue); break;
            case 3: employee.setOvertime((Double) aValue); break;
            case 4: employee.setWage((Double) aValue); break;
            case 5: employee.setDate((java.time.LocalDate) aValue); break;
            case 6: employee.setTaxDeductions((Double) aValue); break;
            case 7: employee.setAttendance((String) aValue); break;
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
    
    public void addEmployee(Employee employee) {
        employees.add(employee);
        fireTableRowsInserted(employees.size() - 1, employees.size() - 1);
    }
    
    public void removeEmployee(int rowIndex) {
        employees.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
    
    public Employee getEmployee(int rowIndex) {
        return employees.get(rowIndex);
    }
    
    public void updateEmployee(int rowIndex, Employee employee) {
        employees.set(rowIndex, employee);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees);
    }
    
    public void clearAll() {
        int size = employees.size();
        if (size > 0) {
            employees.clear();
            fireTableRowsDeleted(0, size - 1);
        } else {

            employees.clear();
        }
    }
}