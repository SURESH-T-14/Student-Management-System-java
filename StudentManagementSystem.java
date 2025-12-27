package com.example.sms;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class StudentManagementSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentDAO dao = new StudentDAO();

    public static void main(String[] args) {
        System.out.println("Welcome to Student Management System");
        int choice;
        do {
            System.out.println("\n1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. View Student By Id");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = Integer.parseInt(scanner.nextLine());

            try {
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> viewAll();
                    case 3 -> viewById();
                    case 4 -> updateStudent();
                    case 5 -> deleteStudent();
                    case 0 -> System.out.println("Goodbye!");
                    default -> System.out.println("Invalid choice");
                }
            } catch (SQLException e) {
                System.err.println("Database Error: " + e.getMessage());
            }
        } while (choice != 0);
    }

    private static void addStudent() throws SQLException {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter age: ");
        int age = Integer.parseInt(scanner.nextLine());

        Student s = new Student(0, name, email, age);
        dao.addStudent(s);
        System.out.println("Student added successfully.");
    }

    private static void viewAll() throws SQLException {
        List<Student> list = dao.getAllStudents();
        for (Student s : list) {
            System.out.println(s);
        }
    }

    private static void viewById() throws SQLException {
        System.out.print("Enter id: ");
        int id = Integer.parseInt(scanner.nextLine());
        Student s = dao.getStudentById(id);
        if (s != null) {
            System.out.println(s);
        } else {
            System.out.println("Student not found");
        }
    }

    private static void updateStudent() throws SQLException {
        System.out.print("Enter id: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter age: ");
        int age = Integer.parseInt(scanner.nextLine());

        Student s = new Student(id, name, email, age);
        dao.updateStudent(s);
        System.out.println("Student updated successfully.");
    }

    private static void deleteStudent() throws SQLException {
        System.out.print("Enter id: ");
        int id = Integer.parseInt(scanner.nextLine());
        dao.deleteStudent(id);
        System.out.println("Student deleted successfully.");
    }
}
