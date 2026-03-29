package LibraryManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import com.toedter.calendar.JDateChooser;

public class ReturnManagement {

    private JPanel panel;

    private JTextField txtIssueId, txtBookId, txtStudentId, txtFine;
    private JDateChooser returnDate;

    private JTable table;
    private DefaultTableModel model;

    Connection con;

    public ReturnManagement(String role){

        con = DBConnection.getConnection();

        panel = new JPanel(new BorderLayout(10,10));

        // ================= FORM =================

        JPanel form = new JPanel(new GridLayout(5,2,10,10));
        form.setBorder(BorderFactory.createTitledBorder("Return Book"));

        txtIssueId = new JTextField();
        txtBookId = new JTextField();
        txtStudentId = new JTextField();
        txtFine = new JTextField();

        returnDate = new JDateChooser();

        form.add(new JLabel("Issue ID"));
        form.add(txtIssueId);

        form.add(new JLabel("Book ID"));
        form.add(txtBookId);

        form.add(new JLabel("Student ID"));
        form.add(txtStudentId);

        form.add(new JLabel("Return Date"));
        form.add(returnDate);

        form.add(new JLabel("Fine"));
        form.add(txtFine);

        // ================= BUTTONS =================

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER,15,10));

        JButton btnSearch = new JButton("Search");
        JButton btnReturn = new JButton("Return Book");
        JButton btnClear = new JButton("Clear");

        buttons.add(btnSearch);
        buttons.add(btnReturn);
        buttons.add(btnClear);

        JPanel topPanel = new JPanel(new BorderLayout());

        topPanel.add(form,BorderLayout.CENTER);
        topPanel.add(buttons,BorderLayout.SOUTH);

        panel.add(topPanel,BorderLayout.NORTH);

        // ================= TABLE =================

        model = new DefaultTableModel(
                new String[]{"ReturnID","IssueID","BookID","StudentID","ReturnDate","Fine"},0);

        table = new JTable(model);

        JScrollPane scroll = new JScrollPane(table);

        panel.add(scroll,BorderLayout.CENTER);

        // ================= BUTTON ACTIONS =================
        if(role.equalsIgnoreCase("student")){
    btnReturn.setEnabled(false);
}

        btnSearch.addActionListener(e -> searchIssue());
        btnReturn.addActionListener(e -> returnBook());
        btnClear.addActionListener(e -> clear());

        loadTable();
    }

    public JPanel getPanel(){
        return panel;
    }

    // ================= SEARCH ISSUE =================

    private void searchIssue(){

        try{

            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM issues WHERE issue_id=? AND status='Issued'");

            pst.setString(1,txtIssueId.getText());

            ResultSet rs = pst.executeQuery();

            if(rs.next()){

                txtBookId.setText(rs.getString("book_id"));
                txtStudentId.setText(rs.getString("student_id"));

                Date dueDate = rs.getDate("due_date");
                Date today = new Date();

                long diff = today.getTime() - dueDate.getTime();

                long days = TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);

                int fine = 0;

                if(days > 0)
                    fine = (int)days * 5;

                txtFine.setText(String.valueOf(fine));

            }
            else{

                JOptionPane.showMessageDialog(panel,"Issue ID not found");

            }

        }
        catch(Exception e){

            System.out.println(e);

        }
    }

    // ================= RETURN BOOK =================

    private void returnBook(){

        try{

            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO return_books(issue_id,book_id,student_id,return_date,fine_amount) VALUES(?,?,?,?,?)");

            pst.setString(1,txtIssueId.getText());
            pst.setString(2,txtBookId.getText());
            pst.setString(3,txtStudentId.getText());

            pst.setDate(4,new java.sql.Date(returnDate.getDate().getTime()));

            pst.setInt(5,Integer.parseInt(txtFine.getText()));

            pst.executeUpdate();

            pst = con.prepareStatement(
                    "UPDATE issues SET status='Returned' WHERE issue_id=?");

            pst.setString(1,txtIssueId.getText());

            pst.executeUpdate();

            pst = con.prepareStatement(
                    "UPDATE books SET quantity = quantity + 1 WHERE id=?");

            pst.setString(1,txtBookId.getText());

            pst.executeUpdate();

            JOptionPane.showMessageDialog(panel,"Book Returned Successfully");
            PreparedStatement log = con.prepareStatement(
"INSERT INTO activity_log(user_role,action,details) VALUES(?,?,?)");

log.setString(1,"Faculty");
log.setString(2,"Return Book");
log.setString(3,"Book "+txtBookId.getText()+" returned by Student "+txtStudentId.getText());

log.executeUpdate();

            loadTable();
            clear();

        }
        catch(Exception e){

            System.out.println(e);

        }

    }

    // ================= LOAD TABLE =================

    private void loadTable(){

        try{

            model.setRowCount(0);

            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM return_books");

            ResultSet rs = pst.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{

                        rs.getInt("return_id"),
                        rs.getString("issue_id"),
                        rs.getString("book_id"),
                        rs.getString("student_id"),
                        rs.getDate("return_date"),
                        rs.getInt("fine_amount")

                });

            }

        }
        catch(Exception e){

            System.out.println(e);

        }

    }

    // ================= CLEAR =================

    private void clear(){

        txtIssueId.setText("");
        txtBookId.setText("");
        txtStudentId.setText("");
        txtFine.setText("");
        returnDate.setDate(null);

    }

}