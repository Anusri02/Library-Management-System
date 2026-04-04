package service;

import dao.StudentDAO;

public class StudentService {

    StudentDAO dao = new StudentDAO();

    public void addStudent(String id, String name, String course, String email, String phone) {

        dao.addStudent(id, name, course, email, phone);
    }
}