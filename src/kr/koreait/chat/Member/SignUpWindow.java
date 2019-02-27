package kr.koreait.chat.Member;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class SignUpWindow extends JFrame implements ActionListener{
	
	public static boolean isWindowCreated = false;
	
	JLabel idLabel;
	JLabel pwLabel;
	JLabel genderLabel;
	JLabel ageLabel;
	JTextField idField;
	JPasswordField pwField;
	JTextField ageField;
	JButton idOverlapButton;
	JButton okButton;
	
	JPanel genderPanel;
	
	JRadioButton men, women;
	ButtonGroup gender;
	
	boolean overlap_flag=false;
	
	public SignUpWindow() {
		
		frontEnd();
		
		backEnd();
		
	}

	public void frontEnd() {
		setTitle("ChatProjectWin");
		setBounds(800, 100, 450, 350);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		
		Container 	 layeredPane = getContentPane();
//		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBackground(new Color(92, 209, 229));
		int adjust = 0;   
		if(!isResizable()) { adjust = 10;}
		layeredPane.setBounds(0, 0, this.getSize().width - 16 + adjust, this.getSize().height - 39 + adjust); 
		layeredPane.setLayout(null);
		
		idLabel = new JLabel("아이디");
		idLabel.setBounds(40, 50, 70, 30);
		idLabel.setFont(new Font("돋음", Font.BOLD, 15));
		layeredPane.add(idLabel);

		idField = new JTextField(15);
		idField.setBounds(110, 53, 180, 30);
		layeredPane.add(idField);

		idOverlapButton = new JButton("중복검사");
		idOverlapButton.setBounds(290, 53, 95, 30);
		idOverlapButton.setBackground(new Color(250, 237, 125));
		idOverlapButton.setFont(new Font("돋음", Font.BOLD, 14));
		layeredPane.add(idOverlapButton);
		
		pwLabel = new JLabel("비밀번호");
		pwLabel.setBounds(40, 101, 80, 30);
		pwLabel.setFont(new Font("돋음", Font.BOLD, 15));
		layeredPane.add(pwLabel);
		
		pwField = new JPasswordField(15);
		pwField.setBounds(110, 103, 275, 30);
		layeredPane.add(pwField);
		
		genderLabel = new JLabel("성별");
		genderLabel.setBounds(40, 153, 130, 20);
		genderLabel.setFont(new Font("돋음", Font.BOLD, 17));
		layeredPane.add(genderLabel);
		
		men = new JRadioButton("남");
		men.setFont(new Font("돋음", Font.BOLD, 17));
		men.setBackground(new Color(92, 209, 229));
		women = new JRadioButton("여", true);
		women.setFont(new Font("돋음", Font.BOLD, 17));
		women.setBackground(new Color(92, 209, 229));
		gender = new ButtonGroup();
		gender.add(men);
		gender.add(women);
		
		
		genderPanel = new JPanel();
		genderPanel.add(men);
		genderPanel.add(women);
		genderPanel.setBounds(97, 145, 115, 40);
		genderPanel.setBackground(new Color(92, 209, 229));
		
		layeredPane.add(genderPanel);
		
		ageLabel = new JLabel("나이");
		ageLabel.setBounds(40, 195, 120, 30);
		ageLabel.setFont(new Font("돋음", Font.BOLD, 17));
		layeredPane.add(ageLabel);
		
		ageField = new JTextField(15);
		ageField.setBounds(110, 198, 275, 30);
		layeredPane.add(ageField);
		
		okButton = new JButton("가입하기");
		okButton.setBounds(150, 245, 130, 40);
		okButton.setBackground(new Color(250, 237, 125));
		okButton.setFont(new Font("돋음", Font.BOLD, 20));
		layeredPane.add(okButton);
		
		// 색깔
		/*
		layeredPane.setBackground(Color.LIGHT_GRAY);
		idField.setBackground(Color.WHITE);
		pwField.setBackground(Color.WHITE);
		ageField.setBackground(Color.WHITE);
		men.setBackground(Color.LIGHT_GRAY);
		women.setBackground(Color.LIGHT_GRAY);;
		genderPanel.setBackground(Color.LIGHT_GRAY);
		idOverlapButton.setBackground(Color.WHITE);
		okButton.setBackground(Color.WHITE);
		*/
		
		
//		add(layeredPane); Container는 이거 하면 안된다. 자기가 자기를 추가하는 꼴
		setVisible(true);
	}
	
	public static void main(String[] args) {
		SignUpWindow w = new SignUpWindow();
	}

	public void backEnd() {
		
		okButton.		addActionListener(this);
		idOverlapButton.addActionListener(this);
		
		// 윈도우 단일 생성 관련부
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				isWindowCreated = false;
			}
		});
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		MemberVO vo = new MemberVO();
		String id, pw, gender;
		int age = 0;
		
		switch (e.getActionCommand()) {
		case "중복검사":
			id = idField.getText().trim();
			overlap_flag = MemberDAO.member_overlap(id);
			break;
		case "가입하기":
			if(overlap_flag == true) {
				id = idField.getText().trim();
				pw = pwField.getText().trim();
				gender = men.isSelected()?men.getActionCommand():women.getActionCommand();
				if(ageField.getText().trim().equals("")) {
					age = 0;
				} else {
					if(Pattern.matches("^[0-9]+$", ageField.getText().trim())){
						age = Integer.parseInt(ageField.getText().trim());
					}else{
						JOptionPane.showMessageDialog(null, "나이입력란에 숫자를 입력하세요.", "나이입력오류", JOptionPane.WARNING_MESSAGE);
						break;
					}
				}
				vo.setId(id);
				vo.setPw(pw);
				vo.setGender(gender);
				vo.setAge(age);
				vo.setNowIp("logout");
				if(MemberDAO.member_insert(vo)) {
					setVisible(false); 
					dispose();
				}
				break;
			} else {
				JOptionPane.showMessageDialog(null, "아이디 중복검사를 해주세요.", "중복검사오류", JOptionPane.WARNING_MESSAGE);
				break;
			}
		}
	}

	
	
	
}
