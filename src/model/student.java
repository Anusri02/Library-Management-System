package model;

public class Student {

    private String id;
    private String name;
    private String course;
    private String email;
    private String phone;

    public Student(String id, String name, String course, String email, String phone) {
        this.id = id;
        this.name = name;
        this.course = course;
        this.email = email;
        this.phone = phone;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCourse() { return course; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}