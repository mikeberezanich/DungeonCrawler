package game;



import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import javax.swing.JFrame;

public class TitleScreen extends JPanel implements ActionListener {

	private String name = "Wrath of the Ascendant";
	private Container container;
	private JFrame menuScreen;
	private JButton startButton;
	public TitleScreen(){
		menuScreen = new JFrame(name);
		ImageIcon button = new ImageIcon("src\\assets\\Button.png");
		ImageIcon buttonPressed = new ImageIcon("src\\assets\\buttonRollover.png");
		
		startButton = new JButton(button);
		startButton.setRolloverEnabled(true);
		startButton.setRolloverIcon(buttonPressed);
		startButton.setBounds(220, 300, 200, 70);
		startButton.setBorderPainted(false);
		startButton.setToolTipText("Play " + name);
		startButton.addActionListener(this);
		startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		menuScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		menuScreen.setResizable(false);
		container = menuScreen.getContentPane();
		this.setLayout(null);
		this.add(startButton);
		container.add(this);
		this.setPreferredSize(new Dimension (640,480));
		setLayout(new BorderLayout());
		JLabel background=new JLabel(new ImageIcon("src\\assets\\bg.png"));
		add(background);
		background.setLayout(new FlowLayout());
		menuScreen.pack();
		menuScreen.setVisible(true);
	}
	
	public static void main(String[] args) {
		new TitleScreen();

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object a = event.getSource();
		if(a == startButton){
			new LwjglApplication(new Game(), "Wrath of the Ascendant", 1024, 768);
			this.menuScreen.dispose();
		}
		
	}

}

