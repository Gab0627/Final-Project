import java.sql.*;

public class TestAddEmployee {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/employeeData";
        String user = "root";
        String password = "YourPasswordHere";

        try (Connection myConn = DriverManager.getConnection(url, user, password)) {
            String sqlCommand = "SELECT e.Fname, e.Lname, e.email, e.Salary, jt.job_title " +
                                "FROM employees e " +
                                "JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                                "JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id " +
                                "ORDER BY e.Fname, e.Lname";

            Statement myStmt = myConn.createStatement();
            ResultSet myRS = myStmt.executeQuery(sqlCommand);

            System.out.println("\nEMPLOYEE LIST\n");
            System.out.println("First Name\tLast Name\tEmail\t\t\tSalary\tJob Title");
            System.out.println("----------\t---------\t-----\t\t\t------\t---------");

            while (myRS.next()) {
                String firstName = myRS.getString("e.Fname");
                String lastName = myRS.getString("e.Lname");
                String email = myRS.getString("e.email");
                double salary = myRS.getDouble("e.Salary");
                String jobTitle = myRS.getString("jt.job_title");

                System.out.printf("%-10s\t%-10s\t%-20s\t%.2f\t%s\n", firstName, lastName, email, salary, jobTitle);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: " + e.getLocalizedMessage());
        }
    }
}

