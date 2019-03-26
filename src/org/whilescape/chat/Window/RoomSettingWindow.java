package org.whilescape.chat.Window;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.whilescape.chat.DAO.RoomDAO;
import org.whilescape.chat.DTO.RoomVO;
import org.whilescape.chat.WindowRes.Resource;



public class RoomSettingWindow extends JFrame implements ItemListener, ActionListener{
	
	
	///////////////////////////////////// 멤버 객체 영역 시작 /////////////////////////////////////////
	public static boolean isWindowCreated = false;
	
	public JLabel channelLabel, channelNameLabel;
	public JLabel roomNameLabel, roomNameLabel2, roomNameUpdateLabel;
	public JLabel maxMemLabel;
	public JLabel privateLabel, privateLabel2;
	
	public Choice maxMemCombo;
	
	public JPanel channelPanel  = new JPanel(new GridLayout(1, 2));
	public JPanel roomNamePanel = new JPanel(new GridLayout(1, 2));
	public JPanel roomNameUpdatePanel = new JPanel(new GridLayout(1, 2));
	public JPanel maxMemPanel  = new JPanel(new GridLayout(1, 2));
	public JPanel privatePanel = new JPanel(new GridLayout(1, 2));
	
	public JTextField roomNameField;
	public JTextField roomPwField;
	public String privateChoice;

	public RoomVO rvo = new RoomVO();
	
	public JRadioButton yBtn, nBtn;
	public ButtonGroup btnGroup;
	
	public JButton cancelButton;
	public JButton okButton;
	
	private Color main_bg_color 	= Resource.main_bg_color;  
	private Color main_text_color 	= Color.BLACK;
	private Color button_color      = Resource.main_btn_color;
	
	///////////////////////////////////// 멤버 객체 영역 끝 /////////////////////////////////////////
	
	
	
	// 생성자 
	public RoomSettingWindow(RoomVO rvo) {
		
		if(!isWindowCreated) {
			this.rvo = rvo;
			frontEnd();
			backEnd();
			isWindowCreated = true;
		}else {
			JOptionPane.showMessageDialog(null, "이미 생성된 윈도우입니다", "이미 생성된 윈도우입니다", JOptionPane.ERROR_MESSAGE);
		}
		
	}

	
	// 프론트엔드
	public void frontEnd() {
		setTitle("채팅방 정보 수정");
		setBounds(800, 100, 450, 350);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		
		Container layeredPane = getContentPane();
		layeredPane.setBackground(main_bg_color);
		int adjust = 0;   
		if(!isResizable()) { adjust = 10;}
		layeredPane.setBounds(0, 0, this.getSize().width - 16 + adjust, this.getSize().height - 39 + adjust); 
		layeredPane.setLayout(null);
		
		roomNameLabel = new JLabel("* 채팅방 제목 변경");
		roomNameField = new JTextField(rvo.getRoomname());
		
		roomNamePanel.add(roomNameLabel);
		roomNamePanel.add(roomNameField);
		roomNamePanel.setBounds(40, 60, 300, 20);
		roomNamePanel.setBackground(main_bg_color);
		layeredPane.add(roomNamePanel);		
				
				
				
		maxMemLabel = new JLabel("* 참여인원 변경");		

		maxMemCombo = new Choice();
		maxMemCombo.add("2");
		maxMemCombo.add("3");
		maxMemCombo.add("4");
		maxMemCombo.add("5");
		maxMemCombo.add("6");
		maxMemCombo.add("7");
		maxMemCombo.add("8");
		maxMemCombo.add("9");
		maxMemCombo.add("10");
		
		maxMemCombo.select(Integer.parseInt(rvo.getMaxmem())-2);
		
		maxMemPanel.add(maxMemLabel);
		maxMemPanel.add(maxMemCombo);
		
		maxMemPanel.setBackground(main_bg_color);
		maxMemPanel.setBounds(40, 100, 300, 50);
		layeredPane.add(maxMemPanel);				
		
				
		privateLabel = new JLabel("* 공개 여부 및");
		privateLabel2 = new JLabel("비밀번호 설정");
		privateLabel.setBounds(40, 170, 130, 30);
		privateLabel2.setBounds(50, 190, 130, 30);
		layeredPane.add(privateLabel);
		layeredPane.add(privateLabel2);
		
		if(rvo.getRoompw().equals("")) {
			yBtn = new JRadioButton("공개", true);
			nBtn = new JRadioButton("비공개 : 비밀번호");
			privateChoice = "공개";
		}else {
			yBtn = new JRadioButton("공개");
			nBtn = new JRadioButton("비공개 : 비밀번호", true);
			privateChoice = "비공개 : 비밀번호";
		}
		
		btnGroup = new ButtonGroup();
		btnGroup.add(yBtn);
		btnGroup.add(nBtn);

		yBtn.setBounds(190, 170, 60, 30);
		yBtn.setBackground(main_bg_color);
		layeredPane.add(yBtn);
		nBtn.setBounds(190, 200, 130, 30);
		nBtn.setBackground(main_bg_color);
		layeredPane.add(nBtn);
		
		roomPwField = new JTextField(rvo.getRoompw());
		roomPwField.setBounds(320, 206, 80, 20);
		layeredPane.add(roomPwField);
				
		okButton = new JButton("수정");
		okButton.setBounds(130, 260, 90, 25);
		okButton.setBackground(button_color);
		layeredPane.add(okButton);
		
		cancelButton = new JButton("취소");
		cancelButton.setBounds(230, 260, 90, 25);
		cancelButton.setBackground(button_color);
		layeredPane.add(cancelButton);
		
		setVisible(true);
	}
	
	
	// 백엔드
	public void backEnd() {
		yBtn.addItemListener(this);
		nBtn.addItemListener(this);
		cancelButton.addActionListener(this);
		okButton.addActionListener(this);
		

        //	방 만들기 버튼에 Enter 키로 KeyListener 만들기
		okButton.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10) {
					
				}
			}
			
		});
		
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
		
		Object object = e.getSource();
		JRadioButton radioButton = (JRadioButton) object;
		
		privateChoice = radioButton.getText();
		
		if(privateChoice.equals("비공개 : 비밀번호")) {
			roomPwField.setEditable(true);
			roomPwField.setText(rvo.getRoompw());
		} else {
			roomPwField.setEditable(false);
			roomPwField.setText("");
		}
		
		rvo.setPrivateChoice(privateChoice);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		switch(e.getActionCommand()) {
		
			case "수정" :
				
				String roomName = roomNameField.getText().trim();
				String maxMem = maxMemCombo.getSelectedItem();
				String roomPw = roomPwField.getText().trim();
				
				
				
				rvo.setRoomname(roomName);
				rvo.setMaxmem(maxMem);
				rvo.setRoompw(roomPw);
				rvo.setPrivateChoice(privateChoice);
				
				maxMemCombo.select(Integer.parseInt(rvo.getMaxmem())-2);
				
				if(RoomDAO.chat_room_update(rvo)) {
					RoomSettingWindow.isWindowCreated = false;
					setVisible(false); 
					dispose();
					MainWindow.view();
				}
				
				break;
				
				
			case "취소" :
				
				JOptionPane.showMessageDialog(null, "변경하지 않고 종료합니다.", "취소", JOptionPane.CANCEL_OPTION);
				RoomSettingWindow.isWindowCreated = false;
				setVisible(false); 
				dispose();
				
				break;
		
		}
		
		
		
	}
	
	

}
