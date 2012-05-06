package web;

import java.util.ArrayList;
import java.util.List;

public class Course {

	public static final DBConnection dbc = new DBConnection();
	public static Object dbLock = new Object();
	
	/* Stored in db Course table */
	public int ID; 			// same as classNum
	public String avgGrade;
	public String code; 		// ex: CS161
	public int deptID;
	public String description;
	public String grading;
	public String title;   		// ex: Introduction to Algorithms
	public String lectureDays; 	// ex: Mon, Wed, Fri represented as '10101'
	public int numReviews;
	public int numUnits;	
	public double rating;	
	public String tags;
	public int timeBegin;
	public int timeEnd;
	public String type;
	public int workload;
	
	/* Stored in other tables */
	public List<String> lecturers; // names of lecturers
	public List<String> prereqs; // strings of course name "CS109"
	public List<String> universityReqs; // "GER:Ethics"
	
	
	/* Construct new course instance */
	public Course(int classNum, String avgGrade, String code, String description, int deptID, 
				  String fullTitle, String grading, String lectureDays, 
				  int numReviews, int numUnits, double rating, int timeBegin, int timeEnd, 
				  String tags, String type, int workload) {
		this.ID = classNum;		
		this.avgGrade = avgGrade;
		this.code = code;
		this.deptID = deptID;
		this.description = description;
		this.grading = grading;
		this.title = fullTitle;
		this.lectureDays = lectureDays;
		this.numReviews = numReviews;
		this.numUnits = numUnits;
		this.rating = rating;
		this.tags = tags;		
		this.timeBegin = timeBegin;
		this.timeEnd = timeEnd;
		this.type = type;
		this.workload = workload;
	}
	
	/* Construct a course instance from an existing one in database */
	public Course(int id) {
		this.avgGrade = dbc.getCourseAttribute(id, "avgGrade");
		this.code = dbc.getCourseAttribute(id, "code");
		this.deptID = Integer.parseInt(dbc.getCourseAttribute(id, "deptID"));
		this.description = dbc.getCourseAttribute(id, "description");
		this.grading = dbc.getCourseAttribute(id, "grading");
		this.title = dbc.getCourseAttribute(id, "title");
		this.lectureDays = dbc.getCourseAttribute(id, "lectureDays");
		this.numReviews = Integer.parseInt(dbc.getCourseAttribute(id, "numReviews"));
		this.numUnits = Integer.parseInt(dbc.getCourseAttribute(id, "numUnits"));	
		this.rating = Float.parseFloat(dbc.getCourseAttribute(id, "rating"));
		this.tags = dbc.getCourseAttribute(id, "tags");
		this.timeBegin = Integer.parseInt(dbc.getCourseAttribute(id, "timeBegin"));
		this.timeEnd = Integer.parseInt(dbc.getCourseAttribute(id, "timeEnd"));	
		this.type = dbc.getCourseAttribute(id, "type");
		this.workload = Integer.parseInt(dbc.getCourseAttribute(id, "workload"));
	}

	public Course() {
		
	}
	
	public List<String> getLecturers() {
		List<Integer> listOfLecturersIDs = dbc.getJunctionIDs("rhun_lecturer_course", ID, "courseID", "lecturerID");

		List<String> attributes = new ArrayList<String>();
		for (int ID : listOfLecturersIDs) {
			attributes.add(dbc.getAttribute("rhun_lecturers", ID, "name"));
		}
		return attributes;
		//return dbc.getAttributes("rhun_lecturers", listOfLecturersIDs, "name");		
	}
	
	// writes course object to the Course Table. If there's already a row for it,
	// it overwrites the values.
	public void writeToDatabase() {
		String sep = "\", \"";		
		/*
		long id = 0;
		synchronized(dbLock) {
			id = dbc.getNextID("rhun_courses"); // gets count of existing courses
		}*/
		String query = "";
		if (dbc.exists("rhun_courses", ID)) {
			sep = "\", ";						// if already exists, update the row
			query = "UPDATE rhun_courses SET " +
					"avgGrade = \"" + avgGrade + sep + 
					"code = \"" + code + sep + 
					"deptID = \"" + deptID + sep + 
					"description = \"" + description + sep + 
					"grading = \"" + grading + sep + 
					"title = \"" + title + sep + 
					"lectureDays = \"" + lectureDays + sep + 
					"numReviews = \"" + numReviews + sep + 
					"numUnits = \"" + numUnits + sep + 
					"rating = \"" + rating + sep + 
					"tags = \"" + tags + sep + 
					"timeBegin = \"" + timeBegin + sep +
					"timeEnd = \"" + timeEnd + sep + 
					"type = \"" + type + sep +
					"workload = \"" + workload + "\" " +
					"WHERE ID=" + ID + ";";
		} else {								// if doesn't exist, insert into database
			query = "INSERT INTO rhun_courses VALUES (" + ID + ", \"" + 
					avgGrade + sep + 
					code + sep + 
					deptID + sep + 
					description + sep + 
					grading + sep + 
					title + sep + 
					lectureDays + sep + 
					numReviews + sep + 
					numUnits + sep + 
					rating + sep + 
					tags + sep + 
					timeBegin + sep +
					timeEnd + sep + 
					type + sep +
					workload + "\")";
		}
		dbc.update(query);
	}
	
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Course))return false;
	    Course otherCourse = (Course)other;
	    return otherCourse.code.equals(this.code);
	}
	
}
