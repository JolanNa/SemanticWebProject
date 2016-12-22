package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import NameIndexer.NameIndexer;
import QueryConverter.QueryConverter;

/**
 * Servlet implementation class ServletDetails
 */
@WebServlet("/Details")
public class ServletDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String nameSpace = "http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#";   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletDetails() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String resultHtml = "";
		
		String uri = request.getParameter("uri").toString().replaceAll("HASHTAG", "#");
		System.out.println(uri);
		
		QueryConverter qc = QueryConverter.getInstance();
		
		String sparqlQuery = "PREFIX wo: <http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#>\n"
				+ "PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>\n\n"
				+ "SELECT ?property ?value"
				+ "\nWHERE {"
				+ "   <"+uri+"> ?property ?value."
				+ "}";
		String name = "";
		
		try {
			String sparqlEndpoint = "http://localhost:3030/DatasetPathName/sparql";
			System.out.print(sparqlQuery);
			Query query = QueryFactory.create(sparqlQuery);
			QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, sparqlQuery);
			
			ResultSet rs = qexec.execSelect();
			
			System.out.println(rs.toString());
			
//			resultHtml += "<div class='detailsHeading'>"++"";
			
			
			name = "";
			String img = "";
			for ( ; rs.hasNext() ; )
			{
				QuerySolution sol = rs.nextSolution() ;
//				resultHtml += "<p>" + sol.get("property").toString() + "</p>";
				switch(sol.get("property").toString()) {
				case "http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#hasName":
					name = sol.get("value").toString();
					break;
				case "http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#imageLink":
					img = sol.get("value").toString();
				default:
					break;
					
				}
//				if(sol.get("property").toString().equals("http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#hasName")) {
//					resultHtml += sol.get("value").toString();
//					name = sol.get("value").toString();
//				}
		    }
			
			resultHtml += "		<div class=\"resultImageContainer\"> ";
			resultHtml += "			<img src=\""+ img + "\" onerror=\"this.onerror=null;this.src='https://secure.gravatar.com/avatar/f22077456dc75ae4115888151c4a02a7?s=80&d=identicon';\"  /> "; //+ "\" onerror=\"this.onerror=null;this.src='notFound.gif';\"
			resultHtml += "		</div> ";
			resultHtml += "		<div class=\"resultNameContainer\"> ";
			resultHtml += 			name;
			resultHtml += "		</div> ";

			System.out.println(rs.toString());
//			queryResult.setResult(executeSPARQL(sparql_query));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//////////////////////////////////////////////////////
		// recomendation
		//////////////////////////////////////////////////////
		
		String recommendationsHtml = "";
		Cookie[] cookies = request.getCookies();
        boolean foundCookie = false;
        String lastSeen = "";
        for(int i = 0; i < cookies.length; i++) { 
            Cookie cookie1 = cookies[i];
            if (cookie1.getName().equals("lastSeen")) {
                lastSeen = cookie1.getValue();
                foundCookie = true;
            }
        }  
        if (foundCookie) {
    		NameIndexer ni =  qc.getNameIndexer();
    		String recQuery = ni.reccomendationQuery(lastSeen, request, response);
    		try {
    			String sparqlEndpoint = "http://localhost:3030/DatasetPathName/sparql";
    			Query query = QueryFactory.create(recQuery);
    			QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, query);
    			ResultSet rs = qexec.execSelect();
    			System.out.println("\n RecQuery" + recQuery + "\n");
    			
    			recommendationsHtml += "<h4>Recommendations</h4>";
    			int counter = 0;
    			while(rs.hasNext() && counter <= 5)
    			{
    				QuerySolution sol = rs.nextSolution() ;
    				recommendationsHtml += "<a class=\"resulLink\" href=\"Details?uri="+ sol.get("related").toString().replaceAll("#", "HASHTAG") +"\">"
    						 + "<li>" + sol.get("label").toString() + "</li>";
    				counter++;
    		    }

    			
    			
//    			queryResult.setResult(executeSPARQL(sparql_query));
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
		
		
		
		Cookie cookie1 = new Cookie("lastSeen", name);
        cookie1.setMaxAge(24*60*60);
        response.addCookie(cookie1); 

        request.setAttribute("recommendationsHtml", recommendationsHtml);
		request.setAttribute("resultHtml", resultHtml);
		request.getRequestDispatcher("/details.jsp").forward(request, response);
	}

}
