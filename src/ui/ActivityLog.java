package ui;
import util.DBConnection;
import util.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ActivityLog {

    private JPanel panel;
    private JTable table;
    private DefaultTableModel model;

    Connection con;

    public ActivityLog(String role){

        con = DBConnection.getConnection();

        panel = new JPanel(new BorderLayout(10,10));

        JLabel title = new JLabel("Activity Log",JLabel.CENTER);
        title.setFont(new Font("Segoe UI",Font.BOLD,22));

        panel.add(title,BorderLayout.NORTH);

        model = new DefaultTableModel(
        new String[]{"LogID","Role","Action","Details","Time"},0);

        table = new JTable(model);

        JScrollPane scroll = new JScrollPane(table);

        panel.add(scroll,BorderLayout.CENTER);

        loadLogs(role);

    }

    public JPanel getPanel(){
        return panel;
    }

    private void loadLogs(String role){

        try{

            model.setRowCount(0);

            PreparedStatement pst;

            if(role.equalsIgnoreCase("faculty")){

                pst = con.prepareStatement(
                "SELECT * FROM activity_log ORDER BY log_time DESC");

            }
            else{

                pst = con.prepareStatement(
                "SELECT * FROM activity_log WHERE user_role='Student' ORDER BY log_time DESC");

            }

            ResultSet rs = pst.executeQuery();

            while(rs.next()){

                model.addRow(new Object[]{

                        rs.getInt("log_id"),
                        rs.getString("user_role"),
                        rs.getString("action"),
                        rs.getString("details"),
                        rs.getTimestamp("log_time")

                });

            }

        }

        catch(Exception e){

            System.out.println(e);

        }

    }

}