public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Philippine Payroll Management System...");
        
        // Initialize database connection
        try {
            System.out.println("Initializing database connection...");
            DatabaseConfig.getInstance();
            System.out.println("Database initialization complete.");
        } catch (Exception e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Initialize and show the GUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            new PayrollGui();
        });
    }
}