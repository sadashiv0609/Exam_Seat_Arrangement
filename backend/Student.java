package backend;

public class Student {
    private String rollNo;
    private String name;
    private String department;
    private String division;

    public Student(String rollNo, String name, String department, String division) {
        this.rollNo = rollNo;
        this.name = name;
        this.department = department;
        this.division = division;
    }

    public String getRollNo() { return rollNo; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getDivision() { return division; }
}