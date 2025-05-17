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
    private String password = "123";
    
    private static DatabaseConfig instance;
    private Connection connection;
    
    private DatabaseConfig() {
        loadConfig();
        // Initialize connection immediately
        getConnection();
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
    
    public Connection getConnection() {
        if (connection == null) {
            try {
                // Load JDBC driver explicitly - helps on Windows
                try {
                    Class.forName("org.mariadb.jdbc.Driver");
                    System.out.println("MariaDB JDBC driver loaded successfully");
                } catch (ClassNotFoundException e) {
                    try {
                        // Fall back to MySQL driver if MariaDB driver isn't available
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        System.out.println("MySQL JDBC driver loaded successfully");
                    } catch (ClassNotFoundException e2) {
                        System.err.println("Neither MariaDB nor MySQL JDBC driver found. Please ensure drivers are in classpath.");
                        JOptionPane.showMessageDialog(null, 
                            "JDBC driver not found. Please ensure MariaDB or MySQL JDBC driver is in your classpath.",
                            "Driver Error", JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                }
                
                setupDatabase();
            } catch (SQLException e) {
                System.err.println("Database connection error: " + e.getMessage());
                String errorMessage = "Database connection error: " + e.getMessage() + "\n\n";
                String solution = "Please ensure:\n" +
                    "1. MariaDB is installed and running\n" +
                    "2. Username and password in config.properties are correct\n" +
                    "3. No firewall is blocking port " + port + "\n" +
                    "4. MariaDB is accepting connections from localhost";
                
                // Windows-specific suggestions
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    solution += "\n\nWindows-specific troubleshooting:\n" +
                        "5. Check Services.msc to verify MariaDB service is running\n" +
                        "6. Try using 127.0.0.1 instead of localhost in config.properties\n" +
                        "7. Verify no other process is using port " + port;
                }
                
                JOptionPane.showMessageDialog(null, 
                    errorMessage + solution,
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return connection;
    }
    
    private void setupDatabase() throws SQLException {
        try {
            // First test if we can connect to the server
            String serverUrl = "jdbc:mariadb://" + host + ":" + port;
            connection = DriverManager.getConnection(serverUrl, username, password);
            System.out.println("Connected to MariaDB server successfully.");
            
            // Create database if it doesn't exist
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName);
            
            // Close the connection to server
            connection.close();
            
            // Connect to the specific database
            String dbUrl = serverUrl + "/" + databaseName;
            connection = DriverManager.getConnection(dbUrl, username, password);
            System.out.println("Connected to database: " + databaseName);
            
            // Create tables
            createTables();
            
            System.out.println("Database connection successful. Database and tables ready.");
        } catch (SQLException e) {
            System.err.println("MariaDB Connection Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "MariaDB Database error: " + e.getMessage() + "\n\n" +
                "Check that MariaDB is running and your credentials are correct.",
                "MariaDB Connection Error", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
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
    
    // Test connection and display connection status
    public boolean testConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                System.out.println("Database connection is active.");
                return true;
            } else {
                System.out.println("Database connection is not active. Attempting to reconnect...");
                connection = null;
                return getConnection() != null;
            }
        } catch (SQLException e) {
            System.err.println("Error testing connection: " + e.getMessage());
            return false;
        }
    }
}