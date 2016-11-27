/////////////////////////////////////////////////
//   TODO
//
// trim() the tokenized values // maybe done with the trim in create_uri?
// don't add promotions outside of initial adding
// sometimes there are still teams added to wrestlers e.g. the beatdown clan
//
// - labels
// - make all dates real dates not strings
// - makeNamedIndividual() {
//
/////////////////////////////////////////////////

package scraper;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntDocumentManager;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import progressSaver.ProgressSaver;
import res.Matcher;
import res.MatcherEvents;
import res.MatcherPromotions;
import res.MatcherTitles;
import res.MatcherWorkers;

import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


/**
 * The WestlingScraper scrapes the data from the website cagematch.net
 * 
 * The fetched data is stored in an sql db, which is specified in the DB.java class
 *
 * The Scraper doesn't have to fetch all the data at once, but can pick up at a point until where the db is filled. (very basic)
 */
public class WrestlingScraper {								/*WATCH OUT FOR  VVV */
	/*CHANGE THE PATH JOLAN!!!*/ private static String ontfile= "/Users/Jolan/git/SemanticWebProject/javaProject/wrestling.rdf";
	private static HtmlToPlainText htmlPlain; // to convert html to plain text which can be added to the db
	//http://www.semanticweb.org/vasco/ontologies/2016/9/wrestling
    private static final String NS= "http://www.semanticweb.org/vasco/ontologies/2016/9/untitled-ontology-5";
    private static OntModel m = ModelFactory.createOntologyModel();
    private static OntDocumentManager dm = m.getDocumentManager();
    private static int entityExtracted;


    private static final ArrayList<String> FULL_LINKS_PROMOTIONS = new ArrayList<String>() {{
		add("http://www.cagematch.net/?id=8&nr=1");
		add("http://www.cagematch.net/?id=8&nr=5");
		add("http://www.cagematch.net/?id=8&nr=4");
		add("http://www.cagematch.net/?id=8&nr=12");
		add("http://www.cagematch.net/?id=8&nr=18");
		add("http://www.cagematch.net/?id=8&nr=545");
		add("http://www.cagematch.net/?id=8&nr=8");
		add("http://www.cagematch.net/?id=8&nr=7");
		add("http://www.cagematch.net/?id=8&nr=6");
		add("http://www.cagematch.net/?id=8&nr=96");
//		add("http://www.cagematch.net/?id=8&nr=35");
//		add("http://www.cagematch.net/?id=8&nr=19");
//		add("http://www.cagematch.net/?id=8&nr=122");
//		add("http://www.cagematch.net/?id=8&nr=78");
//		add("http://www.cagematch.net/?id=8&nr=2");
//		add("http://www.cagematch.net/?id=8&nr=3");
	}};
	private static final ArrayList<String> LINKS_PROMOTIONS = new ArrayList<String>() {{
			add("?id=8&nr=1");
			add("?id=8&nr=5");
			add("?id=8&nr=4");
			add("?id=8&nr=12");
			add("?id=8&nr=18");
			add("?id=8&nr=545");
			add("?id=8&nr=8");
			add("?id=8&nr=7");
			add("?id=8&nr=6");
			add("?id=8&nr=96");
//			add("?id=8&nr=35");
//			add("?id=8&nr=19");
//			add("?id=8&nr=122");
//			add("?id=8&nr=78");
//			add("?id=8&nr=2");
//			add("?id=8&nr=3");	
		}};
	/* data_specs
	 * A List that specifies the outlines of the data to crawl
	 * elements -> A hash Map with the specifications: [String sql_create_sheet,       #the sql create sheet has the sql command to create the table
	 * 											    	String table_name, 
	 * 													Matcher matcher,
	 * 											    	boolean simple_table,
	 * 													String raw_link,               #link to the site where the links are fetched from
	 * 											    	int nr_of_values, 
	 * 											    	int step_size
	 * 													]
	 */
	
	private static ArrayList<HashMap<String, Object>> data_specs = new ArrayList<HashMap<String, Object>>() {{
		   add(new HashMap<String, Object>() {{
		   	   put("matcher", new MatcherPromotions());
		   	   put("name", "Promotion");
		   	   put("raw_link", "http://www.cagematch.net/?id=8&view=promotions");
		   	   put("nr_elements", new Integer(2));
		   	   put("step_size", new Integer(100));
		   	   put("appendix", "&s=");
		   	   put("appendix_element", ""); // appendix during crawling of single elements
		    }});
//		   add(new HashMap<String, Object>() {{
//		   	   put("matcher", new MatcherEvents());
//		   	   put("name", "Event");
//		   	   put("raw_link", "http://www.cagematch.net/?id=1&view=results");
//		   	   put("nr_elements", new Integer(100));
//		   	   put("step_size", new Integer(100));
//		   	   put("appendix", "&s="); // appendix during link extraction
//		   	   put("appendix_element", ""); // appendix during crawling of single elements
//		   	   put("columnPromotion", 2); // appendix during crawling of single elements
//		   	   put("column_link", 2); // appendix during crawling of single elements
//			}});
//		   add(new HashMap<String, Object>() {{
//		   	   put("matcher", new MatcherStables());
//		   	   put("name", "Stable");
//		   	   put("raw_link", "http://www.cagematch.net/?id=29");
//		   	   put("nr_elements", new Integer(1000));
//		   	   put("step_size", new Integer(100));
//		   	   put("appendix", "&s="); // appendix during link extraction
//		   	   put("appendix_element", ""); // appendix during crawling of single elements
//		   	   put("columnPromotion", 0); // appendix during crawling of single elements
//		   	   put("column_link", 1); // appendix during crawling of single elements
//			}});
		   add(new HashMap<String, Object>() {{
			put("matcher", new MatcherTitles());
			put("name", "Title");
			put("raw_link", "http://www.cagematch.net/?id=5&view=titles");
			put("nr_elements", new Integer(900));
			put("step_size", new Integer(100));
			put("appendix", "&s=");
			put("appendix_element", ""); // appendix during crawling of single elements
		   	put("columnPromotion", 3); // appendix during crawling of single elements
		   	put("column_link", 2); // appendix during crawling of single elements
			}});
//		   add(new HashMap<String, Object>() {{
//				put("matcher", new MatcherTeams());
//				put("name", "TagTeam");
//				put("raw_link", "http://www.cagematch.net/?id=28");
//				put("nr_elements", new Integer(200));
//				put("step_size", new Integer(100));
//				put("appendix", "&s=");
//				put("appendix_element", ""); // appendix during crawling of single elements
//			   	put("columnPromotion", 0); // appendix during crawling of single elements
//		        put("column_link", 1); // appendix during crawling of single elements
//			}});
//		   add(new HashMap<String, Object>() {{
//		   	   put("matcher", new MatcherWorkers());
//		   	   put("name", "Worker");
//		   	   put("raw_link", "http://www.cagematch.net/?id=2&view=workers");
//		   	   put("nr_elements", new Integer(400));
//		   	   put("step_size", new Integer(100));
//		   	   put("appendix", "&s=");
//		   	   put("appendix_element", ""); // appendix during crawling of single elements
//		   	   put("columnPromotion", 6); // appendix during crawling of single elements
//		   	   put("column_link", 2); // appendix during crawling of single elements
//			}});
		   
		}};
		
	public static void main(String[] args) {
		// get a ProgressSaver object with the current progress
        ProgressSaver ps = getProgressSaver();

		dm.addAltEntry( "http://www.semanticweb.org/vasco/ontologies/2016/9/wrestling",
                "file:" + ontfile );
		m.read("http://www.semanticweb.org/vasco/ontologies/2016/9/wrestling");
//		
//		OntClass buddy= m.getOntClass(NS + "#Person");
//		if(buddy == null) {
//			System.out.println("SKAJDS:");
//		}
		
//		String text= "SOMe random name 21.12.2014 - 22.10.2018";
//		Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4}");
//		java.util.regex.Matcher matche = pattern.matcher(text);
//		if(matche.find()) { 
//			System.out.println(matche.group(0));
//		}
//		if(matche.find()) {
//
//			System.out.println(matche.group(0));
//		}
		
		System.out.println("Kintaro Oki & Michiaki Yoshimura (3)".replaceAll("\\([0-9]+\\)", ""));
//		
//		
//		
//		try {
//			TimeUnit.SECONDS.sleep(100);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		for(HashMap<String, Object> specs : data_specs) {
			// Data has to be crawled. But if there already is some, start there!
			//first extract the links, where to crawl
			System.out.println("--- '" + specs.get("name") + " ---");
			List<String> links;
			//loop over the years for events..
			if(specs.get("name") == "Promotion") {
				links = FULL_LINKS_PROMOTIONS; 
			} else if(specs.get("name") == "Event") {
				links = getLinksFromMultiSiteTable((String) specs.get("raw_link"),
						(String) specs.get("appendix"),
                        ps.getProgress((String) specs.get("name")),
						(Integer) specs.get("step_size"),
						(Integer) specs.get("nr_elements"),
						(Integer) specs.get("columnPromotion"),
						(Integer) specs.get("column_link"));
				for(int year = 2016; year >= 2015; year--) {
					System.out.println("Year : " + year);
					links.addAll(getLinksFromMultiSiteTable(specs.get("raw_link") +"&Year=" + Integer.toString(year),
						(String) specs.get("appendix"),
                            ps.getProgress((String) specs.get("name")),
						(Integer) specs.get("step_size"),
						(Integer) specs.get("nr_elements"),
						(Integer) specs.get("columnPromotion"),
						(Integer) specs.get("column_link")));
				}
			} else {
				links = getLinksFromMultiSiteTable((String) specs.get("raw_link"),
						(String) specs.get("appendix"),
                        ps.getProgress((String) specs.get("name")),
						(Integer) specs.get("step_size"),
						(Integer) specs.get("nr_elements"),
						(Integer) specs.get("columnPromotion"),
						(Integer) specs.get("column_link"));
			}
			System.out.println("Retrieved links for '" + specs.get("name") + "'  -> "+links.size()+" links");
			switch(specs.get("name").toString())
			{
			case "Promotion":
				entityExtracted=0;
				break;
			case "Event":
				entityExtracted=1;
				break;
			case "Worker":
				entityExtracted=2;
				break;
			case "Title":
				entityExtracted=3;
				break;
			case "TagTeam":
				entityExtracted=4;
				break;
			case "Stable":
				entityExtracted=5;
				break;
			}
			int j = 0;
			for(String link : links) {
				if(j%100 == 0) {
					System.out.print("Data Extraction: " + ((float )j)/ links.size() + "%   \r");
				}
				j++;
				
//				m.write(System.out,"RDF/XML");
				if(entityExtracted==5) {
					extract_stable(link, (String) specs.get("appendix_element"), (Matcher) specs.get("matcher"));
					continue;
				}
				extractOverviewTableData(link, (String) specs.get("appendix_element"), (String) specs.get("table_name"), (Matcher) specs.get("matcher"));
				
			}
			
			/////// Save steps. So if something goes wrong, we can pick up here
			saveOntology();
			///////
		}
		
		
		/**
		 * Write the model to file
		 */
		saveOntology();

	}
	
	/**
	 * This method extracts all the links to the "sites/url's" to the single elements. Since all
	 * the tables are build similar this method can be used for all the link extraction
	 * 
	 * @param link : the basic link
	 * @param appendix : the appendix for following sites/url's
	 * @param stepSize : the step size of the table
	 * @return a list with links to all the elements of the table (e.g. Workers, Promotions etc.)
	 */
	public static List<String> getLinksFromMultiSiteTable(String link, String appendix,int start, int stepSize, int numberOfElements, int columnPromotion, int columnLink) {
		List<String> links = new ArrayList<String>();
		Document document = null;
		//after the first batch a appendix + step size is needed to connect to the link
		for(int i=start;i <= numberOfElements; i += stepSize) {
			System.out.print("Link Extraction : " + ((float )i)/numberOfElements + "% \r");
			try {
				document = Jsoup.connect(link+appendix+Integer.toString(i)).timeout(20 * 1000).get();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			Elements rows = document.getElementsByClass("TRow");
			for(Element row : rows) {
				// if columnPromution is 0 we don't check for promotion and add all links
				if (columnPromotion != 0) {
					//if it is not 0 we check the promotion given in the table if we want to include it.
					// or if there is no value in the promotion field. in that case we also don't want to add the link
					
					if((row.child(columnPromotion).select("a[href]").size() == 0 || !LINKS_PROMOTIONS.contains(row.child(columnPromotion).child(0).attr("href")))) {
						if (null == m.getIndividual(create_uri(row.child(columnLink).child(0).text(), row.child(columnLink).child(0).attr("href")))) {
							continue;
						}
					}
				}
				////// TODO : we might also want to include links where we already have a event, match etc...
//				if(null != m.getIndividual(create_uri(row.child(columnLink).child(0).text(), row.child(columnLink).child(0).attr("href")))) {
//					
//				}
				
				int nrElementsInField = row.child(columnLink).select("a[href]").size();
				if(nrElementsInField >= 2){
					links.add("http://www.cagematch.net/" + row.child(columnLink).child(nrElementsInField - 1).attr("href"));
				} else {
					links.add("http://www.cagematch.net/" + row.child(columnLink).child(0).attr("href"));
				}
			}
		}
		return links;
	}
	
	
	/**
	 * Extract the data from a simple table in the overview of Elements
	 * 
	 * @param link : link to the site with the overview
	 * @throws SQLException if a problem with the sql connection arises
	 */
	public static void extractOverviewTableData(String link, String appendix, String table, Matcher matcher) {
		Document document = null;
		String field_name;
		String value;
		Element valueElement;
		try {
			document = Jsoup.connect(link + appendix).timeout(20*1000).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String name = document.select("h1.TextHeader").first().text();	
		Elements rows = document.getElementsByClass("InformationBoxRow");	
		Resource promo=null;
		Individual rassler=null;
		Individual team=null;
		Individual title=null;
        Resource ind = null; // a dummy individual we always use when creating new Instances for the Ontology
		
		if(name.charAt(0)=='#')
			return;
		//because seriously...
		
//		String name;
		Individual event = null;
		boolean restart = true;
		
		for(Element row : rows) {
			field_name = matcher.match(row.child(0).text());
			if (field_name != null) {
				Elements links_for_id = row.child(1).select("a[href]");
				valueElement = row.child(1);
				value = StringEscapeUtils.escapeHtml4(row.child(1).text()).trim();
				StringTokenizer tk;
				String token;
				if(entityExtracted==0)
				
				{
					if(field_name.equals("current_name"))
					{
						promo = makeIndividual(name, link, "Promotion");
						promo.addProperty(m.getProperty(NS + "#hasName"), value);
					}
					else
					{
						switch(field_name)
						{
						case "current_abbrev":
							promo.addProperty(m.getProperty(NS + "#hasAbbreviation"),value);
							break;
						case "status":
							promo.addProperty(m.getProperty(NS + "#hasStatus"),value);
							break;
						case "location":
							promo.addProperty(m.getProperty(NS + "#hasLocation"),value);
							break;
						case "active_time":
							promo.addProperty(m.getProperty(NS + "#hasActiveTime"),value);
							break;
						case "owners":
							String[] ownersRaw = value.split("\\)");
							for(String or: ownersRaw) {
								String[] ownersClean = or.split("\\(")[0].split("&|amp;|&amp;|,");
								String[] dates = or.split("\\(")[1].split("-");
                                ind = makeIndividual(Arrays.toString(dates) + Arrays.toString(ownersClean), links_for_id, "Ownership");
								for(String oc: ownersClean) {
									Resource owner = makeIndividual(oc, links_for_id, "Owner");
									owner.addProperty(m.getProperty(NS+"#hasName"), oc);
									ind.addProperty(m.getProperty(NS+"#hasOwner"), owner);
								}
								if(dates.length < 2) { // only one date given.  therfore owned the promotion only in this year
									ind.addProperty(m.getProperty(NS+"#beginningOwnership"), create_date_literal(dates[0].trim(),m));
									ind.addProperty(m.getProperty(NS+"#endOwnership"), create_date_literal(dates[0].trim(),m));
								} else {
									ind.addProperty(m.getProperty(NS+"#beginningOwnership"), create_date_literal(dates[0].trim(),m));
									ind.addProperty(m.getProperty(NS+"#endOwnership"), create_date_literal(dates[1].trim(),m));
								}
								promo.addProperty(m.getProperty(NS+"#hasOwnership"), ind);
							}
							break;
						case "popular_events":
							tk= new StringTokenizer(value,",");
							while (tk.hasMoreTokens())
							{
								token = tk.nextToken();
								ind = makeIndividual(token, links_for_id, "Event");
								ind.addProperty(m.getProperty(NS + "#hasName"),token);
								promo.addProperty(m.getProperty(NS + "#hasEvent"), ind);
							}
							break;
						case "tv_shows":
							tk= new StringTokenizer(value,",");
							while (tk.hasMoreTokens())
							{
								token=tk.nextToken().trim();
//								Individual show=m.createIndividual(create_uri(token, links_for_id), m.getOntClass(NS +"#Show"));
								ind = makeIndividual(token, links_for_id, "Show");
								ind.addProperty(m.getProperty(NS + "#hasName"),token);
								promo.addProperty(m.getProperty(NS + "#hasShow"), ind);
							}
							break;
						default:
							break;
						}

					}
				}
				
				else if(entityExtracted==1)
					
				{

					if(field_name.equals("name_of_event"))
					{
//						event = m.createResource(create_uri(name, link), m.getOntClass(NS +"#Event"));
						event = makeIndividual(name, link, "Event");
						event.addProperty(m.getProperty(NS + "#hasName"), name);
						
					}
					else
					{
						switch(field_name)
						{
						case "date":
							Literal l = create_date_literal(value, m);
							event.addProperty(m.getProperty(NS + "#hasDate"), l);
							break;
						case "promotion":
							if(m.getIndividual(create_uri(value, links_for_id)) != null) {
								event.addProperty(m.getProperty(NS + "#hasPromotion"), m.getIndividual(create_uri(value, links_for_id)));
							} 
							break;
						case "type":
						    switch (value) {
                                case "TV-Show":
                                    event.setOntClass(m.getOntClass(NS+"#TV-Show"));
                                    break;
                                case "House Show":
                                    event.setOntClass(m.getOntClass(NS+"#House-Show"));
                                    break;
                                case "Pay Per View":
                                    event.setOntClass(m.getOntClass(NS+"#PayPerView"));
                                    break;
                            }
							break;
						case "location":
							event.addProperty(m.getProperty(NS + "#hasLocation"), value);
                            // TODO add a real place
							break;
						case "arena":
							ind = makeIndividual(value, "", "Venue");
							event.addProperty(m.getProperty(NS + "#hasVenue"), ind);
							break;
						case "attendance":
							String attendanceString = value.replaceAll("\\.|ca|fans|Fans|\\b|[a-z]|[A-Z]|,", "").trim();
							if(!attendanceString.equals(""))
							{
								try {
									int attendance = Integer.parseInt(attendanceString);
									Literal a = m.createTypedLiteral(attendance);
									event.addProperty(m.getProperty(NS+"#hasAttendance"), a);
								} catch (NumberFormatException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							break;
						case "breadcast_type":
							event.addProperty(m.getProperty(NS + "#hasBroadcastType"), value);
							break;
						case "broadcast_date":
						    Literal date = create_date_literal(value, m);
							event.addProperty(m.getProperty(NS + "#hasBroadcastDate"), date);
							break;
						case "network":
							Individual n = makeIndividual(value, "", "TV_Network");
                            n.addProperty(m.getProperty(NS + "#hasName"), value.trim());
							event.addProperty(m.getProperty(NS + "#airedOn"),n);
							break;
						case "commentators":
							String[] commentators = value.split(", |&|amp;");
							for(String c: commentators) {
                                if (c.equals("")) continue; // skip if string empty
								ind = makeIndividual(c.trim(), links_for_id, "Commentator");
								event.addProperty(m.getProperty(NS + "#hasCommentator"),ind);
								ind.addProperty(m.getProperty(NS + "#hasName"), c);
							}
							break;
						default:
							break;
						}
						
					}
				} // else if(entityExtracted = 1)
				else if(entityExtracted==2)
				{
					if(restart==true)
					{
						rassler= makeIndividual(name.split(", ")[0], link, "Wrestler");
//						System.out.println("Rassler Name: " + rassler.toString());
						rassler.addProperty(m.getProperty(NS + "#hasName"), name);
						restart=false;
					}
					else
					{
						switch(field_name)
						{

						case "current_promotion":

							if(m.getIndividual(create_uri(value.trim(), links_for_id))!=null)
							{
								rassler.addProperty(m.getProperty(NS+"#hasPromotion"), m.getIndividual(create_uri(value.trim(), links_for_id)));
							}
							break;
						case "birthplace":
							rassler.addProperty(m.getProperty(NS+"#hasBirthplace"),value);
							break;
						case "age":
							int age = Integer.parseInt(value.replaceAll(" years", ""));
							Literal a = m.createTypedLiteral(age);
							rassler.addProperty(m.getProperty(NS+"#hasAge"),a);
							break;
						case "gender":
							rassler.addProperty(m.getProperty(NS+"#hasGender"),value);
							break;
						case "height":
							int height = Integer.parseInt(value.split("\\(")[1].split("\\)")[0].replaceAll(" cm", ""));
							Literal h = m.createTypedLiteral(height);
							rassler.addProperty(m.getProperty(NS+"#hasHeight"),h);
							break;
						case "weight":
							int weight = Integer.parseInt(value.split("\\(")[1].split("\\)")[0].replaceAll(" kg", ""));
							Literal w = m.createTypedLiteral(weight);
							rassler.addProperty(m.getProperty(NS+"#hasWeight"),w);
							break;
						case "relatives_in_wrestling":
							tk= new StringTokenizer(value,",");
							while(tk.hasMoreTokens())
							{
								token=tk.nextToken();
								if(m.getIndividual(create_uri(token,links_for_id))!=null)
								{
									rassler.addProperty(m.getProperty(NS+"#isRelated"), m.getIndividual(create_uri(token,links_for_id)));
								}
								else
								{
									Individual relative=m.createIndividual(create_uri(token,links_for_id), m.getOntClass(NS +"#Person"));
									relative.addProperty(m.getProperty(NS + "#hasName"),token); //maybe include if links exists: don't add name
									rassler.addProperty(m.getProperty(NS + "#isRelated"),relative);
								}
							}
							break;
						case "background_in_sports":
							rassler.addProperty(m.getProperty(NS+"#hasSportsBackground"), value);
							break;
						case "alter_egos":
							Elements clean_names = valueElement.select("a[href]");
							Individual alterEgo;
							for(Element e: clean_names) {
                                alterEgo = makeIndividual(e.text(), links_for_id, "Wrestler");
								alterEgo.addProperty(m.getProperty(NS+"#hasName"), e.text());
								rassler.addProperty(m.getProperty(NS + "#isAlterEgo"),alterEgo);
								alterEgo.addProperty(m.getProperty(NS + "#isAlterEgo"),rassler);
							}
							break;
							/*case "roles":
							tk= new StringTokenizer(value,",");
							while(tk.hasMoreTokens())
							{
								token=tk.nextToken();
								rassler.addProperty("", );
							}
							break;*/
						case "start_of_career":
							Literal l = create_date_literal(value, m);
							rassler.addProperty(m.getProperty(NS+"#beginningOfCareer"),l);
							break;
						case "wrestling_style":
							tk = new StringTokenizer(value, ",");
							while(tk.hasMoreTokens()) 
							{
								token = tk.nextToken();
								rassler.addProperty(m.getProperty(NS+"#hasStyle"), token.trim());
							}
							break;
						case "trainer":
							Elements clean_trainers = valueElement.select("a[href]");
							Individual trainer;
							for(Element e: clean_trainers) {
                                trainer = makeIndividual(e.text(), links_for_id, "Wrestler");
								trainer.addProperty(m.getProperty(NS+"#hasName"), e.text());
								rassler.addProperty(m.getProperty(NS + "#hasTrainer"),trainer);
							}
							
							break;
						case "nicknames":
							tk= new StringTokenizer(value,",");
							while(tk.hasMoreTokens())
							{
								token=tk.nextToken();
								rassler.addProperty(m.getProperty(NS + "#hasNickname"), value);
							}
							// TODO : maybe add as equal individual as well.. easier for search
							break;
						case "trademark_holds":
							tk= new StringTokenizer(value,",");
							while(tk.hasMoreTokens())
							{
								token=tk.nextToken();
								rassler.addProperty(m.getProperty(NS + "#hasTrademarkHold"), token.trim());
							}
							break;
						case "marital_partner":
							if(m.getIndividual(create_uri(value, links_for_id))!=null)
							{
								rassler.addProperty(m.getProperty(NS+"#hasMaritalPartner"), m.getIndividual(create_uri(value, links_for_id)));
								m.getIndividual(create_uri(value, links_for_id)).addProperty(m.getProperty(NS+"#hasMaritalPartner"), rassler);
							}
							else
							{
								Individual alter=m.createIndividual(create_uri(value, links_for_id), m.getOntClass(NS +"#Person"));
								alter.addProperty(m.getProperty(NS + "#hasName"),value);
								rassler.addProperty(m.getProperty(NS + "#hasMaritalPartner"),alter);
								alter.addProperty(m.getProperty(NS + "#hasMaritalPartner"),rassler);
							}
							break;
						case "obit":
							rassler.addProperty(m.getProperty(NS+"#hasObit"), value);
							break;
						case "cause_of_death":
							rassler.addProperty(m.getProperty(NS+"#hasCauseOfDeath"), value);
							break;
						case "end_of_career":
							Literal eoc = create_date_literal(value, m);
							rassler.addProperty(m.getProperty(NS+"#endOfCareer"), eoc);
							break;
						default:
							break;
						}
					}
				} //else if(entityExtracted == 2)
				else if(entityExtracted==3)
				{
					if(field_name.equals("current_name"))
					{
						//name= NS + "#"+ value;
						title = makeIndividual(name, link, "Title");
						title= m.createIndividual(create_uri(name, link), m.getOntClass(NS +"#Title"));
//						System.out.println(title.toString());
						title.addProperty(m.getProperty(NS + "#hasName"), value);
					}
					else
					{
						switch(field_name)
						{
						case "status":
							if(value.toLowerCase().equals("active"))
								
								title.addProperty(m.getProperty(NS+ "#isActive"), m.createTypedLiteral(true));
							else
								title.addProperty(m.getProperty(NS+ "#isActive"), m.createTypedLiteral(true));
							break;
						case "promotions":
							tk= new StringTokenizer(value,"()");
							while(tk.hasMoreTokens())
							{
								token=tk.nextToken();
								token=token.trim();
								if(m.getIndividual(NS+"#"+token)!=null)
									m.getIndividual(NS + "#" + token).addProperty(m.getProperty(NS + "#hasTitle"), title);
//								else
//								{
//									Individual putmedownplease= m.createIndividual(NS+"#"+token.replaceAll("%","percent"),m.getOntClass(NS+ "#Promotion"));
//									putmedownplease.addProperty(m.getProperty(NS + "#hasName"), token);
//									title.addProperty(m.getProperty(NS + "#hasPromotion"), putmedownplease);
//								}
								tk.nextToken();
							}
							break;
							default: break;
						}
					}
				}
				else if(entityExtracted==4)
				{
					if(restart==true)
					{
						team= m.createIndividual(create_uri(name, link), m.getOntClass(NS +"#Team"));
//						System.out.println("Team Name: " + team.toString());
						team.addProperty(m.getProperty(NS + "#hasName"), name);
						restart=false;
					}
					else
					{
						switch(field_name)
						{
						case "promotions":
							if(m.getIndividual(NS +"#" +value)!=null)
								team.addProperty(m.getProperty(NS + "#hasPromotion"), m.getIndividual(create_uri(value,links_for_id)));
//							else
//							{
//								Individual noob= m.createIndividual(NS + "#"+ value.replaceAll("%","percent"), m.getOntClass(NS +"#Promotion"));
//								noob.addProperty(m.getProperty(NS + "#hasName"), value);
//								team.addProperty(m.getProperty(NS+"#hasPromotion"), noob);
//							}
							break;
						case "years":
							team.addProperty(m.getProperty(NS+"#hasActiveTime"), value);
							break;
						case "holds":
							tk= new StringTokenizer(value,",");
							while(tk.hasMoreTokens())
							{
								token=tk.nextToken();
								team.addProperty(m.getProperty(NS + "#hasTrademarkHold"), value);
							}
							break;
						}
					}
				}
				
				
				
				
			}
		} // for(row : rows) {...
		
		//Now if event extract all the matches and create the according resources
		if (entityExtracted == 1) {
			extract_matches(document, name, link);
//			
		}//if (entityExtracted == 1) {
		
		//Now extract a title if title
		if(entityExtracted==3)
		{	
			Elements holders= document.getElementsByClass("TRow");
			int counter = holders.size();
			for(Element row : holders)
			{
				counter--;
				String[] members=null;
				String text= row.getElementsByClass("TextBold").text();
				Elements links_for_id = row.select("a[herf]");
				text = text.replaceAll("\\[([0-9])+\\]", "");
				text = text.replaceAll("\\([0-9]+\\)", "");
				team = null;
				Individual teamMember = null;
				Individual reign = makeIndividual(name + " - " + counter, "", "TitleTerm");
				//get the dates:
				Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}\\.\\d{4}");
				java.util.regex.Matcher matche = pattern.matcher(row.text());
				Literal l;
				if (matche.find()) { 
					l = create_date_literal(matche.group(0), m);
					reign.addProperty(m.getProperty(NS + "#hasStartingDate"), l);
				}
				if (matche.find()) { 
					l = create_date_literal(matche.group(0), m);
					reign.addProperty(m.getProperty(NS + "#hasEndDate"), l);
				}
				
				String teamName = "";
				if(text.contains("&")) {
					String innerBrackets = text;
					if(text.contains("(")) {
						teamName = text.split(" \\(")[0];
						innerBrackets = text.split("\\(")[1].split("\\)")[0];
					} 
					members = innerBrackets.split(" & |, ");
					
				} 
				else {
					members = text.split(" & |, ");
				}
				
				if(!teamName.equals("")) {
					team = makeIndividual(teamName, links_for_id, "Team");
				}
				if(members!=null) {
					for(String me:members) {
						if(me.equals("")) continue;
						me = me.split("\\(")[0];
						// in case the title was vacant for a time. We want to add a vacancy
						if(me.equals("VAKANT")) {
							reign.setOntClass(m.getOntClass(NS + "#TitleVacancy"));
							title.addProperty(m.getProperty(NS + "#hasVacancy"), reign);
						} else {
							OntClass buddy= m.getOntClass(NS + "#Person");
							if(buddy == null) {
								System.out.println("SKAJDS:");
							}
							reign.setOntClass(m.getOntClass(NS + "#TitleReign"));
							title.addProperty(m.getProperty(NS + "#hasTitleReign"), reign);
							teamMember = makeIndividual(me, links_for_id, "Wrestler");
							teamMember.addProperty(m.getProperty(NS + "#wonTitleReign"), reign);
						}
						if (team != null)
							team.addProperty(m.getProperty(NS + "#hasPerson"), teamMember);
					}
					if(team!=null) {
						team.addProperty(m.getProperty(NS + "#wonTitleReign"), reign);
					}
				}
			}
		}
		// extract team members for team instances
		if(entityExtracted==4)
		{
			Elements members= document.getElementsByClass("Borderless");
			for (Element row: members)
			{
				Elements individuals= row.getElementsByClass("WorkerPictureBox");
				Elements links_for_id = row.select("a[href]");
				for (Element member: individuals)
				{
//					System.out.println("Member: " + member);
					String dude= member.text();
//					System.out.println("Member: " + dude);
					if(m.getIndividual(create_uri(dude, links_for_id))!=null)
					{
						team.addProperty(m.getProperty(NS+"#hasPerson"), m.getIndividual(create_uri(dude, links_for_id)));
					}
					else
					{
						Individual noob= m.createIndividual(create_uri(dude, links_for_id), m.getOntClass(NS +"#Wrestler"));
						noob.addProperty(m.getProperty(NS + "#hasName"), dude);
						team.addProperty(m.getProperty(NS+"#hasPerson"),noob );
					}
				}
			}
		}
	}
	


	
	/**
	 * 
	 * @param document
	 * @param name
	 * @param link
	 */
	private static void extract_matches(Document document, String name, String link) {
		Elements matches = document.select("div.Match");
        Individual card = makeIndividual("Card", link, "Card");
		int count = 1; // to name the matches
		for(Element match : matches) {
            Individual resMatch = makeIndividual(name + count, link, "Match");
			count++;
			if(match.select(".matchResults").text().contains("No Contest")) {continue;} // NO Contest - skip
			if(match.select(".matchResults").text().contains("Time Limit Draw")) {continue;} // NO Contest - skip
			if(match.select(".matchResults").text().contains("Draw")) {continue;} // NO Contest - skip
			if(match.select(".matchResults").text().contains("Double DQ")) {continue;} // NO Contest - skip
			if(match.select(".matchResults").text().contains("Double Count Out")) {continue;} // NO Contest - skip
			if(match.select(".matchResults").text().contains(" vs ")) {continue;} // NO Contest - skip
			if(match.select(".matchResults").text().contains(" vs. ")) {continue;} // NO Contest - skip
			if(match.select(".matchResults").text().contains("Winner:")) {continue;} // NO Contest - skip
			if(match.select(".matchResults").text().contains("Winner")) {continue;} // NO Contest - skip
			//deal with best out of _someNr_ which means romoving everything behind the -
			String matchResults = match.select(".matchResults").html().split(Pattern.quote(" -"))[0];
			matchResults = matchResults.replaceAll("\\[([0-9])+\\]", ""); // [1] remove numbers in brackets
			matchResults = matchResults.replaceAll("\\[([0-9])+:([0-9])+\\]", ""); // [1:1] remove numbers in brackets
			matchResults = matchResults.split(" by ")[0]; //remove all 'by' explanations eg. : Zieggler defeats Peter by DQ
			//regex to extract duration of the fight
			Pattern pattern = Pattern.compile("\\(([0-9])+:([0-9])+\\)");
			java.util.regex.Matcher matche = pattern.matcher(matchResults);
			if (matche.find()) { 
				String mins = matche.group(0).replaceAll("\\(|\\)|[A-Z]|[a-z]", "").split(":")[0]; 
				String secs = matche.group(0).replaceAll("\\(|\\)|[A-Z]|[a-z]", "").split(":")[1]; 
				try {
					Literal l = m.createTypedLiteral(Integer.parseInt(mins) * 60 + Integer.parseInt(secs));
					resMatch.addProperty(m.getProperty(NS + "#hasDuration"), l); 
				} catch (Exception e) {
					//do nothing
				}
			}
			matchResults = matchResults.replaceAll("\\(([0-9])+:([0-9])+\\)", "");
//			System.out.println("{  " + matchResults + "  }");
			
			String matchType = match.select(".MatchType").text();
			resMatch.addProperty(m.getProperty(NS + "#hasType"), matchType);
			
			String[] explode = matchResults.split(Pattern.quote(" defeat "));
			if(explode.length == 1) {
				explode = matchResults.split(Pattern.quote(" defeats "));
			}
			String teamW = explode[0];
			String teamL = explode[1];
			
			//split up the winning Team
			String[] partsTeamW = teamW.split("[\\(\\)]| &amp; |,|w/");
			for(String s : partsTeamW) {
				if(s.equals("") || s.equals(" ") || s.equals("c")) {continue;}
				String gimmick;
				String link_for_id;
				if(!s.contains("<a href")) {
					gimmick = s.trim(); // get rid of leading and trailing spaces in the name
					link_for_id = "";
				} else {
					gimmick = s.split("[\\>\\<]")[2];// get inside of the <a></a> tag
					link_for_id = s.split("[\\\"\\\"]")[1].replaceAll("&amp;", "&"); // get the value of href="value"
				}
				if(s.contains("id=28") || s.contains("id=29")) {
					//is a tag team or stable
//					Resource tagteam = m.createResource(create_uri(gimmick, link_for_id), m.getOntClass(NS + "#Team"));
                    Individual tagteam = makeIndividual(gimmick, link_for_id, "Team");
					resMatch.addProperty(m.getProperty(NS + "#hasWinner"), tagteam);
					tagteam.addProperty(m.getProperty(NS + "#hasName"), gimmick);
				} else {
					//is a wrestler

					Individual wr = makeIndividual(gimmick, link_for_id, "Wrestler");
					resMatch.addProperty(m.getProperty(NS + "#hasWinner"), wr);
					wr.addProperty(m.getProperty(NS + "#hasName"), gimmick);
				}
			}
			//split up the losing team
			String[] partsTeamL = teamL.split("[\\(\\)]| &amp; |,|w/");
			for(String s : partsTeamL) {
				if(s.equals("") || s.equals(" ") || s.equals("c")) {continue;}
				String gimmick;
				String link_for_id;
				if(!s.contains("<a href")) {
					gimmick = s.trim(); // get rid of leading and trailing spaces in the name
					link_for_id = "";
				} else {
					gimmick = s.split("[\\>\\<]")[2];// get inside of the <a></a> tag
					link_for_id = s.split("[\\\"\\\"]")[1].replaceAll("&amp;", "&"); // get the value of href="value"
				}
				if(s.contains("id=28")) {
					//is a tag team
//					Resource tagteam = m.createResource(create_uri(gimmick, link_for_id), m.getOntClass(NS + "#Team"));
                    Individual tagteam = makeIndividual(gimmick, link_for_id, "Team");
					resMatch.addProperty(m.getProperty(NS + "#hasLoser"), tagteam);
                    tagteam.addProperty(m.getProperty(NS + "#hasName"), gimmick);
				} else {
					//is a wrestler
                    Individual wr = makeIndividual(gimmick, link_for_id, "Wrestler");
					resMatch.addProperty(m.getProperty(NS + "#hasLoser"), wr);
                    wr.addProperty(m.getProperty(NS + "#hasName"), gimmick);
				}
			}
			card.addProperty(m.getProperty(NS+"#hasMatch"), resMatch);
		} // for(Element match : matches) {

//		m.createResource(create_uri(name, link)).addProperty(m.getProperty(NS+"#hasCard"), card);
		makeIndividual(name, link, "").addProperty(m.getProperty(NS + "#hasCard"), card);
	} // create_matches

	/**
	 * 
	 * @param link
	 * @param appendix
	 * @param matcher
	 */
	private static void extract_stable(String link, String appendix, Matcher matcher) {
		Document document = null;
		String field_name;
		String value;
		try {
			document = Jsoup.connect(link + appendix).timeout(20*1000).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//check if we use the stable.. if the wrestlers are unkown we don't use it
		Elements wrestlers = document.select(".WorkerPictureBox");
		boolean dontUseStable = true;
		for(Element w: wrestlers) {
			String wrestlerLink = w.select("a[href]").attr("href");
			String wrestlerName = w.select("a[href]").text();
			if(m.getIndividual(create_uri(wrestlerName, wrestlerLink)) != null) {
				dontUseStable = false;
			}
		}
		if(dontUseStable) return;
		//seems like we will add the stable
		String name = document.select("h1.TextHeader").first().text();
		
		Elements rows = document.getElementsByClass("InformationBoxRow");

		Resource stable = m.createResource(create_uri(name, link), m.getOntClass(NS + "#Team")); 
		for(Element w: wrestlers) {
			String wrestlerLink = w.select("a[href]").attr("href");
			String wrestlerName = w.select("a[href]").text();
			Individual wrestler = m.getIndividual(create_uri(wrestlerName, wrestlerLink));
			if(wrestler != null) {
				stable.addProperty(m.getProperty(NS + "#hasPerson"), wrestler);
			}
		}
		for(Element row : rows) {
			field_name = matcher.match(row.child(0).text());
			if (field_name != null) {
				Elements links_for_id = row.child(1).select("a[href]");

				value = StringEscapeUtils.escapeHtml4(row.child(1).text());
				switch(field_name) 
				{
				case "promotion":
//					Resource r = m.createResource(create_uri(value,links_for_id), m.getOntClass(NS+"#Promotion"));
//					stable.addProperty(m.getProperty(NS + "#hasPromotion"),r);
					if(m.getIndividual(create_uri(value, links_for_id)) == null) {
//						stable.addProperty(m.getProperty(NS + "#hasPromotion"), m.getIndividual(create_uri(value, links_for_id)));
					} else {
						stable.addProperty(m.getProperty(NS + "#hasPromotion"), m.getIndividual(create_uri(value, links_for_id)));
					}
					break;
				case "years":
					stable.addProperty(m.getProperty(NS + "#hasActiveTime"), value);
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 *
	 * @param name
	 * @param link
	 * @param ontClass
	 * @return
	 */
	private static Individual makeIndividual(String name, String link, String ontClass) {
        Individual i = m.getIndividual(create_uri(name.trim(), link));
        if(i != null) return i;
        i = m.createIndividual(create_uri(name.trim(), link), m.getOntClass(NS+"#"+ontClass));
//        i.addProperty(RDFS.label, m.createLiteral(name.trim(), "en"));
        return i;
	}
	private static Individual makeIndividual(String name, Element link, String ontClass) {
        Individual i = m.getIndividual(create_uri(name.trim(), link));
        if(i != null) return i;
        i = m.createIndividual(create_uri(name.trim(), link), m.getOntClass(NS+"#"+ontClass));
//        i.addProperty(RDFS.label, m.createLiteral(name.trim(), "en"));
        return i;
	}
	private static Individual makeIndividual(String name, Elements links_for_id, String ontClass) {
        Individual i = m.getIndividual(create_uri(name.trim(), links_for_id));
        if(i != null) return i;
        i = m.createIndividual(create_uri(name.trim(), links_for_id), m.getOntClass(NS+"#"+ontClass));
//        i.addProperty(RDFS.label, m.createLiteral(name.trim(), "en"));
        return i;
	}
	

	private static void saveOntology() {
        OutputStream out = null;
        String savePath = "testen.rdf";
        try {
            out = new FileOutputStream(savePath);
            m.write( out, "Turtle" );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
                System.out.println("Saved Ontology to " + savePath);
            }
            catch (IOException closeException) {
                // ignore
            }
        }
    }

    private static String create_id(Element link) {
        return create_id(link.attr("href"));
    }
    private static String create_id(String link) {
        if(link == "") {
            return "";
        }
        StringTokenizer t = new StringTokenizer(link + "&", "&");
        t.nextToken();
        return t.nextToken();
    }

    /**
     *
     * @param name
     * @param link_list
     * @return
     */
    private static String create_uri(String name, Elements link_list) {
        Element matched_link = null;
        for(Element e: link_list) {
            if(name == e.text()) {
                matched_link = e;
            }
        }
        return create_uri(name, matched_link);
    }
    /**
     *
     * @param name
     * @param link
     * @return
     */
    private static String create_uri(String name, Element link) {
        if(link == null) {return create_uri(name, "");}
        return create_uri(name, link.attr("href"));
    }
    /**
     *
     * @param name
     * @param link
     * @return
     */
    private static String create_uri(String name, String link) {
        name = name.replaceAll("%", "PRCT").replaceAll("#", "NR").replaceAll(" ", "_").replaceAll("\\[|\\]|\\(|\\)", "");
        return NS + "#" + name.trim() + create_id(link);
    }

    /**
     *
     * @param s
     * @param m
     * @return
     */
    private static Literal create_date_literal(String s, Model m) {
        Calendar cal = GregorianCalendar.getInstance();
        boolean parse = true;
        if (s.contains("today")) {
            cal.setTime(new Date());
            return m.createTypedLiteral(cal);
        }
        try {
            SimpleDateFormat sdf = null;
            if (s.matches("\\d\\d.\\d\\d.(19|20)\\d\\d")) {
                sdf = new SimpleDateFormat("dd.MM.yyyy");
            } else if (s.matches("\\d\\d.(19|20)\\d\\d")) {
                sdf = new SimpleDateFormat("MM.yyyy");
            } else if (s.matches("(19|20)\\d\\d")) {
                sdf = new SimpleDateFormat("yyyy");
            } else {
                parse = false;
            }
            if (parse) {
                cal.setTime(sdf.parse(s));
            } else {
                return create_date_literal("today", m);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return m.createTypedLiteral(cal);

    }


    private static ProgressSaver getProgressSaver() {
        //check if a
        ProgressSaver ps = null;
        try {
            FileInputStream fileIn = new FileInputStream("savedProgress.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            ps = (ProgressSaver) in.readObject();
            in.close();
            fileIn.close();
        } catch(ClassNotFoundException|IOException c) {
            System.out.println(" - No saved Progress found: Starting from scratch! - ");
            ps = new ProgressSaver();
            return ps;
        }
        System.out.println(" - Found save Progress - ");
        return ps;

    }

    private static void saveProgressSaver(ProgressSaver ps) {
        try {
            FileOutputStream fileOut = new FileOutputStream("savedProgress.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(ps);
            out.close();
            fileOut.close();
            System.out.printf("Serialized data is saved in /tmp/employee.ser");
        }catch(IOException i) {
            i.printStackTrace();
        }
    }

	
	
}
