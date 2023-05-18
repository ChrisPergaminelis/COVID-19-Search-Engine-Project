package utils;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class SearchHistory {

	private File searchHistory;
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;
	private PrintWriter printWriter;
	private Scanner scanner;
	private Logger logger;
	private JTextPane displayableHistory;
	
	public SearchHistory(String searchHistoryPath, Logger logger) {
		this.logger = logger;
		this.searchHistory = new File(searchHistoryPath);
		if (!searchHistory.exists()) {
			try {
				logger.logSystem("Thread SearchHistory searchHistory file not found");
				searchHistory.createNewFile();
				logger.logSystem("Thread SearchHistory created new searchHistory file");
			} catch (IOException e) {
				logger.logSystem(e.getMessage());
			}
		}
		try {
			fileWriter = new FileWriter(searchHistory, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			printWriter = new PrintWriter(bufferedWriter);
		} catch (IOException e) {
			logger.logSystem(e.getMessage());
		}
	}
	
	public void terminate() {
		printWriter.flush();
		printWriter.close();
		try {
			fileWriter.close();
		} catch (IOException e) {
			logger.logSystem(e.getMessage()); // log error to logfile
		}
	}
	
	public String writeToSearchHistory(String log) {
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"); // get date and time in a simple format
		String formattedDate = dateTime.format(dateTimeFormat);
		printWriter.println(String.format("%s %s", formattedDate, log));
		printWriter.flush();
		logger.logSystem("Thread SearchHistory writeToSearchHistory");
		return log;
	}
	
	public String readFromSearchHistory() {
		try {
			scanner = new Scanner(searchHistory);
			StringBuilder logs = new StringBuilder();
			while(scanner.hasNextLine()) {
				logs.append(scanner.nextLine() + "\n");
			}
			logger.logSystem("Thread SearchHistory readFromSearchHistory");
			scanner.close();	
			return logs.toString();
		} catch (FileNotFoundException e) {
			logger.logSystem(e.getMessage());
		}
		return null;
	}
	
	public void loadHistory() throws BadLocationException {
		displayableHistory = new JTextPane();
		displayableHistory.setEditable(false);
		displayableHistory.setFont(new Font("Arial",Font.PLAIN,14));
		displayableHistory.setBackground(Color.DARK_GRAY);
		StyledDocument historyPage = displayableHistory.getStyledDocument();
		Style darkThemeText = displayableHistory.addStyle("Dark Theme", null);
		StyleConstants.setForeground(darkThemeText, Color.WHITE);
		historyPage.insertString(historyPage.getLength(), readFromSearchHistory(), darkThemeText); // load history as user requested it for display
	}
	
	public JTextPane getDisplayableHistory() {
		return this.displayableHistory;
	}

	public void deleteSearchHistroy() { // delete history
		try {
			new PrintWriter(searchHistory).close(); // trunctuate file
		} catch (FileNotFoundException e) {
			logger.logSystem(e.getMessage()); // log error to logfile
		}
	}
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
}
