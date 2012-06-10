package web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Servlet implementation class Servlet_Sched
 */
public class Servlet_Sched extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet_Sched() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		
		HttpSession session = request.getSession();	
		
		// change this to the location of your tagger
		String address = "C:\\\\Users\\\\Elaine\\Desktop\\galadriel\\CourserankConnector\\";
		String tagger_address = address + "models/english-left3words-distsim.tagger";
		MaxentTagger t = null;
		userData u = null;
		
		// initialize or retrieve the userData
		if (session.getAttribute("userData") == null) {
			try {
				t = new MaxentTagger(tagger_address);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        u  = new userData("eaconte","mttresp1",t);
			session.setAttribute("userData", u);
		} else {			
			System.out.println("reusing session variable");			
			u = (userData) session.getAttribute("userData");
		}
		
	        keywordSearch k = new keywordSearch(u.tagger);
	        ScheduleFiller sf = new ScheduleFiller();
	        
	        Schedule s = new Schedule();
	        
			
	        
	        // get xed out blocks
	        String dayStr = request.getParameter("blockDay");
	        String startStr = request.getParameter("blockStart");
	        String endStr = request.getParameter("blockEnd");
	        String courseStr = request.getParameter("blockCourse");
			
	        if ((dayStr != null) && (startStr != null) && ( endStr != null ) && (!dayStr.equals(""))) {
				int day = Integer.parseInt(dayStr);
				int startTime = Integer.parseInt(startStr);
				int endTime = Integer.parseInt(endStr);
				
				// block out
				String dayString = "00000";
				StringBuffer buf = new StringBuffer( dayString );
				buf.setCharAt( day - 1, '1' );
				dayString = buf.toString( );
				s.addItem("BLOCK", dayString, startTime, endTime);
	        }
	        //s.addItem("CS244", "01010", 1250, 1405);
	        //s.addItem("No Fridays", "00001", 0, 2400);
	        //s.addItem("ALLTIME", "11111", 0, 2400);
	        

	        
	        // get factors
	        int relevance = 0;
			int interest = 0;
			int level = 0;
			int work = 0;
			int popularity = 0;
			int gers = 0;
	        if ((request.getParameter("relevance") != null) && (!request.getParameter("relevance").equals(""))) {
	        	relevance = Integer.parseInt(request.getParameter("relevance"));
				interest = Integer.parseInt(request.getParameter("interest"));
				level = Integer.parseInt(request.getParameter("level"));
				work = Integer.parseInt(request.getParameter("work"));
				popularity = Integer.parseInt(request.getParameter("popularity"));
				gers = Integer.parseInt(request.getParameter("gers"));
	        }
			
			
	        searchFactors f = new searchFactors();
	        /*
	        //f.setFactor("RELEVANCE", 1);
	        f.setFactor("INTEREST", 2);
	        f.setFactor("LEVEL", 1);
	        //f.setFactor("GERS", 1);
	        f.setFactor("WORK", 1);
	        f.setFactor("POPULARITY", 1);
	        f.setFactor("INDEPENDENT", 1);
	        f.setFactor("PROJECT", 1);
	        f.setFactor("TOTAL", 1);
	        */

	        f.setFactor("RELEVANCE", .5f+((float)3*relevance/6));
	        f.setFactor("INTEREST", .5f+((float)4*interest/6));
	        f.setFactor("LEVEL", .5f+((float)3*level/6));
	        f.setFactor("WORK", .5f+((float)3*work/6));
	        f.setFactor("POPULARITY", .5f+((float)3*popularity/6));
	        f.setFactor("GERS", .5f+((float)3*gers/6));
	        f.setFactor("TOTAL", (float) 1.5);
	        f.setFactor("PROJECT", 1);
	        f.setFactor("INDEPENDENT", 1);

	        
	        // block out courses
	        // create sess var
	        if (courseStr != null) {
		        u.rejectCourse(courseStr);
		        if (session.getAttribute("sessionBarredCourses") == null) {
			        List<String> sessionBarredCourses = new ArrayList<String>();
			        sessionBarredCourses.add(courseStr);
		        	session.setAttribute("sessionBarredCourses", sessionBarredCourses);
		        } else {
		        	List<String> sessionBarredCourses = (List<String>) session.getAttribute("sessionBarredCourses");
		        	sessionBarredCourses.add(courseStr);
		        }
	        }
	        List<String> barredCourses = (List<String>) session.getAttribute("sessionBarredCourses"); 
	        if (barredCourses == null) {
	        	barredCourses = new ArrayList<String>();
	        	session.setAttribute("sessionBarredCourses", barredCourses);
	        }
	        //List<String> barredCourses = new ArrayList<String>();
	        barredCourses.add("CS 198");
	        barredCourses.add("ENGR 70A");
	        barredCourses.add("ENGR 70B");
	        barredCourses.add("CS 105");
	        //u.removeGER("ECGender");
	        Date start = new Date();
	        
	        
	        // Major Reqs
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
	        
	        // add courses
			List<Course> addedCourses = (List<Course>) session.getAttribute("addedCourses");
			if (addedCourses != null) {
				for (Course c : addedCourses) {	
					System.out.println("adding item" + c.code + c.lectureDays);
					s.addItem(c.code, c.lectureDays, c.timeBegin, c.timeEnd);
					//tdata.addCourse(c);
				}
			}
	        
	        
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

			// construct what to send
	        RequestDispatcher rd;
			request.setAttribute("scheduleResults", results);
			
			rd = request.getRequestDispatcher("schedule.jsp");
			rd.forward(request, response);
	        
	        //k.search(u,tdata,"politics", "ALL",f,s);
	        
			System.err.println("Time to search for courses: "+(float)((finish.getTime()-start.getTime())/1000f));
	}

}
