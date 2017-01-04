package QueryConverter;

import org.apache.jena.query.ResultSet;
import org.json.JSONObject;

public class QueryResult {
	private int dummy;
	private ResultSet result;
	private String sparqlQuery;
	private String answerClass;
	private String searchType;
	private String lastLetter;
	private int currentLetter;
	private int skipLetters;
	
	public QueryResult() {
		this.dummy = 0;
		this.sparqlQuery = "";
		this.answerClass = "";
		this.searchType = "error";
		this.currentLetter = 23;
		this.skipLetters = 0;
		this.lastLetter = "x";
	}

	public int getDummy() {
		return dummy;
	}

	public void setDummy(int dummy) {
		this.dummy = dummy;
	}

	public String getSparqlQuery() {
		return sparqlQuery;
	}

	public void setSparqlQuery(String sparqlQuery) {
		this.sparqlQuery = sparqlQuery;
	}

	public String getAnswerClass() {
		return answerClass;
	}

	public void setAnswerClass(String answerType) {
		this.answerClass = answerType;
	}

	public ResultSet getResult() {
		return result;
	}

	public void setResult(ResultSet result) {
		this.result = result;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getLastLetter() {
		return "" + ((char) (97 + ((currentLetter + skipLetters)%27)));
	}

	public void setLastLetter(String lastLetter) {
		this.lastLetter = lastLetter;
	}

	public int getCurrentLetter() {
		return currentLetter;
	}

	public int getSkipLetters() {
		return skipLetters;
	}
	
	public void addToCurrentLetter(int toAdd) {
		this.currentLetter += toAdd;
	}
	
	public void addToSkipLetters(int toAdd) {
		this.currentLetter += toAdd;
	}
	
	public void resetSkipLetters() {
		this.skipLetters = 0;
	}
	
	public void addToSparql(String toAdd) {
		this.sparqlQuery += toAdd;
	}
	
	
	
}
