package kr.koreait.chat.Initiator;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import kr.koreait.chat.Main.MainWindow;
import kr.koreait.chat.Member.MemberDAO;
import kr.koreait.chat.Member.MemberVO;
import kr.koreait.chat.Member.SignUpWindow;

public class LoginWindow extends JFrame implements ActionListener {
	
	public static MemberVO loginMem;
	
	public static MemberVO getLoginMem() {
		return loginMem;
	}

	public static void setLoginMem(MemberVO loginMem) {
		LoginWindow.loginMem = loginMem;
	}
	
	JLabel idLabel;
	JLabel pwLabel;
	JLabel chatLabel;
	JTextField idField;
	JPasswordField pwField;
	JButton signupButton;
	JButton loginButton;
	
	public LoginWindow() {

		frontEnd();
		backEnd();
	}
	
    public void frontEnd() {
		
       	setTitle("ChatProjectWin");
		setBounds(700, 100, 370, 450);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		
		Container layeredPane = getContentPane();
		layeredPane.setBackground(new Color(92, 209, 229));
		layeredPane.setBounds(50, 50, 200, 300); 
		layeredPane.setLayout(null);
	
		int x=55;
		int y=100;
	
		
		chatLabel = new JLabel("CHAT");
		chatLabel.setFont(new Font("돋음", Font.PLAIN, 60));
		chatLabel.setForeground(new Color(7, 129, 150));
		chatLabel.setHorizontalAlignment(JLabel.CENTER);
		chatLabel.setBounds(70, 30, 200, 100);
		layeredPane.add(chatLabel);
			
		idLabel = new JLabel("아이디");
		idLabel.setFont(new Font("돋음",Font.PLAIN, 13));
		idLabel.setBounds(x + 3, y+47, 60, 20);
		layeredPane.add(idLabel);
		
		idField = new JTextField();
		idField.setBounds(x, y+70, 250, 35);
		layeredPane.add(idField);
		
		pwLabel = new JLabel("비밀번호");
		pwLabel.setFont(new Font("돋음",Font.PLAIN, 13));
		pwLabel.setBounds(x + 3, y+110, 60, 20);
		layeredPane.add(pwLabel);
		
		
		pwField = new JPasswordField();
		pwField.setBounds(x, y+134, 250, 35);
		layeredPane.add(pwField);
			
		loginButton = new JButton("로그인");
//		loginButton.setBackground(new Color(237, 160, 199));
		loginButton.setBackground(new Color(250, 237, 125));
		loginButton.setForeground(Color.BLACK);
		loginButton.setFont(new Font("돋음",Font.BOLD, 14));
		loginButton.setBounds(x+125, y+190, 125, 50);
		layeredPane.add(loginButton);
		
		
		
		signupButton = new JButton("회원가입");
//		signupButton.setBackground(new Color(237, 160, 199));
		signupButton.setBackground(new Color(250, 237, 125));
		signupButton.setFont(new Font("돋음", Font.BOLD, 14));
		signupButton.setForeground(Color.BLACK);
		signupButton.setBounds(x, y+190, 125, 50);		
		layeredPane.add(signupButton);
			
   
		setVisible(true);
		
	}

	public void backEnd() {
		signupButton.addActionListener(this);
		loginButton.addActionListener(this);
		
		idField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10) {
					login();
				}
			}			
		});
		pwField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {				
				if(e.getKeyCode() == 10) {
					login();
				}				
			}			
		});
		
	
	}

	public static void main(String[] args) {
		LoginWindow window = new LoginWindow();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "회원가입":
			join();
			break;
		case "로그인":
			login();
			break;		
		}
	}

	private void join() {
		SignUpWindow signUpWindow = new SignUpWindow();
		signUpWindow.setLocation(this.getLocation().x - signUpWindow.getWidth(), signUpWindow.getLocation().y);
	}
	
	private void login() {
		boolean login_flag=false;
		MemberVO vo = new MemberVO();
		vo.setId(idField.getText().trim());
		vo.setPw(pwField.getText().trim());
		// 일단 로그인 시도 단계에서 현재 컴퓨터의 ip를 vo에 넣어서 MemberDAO.login(vo)로 로그인 시도를 해본다
		String nowIp = null;
		try {
			nowIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {e.printStackTrace();}
		vo.setNowIp(nowIp);
		
		login_flag = MemberDAO.login(vo);
		
		// 로그인 성공
		if(login_flag) {
			MemberVO mvo = MemberDAO.getMemberProfile(vo);
			MainWindow mainWindow = new MainWindow(mvo);
			MainWindow.view();
			loginMem = vo;
			setVisible(false);	
			
		} else {
			// 로그인 실패 
			
		}
	}


}
