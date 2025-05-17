import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Try to load MySQL/MariaDB JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Make sure you have the MySQL connector JAR in your classpath.");
            JOptionPane.showMessageDialog(null, 
                "MySQL JDBC Driver not found. Please add mysql-connector-java to your classpath.", 
                "Driver Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Validate database connection
        DatabaseConfig dbConfig = DatabaseConfig.getInstance();
        if (dbConfig.validateConnection()) {
            // Initialize and show the GUI
            SwingUtilities.invokeLater(() -> new PayrollGui());
        } else {
            // Show database configuration dialog
            // This would typically be a form to update database settings
            System.out.println("Please update your database configuration in config.properties and restart the application.");
        }
    }
}