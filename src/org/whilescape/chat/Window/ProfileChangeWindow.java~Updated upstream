package org.whilescape.chat.Window;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.whilescape.chat.DAO.MemberDAO;
import org.whilescape.chat.DTO.MemberVO;
import org.whilescape.chat.WindowRes.Resource;

public class ProfileChangeWindow extends JFrame implements ActionListener , ItemListener{
	
	
	///////////////////////////////////// 멤버 객체 영역 끝 /////////////////////////////////////////
	public static boolean isWindowCreated = false;
	
	public JLabel startLabel;
	public JLabel idLabel;
	public JLabel pwLabel;
	public JLabel nowPwLabel;
	public JLabel newPwLabel1;
	public JLabel newPwLabel2;
	
	public JTextField idField;
	public JPasswordField pwField;
	public JPasswordField newPwField1;
	public JPasswordField newPwField2;
	
	public JButton idOverlapBtn;
	public JButton okBtn;
	public JButton noBtn;
	
	public JRadioButton yBtn, nBtn;
	public ButtonGroup btnGroup;

	public boolean duplicate_flag = false;
	
	public String userName;
	public static String idUpdateChoice;
	
	
    // 색
	private Color main_bg_color 	= Resource.main_bg_color;  
	private Color main_text_color 	= Color.BLACK;
	private Color button_color      = Resource.main_btn_color;
    
	
	///////////////////////////////////// 멤버 객체 영역 끝 /////////////////////////////////////////
	
	public ProfileChangeWindow(String userName) {
		
		this.userName = userName;
		
		if(!isWindowCreated) {
			frontEnd();
			backEnd();
			isWindowCreated = true;
		}else {
			JOptionPane.showMessageDialog(null, "이미 생성된 윈도우입니다", "이미 생성된 윈도우입니다", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	public void frontEnd() {
		setTitle("프로필 수정");
		setBounds(800, 400, 310, 400);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		
		Container layeredPane = getContentPane();
		layeredPane.setBackground(new Color(92, 209, 229));
		int adjust = 0;   
		if(!isResizable()) { adjust = 10;}
		layeredPane.setBounds(0, 0, this.getSize().width - 16 + adjust, this.getSize().height - 39 + adjust); 
		layeredPane.setLayout(null);
		
		startLabel = new JLabel( "**  " + userName + "  ** 님의 정보를 수정합니다.");
//		startLabel = new JLabel(" 님의 정보를 수정합니다.");
		startLabel.setBounds(20, 10, 250, 50);
		startLabel.setBackground(main_bg_color);		
		layeredPane.add(startLabel);
		
		idLabel = new JLabel("* 아이디 변경");
		idLabel.setBounds(20, 50, 80, 50);
		layeredPane.add(idLabel);
		
		yBtn = new JRadioButton("변경");
		yBtn.setBackground(main_bg_color);
		nBtn = new JRadioButton("변경안함");
		nBtn.setBackground(main_bg_color);
		btnGroup = new ButtonGroup();
		btnGroup.add(yBtn);
		btnGroup.add(nBtn);


		yBtn.setBounds(140, 60, 60, 30);
		layeredPane.add(yBtn);
		nBtn.setBounds(200, 60, 100, 30);
		layeredPane.add(nBtn);
		
		pwLabel = new JLabel("* 비밀번호 변경");
		pwLabel.setBounds(20, 150, 150, 50);
		layeredPane.add(pwLabel);
		
		idField = new JTextField("새로운 아이디");
		idField.setBounds(20, 100, 170, 25);
		layeredPane.add(idField);

		idOverlapBtn = new JButton("중복검사");
		idOverlapBtn.setBounds(200, 100, 90, 25);
		idOverlapBtn.setBackground(button_color);
		layeredPane.add(idOverlapBtn);
		
		nowPwLabel = new JLabel("현재 비밀번호");
		newPwLabel1 = new JLabel("새 비밀번호");
		newPwLabel2 = new JLabel("새 비밀번호 확인");		
		nowPwLabel.setBounds(20, 200, 150, 25);
		newPwLabel1.setBounds(20, 230, 150, 25);
		newPwLabel2.setBounds(20, 260, 150, 25);
		layeredPane.add(nowPwLabel);
		layeredPane.add(newPwLabel1);
		layeredPane.add(newPwLabel2);
		
		pwField =  new JPasswordField(20);
		newPwField1 = new JPasswordField(20);
		newPwField2 = new JPasswordField(20);		
		pwField.setBounds(130, 200, 150, 25);
		newPwField1.setBounds(130, 230, 150, 25);
		newPwField2.setBounds(130, 260, 150, 25);
		layeredPane.add(pwField);
		layeredPane.add(newPwField1);
		layeredPane.add(newPwField2);
		
		okBtn = new JButton("저장");
		noBtn = new JButton("취소");
		okBtn.setBounds(80, 320, 75, 35);
		okBtn.setBackground(button_color);
		noBtn.setBounds(160, 320, 75, 35);
		noBtn.setBackground(button_color);
		
		layeredPane.add(okBtn);
		layeredPane.add(noBtn);


		setVisible(true);
	}
	
	
	public void backEnd() {
		yBtn.addItemListener(this);
		nBtn.addItemListener(this);
		idField.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == 1) {
					idField.setText("");
				}
			}
		});
		
		idOverlapBtn.addActionListener(this);
		okBtn.addActionListener(this);
		noBtn.addActionListener(this);
		
		// 윈도우 단일 생성 관련부
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				isWindowCreated = false;
			}
		});
	}

	@Override
	public void itemStateChanged(ItemEvent e) {		
		
		JRadioButton radioButton = (JRadioButton) e.getSource();
		
		idUpdateChoice = radioButton.getText();
		
		if(idUpdateChoice.equals("변경")) {
			idField.setEditable(true);			
			idOverlapBtn.setEnabled(true);
		} else {
			idField.setEditable(false);
			idOverlapBtn.setEnabled(false);
		}
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		MemberVO vo = new MemberVO();
		String id, nowPw, newPw1, newPw2;
		boolean pwFlag = true;
		
		switch(e.getActionCommand()) {
			case "중복검사" :
				id = idField.getText().trim();
				duplicate_flag = MemberDAO.id_Duplication_test(id);				
				break;
				
			case "저장" :
				if(idUpdateChoice == null) {
//					아이디를 변경할지 안할지 선택 안했을 경우
					JOptionPane.showMessageDialog(null, "아이디를 변경할지 선택하세요", "오류", JOptionPane.CANCEL_OPTION);
					break;
				}else {
					
//					아이디를 변경할지 안할지 선택이 끝나면
					
					if(idUpdateChoice.equals("변경")) {
//					변경을 선택했을 경우 (아이디/비밀번호 모두 변경)
						if(duplicate_flag == true) {
							id = idField.getText().trim();
							nowPw = pwField.getText().trim();
							newPw1 = newPwField1.getText().trim();
							newPw2 = newPwField2.getText().trim();
							
							pwFlag = id_pw_check(nowPw, newPw1, newPw2, pwFlag);
							
							if(pwFlag) {								
								vo.setId(id);
								vo.setPw(newPw2);
								if(MemberDAO.member_update_id_pw(vo)) {
									MainWindow.nameLabel.setText("ID : " + id);
									ProfileChangeWindow.isWindowCreated = false;
									setVisible(false);
									dispose();
								}								
							}							
							break;							
						}else {
							JOptionPane.showMessageDialog(null, "아이디 중복검사를 해주세요.", "중복검사오류", JOptionPane.WARNING_MESSAGE);
							break;
						}
					}else {
//					변경안함을 선택한 경우 (비밀번호만 변경)					
						nowPw = pwField.getText().trim();
						newPw1 = newPwField1.getText().trim();
						newPw2 = newPwField2.getText().trim();
						
						pwFlag = id_pw_check(nowPw, newPw1, newPw2, pwFlag);						
						if(pwFlag) {							
							vo.setPw(newPw2);
							if(MemberDAO.member_update_pw(vo)) {
								ProfileChangeWindow.isWindowCreated = false;
								setVisible(false);
								dispose();
							}							
						}
						break;
					}
				}
				
			case "취소" :
				JOptionPane.showMessageDialog(null, "변경하지 않고 종료합니다.", "취소", JOptionPane.CANCEL_OPTION);
				ProfileChangeWindow.isWindowCreated = false;
				setVisible(false);
				dispose();
				break;
		}
		
	}

	public boolean id_pw_check(String nowPw, String newPw1, String newPw2, boolean pwFlag) {
		if(!nowPw.equals(LoginWindow.getLoginMem().getPw())) {
			JOptionPane.showMessageDialog(null, "현재 비밀번호가 일치하지 않습니다.", "오류", JOptionPane.CANCEL_OPTION);
			pwFlag = false;
		}else if(!newPw1.equals(newPw2)) {
			JOptionPane.showMessageDialog(null, "변경하려는 비밀번호가 일치하지 않습니다.", "오류", JOptionPane.CANCEL_OPTION);
			pwFlag = false;
		}
		return pwFlag;
	}

	
	public static void main(String[] args) {
		ProfileChangeWindow window = new ProfileChangeWindow("what");
	}
}
