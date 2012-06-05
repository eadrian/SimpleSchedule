
import java.util.ArrayList;
import java.util.List;

public class Course {

	public static final DBConnection dbc = new DBConnection();
	public static Object dbLock = new Object();
	
	/* Stored in db Course table */
	public int ID; 			// same as classNum
	public String avgGrade;
	public String code; 		// ex: CS161
	public int courseNumber;
	public int deptID;
	public String description;
	public String grading;
	public String title;   		// ex: Introduction to Algorithms
	public String lectureDays; 	// ex: Mon, Wed, Fri represented as '10101'
	public int numReviews;
	public int numUnits;	
	public double rating;	
	
	public int timeBegin;
	public int timeEnd;
	public String type;
	public int workload;
	public String quarter = "";
	public String department;
	public String deptAB;
	public float wScore;
	public float nScore;
	public float qScore;
	public float tScore;
	public float majorTScore;
	public float preScore;
	public int numPrereqs;
	public int GERScore;
	
	public String tags;
	public String titleTags;
	public String deptTags;
	public String allTags;
	public String deptCode;
	public int deptNum;
	public float totalScore;
	public boolean fillsGER = false;
	public boolean fillsREQ = false;
	
	public float relevance;
	
	
	/* Stored in other tables */
	public List<String> lecturers; // names of lecturers
	public List<String> prereqs; // strings of course name "CS109"
	public String universityReqs; // "GER:Ethics"
	
	
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
		lecturers = new ArrayList<String>();
		prereqs = new ArrayList<String>();
		universityReqs = "";
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
		lecturers = new ArrayList<String>();
		prereqs = new ArrayList<String>();
		universityReqs = "";
	}
	
	public Course(Course c) {
		this.ID = c.ID;		
		this.avgGrade = c.avgGrade;
		this.code = c.code;
		this.deptID = c.deptID;
		this.description = c.description;
		this.grading = c.grading;
		this.title = c.title;
		this.lectureDays = c.lectureDays;
		this.numReviews = c.numReviews;
		this.numUnits = c.numUnits;
		this.rating = c.rating;
		this.tags = c.tags;		
		this.timeBegin = c.timeBegin;
		this.timeEnd = c.timeEnd;
		this.type = c.type;
		this.workload = c.workload;
		lecturers = new ArrayList<String>();
		prereqs = new ArrayList<String>();
		universityReqs = "";
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
		System.out.println("Writing: "+code+" NUM: "+ID);
		if (code.equals("EARTHSYS 42")) {
			System.out.println(description);
			
		}
		deptID = ImportData.getDepartmentID(deptAB, department);
		String query = "";
		if (dbc.courseExists("rhun_courses", ID, quarter)) {
			sep = "\", ";						// if already exists, update the row
			query = "UPDATE rhun_courses SET " +
					"avgGrade = \"" + DBEscape(avgGrade) + sep + 
					"code = \"" + DBEscape(code) + sep + 
					"deptID = \"" + deptID + sep + 
					"description = \"" + DBEscape(description) + sep + 
					"grading = \"" + grading + sep + 
					"title = \"" + DBEscape(title) + sep + 
					"quarter = \"" + quarter + sep + 
					"lectureDays = \"" +lectureDays + sep + 
					"numReviews = \"" + numReviews + sep + 
					"numUnits = \"" + numUnits + sep + 
					"rating = \"" +rating + sep + 
					"tags = \"" + DBEscape(tags) + sep + 
					"timeBegin = \"" + timeBegin + sep +
					"timeEnd = \"" + timeEnd + sep + 
					"type = \"" + type + sep +
					"workload = \"" + workload + sep +
					"NumPrereqs = \"" + 0 + "\" " +
					"WHERE ID=" + ID + ";";
		} else {								// if doesn't exist, insert into database
			query = "INSERT INTO rhun_courses VALUES (" + ID + ", \"" + 
					DBEscape(avgGrade) + sep + 
					DBEscape(code) + sep + 
					deptID + sep + 
					DBEscape(description)  + sep + 
					grading + sep + 
					DBEscape(title) + sep +
					quarter + sep +
					lectureDays + sep + 
					numReviews + sep + 
					numUnits + sep + 
					rating + sep + 
					DBEscape(tags) + sep + 
					timeBegin + sep +
					timeEnd + sep + 
					type + sep +
					workload + sep +
					0+sep+0+sep+0+sep+0+sep+0+ "\")";
		}
		dbc.update(query);
		ImportData.importLecturers(""+ID, this.lecturers, quarter);
		
		ImportData.importUnivReqs(code, ""+ID, this.universityReqs);
	}
	
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Course))return false;
	    Course otherCourse = (Course)other;
	    return (otherCourse.code.equals(this.code) && otherCourse.quarter.equals(this.quarter));
	}
	
	public String DBEscape(String s) {
		if (s==null)
			return null;
		String n = s;
		n.replace("\\", "\\\\");
		n.replace("\"", "");
		n.replace("\'", "");
		while (n.contains("<") && n.contains(">")) {
			n = new String(n.substring(0, n.indexOf("<"))+n.substring(n.indexOf(">")+1));
		}
		return n;
	}
	
	
}
