package org.whilescape.chat.Component;

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

public class BalloonPanel extends JPanel{

	public int innerRectangle_width;
	public int innerRectangle_heigth;
	public int innerRectangle_area;
	
	public int radius = 10;	
	public Color background_color;


	public JTextArea textArea;
	
	public BalloonPanel(String text, Font font, Color background_color, int motherPanel_width) {
		
		this.setLayout(null);
		
		textArea = new JTextArea(text);
		textArea.setFont(font);
		textArea.setEditable(false);
		innerRectangle_area = (int)(textArea.getPreferredSize().getWidth()* textArea.getPreferredSize().getHeight());
		
		radius = (int)(font.getSize()/1.5);
		this.background_color = background_color;
		if(textArea.getPreferredSize().getWidth() >= motherPanel_width * 70/100) {
			innerRectangle_width = motherPanel_width * 70/100;
			innerRectangle_heigth = (int)(innerRectangle_area / (double)innerRectangle_width * 1.5);
		
		}else {
			innerRectangle_width  = (int)textArea.getPreferredSize().getWidth();
			innerRectangle_heigth = (int)textArea.getPreferredSize().getHeight();
		}
		
		textArea.setLineWrap(true);
		textArea.setSize(new Dimension(innerRectangle_width, innerRectangle_heigth)); // setPreferredSize는?
		textArea.setLocation(radius, radius);
		textArea.setBackground(background_color);
		textArea.setForeground(Color.BLACK);
				
		this.setSize(new Dimension(innerRectangle_width + 2*radius, innerRectangle_heigth+ 2*radius));
		this.add(textArea);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(background_color);		
//		g.fillRect(radius, radius, innerRectangle_width, innerRectangle_heigth);
		
		g.fillRect(radius, 0, 							 innerRectangle_width, radius);
		g.fillRect(radius, this.getHeight()-radius, 	 innerRectangle_width, radius);
		g.fillRect(0,      radius,		 				 radius, innerRectangle_heigth);
		g.fillRect(this.getWidth()-radius, radius,	     radius, innerRectangle_heigth);
		
		g.fillOval(0, 0, 													2*radius, 2*radius);
		g.fillOval(0, 							this.getHeight()-2*radius,  2*radius, 2*radius);
		g.fillOval(this.getWidth() - 2*radius, 0,     						2*radius, 2*radius);
		g.fillOval(this.getWidth() - 2*radius,  this.getHeight()-2*radius, 	2*radius, 2*radius);
		
	}
	
	public static void main(String[] args) {
		JFrame window = new JFrame();
		window.setBounds(new Rectangle(100, 400, 1000, 600));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLocationByPlatform(true);
		window.setVisible(true);
		
		Container con = window.getContentPane();
		con.setLayout(null);
		
		String str = "동해물과 백두산이 마르고 닳도록 따라라라 보우하사 우리나라만세\n"
				+ "무궁화 삼천리 화려강산 대한사람 대한으로 길이 보전하세";
		str +=  "\n동해물과 백두산이 마르고 닳도록 따라라라 보우하사 우리나라만세\n"
				+ "무궁화 삼천리 화려강산 대한사람 대한으로 길이 보전하세";
		BalloonPanel bp = new BalloonPanel(str, new Font("배달의민족 도현", Font.PLAIN, 15), Color.ORANGE ,100);

		con.add(bp);
	
		
	}

}
