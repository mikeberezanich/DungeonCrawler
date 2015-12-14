package game;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GLContext;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.sun.java.swing.plaf.windows.resources.windows;

class JTextFieldLimit extends PlainDocument {
	  private int limit;
	  JTextFieldLimit(int limit) {
	    super();
	    this.limit = limit;
	  }

	  JTextFieldLimit(int limit, boolean upper) {
	    super();
	    this.limit = limit;
	  }

	  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
	    if (str == null)
	      return;

	    if ((getLength() + str.length()) <= limit) {
	      super.insertString(offset, str, attr);
	    }
	  }
	}

public class GameOver extends JPanel implements ActionListener {
	private String name = "Game Over!";
	private JFrame menuScreen;
	private JButton playButton;
	private JButton scoreButton;
	private JButton quitButton;
	private JButton submitButton;
	private Container container;
	private JTextField enterInitials;
	private JLabel background;
	private Statement stmt;
	public GameOver(){
		
		Font font = new Font("TimesRoman", Font.BOLD, 48);
		enterInitials = new JTextField();
		enterInitials.setBounds(272,150,96,60);
		enterInitials.setBackground(Color.red);
		enterInitials.setForeground(Color.WHITE);
		enterInitials.setFont(font);
		enterInitials.setOpaque(false);
		enterInitials.setBorder(BorderFactory.createEmptyBorder());
		enterInitials.setDocument(new JTextFieldLimit(3));
		
		
		
	    
		
		ImageIcon newGame = new ImageIcon("src\\Assets\\Buttons\\NG1.png");
		ImageIcon newGameRollover = new ImageIcon("src\\Assets\\Buttons\\NG2.png");
		ImageIcon highScores = new ImageIcon("src\\Assets\\Buttons\\HS1.png");
		ImageIcon highScoresRollover = new ImageIcon("src\\Assets\\Buttons\\HS2.png");
		ImageIcon quitGame = new ImageIcon("src\\Assets\\Buttons\\Quit1.png");
		ImageIcon quitGameRollover = new ImageIcon("src\\Assets\\Buttons\\Quit2.png");
		ImageIcon submitInitials = new ImageIcon("src\\Assets\\Buttons\\SUBMIT1.png");
		ImageIcon submitInitialsRollover = new ImageIcon("src\\Assets\\Buttons\\SUBMIT2.png");
		
		menuScreen = new JFrame(name);
		menuScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuScreen.setResizable(false);
		
		playButton = new JButton(newGame);
		playButton.setRolloverEnabled(true);
		playButton.setRolloverIcon(newGameRollover);
		playButton.setBounds(90, 280, 200, 70);
		playButton.setBorderPainted(false);
		playButton.setToolTipText("Start a New Game");
		playButton.addActionListener(this);
		playButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		scoreButton = new JButton(highScores);
		scoreButton.setRolloverEnabled(true);
		scoreButton.setRolloverIcon(highScoresRollover);
		scoreButton.setBounds(350, 280, 200, 70);
		scoreButton.addActionListener(this);
		scoreButton.setToolTipText("View High Scores");
		scoreButton.setBorderPainted(false);
		scoreButton.setForeground(new Color(132,0,6));
		scoreButton.setContentAreaFilled(false);
		scoreButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		quitButton = new JButton(quitGame);
		quitButton.setRolloverEnabled(true);
		quitButton.setRolloverIcon(quitGameRollover);
		quitButton.setBounds(220, 390, 200, 70);
		quitButton.addActionListener(this);
		quitButton.setToolTipText("Return to Main Menu");
		quitButton.setBorderPainted(false);
		quitButton.setContentAreaFilled(false);
		quitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		submitButton = new JButton(submitInitials);
		submitButton.setRolloverEnabled(true);
		submitButton.setRolloverIcon(submitInitialsRollover);
		submitButton.setBounds(272, 220, 96, 40);
		submitButton.addActionListener(this);
		submitButton.setToolTipText("Submit Initials");
		submitButton.setBorderPainted(false);
		submitButton.setContentAreaFilled(false);
		submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		container = menuScreen.getContentPane();
		this.setLayout(null);
		this.add(playButton);
		this.add(scoreButton);
		this.add(quitButton);
		this.add(submitButton);
		this.add(enterInitials);
		
		container.add(this);
		this.setPreferredSize(new Dimension (640,480));
		
		setLayout(new BorderLayout());
		background=new JLabel(new ImageIcon("src\\Assets\\gameover.png"));
		add(background);
		background.setLayout(new FlowLayout());
		
		menuScreen.pack();
		menuScreen.setVisible(true);
	}
	public void publishScores (String initials) throws Exception{
		Class.forName("com.mysql.jdbc.Driver").newInstance();

	    Connection m_Connection = DriverManager.getConnection(
	        "jdbc:mysql://hoolahanphotography.com:3306/dungeoncrawler", "gameuser", "gamepass");
	    PreparedStatement pstmt = m_Connection.prepareStatement("INSERT INTO highscores (name, score) VALUES (?, ?)");
	    
	    pstmt.setString(1, initials);
	    pstmt.setInt(2, Game.score);
	    pstmt.executeUpdate();
	    System.out.println("Entry successfully added to database!");
	    
	}
	public void actionPerformed(ActionEvent event) {
		Object a = event.getSource();
		if(a == playButton){
			// this opens new game full screen size??
			new LwjglApplication(new Game(), "Wrath of the Ascendant", 1024, 768);
			this.menuScreen.dispose();
			
		}
		else if(a == scoreButton){
			try {
				new HighScore();
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.menuScreen.dispose();
		}
		else if(a == quitButton){
			this.menuScreen.dispose();
		}
		else if(a == submitButton){
			String initials;
			initials = enterInitials.getText();
			 try {
				publishScores(initials);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			 
			 remove(background);
			 remove(submitButton);
			 remove(enterInitials);
			 setLayout(new BorderLayout());
			 background = new JLabel(new ImageIcon("src\\Assets\\gameover2.png"));
			 add(background);
			 background.setLayout(new FlowLayout());
			 menuScreen.getContentPane().validate();
		     menuScreen.getContentPane().repaint();
			 
			
		}
	}
}

