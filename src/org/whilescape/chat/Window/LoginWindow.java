package org.whilescape.chat.Window;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.accessibility.AccessibleContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.whilescape.chat.DAO.MemberDAO;
import org.whilescape.chat.DTO.MemberVO;
import org.whilescape.chat.WindowRes.Resource;

public class LoginWindow extends JFrame implements ActionListener {
	
	
	///////////////////////////////////// 멤버 객체 영역 시작 /////////////////////////////////////////
	
	// Sign In 처리된 멤버의 정보를 담는 변수
	private static MemberVO loginMem;
	
	public static MemberVO getLoginMem() {
		return loginMem;
	}

	public static void setLoginMem(MemberVO loginMem) {
		LoginWindow.loginMem = loginMem;
	}
		
	// 윈도우를 구성하는 서브 객체들
	public JLabel idLabel, pwLabel, chatLabel;
	public JTextField idField;
	public JPasswordField pwField;
	public JButton signupButton, loginButton;
	
	// 색
	private Color main_bg_color 	= Resource.main_bg_color;  // 하늘색
	private Color sub_bg_color 		= Color.WHITE;
	private Color main_text_color 	= Color.BLACK;
	private Color sub_text_color 	= Color.BLACK;  
	private Color button_color      = Resource.main_btn_color;
	
	
	///////////////////////////////////// 멤버 객체 영역 끝 /////////////////////////////////////////
	

	
	// 생성자
	public LoginWindow() {
		// 생성자를 실행하면 프론트엔드와 백엔드 메서드를 실행한다.
		frontEnd();
		backEnd();
	}
	
	// frontend
    public void frontEnd() {
		
    	// window title, position, size, closeOperation, setLayout

       	setTitle("Login Window");
		setBounds(700, 100, 375, 400);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		
		Container layeredPane = getContentPane();
		int adjust = 0;   
		if(!isResizable()) { adjust = 10;}
		layeredPane.setBounds(0, 0, this.getSize().width - 16 + adjust, this.getSize().height - 39 + adjust); 
		layeredPane.setBackground(main_bg_color);
		layeredPane.setBounds(50, 50, this.getLocation().x, this.getLocation().y); 
		layeredPane.setLayout(null);
	
		int x=55;
		int y=100;
	
		chatLabel = new JLabel("Sign In");
		chatLabel.setFont(new Font(Resource.mainFontType, Font.PLAIN, 60));
		chatLabel.setForeground(main_text_color);
		chatLabel.setHorizontalAlignment(JLabel.CENTER);
		chatLabel.setSize(200,  100);
		chatLabel.setLocation((this.getSize().width-chatLabel.getSize().width)/2, 30);
		layeredPane.add(chatLabel);
			
		idLabel = new JLabel("아이디");
		idLabel.setFont(new Font(Resource.mainFontType, Font.PLAIN, 13));
		idLabel.setForeground(main_text_color);
		idLabel.setBounds(x + 3, y+47, 90, 20);
		layeredPane.add(idLabel);
		
		idField = new JTextField();
		idField.setBounds(x, y+70, 250, 35);
		layeredPane.add(idField);
		

		pwLabel = new JLabel("패스워드");
		pwLabel.setFont(new Font(Resource.mainFontType, Font.PLAIN, 13));
		pwLabel.setForeground(main_text_color);
		pwLabel.setBounds(x + 3, y+110, 90, 20);
		layeredPane.add(pwLabel);
		
		pwField = new JPasswordField();
		pwField.setBounds(x, y+134, 250, 35);
		layeredPane.add(pwField);
			
		loginButton = new JButton("Sign In");
		loginButton.setBackground(button_color);
		loginButton.setFont(new Font(Resource.mainFontType,Font.BOLD, 18));
		loginButton.setForeground(sub_text_color);
		loginButton.setBounds(x+125, y+190, 125, 50);
		layeredPane.add(loginButton);
		
		signupButton = new JButton("Sign Up");
		signupButton.setBackground(button_color);
		signupButton.setFont(new Font(Resource.mainFontType, Font.BOLD, 18));
		signupButton.setForeground(sub_text_color);
		signupButton.setBounds(x, y+190, 125, 50);		
		layeredPane.add(signupButton);
			
   
		setVisible(true);
		
	}

    
	// Below this line, there are methods related to backEnd operation // 
	/////////////////////////////////////////////////////////////////////
    
	public void backEnd() {
		signupButton.addActionListener(this);
		loginButton.addActionListener(this);
		
		idField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					login();
				}
			}			
		});
		pwField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {				
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					login();
				}				
			}			
		});
		
	
	}

	// Button Click
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "Sign Up":
			join();
			break;
		case "Sign In":
			login();
			break;		
		}
	}

	// Sign Up 윈도우 생성
	private void join() {
		SignUpWindow signUpWindow = new SignUpWindow();
		signUpWindow.setLocation(this.getLocation().x - signUpWindow.getWidth(), signUpWindow.getLocation().y);
	}
	
	// Sign In 절차 시행
	private void login() {
		boolean login_flag=false;
		MemberVO vo = new MemberVO();
		vo.setId(idField.getText().trim());
		vo.setPw(pwField.getText().trim());
		// 일단 Sign In 시도 단계에서 현재 컴퓨터의 ip를 vo에 넣어서 MemberDAO.login(vo)로 Sign In 시도를 해본다
		String nowIp = null;
		try {nowIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {e.printStackTrace();}
		vo.setNowIp(nowIp);
		
		login_flag = MemberDAO.login(vo);
		
		if(login_flag) {
			// Sign In 성공
			MemberVO mvo = MemberDAO.getMemberProfile(vo);
			MainWindow mainWindow = new MainWindow(mvo);
			MainWindow.view();
			loginMem = vo;
			setVisible(false);	
			
		} else {
			// Sign In 실패 
		}
	}

	
	

	public static void main(String[] args) {
		LoginWindow loginWindow = new LoginWindow();
	}
	


}
