package indexwriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import utils.Logger;

public class _IndexWriter {

	private File indexFolder;
	private File dataFolder;
	private Logger logger;
	
	public _IndexWriter(File indexFolder, File dataFolder, Logger logger) {
		this.logger = logger;
		this.dataFolder = dataFolder;
		this.indexFolder = indexFolder;
	}

	public boolean isEmpty() {
		File[] files = indexFolder.listFiles();
		return files.length == 0;
	}
	
	public int cleanIndexDir() {
		File[] files = indexFolder.listFiles();
		for (int i = 0; i < files.length; i++) {
			files[i].delete();
		}
		return 0;
	}

	public void writeIndex() {
		Directory directory;
		IndexWriterConfig writerConfig;
		IndexWriter writer;
		try {
			directory = FSDirectory.open(indexFolder.toPath()); // opening fsdirectory
			writerConfig = new IndexWriterConfig(new StandardAnalyzer()); // use indexwriter with standard analyzer
			writer = new IndexWriter(directory, writerConfig);
			File[] files = dataFolder.listFiles();
			for(int i = 0; i < files.length; i++) {
				File file = files[i];
				logger.logSystem("Thread _IndexWriter indexing file:... " + file.getCanonicalPath()); // log operation to logfile
				Document doc = new Document(); //create a new logical document
				
				FileReader fileReader = new FileReader(file, StandardCharsets.UTF_8);
				BufferedReader bufferedFileReader = new BufferedReader(fileReader);
				
				doc.add(new StringField("filename", file.getName(), Field.Store.YES)); // add filename (not analyzed)
				doc.add(new StringField("path", file.getCanonicalPath(), Field.Store.YES)); // add filepath (not analyzed)
				doc.add(new TextField("title", bufferedFileReader.readLine(), Field.Store.YES)); // add article title
				doc.add(new TextField("author", bufferedFileReader.readLine(), Field.Store.YES)); // add article author
				String date = bufferedFileReader.readLine();
				doc.add(new StringField("date", date, Field.Store.YES)); // get the date (not analyzed)
				doc.add(new SortedDocValuesField("date", new BytesRef(date))); // get the date as a sortable field
				StringBuilder contents = new StringBuilder();
				String line;
				while ( (line = bufferedFileReader.readLine()) != null) {
					contents.append(line);
				}
				doc.add(new TextField("contents", contents.toString(), Field.Store.YES)); // get article main text
				writer.addDocument(doc);
				bufferedFileReader.close();
				fileReader.close();
			}
			logger.logSystem("Thread _IndexWriter done Indexing");
			writer.close();
			directory.close();
		} catch (IOException e) {
			logger.logSystem(e.getMessage());
		}
	}
}
