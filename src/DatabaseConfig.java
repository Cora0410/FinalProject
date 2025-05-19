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
    private String password = "ciansmdb851_*!";
    
    private static DatabaseConfig instance;
    
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
    
    public Connection getConnection() {
        Connection connection = null;
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
            
            // Connect to the database
            String dbUrl = "jdbc:mariadb://" + host + ":" + port + "/" + databaseName;
            connection = DriverManager.getConnection(dbUrl, username, password);
            
            // If this is the first time connecting, create tables
            if (!tablesExist(connection)) {
                createTables(connection);
            }
            
            return connection;
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
            return null;
        }
    }
    
    private boolean tablesExist(Connection connection) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "employees", null);
            return tables.next();
        } catch (SQLException e) {
            System.err.println("Error checking if tables exist: " + e.getMessage());
            return false;
        }
    }
    
    private void createTables(Connection connection) {
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
    
    // Helper methods to interact with the database with proper connection handling
    public boolean executeUpdate(String sql) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            return false;
        } finally {
            closeResources(stmt, conn);
        }
    }
    
    public ResultSet executeQuery(String sql) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            closeResources(stmt, conn);
            return null;
        }
    }
    
    // Test connection and display connection status
    public boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            return conn != null;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    
    // Helper method to close resources
    public static void closeResources(Statement stmt, Connection conn) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing statement: " + e.getMessage());
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    public static void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing result set: " + e.getMessage());
            }
        }
        closeResources(stmt, conn);
    }
}