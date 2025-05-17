import java.io.*;
import java.sql.*;
import java.util.Properties;
import javax.swing.JOptionPane;

public class DatabaseConfig {
    // Default database parameters
    private String host = "localhost";
    private int port = 3306;
    private String databaseName = "payroll_system";
    private String username = "root";
    private String password = "";
    
    private static DatabaseConfig instance;
    private Connection connection;
    
    private DatabaseConfig() {
        loadConfig();
    }
    
    public static DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }
    
    private void loadConfig() {
        try {
            File configFile = new File("config.properties");
            if (configFile.exists()) {
                Properties properties = new Properties();
                FileInputStream in = new FileInputStream(configFile);
                properties.load(in);
                in.close();
                
                host = properties.getProperty("host", host);
                port = Integer.parseInt(properties.getProperty("port", String.valueOf(port)));
                databaseName = properties.getProperty("database", databaseName);
                username = properties.getProperty("username", username);
                password = properties.getProperty("password", password);
            } else {
                // Create default config file
                createDefaultConfig();
            }
        } catch (Exception e) {
            System.err.println("Error loading configuration: " + e.getMessage());
            createDefaultConfig();
        }
    }
    
    private void createDefaultConfig() {
        try {
            Properties properties = new Properties();
            properties.setProperty("host", host);
            properties.setProperty("port", String.valueOf(port));
            properties.setProperty("database", databaseName);
            properties.setProperty("username", username);
            properties.setProperty("password", password);
            
            FileOutputStream out = new FileOutputStream("config.properties");
            properties.store(out, "Database Configuration");
            out.close();
            
            System.out.println("Created default configuration file 'config.properties'");
        } catch (Exception e) {
            System.err.println("Error creating default configuration: " + e.getMessage());
        }
    }
    
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                setupDatabase();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, 
                    "Database connection error: " + e.getMessage() + "\n\n" +
                    "Make sure:\n" +
                    "1. MySQL/MariaDB is installed and running\n" +
                    "2. Username and password in config.properties are correct\n" +
                    "3. You have mysql-connector-java.jar in your classpath",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                throw e;
            }
        }
        return connection;
    }
    
    private void setupDatabase() throws SQLException {
        // First connect to server without specifying database
        String rootUrl = "jdbc:mysql://" + host + ":" + port + "?useSSL=false";
        connection = DriverManager.getConnection(rootUrl, username, password);
        
        // Create database if it doesn't exist
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName);
        
        // Connect to the specific database
        connection.close();
        String dbUrl = "jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?useSSL=false";
        connection = DriverManager.getConnection(dbUrl, username, password);
        
        // Create tables if they don't exist
        createTables();
        
        System.out.println("Database and tables are ready.");
    }
    
    private void createTables() {
        try {
            Statement statement = connection.createStatement();
            
            // Create Employee table
            String createEmployeeTable = "CREATE TABLE IF NOT EXISTS employees (" +
                    "id VARCHAR(20) PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "hourly_rate DOUBLE NOT NULL," +
                    "date_hired DATE," +
                    "address VARCHAR(255)," +
                    "phone VARCHAR(20)," +
                    "tin_number VARCHAR(20)" +
                    ")";
            statement.executeUpdate(createEmployeeTable);
            
            // Create Payroll table
            String createPayrollTable = "CREATE TABLE IF NOT EXISTS payroll (" +
                    "payroll_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "employee_id VARCHAR(20)," +
                    "total_hours DOUBLE," +
                    "overtime DOUBLE," +
                    "wage DOUBLE," +
                    "date DATE," +
                    "tax_deductions DOUBLE," +
                    "sss_deduction DOUBLE," +
                    "philhealth_deduction DOUBLE," +
                    "pagibig_deduction DOUBLE," +
                    "net_pay DOUBLE," +
                    "FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE" +
                    ")";
            statement.executeUpdate(createPayrollTable);
            
            // Create Attendance table
            String createAttendanceTable = "CREATE TABLE IF NOT EXISTS attendance (" +
                    "attendance_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "employee_id VARCHAR(20)," +
                    "date DATE," +
                    "status VARCHAR(20)," +
                    "time_in TIME," +
                    "time_out TIME," +
                    "FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE" +
                    ")";
            statement.executeUpdate(createAttendanceTable);
            
            System.out.println("Database tables created successfully.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
    
    // Helper methods to interact with the database
    public boolean executeUpdate(String sql) {
        try {
            Statement statement = getConnection().createStatement();
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return false;
        }
    }
    
    public ResultSet executeQuery(String sql) {
        try {
            Statement statement = getConnection().createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return null;
        }
    }
}