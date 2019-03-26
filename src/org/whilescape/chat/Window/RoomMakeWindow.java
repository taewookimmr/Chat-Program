package org.whilescape.chat.Window;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;

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

import org.whilescape.chat.DAO.RoomDAO;
import org.whilescape.chat.DTO.MemberVO;
import org.whilescape.chat.DTO.RoomVO;
import org.whilescape.chat.Network.MultiChatClient;
import org.whilescape.chat.Network.MultiChatServer;
import org.whilescape.chat.Network.Thread_Ver_Server;
import org.whilescape.chat.WindowRes.Resource;


public class RoomMakeWindow extends JFrame implements ItemListener, ActionListener{
	
	///////////////////////////////////// 멤버 객체 영역 시작 /////////////////////////////////////////
	public static boolean isWindowCreated = false;
	
	public JLabel channelLabel;
	public JLabel roomNameLabel;
	public JLabel maxMemLabel;
	public JLabel privateLabel;
	
	public Choice channelCombo;
	public Choice maxMemCombo;
	
	public JPanel channelPanel  = new JPanel(new GridLayout(1, 2));
	public JPanel roomNamePanel = new JPanel(new GridLayout(1, 2));
	public JPanel maxMemPanel   = new JPanel(new GridLayout(1, 2));
	public JPanel privatePanel  = new JPanel(new GridLayout(1, 2));
	
	public JTextField roomNameField;
	public JPasswordField roomPwField; 
	public RoomVO rvo = new RoomVO();
	
	public JRadioButton yBtn, nBtn;
	public ButtonGroup btnGroup;
	public String privateChoice;       // why static?
	
	public JButton cancelButton;
	public JButton okButton;
	
	private Color main_bg_color 	= Resource.main_bg_color;  
	private Color main_text_color 	= Color.BLACK;
	private Color button_color      = Resource.main_btn_color;
	
	
    ///////////////////////////////////// 멤버 객체 영역 시작 /////////////////////////////////////////
	
	// 생성자
	public RoomMakeWindow() {
	
		if(!isWindowCreated) {
			frontEnd();
			backEnd();
			isWindowCreated = true;
		}else {
			JOptionPane.showMessageDialog(null, "이미 생성된 윈도우입니다", "이미 생성된 윈도우입니다", JOptionPane.ERROR_MESSAGE);
		}
		
	}

	public void frontEnd() {
		setTitle("채팅방 만들기");
		setBounds(800, 100, 450, 350);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		
		Container layeredPane = getContentPane();
		layeredPane.setBackground(main_bg_color);
		int adjust = 0;   
		if(!isResizable()) { adjust = 10;}
		layeredPane.setBounds(0, 0, this.getSize().width - 16 + adjust, this.getSize().height - 39 + adjust); 
		layeredPane.setLayout(null);
		
		
		channelLabel = new JLabel("* 채널");
		channelCombo = new Choice();		
		channelCombo.add("스포츠");
		channelCombo.add("뷰티");
		channelCombo.add("연예");
		channelCombo.add("음식");
		channelCombo.add("다이어트");
		channelCombo.add("여행");
		channelCombo.add("자격증");
		channelCombo.add("뮤직");
		channelCombo.add("패션");
		channelCombo.add("프로그래밍");
		channelCombo.add("취준");
		
		channelPanel.add(channelLabel);
		channelPanel.add(channelCombo);
		
		channelPanel.setBounds(40, 20, 300, 50);
		channelPanel.setBackground(main_bg_color);
		layeredPane.add(channelPanel);
						
		roomNameLabel = new JLabel("* 채팅방 제목");
		
		roomNameField = new JTextField(30);
		
		roomNamePanel.add(roomNameLabel);
		roomNamePanel.add(roomNameField);
		roomNamePanel.setBounds(40, 80, 300, 20);
		roomNamePanel.setBackground(main_bg_color);
		layeredPane.add(roomNamePanel);		
				
		maxMemLabel = new JLabel("* 참여인원");		

		maxMemCombo = new Choice();		
		maxMemCombo.setBounds(60, 110, 300, 60);
		maxMemCombo.add("2");
		maxMemCombo.add("3");
		maxMemCombo.add("4");
		maxMemCombo.add("5");
		maxMemCombo.add("6");
		maxMemCombo.add("7");
		maxMemCombo.add("8");
		maxMemCombo.add("9");
		maxMemCombo.add("10");
		
		maxMemPanel.add(maxMemLabel);
		maxMemPanel.add(maxMemCombo);
		
		maxMemPanel.setBounds(40, 110, 300, 50);
		maxMemPanel.setBackground(main_bg_color);
		layeredPane.add(maxMemPanel);				
		
		privateLabel = new JLabel("* 공개 여부");
		privateLabel.setBounds(40, 170, 90, 30);
		layeredPane.add(privateLabel);
		
		yBtn = new JRadioButton("공개");
		nBtn = new JRadioButton("비공개 : 비밀번호");
		btnGroup = new ButtonGroup();
		btnGroup.add(yBtn);
		btnGroup.add(nBtn);

		yBtn.setBounds(185, 170, 60, 30);
		yBtn.setBackground(main_bg_color);
		layeredPane.add(yBtn);
		nBtn.setBounds(185, 200, 145, 30);
		nBtn.setBackground(main_bg_color);
		layeredPane.add(nBtn);
						
		roomPwField = new JPasswordField(10);		
		roomPwField.setBounds(330, 206, 80, 20);
		layeredPane.add(roomPwField);
				
		okButton = new JButton("방만들기");
		okButton.setBounds(105, 250, 110, 40);
		okButton.setBackground(button_color);
		layeredPane.add(okButton);
		
		cancelButton = new JButton("취소");
		cancelButton.setBounds(225, 250, 110, 40);
		cancelButton.setBackground(button_color);
		layeredPane.add(cancelButton);
		
		
		// 폰트
		String font = Resource.mainFontType;
		channelLabel.setFont(new Font(font, Font.BOLD, 17));
		roomNameLabel.setFont(new Font(font, Font.BOLD, 17));
		maxMemLabel.setFont(new Font(font, Font.BOLD, 17));
		privateLabel.setFont(new Font(font, Font.BOLD, 17));
		
		yBtn.setFont(new Font(font, Font.BOLD, 15));
		nBtn.setFont(new Font(font, Font.BOLD, 15));
		
		okButton.setFont(new Font(font, Font.BOLD, 17));
		cancelButton.setFont(new Font(font, Font.BOLD, 17));
		
		// 글자색
		okButton.setForeground(Color.BLACK);
		cancelButton.setForeground(Color.BLACK);
			
		setVisible(true);
	}
	
	public void backEnd() {
		yBtn.addItemListener(this);
		nBtn.addItemListener(this);
		cancelButton.addActionListener(this);
		
		// 방 만들기~~ 
		okButton.addActionListener(new ActionListener() {				
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String channel  = channelCombo.getSelectedItem();
				String roomName = roomNameField.getText().trim();
				String maxMem   = maxMemCombo.getSelectedItem();
				String roomPw   = roomPwField.getText().trim();
				
				rvo.setChannel(channel);
				rvo.setRoomname(roomName);
				rvo.setMaxmem(maxMem);
				rvo.setRoompw(roomPw);
				rvo.setPrivateChoice(privateChoice);
				rvo.setId(LoginWindow.getLoginMem().getId());
				
				try   {rvo.setServerip(InetAddress.getLocalHost().getHostAddress());} 
				catch (Exception e2) {e2.printStackTrace();}				
				rvo.setServerLocalPort(MultiChatServer.server_localport++);
		

				if (RoomDAO.chat_insert(rvo)) {
//					방번호가 만들어졌으면 갖고옴
					int roomNumber = RoomDAO.getRoomNumberFromDB(rvo);
					rvo.setRoomnumber(roomNumber);

					RoomMakeWindow.isWindowCreated = false;
					setVisible(false);
					dispose();
					MainWindow.view();

//					방만들기 조건이 충족되었다면 서버 쓰레드를 돌린다.
					Thread_Ver_Server tvs = new Thread_Ver_Server(rvo);
					Thread serverThread = new Thread(tvs);
					serverThread.setDaemon(true);
					serverThread.start();

//					방장이 셀프클라이언트로서 참석한다
					MemberVO mvo_roomKing = LoginWindow.getLoginMem();
					MultiChatClient selfClient = new MultiChatClient(rvo, mvo_roomKing);		
				
				}
				
			}

		});

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
		} else {
			roomPwField.setEditable(false);
			roomPwField.setText("");
			
		}
		rvo.setPrivateChoice(privateChoice);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand() == "취소") {			
			RoomMakeWindow.isWindowCreated = false;
			setVisible(false); 
			dispose();
		}
		
	}
	
	public static void main(String[] args) {
		RoomMakeWindow window = new RoomMakeWindow();
	}

	
}
