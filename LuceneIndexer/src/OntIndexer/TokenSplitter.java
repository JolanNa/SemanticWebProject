package OntIndexer;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

public class TokenSplitter {
	
	public int tokenCounter(TokenStream t)
	{
		int count=0;
		try {
			while(t.incrementToken())
				count++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	
	
	public TokenStream setTokenStream(String query)
	{
		 Analyzer analyzer = new StandardAnalyzer(); // or any other analyzer
	     TokenStream names = analyzer.tokenStream("name", new StringReader(query));
	     TokenStream promos = analyzer.tokenStream("promotion", new StringReader(query));
	     TokenStream titles = analyzer.tokenStream("title", new StringReader(query));
	     return names;
	}
}
