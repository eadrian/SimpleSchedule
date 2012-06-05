import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;


public class ScheduleFiller {
	private static int YEARS = 4;
	private static int QUARTERS = 3;
	private static int TOP_COURSES = 1000;
	private static int RAND_NUM = 3;
	private static int TOP_DEPTS = 3;
	private static int RAND_DEPTS = 3;
	private static int RAND_KEYWORDS = 3;
	private static int KEYWORD_DEPTH = 1;
	private static DBConnection dbc;
	public static Object dbLock;
	private static float MAX_SCORE = 100;
	private static int TARGET_UNITS = 15;
	private static int MAX_DEVIATION = 5;
	private static int MAX_BRANCH = 3;
	private static float MAX_QUARTERS = 12;
	private static int SCHED_LIM = 10;
	private static int TOP_CHOICES = 5;
	private static int DIVERSITY_BONUS = 50;
	private static int MAX_ATTEMPTS = 1000;
	private static int MAX_SIMILARITY = 2;
	private static int MAX_SCHEDULES = 200;
	private static int BRANCH_MAX = MAX_SCHEDULES / MAX_BRANCH;
	private int scheduleCount = 0;
	private int branchCount = 0;
	private static int RARE_REQ = 3;
	
	private List<String> coursesTaken = new ArrayList<String>();

	public ScheduleFiller() {
		dbc = new DBConnection();
		dbLock = new Object();
	}
	
	public List<List<Course>> getRandSchedules(userData udata, TransientData tdata, searchFactors facts, String quarter, int year, int num) {
		List<List<Course>> schedules = getSchedule(udata, tdata, facts, quarter, year);
		Random r = new Random();
		int count = 0;
		int attempts = 0;
		List<List<Course>> results = new ArrayList<List<Course>>();
		while (count < num && attempts < MAX_ATTEMPTS) {
			int rand = r.nextInt(schedules.size()-1);
			List<Course> sched = schedules.get(rand);
			if (!containsSimilar(sched, results)) {
				results.add(sched);
				count++;
			} else {
				schedules.remove(rand);
				if (schedules.size()==0)
					break;
			}
		}
		return results;
	}
	
	

	private boolean containsSimilar(List<Course> sched, List<List<Course>> schedules) {
		for (int i=0; i<schedules.size(); i++) {
			int total = 0;
			for (int j=0; j<sched.size(); j++) {
				if (schedules.get(i).contains(sched.get(j)))
					total++;
			}
			if (total > MAX_SIMILARITY)
				return true;
			
		}
		return false;
	}
	
	
	public List<List<Course>> getSchedule(userData udata, TransientData tdata, searchFactors facts, String quarter, int year) {
		List<List<Course>> scheds = new ArrayList<List<Course>>();
		
		if (tdata.courses.size()>4 || tdata.totalUnits >= TARGET_UNITS) {
			scheds.add(tdata.courses);
			return scheds;
		}
		
		
		List<Course> results = getBestCourse(udata, tdata, facts, quarter, year);
		List<Course> top_results = new ArrayList<Course>();
		for (int i=1; i<TOP_CHOICES; i++) {
			top_results.add(results.get(i));
		}
		Course c = results.get(0);
		results = getConflictingCourses(c, results, MAX_BRANCH);
		results.addAll(top_results);
		if (results.size()==0)
			System.out.println(c.code+" days: "+c.lectureDays+" times: "+c.timeBegin+" : "+c.timeEnd);
		Random r = new Random();
		int branches = 0;
		for (int i=0; i < results.size(); i++) {
			
			int rand = r.nextInt(10);
			//System.out.println("RAND: "+rand);
			if (i==0 || (rand < 3 && branches < MAX_BRANCH && scheduleCount <MAX_SCHEDULES )) {
				if (i>0) {
					branches++;
					scheduleCount++;
					branchCount++;
				}
				Course chosen = results.get(i);
				tdata.barredCourses.add(chosen.code);
				TransientData t = new TransientData(tdata);
				
				
				t.addCourse(chosen);
				
				coursesTaken.add(chosen.code);
				List<List<Course>> schedules = getSchedule(udata, t, facts, quarter, year);
				//scheds.addAll(schedules);
				for (int j=0; j<schedules.size(); j++) {
					scheds.add(schedules.get(j));
				}
				t.reqs.removeCourse(chosen.code);
			}
		}
		
		return scheds;
		/*
		for (int i=0; i<5 && tdata.totalUnits < TARGET_UNITS; i++) {
			List<Course> results = getBestCourse(udata, tdata, facts, quarter, year);
			Course c = results.get(0);
			tdata.addCourse(c);
			chosen.add(c);
			results = getConflictingCourses(c, results, 3);
			for (int j=0; j<results.size(); j++)
				System.out.println("Conflicting: "+results.get(j).code);
		}
		for (int i=0; i<chosen.size(); i++) {
			System.out.println(chosen.get(i).code+" : "+chosen.get(i).title);
		}*/
	}
	
	public List<Course> getConflictingCourses(Course c, List<Course> courses, int lim) {
		List<Course> results = new ArrayList<Course>();
		int counter=0;
		for (int i=0; i<courses.size() && counter < lim; i++) {
			Course cor = courses.get(i);
			if (conflicts(cor, c)) {
				results.add(cor);
				counter++;
			}
				
		}
		return results;
	}
	
	
	private boolean conflicts(Course cor, Course c) {
		String d1 = cor.lectureDays;
		int start1 = cor.timeBegin;
		int end1 = cor.timeEnd;
		String d2 = c.lectureDays;
		int start2 = c.timeBegin;
		int end2 = c.timeEnd;
		if (d1.length()<d2.length())
			return true;
		for (int i=0; i<d1.length(); i++) {
			if (d1.charAt(i)=='1'&& d2.charAt(i)=='1') {
				if ((start1<=start2 && end1>=start2) || (start1<=end2 && end1>=end2) || (start1>=start2 && end1<=end2))
					return true;
			}
		}
		return false;
	}




	public List<Course> getBestCourse(userData udata, TransientData tdata, searchFactors facts, String quarter, int year) {
		String major = udata.major.trim();
		
		
		int quarters = 0;
		if (quarter.equals("Autumn")) {
			quarters = 0;
		} else if (quarter.equals("Winter")) {
			quarters = 0;
		} else if (quarter.equals("Spring")) {
			quarters = 1;
		} else if (quarter.equals("Summer")) {
			quarters = 1;
		}
		int quartersLeft = QUARTERS*(4-(year-udata.earliestYear))+quarters;
		
		System.out.println("Qs Left: "+quartersLeft);
		

		
		
		float expecGERS = tdata.GERSneeded.size() / quartersLeft - tdata.GERSTaken;
		
		float expecMajor = tdata.reqs.sortedReqs.size() / quartersLeft - tdata.REQSTaken;
		
		
		System.out.println("GERS Left: "+tdata.GERSneeded.size()+" Per Quarter: "+expecGERS);
		
		//Do a similar thing for major reqs as for GERS.
		
		List<AttrVal> match = new ArrayList<AttrVal>();
		
		AttrVal q = new AttrVal();
		q.type = "String";
		q.attr = "quarter";
		q.val = quarter;
    	match.add(q);
    	
    	
		List<Course> topCourses = dbc.getCoursesThatMatchSortedLim(match, true, "tScore", TOP_COURSES);
		
		int counter = 0;
		List<String> deptsToSearch = new ArrayList<String>();
		int randcount = 0;


		for (String s : udata.sortedDepts.keySet()) {
			if (counter < TOP_DEPTS) {
				System.out.println(s);
				deptsToSearch.add(s);
				counter++;
			} else if (randcount < RAND_DEPTS){
				Random r = new Random();
				int rand = r.nextInt(udata.sortedDepts.size()-TOP_DEPTS);
				System.out.println("Random number: "+rand);
				if (rand<RAND_DEPTS) {
					randcount++;
					System.out.println(s);
					deptsToSearch.add(s);
				}
			}
			
			
		}
		
		
		for (int i=0; i<deptsToSearch.size(); i++) {
			String d = deptsToSearch.get(i);
			System.out.println("To Search: "+deptsToSearch.get(i));
			AttrVal att = new AttrVal();
			att.type = "String";
			att.attr = "deptCode";
			
			att.or = true;
			att.val = d.trim();
			match.add(att);
		}
		
		List<Course> deptResults = dbc.getCoursesThatMatch(match);
		
		/*if (true)
			return null;*/
		
		List<Course> allResults = union(deptResults, topCourses);
		deptResults=null;
		topCourses=null;
		
		
		keywordSearch k = new keywordSearch(null);

		allResults = k.sortResults(udata, allResults, tdata.s, facts, "");
		
		allResults = processResults(allResults, udata, tdata, quartersLeft, expecGERS, expecMajor);
		for (int i=0; i<allResults.size() && i<25; i++) {
			System.out.println(""+i+". "+allResults.get(i).code+ " Quarter: "+allResults.get(i).quarter);
		}
		
		
		/*
		for (int i=0; i<RAND_NUM; i++) {
			int index = randIndex(topCourses);
			Course c = topCourses.get(index);
			String randTag = chooseRandomTag(c.tags);
			if (randTag.equals("")) {
				System.out.println("None found");
				System.out.println(c.code+" : "+c.tags+" : "+c.description);
			}
			System.out.println(c.code+ " : index: "+index+" Tag: "+randTag);
		}
		*/
		return allResults;
		
	}
	
	private List<Course> processResults(List<Course> allResults,
			userData udata, TransientData tdata, int quartersLeft, float expecGERS, float expecMajor) {
		
		System.out.println("Expected GERS: "+expecGERS+" Exepcted Majors: " + expecMajor);
		
		float urgency = 1;
		if (quartersLeft <=3)
			urgency = 5-quartersLeft;
		for (int i=0; i<allResults.size(); i++) {
			Course c = allResults.get(i);
			if (c.description.toUpperCase().contains("COREQUISITE")) {
				c.totalScore = 0;    //IMPROVE THIS, CHECK IF THEY ARE TAKING THE COREQUISITE
			}
			//Check if it is in barred courses
			if (tdata.barredCourses.contains(c.code))
				c.totalScore = 0;
			
			//Check if it is in teh list of courses already taken
			if (tdata.courses.contains(c))
				c.totalScore = 0;
			
			//Check for impossible time
			if (c.timeBegin == c.timeEnd)
				c.totalScore = 0;
			
			//Check if it fulfills GERS that are not yet fulfilled
			String GERS = c.universityReqs;
			if (!GERS.equals(null)) {
				String[] gersTaken = GERS.split(" ");
				for (int j=0; j<gersTaken.length || j==0; j++) {
					if (tdata.GERSneeded.contains(gersTaken[j])) {
						//Change score in relation to expected number of GERs needed to fulfill per quarter
						c.totalScore += (urgency*(MAX_SCORE*expecGERS))/2;
						System.err.println("FOUND ONE");
						c.fillsGER = true;
					}
				
				}
			}
			
			//Check if it fulfills unfulfilled major reqs
			String req = tdata.reqs.getReq(c.code);
			if (!req.equals("")) {
				//Fulfills a new req
				c.fillsREQ = true;
				if (tdata.reqs.reqsFulfilling.get(req).size() <= RARE_REQ) {
					c.totalScore+=urgency*(MAX_SCORE*expecMajor);
				} else {
					c.totalScore+=urgency*(MAX_SCORE*expecMajor)/2;
				}
			}
			
			//Check for department / expected courseload balance
			float expectation = 0;
			if (tdata.courseExpec.containsKey(c.deptCode)) {
				expectation = tdata.courseExpec.get(c.deptCode);
			} else {
				expectation = tdata.courseExpec.get("OTHER");
				if (tdata.expecDept.containsKey(c.deptCode))
					expectation+=tdata.expecDept.get(c.deptCode);
				if (tdata.deptsTaken.containsKey(c.deptCode))
					expectation = 0;
			}
			c.totalScore+=MAX_SCORE*expectation;
			
			//Prereqs
			c.totalScore+=((float)quartersLeft)/MAX_QUARTERS * c.preScore;
			
			
			//Unit balance
			float unitTotal = tdata.totalUnits+c.numUnits;
			if (unitTotal > TARGET_UNITS) {
				c.totalScore+=MAX_SCORE-(MAX_SCORE * (unitTotal - TARGET_UNITS)/MAX_DEVIATION);
			} else {
				c.totalScore+=MAX_SCORE;
			}
			
			if (c.numUnits == 1 && tdata.oneUnit)
				c.totalScore-=MAX_SCORE;
			
			//Bias against courses in other schedules
			if (!coursesTaken.contains(c.code))
				c.totalScore+=DIVERSITY_BONUS;
			
			
			
			
			if (c.deptCode.equals("ATHLETIC")) {
				//Fix Sports
				String nTitle = c.title.toUpperCase().trim();
				
				if (nTitle.contains("WOMEN"))
					c.totalScore=0;
				else if (nTitle.contains("MEN"))
					c.totalScore=0;
				
				if (nTitle.contains("ADVANCED")) {
					String preTitle = nTitle.replace("ADVANCED", "INTERMEDIATE");
					if (!udata.titlesTaken.contains(preTitle)) {
						c.totalScore = 0;
						System.out.println("Did not contain ATH prereq");
					} else {
						c.totalScore+=MAX_SCORE;
					}
				} else if (nTitle.contains("INTERMEDIATE")) {
					String preTitle = nTitle.replace("INTERMEDIATE", "BEGINNER");
					String altTitle = nTitle.replace("INTERMEDIATE", "BEGINNING");
					if (!udata.titlesTaken.contains(preTitle) && !udata.titlesTaken.contains(altTitle)) {
						c.totalScore = 0;
						System.out.println("Did not contain ATH prereq");
					} else {
						c.totalScore+=MAX_SCORE;
					}
				} else if (nTitle.contains("BEGINNER") || nTitle.contains("BEGINNING"))
					c.totalScore+=MAX_SCORE/2;
			}
			if (c.deptCode.contains("LANG")) {
				//Check for language level
				
				
				String nTitle = c.title.toUpperCase().trim();
				
				if (nTitle.contains("SECOND-YEAR")) {
					String preTitle = nTitle.replace("SECOND", "FIRST");
					if (!udata.titlesTaken.contains(preTitle)) {
						c.totalScore = 0;
						System.out.println("Did not contain LANG prereq");
					} else {
						c.totalScore+=MAX_SCORE;
					}
				} else if (nTitle.contains("THIRD-YEAR")) {
					String preTitle = nTitle.replace("THIRD", "SECOND");
					if (!udata.titlesTaken.contains(preTitle)) {
						c.totalScore = 0;
						System.out.println("Did not contain LANG prereq");
					} else {
						c.totalScore+=MAX_SCORE;
					}
				} else if (nTitle.contains("BEGINNER") || nTitle.contains("BEGINNING"))
					c.totalScore+=MAX_SCORE/2;
			}
			
			//Random shake-it-up factor
			Random r = new Random();
			c.totalScore+=r.nextInt((int) MAX_SCORE);
		}
		
		//Sort Score
		Collections.sort(allResults, CourseSorter.COMPARATOR);
		return allResults;
	}







	public <T> List<T> union(List<T> list1, List<T> list2) {
        Set<T> set = new HashSet<T>();

        List<T> results = new ArrayList<T>(list1);
        for (int i=0; i<list2.size(); i++) {
        	if (!results.contains(list2.get(i)))
        		results.add(list2.get(i));
        }
        return results;
    }
	
	public String chooseRandomTag(String tags) {
		StringTokenizer st = new StringTokenizer(tags, " ;,.)/-:(", false);
		int total = st.countTokens();
		if (total==0 || total==1)
			return "";
		Random r = new Random();
		int randIndex = r.nextInt(total-1)+1;
		String tag = "";
		for (int i=0; i<randIndex; i++) {
			tag = st.nextToken().trim();
		}
		System.out.println("Random tag: "+tag);
		return tag;
	}
	
	public <T> int randIndex(List<T> list) {
		Random r = new Random();
		return r.nextInt((list.size()-1));
	}

}
