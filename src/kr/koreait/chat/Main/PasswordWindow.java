package kr.koreait.chat.Main;

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

import kr.koreait.chat.Initiator.LoginWindow;
import kr.koreait.chat.Room.RoomVO;
import kr.koreait.chat.ZNetwork.MultiChatClient;

public class PasswordWindow extends JFrame implements ActionListener{
	
	
	JLabel label;
	JPasswordField passwordField;
	JButton okBtn, noBtn;
	
	public PasswordWindow() {
		
		frontEnd();
		backEnd();
	
	}

	public void frontEnd() {
		setTitle("비공개방 비밀번호");
		setBounds(700, 400, 300, 180);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		

		Container layeredPane = getContentPane();
		layeredPane.setBackground(new Color(92, 209, 229));
		int adjust = 0;   
		if(!isResizable()) { adjust = 10;}
		layeredPane.setBounds(0, 0, this.getSize().width - 16 + adjust, this.getSize().height - 39 + adjust); 
		layeredPane.setLayout(null);
		
		label = new JLabel("비밀번호를 입력해 주세요.");
		label.setFont(new Font("나눔고딕코딩", Font.BOLD, 13));
		label.setBounds(60, 10, 200, 50);
		layeredPane.add(label);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(47, 50, 200, 25);
		layeredPane.add(passwordField);
		
		okBtn = new JButton("확인");
		okBtn.setBackground(new Color(250, 237, 125));
		okBtn.setBounds(75, 90, 60, 30);
		layeredPane.add(okBtn);
		
		noBtn = new JButton("취소");
		noBtn.setBounds(145, 90, 60, 30);
		noBtn.setBackground(new Color(250, 237, 125));
		layeredPane.add(noBtn);
		
		setVisible(true);
	}
	
	public void backEnd() {
		okBtn.addActionListener(this);
		noBtn.addActionListener(this);
		
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10) {
					passwordOk();
				}
			}
			
		});
	}

	
	public static void main(String[] args) {
		
		new PasswordWindow(); 

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
		rvo = MainDAO.selectByRoomnumber(roomNumber);
		
		String password = passwordField.getText().trim();

		if(rvo.getRoompw().equals(password)) {
//			JOptionPane.showMessageDialog(null, "비밀번호가 맞았습니다.");
			MultiChatClient clientThread = new MultiChatClient(rvo, LoginWindow.getLoginMem());
			setVisible(false);
			dispose();
		} else {
			JOptionPane.showMessageDialog(null, "비밀번호가 틀렸습니다.");
			passwordField.setText("");
			passwordField.requestFocus();
		}
	}
	
	

}
