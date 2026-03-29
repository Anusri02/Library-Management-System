package LibraryManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentManagement {
    private JPanel panel;
    private JTextField txtStudentId, txtStudentName, txtCourse, txtEmail, txtPhone;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch;
    private JTable jTableStudents;
    private DefaultTableModel model;
    private String role;

    // Database connection details
    private final String URL = "jdbc:mysql://localhost:3306/library";
    private final String USER = "root";       // change if needed
    private final String PASSWORD = "Anusri@02";       // set your MySQL password

    public StudentManagement(String role) {
        this.role = role;
        panel = new JPanel(new BorderLayout(10,10));

        // ===== Form Panel =====
        JPanel formPanel = new JPanel(new GridLayout(5,2,10,10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));

        formPanel.add(new JLabel("Student ID:"));
        txtStudentId = new JTextField();
        formPanel.add(txtStudentId);

        formPanel.add(new JLabel("Name:"));
        txtStudentName = new JTextField();
        formPanel.add(txtStudentName);

        formPanel.add(new JLabel("Course:"));
        txtCourse = new JTextField();
        formPanel.add(txtCourse);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Phone:"));
        txtPhone = new JTextField();
        formPanel.add(txtPhone);

        // ===== Buttons Panel =====
        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear");
        btnSearch = new JButton("Search");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnSearch);

        // ===== Top Panel =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ===== Table Panel =====
        model = new DefaultTableModel(new String[]{"ID","Name","Course","Email","Phone"},0);
        jTableStudents = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(jTableStudents);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Student Records"));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // ===== Role Restriction =====
        if (role.equalsIgnoreCase("student")) {
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }

        // ===== Button Actions =====
        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearFields());
        btnSearch.addActionListener(e -> searchStudent());

        // ===== Load data from DB =====
        loadStudentsFromDB(role);
    }

    public JPanel getPanel() {
        return panel;
    }

    // ===== Database Connection =====
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ===== Load Students =====
    private void loadStudentsFromDB(String role) {
        model.setRowCount(0);
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            if (role.equalsIgnoreCase("faculty")) {
                ResultSet rs = stmt.executeQuery("SELECT id, name, course, email, phone FROM students");
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("course"),
                            rs.getString("email"),
                            rs.getString("phone")
                    });
                }
            }
            // students see nothing until they search
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error loading data: " + e.getMessage());
        }
    }

    // ===== Add Student =====
    private void addStudent() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO students (id, name, course, email, phone) VALUES (?,?,?,?,?)")) {
            ps.setString(1, txtStudentId.getText());
            ps.setString(2, txtStudentName.getText());
            ps.setString(3, txtCourse.getText());
            ps.setString(4, txtEmail.getText());
            ps.setString(5, txtPhone.getText());
            ps.executeUpdate();
            loadStudentsFromDB(role);
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error adding student: " + e.getMessage());
        }
    }

    // ===== Update Student =====
    private void updateStudent() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE students SET name=?, course=?, email=?, phone=? WHERE id=?")) {
            ps.setString(1, txtStudentName.getText());
            ps.setString(2, txtCourse.getText());
            ps.setString(3, txtEmail.getText());
            ps.setString(4, txtPhone.getText());
            ps.setString(5, txtStudentId.getText());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                loadStudentsFromDB(role);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(panel, "Student ID not found!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error updating student: " + e.getMessage());
        }
    }

    // ===== Delete Student =====
    private void deleteStudent() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE id=?")) {
            ps.setString(1, txtStudentId.getText());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                loadStudentsFromDB(role);
                clearFields(); // ✅ clears fields after delete
            } else {
                JOptionPane.showMessageDialog(panel, "Student ID not found!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error deleting student: " + e.getMessage());
        }
    }

    // ===== Search Student =====
    private void searchStudent() {
        model.setRowCount(0); // clear table first
        try (Connection conn = getConnection()) {
            PreparedStatement ps;
            if (role.equalsIgnoreCase("student")) {
                // Students can search by ID OR Name
                if (!txtStudentId.getText().isEmpty()) {
                    ps = conn.prepareStatement("SELECT id, name, course, email, phone FROM students WHERE id=?");
                    ps.setString(1, txtStudentId.getText());
                } else if (!txtStudentName.getText().isEmpty()) {
                    ps = conn.prepareStatement("SELECT id, name, course, email, phone FROM students WHERE name=?");
                    ps.setString(1, txtStudentName.getText());
                } else {
                    JOptionPane.showMessageDialog(panel, "Enter Student ID or Name to search!");
                    return;
                }
            } else {
                // Faculty can search only by ID
                if (!txtStudentId.getText().isEmpty()) {
                    ps = conn.prepareStatement("SELECT id, name, course, email, phone FROM students WHERE id=?");
                    ps.setString(1, txtStudentId.getText());
                } else {
                    JOptionPane.showMessageDialog(panel, "Enter Student ID to search!");
                    return;
                }
            }

            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                txtStudentId.setText(rs.getString("id"));
                txtStudentName.setText(rs.getString("name"));
                txtCourse.setText(rs.getString("course"));
                txtEmail.setText(rs.getString("email"));
                txtPhone.setText(rs.getString("phone"));

                // Show in table
                model.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("course"),
                        rs.getString("email"),
                        rs.getString("phone")
                });
            }
            if (!found) {
                JOptionPane.showMessageDialog(panel, "No matching student found!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error searching student: " + e.getMessage());
        }
    }

    // ===== Clear Fields =====
    private void clearFields() {
        txtStudentId.setText("");
        txtStudentName.setText("");
        txtCourse.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        if (role.equalsIgnoreCase("faculty")) {
            loadStudentsFromDB(role); // refresh table for faculty
        } else {
            model.setRowCount(0); // clear table for students
        }
    }

    // ===== Test Run =====
    // ===== Test Run =====
public static void main(String[] args) {
    JFrame frame = new JFrame("Test Student Management");
    frame.setSize(900,600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ✅ closes app when window is closed
    frame.add(new StudentManagement("faculty").getPanel()); // try "faculty" or "student"
    frame.setVisible(true);
}
}
