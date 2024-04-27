import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EmployeeManagementGUI extends JFrame {
    private JTextField searchField;
    private JTextField addNameField;
    private JTextField updateNameField;
    private JButton searchButton;
    private JButton addButton;
    private JButton updateButton;
    private JTextArea resultArea;

    private Connection connection;
    private JTextComponent addLnameField;
    private JTextComponent addFnameField;
    private JTextComponent addHireDateField;
    private JTextComponent addEmailField;
    private JTextComponent addSalaryField;
    private JTextComponent addSSNField;

    public EmployeeManagementGUI() {
        // Set up the GUI components
        createComponents();

        // Initialize database connection
        initializeDBConnection();

        // Add action listeners for the buttons
        addActionListeners();
    }
   
    private void createComponents() {
        // Layout and components setup
        searchField = new JTextField(20);
        addNameField = new JTextField(20);
        updateNameField = new JTextField(20);
        addFnameField = new JTextField(20); // Add this line
        addLnameField = new JTextField(20); // Add this line
        addEmailField = new JTextField(20); // Add this line
        addHireDateField = new JTextField(20); // Add this line
        addSalaryField = new JTextField(20); // Add this line
        addSSNField = new JTextField(20); // Add this line
        searchButton = new JButton("Search");
        addButton = new JButton("Add Employee");
        updateButton = new JButton("Update Employee");
        resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);

        setLayout(new FlowLayout());
        add(new JLabel("Search:"));
        add(searchField);
        add(searchButton);
        add(new JLabel(""))
        add(new JLabel("Add Name:"));
        add(addNameField);
        add(addButton);
        add(new JLabel("Update Name:"));
        add(updateNameField);
        add(updateButton);
        add(new JScrollPane(resultArea));
    }

    private void initializeDBConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/employeeData";
            String user = "root";
            String password = "Shwetaben_0627";

            // Ensure the JDBC driver is loaded
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish a connection to the database
            connection = DriverManager.getConnection(url, user, password);
            resultArea.setText("Connected to the database successfully.");
        } catch (ClassNotFoundException e) {
            resultArea.setText("MySQL JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            resultArea.setText("Failed to connect to the database. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void addActionListeners() {
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEmployee();
            }
        });
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();

        if (searchTerm.isEmpty()) {
            resultArea.setText("Please enter a search term.");
            return;
        }
    
        try {
            // Create a PreparedStatement with a parameterized query to avoid SQL injection
            // String sql = "SELECT * FROM employees WHERE empid LIKE CONCAT('%', ?, '%')";
            String sql;
            if (searchTerm.length() == 1 && Character.isDigit(searchTerm.charAt(0))) {
                // Search for exact matches if the search term is a single digit
                sql = "SELECT * FROM employees WHERE empid = ?";
            } else {
                // Use LIKE with wildcards for other search terms
                sql = "SELECT * FROM employees WHERE empid LIKE CONCAT('%', ?, '%')";
            }

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, searchTerm);
    
            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();
    
            // Process the results
            StringBuilder resultText = new StringBuilder();
            resultText.append("Search results:\n");
    
            while (resultSet.next()) {
                int id = resultSet.getInt("empid");
                String name = resultSet.getString("Fname");
                // Add more columns as needed
    
                resultText.append("ID: ").append(id).append(", Name: ").append(name).append("\n");
                // Append more columns as needed
            }
    
            if (resultText.length() == 0) {
                resultText.append("No results found.");
            }
    
            resultArea.setText(resultText.toString());
    
            // Close the ResultSet and PreparedStatement
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            resultArea.setText("Error executing search query: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addEmployee() {
        String fname = addFnameField.getText().trim();
        String lname = addLnameField.getText().trim();
        String email = addEmailField.getText().trim();
        String hireDate = addHireDateField.getText().trim();
        double salary = Double.parseDouble(addSalaryField.getText().trim());
        String ssn = addSSNField.getText().trim();
    
        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || hireDate.isEmpty() || ssn.isEmpty()) {
            resultArea.setText("Please enter all required information for the new employee.");
            return;
        }
        
    
        try {
            String sql = "INSERT INTO employee (Fname, Lname, email, HireDate, Salary, SSN) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, fname);
            preparedStatement.setString(2, lname);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, hireDate);
            preparedStatement.setDouble(5, salary);
            preparedStatement.setString(6, ssn);
    
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                resultArea.setText("Employee added successfully.");
            } else {
                resultArea.setText("Failed to add employee.");
            }
    
            preparedStatement.close();
        } catch (SQLException e) {
            resultArea.setText("Error adding employee: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    

    private void updateEmployee() {
        // Update employee implementation
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                EmployeeManagementGUI frame = new EmployeeManagementGUI();
                frame.setTitle("Employee Management System");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
