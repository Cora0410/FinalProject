import java.time.LocalDate;

public class Employee {
    private String id;
    private String name;
    private double totalHours;
    private double overtime;
    private double wage;
    private LocalDate date;
    private double taxDeductions;
    private String attendance;
    
    public Employee() {}
    
    public Employee(String id, String name, double totalHours, double overtime, 
                   double wage, LocalDate date, double taxDeductions, String attendance) {
        this.id = id;
        this.name = name;
        this.totalHours = totalHours;
        this.overtime = overtime;
        this.wage = wage;
        this.date = date;
        this.taxDeductions = taxDeductions;
        this.attendance = attendance;
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
    }
    
    public double getOvertime() {
        return overtime;
    }
    
    public void setOvertime(double overtime) {
        this.overtime = overtime;
    }
    
    public double getWage() {
        return wage;
    }
    
    public void setWage(double wage) {
        this.wage = wage;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public double getTaxDeductions() {
        return taxDeductions;
    }
    
    public void setTaxDeductions(double taxDeductions) {
        this.taxDeductions = taxDeductions;
    }
    
    public String getAttendance() {
        return attendance;
    }
    
    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }
}