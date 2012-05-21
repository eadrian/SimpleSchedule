import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
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
	public static float DEFAULT_PROFESSOR_RATING = (float) 2.5;
	public static float STAR_FACTOR = (float) 1.5;
	public static int RATINGS_MINIMUM = 10;
	public static int MIN_WLOAD = 100;
	public static int MAX_WLOAD = 600;
	public static float MAX_SCORE=100;
	public static float REVIEWS_FACTOR = (float) .7;
	public static float FREQ_FACTOR = (float) .3;
	public static float PREREQ_FACTOR = (float) .4;
	
	public Map<String, Integer> GERNUM;
	
	public dataProcessor() {
		dbc = new DBConnection();
		dbLock = new Object();
		//calculateAverageRatings();
		//generateCourseData();
		//generatePrelimScores();
		//makeSumTags();
		addPrereqLists();
	}
	
	private void makeSumTags() {
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_courses;" );
			int i=0;
			while(rs.next()){
				i++;
				if (i%100 == 0)
					System.out.println("progress");
				String tags = rs.getString("tags");
				String dtags = rs.getString("deptTags");
				String ttags = rs.getString("titleTags");
				String allTags = tags+dtags+ttags;
				
				List<AttrVal> match = new ArrayList<AttrVal>();
				AttrVal id = new AttrVal();
				id.type = "int";
				id.attr = "ID";
				id.val = ""+rs.getString("ID");
				match.add(id);
				AttrVal q = new AttrVal();
				q.type = "String";
				q.attr = "quarter";
				q.val = ""+rs.getString("quarter");
				match.add(q);
				
				List<AttrVal> update = new ArrayList<AttrVal>();
				AttrVal a1 = new AttrVal();
				a1.type = "String";
				a1.attr = "allTags";
				a1.val = allTags;
				
				update.add(a1);
				dbc.updateAttributesWhere("rhun_courses", match, update);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to get course codes.");			
		}
		
	}
	
	private void addPrereqLists() {
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_course_prereqs;" );
			int i=0;
			while(rs.next()){
				String preStr = rs.getString("prereqCode");
				String code = rs.getString("code");
				if (preStr!=null && !preStr.equals("")) {
					dbc.updateAttributeWhere("rhun_courses", "code", "'"+code.trim()+"'", "prereqs", "'"+preStr+"'");
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to get course codes.");			
		}
		
	}

	private void generatePrelimScores() {
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_courses;" );
			
			while(rs.next()){
				String name = rs.getString("code").trim();
				Float nScore = rs.getFloat("nScore");
				Float wScore = rs.getFloat("wScore");
				Float qScore = rs.getFloat("qScore");
				Float prereqs = rs.getFloat("preScore");
				int cnum = rs.getInt("cnum");
				Float tScore = calculateTScore(nScore, wScore, qScore, prereqs, cnum, false);
				Float majorTScore = calculateTScore(nScore, wScore, qScore, prereqs, cnum, true);
				List<AttrVal> match = new ArrayList<AttrVal>();
				AttrVal id = new AttrVal();
				id.type = "int";
				id.attr = "ID";
				id.val = ""+rs.getString("ID");
				match.add(id);
				AttrVal q = new AttrVal();
				q.type = "String";
				q.attr = "quarter";
				q.val = ""+rs.getString("quarter");
				match.add(q);
				
				List<AttrVal> update = new ArrayList<AttrVal>();
				AttrVal a1 = new AttrVal();
				a1.type = "float";
				a1.attr = "tScore";
				a1.val = ""+tScore;
				AttrVal a2 = new AttrVal();
				a2.type = "float";
				a2.attr = "MajorTScore";
				a2.val = ""+majorTScore;
				
				update.add(a1);
				update.add(a2);
				dbc.updateAttributesWhere("rhun_courses", match, update);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to get course codes.");			
		}
		
	}

	private Float calculateTScore(Float nScore, Float wScore, Float qScore,
			Float prereqs, int cnum, boolean inMajor) {
		int cScore = 0;
		if (cnum<50) {
			cScore = 70;
		} else if (cnum<100) {
			cScore = 80;
		} else if (cnum<180) {
			cScore = 90;
		} else if (cnum<280) {
			cScore = 50;
		} else {
			cScore = 40;
		}
		Float score = cScore+wScore+qScore+nScore;
		if (inMajor) {
			score = (float) (score*(float).5+prereqs*(float).5);
		} 
		return score;
	}

	public void generateCourseData() {
		ValueSpreader v = new ValueSpreader();
		
		List<String> depts = getDepartments();
		List<String> cnames = new ArrayList<String>();
		Map<String, List<String>> reqMap = new HashMap<String, List<String>>(500);
		Map<String, Integer> freq = new HashMap<String, Integer>(3000);
		
		
		Map<String, Integer> reqCount = new TreeMap<String, Integer>();
		ValueComparator bvc =  new ValueComparator(reqCount);
		Map<String, Integer> sortedCount = new TreeMap<String, Integer>(bvc);
		
		
		Map<String, Integer> wMap = new TreeMap<String, Integer>();
		ValueComparator comp =  new ValueComparator(wMap);
		Map<String, Integer> sortedWork = new TreeMap<String, Integer>(comp);
		
		//GER data
		Map<String, String> GERS = getGERS();
		
		//Map<String, Float> 
		Map<String, Integer> revMap = new HashMap<String, Integer>();
		
		Map<String, Float> nScores = new HashMap<String, Float>();
		Map<String, Integer> wScores = new HashMap<String, Integer>();
		//Department data
		Map<String, String> deptNames = new HashMap<String, String>();
		
		
		//Initialize Tagger
		
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
		
		
		//Get course codes
		
		float avgReviews = 0;
		int totalCourses = 0;
		int maxReviews = 0;
		String maxCode = "";
		
		
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_departments;" );
			while(rs.next()){
				String name = rs.getString("code").trim();
				String title = rs.getString("title").trim();
				deptNames.put(name, getTags(tagger, title));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to get dept codes.");			
		}
		
		
		List<Integer> numRevs = new ArrayList<Integer>();
		
		Date start = new Date();
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_courses;" );
			Date latency = new Date();
			System.err.println("Time to get course result: "+(float)((latency.getTime()-start.getTime())/1000f));
			int i=0;
			while(rs.next() && i<1){
				String name = rs.getString("code").trim();
				cnames.add(name);
				if (freq.containsKey(name)) {
					freq.put(name, freq.get(name)+1);
				} else {
					freq.put(name, 1);
				}
				int revs = rs.getInt("numReviews");
				avgReviews = (avgReviews*totalCourses+revs)/(totalCourses+1);
				totalCourses++;
				if (revs > maxReviews) {
					maxReviews = revs;
					maxCode = name;
				}
				numRevs.add(revs);
				if (!revMap.containsKey(name))
					revMap.put(name, revs);
				
			}
			Date finish = new Date();
			System.err.println("Time to iterate through courses: "+(float)((finish.getTime()-start.getTime())/1000f));
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to get course codes.");			
		}
		
		System.out.println("Avg Reviews: "+avgReviews+ " MaxReviews: "+maxReviews+" for "+maxCode);
		Collections.sort(numRevs);
		System.out.println("Median Reviews: "+numRevs.get(numRevs.size()/2));
		
		
		//Test Value Spreader on Reviews:
		Map<String, Float> spreadRev = v.spreadValuesI(revMap,(float).75, (float).3);
		
		
		
		
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_courses;" );
			int i=0;
			while(rs.next()){
				Course c = new Course(rs.getInt("ID"), rs.getString("avgGrade"), rs.getString("code").trim(), 
											  rs.getString("description"), rs.getInt("deptID"), rs.getString("title"),
											  rs.getString("grading"), rs.getString("lectureDays"), rs.getInt("numReviews"), 
											  rs.getInt("numUnits"), rs.getDouble("rating"), rs.getInt("timeBegin"), 
											  rs.getInt("timeEnd"), rs.getString("tags"), rs.getString("type"), rs.getInt("workload"));
				c.quarter = rs.getString("quarter");
				if (!cnames.contains(c.code)) {
					cnames.add(c.code);
				}
				//System.out.println(tagger.tagString(c.description));
				//System.out.println(tagger.tagString(c.title));
				String tags = getTags(tagger, c.description);
				String titleTags = getTags(tagger, c.title);
				if (titleTags.length()>40)
					System.err.println("Long title: "+c.title);
				String deptCode = new String(c.code.substring(0, c.code.indexOf(" ")));
				String deptTags = deptNames.get(deptCode);
				//System.out.println(c.code);
				//System.out.println("Dept tags: "+deptTags);
				//System.out.println("title tags: "+titleTags);
				//System.out.println("desc tags: "+tags);
			     //System.out.println(tags);
			     if (tags.equals(""))
			    	 System.out.println(c.code+" no tags");
			     //dbc.updateAttributeWhere("rhun_courses", "code", "'"+c.code+"'", "tags", "'"+tags+"'");
			     
			     //Get course dept. code #
			     String codeNum = "";
			     for (int j=0; j<c.code.length(); j++) {
			    	 if (Character.isDigit(c.code.charAt(j))) {
			    		 codeNum = codeNum+c.code.charAt(j);
			    	 }
			     }
			     //dbc.updateAttributeWhere("rhun_courses", "code", "'"+c.code+"'", "cnum", ""+codeNum);
			     //System.out.println(""+c.code+" : "+codeNum);
			     
			     
			     //Workload
			     if (c.workload == -1) {
			    	 c.workload = (int) (c.numUnits * 2.75);
			     }
			     //dbc.updateAttributeWhere("rhun_courses", "code", "'"+c.code+"'", "workload", ""+c.workload);
			     //Unit correction
			     if (c.numUnits==0) {
			    	 c.numUnits=5;
			    	 //dbc.updateAttributeWhere("rhun_courses", "code", "'"+c.code+"'", "numUnits", ""+c.numUnits);
			     }
			     //WORKLOAD FORMULA
			     int wScore = calculateWScore(c);
			     if (c.numUnits<3) {
			    	 if (wScore>(MIN_WLOAD*c.numUnits)) {
			    		 wScore = MIN_WLOAD*c.numUnits;
			    	 }
			     }
			     if (wScore<MIN_WLOAD)
			    	 wScore=MIN_WLOAD;
			     if (c.code.equals("CS 140"))
			    	 System.out.println("CS 140: "+wScore);
			     
			     if (wScore>MAX_WLOAD) {
			    	 wScore = MAX_WLOAD;
			    	 System.err.println("OVER MAX : "+c.code);
			     }
			     wScore = MAX_WLOAD-wScore;
			     if (!wMap.containsKey(c.code)) {
			    	 wMap.put(c.code, wScore);
			     }
			     
			     
			     //dbc.updateAttributeWhere("rhun_courses", "code", "'"+c.code+"'", "wScore", ""+wScore);
			     //Get Professor Score
			     float professorRating = getProfessorScore(c);
			     //System.out.println("Professor Rating Avg: "+professorRating);
			     //Calculate course quality rating (qScore)
			     float qScore = calculateQScore(c, professorRating);
			     if (qScore>90) {
			    	 System.out.println("High Quality Score: "+c.code+" Score: "+qScore);
			     } if (qScore<60) {
			    	 System.out.println("Low Quality Score: "+c.code+" Score: "+qScore);
			     }
			     
			   //Get Frequency
			     float frequency = ((float)(4-freq.get(c.code))/3f)*100;
			     //dbc.updateAttributeWhere("rhun_courses", "code", "'"+c.code+"'", "freq", ""+frequency);
			     //System.out.println("Frequency = "+frequency);
			     /*if (frequency==1)
			    	 System.out.println("All quarters: "+c.code);*/
			     
			   //Calculate Necessity Score
			     float nScore = calculateNScore(((float)freq.get(c.code)/3f)*100, spreadRev.get(c.code));
			     if (!nScores.containsKey(c.code))
			    	 nScores.put(c.code, nScore);
			     
			     
			     List<AttrVal> match = new ArrayList<AttrVal>();
				AttrVal id = new AttrVal();
				id.type = "int";
				id.attr = "ID";
				id.val = ""+c.ID;
				match.add(id);
				AttrVal q = new AttrVal();
				q.type = "String";
				q.attr = "quarter";
				q.val = ""+c.quarter;
				match.add(q);
				
				List<AttrVal> update = new ArrayList<AttrVal>();
				AttrVal a1 = new AttrVal();
				a1.type = "float";
				a1.attr = "qScore";
				a1.val = ""+qScore;
				AttrVal a2 = new AttrVal();
				a2.type = "float";
				a2.attr = "profScore";
				a2.val = ""+professorRating;
				AttrVal a3 = new AttrVal();
				a3.type = "float";
				a3.attr = "wScore";
				a3.val = ""+wScore;
				AttrVal a4 = new AttrVal();
				a4.type = "int";
				a4.attr = "workload";
				a4.val = ""+c.workload;
				AttrVal a5 = new AttrVal();
				a5.type = "int";
				a5.attr = "cnum";
				a5.val = ""+codeNum;
				AttrVal a6 = new AttrVal();
				a6.type = "String";
				a6.attr = "tags";
				
				a6.val = ""+DBEscape(tags);
				AttrVal a7 = new AttrVal();
				a7.type = "float";
				a7.attr = "freq";
				a7.val = ""+frequency;
				
				AttrVal a8 = new AttrVal();
				a8.type = "String";
				a8.attr = "deptTags";
				a8.val = deptTags;
				
				AttrVal a9 = new AttrVal();
				a9.type = "String";
				a9.attr = "titleTags";
				a9.val = titleTags;
				
				AttrVal a10 = new AttrVal();
				a10.type = "String";
				a10.attr = "deptCode";
				a10.val = deptCode;
				
				AttrVal a11 = new AttrVal();
				a11.type = "float";
				a11.attr = "nScore";
				a11.val = ""+nScore;
				
				AttrVal a12 = new AttrVal();
				a12.type = "String";
				a12.attr = "GERS";
				a12.val = ""+GERS.get(c.code);
				
				
				Integer numGERS = GERNUM.get(c.code);
				if (numGERS==null)
					numGERS=0;
				if (numGERS>0) 
					System.out.println(""+c.code+" : "+GERS.get(c.code));
				AttrVal a13 = new AttrVal();
				a13.type = "int";
				a13.attr = "numGERS";
				a13.val = ""+(numGERS*50);
				/*
				update.add(a1);
				update.add(a2);
				//update.add(a3);
				update.add(a4);
				update.add(a5);
				update.add(a6);
				update.add(a7);
				update.add(a8);
				update.add(a9);
				update.add(a10);
				update.add(a11);*/
				
				update.add(a12);
				update.add(a13);
				dbc.updateAttributesWhere("rhun_courses", match, update);
			     
			     
			     
			     
			     
			     
				 String prereqs = generatePrereqs(cnames, depts, c.description, c.code);
				 /*dbc.update("INSERT INTO rhun_course_prereqs VALUES ('" + c.code + "', '" + 
							prereqs + "');");*/
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
				
				i++;
			}				
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to get course data.");			
		}
		
		//Debug printing CS workloads
		int num = 0;
		for (Map.Entry<String, Integer> e : sortedWork.entrySet()) {
            //System.out.println("key/value: " + e.getKey() + "/"+e.getValue());
            num++;
        }
		//System.out.println("Num classes: "+num);
		if (true)
			return;
		//////////////////////////
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
		
		Map<String, Float> spreadReqs = v.spreadValuesI(reqCount, (float).2, (float).3);
		
		Map<String, Float> spreadWScores = v.spreadValuesI(wMap, (float).2, (float).3);
		for (String key : spreadReqs.keySet()) {
			//dbc.updateAttributeWhere("rhun_courses", "code", "'"+key+"'", "nScore", ""+(spreadReqs.get(key)*PREREQ_FACTOR+(1-PREREQ_FACTOR)*nScores.get(key)));
			dbc.updateAttributeWhere("rhun_courses", "code", "'"+key+"'", "preScore", ""+spreadReqs.get(key));
		}
		for (String key : spreadWScores.keySet()) {
			dbc.updateAttributeWhere("rhun_courses", "code", "'"+key+"'", "wScore", ""+(spreadWScores.get(key)));
		}
		for (String key : sortedCount.keySet()) {
            System.out.println("key/value: " + key + "/"+sortedCount.get(key));
            dbc.updateAttributeWhere("rhun_courses", "code", "'"+key+"'", "NumPrereqs", ""+sortedCount.get(key));
        }
		System.out.println("First size: "+reqCount.size()+" Second size "+sortedCount.size());
		
	}
	
	
	private Map<String, String> getGERS() {
		Map<String, String> GERS = new HashMap<String, String>();
		Map<Integer, String> GERID = new HashMap<Integer, String>();
		GERNUM = new HashMap<String, Integer>();
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_reqs;" );
			
			while(rs.next()){
				String name = rs.getString("name").trim();
				int id = rs.getInt("ID");
				GERID.put(id, name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to get GER codes.");			
		}
		
		
		try {
			Statement stmt = dbc.con.createStatement();
			stmt.executeQuery("USE " + dbc.database);
			ResultSet rs = stmt.executeQuery("SELECT * FROM rhun_course_reqs;" );
			
			while(rs.next()){
				String name = rs.getString("code").trim();
				int id = rs.getInt("reqID");
				if (GERS.containsKey(name)) {
					GERS.put(name, GERS.get(name)+" "+GERID.get(id));
					GERNUM.put(name, GERNUM.get(name)+1);
				} else {
					GERS.put(name, GERID.get(id));
					GERNUM.put(name, 1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to get GER codes.");			
		}
		
		return GERS;
	}

	private float calculateNScore(float freq, Float revs) {
		return freq*FREQ_FACTOR+revs*REVIEWS_FACTOR;
	}

	private float calculateQScore(Course c, float professorRating) {
		int ratings = c.numReviews;
		int profWeight = ratings;
		if (ratings<RATINGS_MINIMUM) {
			profWeight = RATINGS_MINIMUM;
		}
		return (float) (20f*(c.rating*ratings+professorRating*profWeight)/((float)(ratings+profWeight)));
	}

	private float getProfessorScore(Course c) {
		List<AttrVal> match = new ArrayList<AttrVal>();
		AttrVal id = new AttrVal();
		id.type = "int";
		id.attr = "courseID";
		id.val = ""+c.ID;
		
		AttrVal q = new AttrVal();
		q.type = "String";
		q.attr = "quarter";
		q.val = c.quarter;
		
		match.add(id);
		match.add(q);
		
		List<AttrVal> request = new ArrayList<AttrVal>();
		AttrVal a = new AttrVal();
		a.type = "int";
		a.attr = "lecturerID";
		request.add(a);
		
		List<List<AttrVal>> results=dbc.getAttributesThatMatch("rhun_lecturer_course", request, match, false, "");
		float avgRating;
		float totalRating = 0;
		for (int i=0; i<results.size(); i++) {
			int pid = Integer.parseInt(results.get(i).get(0).val);
			
			match = new ArrayList<AttrVal>();
			id = new AttrVal();
			id.type = "int";
			id.attr = "ID";
			id.val = ""+pid;
			match.add(id);
			
			request = new ArrayList<AttrVal>();
			a = new AttrVal();
			a.type = "float";
			a.attr = "avgRating";
			request.add(a);
			
			AttrVal s = new AttrVal();
			s.type = "int";
			s.attr = "star";
			request.add(s);
			
			List<List<AttrVal>> ratings=dbc.getAttributesThatMatch("rhun_lecturers", request, match, false, "");
			
			for (int j=0; j<ratings.size(); j++) {
				float rating = Float.parseFloat(ratings.get(j).get(0).val);
				int star = Integer.parseInt(ratings.get(j).get(1).val);
				if (rating == 0)
					rating = DEFAULT_PROFESSOR_RATING;
				
				if (j==0 && star==1)
					totalRating+=STAR_FACTOR;
				totalRating+=rating;
			}
		}
		avgRating = totalRating / results.size();
		if (results.size()==0) {
			System.out.println("no ratings...");
			return 0;
		}
		return avgRating;
	}

	//Calculates a number for the workload per unit as estimated minutes per unit. Then weight multiply by average grade.
	private int calculateWScore(Course c) {
		
		//Calculate minutes per day
		int timeDay=((c.timeEnd/100)-(c.timeBegin/100))*60-c.timeBegin%100+c.timeEnd%100;
		if (c.timeBegin==c.timeEnd)
			timeDay = 100;
		
		//System.out.println("time begin: "+c.timeBegin+" : Time End: "+c.timeEnd+" Duration: "+timeDay);
		int weekMin = 0;
		for (int i=0; i<c.lectureDays.length(); i++) {
			if (c.lectureDays.charAt(i)=='1') {
				//System.out.println("Day");
				weekMin+=timeDay;
			}
		}
		//System.out.println("Min per week: "+weekMin);
		
		weekMin+=(c.workload*60);
		
		int units = c.numUnits;
		
		if (c.numUnits==0) {
			System.out.println(c.code+" zero units");
			units = 3;
		}
		
		int minUnit = weekMin / units;
		
		//((float)c.workload)/20f;
		
		//ARBITRARY GRADE FACTORS, TRY TWEAKING FOR BETTER WORKLOAD RESULTS
		double gradeFactor = 1;
		if (c.avgGrade.equals("A")) {
			gradeFactor = 0.5;
		} else if (c.avgGrade.equals("A-")) {
			gradeFactor = 1;
		} else if (c.avgGrade.equals("B+")) {
			gradeFactor = 1.5;
		} else if (c.avgGrade.equals("B")) {
			gradeFactor = 2.5;
		} else if (c.avgGrade.equals("B-")) {
			gradeFactor = 3;
		} else if (c.avgGrade.contains("C")) {
			gradeFactor = 3.5;
		}
		
		return (int) (minUnit*gradeFactor);
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
			
			List<List<AttrVal>> results=dbc.getAttributesThatMatch("rhun_lecturer_course", request, match, false, "");
			
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
				List<List<AttrVal>> courseData=dbc.getAttributesThatMatch("rhun_courses", request, match, false, "");
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
	
	/*
	public Map<String, Float> spreadValuesF(Map<String, Float> m) {
		
		FloatComparator comp =  new FloatComparator(m);
		Map<String, Float> tree = new TreeMap<String, Float>();
		for (Map.Entry<String, Float> e : m.entrySet()) {
            //System.out.println("key/value: " + e.getKey() + "/"+e.getValue());
            tree.put(e.getKey(), new Float(e.getValue()));
        }
		
		int size = m.size();
		int count = 0;
		Float prevValue = null;
		Float value = (float) 0.0;
		for (Map.Entry<String, Float> e : tree.entrySet()) {
            //System.out.println("key/value: " + e.getKey() + "/"+e.getValue());
			if (prevValue!=null && e.getValue().equals(prevValue)) {
				m.put(e.getKey(), value);
			} else {
				float remainder = MAX_SCORE-value;
				float inc = remainder / (size-count);
				value = value+inc;
				prevValue = value;
				m.put(e.getKey(), value);
			}	
            count++;
        }
		return m;
	}
	
public Map<String, Float> spreadValuesI(Map<String, Integer> m) {
		Map<String, Float> mprime = new HashMap<String, Float>();
		ValueComparator comp =  new ValueComparator(m, true);
		Map<String, Integer> tree = new TreeMap<String, Integer>(comp);
		
		int max = 0;
		for (Map.Entry<String, Integer> e : m.entrySet()) {
            //System.out.println("key/value: " + e.getKey() + "/"+e.getValue());
			if (e.getValue()>max)
				max = e.getValue();
            tree.put(e.getKey(), new Integer(e.getValue()));
        }
		
		int size = m.size();
		int count = 0;
		
		float smoothingFactor = (float) .3;
		float incFactor = 1 - smoothingFactor; 
		//1.8 70->60     2 60->50 2.3 50->40 2.8 40->30 4 30->20
		float movingInc = MAX_SCORE / size;
		float nextInc = movingInc;
		
		Integer prevValue = null;
		Float value = (float) 0;
		int repeatCount = 1;
		boolean other = false;
		//Float factor = (max)/((float)(size));
		for (Map.Entry<String, Integer> e : tree.entrySet()) {
            //System.out.println("key/value: " + e.getKey() + "/"+e.getValue());
			if (prevValue!=null && e.getValue().equals(prevValue)) {
				//repeatCount++;
				nextInc+=(movingInc/2);
						///repeatCount;
			} else {
				/*float remainder = MAX_SCORE-value;
				//Float inc = (remainder / (size-count));
				Float expinc = (remainder / (size-count));
				if (prevValue==null)
					prevValue = 0;
				
				Float factor = (max-prevValue)/((float)(size-count));
				
				Float inc = (e.getValue()-prevValue)*(incFactor*expinc/factor+smoothingFactor*expinc);
				//Float inc = (remainder / (max-e.getValue()))*(e.getValue()-prevValue);
				if (prevValue==null)
					prevValue = 0;
				Float factor = (max-prevValue)/((float)(size-count));
				Float inc = smoothingFactor*nextInc+incFactor*((e.getValue()-prevValue)*nextInc/factor);
				
				value = value+inc;
				prevValue = e.getValue();
				movingInc = (MAX_SCORE-value)/(size-count-1);
				nextInc = movingInc;
				repeatCount=1;
			}
			
			/*if (other) {
				System.out.println("Name: "+e.getKey()+" Old Value: "+e.getValue()+" Spread Value: "+value);
				other = false;
			} else
				other = true;
			mprime.put(e.getKey(), value);
			if (count==size/2)
				System.err.println("MEDIAN REACHED");
            count++;
        }
		return mprime;
	}
	*/
	
	

	
	public String DBEscape(String s) {
		if (s==null)
			return null;
		String n = s;
		n.replace("\\", "\\\\");
		n.replace("\"", "");
		n.replace("\'", "");
		while (n.contains("<") && n.contains(">")) {
			n = new String(n.substring(0, n.indexOf("<"))+n.substring(n.indexOf(">")+1));
		}
		return n;
	}
	
	public static void main(String[] args) {
		new dataProcessor();
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
