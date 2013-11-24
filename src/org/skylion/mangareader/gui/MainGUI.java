package org.skylion.mangareader.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.skylion.mangareader.mangaengine.MangaEngine;
import org.skylion.mangareader.mangaengine.MangaHereAPI;
import org.skylion.mangareader.util.*;

/**
 * 
 * A GUI designed for Janga
 * @author Skylion
 *
 */
public class MainGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6351329658980316434L;

	private Container pane;//Place holder

	/**
	 * User Interface Buttons
	 */
	private JButton next;
	private JButton previous;

	private JPanel toolbar;
	private JComboBox<String> chapterSel;
	private JComboBox<String> pageSel;
	/**
	 * User commandline
	 */
	private JTextField mangaSelect;
	private JLabel page; //That page currently displayed


	/**
	 * Used to create and store Global Keystroke
	 */
	private HashMap<KeyStroke, Action> actionMap = new HashMap<KeyStroke, Action>();

	/**
	 * The Engine used to fetch content from the Manga Website. In this case MangaHere
	 */
	private MangaEngine mangaEngine;

	public MainGUI(){
		try {
			mangaEngine = new MangaHereAPI();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		initGUI();
	}

	private void initGUI(){
		setTitle("Janga Manga Reader");
		setBackground(Color.BLACK);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		pane = getContentPane();
		pane.setLayout(new BorderLayout());
		pane.setBackground(Color.BLACK);

		previous = new JButton("Previous Page");
		previous.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loadPage(mangaEngine.getPreviousPage());
			}
		});
		
		next = new JButton("Next Page");
		next.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt){
				loadPage(mangaEngine.getNextPage());
			}
		});

		mangaSelect = new JTextField("Type your manga into here");
		mangaSelect.setEditable(true);
		mangaSelect.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loadPage(mangaEngine.getMangaURL(mangaSelect.getText()));
				refreshLists();
			}
		});



		//Wraps 
		new AutoSuggestor(mangaSelect, this, mangaEngine.getMangaList() , Color.WHITE.brighter(), Color.BLUE, Color.RED, 0.75f);			
		//AutoCompleteDecorator.decorate(mangaSelect, mangaEngine.getMangaList(), false);


		//Gets the Initial Image
		try {
			page = new JLabel(new StretchIconHQ((Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader()
					.getResource("org/skylion/mangareader/resource/WelcomeScreen.png")))));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		page.setPreferredSize(getEffectiveScreenSize());

		//Sets up the Page
		toolbar = new JPanel();
		chapterSel = generateComboBox("Chapter: ", mangaEngine.getChapterList().length);
		chapterSel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				int index = chapterSel.getSelectedIndex();
				try {
					loadPage(mangaEngine.loadImg(mangaEngine.getChapterList()[index]));
					refreshLists();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});		
		pageSel = generateComboBox("Page: ", mangaEngine.getPageList().length);
		pageSel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				int index = pageSel.getSelectedIndex();
				try {
					loadPage(mangaEngine.loadImg(mangaEngine.getPageList()[index]));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.X_AXIS));
		toolbar.add(mangaSelect);
		toolbar.add(chapterSel);
		toolbar.add(pageSel);
		
		pane.add(toolbar, BorderLayout.NORTH);
		pane.add(next, BorderLayout.EAST);
		pane.add(previous, BorderLayout.WEST);
		pane.add(page, BorderLayout.CENTER);
		//pane.add(mangaSelect,BorderLayout.NORTH);
		initKeyboard();
		
		next.setSize(previous.getSize());
	}

	private void initKeyboard(){
		Action nextPageAction = new AbstractAction(){
			/**
			 * AutoGenerated UID
			 */
			private static final long serialVersionUID = -3381019543157339629L;

			public void actionPerformed(ActionEvent e) {
				loadPage(mangaEngine.getNextPage());
			}
		};

		Action previousPageAction = new AbstractAction(){
			/**
			 * AutoGenerated UID
			 */
			private static final long serialVersionUID = 1148536792558547221L;

			public void actionPerformed(ActionEvent e) {
				loadPage(mangaEngine.getPreviousPage());
			}
		};

		//Loads Keyboard Commands
		actionMap.put(KeyStroke.getKeyStroke("released UP"), nextPageAction);
		actionMap.put(KeyStroke.getKeyStroke("released DOWN"), previousPageAction);
		actionMap.put(KeyStroke.getKeyStroke("released PAGE_UP"), nextPageAction);
		actionMap.put(KeyStroke.getKeyStroke("released PAGE_DOWN"), previousPageAction);
		
		//Overrides the KeyboardFocusManager to allow Global key commands.
		KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kfm.addKeyEventDispatcher( new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				KeyStroke keyStroke = KeyStroke.getKeyStrokeForEvent(e);
				//System.out.println(keyStroke);
				if ( actionMap.containsKey(keyStroke) ) {
					final Action a = actionMap.get(keyStroke);
					final ActionEvent ae = new ActionEvent(e.getSource(), e.getID(), null );
					SwingUtilities.invokeLater( new Runnable() {
						@Override
						public void run() {
							a.actionPerformed(ae);
						}
					} ); 
					return true;
				}
				return false;
			}
		});
	}


	/**
	 * Loads the page of the Manga specified by the URL
	 * @param newURL
	 */
	private void loadPage(String URL){
		try {
			loadPage(mangaEngine.loadImg(URL));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			page.setIcon(new StretchIconHQ((Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader()
					.getResource("org/skylion/mangareader/resource/LicenseError.png")))));
			Toolkit.getDefaultToolkit().beep();
			return;
		}
	}

	private void loadPage(StretchIconHQ image){
		page.setIcon(image);
	}

	/**
	 * Gets the efective screen size: the screen size without toolbar
	 * @return
	 */
	private Dimension getEffectiveScreenSize(){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
		int taskBarSize = scnMax.bottom;
		return new Dimension(screenSize.width, screenSize.height-taskBarSize);
	}

	/**
	 * Spawns a combo box
	 * @param prefix the prefix you want to have before the combobox
	 * @param size The size of the ComboBox
	 * @return The ComboBox
	 */
	private JComboBox<String> generateComboBox(String prefix, int size){
		String[] out = new String[size];
		for(int i = 0; i<size; i++){
			out[i] = (prefix + (i+1));
		}
		return new JComboBox<String>(out);
	}
	
	/**
	 * Updates the ComboBoxes for chapters and pages
	 */
	private void refreshLists(){
		String[] chapters = new String[mangaEngine.getChapterList().length];
		for(int i = 0; i<mangaEngine.getChapterList().length; i++){
			chapters[i] = ("Chapter: " + (i+1));
		}
		chapterSel.setModel(new DefaultComboBoxModel<String>(chapters));//Work around to forcefully refresh
		String[] pages = new String[mangaEngine.getPageList().length];
		for(int i = 0; i<mangaEngine.getPageList().length; i++){
			pages[i] = ("Page: " + (i+1));
		}
		pageSel.setModel(new DefaultComboBoxModel<String>(pages));
	}

}
