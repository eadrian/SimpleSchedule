package web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.util.*;
import web.*;

/**
 * Servlet implementation class Servlet_AddCourse
 */
public class Servlet_AddCourse extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet_AddCourse() {
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
		if ((request.getParameter("courseid") == null) && (request.getParameter("courseid").equals("")) ) return;
		int courseid = Integer.parseInt(request.getParameter("courseid"));
		String coursecode = request.getParameter("coursecode");

		HttpSession session = request.getSession();	

		List<Course> addedCourses = (List<Course>) session.getAttribute("addedCourses");
		if (addedCourses == null) {
			List<Course> newAddedCourses = new ArrayList<Course>(); 
			session.setAttribute("addedCourses", newAddedCourses);
		}
		
		Course newaddedcourse = new Course(coursecode, courseid); 
		addedCourses.add(newaddedcourse);
		System.out.println("adding course: " + newaddedcourse.code);
		session.setAttribute("addedCourses", addedCourses);

	}

}
