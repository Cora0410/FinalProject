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