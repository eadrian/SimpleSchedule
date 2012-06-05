package web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.stanford.nlp.io.EncodingPrintWriter.out;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;



/**
 * Servlet implementation class Servlet_Search
 */
public class Servlet_Search extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public Servlet_Search() {
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

		String query = request.getParameter("query");
		int relevance = Integer.parseInt(request.getParameter("relevance"));
		int interest = Integer.parseInt(request.getParameter("interest"));
		int level = Integer.parseInt(request.getParameter("level"));
		int work = Integer.parseInt(request.getParameter("work"));
		int popularity = Integer.parseInt(request.getParameter("popularity"));
		int gers = Integer.parseInt(request.getParameter("gers"));
		
		
		// start key word search
        keywordSearch k = new keywordSearch(u.tagger);        
        Schedule s = new Schedule();
        // get factors
        s.addItem("CS244", "01010", 1250, 1405);
        s.addItem("No Fridays", "00001", 0, 2400);
        //s.addItem("ALLTIME", "11111", 0, 2400);
        searchFactors f = new searchFactors();
        f.setFactor("RELEVANCE", relevance);
        f.setFactor("INTEREST", interest);
        f.setFactor("LEVEL", level);
        f.setFactor("WORK", work);
        f.setFactor("POPULARITY", popularity);
        f.setFactor("GERS", gers);
        f.setFactor("TOTAL", 1);
        f.setFactor("PROJECT", 1);
        f.setFactor("INDEPENDENT", 1);
        // get the map of scores and courses
        //Map<String, Float> scoreMap = k.search(u,"computer networks", "ALL",f,s);
		List<Course> sortedScores = k.search(u,query, "ALL",f,s);
/*
		for (Course c : sortedScores) {
			System.out.println("LALA: " + c.code + " : " + c.description);
		}
		*/
        
		// construct what to send
        RequestDispatcher rd;
		List<String> searchResults = new ArrayList<String>();
		String returnedHTML = "";
		for (Course c : sortedScores) {
			returnedHTML += "<li class='course' onclick='blah()'><strong>" + c.code + ": " + c.title + "</strong>";
			returnedHTML += "<i>" + c.lectureDays + "</i>";
			returnedHTML += "<span style='display: none'>" + c.description + "</span>";
			returnedHTML += "</li>";
			System.out.println(c.code + c.description);
		}
		request.setAttribute("searchResults", searchResults);
		request.setAttribute("sortedScores", sortedScores);
		
		rd = request.getRequestDispatcher("search.jsp");
		//rd.forward(request, response);
		
		response.getWriter().write(returnedHTML);
		
	}

}
