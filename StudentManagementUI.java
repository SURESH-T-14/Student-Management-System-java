package com.example.sms;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentManagementUI extends JFrame {

    private final StudentDAO dao = new StudentDAO();
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Age"}, 0);
    private JTextField idField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField ageField;

    public StudentManagementUI() {
        setTitle("Student Management System - Swing UI");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(5, 2));
        idField = new JTextField();
        nameField = new JTextField();
        emailField = new JTextField();
        ageField = new JTextField();

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");
        JButton clearBtn = new JButton("Clear");

        form.add(new JLabel("ID (for update/delete):"));
        form.add(idField);
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Age:"));
        form.add(ageField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(clearBtn);

        add(form, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String email = emailField.getText();
                int age = Integer.parseInt(ageField.getText());
                if (name.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields!");
                    return;
                }
                dao.addStudent(new Student(0, name, email, age));
                JOptionPane.showMessageDialog(this, "Student added successfully!");
                clearFields();
                loadTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid age format!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        updateBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                String email = emailField.getText();
                int age = Integer.parseInt(ageField.getText());
                if (name.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields!");
                    return;
                }
                dao.updateStudent(new Student(id, name, email, age));
                JOptionPane.showMessageDialog(this, "Student updated successfully!");
                clearFields();
                loadTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input format!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        deleteBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?");
                if (confirm == JOptionPane.YES_OPTION) {
                    dao.deleteStudent(id);
                    JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                    clearFields();
                    loadTable();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid ID!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        refreshBtn.addActionListener(e -> loadTable());

        clearBtn.addActionListener(e -> clearFields());

        loadTable();
    }

    private void loadTable() {
        try {
            List<Student> list = dao.getAllStudents();
            tableModel.setRowCount(0);
            for (Student s : list) {
                tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getEmail(), s.getAge()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        ageField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentManagementUI().setVisible(true);
        });
    }
}
