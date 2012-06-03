package web;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MajorReqs {
	private static DBConnection dbc;
	public static List<String> reqsNeeded = null;
	
	public MajorReqs(String major, userData ud) {
	//public MajorReqs(String major, List<String> coursesTaken) {
		reqsNeeded = new ArrayList<String>();
		dbc = new DBConnection();
		generateMajorReqsLeft(major, ud);
	}
	
	private static void generateMajorReqsLeft(String major, userData ud) {
		List<String> testsTaken = ud.UGTESTs;
		Set<String> cTaken = ud.courses;
		generateMajorReqsLeft(major, cTaken, testsTaken);
	}
	
	private static void generateMajorReqsLeft(String major, Set<String> cTaken, List<String> testsTaken) {

		// get list of requirement groups needed
		List<Integer> allReqGroups = new ArrayList<Integer>();		// list of ids of reqGroups needed
		allReqGroups = dbc.getAllReqGroups("Systems");

		// get list of all requirements needed
		List<Integer> allReqs = new ArrayList<Integer>();	
		allReqs = dbc.getAllReqs(allReqGroups);
		
		// modify list of requirements to only include ones unfulfilled
		// mark off the requirements fulfilled by courses taken
		dbc.checkOffReqs(allReqs, cTaken);	
		// mark off requirements fulfilled by ap/ib tests
		dbc.checkOffReqs_UGTests(allReqs, testsTaken);
		
		reqsNeeded = new ArrayList<String>();		// stores the names of the requirements to fulfill
		// get names
		if (allReqs.size() == 0) System.out.println("No major reqs left to fulfill.");
		for (int i = 0; i < allReqs.size(); i++) {
			String reqName = dbc.getAttribute("rhun_reqs", allReqs.get(i), "name");
			System.out.println("needed: " + reqName);
			reqsNeeded.add(reqName);
		}
	}
	

	public static void main(String[] args) throws Exception {
		Set<String> cTaken = new HashSet<String>();
		List<String> testsTaken = new ArrayList<String>();
		cTaken.add("CS 143");
		cTaken.add("EE 108B");
		cTaken.add("Math 51");
		cTaken.add("Math 21");
		cTaken.add("CS 161");
		cTaken.add("Engr 40");
		cTaken.add("Engr 50");
		cTaken.add("CS 140"); 
		cTaken.add("CS 106B");
		cTaken.add("CS 147");
		cTaken.add("CS 103");
		//MajorReqs mr = new MajorReqs("Systems", cTaken);
		reqsNeeded = new ArrayList<String>();
		dbc = new DBConnection();
		generateMajorReqsLeft("Systems", cTaken, testsTaken);
	}

}
