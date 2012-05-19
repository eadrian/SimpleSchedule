import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

//import dataProcessor.ValueComparator;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;


public class keywordSearch {
	
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
	
	public keywordSearch (List<String> coursesTaken, String major) {
		dbc = new DBConnection();
		dbLock = new Object();
		
		keywords = new HashMap<String, Integer>();
		ValueComparator bvc =  new ValueComparator(keywords, true);
		sortedWords = new TreeMap<String, Integer>(bvc);
		depts = new HashMap<String, Integer>();
		ValueComparator mvc =  new ValueComparator(depts, true);
		sortedDepts = new TreeMap<String, Integer>(mvc);
		this.major = major;
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
	private void generateUserData(List<String> coursesTaken) {
		getDeptTags();
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
			try {
				Statement stmt = dbc.con.createStatement();
				stmt.executeQuery("USE " + dbc.database);
				ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_courses WHERE code = '" + code+"'");
				
				if(rs.next()) {
					tags = rs.getString("allTags");
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
		    		 keywords.put(s, 1);
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
	public void search(String search) {
		List<String> keywordList = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(search, " ;,.)/-:(", false);
		List<AttrVal> match = new ArrayList<AttrVal>();
	    while (st.hasMoreTokens()) {
	    	Stemmer stemmer = new Stemmer();
	    	String token = st.nextToken().trim();
	    	keywordList.add(stemmer.stemString(token));
	    	AttrVal a = new AttrVal();
	    	a.like = true;
	    	a.type = "String";
	    	a.attr = "tags";
	    	a.val = "%"+stemmer.stemString(token).toUpperCase()+"%";
	    	match.add(a);
	    }
	    List<AttrVal> request = new ArrayList<AttrVal>();
	    AttrVal r = new AttrVal();
	    r.type="String";
	    r.attr = "code";
	    
	    AttrVal r2 = new AttrVal();
	    r2.type="float";
	    r2.attr = "qScore";
	    
	    AttrVal r3 = new AttrVal();
	    r3.type="float";
	    r3.attr = "wScore";
	    
	    AttrVal r4 = new AttrVal();
	    r4.type="float";
	    r4.attr = "nScore";
	    
	    AttrVal r5 = new AttrVal();
	    r5.type="String";
	    r5.attr = "tags";
	    
	    AttrVal r9 = new AttrVal();
	    r9.type="float";
	    r9.attr = "tScore";
	    
	    request.add(r);
	    request.add(r2);
	    request.add(r3);
	    request.add(r4);
	    request.add(r5);
	    request.add(r9);
	    List<List<AttrVal>> results = dbc.getAttributesThatMatch("rhun_courses", request, match, true, "tScore");
	    
	    
	    Map<String, Float> rateMap = new TreeMap<String, Float>();
		FloatComparator bvc =  new FloatComparator(rateMap);
		Map<String, Float> sortedResults = new TreeMap<String, Float>(bvc);
	    
	    for (int i=0; i<results.size(); i++) {
	    	
	    	String code = results.get(i).get(0).val;
			System.out.println("Code: "+code+" Score: "+Float.parseFloat(results.get(i).get(5).val));
			float q = Float.parseFloat(results.get(i).get(1).val);
			float w = Float.parseFloat(results.get(i).get(2).val);
			float n = Float.parseFloat(results.get(i).get(3).val);
			if (!rateMap.containsKey(code)) {
				rateMap.put(code, q+w+n);
				sortedResults.put(code, q+w+n);
			}
	    }
	    /*
	    for (Map.Entry<String, Float> e : sortedResults.entrySet()) {
            System.out.println("key/value: " + e.getKey() + "/"+e.getValue());
            
        }*/
		String codeNum = "";
	     /*for (int j=0; j<c.code.length(); j++) {
	    	 if (Character.isDigit(c.code.charAt(j))) {
	    		 codeNum = codeNum+c.code.charAt(j);
	    	 }
	     }*/
	}
	
	String getTags(MaxentTagger tagger, String str) {
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
	
	
}
