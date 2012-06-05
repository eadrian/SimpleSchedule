

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MajorReqs {
	private static DBConnection dbc;
	public static List<String> reqsNeeded = null;			// stores list of names of fulfilled major requirements 
	public static List<Integer> allReqs; 					// stores list of ids of all major requirements (fulfilled and unfulfilled)
	public Map<String, List<String>> reqsFulfilling;
	public Map<String, List<String>> sortedReqs;
	public Map<String, MajorReq> courseReqs;
	
	
	class MajorReq {
		public String name;
		public List<String> codes;
	}
	
	public MajorReqs(String major, userData ud) {
		dbc = new DBConnection();
		
		reqsNeeded = new ArrayList<String>();
		allReqs = new ArrayList<Integer>();	
		reqsFulfilling = new HashMap<String, List<String>>();
		reqCompare r = new reqCompare(reqsFulfilling);
		sortedReqs = new TreeMap<String, List<String>>(r);
		courseReqs = new HashMap<String, MajorReq>();
		
		
		List<Integer> allReqGroups = new ArrayList<Integer>();		// list of ids of reqGroups needed
		allReqGroups = dbc.getAllReqGroups(major);

		// get list of all requirements needed
		allReqs = dbc.getAllReqs(allReqGroups);
		
		for (int i=0; i<allReqs.size(); i++) {
			String reqName = dbc.getAttribute("rhun_reqs", allReqs.get(i), "name");
			List<String> cs = getCoursesFulfillingMR(reqName);
			//System.out.println(reqName+" : "+allReqs.get(i));
			//System.out.println(cs);
			reqsFulfilling.put(reqName, cs);
			sortedReqs.put(reqName, cs);
		}
		/*System.err.println("Printing Reqs");
		for (String key : sortedReqs.keySet()) {
			System.out.println(key);
			System.out.println(sortedReqs.get(key));
		}
		
		*/
		
		generateData(major, ud);
		/*
		System.out.println("Reqs Remaining: ");
		for (String key : sortedReqs.keySet()) {
			System.out.println(key);
		}*/
		
		//generateMajorReqsLeft(major, ud);
	}
	
	private void generateData(String major, userData ud) {
		// TODO Auto-generated method stub
		Iterator i = ud.courses.iterator();
		while (i.hasNext()) {
			String code = (String) i.next();
			addCourse(code);
		}
		
		i = ud.UGTESTs.iterator();
		while (i.hasNext()) {
			String code = (String) i.next();
			while (addCourse(code)){}
			
		}
	}

	public boolean addCourse(String code) {
		Iterator it = sortedReqs.entrySet().iterator();
		System.out.println("Looking for: "+code);
		String req = "";
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        List<String> reqs = ((List<String>)pairs.getValue());
	        //System.out.println("Search: "+code+" Req: "+pairs.getKey()+" : "+reqs);
	        if (reqs.contains(code)) {
	        	req = (String) pairs.getKey();
	        	
	        	break;
	        }
	    }
	    if (!req.equals("")) {
	    	MajorReq m = new MajorReq();
	    	m.name = req;
	    	m.codes = sortedReqs.get(m.name);
	    	courseReqs.put(code, m);
	    	sortedReqs.remove(req);
	    	reqsFulfilling.remove(req);
	    	System.out.println(code+"Fulfilled: "+req);
	    	
	    	
	    	return true;
	    }
	    return false;
	}
	public String getReq(String code) {
		Iterator it = sortedReqs.entrySet().iterator();
		
		String req = "";
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        if (((List<String>)pairs.getValue()).contains(code)) {
	        	req = (String) pairs.getKey();
	        	break;
	        }
	    }
	    return req;
	}
	
	public void removeCourse(String code) {
		if (courseReqs.containsKey(code)) {
			MajorReq m = courseReqs.get(code);
			reqsFulfilling.put(m.name, m.codes);
			sortedReqs.put(m.name, m.codes);
			courseReqs.remove(code);
		}
	}
	
	public void printReqs() {
		System.out.println("Reqs Remaining: ");
		for (String key : sortedReqs.keySet()) {
			System.out.println(key);
		}
	}
	
	private static void generateMajorReqsLeft(String major, userData ud) {
		List<String> testsTaken = ud.UGTESTs;
		Set<String> cTaken = ud.courses;
		generateMajorReqsLeft(major, cTaken, testsTaken);
	}
	
	private static void generateMajorReqsLeft(String major, Set<String> cTaken, List<String> testsTaken) {

		// get list of requirement groups needed
		List<Integer> allReqGroups = new ArrayList<Integer>();		// list of ids of reqGroups needed
		allReqGroups = dbc.getAllReqGroups(major);

		// get list of all requirements needed
		allReqs = dbc.getAllReqs(allReqGroups);
		
		// modify list of requirements to only include ones unfulfilled
		// mark off the requirements fulfilled by courses taken
		dbc.checkOffReqs(allReqs, cTaken);							// checks off requirements based on rarity
		// mark off requirements fulfilled by ap/ib tests
		dbc.checkOffReqs_UGTests(allReqs, testsTaken);
		
		reqsNeeded = new ArrayList<String>();						// stores the names of the requirements to fulfill
		// get names
		if (allReqs.size() == 0) System.out.println("No major reqs left to fulfill.");
		for (int i = 0; i < allReqs.size(); i++) {
			String reqName = dbc.getAttribute("rhun_reqs", allReqs.get(i), "name");
			System.out.println("unfulfilled: " + reqName);
			reqsNeeded.add(reqName);
		}
	}
	
	// returns the rarest major requirement that a given course fulfills
	public static String getFulfilledReq(String courseCode) {
		return dbc.getRarestReq(allReqs, courseCode);
	}
	
	// returns list of course codes that fulfills a single major req passed in
	public static List<String> getCoursesFulfillingMR(String majorReq) {
		return dbc.getCoursesThatFulfillMR(majorReq);		
	}

	public static void main(String[] args) throws Exception {
		// getting list of reqs still unfulfilled
		Set<String> cTaken = new HashSet<String>();
		List<String> testsTaken = new ArrayList<String>();
		cTaken.add("EE 108A");
		
		cTaken.add("EE 101A");
		
		cTaken.add("CS 143");
		cTaken.add("ME 210");		
		cTaken.add("CS 144");
		cTaken.add("CS 140");	
		
		cTaken.add("Math 51");
		cTaken.add("CS 161");
		cTaken.add("CS 103");
		
		cTaken.add("Engr 40");
		cTaken.add("ARTSTUD 60");
		cTaken.add("CS 106B");
		reqsNeeded = new ArrayList<String>();
		dbc = new DBConnection();
		generateMajorReqsLeft("Computer Engineering", cTaken, testsTaken);
		
		// getting list of courses that fulfill a given major req
		List<String> listReqsFulfilled = getCoursesFulfillingMR("Math Elective");
		Iterator<String> iter = listReqsFulfilled.iterator();
		while (iter.hasNext()) {
			//System.out.println("Course that fulfills math elective: " + iter.next());
		}
	}
	
	class reqCompare implements Comparator
    {
		public Map<String, List<String>> map;
		public reqCompare(Map<String, List<String>> m) {
			map = m;
		}
		
        public int compare(Object o1,Object o2)
        {
        	int com =  map.get(((String)o1)).size()-map.get(((String)o2)).size();
        	if (com == 0)
        		return ((String)o1).compareTo((String)o2);
            return com;
        }
    }

}
