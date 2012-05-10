package web;


public class ImportData {

	private DBConnection dbc = new DBConnection();
	public static Object dbLock = new Object();

	// import course data
	public ImportData(String avgGrade, String courseTitle, String description, String department, 
					  String fullTitle, String lecturer, String location, String numReviews,
					  String numUnits, String rating, String timeBlock, String lectureDays, 
					  String quartersOffered, String UGReqs, String tags) {
		
		// parses numbers from string arguments
		long deptID = getDepartmentID(department);
		int numReviewsInt = Integer.parseInt(numReviews);
		int numUnitsInt = Integer.parseInt(numUnits);
		double ratingFlt = Double.parseDouble(rating);
		
		// new course instance added to database
		Course importedCourse = new Course(avgGrade, courseTitle, description, deptID, 
										   fullTitle, lectureDays, lecturer, location, 
										   numReviewsInt, numUnitsInt, ratingFlt, timeBlock);
		
		long courseID = importedCourse.getID();
		
		/* store days, quarters, requirements in separate table each
		 * (usually many-to-many attributes should get their own db table)
		 */
		storeManyToManyAttributes("rhun_lectureDays", courseID, lectureDays);
		storeManyToManyAttributes("rhun_quartersOffered", courseID, quartersOffered);
		storeManyToManyAttributes("rhun_requirements", courseID, UGReqs);
		storeManyToManyAttributes("rhun_tags", courseID, tags);
		System.out.println("Input stored.");
		
	}
	
	// import a user's data
	public ImportData(String sunetID, String password, String name, String year, String major, 
					  String coursesTaken, int unitsTaken) {		
		String query = "INSERT INTO rhun_users VALUES (\"" + sunetID + "\", \"" + password + 
					   "\", \"" + name + "\", \"" + year + "\", \"" + major + 
					   "\", " + unitsTaken + ")";
		dbc.update(query);		
		storeManyToManyAttributes("rhun_coursesTaken", sunetID, coursesTaken);
	}
	
	// gets the existing department ID from the database
	private long getDepartmentID(String name) {
		long ID = dbc.getID("rhun_departments", "name", name);
		if (ID < 0) {									// if none exists, add one to database
			synchronized(dbLock) {
				ID = dbc.getNextID("rhun_departments");
			}
			String query = "INSERT INTO rhun_departments VALUES (" + ID + ", \"" + 
			name + "\", \"\")";
			dbc.update(query);
		}	
		return ID;
	}

	/* For every many-to-many attribute, add it as a row to its table */
	private void storeManyToManyAttributes(String table, long courseID, String attrString) {
		String[] attrs = attrString.split(", ");
		int size = attrs.length;
		for (int i = 0; i < size; i++) {	
			dbc.update("INSERT INTO " + table + " VALUES (" + courseID + ", \"" + 
					attrs[i] + "\");");
		}
	}
	private void storeManyToManyAttributes(String table, String sunetID, String attrString) {
		String[] attrs = attrString.split(", ");
		int size = attrs.length;
		for (int i = 0; i < size; i++) {	
			dbc.update("INSERT INTO " + table + " VALUES (\"" + sunetID + "\", \"" + 
					attrs[i] + "\");");
		}
	}
	
	public static void main(String[] args){	
		ImportData importCourse = new ImportData("A-", "CS107", "descr", "CS", "Programming Abstractions",
								 "Zelenski", "Gates 100", "10", "5", "5.5", "11:00 AM - 12:15 PM", "Mon, Wed, Fri",
								 "Aut, Spr", "GER:DBHum, GER:ECGender", "tag1, tag2, tag3");
		
		ImportData importUser = new ImportData("arstark", "password", "Arya Stark", "2014",
											   "Mechanical Engineering", "CS106, CS107", 100);
	
	}
}
