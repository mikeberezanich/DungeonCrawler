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
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class HighScore extends JPanel implements ActionListener {
	private JTextArea textScores;
	private JButton playButton;
	private JButton quitButton;
	private Container container;
	private String name = "High Scores";
	private JFrame highScores;
	private JScrollPane scrollPane;
	public HighScore() throws Exception{
		Font fontx = new Font("Serif", Font.BOLD | Font.ITALIC, 24);
		ImageIcon newGame = new ImageIcon("src\\Assets\\Buttons\\NG1.png");
		ImageIcon newGameRollover = new ImageIcon("src\\Assets\\Buttons\\NG2.png");
		ImageIcon quitGame = new ImageIcon("src\\Assets\\Buttons\\Quit1.png");
		ImageIcon quitGameRollover = new ImageIcon("src\\Assets\\Buttons\\Quit2.png");
		highScores = new JFrame(name);
		textScores = new JTextArea ("");
		scrollPane = new JScrollPane(textScores);
		container = highScores.getContentPane();
		
		scrollPane.setBounds(70, 161, 500, 219);
		scrollPane.setOpaque(false);
		textScores.setOpaque(false);
		textScores.setEditable(false);
		textScores.setFont(fontx);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setForeground(Color.white);
		textScores.setForeground(Color.white);
		
		
		playButton = new JButton(newGame);
		playButton.setRolloverEnabled(true);
		playButton.setRolloverIcon(newGameRollover);
		playButton.setBounds(10, 400, 200, 70);
		playButton.setBorderPainted(false);
		playButton.setToolTipText("Start a New Game");
		playButton.addActionListener(this);
		playButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		quitButton = new JButton(quitGame);
		quitButton.setRolloverEnabled(true);
		quitButton.setRolloverIcon(quitGameRollover);
		quitButton.setBounds(430, 400, 200, 70);
		quitButton.addActionListener(this);
		quitButton.setToolTipText("Return to Main Menu");
		quitButton.setBorderPainted(false);
		quitButton.setContentAreaFilled(false);
		quitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		this.setLayout(null);
		this.add(playButton);
		this.add(quitButton);
		this.add(scrollPane);
		container.add(this);
		this.setPreferredSize(new Dimension (640,480));
		setLayout(new BorderLayout());
		JLabel background=new JLabel(new ImageIcon("src\\Assets\\highscores.png"));
		add(background);
		background.setLayout(new FlowLayout());
		highScores.pack();
		highScores.setVisible(true);
		
		Class.forName("com.mysql.jdbc.Driver").newInstance();

	    Connection m_Connection = DriverManager.getConnection(
	        "jdbc:mysql://hoolahanphotography.com:3306/dungeoncrawler", "gameuser", "gamepass");

	    Statement m_Statement = m_Connection.createStatement();
	    String query = "SELECT * FROM highscores";
	    
	    ResultSet m_ResultSet = m_Statement.executeQuery(query);
	    while (m_ResultSet.next()) {
	    	textScores.append("        " + m_ResultSet.getString(1) + "       .................................       " + m_ResultSet.getString(2)+System.lineSeparator());
	    }
	}
  public static void main(String[] args) throws Exception {
    new HighScore();
	 }
  public void actionPerformed(ActionEvent event) {
		Object a = event.getSource();
		if(a == playButton){
			new LwjglApplication(new Game(), "Wrath of the Ascendant", 1024, 768);
			
			this.highScores.dispose();
		}
		else if(a == quitButton){
			this.highScores.dispose();
		}
		
	}
 
}
