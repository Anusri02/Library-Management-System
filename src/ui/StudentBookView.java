package ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class StudentBookView {

    private JPanel panel;
    private JTextField txtSearch;
    private JTable table;
    private DefaultTableModel model;

    private String studentId;

    private final String URL = "jdbc:mysql://localhost:3306/library";
    private final String USER = "root";
    private final String PASSWORD = "Anusri@02";

    public StudentBookView(String studentId) {

        this.studentId = studentId;

        panel = new JPanel(new BorderLayout(15,15));
        panel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        // ===== TITLE =====
        JLabel title = new JLabel("📚 Student Library Portal", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        panel.add(title, BorderLayout.NORTH);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new String[]{"ID","Title","Author","Publisher","Status"},0);

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Center align
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scroll = new JScrollPane(table);
        panel.add(scroll, BorderLayout.CENTER);

        // ===== SEARCH PANEL =====
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER,15,10));

        txtSearch = new JTextField(20);

        JButton btnSearch = createButton("🔍 Search", new Color(59,130,246));
        JButton btnBrowse = createButton("📚 Browse", new Color(34,197,94));
        JButton btnMyBooks = createButton("📖 My Books", new Color(168,85,247));

        bottom.add(new JLabel("Search: "));
        bottom.add(txtSearch);
        bottom.add(btnSearch);
        bottom.add(btnBrowse);
        bottom.add(btnMyBooks);

        panel.add(bottom, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        btnSearch.addActionListener(e -> searchBooks());
        btnBrowse.addActionListener(e -> loadAllBooks());
        btnMyBooks.addActionListener(e -> loadMyBooks());

        loadAllBooks();
    }

    public JPanel getPanel() {
        return panel;
    }

    private JButton createButton(String text, Color color){
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ===== ALL BOOKS =====
    private void loadAllBooks() {

        model.setRowCount(0);

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM books");

            while (rs.next()) {

                int qty = rs.getInt("quantity");

                String status = (qty > 0)
                        ? "Available (" + qty + ")"
                        : "Out of Stock";

                model.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        status
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, e.getMessage());
        }
    }

    // ===== SEARCH =====
    private void searchBooks() {

        model.setRowCount(0);

        try (Connection conn = getConnection()) {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?");

            ps.setString(1, "%" + txtSearch.getText() + "%");
            ps.setString(2, "%" + txtSearch.getText() + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int qty = rs.getInt("quantity");

                String status = (qty > 0)
                        ? "Available (" + qty + ")"
                        : "Out of Stock";

                model.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        status
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, e.getMessage());
        }
    }

    // ===== MY BOOKS (SECURE) =====
    private void loadMyBooks() {

        model.setRowCount(0);

        try (Connection conn = getConnection()) {

            String sql = "SELECT b.id, b.title, b.author, b.publisher " +
                    "FROM books b JOIN issues i ON b.id = i.book_id " +
                    "WHERE i.student_id = ? AND i.status='Issued'";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                model.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        "Issued"
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, e.getMessage());
        }
    }
}