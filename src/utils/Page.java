package utils;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.lucene.document.Document;

public class Page {

	private float score;
	private Document document;
	private JEditorPane description;
	private JTextPane displayableDocument;
	
	public Page(Document document, float score, String description) {
		this.document = document;
		this.score = score;
		
		displayableDocument = new JTextPane();
		displayableDocument.setEditable(false);
		displayableDocument.setFont(new Font("Arial",Font.PLAIN,14));
		displayableDocument.setBackground(Color.DARK_GRAY); // set page font and color
		
		this.description = new JEditorPane(); // generate the page preview description
		this.description.setEditable(false);
		this.description.setContentType("text/html");
		this.description.setText(
				document.get("title") + "<br>" +
				"<small>"+description+"</small>");
	}

	public void loadPage() throws BadLocationException { // load the page the user requested for display
		StyledDocument styledDocument = displayableDocument.getStyledDocument();
		Style darkThemeText = displayableDocument.addStyle("Dark Theme", null);
		StyleConstants.setForeground(darkThemeText, Color.WHITE);
		styledDocument.insertString(styledDocument.getLength(),
				document.get("title") + "\n" +
				document.get("author") + "\n" +
				document.get("date") + "\n" +
				document.get("contents"), 
				darkThemeText);
	}
	
	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}
	
	public JEditorPane getDescription() {
		return description;
	}
	
	public void setDescription(JEditorPane description) {
		this.description = description;
	}
	
	public JTextPane getDisplayableDocument() {
		return displayableDocument;
	}
	
	public void setDisplayableDocument(JTextPane displayableDocument) {
		this.displayableDocument = displayableDocument;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}
	
	public String toString() {
		return description.getText();
	}
}
