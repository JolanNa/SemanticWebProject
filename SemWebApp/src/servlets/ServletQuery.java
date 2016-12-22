package servlets;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import QueryConverter.QueryConverter;
import QueryConverter.QueryResult;

/**
 * Servlet implementation class ServletQuery
 */
@WebServlet("/servletquery")
public class ServletQuery extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletQuery() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		String query = request.getParameter("searchField");
		String name = "Randy Orton";
		
		QueryConverter qc = QueryConverter.getInstance();
		QueryResult queryResult = new QueryResult();
		String sparql = qc.convertToSPARQL(query, queryResult);
		out.println(sparql);
		
		JSONObject result = qc.executeSPARQL(sparql);
		
		out.println(result.toString(4));
	}

}
