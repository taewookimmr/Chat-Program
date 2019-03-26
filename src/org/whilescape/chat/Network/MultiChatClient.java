package org.whilescape.chat.Network;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.whilescape.chat.DAO.DBUtil;
import org.whilescape.chat.DAO.MemberDAO;
import org.whilescape.chat.DAO.RoomDAO;
import org.whilescape.chat.DTO.MemberVO;
import org.whilescape.chat.DTO.RoomVO;
import org.whilescape.chat.Window.ChattingRoomWindow;
import org.whilescape.chat.Window.MainWindow;
import org.whilescape.chat.Window.RoomSettingWindow;
import org.whilescape.chat.WindowRes.BalloonPanel;
import org.whilescape.chat.WindowRes.Resource;

public class MultiChatClient implements ActionListener, Runnable{

    public ChattingRoomWindow window;

	public Socket socket;
	public Scanner sc;
	public PrintWriter pw;
    public String msg ="";
   
    public RoomVO rvo;
    public MemberVO mvo_me;
    public List<MemberVO>    mvoList    = Collections.synchronizedList(new ArrayList<MemberVO>());
    
	public MultiChatClient(RoomVO rvo, MemberVO mvo) {

		this.rvo = rvo;
		this.mvo_me = mvo;
		
		frontEnd();
		backEnd();
		accessTrial_threadStart();
		
	}
	
	public void frontEnd() {
		window = new ChattingRoomWindow(rvo, mvo_me);
		String str = "";
		str += "[방 제목 : " + rvo.getRoomname() + "]";
		str += "[방장 아이디 : " + rvo.getId() + "]";

		window.setTitle(rvo.getRoomname());
		window.roomName_label.setText(str);
	
		window.setVisible(false); // 방 접속이 허용되기 전에는 invisible
	}

	public void backEnd() {

		window.message_send_button.addActionListener(this);
		window.exit_button.		   addActionListener(this);

		window.setting_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
											
				if(window.mvo.getId().equals(rvo.getId())) {		// MemberVO ID와 RoomVO ID가 일치하면 방장
//				방장인 경우에만 설정 창이 실행된다.
					if(RoomSettingWindow.isWindowCreated == false) {
						RoomSettingWindow roomUpdateWindow = new RoomSettingWindow(rvo);
						RoomSettingWindow.isWindowCreated = true;

					}
					else {
						JOptionPane.showMessageDialog(null, "이미 만들어진 윈도우입니다.", "오류", JOptionPane.WARNING_MESSAGE);
					}	
				}else {
//				방장이 아닌 경우 설정 창 실행 불가
					JOptionPane.showMessageDialog(null, "권한이 없습니다.", "수정불가", JOptionPane.WARNING_MESSAGE);	
				}
			}
		});
		
		window.message_textfield.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {				
				if(e.getKeyCode() == KeyEvent.VK_ENTER) { // enter key를 누르면 메시지를 전달한다.
					sendMessage();
				}				
			}
		});
		
		window.getLayeredPane().addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {

				if(window.messageStyle == window.BASIC_STYLE) {
					
					Font   presentFont 		= window.conversation_textArea.getFont();
					String presentFontName 	= presentFont.getFontName();
					int    presentFontSize 	= presentFont.getSize();
					int    presentFontStyle = presentFont.getStyle();
					
					switch(e.getWheelRotation()) {
					
					case 1  :  
						if(presentFontSize > 10) presentFontSize -= 1; 
						window.conversation_textArea.setFont(new Font(presentFontName, presentFontStyle, presentFontSize));
					break;
					
					
					case -1 :  
						if(presentFontSize < 100) presentFontSize += 1; 
						window.conversation_textArea.setFont(new Font(presentFontName, presentFontStyle, presentFontSize));
					break;
					
					}
				}
			
			}
		});
		
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				roomOutProcess();
			}

		});
	
	
	
	}
	
	public void accessTrial_threadStart() {
		
		if(RoomDAO.roomJoin(rvo.getRoomnumber(), mvo_me.getId())) {

			try {
			    socket = new Socket(rvo.getServerip(), rvo.getServerLocalPort());
			    msg = rvo.getServerip() +"서버 "+ rvo.getServerLocalPort() +"번 포트로 접속 시도\n";
				msg += socket + " 접속 성공\n";
				window.setVisible(true); // 접속 성송하였다면 채팅방이 visible하게 설정
			
				switch(window.messageStyle) {
					case  ChattingRoomWindow.BASIC_STYLE:
						window.conversation_textArea.setText(msg);
						window.message_textfield.requestFocus();
						window.scrollBar.setValue(window.scrollBar.getMaximum());	
					break;
					
					case  ChattingRoomWindow.BALLOON_STYLE:
						
						Font font = new Font(Resource.mainFontType, Font.PLAIN, 15);
						Color bgColor = Color.ORANGE;
						BalloonPanel panel = new BalloonPanel(msg, font, bgColor, window.conversation_inner_panel.getWidth());
						panel.setLocation(10, window.bottom_line);
						window.bottom_line +=  panel.getHeight() + window.interval_m;
						
						window.conversation_inner_panel.add(panel);	// 실질적으로 메시지를 띄우는 동작	
						window.balloons.add(panel); 
						window.scrollBar.setValue(window.scrollBar.getMaximum());	
						
					break;
				}
		
				sc = new Scanner(socket.getInputStream());
				pw = new PrintWriter(socket.getOutputStream());
				
				Thread thread = new Thread(this);
				thread.setDaemon(true);
				thread.start();
			
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}else {
			JOptionPane.showMessageDialog(null, "이미 같은 방에 접속중입니다.");
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	switch(e.getActionCommand()) {
		case "전송" :
			sendMessage();
		break;
		
		case "채팅방 나가기":
			roomOutProcess();
		break;
	}

	
	}
	
	@Override
	public void run() {
	
		try {
			// while 반복문을 사용하는 이유
			// 채팅방에 있는 동안 실시간으로 서버와 연결되어 있어야 하기 때문.
			
			while(true) {
				StringBuffer str = new StringBuffer();
				str.append(sc.nextLine().trim());

				if(str.length() != 0) {
					
					msg += str + "\n\n";
					switch(window.messageStyle) {
					
					case ChattingRoomWindow.BASIC_STYLE:
						window.conversation_textArea.setText(msg);
						window.scrollBar.setValue(window.scrollBar.getMaximum());	
					break;
						
					case ChattingRoomWindow.BALLOON_STYLE:
						
						window.conversation_inner_panel.repaint();
					
						String temp = str.toString();
						
						int start = temp.indexOf("[");
						int end   = temp.indexOf("]");
						
						// parsing을 하여 id부와 message부를 얻어낸다.
						String id   = temp.substring(start+1, end);
						String text = temp.substring(end+1, temp.length());
						
						
						Font 	i_font = new Font(Resource.mainFontType, Font.PLAIN, 10);
						Color 	i_bgColor = Color.YELLOW;
						Font 	b_font = new Font(Resource.mainFontType, Font.PLAIN, 15);
						Color 	b_bgColor = Color.ORANGE;
						
						BalloonPanel iPanel = new BalloonPanel(id, i_font, i_bgColor, window.conversation_inner_panel.getWidth());
						BalloonPanel bPanel = new BalloonPanel(text, b_font, b_bgColor, window.conversation_inner_panel.getWidth());
						
						
						int x = 0; // 말풍선의 x좌표값
						if(id.equals(mvo_me.getId())) {
							x = window.conversation_inner_panel.getWidth() - (bPanel.getWidth() + 10);
							// 본인이 쓴 글이면 우측에 위치시킨다.
						}else {
							x = 10; // 상대방이 쓴 글이면 좌측에 위치시킨다.
						}

						iPanel.setLocation(x, window.bottom_line);
						window.bottom_line +=  iPanel.getHeight() + window.interval_s;
						window.conversation_inner_panel.setPreferredSize(new Dimension(
								window.conversation_inner_panel.getWidth(), 
								window.bottom_line  + window.interval_m*2)
						);
						window.conversation_inner_panel.add(iPanel); // id풍선을 실질적으로 그린다.
						window.conversation_inner_panel.repaint();
						
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
			
						bPanel.setLocation(x, window.bottom_line);
						window.bottom_line +=  bPanel.getHeight() + window.interval_m;
						window.conversation_inner_panel.setPreferredSize(new Dimension(
								window.conversation_inner_panel.getWidth(), 
								window.bottom_line  + window.interval_m*2)
						);
						window.conversation_inner_panel.add(bPanel); // 메시지 풍선을 실질적으로 그린다.
						window.conversation_inner_panel.repaint();
						
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
				
						
						window.balloons.add(bPanel); // 모든 대화들을 저장하는 용도
											
						window.scrollBar.setValue(window.scrollBar.getMaximum());
						window.conversation_inner_panel.repaint();
					break;
					}
		
					// 방 제목 변경된 것 반영되도록 하는 부분
					RoomVO new_rvo = RoomDAO.selectByRoomnumber(rvo.getRoomnumber());
					String title = "";
					title += "[방 제목 : " + new_rvo.getRoomname() + "]";
					title += "[채널 : " + new_rvo.getChannel()+ "]";
					window.setTitle(new_rvo.getRoomname());
					window.roomName_label.setText(title);
					
				}
				renewal_participantList_in_yourChattingWindow(rvo.getRoomnumber());
				try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			}	

		} catch (Exception e) {
				//	e.printStackTrace();
				//	본인 퇴장 시 : 위 try 내부의 String str = sc.nextLine().trim(); 에서 Scanner closed 예외
				//	방장 퇴장 시 : 위 try 내부의 String str = sc.nextLine().trim(); 에서 No line found  예외
				//	그럼 본인이 방장으로서 퇴장 시? : 방장 퇴장 시의 오류가 같은 위치에서 뜸.
			
			boolean condition1 = socket.isClosed();
			boolean condition2 = (socket == null);
			
			if(condition1 || condition2) {
				// 오히려 여기가 본인이 나가는 곳이네, 그렇지 roomoutProcess 메서드에서 자기 socket을 꺼버린다!
				// 그렇다면 MCT 쪽의 couple socket은 그쪽 socketList에서 remove 제대로 되나 확인해야한다.
				// condition1은 true, condition2는 false
				JOptionPane.showMessageDialog(null, "본인이 퇴장합니다.");
	
			} else {
				// 방장 퇴장하면서 실행되는 mct의 lists_renewal에서 참여자와 연결된 소켓(MCT쪽)을 다 close()해도
				// condition1은 false, condition2는 false
				// 이렇게 생각하면된다. MCT 쪽의 couple socket이 꺼져도 여기 socket은 안 꺼지지만
				// try 내부 String str = sc.nextLine().trim();에서 no line found 예외가 발생하여 여기로 오게 된다.
				JOptionPane.showMessageDialog(null, "방장이 퇴장하였습니다." );
			}
		} finally {
			window.dispose();
			MainWindow.view(); 
		}

	}

	private void sendMessage() {
		String str = window.message_textfield.getText().trim();
		
		if(str.length() > 0 ) {
			if(pw != null) {
				pw.write("["+ mvo_me.getId()+ "]" +str + "\n");
				pw.flush();	
				window.scrollBar.setValue(window.scrollBar.getMaximum());	// 화면 넘치면 스크롤 자동 아래고정
			}
		}
		
		window.message_textfield.setText("");
		window.message_textfield.requestFocus();
		
	}			
    
	private void roomOutProcess() {
		
		// 일단 나 방에서 나간다고 보냄
		if(pw != null) {
			String str = "";
			str += "##########";
			str +="["+ mvo_me.getId()+ "]님이 방을 나갔습니다";
			str += "##########\n";
			pw.write(str + "\n");
			pw.flush();
		}
		
		// DB, 방과 관련된 DB를 업데이트 함. (방장이냐 아니냐에 따라 처리가 달라짐)
		RoomDAO.roomOut(rvo.getRoomnumber(), mvo_me.getId());
		
		if(socket != null) { try { socket.close(); } catch (Exception e1) { e1.printStackTrace(); } }
		if(pw != null) 	   { try { pw.close(); } catch (Exception e1) { e1.printStackTrace(); } }
		if(sc != null)     { try { sc.close(); } catch (Exception e1) { e1.printStackTrace(); } }
		
		window.dispose(); 	
	}

	private void renewal_participantList_in_yourChattingWindow(int roomnumber) {
		ArrayList<String> userList = new ArrayList<>();
		
		try {
			Connection conn = DBUtil.getMysqlConnection();
			String sql = "select id from roommember where roomnumber = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomnumber);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				boolean flag = true;
				String id = rs.getString("id");
				for(String str : userList) {
					if(str.equals(id)) {
					  flag = false;
					  break;
					}
				}
				if(flag) {userList.add(id);}
			}

			DBUtil.close(conn);
			DBUtil.close(pstmt);
			DBUtil.close(rs);
			
			
			mvoList.clear(); // 이게 핵심이다. 일단 다 비우고 채운다.
			
			
			for(int i =0; i < userList.size(); i++) {
				MemberVO mvo_temp = new MemberVO();
				mvo_temp.setId(userList.get(i));
				mvoList.add(MemberDAO.getMemberProfile(mvo_temp));
			}
			
			for(int i = window.model.getRowCount() - 1; i >= 0; i--) {
				window.model.removeRow(i);
			}
			
			String rowData[] = new String[4];
			int j = 1;
			for(MemberVO mvo : mvoList) {
				rowData[0] = j++ + "";
				rowData[1] = mvo.getId();
				rowData[2] = mvo.getGender();
				rowData[3] = mvo.getAge() + "";
				
				window.model.addRow(rowData);	
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
