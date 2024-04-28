
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

    private JTextField updateFnameField;
    private JTextField updateLnameField;
    private JTextField updateEmailField;
    private JTextField updateJobTitleField;
    private JTextField updateSalaryField;
    private JTextField updateSSNField;

    private Connection connection;
    private JTextComponent addFnameField;
    private JTextComponent addLnameField;
    private JTextComponent addEmailField;
    private JTextComponent addHireDateField;
    private JTextComponent addSalaryField;
    private JTextComponent addSSNField;
    JComboBox<String> employmentTypeComboBox;
    private JTextField jobTitleField;

    public EmployeeManagementGUI() {
        // Set up the GUI components
        createComponents();

        // Initialize database connection
        initializeDBConnection();

        // Add action listeners for the buttons
        addActionListeners();

        setTitle("Employee Management System");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createComponents() {
        // Main panel setup
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel setup
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Add employee panel setup
        JPanel addPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        addPanel.setBorder(BorderFactory.createTitledBorder("Add Employee"));
        addFnameField = new JTextField(20);
        addLnameField = new JTextField(20);
        addEmailField = new JTextField(20);
        addHireDateField = new JTextField(20);
        addSalaryField = new JTextField(20);
        addSSNField = new JTextField(20);
        jobTitleField = new JTextField(20);

        addButton = new JButton("Add Employee");
        addPanel.add(new JLabel("First Name:"));
        addPanel.add(addFnameField);
        addPanel.add(new JLabel("Last Name:"));
        addPanel.add(addLnameField);
        addPanel.add(new JLabel("Email:"));
        addPanel.add(addEmailField);
        addPanel.add(new JLabel("Hire Date:"));
        addPanel.add(addHireDateField);

        addPanel.add(new JLabel("Job Title:"));
        addPanel.add(jobTitleField);

        addPanel.add(new JLabel("Salary:"));
        addPanel.add(addSalaryField);
        addPanel.add(new JLabel("SSN:"));
        addPanel.add(addSSNField);
        employmentTypeComboBox = new JComboBox<>(new String[]{"Full-time", "Part-time"});
        addPanel.add(new JLabel("Employment Type:"));
        addPanel.add(employmentTypeComboBox);

        addPanel.add(addButton);

        // Update employee panel setup
        JPanel updatePanel = new JPanel();
        updatePanel.setLayout(new GridLayout(7, 2, 10, 10)); // 7 rows for labels and fields, 2 columns
        updatePanel.setBorder(BorderFactory.createTitledBorder("Update Employee"));

        // Create and add components for updating employee details
        updateNameField = new JTextField(20);
        JTextField updateLnameField = new JTextField(20);
        JTextField updateEmailField = new JTextField(20);
        JTextField updateJobTitleField = new JTextField(20);
        JTextField updateSalaryField = new JTextField(20);
        JTextField updateSSNField = new JTextField(20); // For identification, not for update

        updateButton = new JButton("Update Employee");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateEmployee(); // This method will handle the logic to update the employee
            }
        });

        // Adding components to the update panel
        updatePanel.add(new JLabel("First Name:"));
        updatePanel.add(updateNameField);
        updatePanel.add(new JLabel("Last Name:"));
        updatePanel.add(updateLnameField);
        updatePanel.add(new JLabel("Email:"));
        updatePanel.add(updateEmailField);
        updatePanel.add(new JLabel("Job Title:"));
        updatePanel.add(updateJobTitleField);
        updatePanel.add(new JLabel("Salary:"));
        updatePanel.add(updateSalaryField);
        updatePanel.add(new JLabel("SSN (for lookup):"));
        updatePanel.add(updateSSNField);

        // Add button at the end
        updatePanel.add(new JLabel("")); // Placeholder for alignment
        updatePanel.add(updateButton);

        // Result area setup
        resultArea = new JTextArea(10, 50);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Results"));

        // Remove employee panel setup
        JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField removeField = new JTextField(20);
        JButton removeButton = new JButton("Remove Employee");
        removePanel.add(new JLabel("Employee ID:"));
        removePanel.add(removeField);
        removePanel.add(removeButton);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeEmployee(removeField.getText().trim());
            }
        });

        //exit button 
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Create a control panel for bottom controls like the exit button
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(exitButton);

        // Adding panels to main panel
        mainPanel.add(searchPanel);
        mainPanel.add(addPanel);
        mainPanel.add(updatePanel);
        mainPanel.add(scrollPane);
        mainPanel.add(controlPanel);
        mainPanel.add(removePanel);

        // Setting the main panel to the JFrame
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        //Remove Employee Frame
    }

    private void initializeDBConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/employeeData";
            String user = "root";
            String password = "Pword";

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
                            // + "JOIN employee_job_titles ON employees.empid = employee_job_titles.empid "
                            // + "JOIN job_titles ON job_titles.job_title_id = employee_job_titles.job_title_id "
                            // + "JOIN employee_division ON employees.empid = employee_division.empid "
                            // + "JOIN division ON division.ID = employee_division.div_ID "
                            + "WHERE employees.SSN = ?";  // Assuming SSN is stored as 9 digits or formatted accordingly
                } else {
                    sql = "SELECT * FROM employees "
                            // + "JOIN employee_job_titles ON employees.empid = employee_job_titles.empid "
                            // + "JOIN job_titles ON job_titles.job_title_id = employee_job_titles.job_title_id "
                            // + "JOIN employee_division ON employees.empid = employee_division.empid "
                            // + "JOIN division ON division.ID = employee_division.div_ID "
                            + "WHERE employees.empid = ?";
                }
            } else if (searchTerm.matches("\\d{3}-?\\d{2}-?\\d{4}")) {
                String formattedSSN = searchTerm.replaceAll("-", "");
                searchTerm = formattedSSN;
                sql = "SELECT * FROM employees "
                        // + "JOIN employee_job_titles ON employees.empid = employee_job_titles.empid "
                        // + "JOIN job_titles ON job_titles.job_title_id = employee_job_titles.job_title_id "
                        // + "JOIN employee_division ON employees.empid = employee_division.empid "
                        // + "JOIN division ON division.ID = employee_division.div_ID "
                        + "WHERE employees.SSN = ?";
            } else if (searchTerm.matches("^[a-zA-Z]+$")) {
                // Use LIKE for name searches
                sql = "SELECT * FROM employees "
                        // + "JOIN employee_job_titles ON employees.empid = employee_job_titles.empid "
                        // + "JOIN job_titles ON job_titles.job_title_id = employee_job_titles.job_title_id "
                        // + "JOIN employee_division ON employees.empid = employee_division.empid "
                        // + "JOIN division ON division.ID = employee_division.div_ID "
                        + "WHERE Fname LIKE CONCAT('%', ?, '%') OR Lname LIKE CONCAT('%', ?, '%')";
            } else {
                sql = "SELECT * FROM employees WHERE 1 = 0"; // This will return no results
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
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
                    // String jobTitle = resultSet.getString("job_title");
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
                            // .append("\nTitle: ")
                            // .append(jobTitle)
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
            }
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
        String jobTitle = jobTitleField.getText().trim();
        String employmentType = (String) employmentTypeComboBox.getSelectedItem();

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
            String sql = "INSERT INTO employees (Fname, Lname, email, HireDate, Salary, SSN, employment_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, fname);
            preparedStatement.setString(2, lname);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, hireDate);
            preparedStatement.setDouble(5, salary);
            preparedStatement.setString(6, ssn);
            preparedStatement.setString(7, employmentType.toLowerCase().equals("part-time") ? "part_time" : "full_time");
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int employeeId = generatedKeys.getInt(1);

                    sql = "SELECT job_title_id FROM job_titles WHERE job_title = ?";
                    preparedStatement = connection.prepareStatement(sql);

                    preparedStatement.setString(1, jobTitle);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    int jobTitleId = -1;
                    if (resultSet.next()) {
                        jobTitleId = resultSet.getInt("job_title_id");
                    }

                    sql = "INSERT INTO employee_job_titles (empid, job_title_id) VALUES (?, ?)";
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setInt(1, employeeId);
                    preparedStatement.setInt(2, jobTitleId);
                    preparedStatement.executeUpdate();
                    resultArea.setText("Employee added successfully.");
                } else {
                    resultArea.setText("Error retrieving generated employee ID.");
                }

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
        // Get the input values
        String empid = updateFnameField.getText().trim();
        String fname = updateFnameField.getText().trim();
        String lname = updateLnameField.getText().trim();
        String email = updateEmailField.getText().trim();
        String salaryText = updateSalaryField.getText().trim();
        String jobTitle = updateJobTitleField.getText().trim();
        String employmentType = (String) employmentTypeComboBox.getSelectedItem();
        String ssn = updateSSNField.getText().trim(); // You need to add this field to your GUI

        // Validation: Ensure all required fields are filled
        if (empid.isEmpty() || fname.isEmpty() || lname.isEmpty() || email.isEmpty() || salaryText.isEmpty() || ssn.isEmpty()) {
            resultArea.setText("Please enter all required information for the employee update.");
            return;
        }

        // Convert salary to double
        double salary;
        try {
            salary = Double.parseDouble(salaryText);
        } catch (NumberFormatException ex) {
            resultArea.setText("Please enter a valid salary.");
            return;
        }

        String sql = "UPDATE employee SET empid=?, Fname=?, Lname=?, email=?, Salary=?, JobTitle=?, employment_type=? WHERE SSN=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, empid);
            preparedStatement.setString(2, fname);
            preparedStatement.setString(3, lname);
            preparedStatement.setString(4, email);
            preparedStatement.setDouble(5, salary);
            preparedStatement.setString(6, jobTitle);
            preparedStatement.setString(7, employmentType.equalsIgnoreCase("part-time") ? "part_time" : "full_time");
            preparedStatement.setString(8, ssn);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                resultArea.setText("Employee information updated successfully.");
            } else {
                resultArea.setText("No employee found with the given SSN, or no changes were made.");
            }
        } catch (SQLException e) {
            resultArea.setText("Error updating employee information: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //remove employees
    private void removeEmployee(String empId) {
        if (empId.isEmpty()) {
            resultArea.setText("Please enter an employee ID.");
            return;
        }

        try {
            String sql = "DELETE FROM employees WHERE empid = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(empId));

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                resultArea.setText("Employee removed successfully.");
            } else {
                resultArea.setText("No employee found with ID: " + empId);
            }

            preparedStatement.close();
        } catch (SQLException e) {
            resultArea.setText("Error removing employee: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            resultArea.setText("Employee ID must be a valid number.");
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
