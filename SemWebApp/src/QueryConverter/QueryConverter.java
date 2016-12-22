package QueryConverter;


/// query return object

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import NameIndexer.NameIndexer;

public class QueryConverter {
	/*CHANGE THE PATH JOLAN!!!*/ private static String ontfile= "/Users/Jolan/git/SemanticWebProject/javaProject/wrestling.rdf";
	
	private static QueryConverter instance = null;
	
	private static final String NS= "http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5";
    private static OntModel m = ModelFactory.createOntologyModel();
    private static OntDocumentManager dm = m.getDocumentManager();
    
    NameIndexer ni;
	
	private static final HashMap<String, String> PROPERTIES = new HashMap<String, String>() {{
		put("titles", "wonTitleReign");
		put("title", "wonTitleReign");
		put("name", "hasName");
		put("birthday", "hasBirthdate");
		put("age", "hasAge");
		put("trainer", "hasTrainer");
		put("matches", "wonMatch"); // still needs work... connection goes from match to fighter not this way around
		put("alias", "isAlterEgo");
		put("weight", "hasWeight");
		put("height", "hasHeight'");
		put("owner", "hasOwnership");
		put("owns", "isOwner");
		put("holder", "hasTitleReign");
		put("trademark", "hasTrademarkHold");
		put("sytle", "hasStyle");
		put("events", "hasEvent");
		put("date", "hasDate");
		
	}};
	
	private static HashMap<String, ArrayList<String>> classProperties = new HashMap<String, ArrayList<String>>();
	private static final HashMap<String, String[]> classPropertiess = new HashMap<String, String[]>() {{
		put("Wrestler", new String[]{"hasTitle", "hasName", "hasBirthdate"});
		put("Title", new String[]{"hasName"});
	}};
	
	private static HashMap<String, String> propertyReturnType = new HashMap<String, String>() {{
		put("hasTitle", "Title");
		put("hasName", "String");
	}};	
	
	public static QueryConverter getInstance() {
		if (instance==null) {
			instance = new QueryConverter();
		}
		return instance;
	}
	
	protected QueryConverter() {
		
		/////////////////////////////////
		// 1. -> get the index of the entities
		/////////////////////////////////
		ni = new NameIndexer();
		buildNames(ni);
		ni.addPromotion("company");
		ni.addPromotion("organization");
		ni.addTitle("title");
		ni.addTitle("titles");
		ni.addTitle("strap");
		ni.addTitle("belt");
		ni.addTitle("belts");
		ni.addTitle("champion");
		ni.addMatch("matches");
		ni.addMatch("fight");
		ni.addMatch("fights");
		ni.addMatch("match");
		ni.addProperty("age");
		ni.addProperty("height");
		ni.addProperty("weight");
		ni.addProperty("style");
		ni.addProperty("name");
		ni.addProperty("birthday");
		ni.addProperty("age");
		ni.addProperty("trainer");
		ni.addProperty("matches"); // still needs work... connection goes from match to fighter not this way around
		ni.addProperty("alias");
		ni.addProperty("weight");
		ni.addProperty("height");
		ni.addProperty("owner");
		ni.addProperty("owns");
		ni.addProperty("holder");
		ni.addProperty("trademark");
		ni.addProperty("sytle");
		ni.addProperty("events");
		ni.addProperty("date");
		/////////////////////////////////
		// 1. -> get the PROPERTIES... or wordnet
		/////////////////////////////////
		
		/////////////////////////////////
		// 1. -> for each class get all the properties
		/////////////////////////////////
		JSONObject properties = null;
		try {
			properties = executeSPARQL("PREFIX wo: <http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#> " 
										+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
										+ "SELECT ?property ?domain "
										+ "WHERE {" 
												+ "{?property <http://www.w3.org/2000/01/rdf-schema#domain> ?domain} "
											+ "UNION" 
											+ "{"
											    + "?domain rdfs:subClassOf ?superClass." 
											    + "?property <http://www.w3.org/2000/01/rdf-schema#domain> ?superClass."
											+ "}"
									    + "}");
			System.out.println("PREFIX wo: <http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#> " 
					+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
					+ "SELECT ?property ?domain WHERE {" 
					+ "{?property <http://www.w3.org/2000/01/rdf-schema#domain> ?domain} "
					+ "UNION" 
				    + "{?domain rdfs:subClassOf ?superClass." 
				    + "?property <http://www.w3.org/2000/01/rdf-schema#domain> ?superClass.} " 
				    + "}");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JSONObject value = properties.getJSONObject("results");
//		System.out.println(value.getJSONArray("bindings").toString(4));
		for(Object e: value.getJSONArray("bindings")) {
			JSONObject jo = (JSONObject) e;
			String domain = jo.getJSONObject("domain").get("value").toString();
			String property = jo.getJSONObject("property").get("value").toString();
//			System.out.println(range);
//			System.out.println(property);
			String[] splitted = property.split("#|:");
			property = splitted[splitted.length - 1];
			splitted = domain.split("#|:");
			domain = splitted[splitted.length - 1];
			ArrayList<String> theProperties = classProperties.get(domain);
			if (theProperties == null) {
				theProperties = new ArrayList<String>();
				classProperties.put(domain, theProperties);
			}
			theProperties.add(property);
		}
		classProperties.get("Wrestler").addAll(classProperties.get("Thing"));
		classProperties.get("Person").addAll(classProperties.get("Thing"));
//		System.out.println(classProperties.toString());
		classProperties.get("Owner").addAll(classProperties.get("Thing"));
		classProperties.get("Commentator").addAll(classProperties.get("Thing"));
		classProperties.get("Trainer").addAll(classProperties.get("Thing"));
		classProperties.get("Event").addAll(classProperties.get("Thing"));
		classProperties.get("House-Show").addAll(classProperties.get("Thing"));
		classProperties.get("PayPerView").addAll(classProperties.get("Thing"));
		classProperties.get("TV-Show").addAll(classProperties.get("Thing"));
		classProperties.get("TitleReign").addAll(classProperties.get("Thing"));
		classProperties.get("TitleVacancy").addAll(classProperties.get("Thing"));
		System.out.println(classProperties.toString());
		
		
		/////////////////////////////////
		// 1. -> get return types of properties
		/////////////////////////////////
		JSONObject propertiesRanges = null;
		try {
			propertiesRanges = executeSPARQL("SELECT ?property ?range WHERE {?property <http://www.w3.org/2000/01/rdf-schema#range> ?range}");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		value = propertiesRanges.getJSONObject("results");
//		System.out.println(value.getJSONArray("bindings").toString(4));	
		for(Object e: value.getJSONArray("bindings")) {
			JSONObject jo = (JSONObject) e;
			String range = jo.getJSONObject("range").get("value").toString();
			String property = jo.getJSONObject("property").get("value").toString();
//			System.out.println(range);
//			System.out.println(property);
			String[] splitted = property.split("#|:");
			property = splitted[splitted.length - 1];
			splitted = range.split("#|:");
			range = splitted[splitted.length - 1];
			propertyReturnType.put(property, range);
		}
//		System.out.println(propertyReturnType.toString());
		
//		Iterator keys = value.keys();
//		while(keys.hasNext()) {
//			System.out.println("naja");
//			System.out.println(keys.next());
//		}
		
		
		
		
		//TODO make singleton
		
		
		
	}
	
	public QueryResult processUserQuery(String userQuery) {
		QueryResult queryResult = new QueryResult();
		String sparql_query = convertToSPARQL(userQuery, queryResult);
		

		try {
			String sparqlEndpoint = "http://localhost:3030/DatasetPathName/sparql";
			Query query = QueryFactory.create(sparql_query);
			QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, sparql_query);
			queryResult.setResult(qexec.execSelect());
			if (!queryResult.getResult().hasNext()) {
				queryResult.setSearchType("noValues");
			}
			
//			queryResult.setResult(executeSPARQL(sparql_query));
		} catch (Exception e) {
			e.printStackTrace();
			queryResult.setSearchType("error");
		}
		
		return queryResult;
		
	}
	
	
	public void testWordnet() {
		
	}
	
	public String convertToSPARQL(String userQuery, QueryResult queryResult) {
			
		//filter out the the names with lucene
		ArrayList<String> subjects = new ArrayList<String>();

		Vector<String> names=new Vector<String>();
		String inputQuery = userQuery;
		if(userQuery.contains("|")) {
			subjects.add(userQuery.split("\\|")[0]);
			inputQuery = userQuery.split("\\|")[1];
		} else {
			StringTokenizer t= new StringTokenizer(userQuery);
			names=ni.getNameNumber(userQuery);
			names=ni.nameValidator(names);
			
			subjects = new ArrayList<String>(names);
//			for(String s: subjects) {
//				userQuery.replaceAll(names.get(0), "");
//			}
		}
		
		System.out.println("\n\nSUBJECTS : " + subjects.toString() + "\n\n" );
		
//		String query="Adam Cole trainer";
//		String sparql2=ni.buildQuery(query);
//		System.out.println(sparql2);
//		
//		StringTokenizer t= new StringTokenizer(query);
//		Vector<String> names=new Vector<String>();
//		names=ni.getNameNumber(query);
//		System.out.println("Names " + names.toString());
		
		
		StringTokenizer st = new StringTokenizer(inputQuery);
		ArrayList<String> foundProperties = new ArrayList<String>();
		//get the properties mentioned in the text
		while(st.hasMoreTokens()) {
			String value = PROPERTIES.get(st.nextToken());
			if (value != null) {
			    foundProperties.add(value);
			} 
		}
//		System.out.println(foundProperties.size());
		
		
//		dm.addAltEntry( "http://www.semanticweb.org/vasco/ontologies/2016/9/wrestling",
//                "file:" + ontfile );
//		m.read("http://www.semanticweb.org/vasco/ontologies/2016/9/wrestling");
//
//		
		
		String sparql = "";
		
		
		
		/**
		 * 
		 * - check for keywords in serch query:
		 * 		- how much
		 * 			- add a count to the results of query
		 * 		- when/ which day/ which date/ on what date ...
		 * 			-add the has date to properties
		 * 		- 19.10.2012 or other dates
		 * 			-check if subject/s or class/es
		 * 				- add hasDate property with value
		 * 
		 * 			
		 * 
		 * - 0 subjects 
		 * 		- class name
		 * 			-all instances of that type
		 * - 1 subject
		 * 		- no properties
		 * 			- entity search
		 * 		- #properties > 0 [done]
		 * 			apply properties that return objects first & go down from there [done]
		 * - #subjects > 1
		 * 		-properties >= 1
		 * 			- check if properties can be assigned to both/all subjects
		 * 		-else
		 * 			- check for properties with same value
		 * - nothing 
		 * 		- just a normal keyword search over all the stuff... or just entity search
		 * 
		 */
		//helpers
		boolean cont = true; //continue
		String sparql_select = "";
		int currentLetter = 23; //23 = x because 0 = a   : this is used for the variables in the query
		int skipLetters = 0;
	
		ArrayList<String> classNames = new ArrayList<String>();
		if(subjects.size() == 0 && classNames.size() > 0) { /// here add  && classnames.size() > 0
			//check if there exists a class name
				// then extract all the types of this class
		} else if (subjects.size() == 1) {
			if (foundProperties.size() == 0) {

				queryResult.setSearchType("entity");
				String entityQuery = "PREFIX wo: <http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#>\n"
                          + "PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>\n"
                          + "SELECT"
                          + "  ?individual ?label (count(?properties) as ?numbr)\n"
                          + "WHERE\n"
                          + "{"
                          + "  OPTIONAL"
                          + "  {"
                          + "    ?individual rdf:label ?label;"
                          + "    wo:hasName ?name"
                          + "  }"
                          + "  FILTER ( regex(str(?label), \"" + userQuery + "\", \"i\") || regex(str(?name), \"" + userQuery+ "\", \"i\"))"
                          + "      ."
                          + ""
                          + "  ?individual ?properties  ?values."
                          + "  ?individual rdf:label ?label"
                          + "}\n"
                          + "GROUP BY\n"
                          + "  ?individual ?numbr ?label\n"
                          + "ORDER BY DESC(?numbr)";
				queryResult.addToSparql(entityQuery);
//				System.out.println(entityQuery);
				//make a entity search
			} else {
				//initial value for currentObjectType(object that gets the properties right now)
				String currentObjectType = "Wrestler"; // intialize value with type of subject
				queryResult.addToSparql("\t?x rdf:label \"" + subjects.get(0) + "\"@en.\n"); // add subject to query
				//add all properties to query until 
				while (foundProperties.size() > 0 && cont) {
					//get all properties that can be assigned to currentObjectType
					ArrayList<String> possibleProperties = new ArrayList<String>();
					int latestPossiblePropertyIdx = -1;
					int latestObjectIdx = -1; // stores the latest property that returns a object
					String p;
					for(int i = foundProperties.size() - 1; i >= 0; i--) {
						p = foundProperties.get(i);
						if(classProperties.get(currentObjectType).contains(p)) {
							possibleProperties.add(p); // is a possible property of current object
							latestPossiblePropertyIdx = i;
							if (propertyReturnType.get(p) != "Data")
								latestObjectIdx = i; // the return type of property is object
						}	
					}
					// now add property to the query
					if (latestObjectIdx >= 0) {
						// in this case we add a property with a object return
						propertyToSparql(foundProperties.get(latestObjectIdx), queryResult);
						currentObjectType = propertyReturnType.get(foundProperties.get(latestObjectIdx));
						queryResult.addToCurrentLetter(queryResult.getSkipLetters() + 1); // now the current letter changes with the change of the current object
						queryResult.resetSkipLetters(); // makes them 0
						foundProperties.remove(latestObjectIdx);
						queryResult.setSearchType("factual");
					} else if (latestPossiblePropertyIdx >= 0) {
						// in this cas we add a property with a 
						propertyToSparql(foundProperties.get(latestPossiblePropertyIdx), queryResult);
						foundProperties.remove(latestPossiblePropertyIdx);
						queryResult.addToSkipLetters(1); // in the query we use a new letter, so we have to skip them next time
						queryResult.setSearchType("factual");
					} else {
						cont = false; // now we don't add properties anymore so we quit... there still might be unprocessed properties!!!!
					}
				} // while (foundProp....
				// now get letter of last result
				sparql_select = "SELECT ?"+ intToLetter(queryResult.getCurrentLetter());
				
				// add the extra properties (which are all optional) if the last one was a object
				System.out.println(classProperties.get(currentObjectType));
				ArrayList<String> propertiesToExtract = classProperties.get(currentObjectType);
				if (propertiesToExtract != null) {
					for(String p: propertiesToExtract) {
						queryResult.addToSparql("\n\tOPTIONAL { ?" + intToLetter(queryResult.getCurrentLetter()) + " wo:"+ p +" ?" + p + "Tmp. }");
						sparql_select += " (SAMPLE(?"+p+"Tmp) AS ?"+p+")";
					}
					queryResult.addToSparql("\n\tOPTIONAL { ?" + intToLetter(queryResult.getCurrentLetter()) + " rdf:label ?label. }");
				}
				
//				sparql += "\n\t?" + (char) (97 + (currentLetter % 27)) + " rdf:label ?" + (char) (97 + ((currentLetter + skipLetters + 1) % 27)) + ".";
//				String retrievedLetters = "?x";
//				for(int l = 23 + 1; l <= currentLetter + skipLetters; l++)
//					retrievedLetters += " ?" + (char) (97 + (l%27));
				queryResult.setSparqlQuery(sparql_select + " \nWHERE { \n" + queryResult.getSparqlQuery() + " \n}");
				//Group by current letter
				queryResult.addToSparql("GROUP BY ?"+ intToLetter(queryResult.getCurrentLetter()));
				
				//add the prefixes:
				queryResult.setSparqlQuery("PREFIX wo: <http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#>\nPREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>\n\n" + queryResult.getSparqlQuery());
				
		
			}
		} else if (subjects.size() > 1){
			// relational search
			//now check for similarities and/or overlaps
			queryResult.setSearchType("relational");
			StringTokenizer t= new StringTokenizer(userQuery);
			int titles=0;
			int promos=0;
			int matches=0;
			int properties=0;
			int teams=0;
			String subject = ni.buildSubject(t);
			//names=getLabelCount("name", subject);
			t= new StringTokenizer(userQuery);
			queryResult.addToSparql("PREFIX wo: <http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#>\nPREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>\n\n");
			
			
			
			while(t.hasMoreTokens())
			{
				String token= t.nextToken();
				
				titles+=ni.getLabelCount("title", token);
				promos+=ni.getLabelCount("promotion", token);
				matches+=ni.getLabelCount("match", token);
				properties+=ni.getLabelCount("property", token);
				teams+=ni.getLabelCount("team", token);
				
				
			}
			if(titles==0 && promos==0 && matches==0 && properties==0 && teams==0)
				queryResult.addToSparql("SELECT ?relationship WHERE {?subject1 rdf:label \""+ names.get(0)+"\"@en . ?subject2 rdf:label \""+ names.get(1)+"\"@en. ?subject1 ?relationship ?subject2}");
			
			else if(matches==1)
			{
				queryResult.addToSparql("SELECT ?match ?winner ?loser WHERE{"+  
				"?subject1 wo:hasName \""+names.get(0)+"\"."+
				"?subject2 wo:hasName \""+names.get(1)+"\"."+
				"{?match  wo:hasWinner ?subject1. ?match  wo:hasLoser ?subject2. ?match wo:hasWinner ?winner. ?match wo:hasLoser ?loser } UNION"+
				"{?match  wo:hasWinner ?subject2. ?match  wo:hasLoser ?subject1. ?match wo:hasWinner ?winner. ?match wo:hasLoser ?loser}}");
			}
			else if(teams==1)
			{
				queryResult.addToSparql("SELECT ?team ?member1 ?member2 "
						+ "WHERE{ "
						+ "?member1 wo:hasName \""+names.get(0)+"\". "
						+ "?member2 wo:hasName \""+names.get(1)+"\". "
						+ "?team wo:hasPerson ?member1. "
						+ "?team wo:hasPerson ?member2 "
						+ "}");
			}
//			System.out.print(queryResult.getSparqlQuery());
			
			
		} else { // seems like there are no subjects
			// do some standard search
			queryResult.setSearchType("entity");
			String entityQuery = "PREFIX wo: <http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#>\n"
                      + "PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>\n"
                      + "SELECT"
                      + "  ?individual ?label (count(?properties) as ?numbr)\n"
                      + "WHERE\n"
                      + "{"
                      + "  OPTIONAL"
                      + "  {"
                      + "    ?individual rdf:label ?label;"
                      + "    wo:hasName ?name"
                      + "  }"
                      + "  FILTER ( regex(str(?label), \"" + userQuery + "\", \"i\") || regex(str(?name), \"" + userQuery+ "\", \"i\"))"
                      + "      ."
                      + ""
                      + "  ?individual ?properties  ?values."
                      + "  ?individual rdf:label ?label"
                      + "}\n"
                      + "GROUP BY\n"
                      + "  ?individual ?numbr ?label\n"
                      + "ORDER BY DESC(?numbr)";
			queryResult.addToSparql(entityQuery);
//			System.out.println(entityQuery);
		}
		
		// Do this in the if clauses !!

		return queryResult.getSparqlQuery();
	} // convertToSparql
	
	private void propertyToSparql(String property, QueryResult queryResult) {
		switch(property) {
		case "wonTitleReign":
			addToQuery("wonTitleReign", queryResult);
			queryResult.setAnswerClass("TitleReign");
			break;
		default:
			addToQuery(property, queryResult);
			break;
		}
		queryResult.setAnswerClass(propertyReturnType.get(property));
		
	}
	
	
	private void addToQuery(String property, QueryResult qr) {
		qr.addToSparql("\n\t?" + intToLetter(qr.getCurrentLetter()) + " wo:" + property + " ?" + intToLetter(qr.getCurrentLetter() + qr.getSkipLetters() + 1) + ".");
//		return sparql + "\n\t?" + (char) (97 + (letter % 27)) + " wo:" + property + " ?" + (char) (97 + ((letter + skipLetters + 1) % 27)) + ".";
	}
	
	
	public static JSONObject executeSPARQL(String userQuery) throws IOException {
		String endpointUrl = "http://localhost:3030/DatasetPathName/sparql";
		String query = "SELECT ?x WHERE { ?x <http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#hasName> \"World Wrestling Entertainment\" }";
	    System.out.println("in Execute");
		System.out.println(userQuery);
		String finalQuery = endpointUrl + "?query=" + URLEncoder.encode(userQuery, "UTF-8") + "&format=json";
		String requestURL = finalQuery;
		URL wikiRequest = new URL(requestURL);
		URLConnection connection = wikiRequest.openConnection();  
		connection.setDoOutput(true);  
	
		Scanner scanner = new Scanner(wikiRequest.openStream());
		String response = scanner.useDelimiter("\\Z").next();
		JSONTokener tokener = new JSONTokener(response);
		JSONObject root = new JSONObject(tokener);
	//		JSONObject json = Util.parseJson(response);
		scanner.close();
		return root;
	}
	
	
//	public JSONArray getAllProperties(String uri) {
//		String query = "SELECT ?property propValue WHERE { <" + uri + "> ?property ?propValue }";
//		JSONObject result = executeSPARQL(query);
//		JSONObject value = result.getJSONObject("results");
//		JSONArray results = value.getJSONArray("bindings");
//		return results;
//	}
	
	private char intToLetter(int l) {
		return (char) (97 + (l%27));
	}
	
	
	
	
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	//
	//
	//
	//////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////
	
	public static void buildNames(NameIndexer ni) {
		String sparqlEndpoint = "http://localhost:3030/DatasetPathName/sparql";
		String sparqlQuery = "PREFIX wo: <http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#>"+
				"PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>"+
				"PREFIX rdfs: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+

				"SELECT ?subject WHERE {?x wo:hasName ?subject}";
		  
	    Query query = QueryFactory.create(sparqlQuery) ;
	    try (QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, sparqlQuery)) {
	      ResultSet results = qexec.execSelect() ;
	      for ( ; results.hasNext() ; )
	      {
	        QuerySolution soln = results.nextSolution() ;
	        
	        Literal l = soln.getLiteral("subject") ;   // Get a result variable - must be a literal
//	        System.out.println(l.toString().trim());
	        ni.addName(l.toString().trim());
	      }
	    }
	}
	
	public NameIndexer getNameIndexer() {
		return ni;
	}
	
}
