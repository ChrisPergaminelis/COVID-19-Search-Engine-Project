package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
	
	private File consoleLog;
	private FileWriter fileWriter;
	private BufferedWriter bufferedWriter;
	private PrintWriter printWriter;
	
	public Logger(String consoleLogPath) {
		this.consoleLog = new File(consoleLogPath);
		if (!consoleLog.exists()) {
			try {
				consoleLog.createNewFile(); // create new logfile
			} catch (IOException e1) {
			}	
		}
		try {
			fileWriter = new FileWriter(this.consoleLog);
			bufferedWriter = new BufferedWriter(fileWriter);
			printWriter = new PrintWriter(bufferedWriter);
		} catch (IOException e2) {
		}
	}
	
	public void logSystem(String log) {
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"); // get the current date and time in a simple format
		String formattedDate = dateTime.format(dateTimeFormat);
		printWriter.println(String.format("%s %s", formattedDate, log)); // add the log to the logfile
	}

	public void terminate() { // standard closing function
		printWriter.flush();
		printWriter.close();
		try {
			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {
		}		
	}
}
