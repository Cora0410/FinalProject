import java.time.LocalDate;

public class Employee {
    private String id;
    private String name;
    private double totalHours;
    private double overtime;
    private double wage;
    private LocalDate date;
    private double sssDeduction;
    private double philhealthDeduction;
    private double pagibigDeduction;
    private double taxDeduction;
    private double totalDeduction;
    private double grossPay;
    private double netPay;
    private String attendance;
    
    // Philippine deduction rates for 2025
    public static final double SSS_EMPLOYEE_RATE = 0.05;  // 5% (employee share only)
    public static final double SSS_MIN_MSC = 5000.0;      // Minimum Monthly Salary Credit
    public static final double SSS_MAX_MSC = 35000.0;     // Maximum Monthly Salary Credit
    
    public static final double PHILHEALTH_EMPLOYEE_RATE = 0.025;  // 2.5% (employee share only)
    public static final double PHILHEALTH_CAP = 100000.0;         // Monthly compensation cap
    
    public static final double PAGIBIG_EMPLOYEE_RATE = 0.02;  // 2% (employee share only)
    public static final double PAGIBIG_CAP = 10000.0;        // Monthly compensation cap (updated 2024)
    
    public static class DeductionCalculation {
        public double sssDeduction;
        public double philhealthDeduction;
        public double pagibigDeduction;
        public double taxDeduction;
        
        public DeductionCalculation(double sss, double philhealth, double pagibig, double tax) {
            this.sssDeduction = sss;
            this.philhealthDeduction = philhealth;
            this.pagibigDeduction = pagibig;
            this.taxDeduction = tax;
        }
        
        public double getTotalDeduction() {
            return sssDeduction + philhealthDeduction + pagibigDeduction + taxDeduction;
        }
    }
    
    public Employee() {
        this.date = LocalDate.now(); // Default to current date
    }
    
    public Employee(String id, String name, double totalHours, double overtime, 
                   double wage, LocalDate date, 
                   double sssDeduction, double philhealthDeduction,
                   double pagibigDeduction, double taxDeduction,
                   String attendance) {
        this.id = id;
        this.name = name;
        this.totalHours = totalHours;
        this.overtime = overtime;
        this.wage = wage;
        this.date = date != null ? date : LocalDate.now();
        this.sssDeduction = sssDeduction;
        this.philhealthDeduction = philhealthDeduction;
        this.pagibigDeduction = pagibigDeduction;
        this.taxDeduction = taxDeduction;
        this.attendance = attendance;
        
        // Calculate derived fields
        calculateDerivedFields();
    }
    
    public static DeductionCalculation calculateDeductions(double monthlyWage) {
        if (monthlyWage <= 0) {
            return new DeductionCalculation(0, 0, 0, 0);
        }
        
        // SSS Deduction (5% employee share of MSC, range ₱5,000 to ₱35,000)
        double sssBase = Math.max(SSS_MIN_MSC, Math.min(monthlyWage, SSS_MAX_MSC));
        double sssDeduction = sssBase * SSS_EMPLOYEE_RATE;
        
        // PhilHealth Deduction (2.5% employee share, cap at ₱100,000)
        double philhealthBase = Math.min(monthlyWage, PHILHEALTH_CAP);
        double philhealthDeduction = philhealthBase * PHILHEALTH_EMPLOYEE_RATE;
        
        // Pag-IBIG Deduction (2% employee share, cap at ₱10,000)
        double pagibigBase = Math.min(monthlyWage, PAGIBIG_CAP);
        double pagibigDeduction = pagibigBase * PAGIBIG_EMPLOYEE_RATE;
        
        // Income Tax calculation based on TRAIN Law
        double taxDeduction = calculateIncomeTax(monthlyWage);
        
        return new DeductionCalculation(sssDeduction, philhealthDeduction, pagibigDeduction, taxDeduction);
    }
    
    private static double calculateIncomeTax(double monthlyWage) {
        // Convert monthly to annual for tax calculation
        double annualSalary = monthlyWage * 12;
        
        double annualTax = 0;
        
        if (annualSalary <= 250000) {
            // Tax-exempt
            annualTax = 0;
        } else if (annualSalary <= 400000) {
            // 15% of excess over ₱250,000
            annualTax = (annualSalary - 250000) * 0.15;
        } else if (annualSalary <= 800000) {
            // ₱22,500 + 20% of excess over ₱400,000
            annualTax = 22500 + (annualSalary - 400000) * 0.20;
        } else if (annualSalary <= 2000000) {
            // ₱102,500 + 25% of excess over ₱800,000
            annualTax = 102500 + (annualSalary - 800000) * 0.25;
        } else if (annualSalary <= 8000000) {
            // ₱402,500 + 30% of excess over ₱2,000,000
            annualTax = 402500 + (annualSalary - 2000000) * 0.30;
        } else {
            // ₱2,202,500 + 35% of excess over ₱8,000,000
            annualTax = 2202500 + (annualSalary - 8000000) * 0.35;
        }
        
        // Convert back to monthly tax
        return annualTax / 12;
    }
    
    private void calculateDerivedFields() {
        this.totalDeduction = sssDeduction + philhealthDeduction + pagibigDeduction + taxDeduction;
        this.grossPay = wage;
        this.netPay = grossPay - totalDeduction;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getTotalHours() {
        return totalHours;
    }
    
    public void setTotalHours(double totalHours) {
        this.totalHours = totalHours;
        calculateDerivedFields();
    }
    
    public double getOvertime() {
        return overtime;
    }
    
    public void setOvertime(double overtime) {
        this.overtime = overtime;
        calculateDerivedFields();
    }
    
    public double getWage() {
        return wage;
    }
    
    public void setWage(double wage) {
        this.wage = wage;
        calculateDerivedFields();
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public double getSssDeduction() {
        return sssDeduction;
    }
    
    public void setSssDeduction(double sssDeduction) {
        this.sssDeduction = sssDeduction;
        calculateDerivedFields();
    }
    
    public double getPhilhealthDeduction() {
        return philhealthDeduction;
    }
    
    public void setPhilhealthDeduction(double philhealthDeduction) {
        this.philhealthDeduction = philhealthDeduction;
        calculateDerivedFields();
    }
    
    public double getPagibigDeduction() {
        return pagibigDeduction;
    }
    
    public void setPagibigDeduction(double pagibigDeduction) {
        this.pagibigDeduction = pagibigDeduction;
        calculateDerivedFields();
    }
    
    public double getTaxDeduction() {
        return taxDeduction;
    }
    
    public void setTaxDeduction(double taxDeduction) {
        this.taxDeduction = taxDeduction;
        calculateDerivedFields();
    }
    
    public double getTotalDeduction() {
        return totalDeduction;
    }
    
    public double getGrossPay() {
        return grossPay;
    }
    
    public double getNetPay() {
        return netPay;
    }
    
    public String getAttendance() {
        return attendance;
    }
    
    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }
}