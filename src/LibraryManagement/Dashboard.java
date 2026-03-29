package LibraryManagement;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Dashboard extends JFrame {

    private String role;
    private JPanel content;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    public Dashboard(String role) {

        this.role = role;
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
        welcome.setFont(new Font("Segoe UI",Font.PLAIN,15));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFocusPainted(false);
        btnLogout.setBackground(new Color(220,53,69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        add(sidebar,BorderLayout.WEST);

        // ================= MAIN CONTENT =================

        content = new JPanel(new BorderLayout());
        content.setBackground(new Color(241,245,249));

        JPanel cards = new JPanel(new GridLayout(2,3,30,30));
        cards.setBorder(BorderFactory.createEmptyBorder(40,40,40,40));
        cards.setBackground(new Color(241,245,249));

        int totalBooks = getCount("SELECT COUNT(*) FROM books");
        int totalStudents = getCount("SELECT COUNT(*) FROM students");
        int issuedBooks = getCount("SELECT COUNT(*) FROM issues WHERE status='Issued'");
        int returnedBooks = getCount("SELECT COUNT(*) FROM return_books");
        int overdueBooks = getCount("SELECT COUNT(*) FROM issues WHERE due_date < CURDATE() AND status='Issued'");

        Color accent = new Color(59,130,246);

        cards.add(createCard("Total Books",String.valueOf(totalBooks),accent));
        cards.add(createCard("Total Students",String.valueOf(totalStudents),accent));
        cards.add(createCard("Books Issued",String.valueOf(issuedBooks),accent));
        cards.add(createCard("Books Returned",String.valueOf(returnedBooks),accent));
        cards.add(createCard("Overdue Books",String.valueOf(overdueBooks),accent));

        content.add(cards,BorderLayout.CENTER);
        add(content,BorderLayout.CENTER);

        // ================= BUTTON ACTIONS =================

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

        btnLogout.addActionListener(e -> {
            new Login();
            dispose();
        });

        setVisible(true);
    }

    // ================= DATABASE COUNT =================

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

    // ================= PANEL SWITCH =================

    private void showPanel(JPanel panel){

        content.removeAll();
        content.add(panel,BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    // ================= SIDEBAR BUTTON =================

    private JButton createMenuButton(String text){

        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(30,41,59));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI",Font.PLAIN,15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12,20,12,20));

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

    // ================= DASHBOARD CARD =================

    private JPanel createCard(String title,String value,Color color){

        JPanel card = new JPanel(){

            protected void paintComponent(Graphics g){

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0,0,0,30));
                g2.fillRoundRect(6,6,getWidth()-6,getHeight()-6,20,20);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0,0,getWidth()-6,getHeight()-6,20,20);

                super.paintComponent(g);
            }
        };

        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel topBar = new JPanel();
        topBar.setBackground(color);
        topBar.setPreferredSize(new Dimension(100,6));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI",Font.BOLD,15));
        lblTitle.setForeground(new Color(100,100,100));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI",Font.BOLD,34));
        lblValue.setForeground(new Color(30,41,59));
        lblValue.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(topBar,BorderLayout.NORTH);
        card.add(lblTitle,BorderLayout.CENTER);
        card.add(lblValue,BorderLayout.SOUTH);

        return card;
    }

    public static void main(String[] args) {

        new Dashboard("faculty");

    }
}