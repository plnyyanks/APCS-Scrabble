/* Copyright (C) 2012 Phil Lopreiato, Justin Yost
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * @author Phil Lopreiato
 * @author Justin Yost
 * @version 1.0
 */

package core.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import core.tile;
import core.game;

public class GUI implements AdjustmentListener{

	protected JFrame mainFrame;
	private java.awt.Container contentPane;
	protected JLayeredPane layeredPane;

	protected static game gameRef;
	private playerConfig pc;
	protected scoreGUI sg;
	protected rackGUI rg;
	protected boardGUI bg;

	protected Dimension screenSize;

	private int numPlayers;
	private boolean resize;

	//one of these variables need to be static as of now so that playerconfig's inherited method can access it's parent's variables
	
	public GUI() {
		resize = false;
		screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

		mainFrame = new JFrame("Scrabble");
		mainFrame.setLayout(new BorderLayout());
		
		layeredPane = new JLayeredPane();
		mainFrame.add(layeredPane,BorderLayout.CENTER);
		contentPane = mainFrame.getContentPane();
		layeredPane.setOpaque(false);
		mainFrame.setLayout(new java.awt.BorderLayout());
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.add(layeredPane,BorderLayout.CENTER);
		mainFrame.addComponentListener(new resizeListener());
		numPlayers = 0;
	}

	/**
	 * Initializes the GUI and enables the user to select the number of players
	 */
	public void gameInit() {
		clear();
		repaint();
		layeredPane.setPreferredSize(new Dimension(500,200));
		pc = new playerConfig(gameRef, this);
		pc.addComponents(layeredPane);
		
		show(); //show the main content panel
		repaint();
		gameRef.loadDict();
	}
	
	/**
	 * Give the GUI a reference to the game
	 * 
	 * This allows the GUI to notify the game of events such as placing a tile or submitting a word
	 * 
	 * @param in	Input reference to the game object
	 */
	public void setGameRef(game in)
	{
		gameRef = in;
	}

	/**
	 * Tells the game to complete initialization and sets the number of players
	 * 
	 * @param playerInfo	the number of players playing this game
	 */
	protected void startGame(String[][] playerInfo, int skynet, int timeout)
	{
		gameRef.start(playerInfo, skynet, timeout);
	}

	public void setNumPlayers(int numPlayers)
	{
		this.numPlayers = numPlayers;
	}

	public boolean numPlayersSet() {
		numPlayers = pc.getNumPlayers();
		return numPlayers != 0;
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public void loadGameDisplay() {

		clear();
		pc = null; // deallocate config stuff
		layeredPane.setPreferredSize(new Dimension((int)(screenSize.getWidth()),(int)screenSize.getHeight()-60));
		resize = true;
		
		bg = new boardGUI();
		bg.addComponents(layeredPane);
		bg.reset();
		
		sg = new scoreGUI(numPlayers);
		sg.addComponents(layeredPane);

		rg = new rackGUI();
		rg.addComponents(layeredPane);
		
	}

	public void updateScore(int player, int score) {
		if (sg != null)
			sg.updateScore(player, score);
		repaint();
	}
	
	public void updateScore(int player, String name, int score, String last) {
		if (sg != null)
			sg.updateScore(player, name, score, last);
		repaint();
	}
	
	public void updateBagTiles(int tiles)
	{
		if (sg != null)
			sg.updateBagTiles(tiles);
		repaint();
	}
	
	public void updateCurrentTurnScore(int score) {
		sg.updateCurrentTurnScore(score);
	}

	public void setTurn(int player, boolean human)
	{
		if (sg != null) {
			sg.setTurn(player);
			sg.greyButtons(human);
		}
		repaint();
	}

	public void updateRack(int pos /* 0-6 */, BufferedImage tile) {
		if (rg != null)
			rg.updateRack(pos, tile);
		repaint();
	}

	public void updateRack(BufferedImage[] tiles) {
		if (rg != null)
			rg.updateRack(tiles);
		repaint();
	}

	public void updateRack(tile[] tiles) {
		if (rg != null)
			rg.updateRack(tiles);
		repaint();
	}
	
	public void addVirtualBoard(tile[][] virtualBoard) {
		bg.addVirtualBoard(virtualBoard);
		updateCurrentTurnScore(gameRef.scoreVB());
	}
	
	public void resetVB() {
		bg.submitVB();
		sg.clearCheckWord();
	}
	
	public void hide(int x, int y) {
		bg.hide(x, y);
	}

	public void show() {
		mainFrame.pack();
		mainFrame.setVisible(true);
		mainFrame.repaint();
	}

	public void repaint() {
		layeredPane.repaint();
		contentPane.repaint();
		if(bg != null) { bg.repaint(); bg.getContainer().validate(); }
		if(sg != null) sg.repaint();
		if(rg != null) { rg.repaint(); rg.getContainer().validate(); }
		mainFrame.pack();
		mainFrame.repaint();
		if(sg != null) sg.updateCurrentTurnScore(gameRef.scoreVB());
	}

	public void clear() {
		layeredPane.removeAll();
	}
	
	public void hide() {
		mainFrame.setVisible(false);
	}
	
	class tileDnD extends MouseMotionAdapter{
		public void mouseDragged(MouseEvent e) {
			Component c = e.getComponent();
			c.setLocation(c.getX()+e.getX(),c.getY()+e.getY());
			repaint();
		}
		public void startDrag() {

		}
	}

	public void waitForTurn() {
		JOptionPane.showMessageDialog(null,
			    "Click \"OK\" to advance to the next turn.",
			    "Waiting...",
			    JOptionPane.PLAIN_MESSAGE);
    }
	
	public void gameOver(int player) {
		JOptionPane.showMessageDialog(null,player == -2?"It's a tie!":
			    "Player "+(player+1)+" has won the game!",
			    "Game Over!",
			    JOptionPane.PLAIN_MESSAGE);
	}
	

	public int gameOver(String message) {
		String[] options = {"New Game", "Rematch", "Quit"};
		return JOptionPane.showOptionDialog(null,
			    message,
			    "Game Over!",
			    JOptionPane.YES_NO_CANCEL_OPTION,
			    JOptionPane.QUESTION_MESSAGE,
			    null,
			    options,
			    options[2]);
    }
	
	 public void adjustmentValueChanged(AdjustmentEvent e) {
         repaint();
     }

	public void updateTimer(int time) {
	   sg.updateTime(time);
    }
	
	class resizeListener implements ComponentListener{
		@Override
	    public void componentHidden(ComponentEvent arg0) {
		    // TODO Auto-generated method stub
		    
	    }

		@Override
	    public void componentMoved(ComponentEvent arg0) {
		    // TODO Auto-generated method stub
		    
	    }

		@Override
	    public void componentResized(ComponentEvent arg0) {
		   if(resize)
			mainFrame.setPreferredSize(new Dimension(mainFrame.getWidth(),mainFrame.getHeight()));
	    }

		@Override
	    public void componentShown(ComponentEvent arg0) {
		    // TODO Auto-generated method stub
		    
	    }

	}

}