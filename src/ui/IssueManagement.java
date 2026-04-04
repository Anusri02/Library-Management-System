package ui;
import util.DBConnection;
import util.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import com.toedter.calendar.JDateChooser;

public class IssueManagement {

    private JPanel panel;

    private JTextField txtIssueId, txtBookId, txtStudentId;
    private JDateChooser issueDate, dueDate;

    private JTable table;
    private DefaultTableModel model;

    Connection con;

    public IssueManagement(String role) {

        con = DBConnection.getConnection();

        panel = new JPanel(new BorderLayout(10,10));

        // ================= FORM =================

        JPanel form = new JPanel(new GridLayout(5,2,10,10));
        form.setBorder(BorderFactory.createTitledBorder("Issue Book"));

        txtIssueId = new JTextField();
        txtBookId = new JTextField();
        txtStudentId = new JTextField();

        issueDate = new JDateChooser();
        dueDate = new JDateChooser();

        form.add(new JLabel("Issue ID"));
        form.add(txtIssueId);

        form.add(new JLabel("Book ID"));
        form.add(txtBookId);

        form.add(new JLabel("Student ID"));
        form.add(txtStudentId);

        form.add(new JLabel("Issue Date"));
        form.add(issueDate);

        form.add(new JLabel("Due Date"));
        form.add(dueDate);

        // ================= BUTTONS =================

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER,15,10));

        JButton btnSearch = new JButton("Search");
        JButton btnIssue = new JButton("Issue Book");
        JButton btnClear = new JButton("Clear");

        buttons.add(btnSearch);
        buttons.add(btnIssue);
        buttons.add(btnClear);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(form,BorderLayout.CENTER);
        topPanel.add(buttons,BorderLayout.SOUTH);

        panel.add(topPanel,BorderLayout.NORTH);

        // ================= TABLE =================

        model = new DefaultTableModel(
                new String[]{"IssueID","BookID","StudentID","IssueDate","DueDate"},0);

        table = new JTable(model);

        JScrollPane scroll = new JScrollPane(table);

        panel.add(scroll,BorderLayout.CENTER);

        // ================= BUTTON ACTIONS =================

        btnIssue.addActionListener(e -> issueBook());
        btnClear.addActionListener(e -> clear());
        btnSearch.addActionListener(e -> searchIssue());
               if(role.equalsIgnoreCase("student")){
    btnIssue.setEnabled(false);
}


        loadTable();
    }

    public JPanel getPanel(){
        return panel;
    }

  /*  private void issueBook(){

        try{

            PreparedStatement pst = con.prepareStatement(
                    "SELECT quantity FROM books WHERE id=?");

            pst.setString(1, txtBookId.getText());

            ResultSet rs = pst.executeQuery();

            if(rs.next()){

                int qty = rs.getInt("quantity");

                if(qty <= 0){
                    JOptionPane.showMessageDialog(panel,"Book Out Of Stock");
                    return;
                }
            }

            pst = con.prepareStatement(
                    "INSERT INTO issues(issue_id,book_id,student_id,issue_date,due_date,status) VALUES(?,?,?,?,?,?)");

            pst.setString(1,txtIssueId.getText());
            pst.setString(2,txtBookId.getText());
            pst.setString(3,txtStudentId.getText());

            pst.setDate(4,new java.sql.Date(issueDate.getDate().getTime()));
            pst.setDate(5,new java.sql.Date(dueDate.getDate().getTime()));
            pst.setString(6,"Issued");

            pst.executeUpdate();

            pst = con.prepareStatement(
                    "UPDATE books SET quantity=quantity-1 WHERE id=?");

            pst.setString(1,txtBookId.getText());
            pst.executeUpdate();

            JOptionPane.showMessageDialog(panel,"Book Issued");

            loadTable();
            clear();

        } catch(Exception e){
            System.out.println(e);
        }
    }
*/
   private void issueBook(){

    try{

        String bookId = txtBookId.getText();
        String studentId = txtStudentId.getText();

        // ===== CHECK IF BOOK ALREADY ISSUED =====

        PreparedStatement pst = con.prepareStatement(
        "SELECT * FROM issues WHERE book_id=? AND student_id=? AND status='Issued'");

        pst.setString(1, bookId);
        pst.setString(2, studentId);

        ResultSet rs = pst.executeQuery();

        if(rs.next()){

            JOptionPane.showMessageDialog(panel,
            "This book is already issued to this student!");

            return;
        }

        // ===== CHECK BOOK STOCK =====

        pst = con.prepareStatement(
        "SELECT quantity FROM books WHERE id=?");

        pst.setString(1, bookId);

        rs = pst.executeQuery();

        if(rs.next()){

            int qty = rs.getInt("quantity");

            if(qty <= 0){

                JOptionPane.showMessageDialog(panel,
                "Book Out Of Stock");

                return;
            }

        }else{

            JOptionPane.showMessageDialog(panel,
            "Book ID not found");

            return;

        }

        // ===== ISSUE BOOK =====

        pst = con.prepareStatement(
        "INSERT INTO issues(issue_id,book_id,student_id,issue_date,due_date,status) VALUES(?,?,?,?,?,?)");

        pst.setString(1, txtIssueId.getText());
        pst.setString(2, bookId);
        pst.setString(3, studentId);

        pst.setDate(4,new java.sql.Date(issueDate.getDate().getTime()));
        pst.setDate(5,new java.sql.Date(dueDate.getDate().getTime()));

        pst.setString(6,"Issued");

        pst.executeUpdate();

        // ===== REDUCE BOOK QUANTITY =====

        pst = con.prepareStatement(
        "UPDATE books SET quantity = quantity - 1 WHERE id=?");

        pst.setString(1, bookId);

        pst.executeUpdate();

        JOptionPane.showMessageDialog(panel,
        "Book Issued Successfully");

        loadTable();
        clear();
        PreparedStatement log = con.prepareStatement(
"INSERT INTO activity_log(user_role,action,details) VALUES(?,?,?)");

log.setString(1,"Faculty");
log.setString(2,"Issue Book");
log.setString(3,"Book "+txtBookId.getText()+" issued to Student "+txtStudentId.getText());

log.executeUpdate();

    }

    catch(Exception e){

        System.out.println(e);

    }
}
    private void searchIssue(){

        try{

            model.setRowCount(0);

            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM issues WHERE issue_id=?");

            pst.setString(1, txtIssueId.getText());

            ResultSet rs = pst.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getString("issue_id"),
                        rs.getString("book_id"),
                        rs.getString("student_id"),
                        rs.getString("issue_date"),
                        rs.getString("due_date")
                });
            }

        } catch(Exception e){
            System.out.println(e);
        }
    }

    private void loadTable(){

        try{

            model.setRowCount(0);

            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM issues");

            ResultSet rs = pst.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{
                        rs.getString("issue_id"),
                        rs.getString("book_id"),
                        rs.getString("student_id"),
                        rs.getString("issue_date"),
                        rs.getString("due_date")
                });

            }

        } catch(Exception e){
            System.out.println(e);
        }
    }

    private void clear(){

        txtIssueId.setText("");
        txtBookId.setText("");
        txtStudentId.setText("");
        issueDate.setDate(null);
        dueDate.setDate(null);

    }
}