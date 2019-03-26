package org.whilescape.chat.Window;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.border.LineBorder;

import org.whilescape.chat.DAO.RoomDAO;
import org.whilescape.chat.DTO.RoomVO;
import org.whilescape.chat.Network.MultiChatClient;
import org.whilescape.chat.WindowRes.Resource;

public class PasswordWindow extends JFrame implements ActionListener{
	
	public static boolean isWindowCreated = false;
	
	JLabel label;
	JPasswordField passwordField;
	JButton okBtn, noBtn;
	
	private Color main_bg_color 	= Resource.main_bg_color;  
	private Color main_text_color 	= Color.BLACK;
	private Color button_color      = Resource.main_btn_color;
	
	public PasswordWindow() {
		
		
		if(!isWindowCreated) {
			frontEnd();
			backEnd();
			isWindowCreated = true;
		}else {
			JOptionPane.showMessageDialog(null, "이미 생성된 윈도우입니다", "이미 생성된 윈도우입니다", JOptionPane.ERROR_MESSAGE);
		}
	
	}

	public void frontEnd() {
		setTitle("비공개방 비밀번호");
		setBounds(700, 400, 300, 180);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		

		Container layeredPane = getContentPane();
		layeredPane.setBackground(main_bg_color);
		int adjust = 0;   
		if(!isResizable()) { adjust = 10;}
		layeredPane.setBounds(0, 0, this.getSize().width - 16 + adjust, this.getSize().height - 39 + adjust); 
		layeredPane.setLayout(null);
		
		label = new JLabel("비밀번호를 입력해 주세요.");
		label.setFont(new Font( Resource.mainFontType, Font.BOLD, 13));
		label.setBounds(60, 10, 200, 50);
		layeredPane.add(label);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(47, 50, 200, 25);
		layeredPane.add(passwordField);
		
		okBtn = new JButton("확인");
		okBtn.setBackground(button_color);
		okBtn.setBounds(75, 90, 60, 30);
		layeredPane.add(okBtn);
		
		noBtn = new JButton("취소");
		noBtn.setBounds(145, 90, 60, 30);
		noBtn.setBackground(button_color);
		layeredPane.add(noBtn);
		
		setVisible(true);
	}
	
	public void backEnd() {
		okBtn.addActionListener(this);
		noBtn.addActionListener(this);
		
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					passwordOk();
				}
			}
			
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "확인":			
			passwordOk();
			
			break;
		case "취소":
			setVisible(false);
			dispose();
			break;
		}
	}

	private void passwordOk() {
		RoomVO rvo = new RoomVO();
		int position = MainWindow.table.getSelectedRow();
		int roomNumber = Integer.parseInt((String) MainWindow.model.getValueAt(position, 0));
		rvo = RoomDAO.selectByRoomnumber(roomNumber);
		
		String password = passwordField.getText().trim();

		if(rvo.getRoompw().equals(password)) {
			MultiChatClient clientThread = new MultiChatClient(rvo, LoginWindow.getLoginMem());
			setVisible(false);
			dispose();
		} else {
			JOptionPane.showMessageDialog(null, "비밀번호가 틀렸습니다.");
			passwordField.setText("");
			passwordField.requestFocus();
		}
	}
	
	
	// 테스트용 main함수
	public static void main(String[] args) {
		
		new PasswordWindow(); 

	}

}
