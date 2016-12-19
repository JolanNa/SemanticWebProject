import OntIndexer.*;

import java.util.StringTokenizer;

import org.apache.lucene.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public class LuceneTests {

	 static String sparqlEndpoint = "http://localhost:3030/DatasetPathName/sparql";

	  // get expression values for uniprot acc Q16850
	  static String sparqlQuery = "PREFIX wo: <http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#>"+
"PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>"+
"PREFIX rdfs: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+

"SELECT ?subject WHERE {{?x wo:hasName ?subject} UNION{?x wo:hasAbbreviation ?subject}}";

	  public static void buildNames(NameIndexer ni) {

		  
	    Query query = QueryFactory.create(sparqlQuery) ;
	    try (QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, sparqlQuery)) {
	      ResultSet results = qexec.execSelect() ;
	      for ( ; results.hasNext() ; )
	      {
	        QuerySolution soln = results.nextSolution() ;
	        
	        Literal l = soln.getLiteral("subject") ;   // Get a result variable - must be a literal
	        System.out.println(l.toString().trim());
	        ni.addName(l.toString().trim());
	      }
	    }
	    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NameIndexer ni= new NameIndexer();
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
		ni.addTeam("team");
		ni.addProperty("age");
		ni.addProperty("height");
		ni.addProperty("weight");
		ni.addProperty("style");
		ni.addProperty("name");
		ni.addProperty("birthday");
		ni.addProperty("age");
		ni.addProperty("trainer");
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
		
		// building our query
		String query="Alberto Del Rio";
		String sparql=ni.reccomendationQuery(query);
		
		/*PREFIX wo: <http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#>
PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>
SELECT ?individual ?title
WHERE
{

?individual rdf:label "Buchanan"@en.
?individual wo:wonTitleReign ?reign.
?title wo:hasTitleReign ?reign
}

PREFIX wo: <http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#>  
PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#> 
 SELECT ?individual ?match  WHERE  {    ?individual rdf:label "Ryuji Hijikata"@en. { ?match wo:hasLoser ?individual} UNION {
  ?match wo:hasWinner ?individual}}
  
PREFIX wo: <http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#>  
PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#> 
 SELECT  ?individual ?promotion WHERE  {    ?individual rdf:label "Ryuji Hijikata"@en. { ?match wo:hasLoser ?individual} UNION {
  ?match wo:hasWinner ?individual}.
?card wo:hasMatch ?match.
?event wo:hasCard ?card.
?promotion wo:hasEvent ?event}
*
//*/
		//t= new StringTokenizer(query);
//		String sparql= "SELECT ?subject ?object WHERE {?individual rdf:label \""+ni.buildSubject(t) +"\"@en";
//		if(titles>=1)
//		{
//			sparql+=". ?individual wo:wonTitleReign ?reign. ?object wo:hasTitleReign ?reign}";
//		}
//		if(promos>=1)
//		{
//			sparql+=". ?match wo:hasWinner ?individual}. ?card wo:hasMatch ?match. ?event wo:hasCard ?card. ?object wo:hasEvent ?event}";
//		}
//		if(matches>=1)
//		{
//			sparql+= ". { ?object wo:hasLoser ?subject} UNION {  ?object wo:hasWinner ?subject}}";
//		}
		
		System.out.println(sparql);
		
	}

}
