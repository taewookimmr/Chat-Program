package org.whilescape.chat.Room;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import org.whilescape.chat.Main.MainWindow;
import org.whilescape.chat.Member.LoginWindow;
import org.whilescape.chat.Member.MemberVO;
import org.whilescape.chat.Util.DBUtil;
import org.whilescape.chat.ZNetwork.MultiChatClient;
import org.whilescape.chat.ZNetwork.Thread_Ver_Server;



public class RoomUpdateWindow extends JFrame implements ItemListener, ActionListener{
	
	JLabel channelLabel, channelNameLabel;
	JLabel roomNameLabel, roomNameLabel2, roomNameUpdateLabel;
	JLabel maxMemLabel;
	JLabel privateLabel, privateLabel2;
	
	Choice maxMemCombo;
	
	JPanel channelPanel = new JPanel(new GridLayout(1, 2));
	JPanel roomNamePanel = new JPanel(new GridLayout(1, 2));
	JPanel roomNameUpdatePanel = new JPanel(new GridLayout(1, 2));
	JPanel maxMemPanel = new JPanel(new GridLayout(1, 2));
	JPanel privatePanel = new JPanel(new GridLayout(1, 2));
	
	JTextField roomNameField;
	public static JTextField roomPwField;
	public static boolean isWindowCreated = false;
	static RoomVO rvo = new RoomVO();
	
	JRadioButton yBtn, nBtn;
	ButtonGroup btnGroup;
	public static String privateChoice;
	
	JButton cancelButton;
	JButton okButton;
	
	public RoomUpdateWindow(RoomVO rvo) {
		this.rvo = rvo;
				
		frontEnd();
		
		backEnd();
		
	}

	public void frontEnd() {
		setTitle("채팅방 정보 수정");
		setBounds(800, 100, 450, 350);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		
		Container layeredPane = getContentPane();
		layeredPane.setBackground(new Color(92, 209, 229));
		int adjust = 0;   
		if(!isResizable()) { adjust = 10;}
		layeredPane.setBounds(0, 0, this.getSize().width - 16 + adjust, this.getSize().height - 39 + adjust); 
		layeredPane.setLayout(null);
		
		roomNameLabel = new JLabel("* 채팅방 제목 변경");
		roomNameField = new JTextField(rvo.getRoomname());
		
		roomNamePanel.add(roomNameLabel);
		roomNamePanel.add(roomNameField);
		roomNamePanel.setBounds(40, 60, 300, 20);
		roomNamePanel.setBackground(new Color(92, 209, 229));
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
		
		maxMemPanel.setBackground(new Color(92, 209, 229));
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
		yBtn.setBackground(new Color(92, 209, 229));
		layeredPane.add(yBtn);
		nBtn.setBounds(190, 200, 130, 30);
		nBtn.setBackground(new Color(92, 209, 229));
		layeredPane.add(nBtn);
		
		roomPwField = new JTextField(rvo.getRoompw());
		roomPwField.setBounds(320, 206, 80, 20);
		layeredPane.add(roomPwField);
				
		okButton = new JButton("수정");
		okButton.setBounds(130, 260, 90, 25);
		okButton.setBackground(new Color(250, 237, 125));
		layeredPane.add(okButton);
		
		cancelButton = new JButton("취소");
		cancelButton.setBounds(230, 260, 90, 25);
		cancelButton.setBackground(new Color(250, 237, 125));
		layeredPane.add(cancelButton);
		
		setVisible(true);
	}
	
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
				
				maxMemCombo.select(Integer.parseInt(rvo.getMaxmem())-2);
				
				if(RoomMakeDAO.chat_room_update(rvo)) {
					RoomUpdateWindow.isWindowCreated = false;
					setVisible(false); 
					dispose();
					MainWindow.view();
				}
				
				break;
				
				
			case "취소" :
				
				JOptionPane.showMessageDialog(null, "변경하지 않고 종료합니다.", "취소", JOptionPane.CANCEL_OPTION);
				RoomUpdateWindow.isWindowCreated = false;
				setVisible(false); 
				dispose();
				
				break;
		
		}
		
		
		
	}
	
}
