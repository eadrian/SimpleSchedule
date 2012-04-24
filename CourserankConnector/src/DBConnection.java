

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class DBConnection {


	private static final String account = "ccs108elchen3";
	private static final String password = "shaewaem";
	private static final String server = "mysql-user.stanford.edu";
	private static final String database = "c_cs108_elchen3";

	private Connection con;
	public static final String sep = "\", \"";
	
	
	public DBConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			con = DriverManager.getConnection
				( "jdbc:mysql://" + server, account ,password);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
		
	public String getCourseAttribute(long courseID, String attribute) {
		return getAttribute("rhun_courses", courseID, attribute);
	}
	
	public String getDepartmentAttribute(long departmentID, String attribute) {
		return getAttribute("rhun_departments", departmentID, attribute);
	}	
	
	public String getLecturerAttribute(long lecturerID, String attribute) {
		return getAttribute("rhun_lecturers", lecturerID, attribute);
	}	
	
	/** GENERAL METHODS **/
	
	public void addCourse(Course c) {
		
	}

	public void update(String query) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			stmt.executeUpdate(query);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	// given an ID, get an attribute value
	public String getAttribute(String table, long ID, String attribute) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + table +
					" WHERE id = " + ID);
			rs.first();
			return rs.getString(attribute);
		} catch (NumberFormatException e) {
			System.out.println("Not a valid number.");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("Was not able to retrieve attribute.");
			e.printStackTrace();
		}
		return null;
	}
	
	// given an attribute value, get the ID of an object
	public long getID(String table, String attributeName, String attributeVal) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + 
					" WHERE " + attributeName + " = \"" + attributeVal + "\"");
			long id = -1;
			while (rs.next()) {
				id = rs.getLong("ID");
			}
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to retrieve ID");
		}
		return -1;
	}
	
	// get the next ID for a new object to be stored
	public long getNextID(String table) {
		try {
			int maxID = 0;
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM "  + table + ";" );
			while(rs.next()){
				String currQuestionID = rs.getString("id");
				int currCount = Integer.parseInt(currQuestionID);
				if(currCount >= maxID){
					maxID = currCount;
				}
			}				
			return (long) (maxID + 1);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to create new id.");			
		}
		return -1;
	}
	
}

