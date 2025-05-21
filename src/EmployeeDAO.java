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
            
            // Check if employee already exists
            String checkEmployeeSql = "SELECT id FROM employees WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkEmployeeSql);
            checkStmt.setString(1, employee.getId());
            ResultSet rs = checkStmt.executeQuery();
            boolean employeeExists = rs.next();
            rs.close();
            checkStmt.close();
            
            // 1. Insert into employees table only if employee doesn't exist
            if (!employeeExists) {
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
            } else {
                // Update employee name and hourly rate if employee exists
                String updateEmployeeSql = "UPDATE employees SET name = ?, hourly_rate = ? WHERE id = ?";
                pstmtEmployee = conn.prepareStatement(updateEmployeeSql);
                pstmtEmployee.setString(1, employee.getName());
                
                // Calculate hourly rate if hours > 0
                double hourlyRate = 0;
                if (employee.getTotalHours() > 0) {
                    hourlyRate = employee.getWage() / employee.getTotalHours();
                }
                pstmtEmployee.setDouble(2, hourlyRate);
                pstmtEmployee.setString(3, employee.getId());
                pstmtEmployee.executeUpdate();
            }
            
            // 2. Check if payroll record exists for this employee and date
            String checkPayrollSql = "SELECT payroll_id FROM payroll WHERE employee_id = ? AND date = ?";
            checkStmt = conn.prepareStatement(checkPayrollSql);
            checkStmt.setString(1, employee.getId());
            checkStmt.setDate(2, java.sql.Date.valueOf(employee.getDate()));
            rs = checkStmt.executeQuery();
            boolean payrollExists = rs.next();
            
            if (payrollExists) {
                // Update existing payroll record
                int payrollId = rs.getInt("payroll_id");
                rs.close();
                checkStmt.close();
                
                String updatePayrollSql = "UPDATE payroll SET total_hours = ?, overtime = ?, wage = ?, " +
                                         "sss_deduction = ?, philhealth_deduction = ?, pagibig_deduction = ?, " +
                                         "tax_deductions = ?, net_pay = ? WHERE payroll_id = ?";
                pstmtPayroll = conn.prepareStatement(updatePayrollSql);
                pstmtPayroll.setDouble(1, employee.getTotalHours());
                pstmtPayroll.setDouble(2, employee.getOvertime());
                pstmtPayroll.setDouble(3, employee.getWage());
                pstmtPayroll.setDouble(4, employee.getSssDeduction());
                pstmtPayroll.setDouble(5, employee.getPhilhealthDeduction());
                pstmtPayroll.setDouble(6, employee.getPagibigDeduction());
                pstmtPayroll.setDouble(7, employee.getTaxDeduction());
                pstmtPayroll.setDouble(8, employee.getNetPay());
                pstmtPayroll.setInt(9, payrollId);
                pstmtPayroll.executeUpdate();
            } else {
                rs.close();
                checkStmt.close();
                
                // Insert new payroll record
                String insertPayrollSql = "INSERT INTO payroll (employee_id, total_hours, overtime, wage, date, " +
                                         "sss_deduction, philhealth_deduction, pagibig_deduction, tax_deductions, " +
                                         "net_pay) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                pstmtPayroll = conn.prepareStatement(insertPayrollSql);
                pstmtPayroll.setString(1, employee.getId());
                pstmtPayroll.setDouble(2, employee.getTotalHours());
                pstmtPayroll.setDouble(3, employee.getOvertime());
                pstmtPayroll.setDouble(4, employee.getWage());
                pstmtPayroll.setDate(5, java.sql.Date.valueOf(employee.getDate()));
                pstmtPayroll.setDouble(6, employee.getSssDeduction());
                pstmtPayroll.setDouble(7, employee.getPhilhealthDeduction());
                pstmtPayroll.setDouble(8, employee.getPagibigDeduction());
                pstmtPayroll.setDouble(9, employee.getTaxDeduction());
                pstmtPayroll.setDouble(10, employee.getNetPay());
                pstmtPayroll.executeUpdate();
            }
            
            // 3. Check if attendance record exists for this employee and date
            String checkAttendanceSql = "SELECT attendance_id FROM attendance WHERE employee_id = ? AND date = ?";
            checkStmt = conn.prepareStatement(checkAttendanceSql);
            checkStmt.setString(1, employee.getId());
            checkStmt.setDate(2, java.sql.Date.valueOf(employee.getDate()));
            rs = checkStmt.executeQuery();
            boolean attendanceExists = rs.next();
            
            if (attendanceExists) {
                // Update existing attendance record
                int attendanceId = rs.getInt("attendance_id");
                rs.close();
                checkStmt.close();
                
                String updateAttendanceSql = "UPDATE attendance SET status = ? WHERE attendance_id = ?";
                pstmtAttendance = conn.prepareStatement(updateAttendanceSql);
                pstmtAttendance.setString(1, employee.getAttendance() != null ? employee.getAttendance() : "Present");
                pstmtAttendance.setInt(2, attendanceId);
                pstmtAttendance.executeUpdate();
            } else {
                rs.close();
                checkStmt.close();
                
                // Insert new attendance record
                String insertAttendanceSql = "INSERT INTO attendance (employee_id, date, status) VALUES (?, ?, ?)";
                pstmtAttendance = conn.prepareStatement(insertAttendanceSql);
                pstmtAttendance.setString(1, employee.getId());
                pstmtAttendance.setDate(2, java.sql.Date.valueOf(employee.getDate()));
                pstmtAttendance.setString(3, employee.getAttendance() != null ? employee.getAttendance() : "Present");
                pstmtAttendance.executeUpdate();
            }
            
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
        // Since the addEmployee method now handles both insert and update,
        // we can simply call it for updates as well
        return addEmployee(employee);
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
    
    public boolean deleteEmployeeRecord(String employeeId, LocalDate date) {
        // Delete specific payroll and attendance records for a given date
        String deletePayrollSQL = "DELETE FROM payroll WHERE employee_id = ? AND date = ?";
        String deleteAttendanceSQL = "DELETE FROM attendance WHERE employee_id = ? AND date = ?";
        
        Connection conn = null;
        PreparedStatement pstmtPayroll = null;
        PreparedStatement pstmtAttendance = null;
        
        try {
            conn = dbConfig.getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // Delete from payroll table for specific date
            pstmtPayroll = conn.prepareStatement(deletePayrollSQL);
            pstmtPayroll.setString(1, employeeId);
            pstmtPayroll.setDate(2, java.sql.Date.valueOf(date));
            pstmtPayroll.executeUpdate();
            
            // Delete from attendance table for specific date
            pstmtAttendance = conn.prepareStatement(deleteAttendanceSQL);
            pstmtAttendance.setString(1, employeeId);
            pstmtAttendance.setDate(2, java.sql.Date.valueOf(date));
            int affectedRows = pstmtAttendance.executeUpdate();
            
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
            System.err.println("Error deleting employee record: " + e.getMessage());
            return false;
        } finally {
            // Close all resources and restore auto-commit
            try {
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
    
    public Employee getEmployee(String employeeId) {
        // Get the most recent record for an employee
        String sql = "SELECT e.id, e.name, p.total_hours, p.overtime, p.wage, p.date, " +
                     "p.sss_deduction, p.philhealth_deduction, p.pagibig_deduction, p.tax_deductions, " +
                     "a.status " +
                     "FROM employees e " +
                     "LEFT JOIN payroll p ON e.id = p.employee_id AND p.date = (SELECT MAX(date) FROM payroll WHERE employee_id = e.id) " +
                     "LEFT JOIN attendance a ON e.id = a.employee_id AND a.date = p.date " +
                     "WHERE e.id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employeeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
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
                    
                    // Get all deduction values with null checks
                    double sssDeduction = rs.getDouble("sss_deduction");
                    employee.setSssDeduction(rs.wasNull() ? 0 : sssDeduction);
                    
                    double philhealthDeduction = rs.getDouble("philhealth_deduction");
                    employee.setPhilhealthDeduction(rs.wasNull() ? 0 : philhealthDeduction);
                    
                    double pagibigDeduction = rs.getDouble("pagibig_deduction");
                    employee.setPagibigDeduction(rs.wasNull() ? 0 : pagibigDeduction);
                    
                    double taxDeduction = rs.getDouble("tax_deductions");
                    employee.setTaxDeduction(rs.wasNull() ? 0 : taxDeduction);
                    
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
    
    public Employee getEmployeeForDate(String employeeId, LocalDate date) {
        // Get the employee record for a specific date
        String sql = "SELECT e.id, e.name, p.total_hours, p.overtime, p.wage, p.date, " +
                     "p.sss_deduction, p.philhealth_deduction, p.pagibig_deduction, p.tax_deductions, " +
                     "a.status " +
                     "FROM employees e " +
                     "LEFT JOIN payroll p ON e.id = p.employee_id AND p.date = ? " +
                     "LEFT JOIN attendance a ON e.id = a.employee_id AND a.date = ? " +
                     "WHERE e.id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(date));
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            pstmt.setString(3, employeeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
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
                    
                    Date sqlDate = rs.getDate("date");
                    if (sqlDate != null) {
                        employee.setDate(sqlDate.toLocalDate());
                    } else {
                        employee.setDate(date);
                    }
                    
                    // Get all deduction values with null checks
                    double sssDeduction = rs.getDouble("sss_deduction");
                    employee.setSssDeduction(rs.wasNull() ? 0 : sssDeduction);
                    
                    double philhealthDeduction = rs.getDouble("philhealth_deduction");
                    employee.setPhilhealthDeduction(rs.wasNull() ? 0 : philhealthDeduction);
                    
                    double pagibigDeduction = rs.getDouble("pagibig_deduction");
                    employee.setPagibigDeduction(rs.wasNull() ? 0 : pagibigDeduction);
                    
                    double taxDeduction = rs.getDouble("tax_deductions");
                    employee.setTaxDeduction(rs.wasNull() ? 0 : taxDeduction);
                    
                    String status = rs.getString("status");
                    employee.setAttendance(status != null ? status : "Present");
                    
                    return employee;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting employee for date: " + e.getMessage());
        }
        return null;
    }
    
    public List<Employee> getAllEmployees() {
        // Get basic employee info without payroll details
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT id, name FROM employees";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getString("id"));
                employee.setName(rs.getString("name"));
                employee.setDate(LocalDate.now()); // Default to today
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all employees: " + e.getMessage());
        }
        return employees;
    }
    
    public List<Employee> getEmployeesWithPayroll() {
        // Get the most recent payroll records for all employees
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.id, e.name, p.total_hours, p.overtime, p.wage, p.date, " +
                     "p.sss_deduction, p.philhealth_deduction, p.pagibig_deduction, p.tax_deductions, " +
                     "a.status " +
                     "FROM employees e " +
                     "LEFT JOIN payroll p ON e.id = p.employee_id AND p.date = (SELECT MAX(date) FROM payroll WHERE employee_id = e.id) " +
                     "LEFT JOIN attendance a ON e.id = a.employee_id AND a.date = p.date";
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
                
                // Get all deduction values with null checks
                double sssDeduction = rs.getDouble("sss_deduction");
                employee.setSssDeduction(rs.wasNull() ? 0 : sssDeduction);
                
                double philhealthDeduction = rs.getDouble("philhealth_deduction");
                employee.setPhilhealthDeduction(rs.wasNull() ? 0 : philhealthDeduction);
                
                double pagibigDeduction = rs.getDouble("pagibig_deduction");
                employee.setPagibigDeduction(rs.wasNull() ? 0 : pagibigDeduction);
                
                double taxDeduction = rs.getDouble("tax_deductions");
                employee.setTaxDeduction(rs.wasNull() ? 0 : taxDeduction);
                
                String status = rs.getString("status");
                employee.setAttendance(status != null ? status : "Present");
                
                employees.add(employee);
            }
        } catch (SQLException e) {
            System.err.println("Error getting employees with payroll: " + e.getMessage());
        }
        return employees;
    }
    
    public List<Employee> getEmployeesForDate(LocalDate date) {
        // Get payroll records for all employees for a specific date
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.id, e.name, p.total_hours, p.overtime, p.wage, p.date, " +
                     "p.sss_deduction, p.philhealth_deduction, p.pagibig_deduction, p.tax_deductions, " +
                     "a.status " +
                     "FROM employees e " +
                     "LEFT JOIN payroll p ON e.id = p.employee_id AND p.date = ? " +
                     "LEFT JOIN attendance a ON e.id = a.employee_id AND a.date = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(date));
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            
            try (ResultSet rs = pstmt.executeQuery()) {
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
                    
                    employee.setDate(date);
                    
                    // Get all deduction values with null checks
                    double sssDeduction = rs.getDouble("sss_deduction");
                    employee.setSssDeduction(rs.wasNull() ? 0 : sssDeduction);
                    
                    double philhealthDeduction = rs.getDouble("philhealth_deduction");
                    employee.setPhilhealthDeduction(rs.wasNull() ? 0 : philhealthDeduction);
                    
                    double pagibigDeduction = rs.getDouble("pagibig_deduction");
                    employee.setPagibigDeduction(rs.wasNull() ? 0 : pagibigDeduction);
                    
                    double taxDeduction = rs.getDouble("tax_deductions");
                    employee.setTaxDeduction(rs.wasNull() ? 0 : taxDeduction);
                    
                    String status = rs.getString("status");
                    employee.setAttendance(status != null ? status : "Present");
                    
                    employees.add(employee);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting employees for date: " + e.getMessage());
        }
        return employees;
    }
    
    public List<Employee> getEmployeesForDateRange(LocalDate startDate, LocalDate endDate) {
        // Get payroll records for all employees for a date range
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT e.id, e.name, p.total_hours, p.overtime, p.wage, p.date, " +
                     "p.sss_deduction, p.philhealth_deduction, p.pagibig_deduction, p.tax_deductions, " +
                     "a.status " +
                     "FROM employees e " +
                     "LEFT JOIN payroll p ON e.id = p.employee_id AND p.date BETWEEN ? AND ? " +
                     "LEFT JOIN attendance a ON e.id = a.employee_id AND a.date = p.date " +
                     "WHERE p.date IS NOT NULL " +
                     "ORDER BY p.date, e.id";
                     
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, java.sql.Date.valueOf(startDate));
            pstmt.setDate(2, java.sql.Date.valueOf(endDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
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
                    
                    // Get all deduction values with null checks
                    double sssDeduction = rs.getDouble("sss_deduction");
                    employee.setSssDeduction(rs.wasNull() ? 0 : sssDeduction);
                    
                    double philhealthDeduction = rs.getDouble("philhealth_deduction");
                    employee.setPhilhealthDeduction(rs.wasNull() ? 0 : philhealthDeduction);
                    
                    double pagibigDeduction = rs.getDouble("pagibig_deduction");
                    employee.setPagibigDeduction(rs.wasNull() ? 0 : pagibigDeduction);
                    
                    double taxDeduction = rs.getDouble("tax_deductions");
                    employee.setTaxDeduction(rs.wasNull() ? 0 : taxDeduction);
                    
                    String status = rs.getString("status");
                    employee.setAttendance(status != null ? status : "Present");
                    
                    employees.add(employee);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting employees for date range: " + e.getMessage());
        }
        return employees;
    }
}