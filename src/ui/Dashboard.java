package ui;

import util.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Dashboard extends JFrame {

    private String role;
    private String userId; // 🔥 IMPORTANT
    private JPanel content;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    public Dashboard(String role, String userId) {

        this.role = role;
        this.userId = userId; // 🔥 store student id
        con = DBConnection.getConnection();

        setTitle("Library Management System - Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= HEADER =================

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30,41,59));
        header.setPreferredSize(new Dimension(1200,65));

        JLabel title = new JLabel("  Library Management System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI",Font.BOLD,20));

        JLabel welcome = new JLabel("Welcome, "+role.toUpperCase()+"   ");
        welcome.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(220,53,69));
        btnLogout.setForeground(Color.WHITE);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.add(welcome);
        right.add(btnLogout);

        header.add(title,BorderLayout.WEST);
        header.add(right,BorderLayout.EAST);

        add(header,BorderLayout.NORTH);

        // ================= SIDEBAR =================

        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(15,23,42));
        sidebar.setPreferredSize(new Dimension(220,600));

        content = new JPanel(new BorderLayout());

        // ================= FACULTY =================
        if(role.equalsIgnoreCase("faculty")){

            sidebar.setLayout(new GridLayout(7,1,0,10));

            JButton btnStudents = createMenuButton("Student Management");
            JButton btnBooks = createMenuButton("Book Management");
            JButton btnIssue = createMenuButton("Issue Management");
            JButton btnReturn = createMenuButton("Return Book");
            JButton btnLog = createMenuButton("Activity Log");

            sidebar.add(new JLabel());
            sidebar.add(btnStudents);
            sidebar.add(btnBooks);
            sidebar.add(btnIssue);
            sidebar.add(btnReturn);
            sidebar.add(btnLog);

            content.add(createDashboardCards(),BorderLayout.CENTER);

            btnStudents.addActionListener(e ->
                    showPanel(new StudentManagement(role).getPanel())
            );

            btnBooks.addActionListener(e ->
                    showPanel(new BookManagement(role).getPanel())
            );

            btnIssue.addActionListener(e ->
                    showPanel(new IssueManagement(role).getPanel())
            );

            btnReturn.addActionListener(e ->
                    showPanel(new ReturnManagement(role).getPanel())
            );

            btnLog.addActionListener(e ->
                    showPanel(new ActivityLog(role).getPanel())
            );
        }

        // ================= STUDENT =================
        else{

            sidebar.setLayout(new GridLayout(3,1,0,10));

            JButton btnBooks = createMenuButton("📚 Library");

            sidebar.add(new JLabel());
            sidebar.add(btnBooks);

            // Default student screen
            content.add(new StudentBookView(userId).getPanel(), BorderLayout.CENTER);

            btnBooks.addActionListener(e ->
                    showPanel(new StudentBookView(userId).getPanel())
            );
        }

        add(sidebar,BorderLayout.WEST);
        add(content,BorderLayout.CENTER);

        // Logout
        btnLogout.addActionListener(e -> {
            new Login();
            dispose();
        });

        setVisible(true);
    }

    // ================= DASHBOARD CARDS =================

    private JPanel createDashboardCards(){

        JPanel cards = new JPanel(new GridLayout(2,2,30,30));
        cards.setBorder(BorderFactory.createEmptyBorder(40,40,40,40));
        cards.setBackground(new Color(241,245,249));

        int totalBooks = getCount("SELECT COUNT(*) FROM books");
        int totalStudents = getCount("SELECT COUNT(*) FROM students");
        int issuedBooks = getCount("SELECT COUNT(*) FROM issues WHERE status='Issued'");
        int returnedBooks = getCount("SELECT COUNT(*) FROM return_books");

        cards.add(createCard("Total Books",String.valueOf(totalBooks)));
        cards.add(createCard("Total Students",String.valueOf(totalStudents)));
        cards.add(createCard("Books Issued",String.valueOf(issuedBooks)));
        cards.add(createCard("Books Returned",String.valueOf(returnedBooks)));

        return cards;
    }

    private int getCount(String query){
        int count = 0;
        try{
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            if(rs.next())
                count = rs.getInt(1);
        }catch(Exception e){
            System.out.println(e);
        }
        return count;
    }

    private void showPanel(JPanel panel){
        content.removeAll();
        content.add(panel,BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    private JButton createMenuButton(String text){

        JButton button = new JButton(text);
        button.setBackground(new Color(30,41,59));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI",Font.PLAIN,15));

        button.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent evt){
                button.setBackground(new Color(59,130,246));
            }
            public void mouseExited(java.awt.event.MouseEvent evt){
                button.setBackground(new Color(30,41,59));
            }
        });

        return button;
    }

    private JPanel createCard(String title,String value){

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);

        lblTitle.setFont(new Font("Segoe UI",Font.BOLD,15));
        lblValue.setFont(new Font("Segoe UI",Font.BOLD,30));

        card.add(lblTitle,BorderLayout.CENTER);
        card.add(lblValue,BorderLayout.SOUTH);

        return card;
    }

    public static void main(String[] args) {
        new Dashboard("faculty", ""); // test
    }
}