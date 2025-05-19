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
        Connection conn = null;
        PreparedStatement pstmtEmployee = null;
        PreparedStatement pstmtPayroll = null;
        PreparedStatement pstmtAttendance = null;
        
        try {
            conn = dbConfig.getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // 1. Insert into employees table
            String employeeSql = "INSERT INTO employees (id, name, hourly_rate, date_hired) VALUES (?, ?, ?, ?)";
            pstmtEmployee = conn.prepareStatement(employeeSql);
            pstmtEmployee.setString(1, employee.getId());
            pstmtEmployee.setString(2, employee.getName());
            
            // Calculate hourly rate if hours > 0
            double hourlyRate = 0;
            if (employee.getTotalHours() > 0) {
                hourlyRate = employee.getWage() / employee.getTotalHours();
            }
            pstmtEmployee.setDouble(3, hourlyRate);
            pstmtEmployee.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
            pstmtEmployee.executeUpdate();
            
            // 2. Insert into payroll table
            String payrollSql = "INSERT INTO payroll (employee_id, total_hours, overtime, wage, date, tax_deductions) VALUES (?, ?, ?, ?, ?, ?)";
            pstmtPayroll = conn.prepareStatement(payrollSql);
            pstmtPayroll.setString(1, employee.getId());
            pstmtPayroll.setDouble(2, employee.getTotalHours());
            pstmtPayroll.setDouble(3, employee.getOvertime());
            pstmtPayroll.setDouble(4, employee.getWage());
            pstmtPayroll.setDate(5, java.sql.Date.valueOf(employee.getDate() != null ? employee.getDate() : LocalDate.now()));
            pstmtPayroll.setDouble(6, employee.getTaxDeductions());
            pstmtPayroll.executeUpdate();
            
            // 3. Insert into attendance table
            String attendanceSql = "INSERT INTO attendance (employee_id, date, status) VALUES (?, ?, ?)";
            pstmtAttendance = conn.prepareStatement(attendanceSql);
            pstmtAttendance.setString(1, employee.getId());
            pstmtAttendance.setDate(2, java.sql.Date.valueOf(employee.getDate() != null ? employee.getDate() : LocalDate.now()));
            pstmtAttendance.setString(3, employee.getAttendance() != null ? employee.getAttendance() : "Present");
            pstmtAttendance.executeUpdate();
            
            // Commit transaction
            conn.commit();
            return true;
        } catch (SQLException e) {
            // Rollback on error
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error adding employee: " + e.getMessage());
            return false;
        } finally {
            // Close all resources and restore auto-commit
            try {
                if (pstmtEmployee != null) pstmtEmployee.close();
                if (pstmtPayroll != null) pstmtPayroll.close();
                if (pstmtAttendance != null) pstmtAttendance.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    public boolean updateEmployee(Employee employee) {
        Connection conn = null;
        PreparedStatement pstmtEmployee = null;
        PreparedStatement pstmtPayroll = null;
        PreparedStatement pstmtAttendance = null;
        
        try {
            conn = dbConfig.getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // 1. Update employees table
            String employeeSql = "UPDATE employees SET name = ?, hourly_rate = ? WHERE id = ?";
            pstmtEmployee = conn.prepareStatement(employeeSql);
            pstmtEmployee.setString(1, employee.getName());
            
            // Calculate hourly rate if hours > 0
            double hourlyRate = 0;
            if (employee.getTotalHours() > 0) {
                hourlyRate = employee.getWage() / employee.getTotalHours();
            }
            pstmtEmployee.setDouble(2, hourlyRate);
            pstmtEmployee.setString(3, employee.getId());
            pstmtEmployee.executeUpdate();
            
            // 2. Update or insert into payroll table
            // Check if there's a payroll record for this date
            String checkPayrollSql = "SELECT payroll_id FROM payroll WHERE employee_id = ? AND date = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkPayrollSql);
            checkStmt.setString(1, employee.getId());
            checkStmt.setDate(2, java.sql.Date.valueOf(employee.getDate() != null ? employee.getDate() : LocalDate.now()));
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Update existing record
                String updatePayrollSql = "UPDATE payroll SET total_hours = ?, overtime = ?, wage = ?, tax_deductions = ? WHERE payroll_id = ?";
                pstmtPayroll = conn.prepareStatement(updatePayrollSql);
                pstmtPayroll.setDouble(1, employee.getTotalHours());
                pstmtPayroll.setDouble(2, employee.getOvertime());
                pstmtPayroll.setDouble(3, employee.getWage());
                pstmtPayroll.setDouble(4, employee.getTaxDeductions());
                pstmtPayroll.setInt(5, rs.getInt("payroll_id"));
            } else {
                // Insert new record
                String insertPayrollSql = "INSERT INTO payroll (employee_id, total_hours, overtime, wage, date, tax_deductions) VALUES (?, ?, ?, ?, ?, ?)";
                pstmtPayroll = conn.prepareStatement(insertPayrollSql);
                pstmtPayroll.setString(1, employee.getId());
                pstmtPayroll.setDouble(2, employee.getTotalHours());
                pstmtPayroll.setDouble(3, employee.getOvertime());
                pstmtPayroll.setDouble(4, employee.getWage());
                pstmtPayroll.setDate(5, java.sql.Date.valueOf(employee.getDate() != null ? employee.getDate() : LocalDate.now()));
                pstmtPayroll.setDouble(6, employee.getTaxDeductions());
            }
            pstmtPayroll.executeUpdate();
            rs.close();
            checkStmt.close();
            
            // 3. Update or insert attendance record
            String checkAttendanceSql = "SELECT attendance_id FROM attendance WHERE employee_id = ? AND date = ?";
            checkStmt = conn.prepareStatement(checkAttendanceSql);
            checkStmt.setString(1, employee.getId());
            checkStmt.setDate(2, java.sql.Date.valueOf(employee.getDate() != null ? employee.getDate() : LocalDate.now()));
            rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Update existing record
                String updateAttendanceSql = "UPDATE attendance SET status = ? WHERE attendance_id = ?";
                pstmtAttendance = conn.prepareStatement(updateAttendanceSql);
                pstmtAttendance.setString(1, employee.getAttendance() != null ? employee.getAttendance() : "Present");
                pstmtAttendance.setInt(2, rs.getInt("attendance_id"));
            } else {
                // Insert new record
                String insertAttendanceSql = "INSERT INTO attendance (employee_id, date, status) VALUES (?, ?, ?)";
                pstmtAttendance = conn.prepareStatement(insertAttendanceSql);
                pstmtAttendance.setString(1, employee.getId());
                pstmtAttendance.setDate(2, java.sql.Date.valueOf(employee.getDate() != null ? employee.getDate() : LocalDate.now()));
                pstmtAttendance.setString(3, employee.getAttendance() != null ? employee.getAttendance() : "Present");
            }
            pstmtAttendance.executeUpdate();
            rs.close();
            checkStmt.close();
            
            // Commit transaction
            conn.commit();
            return true;
        } catch (SQLException e) {
            // Rollback on error
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error updating employee: " + e.getMessage());
            return false;
        } finally {
            // Close all resources and restore auto-commit
            try {
                if (pstmtEmployee != null) pstmtEmployee.close();
                if (pstmtPayroll != null) pstmtPayroll.close();
                if (pstmtAttendance != null) pstmtAttendance.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
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
        String sql = "SELECT e.id, e.name, p.total_hours, p.overtime, p.wage, p.date, p.tax_deductions, a.status " +
                     "FROM employees e " +
                     "LEFT JOIN payroll p ON e.id = p.employee_id AND p.date = (SELECT MAX(date) FROM payroll WHERE employee_id = e.id) " +
                     "LEFT JOIN attendance a ON e.id = a.employee_id AND a.date = (SELECT MAX(date) FROM attendance WHERE employee_id = e.id) " +
                     "WHERE e.id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employeeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Employee employee = new Employee();
                    employee.setId(rs.getString("id"));
                    employee.setName(rs.getString("name"));
                    employee.setTotalHours(rs.getDouble("total_hours"));
                    employee.setOvertime(rs.getDouble("overtime"));
                    employee.setWage(rs.getDouble("wage"));
                    Date date = rs.getDate("date");
                    if (date != null) {
                        employee.setDate(date.toLocalDate());
                    } else {
                        employee.setDate(LocalDate.now());
                    }
                    employee.setTaxDeductions(rs.getDouble("tax_deductions"));
                    String status = rs.getString("status");
                    employee.setAttendance(status != null ? status : "Present");
                    return employee;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting employee: " + e.getMessage());
        }
        return null;
    }
    
    public List<Employee> getAllEmployees() {
        return getEmployeesWithPayroll();
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
                
                // Handle null values from LEFT JOIN
                double totalHours = rs.getDouble("total_hours");
                employee.setTotalHours(rs.wasNull() ? 0 : totalHours);
                
                double overtime = rs.getDouble("overtime");
                employee.setOvertime(rs.wasNull() ? 0 : overtime);
                
                double wage = rs.getDouble("wage");
                employee.setWage(rs.wasNull() ? 0 : wage);
                
                Date date = rs.getDate("date");
                if (date != null) {
                    employee.setDate(date.toLocalDate());
                } else {
                    employee.setDate(LocalDate.now());
                }
                
                double taxDeductions = rs.getDouble("tax_deductions");
                employee.setTaxDeductions(rs.wasNull() ? 0 : taxDeductions);
                
                String status = rs.getString("status");
                employee.setAttendance(status != null ? status : "Present");
                
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error getting employees with payroll: " + e.getMessage());
        }
        return employees;
    }
}