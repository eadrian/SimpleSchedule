package web;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;


public class TransientData {
	public static final int DEFAULT_INTERESTED_WORDS = 10;
	public List<Course> courses;
	public List<String> barredCourses;
	public Map<String, Integer> keywords;
	public Map<String, Integer> sortedWords;
	public Map<String, Integer> deptsTaken;
	public Map<String, Float> expecDept;
	public List<String> GERSneeded;
	public Map<String, Float> courseExpec;
	public Map<String, List<String>> GERSmet;
	public static float THRESHOLD = (float) .5;
	public int totalUnits=0;
	public boolean oneUnit = false;
	public MajorReqs reqs;
	
	public int GERSTaken = 0;
	public int REQSTaken = 0;
	public int branches = 0;
	
	public userData u;
	public Schedule s;
	public TransientData(userData d, Schedule sched, List<Course> taking, List<String> barred, MajorReqs reqs) {
		this.reqs = reqs;
		keywords = new HashMap<String, Integer>(d.keywords);
		resortKeywords();
		System.out.println(keywords);
		System.out.println("Sorted: "+sortedWords);
		courses = new ArrayList<Course>();
		barredCourses = new ArrayList<String>();
		deptsTaken = new HashMap<String, Integer>();
		expecDept = new HashMap<String, Float>(d.expectedCourses);
		GERSneeded = new ArrayList<String>(d.gers);
		GERSmet = new HashMap<String, List<String>>();
		System.out.println("Need:"+GERSneeded+":");
		s = new Schedule(sched);
		calcExpectedCourses();
		for (int i=0; i<taking.size(); i++) {
			Course c = taking.get(i);
			addCourse(c);
		}
		barredCourses = new ArrayList<String>(barred);
		
		
	}
	
	private void calcExpectedCourses() {
		courseExpec = new HashMap<String, Float>();
		float otherCount = 0;
		for (String s : expecDept.keySet()) {
			float e = expecDept.get(s);
			if (e>THRESHOLD) {
				courseExpec.put(s, e);
			} else {
				otherCount = otherCount + e;
			}
		}
		if (otherCount < THRESHOLD)
			otherCount = THRESHOLD;
		courseExpec.put("OTHER", otherCount);
		
		for (int i=0; i<courses.size(); i++) {
			Course c = courses.get(i);
			if (courseExpec.containsKey(c.deptCode)) {
				courseExpec.put(c.deptCode, courseExpec.get(c.deptCode)-1);
			} else {
				courseExpec.put("OTHER", courseExpec.get("OTHER")-1);
			}
		}
		for (String s : courseExpec.keySet()) {
			System.out.println("Dept: "+s+ " expected remaining: "+courseExpec.get(s));
		}
	}

	public TransientData(TransientData tdata) {
		reqs = tdata.reqs;
		keywords = new HashMap<String, Integer>(tdata.keywords);
		resortKeywords();
		courses = new ArrayList<Course>(tdata.courses);
		barredCourses = new ArrayList<String>(tdata.barredCourses);
		deptsTaken = new HashMap<String, Integer>(tdata.deptsTaken);
		expecDept = new HashMap<String, Float>(tdata.expecDept);
		GERSneeded = new ArrayList<String>(tdata.GERSneeded);
		courseExpec = new HashMap<String, Float>(tdata.courseExpec);
		u = tdata.u;
		s = new Schedule(tdata.s);
		totalUnits = tdata.totalUnits;
		oneUnit = tdata.oneUnit;
		GERSmet = new HashMap<String, List<String>>(tdata.GERSmet);
		sortedWords = new TreeMap<String, Integer>(tdata.sortedWords);
	}
	
	public void addCourse(Course c) {
		courses.add(c);
		incrementDepts(c);
		removeKeywords(c);
		removeGERS(c);
		resortKeywords();
		s.addItem(c.code, c.lectureDays, c.timeBegin, c.timeEnd);
		totalUnits+=c.numUnits;
		if (c.numUnits==1)
			oneUnit = true;
		if (c.fillsGER) {
			GERSTaken++;
			System.out.println("Taking Req");
		}
		if (c.fillsREQ) {
			REQSTaken++;
		}
		reqs.addCourse(c.code);
	}
	
	public void removeCourse(Course c) {
		if (!courses.contains(c)) {
			System.err.println("REMOVING COURSE NOT IN LIST");
			return;
		}
		
		/*decrementDepts(c);
		addKeywords(c);
		addGERS(c);*/
		decrementDepts(c);
		addKeywords(c);
		addGERS(c);
		resortKeywords();
		s.removeItem(c.code);
		if (c.numUnits==1)
			oneUnit=false;
		if (c.fillsGER) {
			GERSTaken--;
		}
		if (c.fillsREQ) {
			REQSTaken--;
		}
		reqs.removeCourse(c.code);
	}

	

	private void decrementDepts(Course c) {
		c.deptCode = c.deptCode.trim();
		if (deptsTaken.containsKey(c.deptCode)) {
			deptsTaken.put(c.deptCode, deptsTaken.get(c.deptCode)+1);
		} 
		
		if (courseExpec.containsKey(c.deptCode)) {
			courseExpec.put(c.deptCode, courseExpec.get(c.deptCode)+1);
		} else {
			courseExpec.put("OTHER", courseExpec.get("OTHER")+1);
		}
	}

	private void addGERS(Course c) {
		if (GERSmet.containsKey(c.code)) {
			List<String> gers = GERSmet.get(c.code);
			for (int i=0; i < gers.size(); i++) {
				GERSneeded.add(gers.get(i));
			}
			GERSmet.remove(c.code);
		}
	}
	
	private void resortKeywords() {
		ValueComparator bvc =  new ValueComparator(keywords);
		sortedWords = new TreeMap<String, Integer>(bvc);
		sortedWords.putAll(keywords);
	}

	private void removeGERS(Course c) {
		String GERS = c.universityReqs;
		if (!GERS.equals(null)) {
			String[] gersTaken = GERS.split(" ");
			for (int j=0; j<gersTaken.length || j==0; j++) {
				boolean rem = GERSneeded.remove(gersTaken[j]);
				if (rem) {
					if (GERSmet.containsKey(c.code)) {
						
					} else {
						GERSmet.put(c.code, new ArrayList<String>());
					}
					List<String> gers = new ArrayList<String>(GERSmet.get(c.code));
					gers.add(gersTaken[j]);
					GERSmet.put(c.code, gers);
				}
			}
		}
		
	}

	private void removeKeywords(Course c) {
		StringTokenizer st = new StringTokenizer(c.allTags, " ;,.)/-:(", false);
	    while (st.hasMoreTokens()) {
	    	 String s = st.nextToken().trim();
	    	 if (keywords.containsKey(s)/* && !isCommon(s)*/) {
	    		 //keywords.remove(s);
	    		 int total = keywords.get(s);
	    		 total = total < 5 ? 0 : total - 5;
	    		 keywords.put(s, total);
	    	 } 
	    }
		
	}
	
	private void addKeywords(Course c) {
		// TODO Auto-generated method stub
		StringTokenizer st = new StringTokenizer(c.allTags, " ;,.)/-:(", false);
	    while (st.hasMoreTokens()) {
	    	 String s = st.nextToken().trim();
	    	 if (keywords.containsKey(s)/* && !isCommon(s)*/) {
	    		 //keywords.remove(s);
	    		 int total = keywords.get(s);
	    		 total = total +5;
	    		 keywords.put(s, total);
	    	 } 
	    }
	}

	private void incrementDepts(Course c) {
		c.deptCode = c.deptCode.trim();
		if (deptsTaken.containsKey(c.deptCode)) {
			deptsTaken.put(c.deptCode, deptsTaken.get(c.deptCode)+1);
		} else {
			deptsTaken.put(c.deptCode.trim(), 1);
		}
		
		if (courseExpec.containsKey(c.deptCode)) {
			courseExpec.put(c.deptCode, courseExpec.get(c.deptCode)-1);
		} else {
			courseExpec.put("OTHER", courseExpec.get("OTHER")-1);
		}
		
	}
}
