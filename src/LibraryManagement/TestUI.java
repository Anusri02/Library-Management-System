
package LibraryManagement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TestUI extends JFrame {
    public TestUI() {
        setTitle("Test Layout");
        setSize(600,400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(2,2));
        formPanel.add(new JLabel("ID:"));
        formPanel.add(new JTextField());
        formPanel.add(new JLabel("Name:"));
        formPanel.add(new JTextField());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JButton("Add"));
        buttonPanel.add(new JButton("Update"));

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID","Name"},0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        new TestUI();
    }
}

