package web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Servlet implementation class Servlet_ChangeTrack
 */
public class Servlet_ChangeTrack extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet_ChangeTrack() {
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

		if ((request.getParameter("track") == null) && (request.getParameter("track").equals("")) ) return;
		String track = request.getParameter("track");

		HttpSession session = request.getSession();	
		session.setAttribute("track", track);
		System.out.println("changed track to" + track);
		/*
		ServletContext sc = getServletContext();
		RequestDispatcher rd;
		
		String source = request.getParameter("source");
		if (source.equals("search")) {
			rd = request.getRequestDispatcher("search.jsp");
			rd.forward(request, response);
		}
		*/
	}

}
