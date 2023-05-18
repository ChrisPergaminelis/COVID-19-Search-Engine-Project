package indexreader;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import utils.Logger;

public class _IndexReader {
	
	 private File indexFolder;
	 private IndexReader reader;
	 private Logger logger;
	 
	 public _IndexReader(File indexFolder, Logger logger) {
		 this.logger = logger;
		 this.indexFolder = indexFolder;
	 }

	 public int readIndex() {
		 try {
			logger.logSystem("Thread _IndexReader reading index directory...");
			Directory directory = FSDirectory.open(indexFolder.toPath()); // open FSDirectory
			reader = DirectoryReader.open(directory); // create a reader from the indexFolder
			directory.close(); // closing the directory
			logger.logSystem("Thread _IndexReader reading done");
			return 0;
		} catch ( IOException e) {
			logger.logSystem(e.getMessage());
			return 1;
		}
	}
	 
	 public IndexReader getIndexReader() {
		 return reader;
	 }

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
