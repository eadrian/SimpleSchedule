import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class userData {
	private String uname;
	private String pword;
	
	private static DBConnection dbc;
	public static Object dbLock;
	
	public Map<String, Integer> keywords;
	public Set<String> courses;
	public String major;
	public Map<String, Integer> depts;
	public Map<String, Integer> sortedWords;
	public Map<String, Integer> sortedDepts;
	public MaxentTagger tagger;
	public Map<String, String> deptTags;
	public Set<String> commonWords;
	public AxessConnect a = null;
	public List<String> coursesTaken = null;
	
	public userData(String uname, String pword) {
		this.uname = uname;
		this.pword = pword;
		try {
			a = new AxessConnect(uname,pword);
			coursesTaken = a.getCourses();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		dbc = new DBConnection();
		dbLock = new Object();
		
		keywords = new HashMap<String, Integer>();
		ValueComparator bvc =  new ValueComparator(keywords, true);
		sortedWords = new TreeMap<String, Integer>(bvc);
		depts = new HashMap<String, Integer>();
		ValueComparator mvc =  new ValueComparator(depts, true);
		sortedDepts = new TreeMap<String, Integer>(mvc);
		//this.major = major;
		 
		
		commonWords = getCommonWords();
		
		courses = new HashSet<String>();
		
		
		
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
	private void generateUserData(List<String> coursesTaken) {
		getDeptTags();
		List<String> gers = getAllGERS();
		for (int i=0; i<coursesTaken.size(); i++) {
			String c = coursesTaken.get(i);
			int index = c.indexOf(":");
			String code = new String(c.substring(0,index).trim());
			String dept = new String(code.substring(0, code.indexOf(" ")));
			
			if (dept.equals("UGTEST"))
				continue;
			
			if (!deptTags.containsKey(dept)) {
				System.err.println("Did not find dept: "+dept);
				continue;
			}
			
			String title = new String(c.substring(index+1));
			
			
			
			courses.add(code);
			
			if (!depts.containsKey(dept)) {
				depts.put(dept, 1);
			} else {
				depts.put(dept, depts.get(dept)+1);
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
					if (!GERS.equals(null)) {
						String[] gersTaken = GERS.split(" ");
						for (int j=0; j<gersTaken.length || j==0; j++) {
							gers.remove(gersTaken[j]);
						}
					}
					System.out.println("Found course tags: "+tags);
				} else {
					tags = deptTags.get(dept)+" "+getTags(tagger, title);
					System.out.println("Did not find course, generated tags: "+tags);
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
		}
		Object[] deptArray = sortedDepts.keySet().toArray();
		major = (String) deptArray[deptArray.length-1];
		System.err.println("Major: "+major);
		System.out.println("GERS Remaining:");
		for (int i=0; i<gers.size(); i++) {
			System.out.println(gers.get(i));
		}
	}
	private List<String> getAllGERS() {
		List<String> GERS = new ArrayList<String>();
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_reqs;" );
			
			while(rs.next()){
				String name = rs.getString("name").trim();
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
	     for (int j=0; j<code.length(); j++) {
	    	 if (Character.isDigit(code.charAt(j))) {
	    		 codeNum = new String(code.substring(j));
	    		 break;
	    	 }
	     }
	     return codeNum.toUpperCase();
	}
	
	public static void main(String[] args) throws Exception {
        userData u  = new userData("eaconte","mttresp1");
        
        keywordSearch k = new keywordSearch(u, "CS");
        
        Schedule s = new Schedule();
        s.addItem("CS244", "01010", 1250, 1405);
        //s.addItem("ALLTIME", "11111", 0, 2400);
        k.search("computer networks", "Spring", true, true,null,s);
    }
}
