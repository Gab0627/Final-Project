import java.sql.*;
import java.util.ArrayList;

public class ShowEmployees {
    public static void main(String[] args)  {
        String url = "jdbc:mysql://localhost:3306/employeeData";
        String user = "root";
        String password = "chin";
        StringBuilder output = new StringBuilder( "" );
        String sqlcommand = "SELECT e.Fname, e.Lname, e.email, jt.job_title, p.pay_date, p.earnings, "+
                "p.fed_tax, p.fed_med, p.fed_SS, p.state_tax, p.retire_401k, p.health_care " + 
                "FROM employees e " + 
                "JOIN employee_job_titles ejt ON e.empid = ejt.empid " + 
                "JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id " +  
                "JOIN payroll p ON e.empid = p.empid " +
                "ORDER BY p.pay_date ;";
        
        try ( Connection myConn = DriverManager.getConnection( url, user, password ) ) 
        {
            Statement myStmt = myConn.createStatement( );

            output.append( "\nEMPLOYEE REPORT\n\nNAME \t\t\tEMAIL\t\t\tTITLE\t\t\tPAY DATE\tGROSS\tFederal\tFedMed\tFedSS\tState\t401K\tHealth$\n" );
            ResultSet myRS = myStmt.executeQuery( sqlcommand );
            while ( myRS.next () ) 
            {
                output.append(myRS.getString( "e.Fname" )+ " " + myRS.getString( "e.Lname" ) + "\t\t" );
                output.append(myRS.getString( "e.email" )+ "\t" + myRS.getString( "jt.job_title" ) + "\t" );
                output.append(myRS.getDate( "p.pay_date" )+ "\t" + myRS.getDouble( "p.earnings" )  + "\t" );
                output.append(myRS.getDouble( "p.fed_tax" ) +"\t" + myRS.getDouble( "p.fed_med" )  + "\t" );
                output.append(myRS.getDouble( "p.fed_SS" ) +"\t" + myRS.getDouble( "p.state_tax" )  + "\t" );
                output.append(myRS.getDouble( "p.retire_401K" )  + "\t" );
                output.append(myRS.getDouble( "p.health_care" ) );
                output.append("\n");
            }
            System.out.println( output.toString() );
            output.setLength( 0 );
            myConn.close();
        } 
        catch (SQLException e) 
        {
            System.out.println("ERROR " + e.getLocalizedMessage());
        }
        finally
        {
        }

        
    }
}
