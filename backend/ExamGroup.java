package backend;

public class ExamGroup {
    private String department;
    private String division;
    private String subject;
    private String studentFilePath;

    public ExamGroup(String department, String division, String subject, String studentFilePath) {
        this.department = department;
        this.division = division;
        this.subject = subject;
        this.studentFilePath = studentFilePath;
    }

    public String getDepartment() { return department; }
    public String getDivision() { return division; }
    public String getSubject() { return subject; }
    public String getStudentFilePath() { return studentFilePath; }
}