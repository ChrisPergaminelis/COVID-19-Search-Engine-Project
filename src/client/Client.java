package client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;

import mainengine.MainEngine;
import utils.Page;
import utils.SearchHistory;

public class Client implements ActionListener, MouseListener, KeyListener, WindowListener{

	private static JFrame frame;
	private static JPanel panel;
	
	private static JScrollPane jsp;
	private static JTextField userText;
	
	private static Container pageListContainer;
	private static CardLayout cardLayout;
	private static JPanel pagePanel;
	private static JScrollPane pageJsp;
	
	private static JButton searchButton;
	private static JButton historyButton;
	private static JButton rightArrowButton;
	private static JButton leftArrowButton;
	private static JButton sortButton;	
	
	private static MainEngine mainengine;

	private static String logoPath = "logo.png";
	
	public static void main(String[] args) {
		int frameWidth = 800;
		int frameHeight = 800;
		
		frame = new JFrame("NOODLE");
		frame.setSize(frameWidth, frameHeight);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new Client());
		frame.setResizable(false);
		
		panel = new JPanel();
		frame.add(panel);
		panel.setLayout(null);
		panel.setBackground(Color.DARK_GRAY);
		panel.addKeyListener(new Client());
		
		JLabel label = new JLabel("NOODLE");
		label.setForeground(Color.WHITE);
		label.setBounds(370, 100, 80, 25);
		panel.add(label);
		
		ImageIcon logo = new ImageIcon(Client.class.getClassLoader().getResource(logoPath));
		JLabel logo_label = new JLabel(logo);
		panel.add(logo_label);
		logo_label.setBounds(350, 10, 100, 100);
		
		userText = new JTextField();
		userText.addKeyListener(new Client());
		userText.setBounds(300, 130, 185, 25);
		panel.add(userText);
		
		searchButton = new JButton("Search");
		searchButton.setBounds(355, 160, 80, 30);
		searchButton.setBackground(Color.GRAY);
		searchButton.setForeground(Color.WHITE);
		searchButton.addActionListener(new Client());
		
		sortButton = new JButton("Sort Results");
		sortButton.setBounds(290, 600, 200, 50);
		sortButton.setBackground(Color.GRAY);
		sortButton.setForeground(Color.WHITE);
		sortButton.addActionListener(new Client());
		
		historyButton = new JButton("History");
		historyButton.setBounds(650, 30, 100, 30);
		historyButton.setBounds(650, 30, 100, 30);
		historyButton.setBackground(Color.GRAY);
		historyButton.setForeground(Color.WHITE);
		historyButton.addActionListener(new Client());
		
		rightArrowButton = new JButton("Next Page");
		rightArrowButton.setBounds(490, 600, 200, 50);
		rightArrowButton.setBackground(Color.GRAY);
		rightArrowButton.setForeground(Color.WHITE);
		rightArrowButton.addActionListener(new Client());
		
		leftArrowButton = new JButton("Previous Page");
		leftArrowButton.setBounds(90, 600, 200, 50);
		leftArrowButton.setBackground(Color.GRAY);
		leftArrowButton.setForeground(Color.WHITE);
		leftArrowButton.addActionListener(new Client());
		
		pageListContainer = new Container();
		cardLayout = new CardLayout();
		pageListContainer.setLayout(cardLayout);
		jsp = new JScrollPane(pageListContainer);
		jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		jsp.setBounds(90, 200, 600, 400);
		
		panel.add(sortButton);
		panel.add(searchButton);
		panel.add(historyButton);
		panel.add(rightArrowButton);
		panel.add(leftArrowButton);
		
		panel.add(jsp);		

		frame.setVisible(true);
		panel.setVisible(false);
		mainengine = new MainEngine();
		JFrame popUp = new JFrame();
		popUp.setResizable(false);
		JOptionPane.showMessageDialog(popUp,"Setup finished");
		panel.setVisible(true);
	}
	
	public static int searchIndex() {
		String query = userText.getText();
		JFrame popUp = new JFrame();
		popUp.setResizable(false);
		String[] modes = {"contents", "author", "title"};
		int mode = JOptionPane.showOptionDialog(popUp, null, "Select search mode", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, modes, modes[0]);
		if (mode == JOptionPane.CLOSED_OPTION)
			return 1;
		ArrayList<Page> pages = mainengine.searchIndex(query, modes[mode]);
		modelPagesToList(pages);
		userText.setText(null);
		return 0;
	}
	
	private static void getSortedPagesResult() {
		JFrame popUp = new JFrame();
		popUp.setResizable(false);
		String[] modes = {"Most Relative First", "Most Recent First", "Oldest First"};
		int mode = JOptionPane.showOptionDialog(popUp, null, "Select search mode", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, modes, modes[0]);
		if (mode == JOptionPane.CLOSED_OPTION)
			return;
		else {
			ArrayList<Page> pages = mainengine.sortDocumentsBy(modes[mode]);
			modelPagesToList(pages);
		}	
	}
	
	private static void modelPagesToList(ArrayList<Page> pages) {
		pageListContainer.removeAll();
		for (int i = 0; i < pages.size(); i = i + 10) {
			JList<Page> displayablePageList = new JList<Page>();
			displayablePageList.setFont(new Font("Arial",Font.PLAIN,16));
			DefaultListModel<Page> listModel = new DefaultListModel<Page>();
			int size = Math.min(pages.size(), i+10);
			listModel.addAll(pages.subList(i, size));
			displayablePageList.setModel(listModel);
			displayablePageList.addMouseListener(new Client());
			pageListContainer.add(displayablePageList);
		}
		cardLayout.first(pageListContainer);
	}
	
	private void loadPageFromJlist(JList<Page> jlist, int index) {
		Page page = jlist.getModel().getElementAt(index);
		
		try {
			page.loadPage();
			mainengine.getSearchHistory().writeToSearchHistory(page.getDocument().get("title") + "\t" + page.getDocument().get("path"));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		pagePanel = new JPanel();
		pagePanel.setLayout(null);
		
		pageJsp = new JScrollPane(page.getDisplayableDocument());
		
		pagePanel.setBounds(0, 0, panel.getWidth(), panel.getHeight());	
		pageJsp.setBounds(0, 30, pagePanel.getWidth(), pagePanel.getHeight()-30);
		
		pageJsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		pageJsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		pagePanel.add(pageJsp);
		
		JButton  returnButton = new JButton("Back");
		returnButton.setBackground(Color.GRAY);
		returnButton.setForeground(Color.WHITE);
		returnButton.addActionListener(new Client());
		pagePanel.add(returnButton);
		returnButton.setBounds(0, 0, 70, 30);
		
		frame.add(pagePanel);
		panel.setVisible(false);
	}
	
	public static void returnFromPage() {
		pagePanel.removeAll();
		frame.remove(pagePanel);
		panel.setVisible(true);
	}

	public static void showHistory() {
		pagePanel = new JPanel();
		pagePanel.setLayout(null);		
		try {
			SearchHistory searchHistory = mainengine.getSearchHistory();
			searchHistory.loadHistory();
			searchHistory.getDisplayableHistory();
			pageJsp = new JScrollPane(searchHistory.getDisplayableHistory());
			
			pagePanel.setBounds(0, 0, panel.getWidth(), panel.getHeight());	
			pageJsp.setBounds(0, 30, pagePanel.getWidth(), pagePanel.getHeight()-30);
			
			pageJsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			pageJsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			
			pagePanel.add(pageJsp);
			
			JButton  returnButton = new JButton("Back");
			returnButton.setBackground(Color.GRAY);
			returnButton.setForeground(Color.WHITE);
			returnButton.addActionListener(new Client());
			pagePanel.add(returnButton);
			returnButton.setBounds(0, 0, 70, 30);
			
			JButton deleteHistoryButton = new JButton("Delete");
			deleteHistoryButton.setBackground(Color.GRAY);
			deleteHistoryButton.setForeground(Color.WHITE);
			deleteHistoryButton.addActionListener(new Client());
			pagePanel.add(deleteHistoryButton);
			deleteHistoryButton.setBounds(pageJsp.getWidth()-70, 0, 70, 30);
			
			frame.add(pagePanel);
			panel.setVisible(false);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public static void terminate() {
		mainengine.terminate();
		System.exit(0);
	}
	
	public static boolean isEmpty(JList<Page> jList) {
		for (int i = 0; i < jList.getModel().getSize(); i++) {
			if (jList.getModel().getElementAt(i) != null)
					return false;
		}
		return true;
	}
	
	@SuppressWarnings("all")
	@Override
	public void mouseClicked(MouseEvent e) {
		JList<Page> jlist = (JList<Page>)e.getSource();
        if (e.getButton() == e.BUTTON1 && e.getClickCount() == 2) {
        	int index = jlist.locationToIndex(e.getPoint());
        	loadPageFromJlist(jlist, index);
        }
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@SuppressWarnings("all")
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton abstractButton = (JButton) e.getSource();
		switch (abstractButton.getLabel()) {
		case "Search":
			searchIndex();
			break;
		case "History":
			showHistory();
			break;
		case "Back":
			returnFromPage();
			break;
		case "Previous Page":
			cardLayout.previous(this.pageListContainer);;
			break;
		case "Next Page":
			cardLayout.next(this.pageListContainer);
			break;
		case "Sort Results":
			getSortedPagesResult();
			break;
		case "Delete":
			mainengine.deleteHistory();
			try {
				mainengine.getSearchHistory().getDisplayableHistory().getStyledDocument().remove(0, 
						mainengine.getSearchHistory().getDisplayableHistory().getStyledDocument().getLength());
			} catch (BadLocationException e1) {	}
			break;
		default:
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == '\n') {
			searchIndex();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		terminate();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}

}
