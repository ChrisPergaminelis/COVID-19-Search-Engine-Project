package utils;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.spell.LevenshteinDistance;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.spell.SuggestWord;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Dictionary {
	
	private File dictionaryFolder;
	private Logger logger;
	private IndexReader reader;
	private SpellChecker spellChecker;
	private Directory directory;
	
	public Dictionary(File dictionaryFolder, IndexReader reader, Logger logger) {
		this.logger = logger;
		this.dictionaryFolder = dictionaryFolder;
		this.reader = reader;
	}
	
	private class WordComparator implements Comparator<SuggestWord> { // Custom word comparator term-freq first

		@Override
		public int compare(SuggestWord first, SuggestWord second) {
			try {
				first.freq = (int) reader.totalTermFreq(new Term("contents", first.string)); // get first word frequency
				second.freq = (int) reader.totalTermFreq(new Term("contents", second.string)); // get second word frequency
				if (first.freq != second.freq)
					return Integer.compare(first.freq, second.freq); // compare operation ...
				else if (first.score != second.score)
					return Float.compare(first.score, second.score);
				else
					return first.string.compareTo(second.string);
			} catch (IOException e) {
				if (first.score != second.score)
					return Float.compare(first.score, second.score);
				else
					return first.string.compareTo(second.string);
			}
		}
		
	}
	
	public int initSpellChecker() { // Initialize the dictionary and read it
		try {
			File[] files = dictionaryFolder.listFiles();
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
			directory = FSDirectory.open(dictionaryFolder.toPath());
			spellChecker = new SpellChecker(directory,
					new LevenshteinDistance(), // standard Levenshtein distance
					new WordComparator());
			spellChecker.clearIndex();
			spellChecker.indexDictionary(new LuceneDictionary(reader, "contents"), new IndexWriterConfig(new StandardAnalyzer()), true); // keep reference from contents
			spellChecker.indexDictionary(new LuceneDictionary(reader, "title"), new IndexWriterConfig(new StandardAnalyzer()), true); // title
			spellChecker.indexDictionary(new LuceneDictionary(reader, "author"), new IndexWriterConfig(new StandardAnalyzer()), true); // and author
			// as one uniform dictionary
		} catch (IOException e) {
			logger.logSystem(e.getMessage()); // log error to logfile
		}
		return 0;
	}
	
	public String getSuggestion(String query) { // get a similar query suggested by the system
		String[] queryList = query.split(" ");
		StringBuilder suggestedQuery = new StringBuilder();
		try {
			for (String word: queryList) {
				String[] suggestions;
				suggestions = spellChecker.suggestSimilar(word, 150); // get 150 similar words
				if (suggestions.length == 0)
					continue;
				else
					suggestedQuery.append(suggestions[0]).append(" "); // and pick the most relevant and frequent one
			}
			return suggestedQuery.toString();
		} catch (IOException e) {
			logger.logSystem(e.getMessage()); // log error to logfile
		}
		return null;
	}

	public void terminate() { // standard terminate method
		try {
			directory.close();
		} catch (IOException e) {
			logger.logSystem(e.getMessage());
		}
		try {
			spellChecker.close();
		} catch (IOException e) {
			logger.logSystem(e.getMessage());
		}
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
