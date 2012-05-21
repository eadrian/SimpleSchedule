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
	private MaxentTagger tagger;
	
	public userData data;
	
	public keywordSearch (userData u, String major) {
		dbc = new DBConnection();
		dbLock = new Object();
		data = u;
		
		
		
		
		try {
			tagger = new MaxentTagger("models/english-left3words-distsim.tagger");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void search(String search, String quarter, boolean sortByDesirability, boolean sortByRelevance, List<searchFactor> factors, Schedule sched) {
		List<Course> results = searchForCourses(search, quarter);
		for (int i=0; i<results.size(); i++) {
			System.out.println(results.get(i).code);
		}
		if (sortByDesirability || sortByRelevance) { 
			float desirabilityFactor = sortByDesirability ? 1 : 0;
			float relevanceFactor = sortByRelevance ? 1 : 0;
			float total = desirabilityFactor+relevanceFactor;
			sortResults(results, desirabilityFactor/total, relevanceFactor/total, sched);
		}
		
	}
	
	private void sortResults(List<Course> results, float dFactor, float rFactor, Schedule sched) {
		System.out.println("dFactor: "+dFactor+" rFactor:"+rFactor);
		
		for (int i=0; i<results.size(); i++) {
			Course c = results.get(i);
			System.out.println("Trying to fit: "+c.code);
			if (sched.checkFit(c.code,c.lectureDays, c.timeBegin, c.timeEnd)) {
				
			} else {
				
			}
				
			
		}
		
	}

	public List<Course> searchForCourses(String search, String quarter) {
		List<String> keywordList = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(search, " ;,.)/-:(", false);
		
		List<AttrVal> match = new ArrayList<AttrVal>();
	    while (st.hasMoreTokens()) {
	    	
	    	String token = st.nextToken().trim();
	    	keywordList.add(token);
	    	
	    }
	    
	    
	    
	    List<Course> cResults = null;
	    
	    //If a single word entered check for class or department
	    if (keywordList.size()==1) {
	    	cResults = getCourseCodeMatches(keywordList.get(0), quarter);
	    	
	    	if (cResults==null) {
	    		//If no results found, try the single word as a department code
	    		
	    		cResults = getDeptCodeMatches(keywordList.get(0),quarter);
	    	}
	    } else if (keywordList.size()==2) {
	    //If two words check for a class match
	    	String combined = keywordList.get(0)+keywordList.get(1);
	    	cResults = getCourseCodeMatches(combined, quarter);
	    }
	    
	    if (cResults!=null)
	    	return cResults;
	    
	    
	    List<String> taggedSearch = new ArrayList<String>();
		String searchTags = getTags(tagger, search);
		st = new StringTokenizer(searchTags, " ;,.)/-:(", false);
		
	    while (st.hasMoreTokens()) {
	    	
	    	taggedSearch.add(st.nextToken());
	    	
	    }
	    System.err.println("No courses or depts found, using keywords...");
	    
	    
	    for (int i=0;i<taggedSearch.size();i++) {
	    	String word = taggedSearch.get(i);
	    	AttrVal a = new AttrVal();
	    	a.like = true;
	    	a.type = "String";
	    	a.attr = "allTags";
	    	System.out.println("Searchword: "+word);
	    	a.val = "%"+word+"%";
	    	match.add(a);
	    }
	    
	    if (!quarter.equals("ALL")) {
    		AttrVal q = new AttrVal();
    		q.type = "String";
    		q.attr = "quarter";
    		q.val = quarter;
        	match.add(q);
    	}
	   
	    cResults = dbc.getCoursesThatMatch(match);
	    
	    if (cResults!=null)
	    	return cResults;
	    
	    match = new ArrayList<AttrVal>();
	    for (int i=0;i<keywordList.size();i++) {
	    	String word = keywordList.get(i).toUpperCase();
	    	AttrVal a = new AttrVal();
	    	a.like = true;
	    	a.type = "String";
	    	a.attr = "allTags";
	    	System.out.println("Searchword: "+word);
	    	a.val = "%"+word+"%";
	    	match.add(a);
	    }
	   
	    
	    if (!quarter.equals("ALL")) {
    		AttrVal q = new AttrVal();
    		q.type = "String";
    		q.attr = "quarter";
    		q.val = quarter;
        	match.add(q);
    	}
	    
	    
	    cResults = dbc.getCoursesThatMatch(match);
	    
	    
	    return cResults;
	    /*
	    
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
            
        }
		String codeNum = "";
	     /*for (int j=0; j<c.code.length(); j++) {
	    	 if (Character.isDigit(c.code.charAt(j))) {
	    		 codeNum = codeNum+c.code.charAt(j);
	    	 }
	     }*/
	}
	
	private List<Course> getDeptCodeMatches(String string, String q) {
		
		List<AttrVal> match = new ArrayList<AttrVal>();
		AttrVal code = new AttrVal();
    	code.type = "String";
    	code.attr = "deptCode";
    	code.val = string.toUpperCase();
    	match.add(code);
    	
    	if (!q.equals("ALL")) {
    		AttrVal quarter = new AttrVal();
    		quarter.type = "String";
    		quarter.attr = "quarter";
    		quarter.val = q;
        	match.add(quarter);
    	}
		
    	List<Course> results = dbc.getCoursesThatMatch(match);
    	for (int i=0; i<results.size(); i++) {
    		System.out.println(""+i+": "+results.get(i).code);
    	}
    	return results;
	}

	private List<Course> getCourseCodeMatches(String combined, String q) {
		
		String deptCode = getCodeDept(combined);
		if (deptCode.equals("NOT FOUND"))
			return null;
		String codeNum = getCodeNum(combined);
		List<AttrVal> match = new ArrayList<AttrVal>();
		AttrVal code = new AttrVal();
    	code.type = "String";
    	code.attr = "code";
    	code.val = deptCode+" "+codeNum;
    	match.add(code);
    	if (!q.equals("ALL")) {
    		AttrVal quarter = new AttrVal();
    		quarter.type = "String";
    		quarter.attr = "quarter";
    		quarter.val = q;
        	match.add(quarter);
    	}
    	
    	
    	List<Course> results = dbc.getCoursesThatMatch(match);
    	for (int i=0; i<results.size(); i++) {
    		System.out.println(""+i+": "+results.get(i).code);
    	}
    	return results;
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
	
}
