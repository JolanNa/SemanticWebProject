package servlets;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.json.JSONArray;
import org.json.JSONObject;

import QueryConverter.QueryConverter;
import QueryConverter.QueryResult;

/**
 * Servlet implementation class ServletSearch
 */
@WebServlet("/Search")
public class ServletSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletSearch() {
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
		String resultHtml = "";

		QueryConverter qc = QueryConverter.getInstance();
		
		QueryResult queryResult = qc.processUserQuery(query);
		
		if (queryResult.getSearchType().equals("error")) {
			resultHtml += "Error";
			resultHtml += "\n\n" + queryResult.getSparqlQuery();
		} else if (queryResult.getSearchType().equals("noValues")) {
			resultHtml += "Sorry, we couln't find anything!";
		} else if (queryResult.getSearchType().equals("entity")) {
			//////////////////////////////////////////////////////
			// ENTITY SEARCH
			//////////////////////////////////////////////////////
			resultHtml += "<ul class=\"resultListEntity\">";
			ResultSet results = queryResult.getResult(); ;
			for ( ; results.hasNext() ; )
			{
				QuerySolution sol = results.nextSolution() ;
		        
				resultHtml += "<a class=\"resulLink\" href=\"Details?uri="+ sol.get("individual").toString().replaceAll("#", "HASHTAG") +"\">"
						+ "<li>" + sol.get("label") + "</li></a>";
					
				
		    }
			resultHtml += "</ul>";
		} else if (queryResult.getSearchType().equals("factual")) {

			resultHtml += "<ul class=\"resultListFactual\">";
			ResultSet results = queryResult.getResult(); ;
			for ( ; results.hasNext() ; )
			{
				QuerySolution sol = results.nextSolution() ;
		        
		        switch (queryResult.getAnswerClass()) {
				case "Person":
					resultHtml += personToHtml(sol, queryResult.getLastLetter());
					break;
				default:
					resultHtml += "<li>" + sol.get(queryResult.getLastLetter()) + "</li>";
					break;
				}
		    }
			resultHtml += "</ul>";
		} else if (queryResult.getSearchType().equals("relational")) {
			//relational search
			resultHtml += "<ul class=\"resultListRelational\">";
			ResultSet results = queryResult.getResult(); 

			System.out.println("\n\nResults : " + results.toString());
			for ( ; results.hasNext() ; )
			{

				QuerySolution sol = results.nextSolution() ;
//				resultHtml += "<li>" + sol.get("relation") + "</li>";
				resultHtml += "<li>" + sol.get("relationship").toString() + "</li>";
		    }
			resultHtml += "</ul>";
			
			
		}
		
//		JspWriter jsp = new JspWriter();
//		Iterator i;
		request.setAttribute("resultHtml", resultHtml);
		request.setAttribute("userQuery", query);
//		request.setAttribute("results", results);
		request.setAttribute("SearchType", queryResult.getSearchType());
		request.getRequestDispatcher("/search.jsp").forward(request, response);
	}
	
	
//	private HttpServletRequest createResults(HttpServletRequest request, String userQuery) {
//		QueryConverter qc = new QueryConverter();
//		String sparql = qc.convertToSPARQL(userQuery);
//		
//		JSONObject result = qc.executeSPARQL(sparql);
//		JSONObject value = result.getJSONObject("results");
//		JSONArray results = value.getJSONArray("bindings");
//		
//		
//		
//		
//		return null;
//	}

	
	private String personToHtml(QuerySolution r, String lastLetter) {
		String html = "";
		
//		for(Iterator iterator = r.keySet().iterator(); iterator.hasNext();) {
//	  	    String key = (String) iterator.next();
//	  	    JSONObject result = (JSONObject) r.get(key);
//	  	    String type = (String) result.get("type");
	  	    
	  	    // let's get all the values we want to display
			System.out.println(lastLetter);
			System.out.println(r.toString());
	  	    
	  	    html += "  	  <a class=\"resulLink\" href=\"Details?uri="+ getValueFromKey(lastLetter, r).replaceAll("#", "HASHTAG") +"\"><li class=\"liResult\"> ";
		  	html += "		<div class=\"resultImageContainer\"> ";
		  	html += "			<img src=\""+ getValueFromKey("imageLink", r)+ "\"  onerror=\"this.onerror=null;this.src='https://secure.gravatar.com/avatar/f22077456dc75ae4115888151c4a02a7?s=80&d=identicon';\" /> "; //+ "\" onerror=\"this.onerror=null;this.src='notFound.gif';\"
		  	html += "		</div> ";
		  	html += "		<div class=\"resultTextContainer\"> ";
		  	html += "			<div class=\"resultHeading\">" + getValueFromKey("hasName", r)  + "</div><div class=\"resultInactive\"></div> ";
			html += "   <div class=\"resultInfoText\">" + getValueFromKey("hasAge", r)  + "</div> ";
			html += "  <div class=\"resultInfoText\">" + getValueFromKey("hasBirthplace", r)  + "</div> ";
			html += "		</div> ";
			html += "		<div class=\"end-float\"> </div> ";
			html += "	</li></a>";
	  	    
	  	    
  	    	
//		}
		
		return html;
	}
	
	private String getValueFromKey(String key, QuerySolution solution) {
		try {
//			JSONObject result = (JSONObject) resultList.get(key);
//			Literal l = solution.getLiteral(key) ;   // Get a result variable - must be a literal
			RDFNode l = solution.get(key) ;
			if (l != null) {
		  	    return l.toString();
			} else {
				return "";
			}
		} catch(Exception e) {
			return "";
		}
	}
}
