
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RemoveEmployee extends JFrame implements ActionListener {

    private Choice cEmpId;
    private JButton delete, back;
    private JLabel lblname, lblphone, lblemail;
    private Connection connection;

    public RemoveEmployee(Connection connection) {
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        // Employee ID Label and Choice
        JLabel labelempId = new JLabel("Employee Id");
        labelempId.setBounds(50, 50, 100, 30);
        add(labelempId);

        cEmpId = new Choice();
        cEmpId.setBounds(200, 50, 150, 30);
        add(cEmpId);

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select * from employee");
            while (rs.next()) {
                cEmpId.add(rs.getString("empId"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Labels for displaying selected employee information
        lblname = new JLabel();
        lblname.setBounds(200, 100, 200, 30);
        add(lblname);

        lblphone = new JLabel();
        lblphone.setBounds(200, 150, 200, 30);
        add(lblphone);

        lblemail = new JLabel();
        lblemail.setBounds(200, 200, 200, 30);
        add(lblemail);

        // Update displayed data when a new employee ID is selected
        cEmpId.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                try {
                    String empId = cEmpId.getSelectedItem();
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery("select * from employee where empId = '" + empId + "'");
                    if (rs.next()) {
                        lblname.setText("Name: " + rs.getString("name"));
                        lblphone.setText("Phone: " + rs.getString("phone"));
                        lblemail.setText("Email: " + rs.getString("email"));
                    } else {
                        lblname.setText("Name: ");
                        lblphone.setText("Phone: ");
                        lblemail.setText("Email: ");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Delete Button
        delete = new JButton("Delete");
        delete.setBounds(50, 250, 100, 30);
        delete.setBackground(Color.BLACK);
        delete.setForeground(Color.WHITE);
        delete.addActionListener(this);
        add(delete);

        // Back Button
        back = new JButton("Back");
        back.setBounds(250, 250, 100, 30);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        add(back);

        // Frame properties
        setSize(400, 350);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == delete) {
            try (Connection connection = this.connection; // Use try-with-resources to manage connection
                     Statement stmt = connection.createStatement()) {
                String empId = cEmpId.getSelectedItem();
                stmt.executeUpdate("delete from employee where empId = '" + empId + "'");
                JOptionPane.showMessageDialog(this, "Employee Deleted Successfully");
                cEmpId.remove(empId);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting employee");
                e.printStackTrace();
            }
        } else if (ae.getSource() == back) {
            this.dispose(); // Close the RemoveEmployee frame
        }
    }
}
