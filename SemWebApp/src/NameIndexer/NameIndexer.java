package NameIndexer;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.index.Term;

public class NameIndexer {
	
	Vector<Term> terms;
	public NameIndexer()
	{
		terms = new Vector<Term>();
	}
	
	public void addName(String n)
	{
		Term nuterm= new Term("name",n);
		terms.add(nuterm);
	}
	public void addPromotion(String n)
	{
		Term nuterm= new Term("promotion",n);
		terms.add(nuterm);
	}
	public void addTitle(String n)
	{
		Term nuterm= new Term("title",n);
		terms.add(nuterm);
	}
	
	public void addMatch(String n)
	{
		Term nuterm= new Term("match",n);
		terms.add(nuterm);
	}
	
	public void addProperty(String n)
	{
		Term nuterm= new Term("property",n);
		terms.add(nuterm);
	}
	
	public String reccomendationQuery(String individual,HttpServletRequest request,HttpServletResponse response)
	{
        	
		
		return " PREFIX wo: <http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5#>"+
				"PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#>"+
				"PREFIX rdfs: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "SELECT DISTINCT ?related ?label WHERE {?dude wo:hasName \""+ individual+"\" OPTIONAL{"
				+ "{?match wo:hasLoser ?related . ?match wo:hasWinner ?dude} "
				+ "UNION {?match wo:hasWinner ?related . ?match wo:hasLoser ?dude}}"
				+ "OPTIONAL {?related rdfs:type ?type."
				+ "?type rdf:subClassOf* wo:Person."
				+ "?dude ?y ?related}"
				+ "OPTIONAL {{ ?match wo:hasLoser ?dude} UNION {  ?match wo:hasWinner ?dude}."
				+ "?card wo:hasMatch ?match."
				+ "?event wo:hasCard ?card."
				+ "?related ?z ?event}"
				+ "?related rdf:label ?label."
				+ "}";
	}
	
	public int getLabelCount(String label, String name)
	{
		int r=0;
		for(int i=0; i<terms.size(); i++)
			if(terms.get(i).field().equals(label) && terms.get(i).text().equals(name))
				r++;
		return r;
	}
	public boolean getLabel(String text, String label)
	{
		int r=0;
		for(int i=0; i<terms.size(); i++)
			if(terms.get(i).field().equals(label) && terms.get(i).text().equals(text))
				return true;
			else if(!terms.get(i).field().equals(label) && terms.get(i).text().equals(text))
				return false;
		return false;
	}
	
	public String buildSubject(StringTokenizer t)
	{
		String ret="";
		while(t.hasMoreTokens())
		{
			String next= t.nextToken();
			if(getLabel(next,"promotion")==false && getLabel(next,"title")==false && getLabel(next,"match")==false && getLabel(next,"property")==false)
			{
				ret+= next +" ";
				if(getLabel(ret.trim(),"name")==true)
					return ret.trim();
			}
			else
				break;
		}
		return ret.trim();
	}
	
	private static final HashMap<String, String> PROPERTIES = new HashMap<String, String>() {{
		put("name", "hasName");
		put("birthday", "hasBirthdate");
		put("age", "hasAge");
		put("trainer", "hasTrainer");
		put("matches", "wonMatch"); // still needs work... connection goes from match to fighter not this way around
		put("alias", "isAlterEgo");
		put("weight", "hasWeight");
		put("height", "hasHeight");
		put("owner", "hasOwnership");
		put("owns", "isOwner");
		put("holder", "hasTitleReign");
		put("trademark", "hasTrademarkHold");
		put("sytle", "hasStyle");
		put("events", "hasEvent");
		put("date", "hasDate");
		
	}};
	
	
	public Vector<String> getNameNumber(String q)
	{
		Vector<String> names= new Vector<String>();
		StringTokenizer t= new StringTokenizer(q);
		String name1= buildSubject(t);
		String name2= buildSubject(t);
		System.out.println("Name 1: " +name1);
		System.out.println("Name 2: " +name2);
		if(name1!="" && name2!="")
		{
			names.add(name1);
			names.add(name2);
		}
		else if(name1=="" && name2!="")
			names.add(name2);
		else if (name1!="" && name2=="")
			names.add(name1);
		return names;
	}
	
	public Vector<String> nameValidator(Vector<String> names)
	{
		for(int i=0; i<names.size(); i++)
		{
			if(getLabelCount("name",names.get(i))==0)
				names.remove(i);
		}
		return names;
	}
	
	public String buildQuery(String q)
	{
		
		StringTokenizer t= new StringTokenizer(q);
		Vector<String> names=new Vector<String>();
		int titles=0;
		int promos=0;
		int matches=0;
		int properties=0;
		String subject = buildSubject(t);
		//names=getLabelCount("name", subject);
		names=getNameNumber(q);
		t= new StringTokenizer(q);
		while(t.hasMoreTokens())
		{
			String token= t.nextToken();
			
			titles+=getLabelCount("title", token);
			promos+=getLabelCount("promotion", token);
			matches+=getLabelCount("match", token);
			properties+=getLabelCount("property", token);
			System.out.println("Token: " + token);
		}
		System.out.println("Number of names: " + names);
		System.out.println("Number of titles: " + titles);
		System.out.println("Number of promotions: " + promos);
		System.out.println("Number of properties: " + properties);
		t= new StringTokenizer(q);
		if(names.size() >=2 ) //relational search
		{
			if(titles==0 && promos==0 && matches==0 && properties==0)
				return "SELECT ?relationship WHERE {?subject1 rdf:label \""+ names.get(0)+"\"@en . ?subject2 rdf:label \""+ names.get(1)+"\"@en. ?subject1 ?relationship ?subject2}";
			return "";
		}
		else //factual search
		{
			if(titles==0 && promos==0 && matches==0 && properties==0)
			{
				return "SELECT DISTINCT * WHERE { ?z wo:hasName \""+subject +"\" . ?z ?x ?y}";
			}
			String sparql= "SELECT ?subject ?object WHERE {?subject rdf:label \""+buildSubject(t) +"\"@en";
			if(properties>=1)
			{
				t=new StringTokenizer(q);
				String prop;
				do
				{
					prop=t.nextToken();
				}while(getLabelCount("property",prop)==0);
				System.out.println("Prop: " +prop );
				sparql+= ". ?subject wo:"+ PROPERTIES.get(prop) +" ?object}";
			}
			if(titles>=1)
			{
				sparql+=". ?subject wo:wonTitleReign ?reign. ?title wo:hasTitleReign ?reign. ?title wo:hasName ?object}";
			}
			if(promos>=1)
			{
				sparql+=".  { ?object wo:hasLoser ?subject} UNION {?match wo:hasWinner ?subject}. ?card wo:hasMatch ?match. ?event wo:hasCard ?card. {?object wo:hasEvent ?event} UNION {?object wo:hasShow ?event}}";
			} //?x rdfs:type ?type.
			//?type rdf:subClassOf* wo:Show
			if(matches>=1)
			{
				sparql+= ". { ?object wo:hasLoser ?subject} UNION {  ?object wo:hasWinner ?subject}}";
			}
			return sparql;
			
		}
	}
//	public String getTerm()
//	{
//		
//	}
}
