package LibraryManagement;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Login extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRole;

    public Login() {

        setTitle("Library Management System");

        // FULL SCREEN WINDOW
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Background Image
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/LibraryImages/Login3.png"));
        Image img = bgIcon.getImage().getScaledInstance(1920,1080,Image.SCALE_SMOOTH);

        JLabel background = new JLabel(new ImageIcon(img));
        background.setLayout(new GridBagLayout());
        setContentPane(background);

        // GLASS LOGIN CARD
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(420,480));
        card.setBackground(new Color(255,255,255,220));
        card.setLayout(null);

        background.add(card);

        // TITLE
        JLabel title = new JLabel("Library Login");
        title.setFont(new Font("Segoe UI",Font.BOLD,32));
        title.setBounds(110,30,250,40);
        card.add(title);

        // USERNAME
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI",Font.PLAIN,14));
        lblUser.setBounds(60,100,200,20);
        card.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(60,125,300,38);
        txtUsername.setFont(new Font("Segoe UI",Font.PLAIN,14));
        card.add(txtUsername);

        // PASSWORD
        JLabel lblPass = new JLabel("Password");
        lblPass.setBounds(60,175,200,20);
        card.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(60,200,300,38);
        card.add(txtPassword);

        txtPassword.addActionListener(e -> loginUser());

        // ROLE
        JLabel lblRole = new JLabel("Role");
        lblRole.setBounds(60,250,200,20);
        card.add(lblRole);

        comboRole = new JComboBox<>(new String[]{"student","faculty"});
        comboRole.setBounds(60,275,300,38);
        card.add(comboRole);

        // LOGIN BUTTON
        JButton btnLogin = new JButton("LOGIN");
        btnLogin.setBounds(60,335,300,45);
        btnLogin.setBackground(new Color(33,150,243));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI",Font.BOLD,16));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.add(btnLogin);

        btnLogin.addActionListener(e -> loginUser());

        // CLEAR BUTTON
        JButton btnClear = new JButton("CLEAR");
        btnClear.setBounds(60,395,300,35);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.add(btnClear);

        btnClear.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
        });

        setVisible(true);
    }


    // LOGIN METHOD (same as yours)
    private void loginUser() {

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = comboRole.getSelectedItem().toString();

        if(username.isEmpty() || password.isEmpty()){

            JOptionPane.showMessageDialog(this,
                    "Please enter username and password",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        String sql = "SELECT * FROM users WHERE username=? AND password=? AND role=?";

        try(Connection con = DBConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(sql)){

            pst.setString(1,username);
            pst.setString(2,password);
            pst.setString(3,role);

            ResultSet rs = pst.executeQuery();

            if(rs.next()){

                JOptionPane.showMessageDialog(this,"Login Successful");

                new Dashboard(role);

                dispose();

            }else{

                JOptionPane.showMessageDialog(this,"Invalid Username or Password");

            }

        }catch(Exception e){

            JOptionPane.showMessageDialog(this,
                    "Database Error : "+e.getMessage());

        }
    }

    public static void main(String[] args) {

        if(DBConnection.getConnection()!=null)
            System.out.println("Database Connected");
        else
            System.out.println("Database Not Connected");

        new Login();
    }
}