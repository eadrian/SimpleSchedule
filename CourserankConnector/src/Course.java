

public class Course {

	public static final DBConnection dbc = new DBConnection();
	public static Object dbLock = new Object();
	
	public enum Grade { A, B, C, D, F, CR, NCR, I }	
	public enum Quarter { AUT, WIN, SPR, SUM }	
	public enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY }
	public enum UGReq { DBEngrAppSci, DBHum, DBMath, DBNatSci, DBSocSci, ECAmerCul, ECEthnicReas, ECGender, ECGlobalCom }
	private long ID;
	
	/* Alphabetical ordered */
	public String avgGrade;
	public String courseTitle; // ex: CS161
	public int courseNumber; //ex: 161
	public String description;
	public long deptID;
	public String fullTitle;   // ex: Introduction to Algorithms
	public String lectureDays; // ex: Mon, Wed, Fri
	public String lecturer;
	public String location;    // adding this in anyway
	public int numReviews;
	public int numUnits;	
	public double rating;	
	public float score; 		// may or may not be necessary
	public String timeBlock;
	//private Set<String> tags;
	
	/* Construct new course instance to add to database */
	public Course(String avgGrade, String courseTitle, String description, long deptID, 
				  String fullTitle, String lectureDays, String lecturer, String location, 
				  int numReviews, int numUnits, double rating, String timeBlock ) {

		this.avgGrade = avgGrade;
		this.courseTitle = courseTitle;
		this.deptID = deptID;
		this.description = description;
		this.fullTitle = fullTitle;
		this.lectureDays = lectureDays;
		this.lecturer = lecturer;
		this.location = location;
		this.numReviews = numReviews;
		this.numUnits = numUnits;
		this.rating = rating;
		this.score = -1; // init to -1 for now
		this.timeBlock = timeBlock;
		
		this.ID = addToDatabase();
		
	}
	public Course() {
		
	}
	
	/* Construct a course instance from an existing one in database */
	public Course(long id) {
		this.avgGrade = dbc.getCourseAttribute(id, "avgGrade");
		this.courseTitle = dbc.getCourseAttribute(id, "courseTitle");
		this.description = dbc.getCourseAttribute(id, "description");
		this.deptID = Integer.parseInt(dbc.getCourseAttribute(id, "deptID"));
		this.fullTitle = dbc.getCourseAttribute(id, "fullTitle");
		this.lectureDays = dbc.getCourseAttribute(id, "lectureDays");
		this.lecturer = dbc.getCourseAttribute(id, "lecturer");
		this.location = dbc.getCourseAttribute(id, "location");	
		this.numReviews = Integer.parseInt(dbc.getCourseAttribute(id, "numReviews"));
		this.numUnits = Integer.parseInt(dbc.getCourseAttribute(id, "numUnits"));	
		this.rating = Integer.parseInt(dbc.getCourseAttribute(id, "rating"));
		this.score = Integer.parseInt(dbc.getCourseAttribute(id, "score"));
		this.timeBlock = dbc.getCourseAttribute(id, "timeBlock");
	}
	
	public long getID() {
		return ID;
	}
	public String getDepartment() {
		return dbc.getDepartmentAttribute(deptID, "name");
	}
	public String getDescription() {
		return description;
	}
	public String getLectureDays() {
		return lectureDays;
	}
	public String getLecturer() {
		return lecturer;
	}
	public String getLocation() {
		return location;
	}
	public int getNumUnits() {
		return numUnits;
	}
	public String getAvgGrade() {
		return avgGrade;
	}
	public int getNumReviews() {
		return numReviews;
	}
	public double getRating() {
		return rating;
	}
	public float getScore() {
		return score;
	}
	public String getTimeBlock() {
		return timeBlock;
	}
	
	public long addToDatabase() {
		String sep = "\", \"";		
		long id = 0;
		synchronized(dbLock) {
			id = dbc.getNextID("rhun_courses"); // gets count of existing courses
		}
		String query = "INSERT INTO rhun_courses VALUES (" + id + ", \"" + 
						avgGrade + sep + 
						courseTitle + sep + 
						deptID + sep + 
						description + sep + 
						fullTitle + sep + 
						lectureDays + sep + 
						lecturer + sep + 
						location + sep + 
						numReviews + sep + 
						numUnits + sep + 
						rating + sep + 
						score + sep + 
						timeBlock + "\")";
		dbc.update(query);
		
		return id;	
	}
	
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Course))return false;
	    Course otherCourse = (Course)other;
	    return otherCourse.courseTitle.equals(this.courseTitle);
	}
	
}
