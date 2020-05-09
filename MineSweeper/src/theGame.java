/*
 * Diego Virtue 
 * Minesweeper with GUI
 * AP Comp Sci Lefebvre
 */

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.GridBagLayout;
import javax.swing.JToggleButton;
import java.awt.GridBagConstraints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JButton;

//import all necessary classes

public class theGame {

	private JFrame frmMiinesweeper;
	private int w;
	private int mines;
	private String name;
	private boolean first = true;
	private JToggleButton [] [] board;
	private boolean  notDone = true;
	private String difficulty;
	//-2 = opened, -1 = bomb, 0 = not open, 1-8 = number of bombs
	private int [] [] blocks;
	private int [] [] flags;
	private long time;
	private JLabel lblNewJgoodiesLabel;
	//initalize all necessary variables including frame and board for blocks, flags, and buttons

	
	//Precondition: menu item was clicked
	ActionListener listen = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
	
			System.exit(0);
		}
		
	};
	//Postcondition: Program stops running
	
	//Precondition: menu item is clicked
	ActionListener l = new ActionListener() {
		
		public void actionPerformed(ActionEvent e) {
	
			
			notDone = true;
			time = System.currentTimeMillis();
			clock();
			
			for (int i = 0; i<w; i++) {
				
				for (int j = 0; j<w; j++) {
				
					board[i][j].setEnabled(true);
					board[i][j].setText("");
					board[i][j].setBackground(Color.WHITE);
					board[i][j].setForeground(Color.WHITE);
					board[i][j].setSelected(true);
					
				}
			
			}// resets all buttons
			
			blocks = new int [w] [w];
			first = true;
			flags= new int [w] [w];
			notDone = true;
			//resets variables
		}
		
	}; //Postcondition: starts a new game and all variables are reset
		
	//Precondition: Any button is clicked
	MouseAdapter rightClick = new MouseAdapter() {
		
		public void mousePressed(MouseEvent e) {

			if (SwingUtilities.isRightMouseButton(e)) {
				int i = -1;
				int j = -1;
				boolean search = true;
				
					while (search && i<(w-1)) {
						i++;
						j=-1;
						while (search &&j<(w-1)) {
							j++;
							if (e.getSource()==board[i] [j]) {
								search = false;
							}
						}
					} // searches for where button is clicked
					
					if (!search && (board[i][j].isEnabled()) && notDone) {
						
						if (flags[i][j] != -3) {
						board[i] [j].setText("F");
						board[i] [j].setBackground(Color.ORANGE);
						board[i] [j].setForeground(Color.ORANGE);
						flags[i][j] = -3;
						board[i][j].setEnabled(false);
						} // if no flag down already put flag
						else {
							if (notDone && ((board[i][j].isEnabled())) || flags[i][j] == -3) {
							board[i] [j].setText("");
							flags[i][j] = 0;
							board[i][j].setEnabled(true);
							}//if flag is down then do nothing (code inside never executes)
						}
					} // makes sure it is possible to play
					
			} // right click sensed, then put down a flag
			
			else {
				int i = -1;
				int j = -1;
				boolean search = true;
				
					while (search && i<(w-1)) {
						i++;
						j=-1;
						while (search &&j<(w-1)) {
							j++;
							if (e.getSource()==board[i] [j]) {
								if (first) {
									spawn(i,j);
									first = false;
								}  // if first click then initalize bombs
								search = false;
							}
						}
					} // find where clicked
					
					if (!search && notDone) {
						if (blocks[i] [j] == -1) {
							stopPlaying();
						} // if bomb clicked stop playing
						
						else {
						
							if (notDone) {
							open(i,j);
							board[i][j].setEnabled(false);
							board[i] [j].setBackground(Color.WHITE);
							board[i] [j].setForeground(Color.WHITE);
							
							}// open box, set correct color, and make sure button cannot be re-clicked
							
						} // execute if not a bomb clicked
					}
					
					dub();
					resise();
					//check for win and if board re sized
			
			} //if button clicked then execute
		}
				
	};//Postcondition: button board senses if it is clicked or right clicked, and acts accordingly
	
	/**
	 * Launch the application.
	 */
	
	//Precondition: None
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					theGame window = new theGame();
					window.frmMiinesweeper.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//PostCondition: application created

	//Precondition: None
	public theGame() {
		
		time = System.currentTimeMillis();
		initialize();
		clock();
		//start time and create JFrame
		
		for (int i = 0; i<w; i++) {
			
			for (int j = 0; j<w; j++) {
				
				board [i] [j]= new JToggleButton();
				
				board [i] [j].setSize(frmMiinesweeper.getWidth()/w, frmMiinesweeper.getHeight()/w);
				frmMiinesweeper.getContentPane().add(board[i] [j]);
				board [i] [j].setLocation(j*(frmMiinesweeper.getWidth()/w), i*(frmMiinesweeper.getHeight()/w));
				frmMiinesweeper.getContentPane().add(board[i] [j]);
				board [i] [j].addMouseListener(rightClick);
				board [i] [j].setSelected(true);
				board[i] [j].setBackground(Color.WHITE);
				board[i] [j].setForeground(Color.WHITE);
				board[i] [j].setOpaque(true);
				//set button size, location, add to frame, mouseListener, if selected, and color
			
				
			} 
		}// initalize button board
	
	} //Post condition: clock started, visuals and button board initalized

	//Precondition: constructor runs
	private void initialize() {
		name = JOptionPane.showInputDialog("What is Your Name:");
		difficulty = JOptionPane.showInputDialog("Difficulty: 1,2,3");
		//prompts for size and name (so score at end is more accurate)
		
		frmMiinesweeper = new JFrame();
		frmMiinesweeper.setTitle("Minesweeper");
		//create frame
		
		frmMiinesweeper.getContentPane().addComponentListener(new ComponentAdapter() {
			//overide
			public void componentResized(ComponentEvent e) {
				resise();
		
			}
		}); //everytime board is size changes change button sie
		
		frmMiinesweeper.setBounds(1000, 1000, 1000, 1000);
		frmMiinesweeper.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMiinesweeper.getContentPane().setLayout(new BorderLayout(0, 0));
		//set Frame size
		
		JPanel panel = new JPanel();
		frmMiinesweeper.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(1, 0, 0, 0));
		//panel for buttons to sit on
		
		JMenuBar menuBar = new JMenuBar();
		frmMiinesweeper.setJMenuBar(menuBar);
		
		JMenu mnMinesweeper = new JMenu("Menu");
		menuBar.add(mnMinesweeper);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("New Game");
		mnMinesweeper.add(mntmNewMenuItem);
		mntmNewMenuItem.addActionListener(l);
		
		JMenuItem exit = new JMenuItem("Exit");
		mnMinesweeper.add(exit);
		exit.addActionListener(listen);
		
		lblNewJgoodiesLabel = new JLabel("");
		menuBar.add(lblNewJgoodiesLabel);
		//items  to costumize menu bar

		w = length(difficulty);
		mines = mine(difficulty);
		board = new JToggleButton [w] [w];
		blocks = new int [w] [w];
		flags= new int [w] [w];
		//create board according to difficulty
	
	}//PostCondition: Initialize the contents of the frame
	
	
	//Precondition: difficulty inputed
	public int length (String d) {
		
		switch(d) {
		
		case "1":
			return 8;
		
			
		case "2": 
			return 16;
		
			
		case "3": 
			return 24;
			
		default: 
			return 8;
		
		}
		
	}//PostCondition: board size determined
	
	//Precondition: difficulty inputed
	public int mine (String d) {
		
		switch(d) {
		
		case "1":
		
			return 10;
		
			
		case "2": 
	
			return 40;
		
			
		case "3":
			
			return 99;
			
		default: 
			return 10;
		
		}
		
	}// Post Condition: number of bombs determined f
	
	//Method not uesed but, Precondition: difficulty inputed
	public String difficulty(String d) {
		switch(d) {
		
		case "1":
			return "beginer";
		
			
		case "2": 
			return "intermiediate";
		
			
		case "3": 
			return "expert";
			
		default:
			return "beginer";
		}
	} // Post condition: difficulty determined from prompt
	
	//Precondition: user resizes JFrame
	public void resise () {
		for (int i = 0; i<w; i++) {
			
			for (int j = 0; j<w; j++) {
				
		
				board [i] [j].setSize(frmMiinesweeper.getWidth()/w, frmMiinesweeper.getHeight()/w);
				frmMiinesweeper.getContentPane().add(board[i] [j]);
				board [i] [j].setLocation(j*(frmMiinesweeper.getWidth()/w), i*(frmMiinesweeper.getHeight()/w));
				frmMiinesweeper.getContentPane().add(board[i] [j]);
			}
		}
	} //Postcondition: resize buttons to fit in frame and so all are equal size
	
	//Precondition: first click has already happened
	public void spawn(int x, int y) {
		for (int i =1; i<=mines; i++) {
			int random2 = (int) (Math.random()*(w-1));
			int rando2 = (int) (Math.random()*(w-1));
			
			if(blocks [random2]  [rando2] != -1 && !(random2 ==x && rando2 == y)) {
				blocks [random2]  [rando2] = -1;
			}
			
			else {
				i--;
			}
		}
	} // PostCondition: spawn in bombs, but do not spawn them in at first click at (x,y) or where other bombs are
	
	//Precondition: if game is won or lost
	public void stopPlaying() {
	
		notDone = false;
		
		for (int i = 0; i<w; i++) {
			
			for (int j = 0; j<w; j++) {
			
			
				if (blocks[i] [j] == 0)
				board[i] [j].setEnabled(false);
				if (blocks [i] [j]== -1) {
					
					board[i][j].setText("*Boom*");
					board[i] [j].setEnabled(false);
					board[i] [j].setBackground(Color.RED);
					board[i] [j].setForeground(Color.RED);

					
				}
			}
			
			System.out.println();
		}
	}//Post condition: reveal bombs and set boolean to not allow game to continue
	
	//Precondition: button is clicked
	public int surroundingBombs(int x,int y) {
		
		int counter = 0;
		
		for(int i = x-1; i<=x+1;i++) {
			for(int j = y-1; j<=y+1;j++) {
			
				if (!(i<0 || j<0 || i>w-1 || j>w-1)) {
					if (i!=x || j!=y) {
						if(blocks[i][j] == -1)
						counter++;
					}
				}
			}
		}
		
		return counter;
	} //Post condition: return count of how many bombs are around surrounding button clicked
	
	
	//Precondition: Button is clicked 
	public void open(int i, int j) {
		if(i<0 ||j<0 || i>w-1 || j>w-1) {
			return;
		}//if not at valid location don't do anything
		
		if(blocks[i] [j] == 0) {
		
			int bombs = surroundingBombs(i,j);
			blocks[i] [j] = bombs;
			//check how many bombs in surrounding area
		
			if (bombs != 0) {
				
				board[i][j].setText("" + bombs);
				
			}// do not open any surrounding blocks and set button text to number of surrounding bombs
		
			else {
		
				blocks[i] [j] = -2;
					for(int x = i-1; x<=i+1;x++) {
						for(int y = j-1; y<=j+1;y++) {
							if(!(x<0 || y<0 || x>w-1 || y>w-1)) {
								if(i!=x || j!=y) {
									open(x,y); 
								}
							}
						}
					} 
			} //open surrounding buttons that are not bombs
		
		}//if valid location execute

		board[i] [j].setEnabled(false);
		
	}//Post condition:sets button to number of surrounding bombs, then calls itself if no bomb at button, and opens all surrounding blocks without bombs
	
	//Precondition: if all blocks opened and have not lost
	public void dub() {
	
		if (notDone) {
			
			boolean win = true;
	
			for (int i = 0; i<w; i++) {
				for (int j = 0; j<w; j++) {
					if (blocks [i] [j] == 0) {
						win = false;
					}
				}
		
			} //check to see if all buttons are opened 
		
			if (win) {
		
				time = (System.currentTimeMillis() - time)/1000;
				//get score
				
				/*
				try {
				
					switch(difficulty) {
				
					case "1":
					
						FileWriter b = new FileWriter("Leaderboard1.txt");
						PrintWriter begin = new  PrintWriter(b);
						begin.println("Beginer Leaderboard");
						begin.println(name + " " + time);
						begin.close();
						break;
						
					case "2":
					
						FileWriter n = new FileWriter("Leaderboard2.txt");
						PrintWriter novice = new  PrintWriter(n);
						novice.println("Intermediate Leaderboard");
						novice.println(name + " " + time);
						novice.close();
						break;
						
					case "3":
					
						FileWriter fw = new FileWriter("Leaderboard3.txt");
						PrintWriter pw = new  PrintWriter(fw);
						pw.println("Expert Leaderboard");
						pw.println(name + " " + time);
						pw.close();
						break;
					
					default:
					
						FileWriter be = new FileWriter("Leaderboard1.txt");
						PrintWriter begins = new  PrintWriter(be);
						begins.println(name + " " + time);
						begins.close();
						break;
			
					}
	
				}
			
				catch(IOException e) {
					
					System.out.println("Error");
				}	
				*/
				//that was me begining to experiment with leaderboard and files
				
				javax.swing.JOptionPane.showMessageDialog(null, "The Winner is " + name);
				javax.swing.JOptionPane.showMessageDialog(null, "It took " + name + " " + time+ " seconds");
				//output score
				
				stopPlaying();
				notDone = false;
				//make sure game stops responding until new game wanted
			
			}//execute if all buttons are opened
	
		}//check to see if player has not lost
		
	}//Postcondition: if win, stop time, call stopPlaying method, and output score
	
	//Precondition: constructor runs, and timer continues to run if game not over
	public void clock() {
		
		Thread clock = new Thread() {
			
			public void run() {
			
		
				try {
					for(;;) {
						if (notDone) {
						lblNewJgoodiesLabel.setText("                               Time: " + ((System.currentTimeMillis() -time)/1000));
						sleep(1000);
						}
					} // for loop that infinitely set JLabel to current run time
				}
				
				catch(InterruptedException e) {
				e.printStackTrace();	
				}
				
				
			}//not gonna lie this method inside of a method is wack, your video said to do this but it seems extra
			
		};//initalize new thread to run timer on
		
		clock.start();
		//start timer
		
	}//Postcondition: running timer created in label

}//end class