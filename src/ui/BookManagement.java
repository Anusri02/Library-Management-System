package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookManagement {

    private JPanel panel;
    private JTextField txtBookId, txtTitle, txtAuthor, txtPublisher, txtQuantity;
    private JComboBox<String> cmbAvailability;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch;
    private JTable jTableBooks;
    private DefaultTableModel model;
    private String role;

    private final String URL = "jdbc:mysql://localhost:3306/library";
    private final String USER = "root";
    private final String PASSWORD = "Anusri@02";

    public BookManagement(String role) {
        this.role = role;
        panel = new JPanel(new BorderLayout(10,10));

        // ===== FORM PANEL (SAME UI, JUST ONE EXTRA FIELD) =====
        JPanel formPanel = new JPanel(new GridLayout(6,2,10,10));
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

        // ✅ ONLY NEW FIELD
        formPanel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        formPanel.add(txtQuantity);

        // ===== BUTTON PANEL =====
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

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ===== TABLE =====
        model = new DefaultTableModel(
            new String[]{"ID","Title","Author","Publisher","Availability","Quantity"},0);

        jTableBooks = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(jTableBooks);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Book Records"));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // ===== ROLE CONTROL =====
        if (role.equalsIgnoreCase("student")) {
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        }

        // ===== ACTIONS =====
        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnClear.addActionListener(e -> clearFields());
        btnSearch.addActionListener(e -> searchBook());

        loadBooksFromDB(role);
    }

    public JPanel getPanel() {
        return panel;
    }

    // ===== CONNECTION =====
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ===== LOAD =====
    private void loadBooksFromDB(String role) {
        model.setRowCount(0);
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(
                "SELECT id, title, author, publisher, availability, quantity FROM books");

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("publisher"),
                    rs.getString("availability"),
                    rs.getInt("quantity")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error loading data: " + e.getMessage());
        }
    }

    // ===== ADD =====
    private void addBook() {

        if(txtQuantity.getText().isEmpty()){
            JOptionPane.showMessageDialog(panel,"Enter Quantity");
            return;
        }

        int quantity = Integer.parseInt(txtQuantity.getText());

        new service.BookService().addBook(
            txtBookId.getText(),
            txtTitle.getText(),
            txtAuthor.getText(),
            txtPublisher.getText(),
            cmbAvailability.getSelectedItem().toString(),
            quantity
        );

        loadBooksFromDB(role);
        clearFields();
    }

    // ===== UPDATE =====
    private void updateBook() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE books SET title=?, author=?, publisher=?, availability=?, quantity=? WHERE id=?")) {

            ps.setString(1, txtTitle.getText());
            ps.setString(2, txtAuthor.getText());
            ps.setString(3, txtPublisher.getText());
            ps.setString(4, cmbAvailability.getSelectedItem().toString());
            ps.setInt(5, Integer.parseInt(txtQuantity.getText()));
            ps.setString(6, txtBookId.getText());

            ps.executeUpdate();

            loadBooksFromDB(role);
            clearFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error updating book: " + e.getMessage());
        }
    }

    // ===== DELETE =====
    private void deleteBook() {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE id=?")) {

            ps.setString(1, txtBookId.getText());
            ps.executeUpdate();

            loadBooksFromDB(role);
            clearFields();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error deleting book: " + e.getMessage());
        }
    }

    // ===== SEARCH =====
    private void searchBook() {
        model.setRowCount(0);

        try (Connection conn = getConnection()) {

            PreparedStatement ps = conn.prepareStatement(
                "SELECT id, title, author, publisher, availability, quantity FROM books WHERE id=?");

            ps.setString(1, txtBookId.getText());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                txtTitle.setText(rs.getString("title"));
                txtAuthor.setText(rs.getString("author"));
                txtPublisher.setText(rs.getString("publisher"));
                cmbAvailability.setSelectedItem(rs.getString("availability"));
                txtQuantity.setText(String.valueOf(rs.getInt("quantity")));

                model.addRow(new Object[]{
                    rs.getString("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("publisher"),
                    rs.getString("availability"),
                    rs.getInt("quantity")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, "Error searching book: " + e.getMessage());
        }
    }

    // ===== CLEAR =====
    private void clearFields() {
        txtBookId.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtPublisher.setText("");
        txtQuantity.setText("");
        cmbAvailability.setSelectedIndex(0);

        loadBooksFromDB(role);
    }

    // ===== TEST =====
    public static void main(String[] args) {
        JFrame frame = new JFrame("Book Management");
        frame.setSize(900,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new BookManagement("faculty").getPanel());
        frame.setVisible(true);
    }
}