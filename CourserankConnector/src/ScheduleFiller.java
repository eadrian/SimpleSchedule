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
	private static int TOP_COURSES = 100;
	private static int RAND_NUM = 3;
	private static int TOP_DEPTS = 3;
	private static int RAND_DEPTS = 3;
	private static int RAND_KEYWORDS = 3;
	private static int KEYWORD_DEPTH = 1;
	private static DBConnection dbc;
	public static Object dbLock;
	public ScheduleFiller() {
		dbc = new DBConnection();
		dbLock = new Object();
	}
	
	
	
	
	
	
	
	public Course getBestCourse(userData udata, TransientData tdata, searchFactors facts, String quarter, int year) {
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
		

		
		
		float expecGERS = tdata.GERSneeded.size() / quartersLeft;
		
		
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
		
		allResults = processResults(allResults, udata, tdata);
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
		return null;
		
	}
	
	private List<Course> processResults(List<Course> allResults,
			userData udata, TransientData tdata) {
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
			
			//Check if it fulfills GERS that are not yet fulfilled
			String GERS = c.universityReqs;
			if (!GERS.equals(null)) {
				String[] gersTaken = GERS.split(" ");
				for (int j=0; j<gersTaken.length || j==0; j++) {
					if (tdata.GERSneeded.contains(gersTaken)) {
						//Change score in relation to expected number of GERs needed to fulfill per quarter
					}
				}
			}
			
			//Check if it fulfills unfulfilled major reqs
			
			
			//Check for department / expected courseload balance
			
			
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
