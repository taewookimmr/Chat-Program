package org.whilescape.chat.Window;

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

import org.whilescape.chat.DAO.MemberDAO;
import org.whilescape.chat.DTO.MemberVO;
import org.whilescape.chat.WindowRes.Resource;

public class SignUpWindow extends JFrame implements ActionListener{
	
	
	///////////////////////////////////// 멤버 객체 영역 시작 /////////////////////////////////////////
	public static boolean isWindowCreated = false;
	
	// id
	public JLabel idLabel; 
	public JTextField idField;
	
	//pw
	public JLabel pwLabel;
	public JPasswordField pwField;
	
	//gender
	public JLabel genderLabel;
	public JPanel genderPanel;
	public JRadioButton men, women;
	public ButtonGroup gender;
	
	//age
	public JLabel ageLabel;
	public JTextField ageField;
	
	// 버튼
	public JButton idDuplicationBtn, okBtn;
	
	// 아이디 중복 플레그
	boolean duplication_flag=false;
	
	
	// 색
	private Color main_bg_color 	= Resource.main_bg_color;  // 하늘색
	private Color sub_bg_color 		= Color.WHITE;
	private Color main_text_color 	= Color.BLACK;
	private Color sub_text_color 	= Color.BLACK;  
	private Color button_color      = Resource.main_btn_color;
	
	///////////////////////////////////// 멤버 객체 영역 끝 /////////////////////////////////////////
	
	// 생성자 
	public SignUpWindow() {
		
		if(!isWindowCreated) {
			frontEnd();
			backEnd();
			isWindowCreated = true;
		}else {
			JOptionPane.showMessageDialog(null, "이미 생성된 윈도우입니다", "이미 생성된 윈도우입니다", JOptionPane.ERROR_MESSAGE);
		}
		
	}

	// 프론트엔드
	public void frontEnd() {
		setTitle("SignUpWindow");
		setBounds(800, 100, 450, 350);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		
		Container 	 layeredPane = getContentPane();
		layeredPane.setBackground(main_bg_color);
		int adjust = 0;   
		if(!isResizable()) { adjust = 10;}
		layeredPane.setBounds(0, 0, this.getSize().width - 16 + adjust, this.getSize().height - 39 + adjust); 
		layeredPane.setLayout(null);
		
		idLabel = new JLabel("아이디");
		idLabel.setBounds(40, 50, 70, 30);
		idLabel.setFont(new Font(Resource.mainFontType, Font.BOLD, 15));
		layeredPane.add(idLabel);

		idField = new JTextField(15);
		idField.setBounds(110, 53, 180, 30);
		layeredPane.add(idField);

		idDuplicationBtn = new JButton("중복검사");
		idDuplicationBtn.setBounds(290, 53, 95, 30);
		idDuplicationBtn.setBackground(button_color);
		idDuplicationBtn.setFont(new Font(Resource.mainFontType, Font.BOLD, 14));
		layeredPane.add(idDuplicationBtn);
		
		pwLabel = new JLabel("비밀번호");
		pwLabel.setBounds(40, 101, 80, 30);
		pwLabel.setFont(new Font(Resource.mainFontType, Font.BOLD, 15));
		layeredPane.add(pwLabel);
		
		pwField = new JPasswordField(15);
		pwField.setBounds(110, 103, 275, 30);
		layeredPane.add(pwField);
		
		genderLabel = new JLabel("성별");
		genderLabel.setBounds(40, 153, 130, 20);
		genderLabel.setFont(new Font(Resource.mainFontType, Font.BOLD, 17));
		layeredPane.add(genderLabel);
		
		men = new JRadioButton("남자");
		men.setFont(new Font(Resource.mainFontType, Font.BOLD, 17));
		men.setBackground(new Color(92, 209, 229));
		women = new JRadioButton("여자");
		women.setFont(new Font(Resource.mainFontType, Font.BOLD, 17));
		women.setBackground(new Color(92, 209, 229));
		
		gender = new ButtonGroup();
		gender.add(men);
		gender.add(women);
		
		
		genderPanel = new JPanel();
		genderPanel.add(men);
		genderPanel.add(women);
		genderPanel.setBounds(110, 145, 150, 40);
		genderPanel.setBackground(new Color(92, 209, 229));
		
		layeredPane.add(genderPanel);
		
		ageLabel = new JLabel("나이");
		ageLabel.setBounds(40, 195, 120, 30);
		ageLabel.setFont(new Font(Resource.mainFontType, Font.BOLD, 17));
		layeredPane.add(ageLabel);
		
		ageField = new JTextField(15);
		ageField.setBounds(110, 198, 275, 30);
		layeredPane.add(ageField);
		
		okBtn = new JButton("가입하기");
		okBtn.setBounds(150, 245, 130, 40);
		okBtn.setBackground(button_color);
		okBtn.setFont(new Font(Resource.mainFontType, Font.BOLD, 20));
		layeredPane.add(okBtn);
		

//		add(layeredPane); Container는 이거 하면 안된다. 자기가 자기를 추가하는 꼴
		setVisible(true);
	}

	// 백엔드
	public void backEnd() {
		
		okBtn.		     addActionListener(this);
		idDuplicationBtn.addActionListener(this);
		
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
		
		// 중복검사 버튼 
		case "중복검사":
			id = idField.getText().trim();
			duplication_flag = MemberDAO.id_Duplication_test(id);
			break;
			
		// 가입하기 버튼
		case "가입하기":
			if(duplication_flag == true) {
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

	
	// 테스트용
	public static void main(String[] args) {
		SignUpWindow w = new SignUpWindow();
	}
	
}
