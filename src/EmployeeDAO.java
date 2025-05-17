import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private DatabaseConfig dbConfig;
    
    public EmployeeDAO() {
        dbConfig = DatabaseConfig.getInstance();
    }
    
    public boolean addEmployee(Employee employee) {
        String sql = "INSERT INTO employees (id, name, hourly_rate, date_hired) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employee.getId());
            pstmt.setString(2, employee.getName());
            // Assuming hourlyRate is calculated from totalHours and wage
            double hourlyRate = 0;
            if (employee.getTotalHours() > 0) {
                hourlyRate = employee.getWage() / employee.getTotalHours();
            }
            pstmt.setDouble(3, hourlyRate);
            pstmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET name = ?, hourly_rate = ? WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employee.getName());
            // Assuming hourlyRate is calculated from totalHours and wage
            double hourlyRate = 0;
            if (employee.getTotalHours() > 0) {
                hourlyRate = employee.getWage() / employee.getTotalHours();
            }
            pstmt.setDouble(2, hourlyRate);
            pstmt.setString(3, employee.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteEmployee(String employeeId) {
        // First delete from related tables to handle foreign key constraints
        String deletePayrollSQL = "DELETE FROM payroll WHERE employee_id = ?";
        String deleteAttendanceSQL = "DELETE FROM attendance WHERE employee_id = ?";
        String deleteEmployeeSQL = "DELETE FROM employees WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement pstmtPayroll = null;
        PreparedStatement pstmtAttendance = null;
        PreparedStatement pstmtEmployee = null;
        
        try {
            conn = dbConfig.getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // Delete from payroll table first
            pstmtPayroll = conn.prepareStatement(deletePayrollSQL);
            pstmtPayroll.setString(1, employeeId);
            pstmtPayroll.executeUpdate();
            
            // Delete from attendance table next
            pstmtAttendance = conn.prepareStatement(deleteAttendanceSQL);
            pstmtAttendance.setString(1, employeeId);
            pstmtAttendance.executeUpdate();
            
            // Finally delete from employees table
            pstmtEmployee = conn.prepareStatement(deleteEmployeeSQL);
            pstmtEmployee.setString(1, employeeId);
            int affectedRows = pstmtEmployee.executeUpdate();
            
            // Commit transaction
            conn.commit();
            
            return affectedRows > 0;
        } catch (SQLException e) {
            // Rollback on error
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error deleting employee: " + e.getMessage());
            return false;
        } finally {
            // Close all resources and restore auto-commit
            try {
                if (pstmtPayroll != null) pstmtPayroll.close();
                if (pstmtAttendance != null) pstmtAttendance.close();
                if (pstmtEmployee != null) pstmtEmployee.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    public Employee getEmployee(String employeeId) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employeeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee();
                    employee.setId(rs.getString("id"));
                    employee.setName(rs.getString("name"));
                    // Other fields will be populated from payroll records
                    return employee;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting employee: " + e.getMessage());
        }
        return null;
    }
    
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getString("id"));
                employee.setName(rs.getString("name"));
                // Other fields will be populated from payroll records
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all employees: " + e.getMessage());
        }
        return employees;
    }
    
    public void addPayroll(String employeeId, double totalHours, double overtime, double wage, LocalDate date, double taxDeductions) {
        String sql = "INSERT INTO payroll (employee_id, total_hours, overtime, wage, date, tax_deductions) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employeeId);
            pstmt.setDouble(2, totalHours);
            pstmt.setDouble(3, overtime);
            pstmt.setDouble(4, wage);
            pstmt.setDate(5, java.sql.Date.valueOf(date));
            pstmt.setDouble(6, taxDeductions);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding payroll: " + e.getMessage());
        }
    }
    
    public void recordAttendance(String employeeId, LocalDate date, String status) {
        String sql = "INSERT INTO attendance (employee_id, date, status) VALUES (?, ?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employeeId);
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            pstmt.setString(3, status);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error recording attendance: " + e.getMessage());
        }
    }
    
    public List<Employee> getEmployeesWithPayroll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.id, e.name, p.total_hours, p.overtime, p.wage, p.date, p.tax_deductions, a.status " +
                     "FROM employees e " +
                     "LEFT JOIN payroll p ON e.id = p.employee_id AND p.date = (SELECT MAX(date) FROM payroll WHERE employee_id = e.id) " +
                     "LEFT JOIN attendance a ON e.id = a.employee_id AND a.date = (SELECT MAX(date) FROM attendance WHERE employee_id = e.id)";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getString("id"));
                employee.setName(rs.getString("name"));
                employee.setTotalHours(rs.getDouble("total_hours"));
                employee.setOvertime(rs.getDouble("overtime"));
                employee.setWage(rs.getDouble("wage"));
                Date date = rs.getDate("date");
                if (date != null) {
                    employee.setDate(date.toLocalDate());
                }
                employee.setTaxDeductions(rs.getDouble("tax_deductions"));
                employee.setAttendance(rs.getString("status"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error getting employees with payroll: " + e.getMessage());
        }
        return employees;
    }
}