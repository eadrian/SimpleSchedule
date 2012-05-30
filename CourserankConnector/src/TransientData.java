import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class TransientData {
	public List<Course> courses;
	public List<String> barredCourses;
	public Map<String, Integer> keywords;
	public Map<String, Integer> deptsTaken;
	public Map<String, Float> expecDept;
	public List<String> GERSneeded;
	public userData u;
	public Schedule s;
	public TransientData(userData d, Schedule sched, List<Course> taking, List<String> barred) {
		
		keywords = new HashMap<String, Integer>(d.keywords);
		courses = new ArrayList<Course>();
		barredCourses = new ArrayList<String>();
		deptsTaken = new HashMap<String, Integer>();
		expecDept = new HashMap<String, Float>(d.expectedCourses);
		GERSneeded = new ArrayList<String>(d.gers);
		s = new Schedule(sched);
		for (int i=0; i<taking.size(); i++) {
			Course c = taking.get(i);
			addCourse(c);
		}
		barredCourses = new ArrayList<String>(barred);
		
		
	}
	
	public TransientData(TransientData tdata) {
		keywords = new HashMap<String, Integer>(tdata.keywords);
		courses = new ArrayList<Course>(tdata.courses);
		barredCourses = new ArrayList<String>(tdata.barredCourses);
		deptsTaken = new HashMap<String, Integer>(tdata.deptsTaken);
		expecDept = new HashMap<String, Float>(tdata.expecDept);
		GERSneeded = new ArrayList<String>(tdata.GERSneeded);
		u = tdata.u;
		s = new Schedule(tdata.s);
	}
	
	public void addCourse(Course c) {
		courses.add(c);
		incrementDepts(c);
		removeKeywords(c);
		removeGERS(c);
	}

	private void removeGERS(Course c) {
		String GERS = c.universityReqs;
		if (!GERS.equals(null)) {
			String[] gersTaken = GERS.split(" ");
			for (int j=0; j<gersTaken.length || j==0; j++) {
				GERSneeded.remove(gersTaken[j]);
			}
		}
		
	}

	private void removeKeywords(Course c) {
		StringTokenizer st = new StringTokenizer(c.allTags, " ;,.)/-:(", false);
	    while (st.hasMoreTokens()) {
	    	 String s = st.nextToken().trim();
	    	 if (keywords.containsKey(s)/* && !isCommon(s)*/) {
	    		 keywords.remove(s);
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
		
	}
}
