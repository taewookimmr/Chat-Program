package org.whilescape.chat.Window;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.whilescape.chat.DTO.MemberVO;
import org.whilescape.chat.DTO.RoomVO;
import org.whilescape.chat.WindowRes.Resource;

public class ChattingRoomWindow extends JFrame {
	
	
	///////////////////////////////////// 멤버 객체 영역 시작 /////////////////////////////////////////
	
	public static final int BASIC_STYLE   = 0;    // non-말풍선형 대화 방식 선택
	public static final int BALLOON_STYLE = 1;    // 말풍선형 대화 방식 선택
	public int messageStyle = BALLOON_STYLE;      // 대화 방식 선택 변수
	

	public RoomVO rvo;   // 생성된 채팅방의 정보를 담고 있는 변수
	public MemberVO mvo; // 생성된 채팅방의 방장의 mvo
	

	/* 상단 패널, 설정 버튼과 나가기 버튼 위치 */
	public JPanel	  	northBar_panel, exit_button_panel;
	public JButton	 	setting_button, exit_button;
	public JLabel     	roomName_label;
	
    /*  채팅 내용이 보여지는 Viewport를 구성하는 부분 */
	
	public JPanel       conversation_outter_panel; // 최외각 틀에 해당.
	
	// conversation_inner_panel:  BALLOON_STYLE 모드인 경우, conversation_outter_panel위에 올라가고, 실질적으로 말풍선들이 add되는 panel
	public JPanel       conversation_inner_panel; 
	// conversation_textArea:  BASIC_STYLE 모드인 경우, conversation_outter_panel위에 올라감. 일반적인 줄 글.
	public JTextArea    conversation_textArea;
	
	public JScrollPane 	conversation_scrollPane;
    public JScrollBar   scrollBar;
	public JPanel	    message_panel;
	public JTextField 	message_textfield;
	public JButton	 	message_send_button;
	

	 /*  채팅방에 참여한 인원 정보를 보여주는 페널 */
	public JPanel       east_panel, participant_panel;
	public JLabel       participant_label;
	
	
	public  String columnNames[] = {"번호", "아이디", "성별", "나이"};
	
	public  DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
		@Override
		public boolean isCellEditable(int row, int column) { // table 내용 수정 불가
			return false;
		}
	};
	
	public  JTable participant_table = new JTable(model);
	
	
    // 말풍선 대화 방식 선택 시, 필요한 요소들
	public ArrayList<JPanel> balloons = new ArrayList<>();
	public int 			bottom_line     = 20;
	public int 			interval_s    = 2;  // 말풍선과 아이디풍선 사이의 간격
	public int          interval_m    = 20; // 말풍선과 말풍선 사이의 간격
	
	
    // 색
	private Color main_bg_color 	= Resource.main_bg_color;  
	private Color main_text_color 	= Color.BLACK;
	private Color button_color      = Resource.main_btn_color;
	
	///////////////////////////////////// 멤버 객체 영역 끝 /////////////////////////////////////////
	
	public ChattingRoomWindow(RoomVO rvo, MemberVO mvo) {
		frontEnd();
		this.rvo = rvo;
		this.mvo = mvo;
	}


	public void frontEnd() {
		setTitle("채팅방");
		setBounds(400, 200, 800, 500);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);

		JLayeredPane layeredPane = new JLayeredPane();
		int adjust = 0;   
		if(!isResizable()) { adjust = 10;}
		layeredPane.setBounds(0, 0, this.getSize().width - 16 + adjust, this.getSize().height - 39 + adjust); 
		layeredPane.setLayout(null);
		
		this.add(layeredPane);
		setVisible(true); 
		
		add_northBar_on(layeredPane);

		add_conversation_outter_panel_on(layeredPane);
		
		add_message_panel_on(layeredPane);
		
		add_east_panel_on(layeredPane);
		

	}
	
	public void add_northBar_on(JLayeredPane layeredPane) {
		String fontName = Resource.mainFontType;
		northBar_panel = new JPanel();
		northBar_panel.setLayout(null);
		northBar_panel.setSize(layeredPane.getWidth() * 70/100, layeredPane.getHeight() * 10/100);
		northBar_panel.setLocation(0, 0);
		northBar_panel.setBackground(main_bg_color);
		layeredPane.add(northBar_panel);
	
		roomName_label = new JLabel("방 제목 :  / 채널 : " );
		roomName_label.setSize(northBar_panel.getWidth() * 85/100, northBar_panel.getHeight() * 90/100);
		roomName_label.setLocation(northBar_panel.getWidth() * 1/100, (northBar_panel.getHeight()-roomName_label.getHeight())/2);
		roomName_label.setFont(new Font(fontName, Font.BOLD, 20));
		northBar_panel.add(roomName_label);
		
		setting_button = new JButton("설정");
		setting_button.setSize(northBar_panel.getWidth() * 12/90, northBar_panel.getHeight() * 80/130);
		setting_button.setLocation(northBar_panel.getWidth() * 1/100 + roomName_label.getWidth() + northBar_panel.getWidth() * 1/100, (northBar_panel.getHeight()-setting_button.getHeight())/2);
		setting_button.setFont(new Font(fontName, Font.BOLD, 20));
		setting_button.setBackground(button_color);
		northBar_panel.add(setting_button);
		

		

	}
	
	public void add_conversation_outter_panel_on(JLayeredPane layeredPane) {
		
		String fontName =  Resource.mainFontType;
		
		conversation_outter_panel = new JPanel();
		conversation_outter_panel.setSize(layeredPane.getWidth() * 70/100, layeredPane.getHeight() * 80/100);
		conversation_outter_panel.setLocation(0, northBar_panel.getHeight());
		conversation_outter_panel.setBackground(main_bg_color);
		conversation_outter_panel.setFont(new Font(fontName, Font.PLAIN, 30));
		layeredPane.add(conversation_outter_panel);
		
		conversation_outter_panel.setLayout(null);
		
		add_conversation_scrollPane_on(conversation_outter_panel);

	}

	public void add_conversation_scrollPane_on(JPanel addOnThisPanel) {
		
		String fontName =  Resource.mainFontType;
		
		
		conversation_scrollPane = new JScrollPane(); // 초기화 먼저 실시
		
		switch(messageStyle) {
		
		case BASIC_STYLE :
			conversation_textArea = new JTextArea();
			conversation_textArea.setBackground(Color.WHITE);
			conversation_textArea.setFont(new Font(fontName, Font.PLAIN, 20));
			conversation_textArea.setEditable(false);
			conversation_scrollPane = new JScrollPane(conversation_textArea);
		
		break;
				
		case BALLOON_STYLE :
			conversation_inner_panel = new JPanel();
			conversation_inner_panel.setLayout(null);
			conversation_inner_panel.setPreferredSize(new Dimension(addOnThisPanel.getWidth() * 60/100, addOnThisPanel.getHeight()*90/100));
			conversation_inner_panel.setBorder(new LineBorder(Color.RED, 2));
			conversation_scrollPane = new JScrollPane(conversation_inner_panel,  JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			

		break;
			
		}
		
		conversation_scrollPane.setSize(addOnThisPanel.getWidth() * 98/100, addOnThisPanel.getHeight()*95/100);
		conversation_scrollPane.setLocation((addOnThisPanel.getWidth() - conversation_scrollPane.getWidth())/2, 10);
		conversation_scrollPane.getVerticalScrollBar().setValue(0);
		
		scrollBar = conversation_scrollPane.getVerticalScrollBar();
		
		addOnThisPanel.add(conversation_scrollPane);
	}

	public void add_message_panel_on(JLayeredPane layeredPane) {
		String fontName =  Resource.mainFontType;
		message_panel     = new JPanel();
		message_panel.setLayout(null);
		message_panel.setSize(layeredPane.getWidth() * 70/100, layeredPane.getHeight() * 10/100 + 4);
		message_panel.setLocation(0, northBar_panel.getHeight() + conversation_outter_panel.getHeight());
		message_panel.setBackground(main_bg_color);
		layeredPane.add(message_panel);
		
		message_textfield  = new JTextField();
		message_textfield.setSize(message_panel.getWidth() * 80/100, message_panel.getHeight() * 80/100);
		message_textfield.setLocation(message_panel.getWidth() * 1/100, (message_panel.getHeight()-message_textfield.getHeight())/2);
		message_textfield.setFont(new Font(fontName, Font.PLAIN, 20));
		message_panel.add(message_textfield);
		
		message_send_button  = new JButton("전송");
		message_send_button.setSize(message_panel.getWidth() * 17/94, message_panel.getHeight() * 80/100);
		message_send_button.setLocation(message_panel.getWidth() * 1/100 + message_textfield.getWidth() + message_panel.getWidth() * 1/100, (message_panel.getHeight()-message_textfield.getHeight())/2);
		message_send_button.setFont(new Font(fontName, Font.BOLD, 20));
		message_send_button.setBackground(button_color);
		message_panel.add(message_send_button);
		
		message_textfield.requestFocus();
	}

	public void add_east_panel_on(JLayeredPane layeredPane) {
		east_panel     = new JPanel();
		east_panel.setLayout(null);
		east_panel.setSize(layeredPane.getWidth() * 30/100, layeredPane.getHeight() * 100/100);
		east_panel.setLocation(northBar_panel.getWidth(), 0);
		east_panel.setBackground(main_bg_color);
		layeredPane.add(east_panel);
		
		add_exit_button_panel_on(east_panel);
		
		add_party_label_on(east_panel);
		
		add_party_panel_on(east_panel);
	}
	
	public void add_exit_button_panel_on(JPanel panel) {
		
		exit_button_panel  = new JPanel();
		exit_button_panel.setLayout(null);
		exit_button_panel.setSize(panel.getWidth() * 100/100, panel.getHeight() * 10/100);
		exit_button_panel.setLocation((east_panel.getWidth()-exit_button_panel.getWidth())/2, 0);
		exit_button_panel.setBackground(main_bg_color);
		panel.add(exit_button_panel);
		

		exit_button = new JButton("채팅방 나가기");
		exit_button.setSize(exit_button_panel.getWidth()*90/110, exit_button_panel.getHeight()*80/130);
		exit_button.setLocation((exit_button_panel.getWidth()-exit_button.getWidth())/2,
				(exit_button_panel.getHeight()-exit_button.getHeight())/2);
		exit_button_panel.add(exit_button);
		
		String fontName =  Resource.mainFontType;
		exit_button.setFont(new Font(fontName, Font.BOLD, 20));
		exit_button.setBackground(button_color);
		
	}

	public void add_party_label_on(JPanel panel) {
		participant_label     = new JLabel("참가자 목록");
		participant_label.setLayout(null);
		participant_label.setSize(panel.getWidth() * 90/100, panel.getHeight() * 10/100);
		participant_label.setLocation((panel.getWidth()-participant_label.getWidth())/2, exit_button_panel.getLocation().y + exit_button_panel.getHeight());
		participant_label.setBackground(main_bg_color);
		panel.add(participant_label);
		
		String fontName =  Resource.mainFontType;
		participant_label.setFont(new Font(fontName, Font.BOLD, 30));
		participant_label.setHorizontalAlignment(JLabel.CENTER);
		
		
	}
	
    public void add_party_panel_on(JPanel panel) {
    	
    	participant_panel  = new JPanel();
		participant_panel.setLayout(null);
		participant_panel.setSize(panel.getWidth() * 90/100, panel.getHeight() * 75/92);
		participant_panel.setLocation((east_panel.getWidth()-participant_panel.getWidth())/2
				, participant_label.getLocation().y + participant_label.getHeight() - 7);
		participant_panel.setBackground(main_bg_color);
		panel.add(participant_panel);
		
		participant_table.setRowHeight(25);

		middle_alignment();
		
		JScrollPane jsp = new JScrollPane(participant_table);
		jsp.setSize(participant_panel.getWidth()-20, participant_panel.getHeight() * 85/100);
		jsp.setLocation((participant_panel.getWidth()-jsp.getWidth())/2, 10);
		participant_panel.add(jsp);
		
	}
   

    private void middle_alignment() {
				
		DefaultTableCellRenderer tScheduleCellRenderer = new DefaultTableCellRenderer();
		tScheduleCellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		TableColumnModel tcmSchedule = participant_table.getColumnModel();
		for (int i = 0; i < tcmSchedule.getColumnCount(); i++) {
			tcmSchedule.getColumn(i).setCellRenderer(tScheduleCellRenderer);
		}
	}
	
    // 테스트용 메인
	public static void main(String[] args) {
		ChattingRoomWindow window = new ChattingRoomWindow(new RoomVO(), new MemberVO());
	}



}
