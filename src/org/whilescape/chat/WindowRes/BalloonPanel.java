package org.whilescape.chat.WindowRes;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.TextArea;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicLabelUI;

// 라인, 카톡과  같은 채팅 프로그램과 같이 말풍선으로 대화를 보여주기 위한 클래스

public class BalloonPanel extends JPanel{

	public int inner_rec_width;
	public int inner_rec_height;
	public int inner_rec_area;
	
	public int radius = 10;	
	public Color bg_color;

	public JTextArea textArea;
	
	public BalloonPanel(String text, Font font, Color background_color, int motherPanel_width) {
		
		this.setLayout(null);
		
		textArea = new JTextArea(text);
		textArea.setFont(font);
		textArea.setEditable(false);
		inner_rec_area = (int)(textArea.getPreferredSize().getWidth()* textArea.getPreferredSize().getHeight());
		
		radius = (int)(font.getSize()/1.5);
		this.bg_color = background_color;
		if(textArea.getPreferredSize().getWidth() >= motherPanel_width * 70/100) {
			inner_rec_width = motherPanel_width * 70/100;
			inner_rec_height = (int)(inner_rec_area / (double)inner_rec_width * 1.5);
		
		}else {
			inner_rec_width  = (int)textArea.getPreferredSize().getWidth();
			inner_rec_height = (int)textArea.getPreferredSize().getHeight();
		}
		
		textArea.setLineWrap(true); 
		textArea.setSize(new Dimension(inner_rec_width, inner_rec_height)); // setPreferredSize는?
		textArea.setLocation(radius, radius);
		textArea.setBackground(background_color);
		textArea.setForeground(Color.BLACK);
				
		this.setSize(new Dimension(inner_rec_width + 2*radius, inner_rec_height+ 2*radius));
		this.add(textArea);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(bg_color);		
		//g.fillRect(radius, radius, inner_rec_width, inner_rec_height);
		
		g.fillRect(radius, 0, 							 inner_rec_width, radius);
		g.fillRect(radius, this.getHeight()-radius, 	 inner_rec_width, radius);
		g.fillRect(0,      radius,		 				 radius, inner_rec_height);
		g.fillRect(this.getWidth()-radius, radius,	     radius, inner_rec_height);
		
		g.fillOval(0, 0, 													2*radius, 2*radius);
		g.fillOval(0, 							this.getHeight()-2*radius,  2*radius, 2*radius);
		g.fillOval(this.getWidth() - 2*radius, 0,     						2*radius, 2*radius);
		g.fillOval(this.getWidth() - 2*radius,  this.getHeight()-2*radius, 	2*radius, 2*radius);
		
	}
	
	public static void main(String[] args) {
		
		JFrame window = new JFrame();
		window.setBounds(new Rectangle(100, 400, 1000, 600));
		window.setTitle("testing");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationByPlatform(true);
		window.setVisible(true);
		
		Container con = window.getContentPane();
		con.setLayout(null);
		
		String str = "like line, this module provides word-balloon";
		BalloonPanel bp = new BalloonPanel(str, new Font(Resource.mainFontType, Font.PLAIN, 15), Color.ORANGE ,200);

		con.add(bp);
	
		
	}

}
