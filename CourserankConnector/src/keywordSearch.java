import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
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
	
	
	
	public static float DEFAULT_MAX_SCORE = 100;
	public static float REL_MAX = DEFAULT_MAX_SCORE;
	
	public keywordSearch (MaxentTagger tagger) {
		dbc = new DBConnection();
		dbLock = new Object();
		
		
		
		
		this.tagger = tagger;
		
	}
	
	public List<Course> search(userData u,String search, String quarter, searchFactors factors, Schedule sched) {
		List<Course> results = searchForCourses(search, quarter);
		for (int i=0; i<results.size(); i++) {
			System.out.println(results.get(i).code);
		}
		
		results = sortResults(u,results, sched, factors, search);
		return results;
		
	}
	
	public List<Course> sortResults(userData data, List<Course> results, Schedule sched, searchFactors factors, String search) {
		Map<String, Course> courseMap = new HashMap<String, Course>();
		Map<String, Float> scoreMap = new HashMap<String, Float>();
		FloatComparator fvc =  new FloatComparator(scoreMap);
		Map<String, Float> sortedScores = new TreeMap<String, Float>(fvc);
		List<Course> courses = new ArrayList<Course>();
		
		for (int i=0; i<results.size(); i++) {
			Course c = results.get(i);
			
			float score = scoreCourse(data,c, sched, factors, search);
			c.totalScore = score;
			courses.add(c);
			courseMap.put(c.code, c);
			scoreMap.put(c.code, score);
			sortedScores.put(c.code, score);
			
			
			
			
			
		}
		Collections.sort(courses, CourseSorter.COMPARATOR);
		
		for (String key : sortedScores.keySet()) {
			//System.out.println(key+" : "+scoreMap.get(key));
		}
		return courses;
		
	}

	private float scoreCourse(userData data, Course c,Schedule sched, searchFactors factors, String search) {
		//System.out.println("Trying to fit: "+c.code);
		if (sched.checkFit(c.code,c.lectureDays, c.timeBegin, c.timeEnd)) {
			
		} else {
			
			return 0;
		}
		//System.out.println("Checking prereqs for "+c.code);
		if (prereqFulfilled(c.prereqs, data.courseString))	{
			
		} else {
			return 0;
		}
		if (alreadyTaken(data, c)) {
			return 0;
		}
		
		if (data.rejectedCourses.contains(","+c.code+",")) {
			return 0;
		}
		
		float totalScore = 0;
		
		
		//System.out.println("Relevance: "+c.relevance);
		if (factors.factorWeight.get("RELEVANCE")>0) {
			//System.out.println("Determining Relevance");
			float relevance = calculateRelevance(c, search);
			totalScore+=relevance * factors.factorWeight.get("RELEVANCE");
		}
		
		if (factors.factorWeight.get("PREREQS")>0) {
			//System.out.println("Determining PreScore");
			float preScore = c.preScore;
			//System.out.println("PreScore for "+c.code+" : "+preScore);
			totalScore+=preScore * factors.factorWeight.get("PREREQS");
		}
		
		
		if (factors.factorWeight.get("INTEREST")>0) {
			//System.out.println("Determining Interest");
			float interest = calculateInterest(data,c);
			//System.out.println("Interest in "+c.code+" : "+interest);
			totalScore+=interest * factors.factorWeight.get("INTEREST");
		}
		
		if (factors.factorWeight.get("LEVEL")>0) {
			//System.out.println("Determining Level Match");
			float level = calculateLevel(data,c);
			//System.out.println("Level Match for "+c.code+" : "+level);
			totalScore+=level * factors.factorWeight.get("LEVEL");
		}
		
		if (factors.factorWeight.get("GERS")>0) {
			//System.out.println("Determining GER Score");
			float GERScore = calculateGERScore(data,c);
			//System.out.println("GER score for "+c.code+" : "+GERScore);
			totalScore+=GERScore * factors.factorWeight.get("GERS");
		}
		
		if (factors.factorWeight.get("WORK")>0) {
			//System.out.println("Determining Work Score");
			float wScore = c.wScore;
			//System.out.println("Level Work Score for "+c.code+" : "+wScore);
			totalScore+=wScore * factors.factorWeight.get("WORK");
		}
		
		if (factors.factorWeight.get("POPULARITY")>0) {
			//System.out.println("Determining Popularity");
			float nScore = c.nScore;
			//System.out.println("Popularity for "+c.code+" : "+nScore);
			totalScore+=nScore * factors.factorWeight.get("POPULARITY");
		}
		
		if (factors.factorWeight.get("INDEPENDENT")>0) {
			//System.out.println("Determining Independent");
			float indep = c.title.toUpperCase().contains("INDEPENDENT") ? 0 : 100;
			//System.out.println("Independent for "+c.code+" : "+indep);
			totalScore+=indep * factors.factorWeight.get("INDEPENDENT");
		}
		
		if (factors.factorWeight.get("PROJECT")>0) {
			//System.out.println("Determining Project");
			float proj = c.title.toUpperCase().contains("PROJECT") ? 0 : 100;
			//System.out.println("Project for "+c.code+" : "+proj);
			totalScore+=proj * factors.factorWeight.get("PROJECT");
		}
		if (factors.factorWeight.get("TOTAL")>0) {
			//System.out.println("Determining Total");
			float tscore = (float) (.25*c.tScore);
			//System.out.println("TScore for "+c.code+" : "+tscore);
			totalScore+=tscore * factors.factorWeight.get("TOTAL");
		}
		
		if (c.description.toUpperCase().contains("PREFERENCE TO FRESHMEN") && !data.YEAR.equals("FRESHMAN")) {
			
		} else if (c.description.toUpperCase().contains("PREFERENCE TO SOPHOMORES")&& !data.YEAR.equals("SOPHOMORE")) {
			
		} else {
			totalScore+=DEFAULT_MAX_SCORE;
		}
		return totalScore;
	}

	private float calculateGERScore(userData data, Course c) {
		float score = 0;
		String GERS = c.universityReqs;
		String GERSused = " ";
		if (!GERS.equals(null)) {
			String[] gersTaken = GERS.split(" ");
			for (int j=0; j<gersTaken.length || j==0; j++) {
				String GER = gersTaken[j];
				if (!GERSused.contains(GER)) {
					GERSused = GERSused + GER+ " ";
					if (data.GERSNeeded.contains(","+GER+","))
						score+=DEFAULT_MAX_SCORE;
				}
			}
		}
		return score;
	}

	private boolean alreadyTaken(userData data, Course c) {
		return data.courseString.contains(","+c.code+",");
	}

	private float calculateLevel(userData data, Course c) {
		int num = data.roundDownHundred(c.deptNum);
		int deptAvg = -1;
		if (data.deptNums.containsKey(c.deptCode)) {
			deptAvg = data.deptNums.get(c.deptCode);
		}
		//System.out.println(c.code+" num : "+num);
		if (deptAvg == num || deptAvg +1 == num) {
			return DEFAULT_MAX_SCORE;
		} else if (num < deptAvg)
			return DEFAULT_MAX_SCORE*3 / 4;
		else if (!data.deptNums.containsKey(c.deptCode) && num > 1)
			return DEFAULT_MAX_SCORE / 3;
		else
			return DEFAULT_MAX_SCORE / 2;
	}

	private float calculateInterest(userData data, Course c) {
		int i=0;
		int total = 0;
		List<String> interestedWords = new ArrayList<String>();
		
		float interest = 0;
		float maxInterest = DEFAULT_MAX_SCORE;
		
		for (String key : data.sortedWords.keySet()) {
			if (i>data.DEFAULT_INTERESTED_WORDS)
				break;
			i++;
			interestedWords.add(key);
			total+=data.keywords.get(key);
			System.out.println(key);
		}
		int words = interestedWords.size();
		for (int j=0; j<words; j++) {
			String word = interestedWords.get(j);
			
			int appearances = getAppearances(c, word);
			//System.out.println("Keyword: "+word+" apps: "+data.keywords.get(word)+" total: "+total+" value: "+((float)data.keywords.get(word))/(total));
			if (appearances>1) {
				//Frequent or more than one appearance in tags.
				//System.out.println("High interest match for: "+word);
				interest+=maxInterest*((float)data.keywords.get(word))/(total);
			} else if (appearances == 1){
				//Rare, probably not a great match on this tag.
				interest+=maxInterest*((float)data.keywords.get(word))/(1.5*total);
			}
		}
		System.out.println("Interest Score: "+interest+" : "+c.code);
		return interest;
	}

	private float calculateRelevance(Course c, String search) {
		
		List<String> searchTokens = getSearchTokens(search, true);
		
		
		int tokens = searchTokens.size();
		
		float relevance = 0;
		float maxRelevance = c.relevance;
		
		if (c.relevance < REL_MAX) {
		
			for (int i=0; i<tokens; i++) {
				
				String token = searchTokens.get(i);
				int appearances = getAppearances(c, token);
				
				if (appearances>1) {
					//Frequent or more than one appearance in tags.  
					relevance+=maxRelevance/tokens;
				} else {
					//Rare, probably not a great match on this tag.
					relevance+=maxRelevance/(1.5*tokens);
				}
			}
			//System.out.println("Total relevance: "+relevance);
			return relevance;
		} else
			return REL_MAX;
	    /*
		String searchTags = getTags(tagger, search);
		if (c.code.equals("CS 144"))
			System.out.println(c.allTags);
		System.out.println("SEARCH TAGS: "+searchTags);
		if (c.allTags.contains(searchTags)) {
			System.out.println("All tags matched: "+c.code);
		} else {
			System.out.println("All tags unmatched: "+c.code);
			System.out.println(c.allTags);
		}
		st = new StringTokenizer(searchTags, " ;,.)/-:(", false);
		/*
	    while (st.hasMoreTokens()) {
	    	
	    	//taggedSearch.add(st.nextToken());
	    	
	    }*/
		
	}

	private int getAppearances(Course c, String token) {
		int appearances = 0;
		if (c.tags.contains(" "+token+" ")) {
			//System.out.println(token+" appears in "+c.code+" : tags");
			appearances++;
		}
		if (c.titleTags.contains(" "+token+" ")) {
			//System.out.println(token+" appears in "+c.code+" : title tags");
			appearances++;
		}
		if (c.deptTags.contains(" "+token+" ")) {
			//System.out.println(token+" appears in "+c.code+" : dept tags");
			appearances++;
		}
		return appearances;
	}

	private boolean prereqFulfilled(List<String> prereqs, String courseString) {
		for (int i=0; i<prereqs.size(); i++) {
			String req = prereqs.get(i).trim();
			req = ","+req+",";
			if (!courseString.contains(req)) {
				System.err.println("Prereq not fulfilled: "+req);
				return false;
			}
		}
		return true;
	}

	public List<Course> searchForCourses(String search, String quarter) {
		List<String> keywordList = new ArrayList<String>();
		
		
		List<AttrVal> match = new ArrayList<AttrVal>();
		
		List<Course> allResults = new ArrayList<Course>();
		List<Course> cResults = null;
		List<String> quotedTokens = new ArrayList<String>();
		/*
		while (search.contains("\"") && search.indexOf("\"") != search.lastIndexOf("\"")) {
			int index = search.indexOf("\"");
			String quoted = new String(search.substring(index+1,search.indexOf("\"", index+1)));
			search = search.replace("\""+quoted+"\"", "");
			quotedTokens.add(quoted);
		}
		StringTokenizer st = new StringTokenizer(search, " ;,.)/-:(", false);
	    while (st.hasMoreTokens()) {
	    	
	    	String token = st.nextToken().trim();
	    	keywordList.add(token);
	    	
	    }
	    for (int i=0; i<quotedTokens.size(); i++) {
	    	keywordList.add(quotedTokens.get(i).trim());
	    }
	    
	    
	    */
		keywordList = getSearchTokens(search, false);
	    
	    float relevance = 0;
	    
	    //If a single word entered check for class or department
	    if (keywordList.size()==1) {
	    	cResults = getCourseCodeMatches(keywordList.get(0), quarter);
	    	relevance = REL_MAX;
	    	if (cResults==null) {
	    		//If no results found, try the single word as a department code
	    		
	    		cResults = getDeptCodeMatches(keywordList.get(0),quarter);
	    		relevance = REL_MAX;
	    	}
	    } else if (keywordList.size()==2) {
	    //If two words check for a class match
	    	String combined = keywordList.get(0)+keywordList.get(1);
	    	cResults = getCourseCodeMatches(combined, quarter);
	    	relevance = REL_MAX;
	    }
	    
	    if (cResults!=null) {
	    	setRelevance(cResults, relevance);
	    	for (int i=0; i<cResults.size(); i++) {
	    		if (!allResults.contains(cResults.get(i))) {
	    			allResults.add(cResults.get(i));
	    		}
	    	}
	    	//return cResults;
	    }
	    
	    
	    List<String> taggedSearch = new ArrayList<String>();
	    /*
		String searchTags = getTags(tagger, search);
		st = new StringTokenizer(searchTags, " ;,.)/-:(", false);
		
	    while (st.hasMoreTokens()) {
	    	
	    	taggedSearch.add(st.nextToken().trim());
	    	
	    }
	    for (int i=0; i<quotedTokens.size(); i++) {
	    	taggedSearch.add(getTags(tagger,quotedTokens.get(i).trim()).trim());
	    }
	    System.err.println("No courses or depts found, using keywords...");
	    
	    */
	    taggedSearch = getSearchTokens(search, true);
	    for (int i=0;i<taggedSearch.size();i++) {
	    	String word = taggedSearch.get(i);
	    	AttrVal a = new AttrVal();
	    	a.like = true;
	    	a.type = "String";
	    	a.attr = "allTags";
	    	System.out.println("Searchword: "+word);
	    	a.val = "% "+word+" %";
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
	    if (cResults!=null) {
	    	setRelevance(cResults, REL_MAX*9/10);
	    	for (int i=0; i<cResults.size(); i++) {
	    		if (!allResults.contains(cResults.get(i))) {
	    			allResults.add(cResults.get(i));
	    		}
	    	}
	    	//return cResults;
	    }
	    match = new ArrayList<AttrVal>();
	    for (int i=0;i<keywordList.size();i++) {
	    	String word = keywordList.get(i).toUpperCase();
	    	AttrVal a = new AttrVal();
	    	a.like = true;
	    	a.type = "String";
	    	a.attr = "allTags";
	    	//System.out.println("Searchword: "+word);
	    	a.val = "% "+word+" %";
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
	    
	    if (cResults!=null) {
	    	setRelevance(cResults, REL_MAX/2);
	    	for (int i=0; i<cResults.size(); i++) {
	    		if (!allResults.contains(cResults.get(i))) {
	    			allResults.add(cResults.get(i));
	    		}
	    	}
	    	//return cResults;
	    }
	    return allResults;
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
    	if (results==null)
    		return null;
    	for (int i=0; i<results.size(); i++) {
    		//System.out.println(""+i+": "+results.get(i).code);
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
    	if (results==null)
    		return null;
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
	
	public void setRelevance(List<Course> courses, float rel) {
		for (int i=0; i<courses.size(); i++) {
			Course c = courses.get(i);
			c.relevance = rel;
			courses.set(i, c);
		}
	}
	
	public List<String> getSearchTokens(String searchStr, boolean tag) {
		List<String> keywordList = new ArrayList<String>();
		String search = searchStr;
		
		List<String> quotedTokens = new ArrayList<String>();
		while (search.contains("\"") && search.indexOf("\"") != search.lastIndexOf("\"")) {
			int index = search.indexOf("\"");
			String quoted = new String(search.substring(index+1,search.indexOf("\"", index+1)));
			search = search.replace("\""+quoted+"\"", "");
			quotedTokens.add(quoted);
		}
		StringTokenizer st = new StringTokenizer(search, " ;,.)/-:(", false);
	    while (st.hasMoreTokens()) {
	    	
	    	String token = st.nextToken().trim().toLowerCase();
	    	if (tag) {
	    		Stemmer stemmer = new Stemmer();
	        	token = stemmer.stemString(token).trim().toUpperCase();
	    	}
	    	keywordList.add(token.toUpperCase());
	    	
	    }
	    for (int i=0; i<quotedTokens.size(); i++) {
	    	String token = quotedTokens.get(i).trim();
	    	if (tag) {
	    		String result = "";
	    		st = new StringTokenizer(token, " ;,.)/-:(", false);
	    		while (st.hasMoreTokens()) {
	    			String s = st.nextToken().trim().toLowerCase();
	    			Stemmer stemmer = new Stemmer();
		        	s = stemmer.stemString(s);
		        	result=result+" "+s;
	    		}
	    		token = result;
	    		
	    	}
	    	keywordList.add(token.trim().toUpperCase());
	    }
	    return keywordList;
	    
	    
	}
	
}
