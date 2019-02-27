package kr.koreait.chat.Main;
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

import kr.koreait.chat.Initiator.LoginWindow;
import kr.koreait.chat.Member.MemberDAO;
import kr.koreait.chat.Member.MemberUpdateWindow;
import kr.koreait.chat.Member.MemberVO;
import kr.koreait.chat.Room.RoomMakeDAO;
import kr.koreait.chat.Room.RoomMakeWindow;
import kr.koreait.chat.Room.RoomVO;
import kr.koreait.chat.ZNetwork.MultiChatClient;

public class MainWindow extends JFrame implements ActionListener{
	
	public Dimension yourMoniterSize, windowSize;

	public JButton profileChange_button, roomMake_button, enterRoom_button, exit_button, choice_button, renewal_button, man, woman;
	public JPanel  buttonBar_panel, interestList_panel, profile_panel, roomList_panel;
	public JLabel  interestList, idxLabel;
	public static int position;
	public static JLabel nameLabel;

	public JLabel managerLabel;
	public JLabel dateLabel;
	public JLabel numbersLabel;
	public JLabel pwLabel;
	public JLabel ageLabel;
	public JLabel genderLabel;
	public List    multiList;

	public Image manImage[] = new Image[5];
	public Image womanImage[] = new Image[5];

	static MemberVO mvo = new MemberVO();
	public static String columnNames[] = {"방 번호", "방 제목", "방장","채널", "인원", "공개여부"};
	static DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
		
		@Override
		public boolean isCellEditable(int row, int column) {				// table 내용 수정 불가
			return false;
		}
	};

    public static JTable table = new JTable(model);
	  
	
	// 생성자
	public MainWindow(MemberVO mvo) {
		this.mvo = mvo; // 이게 맨 앞에 있어야 함_노터치!
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
	
	public static void main(String[] args) {
		MainWindow window = new MainWindow(mvo);
	}

	// 프론트엔드.관심 리스트 페널 위치
	private void add_interest_panel_on(JLayeredPane layeredPane) {

		interestList_panel = new JPanel();
		interestList_panel.setLayout(null);
		interestList_panel.setSize(layeredPane.getWidth() * 25/100, layeredPane.getHeight() * 45/120);
		interestList_panel.setLocation(0 , 0);
		interestList_panel.setBackground(new Color(224, 255, 219));
		
		interestList = new JLabel("◀ CHANNEL ▶");
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
		interestList.setFont(new Font("돋음", Font.BOLD, 23));
		choice_button.setFont(new Font("돋음",Font.BOLD, 14));
		renewal_button.setFont(new Font("돋음",Font.BOLD, 14));
		
		// 색깔
		interestList_panel.setBackground(new Color(92, 209, 229));
		choice_button.setBackground(new Color(250, 237, 125));
		renewal_button.setBackground(new Color(250, 237, 125));

		// 최종적으로 interestList_panel을 layeredPane에 올려준다.
		layeredPane.add(interestList_panel);
		
	}
	
	// 프론트엔드.맴버 프로필 페널 위치
	private void add_profile_panel_on(Container layeredPane, MemberVO mvo) {
		
		
		profile_panel = new JPanel();
		profile_panel.setLayout(null);
		profile_panel.setSize(layeredPane.getWidth()/4, layeredPane.getHeight()*55/70);
		profile_panel.setLocation(0, interestList_panel.getHeight());
		profile_panel.setBackground(new Color(92, 209, 229));
		
		man = new JButton();
		woman = new JButton();
		for(int i = 0; i<manImage.length; i++) {
			manImage[i] = Toolkit.getDefaultToolkit().getImage(String.format("./src/image/man%d.png", i));
			womanImage[i] = Toolkit.getDefaultToolkit().getImage(String.format("./src/image/woman%d.png", i));			
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
		nameLabel.setBorder(new LineBorder(new Color(7, 129, 150), 2));
		profile_panel.add(nameLabel);
		 
		ageLabel 		= new JLabel("  나이 : " + mvo.getAge());
		ageLabel.setSize(profile_panel.getWidth()*90/100, 30);
		ageLabel.setLocation((profile_panel.getWidth()-nameLabel.getWidth())/2 + 2, 235);
		ageLabel.setBorder(new LineBorder(new Color(7, 129, 150), 2));
		profile_panel.add(ageLabel);
		
		genderLabel 	= new JLabel("  성별 : " + mvo.getGender());
		genderLabel.setSize(profile_panel.getWidth()*90/100, 30);
		genderLabel.setLocation((profile_panel.getWidth()-nameLabel.getWidth())/2 + 2, 270);
		genderLabel.setBorder(new LineBorder(new Color(7, 129, 150), 2));
		profile_panel.add(genderLabel);
		
		profileChange_button	= new JButton("프로필 변경");
		profileChange_button.setSize(profile_panel.getWidth()*90/100, 30);
		profileChange_button.setLocation((profile_panel.getWidth()-profileChange_button.getWidth())/2 + 2, 310);
		profile_panel.add(profileChange_button);
		
		
		String fontName 	= "돋음";
		nameLabel.				setFont(new Font(fontName, Font.PLAIN, 15));
		ageLabel.				setFont(new Font(fontName, Font.PLAIN, 15));
		genderLabel.			setFont(new Font(fontName, Font.PLAIN, 15));
		profileChange_button.	setFont(new Font("나눔고딕코딩", Font.PLAIN, 15));
		profileChange_button.setForeground(Color.white);
		
		profile_panel.setBackground(new Color(92, 209, 229));
		profileChange_button.setBackground(new Color(7, 129, 150));
		
		
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
		String fontName 	= "나눔고딕코딩";
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
		String fontName 	= "나눔고딕코딩";
		roomMake_button.setFont(new Font(fontName, Font.BOLD, 20));
		roomMake_button.setForeground(Color.BLACK);
		enterRoom_button.setFont(new Font(fontName, Font.BOLD, 20));
		enterRoom_button.setForeground(Color.BLACK);
		exit_button.setFont(new Font(fontName, Font.BOLD, 20));
		exit_button.setForeground(Color.BLACK);
		
		// 색깔
		buttonBar_panel.setBackground(new Color(92, 209, 229));
		roomMake_button.setBackground(new Color(250, 237, 125));
		enterRoom_button.setBackground(new Color(250, 237, 125));
		exit_button.setBackground(new Color(250, 237, 125));
		

		// 버튼 패널에 추가
		buttonBar_panel.add(roomMake_button);
		buttonBar_panel.add(enterRoom_button);
		buttonBar_panel.add(exit_button);
		
		

		
	}
	
    private void middle_alignment() {
		
// 		1. DefaultTableCellHeaderRenderer 생성 (가운데 정렬을 위한)
// 		2. DefaultTableCellHeaderRenderer의 정렬을 가운데 정렬로 지정
// 		3. 정렬할 테이블의 ColumnModel을 가져옴
//		4. 반복문을 이용하여 테이블을 가운데 정렬로 지정
		
		DefaultTableCellRenderer tScheduleCellRenderer = new DefaultTableCellRenderer();
		tScheduleCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel tcmSchedule = table.getColumnModel();
		for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
			tcmSchedule.getColumn(i).setCellRenderer(tScheduleCellRenderer);
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
					MainDAO.myRoomDelete(mvo.getId());
					System.exit(0);
				} else {
					try {
						e.wait();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			
			// 아래 빨간버튼 눌렀을 시(메인 쓰레드 강제 종료?)에도 로그아웃 되는 효과를 주려면 nowip를 공백으로 처리해야한다.
			// 이 부분은 아직 구현 못했다.
		
		});
		
		table.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				RoomVO rvo = new RoomVO();
				position = table.getSelectedRow();

				try {
					int roomNumber = Integer.parseInt((String) model.getValueAt(position, 0));
					rvo = MainDAO.selectByRoomnumber(roomNumber);

					if (e.getClickCount() == 2) {
//						table.setEnabled(false); // 이게 문제입니다. 이걸 살리면 다른 것들이 상당히 불안해짐. 이것 없이도 잘 돌아가도록 처리해둠_김태우 생각
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

	private void choice() {
		
		ArrayList<kr.koreait.chat.Room.RoomVO> list = RoomMakeDAO.select();
	
		for(int i = model.getRowCount() - 1; i >= 0; i--) {
			model.removeRow(i);
		}
		
		if(list.size() != 0) {
			String rowData[] = new String[6];
			
			for(kr.koreait.chat.Room.RoomVO data : list) {
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
	
	public static void view() {
		ArrayList<kr.koreait.chat.Room.RoomVO> list = RoomMakeDAO.select();
	
		for(int i = model.getRowCount() - 1; i >= 0; i--) {
			model.removeRow(i);
		}
		
		if(list.size() != 0) {
	
			String rowData[] = new String[6];
			for(kr.koreait.chat.Room.RoomVO data : list) {
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

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		
		case "프로필 변경" :
			if(MemberUpdateWindow.isWindowCreated == false) {
				MemberUpdateWindow settingWindow = new MemberUpdateWindow();
				MemberUpdateWindow.isWindowCreated = true;
				settingWindow.setLocation(this.getLocation().x-settingWindow.getWidth(), this.getLocation().y);
			}
			else {
				JOptionPane.showMessageDialog(null, "이미 만들어진 윈도우", "이미창", JOptionPane.WARNING_MESSAGE);
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
				JOptionPane.showMessageDialog(null, "이미 만들어진 윈도우", "이미창", JOptionPane.WARNING_MESSAGE);
			}
			view();
		break;
			
		case "입장하기":
			if(table.getSelectedRow() > -1) {
				
				// 선택한 행의 포지션을 받는다.
				// 그 행의 0번째 열에는 그 방의 방번호가 있다.
				// 그 방번호에 해당하는 rvo를 받아온다.
				// 들어가려는 방의 rvo와 들어가는 클라이언트의 mvo를 Thread_Ver_Client의 생성자로 넣어준다.
				// 여기 디버깅 해야한다.
				
				RoomVO rvo = new RoomVO();
				int position = table.getSelectedRow();
				int roomNumber = Integer.parseInt((String) model.getValueAt(position, 0));
				rvo = MainDAO.selectByRoomnumber(roomNumber);
				
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
				JOptionPane.showMessageDialog(null, "방을 선택하고 버튼을 눌러라");
			}
			
			view();
			break;
			
		case "로그아웃":
			int answer = JOptionPane.showConfirmDialog(null, "로그아웃 하시겠습니까?", "로그아웃", JOptionPane.OK_OPTION);
			if(answer == 0) {
				MemberDAO.nowIp_setLogout_when_logout(LoginWindow.getLoginMem());
				MainDAO.myRoomDelete(mvo.getId());
//				System.exit(0);
				dispose();
				LoginWindow window = new LoginWindow();
			}
			
		break;
			
		case "선택":
			choice();
			multiList.deselect(multiList.getSelectedIndex());
			break;
			
		case "모든 방":
			multiList.deselect(multiList.getSelectedIndex());
			view();
			break;
		}
	}
	

}
