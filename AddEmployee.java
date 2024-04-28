import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class AddEmployee extends JFrame {
    private JTextField firstNameField, lastNameField, emailField, jobTitleField;
    private JButton addButton;

    public AddEmployee() {
        setTitle("Add New Employee");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        panel.add(firstNameField);

        panel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        panel.add(lastNameField);

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Job Title:"));
        jobTitleField = new JTextField();
        panel.add(jobTitleField);

        addButton = new JButton("Add Employee");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });
        panel.add(new JPanel());
        panel.add(addButton);

        add(panel, BorderLayout.CENTER);
    }

    private void addEmployee() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String jobTitle = jobTitleField.getText();
        double salary = 50000.0;
    
        String url = "jdbc:mysql://localhost:3306/employeeData";
        String user = "root";
        String password = "YourPasswordHere";
    
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO employees (Fname, Lname, email, Salary) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setDouble(4, salary);
            int rowsAffected = statement.executeUpdate();
    
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int employeeId = generatedKeys.getInt(1);
    
                    sql = "SELECT job_title_id FROM job_titles WHERE job_title = ?";
                    statement = connection.prepareStatement(sql);
                    statement.setString(1, jobTitle);
                    ResultSet resultSet = statement.executeQuery();
                    int jobTitleId = -1;
                    if (resultSet.next()) {
                        jobTitleId = resultSet.getInt("job_title_id");
                    }
    
                    sql = "INSERT INTO employee_job_titles (empid, job_title_id) VALUES (?, ?)";
                    statement = connection.prepareStatement(sql);
                    statement.setInt(1, employeeId);
                    statement.setInt(2, jobTitleId);
                    statement.executeUpdate();
    
                    JOptionPane.showMessageDialog(this, "Employee added successfully!");
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Error retrieving generated employee ID.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error adding employee.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding employee: " + ex.getMessage());
        }
    }

    private void clearFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        jobTitleField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AddEmployee().setVisible(true);
            }
        });
    }
}
