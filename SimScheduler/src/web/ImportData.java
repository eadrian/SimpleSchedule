package web;


public class ImportData {

	private static DBConnection dbc = new DBConnection();
	public static Object dbLock = new Object();

	// imports CourseRank data 
	// happens in 3 parts: 1) import course object, 
	// 2) then import the course's lecturer(s), 
	// 3) then the univ reqs it fulfills
	private static void importData(String classNumStr, String avgGrade, String courseTitle, String description, 
					  String department, String fullTitle, String grading, String lecturers, String numReviews, 
					  String numUnits, String rating, String timeBeginStr, 
					  String timeEndStr, String lectureDays, String universityReqs, 
					  String tags, String type, String workloadStr) {
		
		importCourse(classNumStr, avgGrade, courseTitle, description, department, fullTitle, grading, 
				     lecturers, numReviews, numUnits, rating, timeBeginStr, timeEndStr, lectureDays, 
				     universityReqs, tags, type, workloadStr);
		importLecturers(classNumStr, lecturers); 		// lecturers will be a comma separated list of lecturers
		importUnivReqs(classNumStr, universityReqs);   // universityReqs will be a comma separated string like "GER: Hum, GER: Math"

		System.out.println("Input stored.");
		
	}
	
	// add course to database if ID not present, else overwrites
	private static void importCourse(String classNumStr, String avgGrade, String code, String description, 
			  String department, String title, String grading, String lecturers, String numReviews, 
			  String numUnits, String rating, String timeBegin, 
			  String timeEnd, String lectureDays, String universityReqs, 
			  String tags, String type, String workload) {
		
		int ID = Integer.parseInt(classNumStr);
		int deptID = getDepartmentID(department);
		
		String sep = "\", \"";		
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
	
	// imports lecturers by adding them to the Lecturers Table and the Course-Lecturers Junction
	private static void importLecturers(String classNum, String lecturersList) {
		String[] attrs = lecturersList.split(", ");
		int size = attrs.length;
		for (int i = 0; i < size; i++) {	
			String lecturerName = attrs[i];				
			long ID = dbc.getID("rhun_lecturers", "name", lecturerName);// check if lecturer already exists
			if (ID < 0) { 												// if not add to lecturer table
				ID = dbc.getNextID("rhun_lecturers");
				dbc.update("INSERT INTO rhun_lecturers VALUES (" + ID + ", \"" + 
						lecturerName + "\", 0, 0, 1);");
			} else {													// increment numCourses count
				dbc.update("UPDATE rhun_lecturers SET numCourses=numCourses+1 WHERE " +
						"name=\"" + lecturerName + "\"");
			}
			dbc.update("INSERT INTO rhun_lecturer_course VALUES(" + ID + ", " + classNum + ")");
		}
	}
	
	private static void importUnivReqs (String classNum, String univReqsList) {
		String[] reqsFulfilled = univReqsList.split(", ");
		int size = reqsFulfilled.length;
		for (int i = 0; i < size; i++) {	
			int reqFulfilledID = dbc.getID("rhun_reqs", "name", reqsFulfilled[i]);	
			dbc.update("INSERT INTO rhun_course_reqs VALUES (" + classNum + ", " + 
						reqFulfilledID + ");");
		}
	}
	
	private static void importPrereqs (String classNum, String prereqsList) {		// prereqsList is like "CS109, EE108, ARTSTUD60"
		String[] prereqs = prereqsList.split(", ");
		int size = prereqs.length;
		for (int i = 0; i < size; i++) {	
			int prereqCourseID = dbc.getID("rhun_courses", "code", prereqs[i]);	
			if (prereqCourseID > 0) {
				dbc.update("INSERT INTO rhun_course_prereqs VALUES (" + classNum + ", " + 
							prereqCourseID + ");");
			}
		}
	}

	public static void importUserData (String sunetID, String password, String name, String year, String major, 
					  String coursesTaken, int unitsTaken) {		
		String query = "INSERT INTO rhun_users VALUES (\"" + sunetID + "\", \"" + password + 
					   "\", \"" + name + "\", \"" + year + "\", \"" + major + 
					   "\", " + unitsTaken + ")";
		dbc.update(query);		
		storeManyToManyAttributes("rhun_coursesTaken", sunetID, coursesTaken);
	}
	
	
	/*** HELPER FUNCTIONS ***/
	private static int getDepartmentID(String code) {			// gets the existing department ID from the database
		int ID = dbc.getID("rhun_departments", "code", code);
		if (ID < 0) {									// if none exists, add one to database
			ID = dbc.getNextID("rhun_departments");
			String query = "INSERT INTO rhun_departments VALUES (" + ID + ", \"" + 
			code + "\", \"\", \"\")";
			dbc.update(query);
		}	
		return ID;
	}
	
	private static void storeManyToManyAttributes(String table, String sunetID, String attrString) {
		String[] attrs = attrString.split(", ");
		int size = attrs.length;
		for (int i = 0; i < size; i++) {	
			dbc.update("INSERT INTO " + table + " VALUES (\"" + sunetID + "\", \"" + 
					attrs[i] + "\");");
		}
	}
	
	private static void updateLecturer(String lecturerName, String attr, String value) {
		int ID = dbc.getID("rhun_lecturers", "name", lecturerName);
		dbc.updateAttribute("rhun_lecturers", ID, attr, value);
	}
	
	public static void main(String[] args){	
		
		// This function is called for each course
		// it takes care of importing the course object, the lecturers, and the GER requirements it fulfills
		importData("21", "A+", "Hum39", "descr", "Hum", 
						 "Intro to Antrho", "Letter", 
						 "Mode", "10", "5", "5.5", "1100", "1150", "10101",
						 "GER:EngrAppSci", "tag6", "lecture", "5");
		
		// Second pass: import prereqs data
		importPrereqs("16799", "CS107, CS103");	
		
		// third pass: add the average rating of each lecturer to the db
		updateLecturer("Plummer", "avgRating", "3");
		
		// Import user data from Axess
		importUserData("arstark", "password", "Arya Stark", "2014",
				  		"Mechanical Engineering", "CS106, CS107", 100);
	
	}
}
