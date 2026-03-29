package LibraryManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookManagement {
    private JPanel panel;
    private JTextField txtBookId, txtTitle, txtAuthor, txtPublisher;
    private JComboBox<String> cmbAvailability;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch;
    private JTable jTableBooks;
    private DefaultTableModel model;
    private String role;

    // Database connection details
    private final String URL = "jdbc:mysql://localhost:3306/library";
    private final String USER = "root";       // change if needed
    private final String PASSWORD = "Anusri@02";       // set your MySQL password

    public BookManagement(String role) {
        this.role = role;
        panel = new JPanel(new BorderLayout(10,10));

        // ===== Form Panel =====
        JPanel formPanel = new JPanel(new GridLayout(5,2,10,10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));

        formPanel.add(new JLabel("Book ID/ISBN:"));
        txtBookId = new JTextField();
        formPanel.add(txtBookId);

        formPanel.add(new JLabel("Title:"));
        txtTitle = new JTextField();
        formPanel.add(txtTitle);

        formPanel.add(new JLabel("Author:"));
        txtAuthor = new JTextField();
        formPanel.add(txtAuthor);

        formPanel.add(new JLabel("Publisher:"));
        txtPublisher = new JTextField();
        formPanel.add(txtPublisher);

        formPanel.add(new JLabel("Availability:"));
        cmbAvailability = new JComboBox<>(new String[]{"Available","Issued"});
        formPanel.add(cmbAvailability);

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
        model = new DefaultTableModel(new String[]{"ID","Title","Author","Publisher","Availability"},0);
        jTableBooks = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(jTableBooks);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Book Records"));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // ===== Role Restriction =====
        if (role.equalsIgnoreCase("student")) {
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }

        // ===== Button Actions =====
        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnClear.addActionListener(e -> clearFields());
        btnSearch.addActionListener(e -> searchBook());

        // ===== Load data from DB =====
        loadBooksFromDB(role);
    }

    public JPanel getPanel() {
        return panel;
    }

    // ===== Database Connection =====
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ===== Load Books =====
    private void loadBooksFromDB(String role) {
        model.setRowCount(0);
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            if (role.equalsIgnoreCase("faculty")) {
                ResultSet rs = stmt.executeQuery("SELECT id, title, author, publisher, availability FROM books");
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getString("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("publisher"),
                            rs.getString("availability")
                    });
                }
            }
            // students see nothing until they search
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error loading data: " + e.getMessage());
        }
    }

    // ===== Add Book =====
    private void addBook() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO books (id, title, author, publisher, availability) VALUES (?,?,?,?,?)")) {
            ps.setString(1, txtBookId.getText());
            ps.setString(2, txtTitle.getText());
            ps.setString(3, txtAuthor.getText());
            ps.setString(4, txtPublisher.getText());
            ps.setString(5, cmbAvailability.getSelectedItem().toString());
            ps.executeUpdate();
            loadBooksFromDB(role);
            clearFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error adding book: " + e.getMessage());
        }
    }

    // ===== Update Book =====
    private void updateBook() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE books SET title=?, author=?, publisher=?, availability=? WHERE id=?")) {
            ps.setString(1, txtTitle.getText());
            ps.setString(2, txtAuthor.getText());
            ps.setString(3, txtPublisher.getText());
            ps.setString(4, cmbAvailability.getSelectedItem().toString());
            ps.setString(5, txtBookId.getText());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                loadBooksFromDB(role);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(panel, "Book ID not found!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error updating book: " + e.getMessage());
        }
    }
        // ===== Delete Book =====
    private void deleteBook() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE id=?")) {
            ps.setString(1, txtBookId.getText());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                loadBooksFromDB(role);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(panel, "Book ID not found!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error deleting book: " + e.getMessage());
        }
    }

    // ===== Search Book =====
    private void searchBook() {
        model.setRowCount(0);
        try (Connection conn = getConnection()) {
            PreparedStatement ps;
            if (role.equalsIgnoreCase("student")) {
                // Students search by Title OR Author
                if (!txtTitle.getText().isEmpty()) {
                    ps = conn.prepareStatement("SELECT id, title, author, publisher, availability FROM books WHERE title=?");
                    ps.setString(1, txtTitle.getText());
                } else if (!txtAuthor.getText().isEmpty()) {
                    ps = conn.prepareStatement("SELECT id, title, author, publisher, availability FROM books WHERE author=?");
                    ps.setString(1, txtAuthor.getText());
                } else {
                    JOptionPane.showMessageDialog(panel, "Enter Title or Author to search!");
                    return;
                }
            } else {
                // Faculty search only by Book ID
                if (!txtBookId.getText().isEmpty()) {
                    ps = conn.prepareStatement("SELECT id, title, author, publisher, availability FROM books WHERE id=?");
                    ps.setString(1, txtBookId.getText());
                } else {
                    JOptionPane.showMessageDialog(panel, "Enter Book ID to search!");
                    return;
                }
            }

            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                txtBookId.setText(rs.getString("id"));
                txtTitle.setText(rs.getString("title"));
                txtAuthor.setText(rs.getString("author"));
                txtPublisher.setText(rs.getString("publisher"));
                cmbAvailability.setSelectedItem(rs.getString("availability"));

                model.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getString("availability")
                });
            }
            if (!found) {
                JOptionPane.showMessageDialog(panel, "No matching book found!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error searching book: " + e.getMessage());
        }
    }

    // ===== Clear Fields =====
    private void clearFields() {
        txtBookId.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtPublisher.setText("");
        cmbAvailability.setSelectedIndex(0);
                if (role.equalsIgnoreCase("faculty")) {
            loadBooksFromDB(role); // refresh table for faculty
        } else {
            model.setRowCount(0); // clear table for students
        }
    } // <-- closes clearFields()

    // ===== Test Run =====
    public static void main(String[] args) {
        JFrame frame = new JFrame("Test Book Management");
        frame.setSize(900,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new BookManagement("faculty").getPanel()); // or "student"
        frame.setVisible(true);
    }
} // <-- closes the class
           