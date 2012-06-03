package web;


import java.sql.*;
import java.util.*;

public class DBConnection {
/*
	// eac's db access
	private static final String account = "root";
	private static final String password = "trespass";
	private static final String server = "localhost";
	public static final String database = "cdata";
*/
	// ekc's db access
	private static final String account = "root";
	private static final String password = "";
	private static final String server = "localhost";
	public static final String database = "rhun";
	
	public Connection con;
	public static final String sep = "\", \"";
	
	//shaewaem
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
		
	public String getCourseAttribute(int courseID, String attribute) {
		return getAttribute("rhun_courses", courseID, attribute);
	}
	
	/** GENERAL METHODS **/

	public void update(String query) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			stmt.executeUpdate(query);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void updateAttribute(String table, int ID, String attr, String value) {
		String query = "UPDATE " + table + " SET " + attr + " = " + value + " WHERE ID=" + ID;
		update(query);
	}
	
	public void updateAttributeWhere(String table, String attr, String val, String attrUp, String upVal) {
		String query = "UPDATE " + table + " SET " + attrUp + " = " + upVal + " WHERE "+attr+"=" + val;
		update(query);
	
	}
	
	public void updateAttributesWhere(String table, List<AttrVal> match, List<AttrVal> update) {
		// Construct the query
		String and = "";
		String query = "UPDATE " + table + " SET ";
		for (int i=0; i<update.size(); i++) {								// for each attr-value pair
	        if (update.get(i).type.equals("String")) {
	        	query += and + update.get(i).attr + " = '" + update.get(i).val + "' ";
	        } else if (update.get(i).type.equals("int")|| update.get(i).type.equals("float")) {
	        	query += and + update.get(i).attr + " = " + update.get(i).val + " ";
	        }
			and = ", ";
	    }
		
		query+=" WHERE ";
		and = "";
	    for (int i=0; i<match.size(); i++) {								// for each attr-value pair
	        if (match.get(i).type.equals("String")) {
	        	query += and + match.get(i).attr + " = '" + match.get(i).val + "' ";
	        } else if (match.get(i).type.equals("int")|| match.get(i).type.equals("float")) {
	        	query += and + match.get(i).attr + " = " + match.get(i).val + " ";
	        }
			and = "AND ";
	    }
		update(query);    
	}
	
	// given an ID, get an attribute value
	// ex: get the title of the course with ID = 2
	public String getAttribute(String table, int ID, String attribute) {
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
	
	// given multiple IDs, return multiple attribute values
	public List<String> getAttributes(String table, List<Integer> IDList, String attribute) {
		List<String> attributes = new ArrayList<String>();
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			for (int ID : IDList) {
				ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + " WHERE id = " + ID);
				while (rs.next()) attributes.add(rs.getString(attribute));
			}
			return attributes;
		} catch (SQLException e) {
			System.out.println("Was not able to retrieve attribute.");
			e.printStackTrace();
		}
		return null;
	}

	// to be used in joint associate db tables: given multiple ID, return multiple associated values
	public List<Integer> getJunctionIDs(String table, int ID, String inColumn, String outColumn) {
		List<Integer> listAssociatedIDs = new ArrayList<Integer>();
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			String query = "SELECT DISTINCT " + outColumn + " FROM " + table + " WHERE " + inColumn + "= " + ID;
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) listAssociatedIDs.add(rs.getInt(outColumn));
			return listAssociatedIDs;
		} catch (SQLException e) {
			System.out.println("Was not able to retrieve attribute.");
			e.printStackTrace();
		}
		return null;
	}
	
	// given an attribute value, get the ID of an object
	public int getID(String table, String attributeName, String attributeVal) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + 
					" WHERE " + attributeName + " = \"" + attributeVal + "\"");
			int id = -1;
			while (rs.next()) {
				id = rs.getInt("ID");
			}
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to retrieve ID");
		}
		return -1;
	}

	// given multiple attribute-value pairs, get IDs of objects that match
	public List<Integer> getIDs(String table, Map<String,String> mapOfAttrValuePairs) {
		List<Integer> courseIDList = new ArrayList<Integer>();
	    Iterator it = mapOfAttrValuePairs.entrySet().iterator();
	    
	    // Construct the query
		String query = "SELECT * FROM " + table + " WHERE ";
		String and = "";
	    while (it.hasNext()) {								// for each attr-value pair
	        Map.Entry pairs = (Map.Entry)it.next();			// append it to the query
	        query += and + pairs.getKey() + " = \"" + pairs.getValue() + "\" ";
	        it.remove(); // avoids a ConcurrentModificationException
			and = "and ";
	    }
	    
	    // Query the database
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				courseIDList.add(rs.getInt("ID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to retrieve ID");
		}
		return courseIDList;
	}
	
	public List<Course> getAllCourses() {
		List<Course> listCourses = new ArrayList<Course>();
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_courses;" );
			while(rs.next()){
				Course newcourse = new Course(rs.getInt("ID"), rs.getString("avgGrade"), rs.getString("code"), 
											  rs.getString("description"), rs.getInt("deptID"), rs.getString("title"),
											  rs.getString("grading"), rs.getString("lectureDays"), rs.getInt("numReviews"), 
											  rs.getInt("numUnits"), rs.getDouble("rating"), rs.getInt("timeBegin"), 
											  rs.getInt("timeEnd"), rs.getString("tags"), rs.getString("type"), rs.getInt("workload"));
				listCourses.add(newcourse);
			}				
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to create new id.");			
		}
		return listCourses;		
	}
	
	
	
	// get the next ID for a new object to be stored
	public int getNextID(String table) {
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
			return (maxID + 1);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to create new id.");			
		}
		return -1;
	}
	
	//Check course
	public boolean courseExists(String table, int ID, String quarter) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM "  + table + " WHERE ID=" + ID + " AND quarter='"+quarter+"';" );
			while(rs.next()){
				return true;
			}				
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to create new id.");			
		}
		return false;
	}
	
	// checks if a given ID in a table already exists
	
	public boolean exists(String table, int ID) {
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM "  + table + " WHERE ID=" + ID + ";" );
			while(rs.next()){
				return true;
			}				
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to create new id.");			
		}
		return false;
	}
	
	public List<List<AttrVal>> getAttributesThatMatch(String table, List<AttrVal> request, List<AttrVal> match, boolean sort, String attr) {
		List<List<AttrVal>> results = new ArrayList<List<AttrVal>>();
	    
	    // Construct the query
		String query = "SELECT * FROM " + table + " WHERE ";
		String and = "";
	    for (int i=0; i<match.size(); i++) {								// for each attr-value pair
	        if (match.get(i).type.equals("String")) {
	        	if (!match.get(i).like)
	        		query += and + match.get(i).attr + " = '" + match.get(i).val + "' ";
	        	else 
	        		query += and + match.get(i).attr + " LIKE '" + match.get(i).val + "' ";
	        } else if (match.get(i).type.equals("int")|| match.get(i).type.equals("float")) {
	        	query += and + match.get(i).attr + " = " + match.get(i).val + " ";
	        }
			and = "AND ";
	    }
	    if (sort) {
	    	query= query + " ORDER BY "+attr;
	    }
	    
	    // Query the database
	    //System.err.println("Query: "+query);
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				results.add(new ArrayList<AttrVal>());
				for (int i=0; i<request.size(); i++) {
					AttrVal a = new AttrVal();
					a.type = request.get(i).type;
					a.attr = request.get(i).attr;
					if (a.type.equals("String")) {
						a.val = rs.getString(a.attr);
					} else if (a.type.equals("int")) {
						a.val = ""+rs.getInt(a.attr);
					} else if (a.type.equals("float")) {
						a.val = ""+rs.getFloat(a.attr);
					}
					results.get(results.size()-1).add(a);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to retrieve ID");
		}
		return results;
	}

	public List<Course> getCoursesThatMatch(List<AttrVal> match ) {
		// TODO Auto-generated method stub
		return getCoursesThatMatchSorted(match, false,"");
		
	}
	
	public List<Course> getCoursesThatMatchSorted(List<AttrVal> match, boolean sort, String attr) {
		
		List<Course> results = new ArrayList<Course>();
	    boolean lastOr = false;
	   
	    // Construct the query
		String query = "SELECT * FROM rhun_courses WHERE ";
		String and = "";
	    for (int i=0; i<match.size(); i++) { // for each attr-value pair
	    	if (i>0 && match.get(i).or) {
	    		
	    		if (!lastOr) {
	    			and= and+"(";
	    		} else {
	    			and = "OR ";
	    		}
	    		lastOr= true;
	    	} else {
	    		lastOr = false;
	    		if (lastOr) {
	    			and = ") "+and;
	    		}
	    	}
	        if (match.get(i).type.equals("String")) {
	        	if (!match.get(i).like)
	        		query += and + match.get(i).attr + " = '" + match.get(i).val + "' ";
	        	else 
	        		query += and + match.get(i).attr + " LIKE '" + match.get(i).val + "' ";
	        } else if (match.get(i).type.equals("int")|| match.get(i).type.equals("float")) {
	        	query += and + match.get(i).attr + " = " + match.get(i).val + " ";
	        }
			and = "AND ";
			
	    }
	    if (lastOr) {
	    	query = query + ")";
	    }
	    if (sort) {
	    	query= query + " ORDER BY "+attr;
	    }
		
		System.err.println("Executing query: "+query);
	    
	    try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				//System.out.println(".");
				Course c = new Course(rs.getInt("ID"), rs.getString("avgGrade"), rs.getString("code"), 
						  rs.getString("description"), rs.getInt("deptID"), rs.getString("title"),
						  rs.getString("grading"), rs.getString("lectureDays"), rs.getInt("numReviews"), 
						  rs.getInt("numUnits"), rs.getDouble("rating"), rs.getInt("timeBegin"), 
						  rs.getInt("timeEnd"), rs.getString("tags"), rs.getString("type"), rs.getInt("workload"));
				c.allTags=rs.getString("allTags");
				c.titleTags=rs.getString("titleTags");
				c.deptTags=rs.getString("deptTags");
				c.deptCode = rs.getString("deptCode");
				c.deptNum = rs.getInt("cnum");
				c.nScore=rs.getFloat("nScore");
				c.wScore=rs.getFloat("wScore");
				c.qScore=rs.getFloat("qScore");
				c.tScore=rs.getFloat("tScore");
				c.majorTScore=rs.getFloat("MajorTScore");
				c.preScore=rs.getFloat("preScore");
				c.numPrereqs=rs.getInt("NumPrereqs");
				c.GERScore=rs.getInt("numGERS");
				c.universityReqs = rs.getString("GERS");
				c.prereqs = parsePrereqs(rs.getString("prereqs"));
				c.quarter = rs.getString("quarter");
				results.add(c);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to retrieve courses that match");
		}
	    if (results.size()==0)
	    	return null;
    	else {
    		System.out.println("Returning results");
	    	return results;
	    	
    	}
		
	}
	
public List<Course> getCoursesThatMatchSortedLim(List<AttrVal> match, boolean sort, String attr, int Lim) {
		
		List<Course> results = new ArrayList<Course>();
	    
	    // Construct the query
		String query = "SELECT * FROM rhun_courses WHERE ";
		String and = "";
	    for (int i=0; i<match.size(); i++) {								// for each attr-value pair
	        if (match.get(i).type.equals("String")) {
	        	if (!match.get(i).like)
	        		query += and + match.get(i).attr + " = '" + match.get(i).val + "' ";
	        	else 
	        		query += and + match.get(i).attr + " LIKE '" + match.get(i).val + "' ";
	        } else if (match.get(i).type.equals("int")|| match.get(i).type.equals("float")) {
	        	query += and + match.get(i).attr + " = " + match.get(i).val + " ";
	        }
			and = "AND ";
	    }
	    if (sort) {
	    	query= query + " ORDER BY "+attr+ " DESC";
	    }
		
		System.err.println("Executing query: "+query);
	    int counter = 0;
	    try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				//System.out.println(".");
				Course c = new Course(rs.getInt("ID"), rs.getString("avgGrade"), rs.getString("code"), 
						  rs.getString("description"), rs.getInt("deptID"), rs.getString("title"),
						  rs.getString("grading"), rs.getString("lectureDays"), rs.getInt("numReviews"), 
						  rs.getInt("numUnits"), rs.getDouble("rating"), rs.getInt("timeBegin"), 
						  rs.getInt("timeEnd"), rs.getString("tags"), rs.getString("type"), rs.getInt("workload"));
				c.allTags=rs.getString("allTags");
				c.titleTags=rs.getString("titleTags");
				c.deptTags=rs.getString("deptTags");
				c.deptCode = rs.getString("deptCode");
				c.deptNum = rs.getInt("cnum");
				c.nScore=rs.getFloat("nScore");
				c.wScore=rs.getFloat("wScore");
				c.qScore=rs.getFloat("qScore");
				c.tScore=rs.getFloat("tScore");
				c.majorTScore=rs.getFloat("MajorTScore");
				c.preScore=rs.getFloat("preScore");
				c.numPrereqs=rs.getInt("NumPrereqs");
				c.GERScore=rs.getInt("numGERS");
				c.universityReqs = rs.getString("GERS");
				c.prereqs = parsePrereqs(rs.getString("prereqs"));
				c.quarter = rs.getString("quarter");
				counter++;
				if (counter <= Lim)
					results.add(c);
				else 
					return results;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to retrieve courses that match");
		}
	    if (results.size()==0)
	    	return null;
    	else {
    		System.out.println("Returning results");
	    	return results;
	    	
    	}
		
	}

	private List<String> parsePrereqs(String reqs) {
		List<String> prereqs = new ArrayList<String>();
		String[] reqArray = reqs.split(",");
		for (int i=0; i<reqArray.length || i==0; i++) {
			if (!reqArray[i].equals(""))
				prereqs.add(reqArray[i]);
		}
		return prereqs;
	}

	public List<Integer> getAllReqGroups(String track) {
		List<Integer> allReqGroups = new ArrayList<Integer>();
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_tracks, rhun_track_reqgroup WHERE rhun_tracks.name = '" + track + "' AND rhun_tracks.id = rhun_track_reqgroup.trackID");
			while(rs.next()){
				String reqGroupIDStr = rs.getString("reqGroupID");
				allReqGroups.add(Integer.parseInt(reqGroupIDStr));
			}				
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to get list of requirement groups.");			
		}
		return allReqGroups;
		
	}

	public List<Integer> getAllReqs(List<Integer> allReqGroups) {
		List<Integer> allReqs = new ArrayList<Integer>();
		Iterator<Integer> iterator = allReqGroups.iterator();
		while (iterator.hasNext()) {			// iterate thru each requirement group
			int reqGroupID = iterator.next();
			try {
				Statement stmt = con.createStatement();
				stmt.executeQuery("USE " + database);
				ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_reqgroup_req WHERE reqGroupID = " + reqGroupID);
				while(rs.next()){
					int times = Integer.parseInt(rs.getString("numRequired"));
					int reqID = Integer.parseInt(rs.getString("reqID"));
					for (int i = 0; i < times; i++) {
						allReqs.add(reqID);
					}
				}				
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Was not able to get list of requirements.");			
			}
		}
		return allReqs;
		
	}

	public void checkOffReqs(List<Integer> allReqs, Set<String> cTaken) {
		Iterator<String> iterator = cTaken.iterator();
		while (iterator.hasNext()) {			// iterate thru each class taken
			String classTaken = iterator.next();
			try {
				Statement stmt = con.createStatement();
				stmt.executeQuery("USE " + database);
				
				ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_reqs,rhun_course_reqs WHERE code = '" + classTaken + "' AND rhun_reqs.ID = rhun_course_reqs.reqID AND reqID > 9 GROUP BY ID ORDER BY size asc");
				boolean reqFound = false;
				int reqFulfilled = 0; 
				while(rs.next() && !reqFound){
					reqFulfilled = Integer.parseInt(rs.getString("ID"));
					// remove that req from the reqs_needed
					for (int i = 0; i < allReqs.size(); i++) {
						if (allReqs.get(i) == reqFulfilled) {
							allReqs.remove(i);
							reqFound = true;
							break;
						}
					}
				}		

			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Was not able to modify list of requirements.");			
			}
		}
		
	}
	
	
	public void checkOffReqs_UGTests(List<Integer> allReqs, List<String> testsTaken) {
		List<Integer> listReqsFulfilled = new ArrayList<Integer>();
		Iterator<String> iterator = testsTaken.iterator();
		while (iterator.hasNext()) {			// iterate thru each test taken
			String testTaken = iterator.next();
			try {
				Statement stmt = con.createStatement();
				stmt.executeQuery("USE " + database);
				
				if (testTaken.equals("UGTEST CH")) {
					ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_reqs WHERE name = 'Science Elective'");
					while(rs.next()) {
						listReqsFulfilled.add(Integer.parseInt(rs.getString("ID")));
					}		
				} else if (testTaken.equals("UGTEST MA")) {
					ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_reqs WHERE name = 'Calculus 1' OR name = 'Calculus 2'");
					while(rs.next()){
						listReqsFulfilled.add(Integer.parseInt(rs.getString("ID")));
					}		
				} else if (testTaken.equals("UGTEST PH")) {
					ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_reqs WHERE name = 'Physics Mechanics' OR name = 'Physics Electricity and Magnetism'");
					while(rs.next()){
						listReqsFulfilled.add(Integer.parseInt(rs.getString("ID")));
					}		
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Was not able to process ap/ib tests to modify list of requirements.");			
			}
		}

		// remove that req from the reqs_needed
		Iterator<Integer> iteratorInt = listReqsFulfilled.iterator();
		while (iteratorInt.hasNext()) {
			int reqFulfilled = iteratorInt.next();
			for (int i = 0; i < allReqs.size(); i++) {
				if (allReqs.get(i) == reqFulfilled) {
					allReqs.remove(i);
					break;
				}
			}
		}
	}


	public String getRarestReq(List<Integer> allReqs, String classTaken) {
		String rarestReq = "";
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_reqs,rhun_course_reqs WHERE code = '" + classTaken + "' AND rhun_reqs.ID = rhun_course_reqs.reqID AND reqID > 9 GROUP BY ID ORDER BY size asc");
			boolean reqFound = false;
			int reqFulfilled = 0; 
			while(rs.next() && !reqFound){
				reqFulfilled = Integer.parseInt(rs.getString("ID"));
				// remove that req from the reqs_needed
				for (int i = 0; i < allReqs.size(); i++) {
					if (allReqs.get(i) == reqFulfilled) {
						rarestReq = getAttribute("rhun_reqs", allReqs.get(i), "name");
						reqFound = true;
						break;
					}
				}
			}		
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to modify list of requirements.");			
		}
		return rarestReq;
	}
	
	public List<String> getCoursesThatFulfillMR(String reqName) {
		List<String> coursesFulfilling = new ArrayList<String>();
		try {
			Statement stmt = con.createStatement();
			stmt.executeQuery("USE " + database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_course_reqs, rhun_reqs WHERE rhun_reqs.ID = rhun_course_reqs.reqID AND name = '" + reqName + "'");
			while(rs.next()) {
				coursesFulfilling.add(rs.getString("code"));
			}	
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to get courses fulfilling major requirements.");			
		}
		return coursesFulfilling;
	}
}

