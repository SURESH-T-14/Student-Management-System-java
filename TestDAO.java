package com.example.sms;

import java.sql.SQLException;
import java.util.List;

public class TestDAO {
    public static void main(String[] args) {
        try {
            StudentDAO dao = new StudentDAO();
            
            System.out.println("=== All Students ===");
            List<Student> students = dao.getAllStudents();
            for (Student s : students) {
                System.out.println("ID: " + s.getId() + ", Name: " + s.getName() + 
                                 ", Email: " + s.getEmail() + ", Age: " + s.getAge());
            }
            
            System.out.println("\n=== Get Student by ID (1) ===");
            Student student = dao.getStudentById(1);
            if (student != null) {
                System.out.println("Found: " + student.getName() + " (" + student.getEmail() + ")");
            }
            
            System.out.println("\n=== Add New Student ===");
            Student newStudent = new Student();
            newStudent.setName("Divya Nair");
            newStudent.setEmail("divya@example.com");
            newStudent.setAge(23);
            dao.addStudent(newStudent);
            System.out.println("Student added successfully!");
            
            System.out.println("\n=== All Students After Insert ===");
            students = dao.getAllStudents();
            System.out.println("Total students: " + students.size());
            for (Student s : students) {
                System.out.println("ID: " + s.getId() + ", Name: " + s.getName());
            }
            
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
