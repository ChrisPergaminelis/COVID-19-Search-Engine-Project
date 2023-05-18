package indexsearcher;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

import utils.Logger;
import utils.Page;

public class _IndexSearcher {
	
	private Logger logger;
	private IndexReader reader;
	private IndexSearcher searcher;
	private String query;
	private String searchMode;
	
	public _IndexSearcher(IndexReader reader, String query, String searchMode, Logger logger) {
		this.logger = logger;
		this.reader = reader;
		this.query = query;
		this.searchMode = searchMode;
	}
	
	public ArrayList<Page> searchIndex(boolean sortedByField, String field, int maxResults, boolean ascending) {
		try {
			searcher = new IndexSearcher(reader); // create a new index searcher with the provided reader
			QueryParser queryParser = new QueryParser(searchMode, new StandardAnalyzer());
			Query queryObject = queryParser.parse(query); // make new query with the desired characteristics
			
			TopDocs docs = null;
			if (sortedByField) {
				Sort sort = new Sort(new SortField(field, SortField.Type.STRING, ascending));
				docs = searcher.search(queryObject, maxResults, sort); // get the top documents sorted by ...
			} else {
				docs = searcher.search(queryObject, maxResults); // get the top documents sorted by relevance "default"
			}
			
			ScoreDoc[] hits = docs.scoreDocs;
			logger.logSystem("Thread _IndexSearcher Found " + hits.length + " hits."); // log operation to logfile
        	ArrayList<Page> pages = new ArrayList<Page>(); // creating a list for pages
    		try {
    			for (int i = 0; i < hits.length; i++) {
    				pages.add( new Page(searcher.doc(hits[i].doc), 
    						hits[i].score, 
    						this.highlight(searcher.doc(hits[i].doc).get("contents")))); //add new page
        	}
    		} catch (IOException e) {
    			logger.logSystem(e.getMessage());
    		}
    		return pages;
		} catch (ParseException | IOException e) {
			logger.logSystem(e.getMessage());
			return null;
		}
	}
	
	public String highlight(String text) {
        try {
            StandardAnalyzer analyzer = new StandardAnalyzer(); 
        	QueryParser queryParser = new QueryParser("contents", new StandardAnalyzer());
        	Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter(), new QueryScorer(queryParser.parse(query)));
        	
        	StringBuilder stringBuilder = new StringBuilder();
        	String[] highlights = highlighter.getBestFragments(analyzer, "contents", text, 4);
        	for (int i = 0; i < highlights.length; i++) {
        		stringBuilder.append(highlights[i] + "...<br>");
        	}
            return stringBuilder.toString();
        } catch (IOException | InvalidTokenOffsetsException | ParseException e) {
            logger.logSystem(e.getMessage());
        }
        return null;
	}
	
	public String getQuery() {
		return query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
