import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeTableModel extends AbstractTableModel {
    private final String[] columnNames = {
        "ID", "Name", "Total Hours", "Overtime", "Wage", "Date", 
        "SSS", "PhilHealth", "Pag-IBIG", "Tax", 
        "Total Deduction", "Gross Pay", "Net Pay", "Attendance"
    };
    private List<Employee> employees;
    private List<Employee> filteredEmployees; // Added for filtering
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private LocalDate filterDate = null; // Store current filter date
    
    public EmployeeTableModel() {
        this.employees = new ArrayList<>();
        this.filteredEmployees = new ArrayList<>();
    }
    
    @Override
    public int getRowCount() {
        return isFiltered() ? filteredEmployees.size() : employees.size();
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
        Employee employee = isFiltered() ? filteredEmployees.get(rowIndex) : employees.get(rowIndex);
        switch (columnIndex) {
            case 0: return employee.getId();
            case 1: return employee.getName();
            case 2: return employee.getTotalHours();
            case 3: return employee.getOvertime();
            case 4: return employee.getWage();
            case 5: return employee.getDate() != null ? employee.getDate().format(dateFormatter) : "";
            case 6: return employee.getSssDeduction();
            case 7: return employee.getPhilhealthDeduction();
            case 8: return employee.getPagibigDeduction();
            case 9: return employee.getTaxDeduction();
            case 10: return employee.getTotalDeduction();
            case 11: return employee.getGrossPay();
            case 12: return employee.getNetPay();
            case 13: return employee.getAttendance();
            default: return null;
        }
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Employee employee = isFiltered() ? filteredEmployees.get(rowIndex) : employees.get(rowIndex);
        try {
            switch (columnIndex) {
                case 0: employee.setId((String) aValue); break;
                case 1: employee.setName((String) aValue); break;
                case 2: 
                    if (aValue instanceof Double) {
                        employee.setTotalHours((Double) aValue);
                    } else if (aValue instanceof String) {
                        employee.setTotalHours(Double.parseDouble((String) aValue));
                    }
                    break;
                case 3: 
                    if (aValue instanceof Double) {
                        employee.setOvertime((Double) aValue);
                    } else if (aValue instanceof String) {
                        employee.setOvertime(Double.parseDouble((String) aValue));
                    }
                    break;
                case 4: 
                    if (aValue instanceof Double) {
                        employee.setWage((Double) aValue);
                    } else if (aValue instanceof String) {
                        employee.setWage(Double.parseDouble((String) aValue));
                    }
                    break;
                case 5: 
                    if (aValue instanceof LocalDate) {
                        employee.setDate((LocalDate) aValue);
                    } else if (aValue instanceof String) {
                        try {
                            String dateStr = (String) aValue;
                            if (!dateStr.isEmpty()) {
                                employee.setDate(LocalDate.parse(dateStr, dateFormatter));
                            }
                        } catch (DateTimeParseException ex) {
                            System.err.println("Invalid date format: " + aValue);
                        }
                    }
                    break;
                case 6: 
                    if (aValue instanceof Double) {
                        employee.setSssDeduction((Double) aValue);
                    } else if (aValue instanceof String) {
                        employee.setSssDeduction(Double.parseDouble((String) aValue));
                    }
                    break;
                case 7: 
                    if (aValue instanceof Double) {
                        employee.setPhilhealthDeduction((Double) aValue);
                    } else if (aValue instanceof String) {
                        employee.setPhilhealthDeduction(Double.parseDouble((String) aValue));
                    }
                    break;
                case 8: 
                    if (aValue instanceof Double) {
                        employee.setPagibigDeduction((Double) aValue);
                    } else if (aValue instanceof String) {
                        employee.setPagibigDeduction(Double.parseDouble((String) aValue));
                    }
                    break;
                case 9: 
                    if (aValue instanceof Double) {
                        employee.setTaxDeduction((Double) aValue);
                    } else if (aValue instanceof String) {
                        employee.setTaxDeduction(Double.parseDouble((String) aValue));
                    }
                    break;
                case 13: employee.setAttendance((String) aValue); break;
            }
            fireTableCellUpdated(rowIndex, columnIndex);
        } catch (ClassCastException | NumberFormatException ex) {
            System.err.println("Error setting value at [" + rowIndex + "," + columnIndex + "]: " + ex.getMessage());
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Make calculated fields non-editable
        return columnIndex != 10 && columnIndex != 11 && columnIndex != 12;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        // Ensure proper column class types
        switch (columnIndex) {
            case 0: case 1: case 13: return String.class;
            case 5: return String.class; // Return String.class for date column
            case 2: case 3: case 4: case 6: case 7: case 8: case 9: case 10: case 11: case 12: return Double.class;
            default: return Object.class;
        }
    }
    
    public void addEmployee(Employee employee) {
        employees.add(employee);
        if (!isFiltered() || (filterDate != null && employee.getDate() != null && employee.getDate().equals(filterDate))) {
            filteredEmployees.add(employee);
            fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
        } else {
            fireTableDataChanged();
        }
    }
    
    public void removeEmployee(int rowIndex) {
        if (isFiltered()) {
            Employee empToRemove = filteredEmployees.get(rowIndex);
            filteredEmployees.remove(rowIndex);
            employees.remove(empToRemove);
        } else {
            employees.remove(rowIndex);
        }
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
    
    public Employee getEmployee(int rowIndex) {
        return isFiltered() ? filteredEmployees.get(rowIndex) : employees.get(rowIndex);
    }
    
    public void updateEmployee(int rowIndex, Employee employee) {
        if (isFiltered()) {
            int mainIndex = employees.indexOf(filteredEmployees.get(rowIndex));
            if (mainIndex >= 0) {
                employees.set(mainIndex, employee);
            }
            filteredEmployees.set(rowIndex, employee);
        } else {
            employees.set(rowIndex, employee);
        }
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees);
    }
    
    public void clearAll() {
        int size = isFiltered() ? filteredEmployees.size() : employees.size();
        if (size > 0) {
            employees.clear();
            filteredEmployees.clear();
            filterDate = null;
            fireTableRowsDeleted(0, size - 1);
        } else {
            employees.clear();
            filteredEmployees.clear();
            filterDate = null;
        }
    }
    
    // New methods for filtering
    public void filterByDate(LocalDate date) {
        filterDate = date;
        if (date == null) {
            clearFilter();
            return;
        }
        
        filteredEmployees.clear();
        for (Employee emp : employees) {
            if (emp.getDate() != null && emp.getDate().equals(date)) {
                filteredEmployees.add(emp);
            }
        }
        fireTableDataChanged();
    }
    public void filterByEmployeeId(String employeeId) {
    if (employeeId == null || employeeId.trim().isEmpty()) {
        clearFilter();
        return;
    }
    
    // Set filter type to employee ID (we need to track this)
    filterDate = null; // Clear date filter
    filteredEmployees.clear();
    
    String searchId = employeeId.trim().toLowerCase();
    for (Employee emp : employees) {
        if (emp.getId() != null && emp.getId().toLowerCase().contains(searchId)) {
            filteredEmployees.add(emp);
        }
    }
    fireTableDataChanged();
}
    
    public void clearFilter() {
        filterDate = null;
        filteredEmployees.clear();
        fireTableDataChanged();
    }
    
    public boolean isFiltered() {
    return filterDate != null || !filteredEmployees.isEmpty();
}
    
    public LocalDate getCurrentFilterDate() {
        return filterDate;
    }
}