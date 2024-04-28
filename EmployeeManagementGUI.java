
import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EmployeeManagementGUI extends JFrame {

    private JTextField searchField;
    private JButton searchButton;
    private JButton addButton;
    private JButton updateButton;
    private JTextArea resultArea;
    private JTextField updateNameField;

    private Connection connection;
    private JTextComponent addFnameField;
    private JTextComponent addLnameField;
    private JTextComponent addEmailField;
    private JTextComponent addHireDateField;
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
        addFnameField = new JTextField(20);
        updateNameField = new JTextField(20);
        addLnameField = new JTextField(20);
        addEmailField = new JTextField(20);
        addHireDateField = new JTextField(20);
        addSalaryField = new JTextField(20);
        addSSNField = new JTextField(20);
        searchButton = new JButton("Search");
        addButton = new JButton("Add Employee");
        updateButton = new JButton("Update Employee");
        resultArea = new JTextArea(20, 50);
        resultArea.setEditable(false);

        setLayout(new FlowLayout());
        add(new JLabel("Search:"));
        add(searchField);
        add(searchButton);
      
        add(new JLabel("First Name:"));
        add(addFnameField);
        add(new JLabel("Last Name:"));
        add(addLnameField);
        add(new JLabel("Email:"));
        add(addEmailField);
        add(new JLabel("Hire Date:"));
        add(addHireDateField);
        add(new JLabel("Salary:"));
        add(addSalaryField);
        add(new JLabel("SSN:"));
        add(addSSNField);
        add(addButton);

        add(updateNameField);
        add(updateButton);

        add(new JScrollPane(resultArea));

    // //Additional fields for updating employee information
    //     add(new JLabel("New First Name:"));
    //     add(addFnameField);
        
    //     add(new JLabel("New Last Name:"));
    //     add(addLnameField);
        
    //     add(new JLabel("New Email:"));
    //     add(addEmailField);
        
    //     add(new JLabel("New Hire Date:"));
    //     add(addHireDateField);
        
    //     add(new JLabel("New Salary:"));
    //     add(addSalaryField);
        
    //     add(new JLabel("New SSN:"));
    //     add(addSSNField);
        
    //     add(updateButton);
    //     add(new JScrollPane(resultArea));
    }


    private void initializeDBConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/employeeData";
            String user = "root";
            String password = "YourPasswordHere";

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
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim();

        if (searchTerm.isEmpty()) {
            resultArea.setText("Please enter a search term.");
            return;
        }

        try {
            // Decide on a query based on the input type; search by ID or name
            String sql;
            if (searchTerm.matches("\\d+")) { // Checks if the searchTerm is numeric
                if (searchTerm.length() == 9) {
                    // Handle 7-digit SSN (you might need to format or pad it as per your database requirements)
                    sql = "SELECT * FROM employees "
                            + "JOIN employee_job_titles ON employees.empid = employee_job_titles.empid "
                            + "JOIN job_titles ON job_titles.job_title_id = employee_job_titles.job_title_id "
                            // + "JOIN employee_division ON employees.empid = employee_division.empid "
                            // + "JOIN division ON division.ID = employee_division.div_ID "
                            + "WHERE employees.SSN = ?";  // Assuming SSN is stored as 9 digits or formatted accordingly
                } else {
                    sql = "SELECT * FROM employees "
                            + "JOIN employee_job_titles ON employees.empid = employee_job_titles.empid "
                            + "JOIN job_titles ON job_titles.job_title_id = employee_job_titles.job_title_id "
                            // + "JOIN employee_division ON employees.empid = employee_division.empid "
                            // + "JOIN division ON division.ID = employee_division.div_ID "
                            + "WHERE employees.empid = ?";
                }
            } else if (searchTerm.matches("\\d{3}-?\\d{2}-?\\d{4}")) {
                String formattedSSN = searchTerm.replaceAll("-", "");
                searchTerm = formattedSSN;
                sql = "SELECT * FROM employees "
                        + "JOIN employee_job_titles ON employees.empid = employee_job_titles.empid "
                        + "JOIN job_titles ON job_titles.job_title_id = employee_job_titles.job_title_id "
                        // + "JOIN employee_division ON employees.empid = employee_division.empid "
                        // + "JOIN division ON division.ID = employee_division.div_ID "
                        + "WHERE employees.SSN = ?";
            } else if (searchTerm.matches("^[a-zA-Z]+$")) {
                // Use LIKE for name searches
                sql = "SELECT * FROM employees "
                        + "JOIN employee_job_titles ON employees.empid = employee_job_titles.empid "
                        + "JOIN job_titles ON job_titles.job_title_id = employee_job_titles.job_title_id "
                        // + "JOIN employee_division ON employees.empid = employee_division.empid "
                        // + "JOIN division ON division.ID = employee_division.div_ID "
                        + "WHERE Fname LIKE CONCAT('%', ?, '%') OR Lname LIKE CONCAT('%', ?, '%')";
            } else {
                sql = "SELECT * FROM employees WHERE 1 = 0"; // This will return no results
            }

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, searchTerm);
            if (!searchTerm.matches("\\d+")) {
                preparedStatement.setString(2, searchTerm); // Set the second parameter for name search
            }

            // Execute the query
            ResultSet resultSet = preparedStatement.executeQuery();

            // Process the results
            StringBuilder resultText = new StringBuilder("Search results:\n");
            boolean found = false;
            while (resultSet.next()) {
                int id = resultSet.getInt("empid");
                String fname = resultSet.getString("Fname");
                String lname = resultSet.getString("Lname");
                String jobTitle = resultSet.getString("job_title");
                // String division =  resultSet.getString("Name");
                // String divisionCity =  resultSet.getString("city");
                // String divisionState =  resultSet.getString("state");
                // String divisionCountry =  resultSet.getString("country");
                String email = resultSet.getString("email");
                // float employeeSalary = resultSet.getFloat("Salary");
                String SSN = resultSet.getString("SSN");
                // Add more fields as needed

                resultText
                        .append("ID: ")
                        .append(id)
                        .append("\nFirst Name: ")
                        .append(fname)
                        .append("\nLast Name: ")
                        .append(lname)
                        .append("\nTitle: ")
                        .append(jobTitle)
                        // .append("\nDivision: ")
                        // .append(division)
                        // .append(", ")
                        // .append(divisionCity)
                        // .append(", ")
                        // .append(divisionState)
                        // .append(", ")
                        // .append(divisionCountry)
                        .append("\nEmail: ")
                        .append(email)
                        // .append("\nSalary: $")
                        // .append(employeeSalary)
                        .append("\nSSN: ")
                        .append(SSN)
                        .append("\n\n");
                found = true;
            }

            if (!found) {
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
        String salaryText = addSalaryField.getText().trim();
        String ssn = addSSNField.getText().trim();

        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || hireDate.isEmpty() || salaryText.isEmpty() || ssn.isEmpty()) {
            resultArea.setText("Please enter all required information for the new employee.");
            return;
        }
      
        double salary;
        try {
            salary = Double.parseDouble(salaryText);
        } catch (NumberFormatException ex) {
            resultArea.setText("Please enter a valid salary.");
            return;
        }
      
        try {
            String sql = "INSERT INTO employees (Fname, Lname, email, HireDate, Salary, SSN) VALUES (?, ?, ?, ?, ?, ?)";
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
  
    // Update employee implementation
  
  private void updateEmployee() {
    String updateName = updateNameField.getText().trim();
    
    if (updateName.isEmpty()) {
        resultArea.setText("Please enter the name of the employee you want to update.");
        return;
    }

    try {
        // Create a PreparedStatement with a parameterized query to avoid SQL injection
        String sql = "SELECT * FROM employees WHERE Fname LIKE CONCAT('%', ?, '%') OR Lname LIKE CONCAT('%', ?, '%')";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, updateName);
        preparedStatement.setString(2, updateName);

        // Execute the query
        ResultSet resultSet = preparedStatement.executeQuery();

        // Process the results
        StringBuilder resultText = new StringBuilder();
        resultText.append("Search results for updating:\n");

        while (resultSet.next()) {
            int id = resultSet.getInt("empid");
            String name = resultSet.getString("Fname");
            String lname = resultSet.getString("Lname");
            String email = resultSet.getString("email");
            String hireDate = resultSet.getString("hiredate");
            double salary = resultSet.getDouble("salary");

            // Display current employee information
            resultText.append("ID: ").append(id).append(", Name: ").append(name).append(" ").append(lname).append("\n");
            resultText.append("Email: ").append(email).append("\n");
            resultText.append("Hire Date: ").append(hireDate).append("\n");
            resultText.append("Salary: ").append(salary).append("\n");

            // Provide editable fields for updating employee information
            JTextField newNameField = new JTextField(name, 20);
            JTextField newLnameField = new JTextField(lname, 20);
            JTextField newEmailField = new JTextField(email, 20);
            JTextField newHireDateField = new JTextField(hireDate, 20);
            JTextField newSalaryField = new JTextField(String.valueOf(salary), 20);

            // Display editable fields
            //error still need to work on these cannot edit gui not sure why 
            resultText.append("Enter new information:\n");
            resultText.append("New First Name: ").append(newNameField.getText()).append("\n");
            resultText.append("New Last Name: ").append(newLnameField.getText()).append("\n");
            resultText.append("New Email: ").append(newEmailField.getText()).append("\n");
            resultText.append("New Hire Date: ").append(newHireDateField.getText()).append("\n");
            resultText.append("New Salary: ").append(newSalaryField.getText()).append("\n");
        }

        if (resultText.length() == 0) {
            resultText.append("No matching employee found for updating.");
        }

        resultArea.setText(resultText.toString());

        // Close the ResultSet and PreparedStatement
        resultSet.close();
        preparedStatement.close();
    } catch (SQLException e) {
        resultArea.setText("Error executing search query for update: " + e.getMessage());
        e.printStackTrace();
    }
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