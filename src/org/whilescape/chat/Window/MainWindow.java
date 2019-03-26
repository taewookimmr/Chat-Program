package org.whilescape.chat.Window;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.List;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.whilescape.chat.DAO.MemberDAO;
import org.whilescape.chat.DAO.RoomDAO;
import org.whilescape.chat.DTO.MemberVO;
import org.whilescape.chat.DTO.RoomVO;
import org.whilescape.chat.Network.MultiChatClient;
import org.whilescape.chat.WindowRes.Resource;

public class MainWindow extends JFrame implements ActionListener{
	
	///////////////////////////////////// 멤버 객체 영역 시작 /////////////////////////////////////////
	public Dimension yourMoniterSize, windowSize;

	public JButton profileChange_button, roomMake_button, enterRoom_button, exit_button, choice_button, renewal_button, man, woman;
	public JPanel  buttonBar_panel, interestList_panel, profile_panel, roomList_panel;
	public JLabel  interestList, idxLabel;
	public JLabel  managerLabel;
	public JLabel  dateLabel;
	public JLabel  numbersLabel;
	public JLabel  pwLabel;
	public JLabel  ageLabel;
	public JLabel  genderLabel;
	public List    multiList;

	public Image manImage[]   = new Image[5]; 
	public Image womanImage[] = new Image[5];

	public static JLabel nameLabel; // 프로필 변경 할 경우, 변경 사항이 반영되게끔 하기 위해 static으로 설정
	public static MemberVO mvo = new MemberVO();
	public static String columnNames[] = {"방 번호", "방 제목", "방장","채널", "인원", "공개여부"};
	public static DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
		
		@Override
		public boolean isCellEditable(int row, int column) {				// table 내용 수정 불가
			return false;
		}
	};
	
    public static JTable table = new JTable(model);
    
    
    // 색
	private Color main_bg_color 	= Resource.main_bg_color;  
	private Color main_text_color 	= Color.BLACK;
	private Color button_color      = Resource.main_btn_color;
    
    
	///////////////////////////////////// 멤버 객체 영역 끝 /////////////////////////////////////////
	
	// 생성자
	public MainWindow(MemberVO mvo) {
	
		this.mvo = mvo;
		if(this.mvo.getGender() == null) {
			this.mvo.setGender("남");
		}
		frontEnd();
		backEnd();
	}


	// 프론트엔드
	public void frontEnd() {
		
		setTitle("메인 윈도우");
		setSize(800, 600);
		
		yourMoniterSize = Toolkit.getDefaultToolkit().getScreenSize();
		windowSize = getSize();
		int xpos = yourMoniterSize.width - windowSize.width;
		int ypos = yourMoniterSize.height / 2 - windowSize.height / 2;
		
		setLocation(xpos*6/10, ypos*3/10);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);

		JLayeredPane layeredPane = new JLayeredPane();
		int adjust = 0;   
		if(!isResizable()) { adjust = 10;}
		layeredPane.setBounds(0, 0, this.getSize().width - 16 + adjust, this.getSize().height - 39 + adjust); 
		layeredPane.setLayout(null);

		
		// 관심리스트 화면
		add_interest_panel_on(layeredPane);
		
		// 프로필 화면 
		add_profile_panel_on(layeredPane, this.mvo);	
		
		// 방 리스트
		add_roomList_panel_on(layeredPane);
		
		add(layeredPane);
		setVisible(true);
	}
	
	// 프론트엔드.관심 리스트 페널 위치
	private void add_interest_panel_on(JLayeredPane layeredPane) {

		interestList_panel = new JPanel();
		interestList_panel.setLayout(null);
		interestList_panel.setSize(layeredPane.getWidth() * 25/100, layeredPane.getHeight() * 45/120);
		interestList_panel.setLocation(0 , 0);
		interestList_panel.setBackground(new Color(224, 255, 219));
		
		interestList = new JLabel("채널");
		interestList.setSize(interestList_panel.getWidth(), 30);
		interestList.setLocation(0,5);
		interestList.setHorizontalAlignment(SwingConstants.CENTER);
		interestList_panel.add(interestList);

	
		multiList = new List(100, false);
		
		java.util.List<String> favorList = new ArrayList<String>();
		favorList.add("스포츠");
		favorList.add("뷰티");
		favorList.add("연예");
		favorList.add("음식");
		favorList.add("다이어트");
		favorList.add("여행");
		favorList.add("자격증");
		favorList.add("뮤직");
		favorList.add("패션");
		favorList.add("프로그래밍");
		favorList.add("취준");
		
		
		for (String favor : favorList) {multiList.add(favor);}
		
		multiList.setSize(interestList_panel.getWidth() * 90/100, 120);
		multiList.setLocation((interestList_panel.getWidth()-multiList.getWidth())/2, interestList.getHeight()+10);
		interestList_panel.add(multiList);
		
		
		// 관심리스트 선택
		choice_button = new JButton("선택");	
		choice_button.setForeground(Color.BLACK);
		choice_button.setSize(interestList_panel.getWidth() * 35/80, 30);
		choice_button.setLocation(multiList.getLocation().x, interestList.getHeight() + multiList.getHeight() + 20);
		interestList_panel.add(choice_button);
		
		renewal_button = new JButton("모든 방");		
		renewal_button.setForeground(Color.BLACK);
		renewal_button.setSize(interestList_panel.getWidth() * 54/112, 30);
		renewal_button.setLocation((choice_button.getLocation().x + choice_button.getWidth() + 10)*90/100, interestList.getHeight() + multiList.getHeight() + 20);
		interestList_panel.add(renewal_button);
		
		
		// 글꼴 
		interestList.setFont(new Font(Resource.mainFontType, Font.BOLD, 23));
		choice_button.setFont(new Font(Resource.mainFontType,Font.BOLD, 14));
		renewal_button.setFont(new Font(Resource.mainFontType,Font.BOLD, 14));
		
		// 색깔
		interestList_panel.setBackground(main_bg_color);
		choice_button.setBackground(button_color);
		renewal_button.setBackground(button_color);

		// 최종적으로 interestList_panel을 layeredPane에 올려준다.
		layeredPane.add(interestList_panel);
		
	}
	
	// 프론트엔드.맴버 프로필 페널 위치
	private void add_profile_panel_on(Container layeredPane, MemberVO mvo) {
		
		
		profile_panel = new JPanel();
		profile_panel.setLayout(null);
		profile_panel.setSize(layeredPane.getWidth()/4, layeredPane.getHeight()*55/70);
		profile_panel.setLocation(0, interestList_panel.getHeight());
		profile_panel.setBackground(main_bg_color);
		
		man = new JButton();
		woman = new JButton();
		for(int i = 0; i<manImage.length; i++) {
//			manImage[i] = Toolkit.getDefaultToolkit().getImage(String.format("./src/image/man%d.png", i));
//			womanImage[i] = Toolkit.getDefaultToolkit().getImage(String.format("./src/image/woman%d.png", i));			
			manImage[i] = Toolkit.getDefaultToolkit().getImage(String.format("./src/image/man%d.png", 0));
			womanImage[i] = Toolkit.getDefaultToolkit().getImage(String.format("./src/image/woman%d.png", 0));			
		}
		if(mvo.getGender().equals("여")) {
			woman = new JButton(new ImageIcon(womanImage[new Random().nextInt(womanImage.length)]));
			woman.setLocation(10, 0);
			woman.setSize(180, 200);
			profile_panel.add(woman);			
		} else {
			man = new JButton(new ImageIcon(manImage[new Random().nextInt(manImage.length)]));
			man.setLocation(10, 0);
			man.setSize(180, 200);
			profile_panel.add(man);					
		} 
		
		nameLabel 		= new JLabel("  ID : " + mvo.getId());
		nameLabel.setSize(profile_panel.getWidth()*90/100, 30);
		nameLabel.setLocation((profile_panel.getWidth()-nameLabel.getWidth())/2 + 2, 200);
		profile_panel.add(nameLabel);
		 
		ageLabel 		= new JLabel("  나이 : " + mvo.getAge());
		ageLabel.setSize(profile_panel.getWidth()*90/100, 30);
		ageLabel.setLocation((profile_panel.getWidth()-nameLabel.getWidth())/2 + 2, 235);
		profile_panel.add(ageLabel);
		
		genderLabel 	= new JLabel("  성별 : " + mvo.getGender());
		genderLabel.setSize(profile_panel.getWidth()*90/100, 30);
		genderLabel.setLocation((profile_panel.getWidth()-nameLabel.getWidth())/2 + 2, 270);
		profile_panel.add(genderLabel);
		
		profileChange_button	= new JButton("프로필 변경");
		profileChange_button.setSize(profile_panel.getWidth()*90/100, 30);
		profileChange_button.setLocation((profile_panel.getWidth()-profileChange_button.getWidth())/2 + 2, 315);
		profileChange_button.setBackground(button_color);
		profile_panel.add(profileChange_button);
		
		
		String fontName = Resource.mainFontType;
		nameLabel.				setFont(new Font(fontName, Font.PLAIN, 15));
		ageLabel.				setFont(new Font(fontName, Font.PLAIN, 15));
		genderLabel.			setFont(new Font(fontName, Font.PLAIN, 15));
		profileChange_button.	setFont(new Font(Resource.mainFontType, Font.BOLD, 15));
		profileChange_button.setForeground(main_text_color);
		
		profile_panel.setBackground(main_bg_color);
		
		
		layeredPane.add(profile_panel);	
		

	}
	
	// 프론트엔드.방 리스트 페널 위치
	private void add_roomList_panel_on(Container layeredPane) {
		
		roomList_panel = new JPanel();
		roomList_panel.setLayout(null);
		roomList_panel.setSize(layeredPane.getWidth() * 75/100, layeredPane.getHeight()* 100/100);
		roomList_panel.setLocation(profile_panel.getWidth(), 0);
		roomList_panel.setBackground(new Color(92, 209, 229));
		
		table.getColumnModel().getColumn(0).setPreferredWidth(80);
		table.getColumnModel().getColumn(1).setPreferredWidth(230);
		table.setRowHeight(25);
		
		JScrollPane jsp = new JScrollPane(table);
		jsp.setSize(roomList_panel.getWidth()-20, roomList_panel.getHeight() * 85/100);
		jsp.setLocation((roomList_panel.getWidth()-jsp.getWidth())/2, 10);
		roomList_panel.add(jsp);
		
		add_buttonBar_panel_on(roomList_panel);
		
		middle_alignment();
		
		// 폰트
		String fontName 	= Resource.mainFontType;
		table.setFont(new Font(fontName, Font.CENTER_BASELINE, 12));
		
		layeredPane.add(roomList_panel);

	}
	
	// 프론트엔드.방 리스트 페널 위치.하단의 버튼바 위치
    private void add_buttonBar_panel_on(JPanel addOnThisPanel) {
		
		buttonBar_panel = new JPanel();
		buttonBar_panel.setLayout(null);
		buttonBar_panel.setSize(addOnThisPanel.getWidth() * 97 / 100, 50);
		buttonBar_panel.setLocation((addOnThisPanel.getWidth() - buttonBar_panel.getWidth())/2, addOnThisPanel.getHeight() - buttonBar_panel.getHeight() - 10);
		addOnThisPanel.add(buttonBar_panel);

		roomMake_button 	= new JButton("방 만들기");
		roomMake_button.	setBounds(0, 0, buttonBar_panel.getWidth()/3, 50);
		enterRoom_button 	= new JButton("입장하기");
		enterRoom_button.	setBounds(buttonBar_panel.getWidth()/3, 0, buttonBar_panel.getWidth()/3, 50);
		exit_button 		= new JButton("로그아웃");
		exit_button.		setBounds(buttonBar_panel.getWidth()/3 * 2, 0, buttonBar_panel.getWidth()/3, 50);
		
		// 글꼴 변경부
		String fontName 	= Resource.mainFontType;
		roomMake_button.setFont(new Font(fontName, Font.BOLD, 20));
		roomMake_button.setForeground(Color.BLACK);
		enterRoom_button.setFont(new Font(fontName, Font.BOLD, 20));
		enterRoom_button.setForeground(Color.BLACK);
		exit_button.setFont(new Font(fontName, Font.BOLD, 20));
		exit_button.setForeground(Color.BLACK);
		
		// 색깔
		buttonBar_panel.setBackground(main_bg_color);
		roomMake_button.setBackground(button_color);
		enterRoom_button.setBackground(button_color);
		exit_button.setBackground(button_color);
		

		// 버튼 패널에 추가
		buttonBar_panel.add(roomMake_button);
		buttonBar_panel.add(enterRoom_button);
		buttonBar_panel.add(exit_button);
		

	}
	
    // 프론트엔드.테이블 가운데 정렬
    private void middle_alignment() {
		
// 		1. DefaultTableCellHeaderRenderer 생성 (가운데 정렬을 위한)
// 		2. DefaultTableCellHeaderRenderer의 정렬을 가운데 정렬로 지정
// 		3. 정렬할 테이블의 ColumnModel을 가져옴
//		4. 반복문을 이용하여 테이블을 가운데 정렬로 지정
		
		/*1*/ DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		/*2*/ renderer.setHorizontalAlignment(SwingConstants.CENTER);
		/*3*/ TableColumnModel tcmSchedule = table.getColumnModel();
		/*4*/ for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
			tcmSchedule.getColumn(i).setCellRenderer(renderer);
		}
	}
    
    
  
	// 백엔드
	public void backEnd() {
		
		choice_button.			addActionListener(this);
		profileChange_button. 	addActionListener(this);
		roomMake_button.		addActionListener(this);
		enterRoom_button.		addActionListener(this);
		exit_button.			addActionListener(this);
		renewal_button.			addActionListener(this);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int result = JOptionPane.showConfirmDialog(null, "채팅 프로그램을 종료하시겠습니까?", "채팅프로그램 종료", JOptionPane.OK_OPTION);
				if(result == 0) {		
					MemberDAO.nowIp_setLogout_when_logout(LoginWindow.getLoginMem());
					RoomDAO.myRoomDelete(mvo.getId());
					System.exit(0);
				} else {
					try {
						e.wait();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			
		});
		
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				RoomVO rvo = new RoomVO();
				 int mouseClickPosition = table.getSelectedRow();

				try {
					int roomNumber = Integer.parseInt((String) model.getValueAt(mouseClickPosition, 0));
					rvo = RoomDAO.selectByRoomnumber(roomNumber);

					if (e.getClickCount() == 2) {   // 더블클릭으로 채팅방 입장
						// table.setEnabled(false); // 이것의 주석을 제거하면 다른 것들이 상당히 불안해진다. // 김태우
						if (rvo.getRoompw().length() > 0) {
							PasswordWindow password = new PasswordWindow();
						} else {
							if (Integer.parseInt(rvo.getNowmem()) < Integer.parseInt(rvo.getMaxmem())) {
								MultiChatClient clientThread = new MultiChatClient(rvo, LoginWindow.getLoginMem());
							} else {
								JOptionPane.showMessageDialog(null, "방에 빈 자리가 없습니다.");
								enterRoom_button.setEnabled(true);
								table.setEnabled(true);
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(null, "Table 비활성화 문제(잠시만 기다리세요)");
				}

			}
			
		});
	}

	public static void view() { 

		ArrayList<org.whilescape.chat.DTO.RoomVO> list = RoomDAO.select();
	
		for(int i = model.getRowCount() - 1; i >= 0; i--) {
			model.removeRow(i);
		}
		
		if(list.size() != 0) {
	
			String rowData[] = new String[6];
			for(org.whilescape.chat.DTO.RoomVO data : list) {
				rowData[0] = data.getRoomnumber()+"";
				rowData[1] = data.getRoomname();
				rowData[2] = data.getId();
				rowData[3] = data.getChannel();
				rowData[4] = data.getNowmem() + "/" + data.getMaxmem();
				rowData[5] = data.getRoompw();
				model.addRow(rowData);
			}			
		} 
		table.setAutoCreateRowSorter(true);
		TableRowSorter<TableModel> tablesorter = new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(tablesorter);
	}
	
	private void channel_choice() {
		
		ArrayList<RoomVO> list = RoomDAO.select();
	
		for(int i = model.getRowCount() - 1; i >= 0; i--) {
			model.removeRow(i);
		}
		
		if(list.size() != 0) {
			String rowData[] = new String[6];
			
			for(org.whilescape.chat.DTO.RoomVO data : list) {
				if(data.getChannel().equals(multiList.getSelectedItem())) {
					rowData[0] = data.getRoomnumber()+"";
					rowData[1] = data.getRoomname();
					rowData[2] = data.getId();
					rowData[3] = data.getChannel();
					rowData[4] = data.getNowmem() + "/" + data.getMaxmem();
					rowData[5] = data.getRoompw();
					model.addRow(rowData);
				}
			}			
			
			table.setAutoCreateRowSorter(true);
			TableRowSorter<TableModel> tablesorter = new TableRowSorter<TableModel>(table.getModel());
			table.setRowSorter(tablesorter);
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		
		case "프로필 변경" :
			if(ProfileChangeWindow.isWindowCreated == false) {
				ProfileChangeWindow settingWindow = new ProfileChangeWindow(this.mvo.getId());
				ProfileChangeWindow.isWindowCreated = true;
				settingWindow.setLocation(this.getLocation().x-settingWindow.getWidth(), this.getLocation().y);
			}
			else {
				JOptionPane.showMessageDialog(null, "이미 만들어진 윈도우", "오류", JOptionPane.WARNING_MESSAGE);
			}
			view();
		break;
		
		case "방 만들기":
			if(RoomMakeWindow.isWindowCreated == false) {
				RoomMakeWindow roomMakeWindow = new RoomMakeWindow();
				RoomMakeWindow.isWindowCreated = true;
				roomMakeWindow.setLocation(this.getLocation().x-roomMakeWindow.getWidth(), this.getLocation().y);

			}
			else {
				JOptionPane.showMessageDialog(null, "이미 만들어진 윈도우", "오류", JOptionPane.WARNING_MESSAGE);
			}
			view();
		break;
			
		case "입장하기":
			if(table.getSelectedRow() > -1) {
				
				// 선택한 행의 포지션을 받는다.
				// 그 행의 0번째 열에는 그 방의 방번호가 있다.
				// 그 방 번호에 해당하는 rvo를 받아온다.
				// 들어가려는 방의 rvo와 들어가는 클라이언트의 mvo를 Thread_Ver_Client의 생성자로 넣어준다.
				// 여기 디버깅 해야 한다.
				
				RoomVO rvo = new RoomVO();
				int position = table.getSelectedRow();
				int roomNumber = Integer.parseInt((String) model.getValueAt(position, 0));
				rvo = RoomDAO.selectByRoomnumber(roomNumber);
				
				if(rvo.getRoompw().length() > 0) {
					new PasswordWindow();					
				} else {
					if(Integer.parseInt(rvo.getNowmem()) < Integer.parseInt(rvo.getMaxmem())) {
						MultiChatClient clientThread = new MultiChatClient(rvo, LoginWindow.getLoginMem());
						
					} else {
						JOptionPane.showMessageDialog(null, "방에 빈 자리가 없습니다.");
					}
				}
				
				
			}else {
				JOptionPane.showMessageDialog(null, "방을 선택하고 버튼을 눌러주세요");
			}
			
			view();
			break;
			
		case "로그아웃":
			int answer = JOptionPane.showConfirmDialog(null, "로그아웃 하시겠습니까?", "로그아웃", JOptionPane.OK_OPTION);
			if(answer == 0) {
				MemberDAO.nowIp_setLogout_when_logout(LoginWindow.getLoginMem());
				RoomDAO.myRoomDelete(mvo.getId());
				dispose();
				LoginWindow window = new LoginWindow();
			}
			
		break;
			
		case "선택":
			channel_choice();
			multiList.deselect(multiList.getSelectedIndex());
			break;
			
		case "모든 방":
			multiList.deselect(multiList.getSelectedIndex());
			view();
			break;
		}
	}
	
	// 테스트
	public static void main(String[] args) {
		MainWindow window = new MainWindow(mvo);
	}

}
