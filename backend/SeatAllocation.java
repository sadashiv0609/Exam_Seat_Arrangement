package backend;

public class SeatAllocation {
    private int benchNo;
    private Student student1;
    private Student student2;

    public SeatAllocation(int benchNo, Student student1, Student student2) {
        this.benchNo = benchNo;
        this.student1 = student1;
        this.student2 = student2;
    }

    public Student getStudent1() { return student1; }
    public Student getStudent2() { return student2; }

    public String toJson() {
        String roll1 = (student1 != null) ? student1.getRollNo() : "-";
        String dept1 = (student1 != null) ? student1.getDepartment() : "";
        
        String roll2 = (student2 != null) ? student2.getRollNo() : "-";
        String dept2 = (student2 != null) ? student2.getDepartment() : "";
        
        return String.format(
            "{\"benchNo\":%d,\"rollNo1\":\"%s\",\"dept1\":\"%s\",\"rollNo2\":\"%s\",\"dept2\":\"%s\"}",
            benchNo, roll1, dept1, roll2, dept2
        );
    }
}