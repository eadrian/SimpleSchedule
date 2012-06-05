package web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.*;

import java.text.*;
 
import java.util.*;
 
import javax.servlet.*;
 
import javax.servlet.http.*;
 

/**
 * Servlet implementation class Servlet_Login
 */
public class Servlet_Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet_Login() {
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
		System.out.println("logging in");
		HttpSession session = request.getSession();	
		String action = request.getParameter("action");
		if (action.equals("logout")) {				// LOGOUT
			//session.removeAttribute("userData");
			System.out.println("logging out");
			session.setAttribute("userData", null);
		} else {								// LOGIN
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			String address = "C:\\\\Users\\\\Elaine\\Desktop\\galadriel\\CourserankConnector\\";
			String tagger_address = address + "models/english-left3words-distsim.tagger";
			MaxentTagger t = null;
			userData u = null;

			// initialize or retrieve the userData
			if (session.getAttribute("userData") == null) {
				System.out.println("setting user Data...");
				try {
					t = new MaxentTagger(tagger_address);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        //u  = new userData("eaconte","mttresp1",t);
		        u  = new userData(username,password,t);
				session.setAttribute("userData", u);
			} else {			
				System.out.println("reusing session variable");			
				u = (userData) session.getAttribute("userData");
			}
			
			
			

	        u.rejectCourse("CS 181");
	        keywordSearch k = new keywordSearch(u.tagger);
	        ScheduleFiller sf = new ScheduleFiller();
	        
	        Schedule s = new Schedule();
	        //s.addItem("CS244", "01010", 1250, 1405);
	        //s.addItem("No Fridays", "00001", 0, 2400);
	        //s.addItem("ALLTIME", "11111", 0, 2400);
	        searchFactors f = new searchFactors();
	        //f.setFactor("RELEVANCE", 1);
	        f.setFactor("INTEREST", 2);
	        f.setFactor("LEVEL", 1);
	        //f.setFactor("GERS", 1);
	        f.setFactor("WORK", 1);
	        f.setFactor("POPULARITY", 1);
	        f.setFactor("INDEPENDENT", 1);
	        f.setFactor("PROJECT", 1);
	        f.setFactor("TOTAL", 1);
	        List<String> barredCourses = new ArrayList<String>();
	        barredCourses.add("CS 198");
	        barredCourses.add("ENGR 70A");
	        barredCourses.add("ENGR 70B");
	        barredCourses.add("CS 105");
	        //u.removeGER("ECGender");
	        Date start = new Date();
	        
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
	         
	        if (true)
	        	return;
	        
	        
	        
	        k.search(u,"politics", "ALL",f,s);
	        
			System.err.println("Time to search for courses: "+(float)((finish.getTime()-start.getTime())/1000f));
			
			
	        response.getWriter().write("success!!!");
		}
	}

}
