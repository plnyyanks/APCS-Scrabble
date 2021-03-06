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
 * @author 	Phil Lopreiato
 * @author 	Justin Yost
 * @version 1.0
 */

package core.gui;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import core.tile;

public class rackGUI extends GUI implements guiSegment {

	private JLayeredPane rackContainer;
	private JLabel rackLetters[];
	

	public rackGUI() {
		rackContainer  = new JLayeredPane();
		rackContainer.setPreferredSize(new Dimension(7* 100/*large tile width*/ + 7* 5/*border*/,(int)screenSize.getHeight() /*height of main window*/));
		rackContainer.setLocation(0,0);
		rackContainer.setOpaque(false);
		rackLetters = new JLabel[7];
		rackContainer.setLayout(null);
	}

	public void addComponents(javax.swing.JLayeredPane pane) {
		//draw rack
		BufferedImage blankTile = null;
		try {
			blankTile = ImageIO.read(this.getClass().getResource("singleTile_large.png"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		for(int i = 0;i<7;i++) { //set default tiles
			rackLetters[i] = new JLabel();
			rackLetters[i].setOpaque(false);
			//rackLetters[i].setLocation(40*i+(5*i),rackContainer.getHeight()-pane.getHeight()-110);
			rackLetters[i].setLocation(100*i+(5*i),5);
			rackLetters[i].setSize(100,110);
			rackLetters[i].addMouseMotionListener(new tileDnD());
			rackLetters[i].addMouseListener(new tileClick());
			rackContainer.add(rackLetters[i],JLayeredPane.DEFAULT_LAYER);
			updateRack(i,blankTile);
		}

		//rackContainer.setSize(rackContainer.getPreferredSize());
		rackContainer.setSize(new Dimension(rackContainer.getPreferredSize().width, rackContainer.getPreferredSize().height+100));
		rackContainer.setLocation(0,/*(int) (pane.getPreferredSize().getHeight()-110)*/0);
		pane.add(rackContainer, 0);
	}

	public void updateRack(int pos /*0-6*/,BufferedImage tile) {
		rackLetters[pos].setIcon(new ImageIcon(tile));
	}

	public void updateRack(BufferedImage[] tiles) {
		for(int i=0; i<tiles.length; i++)
			rackLetters[i].setIcon(new ImageIcon(tiles[i]));
	}

	public void updateRack(tile[] tiles) {
		for(int i=0; i<tiles.length; i++)
			if(tiles[i] != null) {
				rackLetters[i].setIcon(new ImageIcon(tiles[i].paint(true)));
				rackLetters[i].setLocation(100*i+(5*i),5);
			}else
			{
				rackLetters[i].setIcon(null);
				rackLetters[i].setPreferredSize(new Dimension(100,110));
			}
	}

	class tileClick implements MouseListener{

		private int rackIndex;

		public tileClick() {
			super();
			rackIndex = -1;
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			JLabel tile = (JLabel) arg0.getSource();
			rackContainer.setLayer(tile,4,0);
			rackIndex = (arg0.getX()+tile.getX())/105;
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			int boardX, boardY;
			boolean placed = false;
			Component c = arg0.getComponent();
			
			if(c.getX() < 5 || c.getX() > 645 || c.getY() < 120 || c.getY() > 803 || ((JLabel)c).getIcon() == null) { //tile not on game board
				if(c.getX() > 0 && c.getX() < 725 && c.getY() > 0 && c.getY() < 110) 
					gameRef.rackSwap(rackIndex, (arg0.getX()+c.getX())/105);
				else
					returnTile(c);
			}else
			{
				boardX = (int) ((c.getX() - 8)/42.5);
				boardY = (int) ((c.getY() - 125)/45.25);
				
				if(boardX > 14)
					boardX = 14;
				if(boardY > 14)
					boardY = 14;

				placed = gameRef.placeTile(rackIndex,boardX,boardY);
				if(placed){
					((JLabel)c).setIcon(null);
					c.setPreferredSize(new Dimension(100,110));
					returnTile(c);
				}
				else
				{
					returnTile(c);
				}
			}
			//x: 5<645
			//y: 120<803
			
			repaint();
		}

		public void returnTile(Component c)
		{
			c.setLocation(3 + (5*rackIndex) + (rackIndex*100), 5); //returns the tile to its starting place
		}
	}


	public JLayeredPane getContainer() {
		return rackContainer;
	}
}