import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class dataProcessor {
	private static DBConnection dbc;
	public static Object dbLock;
	public Set<String> explored;
	
	
	public dataProcessor() {
		dbc = new DBConnection();
		dbLock = new Object();
		//calculateAverageRatings();
		generateCourseData();
	}
	
	public void generateCourseData() {
		List<String> depts = getDepartments();
		List<String> cnames = new ArrayList<String>();
		Map<String, List<String>> reqMap = new HashMap<String, List<String>>(500);
		
		
		
		Map<String, Integer> reqCount = new TreeMap<String, Integer>();
		ValueComparator bvc =  new ValueComparator(reqCount);
		Map<String, Integer> sortedCount = new TreeMap<String, Integer>(bvc);
		
		//Get course codes
		Date start = new Date();
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_courses;" );
			Date latency = new Date();
			System.err.println("Time to get course result: "+(float)((latency.getTime()-start.getTime())/1000f));
			int i=0;
			while(rs.next() && i<1){
				cnames.add(rs.getString("code").trim());
			}
			Date finish = new Date();
			System.err.println("Time to iterate through courses: "+(float)((finish.getTime()-start.getTime())/1000f));
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to get course codes.");			
		}
		
		MaxentTagger tagger = null;
		
		try {
			tagger = new MaxentTagger("models/english-left3words-distsim.tagger");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_courses;" );
			int i=0;
			while(rs.next() && i<1){
				Course c = new Course(rs.getInt("ID"), rs.getString("avgGrade"), rs.getString("code").trim(), 
											  rs.getString("description"), rs.getInt("deptID"), rs.getString("title"),
											  rs.getString("grading"), rs.getString("lectureDays"), rs.getInt("numReviews"), 
											  rs.getInt("numUnits"), rs.getDouble("rating"), rs.getInt("timeBegin"), 
											  rs.getInt("timeEnd"), rs.getString("tags"), rs.getString("type"), rs.getInt("workload"));
				if (!cnames.contains(c.code)) {
					cnames.add(c.code);
				}
				//System.out.println(tagger.tagString(c.description));
				//System.out.println(tagger.tagString(c.title));
				StringTokenizer st = new StringTokenizer(" "+tagger.tagString(c.title) + " " +tagger.tagString(c.description)+" ");
				String tags = " ";
			     while (st.hasMoreTokens()) {
			    	 String s = st.nextToken();
			         if (s.contains("/N") || s.contains("/J")) {
			        	 String tag = new String(s.substring(0, s.indexOf("/")));
			        	 tag = tag.toLowerCase().trim();
			        	 Stemmer stemmer = new Stemmer();
			        	 String stem = stemmer.stemString(tag);
			        	 if (!tags.contains(stem))
			        		 tags= tags + stem + " ";
			        	 
			         }
			     }
			     //System.out.println(tags);
			     if (tags.equals(""))
			    	 System.out.println(c.code+" no tags");
				 String prereqs = generatePrereqs(cnames, depts, c.description, c.code);
				 if (!prereqs.equals("")) {
					 String[] courses = prereqs.split(",");
					 for (int j=0; j<courses.length; j++) {
						 if (!reqMap.containsKey(courses[j])) {
							 List<String> l = new ArrayList<String>();
							 if (!l.contains(c.code))
								 l.add(c.code);
							 reqMap.put(new String(courses[j]).trim(),l);
						 } else {
							 List<String> l =reqMap.get(courses[j].trim());
							 if (!l.contains(c.code))
								 l.add(c.code);
							 reqMap.put(new String(courses[j]).trim(), l);
						 }
					 }
				 }
				 if (!prereqs.equals("") && c.code.contains("CS"))
			    	 System.out.println(c.code+" Prerequisites: "+prereqs);
				 /*if (c.code.equals("CS 277"))
					 break;*/
				
				//i++;
			}				
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to get course data.");			
		}
		System.out.println("Done tagging");
		System.err.println("Starting prereq map recursion");
		Set<String> keys = reqMap.keySet();
		Iterator<String> it = keys.iterator();
		explored = new HashSet<String>();
		while (it.hasNext()) {
			String key = it.next();
			//System.out.println("CS 107");
			
			int numReqs = recursiveCountPrereqs(reqMap,key);
			System.out.println("Total count courses that have "+key+" as prereq : "+numReqs);
			reqCount.put(key, numReqs);
			sortedCount.put(key, numReqs);
			explored.clear();
		}
		/*keys = reqCount.keySet();
		it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			//System.out.println("CS 107");
			
			sortedCount.put(key, reqCount.get(key));
		}
		for (String key : reqCount.keySet()) {
			sortedCount.put(key, reqCount.get(key));
            //System.out.println("key/value: " + key + "/"+reqCount.get(key));
        }*/
		for (String key : sortedCount.keySet()) {
            System.out.println("key/value: " + key + "/"+sortedCount.get(key));
        }
		System.out.println("First size: "+reqCount.size()+" Second size "+sortedCount.size());
		
	}
	
	private int recursiveCountPrereqs(Map<String, List<String>> reqMap, String course) {
		int count = 0;
		
		if (explored.contains(course)) {
			//System.err.println("Already explored: "+course);
			return -1;
		} else {
			explored.add(course);
		}
		
		List<String> reqs = reqMap.get(course);
		if (reqs != null) {
			for (int i=0; i<reqs.size(); i++) {
				//System.out.println("Course: "+course+" is a prereq for : "+reqs.get(i));
				count=count+1+recursiveCountPrereqs(reqMap, reqs.get(i));
			}
		}
		return count;
	}

	private String generatePrereqs(List<String> cnames, List<String> depts, String description, String code) {
		
		if (description.contains("Prereq") || description.contains("prereq")) {
	    	 int startIndex = 0;
	    	 if (description.contains("Prereq")) {
	    		 startIndex = description.indexOf("Prereq");
	    	 } else if (description.contains("prereq")) {
	    		 startIndex = description.indexOf("prereq");
	    	 }
	    	 String prereqs = new String(description.substring(startIndex));
	    	 if (prereqs.contains(":")) {
	    		 prereqs = new String(prereqs.substring(prereqs.indexOf(":")));
	    	 }
	    	 
	    	 
	    	 String prereqStr = "";
	    	 
	    	 StringTokenizer st = new StringTokenizer(prereqs, " ;,.)/-:(", false);
			 String prev = null;
			 String pPrev = null;
			 String prevCode = null;
			 
			 boolean prevValid = false;
			 boolean prevCourse = false;
		     while (st.hasMoreTokens()) {
		    	 String s = st.nextToken().trim();
		    	 boolean found = false;
		    	 
		    	 for (int j=0; j<s.length() && !found; j++) {
		    		 if (Character.isDigit(s.charAt(j))) {
		    			 found = true;
		    			 String prereq = "";
		    			 if (j!=0) {
		    				 prereq = new String(s.substring(0,j)+" "+s.substring(j));
		    				 System.err.println(code+" : Split word : "+prereq);
		    			 } else {
		    				 /*if (prev !=null) {
		    					 prereq = prev + " "+s;
		    				 }*/
		    				 if (prev!=null && depts.contains(prev)) {
		    					 if (pPrev==null || (!pPrev.equals("OR") || !prevValid))
		    						 prereq = prev + " "+s;
		    				 } else if(prev != null) {
		    					 if (prevCode!=null && (!prev.equals("OR") || !prevValid) && (prevCourse || prev.equals("AND"))) {
		    						 prereq = prevCode + " "+s;
		    					 } else if (prev!=null && (!prev.equals("OR") || !prevValid))
		    					 {
		    						 prereq = new String(code.substring(0, code.indexOf(" ")));
		    						 prereq = prereq+" "+s;
		    					 }
		    				 } else {
		    					 prereq = new String(code.substring(0, code.indexOf(" ")));
	    						 prereq = prereq+" "+s;
		    				 }
		    				 
		    				 /*
		    				 if (prev != null && prevCode != null && !prev.equals("OR") && (prevCourse || prev.equals("AND"))&& !(depts.contains(prev) && cnames.contains(prereq))) {
		    					 prereq = prevCode + " "+s;
		    					 System.err.println(code+" : Trying : "+prereq);
		    				 }
		    				 if (prev != null && cnames.contains(prereq)) {
		    					 //System.out.println("Prev token : "+prereq);
		    					 
		    				 } else {
		    					 if (prev != null && prev.equals("OR") && prevValid) {
		    						 //System.out.println("Or Found");
		    					 } else {
		    						 prereq = new String(code.substring(0, code.indexOf(" ")));
		    						 prereq = prereq+" "+s;
		    					 }
		    					 //System.out.println("Used course code: "+prereq);
		    				 }
		    				 */
		    				 
		    			 }
		    			 if (!prereq.equals(code) && !prereqStr.contains(prereq) && cnames.contains(prereq)) {
		    				 
		    				 if (!prereqStr.equals("")) {
		    					 prereqStr= prereqStr+",";
		    				 }
		    				 prereqStr = prereqStr + prereq;
		    				 prevValid = true;
		    				 prevCourse = true;
		    				 prevCode = new String(prereq.substring(0, prereq.indexOf(" ")));
		    			 } else {
		    				 //System.out.println("Somekind of messed up prereq");
		    				 //System.out.println(c.code + "messed up");
		    				 prevValid = false;
		    				 prevCourse = false;
		    			 }
		    		 }
		    	 }
		    	 if (!found)
		    		 prevCourse = false;
		    	 pPrev = prev;
		    	 prev = s.trim().toUpperCase();
		        	 
		     }
		     return prereqStr;
	     }
		return "";
	}

	private List<String> getDepartments() {
		List<String> depts = new ArrayList<String>();
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_departments;" );
			while(rs.next()){
				String s = rs.getString("code").trim();
				depts.add(s);
			}				
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to create new id.");			
		}
		return depts;
	}

	public void calculateAverageRatings() {
		int num = dbc.getNextID("rhun_lecturers");
		for (int i=1; i<num; i++) {
			if (i<5)
				continue;
			List<AttrVal> match = new ArrayList<AttrVal>();
			AttrVal a = new AttrVal();
			a.type = "int";
			a.attr = "lecturerID";
			a.val = ""+i;
			match.add(a);
			
			List<AttrVal> request = new ArrayList<AttrVal>();
			AttrVal id = new AttrVal();
			id.type = "int";
			id.attr = "courseID";
			
			AttrVal q = new AttrVal();
			q.type = "String";
			q.attr = "quarter";
			
			request.add(id);
			request.add(q);
			
			List<List<AttrVal>> results=dbc.getAttributesThatMatch("rhun_lecturer_course", request, match);
			
			float totalReviews = 0;
			float totalRating = 0;
			
			
			for (int j=0; j<results.size(); j++) {
				AttrVal CID = results.get(j).get(0);
				AttrVal Q = results.get(j).get(1);
				
				//System.out.println(""+CID.attr+" : "+CID.val);
				//System.out.println(""+Q.attr+" : "+Q.val);
				CID.attr = "ID";
				match = new ArrayList<AttrVal>();
				match.add(CID);
				match.add(Q);
				
				request = new ArrayList<AttrVal>();
				AttrVal rev = new AttrVal();
				rev.type = "int";
				rev.attr = "numReviews";
				
				AttrVal rate = new AttrVal();
				rate.type = "float";
				rate.attr = "rating";
				
				request.add(rev);
				request.add(rate);
				List<List<AttrVal>> courseData=dbc.getAttributesThatMatch("rhun_courses", request, match);
				if (courseData.size()!=1) {
					//System.out.println("Uhhhhhh ERROR");
					System.out.println(match.get(0).attr+" : "+match.get(0).val);
					//System.out.println(match.get(1).attr+" : "+match.get(1).val);
				} else {
					int numReviews = Integer.parseInt(courseData.get(0).get(0).val);
					float rating = Float.parseFloat(courseData.get(0).get(1).val);
					//System.out.println("Ratings: "+numReviews+" for average: "+rating);
					
					if (rating ==0 & numReviews < 10) {
						//No valid review data
					} else {
						totalReviews+=numReviews;
						totalRating+=numReviews*rating;
					}
				}
				
			}
			//System.out.println("Average Rating: "+totalRating / totalReviews);
			if (totalReviews < 10)
				System.out.println("Insignificant review data");
			else {
				dbc.updateAttribute("rhun_lecturers", i, "avgRating",""+(totalRating / totalReviews) );
			}
			//dbc.getAttribute("rhun_courses", ID, attribute);
		}
	}
	
	
	
	
	class ValueComparator implements Comparator {

		  Map base;
		  public ValueComparator(Map base) {
		      this.base = base;
		  }

		  public int compare(Object a, Object b) {

		    if((Integer)base.get(a) < (Integer)base.get(b)) {
		      return 1;
		    } else if((Integer)base.get(a) == (Integer)base.get(b)) {
		      return ((String)a).compareTo((String)b);
		    } else {
		      return -1;
		    }
		  }
		}
	
	
	
	public static void main(String[] args) {
		new dataProcessor();
	}
}
