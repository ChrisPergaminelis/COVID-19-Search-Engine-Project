package mainengine;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import indexreader._IndexReader;
import indexsearcher._IndexSearcher;
import indexwriter._IndexWriter;
import utils.Dictionary;
import utils.Logger;
import utils.Page;
import utils.SearchHistory;

public class MainEngine {
	
	private SearchHistory history;
	private Logger logger;
	private Dictionary dictionary;	
	
	private File noodleFolder;
	private File dataFolder;
	private File indexFolder;
	private File dictionaryFolder;
	
	private final String noodleFolderPath = ".//Noodle";
	private final String indexFolderPath = noodleFolderPath + "//indexFolder";
	private final String dataFolderPath = noodleFolderPath + "//dataFolder";
	private final String searchHistoryPath = noodleFolderPath + "//history.txt";
	private final String dictionaryFolderPath = noodleFolderPath + "//dictionary";
	private final String consoleLogPath = noodleFolderPath + "//log.txt";
	
	private _IndexWriter indexWriter;
	private _IndexReader indexReader;
	private _IndexSearcher indexSearcher;
	
	private ArrayList<Page> pages;
	
	public MainEngine() {
		this.noodleFolder = new File(noodleFolderPath);
		if (!noodleFolder.exists()) // if main folder is absent create it
			noodleFolder.mkdir();
		
		this.logger = new Logger(consoleLogPath); // create the logger instance
		this.history = new SearchHistory(searchHistoryPath, logger); // search history recorder 
		
		this.dataFolder = new File(dataFolderPath);
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
			JFrame popUp = new JFrame();
			popUp.setResizable(false);
			JOptionPane.showMessageDialog(popUp,"Data Folder was found empty or missing\n"
					+ "This might be caused on first boot\n"
					+ "The system will now create the Data Folder and then shutdown\n"
					+ "After that copy the text files in the Data Folder and reboot the application");
			System.exit(1);
		}
		this.indexFolder = new File(indexFolderPath);
		this.indexWriter = new _IndexWriter(indexFolder, dataFolder, logger);
		if (!indexFolder.exists()) { // if there is no index folder then create one
			logger.logSystem("Thread MainEngine Index Folder was missing");
			indexFolder.mkdir();
			logger.logSystem("Thread MainEngine created new Index Folder");
			writeIndex();
		}
		
		this.indexReader = new _IndexReader(indexFolder, logger);
		indexReader.readIndex();
		
		this.dictionaryFolder = new File(dictionaryFolderPath);
		if (!dictionaryFolder.exists()) {
			logger.logSystem("Thread MainEngine Dictionary Folder was missing");
			dictionaryFolder.mkdir();
			logger.logSystem("Thread MainEngine created new Dictionary Folder");
		}
		
		this.dictionary = new Dictionary(dictionaryFolder, indexReader.getIndexReader(), logger);
		this.dictionary.initSpellChecker();	// initialize the spell checker
	}
	
	public void terminate() { // terminate each running class from leaves to root
		logger.logSystem("Thread MainEngine shutting down");
		history.terminate();
		dictionary.terminate();
		logger.terminate();
	}
	
	public int writeIndex() {
		if (!indexWriter.isEmpty()) {
			logger.logSystem("Thread MainEngine IndexFile is not found empty");
			JFrame popUp = new JFrame();
			popUp.setResizable(false);
			int res = JOptionPane.showConfirmDialog(popUp,"Index directory is not empty\n"
					+ "Do you wish to overwrite it?");
			if (res == JOptionPane.YES_OPTION) {
				indexWriter.cleanIndexDir();
				logger.logSystem("Thread MainEngine completed IndexFile cleanup");
			}
			else
				return 1;
		}
		indexWriter.writeIndex();
		logger.logSystem("Thread MainEngine completed Index Write process");
		return 0;
	}
	
	public int readIndex() {
		indexReader.readIndex();
		logger.logSystem("Thread MainEngine completed Read Index process");
		return 0;
	}
	
	public ArrayList<Page> searchIndex(String query, String searchMode) {
		this.indexSearcher = new _IndexSearcher(indexReader.getIndexReader(), query, searchMode, logger);	
		pages = indexSearcher.searchIndex(false, null, 50, false);
		history.writeToSearchHistory(String.format("searched term: %s with mode: %s", query, searchMode));
		String suggestedQuery;
		if (pages.size() < 5) {
				suggestedQuery = dictionary.getSuggestion(query);
				if (suggestedQuery == null)
					return pages;
				JFrame popUp = new JFrame();
				popUp.setResizable(false);
				int res = JOptionPane.showConfirmDialog(popUp, String.format("%d documents were found for %s\n"
						+ "Did you mean: %s?", pages.size(), query, suggestedQuery)); // if the returned documents are less than 5 suggest a new similar query
				if (res == JOptionPane.YES_OPTION) {
					this.indexSearcher.setQuery(suggestedQuery.toString());
					pages = indexSearcher.searchIndex(false, null, 50, false);
				}
		}
		return pages;
	}
	
	public ArrayList<Page> sortDocumentsBy(String sortMode) {
		if (indexSearcher == null)
			return null;
		switch (sortMode) { // sort mode
		case "Most Relative First":
			return indexSearcher.searchIndex(false, null, 50, false);
		case "Most Recent First":
			return indexSearcher.searchIndex(true, "date", 50, true);
		case "Oldest First":
			return indexSearcher.searchIndex(true, "date", 50, false);
		default :
			return indexSearcher.searchIndex(false, null, 50, false);
		}
	}
	
	public String getHighlightedDescription(String text) {
		return indexSearcher.highlight(text);
	}
	
	public SearchHistory getSearchHistory() { // return search history object
		return history;
	}

	public void deleteHistory() {
		history.deleteSearchHistroy();		
	}


}
