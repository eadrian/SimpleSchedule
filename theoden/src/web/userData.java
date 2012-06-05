package web;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Iterator;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class userData {
	private String uname;
	private String pword;
	
	private static DBConnection dbc;
	public static Object dbLock;
	public static int DEFAULT_RECURSION_DEPTH = 10;
	public static int DEFAULT_INTERESTED_WORDS = 10;
	
	public Map<String, Integer> keywords;
	public Set<String> courses;
	public String major;
	public Map<String, Integer> depts;
	public Map<String, Integer> sortedWords;
	public Map<String, Integer> sortedDepts;
	public Map<String, Float> expectedCourses;
	
	public MaxentTagger tagger;
	public Map<String, String> deptTags;
	public Set<String> commonWords;
	public AxessConnect a = null;
	public List<String> coursesTaken = null;
	public List<String> titlesTaken = null;
	public String courseString;
	public List<String> UGTESTs = null;
	
	public int earliestYear = 3000;
	public int latestYear = 0;
	public String YEAR = "";
	public Map<String, Integer> deptNums;
	public Map<String, Integer> deptYearTotal;
	public Map<String, Integer> deptLatestYear;
	public String rejectedCourses = ",";
	
	public List<String> gers;
	public String GERSNeeded = "";
	
	public userData(String uname, String pword) {
		
		this.uname = uname;
		this.pword = pword;
		
		initDataStructures();
		
		try {
			tagger = new MaxentTagger("models/english-left3words-distsim.tagger");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		generateUserData(coursesTaken);
		int years = latestYear - earliestYear;
		if (years==3) {
			YEAR = "SENIOR";
		} else if (years==2) {
			YEAR = "JUNIOR";
		} else if (years==1) {
			YEAR = "SOPHOMORE";
		} else if (years==0) {
			YEAR = "FRESHMAN";
		}
		
	}
	
	public userData(String uname, String pword,MaxentTagger tagger) {
		this.uname = uname;
		this.pword = pword;
		
		
		initDataStructures();
		
		this.tagger = tagger;
		
		
		
		generateUserData(coursesTaken);
	}
	
	private Set<String> getCommonWords() {
		Set<String> s = new HashSet<String>();
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_common_words;" );
			while(rs.next()){
				String word = rs.getString("word");
				s.add(word);
				
			}				
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Was not able to create new id.");			
		}
		return s;
	}
	public void initDataStructures() {
		try {
			a = new AxessConnect(this.uname,this.pword);
			coursesTaken = a.getCourses();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		dbc = new DBConnection();
		dbLock = new Object();
		
		keywords = new HashMap<String, Integer>();
		ValueComparator bvc =  new ValueComparator(keywords);
		sortedWords = new TreeMap<String, Integer>(bvc);
		depts = new HashMap<String, Integer>();
		ValueComparator mvc =  new ValueComparator(depts);
		sortedDepts = new TreeMap<String, Integer>(mvc);
		deptLatestYear = new HashMap<String, Integer>();
		deptNums = new HashMap<String, Integer>();
		deptYearTotal = new HashMap<String, Integer>();
		expectedCourses = new HashMap<String, Float>();
		courseString = ",";
		titlesTaken = new ArrayList<String>();
		//this.major = major;
		UGTESTs = new ArrayList<String>();
		
		commonWords = getCommonWords();
		
		courses = new HashSet<String>();
	}
	private void generateUserData(List<String> coursesTaken) {
		getDeptTags();
		gers = getAllGERS();
		for (int i=1; i<coursesTaken.size(); i++) {
			String c = coursesTaken.get(i).split(a.SEPARATOR)[0];
			int year = Integer.parseInt(coursesTaken.get(i).split(a.SEPARATOR)[1]);
			
			if (year < earliestYear) {
				earliestYear = year;
			}
			if (year > latestYear) {
				latestYear = year;
				
			}
 			System.out.println(year);
			
			int index = c.indexOf(":");
			String code = new String(c.substring(0,index).trim());
			String dept = new String(code.substring(0, code.indexOf(" ")));

			
			
			if (dept.equals("UGTEST")) {
				UGTESTs.add(code);
				continue;
			}
			
			if (!deptTags.containsKey(dept)) {
				System.err.println("Did not find dept: "+dept);
				continue;
			}
			
			int num = Integer.parseInt(getCodeNum(code));
			
			String title = new String(c.substring(index+1));
			
			
			
			courses.add(code);
			
			courseString = courseString +code+",";
			
			if (!depts.containsKey(dept)) {
				depts.put(dept, 1);
			} else {
				depts.put(dept, depts.get(dept)+1);
			}
			
			if (!deptLatestYear.containsKey(dept) || year > deptLatestYear.get(dept)) {
				System.out.println(""+code+" : "+year);
				deptLatestYear.put(dept, year);
				deptNums.put(dept, num);
				deptYearTotal.put(dept, 1);
			} else if (year == deptLatestYear.get(dept)) {
				System.out.println(""+code+" : "+year);
				int deptTotal = 1;
				int deptNum = num;
				if (deptNums.containsKey(dept)) {
					deptNum = deptNums.get(dept)+num;
					deptTotal = deptYearTotal.get(dept)+1;
				}
				deptNums.put(dept, deptNum);
				deptYearTotal.put(dept, deptTotal);
			}
			
			System.err.println("Searching for course: "+code);
			String tags="";
			String GERS = "";
			try {
				Statement stmt = dbc.con.createStatement();
				stmt.executeQuery("USE " + dbc.database);
				ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_courses WHERE code = '" + code+"'");
				
				if(rs.next()) {
					tags = rs.getString("allTags");
					GERS = rs.getString("GERS");
					String ctitle = rs.getString("title").toUpperCase().trim();
					titlesTaken.add(ctitle);
					if (!GERS.equals(null)) {
						String[] gersTaken = GERS.split(" ");
						for (int j=0; j<gersTaken.length || j==0; j++) {
							gers.remove(gersTaken[j]);
						}
					}
					String prereqs = rs.getString("prereqs");
					if (!prereqs.equals("")) {
						String recPrereqs = recursiveGetPrereqs(prereqs, DEFAULT_RECURSION_DEPTH);
						//System.out.println("Normal: "+prereqs);
						//System.out.println("Recurse: "+recPrereqs);
						String[] prereqArray = recPrereqs.split(",");
						for (int j=0; j<prereqArray.length; j++) {
							String req = prereqArray[j];
							/*if (!prereqs.contains(req))
								System.out.println("UNIQUE RECURSION REQ FOUND: "+req);*/
							if (!courseString.contains(","+req+","))
								courseString = courseString+req+",";
						}
						//courseString = courseString+recPrereqs;
						//courseString=courseString+prereqs+",";
					}
					System.out.println("Found course tags: "+tags);
				} else {
					tags = deptTags.get(dept)+" "+getTags(tagger, title);
					System.out.println("Did not find course, generated tags: "+tags);
					titlesTaken.add(title.trim().toUpperCase());
				}
				
			} catch (SQLException e) {
				System.out.println("Was not able to retrieve attribute.");
				e.printStackTrace();
			}
			
			StringTokenizer st = new StringTokenizer(tags, " ;,.)/-:(", false);
		    while (st.hasMoreTokens()) {
		    	 String s = st.nextToken().trim();
		    	 if (!keywords.containsKey(s)/* && !isCommon(s)*/) {
		    		 if (!commonWords.contains(s))
		    			 keywords.put(s, 1);
		    		 else
		    			 System.out.println("Common word: "+s);
		    	 } else {
		    		 keywords.put(s, keywords.get(s)+1);
		    	 }
		    }
		    
		}
		
		
		
		
		for (Map.Entry<String, Integer> e : keywords.entrySet()) {
			sortedWords.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<String, Integer> e : sortedWords.entrySet()) {
			System.out.println(e.getKey()+" : "+e.getValue());
		}
		for (Map.Entry<String, Integer> e : depts.entrySet()) {
			sortedDepts.put(e.getKey(), e.getValue());
		}
		for (Map.Entry<String, Integer> e : sortedDepts.entrySet()) {
			System.out.println(e.getKey()+" : "+e.getValue());
			expectedCourses.put(e.getKey(), ((float)e.getValue()/((latestYear-earliestYear+1)*3)));
			System.out.println("Expected Courses for "+e.getKey()+" : "+expectedCourses.get(e.getKey()));
			//System.out.println("Average course number for dept: "+((float)deptNums.get(e.getKey())/deptYearTotal.get(e.getKey())));
			//System.out.println("Avg Rounded: "+roundDownHundred(((int)deptNums.get(e.getKey())/deptYearTotal.get(e.getKey()))));
			deptNums.put(e.getKey(), (int) ((float)deptNums.get(e.getKey())/deptYearTotal.get(e.getKey())));
		}
		Object[] deptArray = sortedDepts.keySet().toArray();
		major = (String) deptArray[0];
		System.err.println("Major: "+major);
		System.out.println("GERS Remaining:");
		for (int i=0; i<gers.size(); i++) {
			if (i==0)
				GERSNeeded = ",";
			GERSNeeded = GERSNeeded + gers.get(i)+",";
			System.out.println(gers.get(i));
		}
		
		System.err.println("Course String: "+courseString);
	}
	private String recursiveGetPrereqs(String prereqs, int depth) {
		if (prereqs==null || prereqs.equals("") || depth==0)
			return "";
		String[] reqs = prereqs.split(",");
		String prereqStr="";
		for (int i=0; i<reqs.length; i++) {
			String req = reqs[i];
			prereqStr=prereqStr+req+",";
			prereqStr=prereqStr+recursiveGetPrereqs(getPrereqs(req), depth-1);
		}
		return prereqStr;
	}

	private String getPrereqs(String ccode) {
		List<AttrVal> match = new ArrayList<AttrVal>();
		AttrVal code = new AttrVal();
    	code.type = "String";
    	code.attr = "code";
    	code.val = ccode;
    	match.add(code);
    	
    	List<AttrVal> request = new ArrayList<AttrVal>();
		AttrVal prereqs = new AttrVal();
		prereqs.type = "String";
		prereqs.attr = "prereqs";
    	request.add(prereqs);
    	List<List<AttrVal>> results = dbc.getAttributesThatMatch("rhun_courses", request, match, false, "");
    	if (results==null || results.get(0)==null || results.get(0).get(0)==null)
    		System.err.println("WOAH BIG ERROR");
    	return results.get(0).get(0).val;
	}

	private List<String> getAllGERS() {
		List<String> GERS = new ArrayList<String>();
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_reqs;" );
			
			while(rs.next()){
				String name = rs.getString("name").trim();
				if (name.contains("EC") || name.contains("DB"))
					GERS.add(name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to get GER codes.");			
		}
		return GERS;
	}

	private void getDeptTags() {
		deptTags =  new HashMap<String, String>();
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_departments;" );
			while(rs.next()){
				String name = rs.getString("code").trim();
				String title = rs.getString("title").trim();
				deptTags.put(name, getTags(tagger, title));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to get dept codes.");			
		}
		
	}
	
	
	public String getTags(MaxentTagger tagger, String str) {
		StringTokenizer st = new StringTokenizer(" "+tagger.tagString(str) + " ");
		String tags = " ";
	     while (st.hasMoreTokens()) {
	    	 String s = st.nextToken();
	         if (s.contains("/N") || s.contains("/J")) {
	        	 String tag = new String(s.substring(0, s.indexOf("/")));
	        	 tag = tag.toLowerCase().trim();
	        	 Stemmer stemmer = new Stemmer();
	        	 String stem = stemmer.stemString(tag);
	        	 //if (!tags.contains(stem))
	        	 tags= tags + stem + " ";
	        	 
	         }
	     }
	     return tags.toUpperCase();
		
	}
	
	public String getCodeDept(String code)
	{
		String deptCode = "NOT FOUND";
	     for (int j=0; j<code.length(); j++) {
	    	 if (Character.isDigit(code.charAt(j))) {
	    		 deptCode = new String(code.substring(0,j));
	    		 break;
	    	 }
	     }
	     return deptCode.toUpperCase();
	}
	
	public String getCodeNum(String code) {
		String codeNum = "NOT FOUND";
		String num="";
	     for (int j=0; j<code.length(); j++) {
	    	 if (Character.isDigit(code.charAt(j))) {
	    		 num= num+code.charAt(j);
	    	 }
	     }
	     if (num.equals(""))
	    	 return codeNum;
	     return num;
	}
	
	public void rejectCourse(String code) {
		this.rejectedCourses= this.rejectedCourses+code+",";
	}
	
	public void removeGER(String GER) {
		gers.remove(GER);
		GERSNeeded.replace(" "+GER, "");
	}
	
	public static void main(String[] args) throws Exception {
		long heapSize = Runtime.getRuntime().totalMemory();
        System.out.println("Heap Size = " + heapSize);
        //if (true) return;
		MaxentTagger t = null;
		try {
			t = new MaxentTagger("models/english-left3words-distsim.tagger");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        userData u  = new userData("eaconte","mttresp1",t);
        u.rejectCourse("CS 181");
        keywordSearch k = new keywordSearch(u.tagger);
        ScheduleFiller sf = new ScheduleFiller();
        
        Schedule s = new Schedule();
        //s.addItem("CS244", "01010", 1250, 1405);
        //s.addItem("No Fridays", "00001", 0, 2400);
        //s.addItem("ALLTIME", "11111", 0, 2400);
        searchFactors f = new searchFactors();
        //f.setFactor("RELEVANCE", 1);
        f.setFactor("INTEREST", 2);
        f.setFactor("LEVEL", 1);
        //f.setFactor("GERS", 1);
        f.setFactor("WORK", 1);
        f.setFactor("POPULARITY", 1);
        f.setFactor("INDEPENDENT", 1);
        f.setFactor("PROJECT", 1);
        f.setFactor("TOTAL", 1);
        List<String> barredCourses = new ArrayList<String>();
        barredCourses.add("CS 198");
        barredCourses.add("ENGR 70A");
        barredCourses.add("ENGR 70B");
        barredCourses.add("CS 105");
        //u.removeGER("ECGender");
        Date start = new Date();
        
        MajorReqs mr = new MajorReqs("Systems", u);  // major requirements
        //mr.removeCourse("ENGR 14");
        //mr.printReqs();
        /*if (true)
        	return;*/
        
        Iterator<String> iter = mr.reqsNeeded.iterator();
    	
        while (iter.hasNext()) {
    		
        	System.out.println("Requirement left: " + iter.next());
    	
        }
        
        TransientData tdata = new TransientData(u,s,new ArrayList<Course>(),barredCourses, mr);
        
        //GET THE SCHEDULES
        List<List<Course>> results = sf.getRandSchedules(u, tdata, f, "Spring", 2012, 10);
        
        Date finish = new Date();
        System.err.println("Time to generate schedules: "+(float)((finish.getTime()-start.getTime())/1000f));
        
        //PRINT THE SCHEDULES
        for (int i=0; i < results.size(); i++) {
        	System.out.println("Schedule "+i+":");
        	for (int j=0; j<results.get(i).size(); j++) {
        		System.out.println(results.get(i).get(j).code+" : "+results.get(i).get(j).title);
        	}
         }
         
        if (true)
        	return;
        
        
        
        k.search(u,"politics", "ALL",f,s);
        
		System.err.println("Time to search for courses: "+(float)((finish.getTime()-start.getTime())/1000f));
    }
	
	public int roundDownHundred(int num) {
		return num/100;
	}
}
