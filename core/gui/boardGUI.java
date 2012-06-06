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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import core.tile;

public class boardGUI extends GUI implements guiSegment{
	
	JLayeredPane boardContainer;
	JLabel boardLabel;
	BufferedImage boardBase;
	private JLabel boardLetters[][];
	private ArrayList<JLabel> virtualBoardLetters;
	private int n;
	
	public boardGUI() {
		//Create and set up the window.
		
		//load board image
		try {                
			boardBase = ImageIO.read(this.getClass().getResource("mainBoard.jpg"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		boardContainer = new JLayeredPane();
		boardContainer.setLayout(null);
		boardContainer.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
		boardContainer.setOpaque(false);
		boardLabel = new JLabel(new ImageIcon( boardBase )); //draw board background
		boardLabel.setPreferredSize(new java.awt.Dimension(652,691));
		boardLabel.setLocation(0,110);
		boardLetters = new JLabel[15][15];
		virtualBoardLetters = new ArrayList<JLabel>();
		n = 0;
	}
	
	public void addComponents(javax.swing.JLayeredPane pane) {
		boardContainer.add(boardLabel, 0);
		boardContainer.setSize(boardContainer.getPreferredSize());
		boardContainer.setLocation(0,120);
		
		pane.add(boardContainer, 0);
	}
	
	public void addTile(tile t, int x, int y) {
		BufferedImage icon = null;
		JLabel label = null;
		if(t != null)
			icon = t.paint(false);
		else {
			try {
				icon = ImageIO.read(this.getClass().getResource("gui/singleTile.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		label = new JLabel(new ImageIcon(icon));
		label.setPreferredSize(new java.awt.Dimension(40,43));
		label.setLocation((x*43)+10,(y*46)+126);
		boardContainer.add(label,4);
		boardLetters[x][y] = label;
	}
	
	public void addVirtualBoard(tile[][] virtualBoard) {
		virtualBoardLetters = new ArrayList<JLabel>();
		JLabel label;
		for(int x=0;x<15;x++) {
			for(int y=0;y<15;y++) {
				if(virtualBoard[x][y] != null) {
					label = new JLabel(new ImageIcon(virtualBoard[x][y].paint(false)));
					label.setLocation((x*42)+8,(y*45)+125);
					label.setPreferredSize(new java.awt.Dimension(40,43));
					label.setOpaque(false);
					boardContainer.add(label,4);
					boardContainer.moveToFront(label);
				}
			}
		}
		repaint();
	}

    public JLayeredPane getContainer() {
	   return boardContainer;
    }
}
