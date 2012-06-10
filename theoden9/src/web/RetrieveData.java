package web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RetrieveData {

	private static DBConnection dbc = new DBConnection();
	public static Object dbLock = new Object();
	
	public RetrieveData() {
		
	}
	
	// given a map of attribute-value pairs, find all courses that have match those pairs
	// ex: find all courses with units = 5 and timeBegin = 1100
	private static List<Course> getCoursesByAttr(Map<String,String> mapOfAttrValuePairs) {
		
		List<Integer> courseIDs = dbc.getIDs("rhun_courses", mapOfAttrValuePairs);
		
		List<Course> listCourses = new ArrayList<Course>();		
		for (int courseID : courseIDs) {				// for each of the course IDs,
			Course newcourse = new Course(courseID);    // get the info from the db and reconstruct a course object
			listCourses.add(newcourse);					// and add it to the list to return
		}
		return listCourses;
	}
	
	private static List<Course> getAllCourses() {
		return dbc.getAllCourses();
	}
	
	public static void main(String[] args){	
		
		/** GET COURSE BY ATTRIBUTES **/
		Map<String,String> mapOfAttrValuePairs = new HashMap<String, String>();
		mapOfAttrValuePairs.put("lectureDays", "10101");				// list as many attributes in key-value format
		mapOfAttrValuePairs.put("numUnits", "5");
		mapOfAttrValuePairs.put("ID", "16");
		
		List<Course> filteredCourses = getCoursesByAttr(mapOfAttrValuePairs);	// returns list of courses that match
		
		for (Course course : filteredCourses) {
			// for each of the courses that match the specified set of attributes, do something
			System.out.println(course.title);
		}

		/** GET ALL COURSES **/
		List<Course> allCourses = getAllCourses();	
		
		for (Course course : allCourses) {
			// for all of the courses, do something
			// for example, print out all the lecturers of each course
			System.out.println(course.getLecturers());
		}
	}
}
