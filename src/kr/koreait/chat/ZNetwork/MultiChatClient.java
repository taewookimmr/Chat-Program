package kr.koreait.chat.ZNetwork;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import kr.koreait.chat.Main.MainDAO;
import kr.koreait.chat.Main.MainWindow;
import kr.koreait.chat.Member.MemberDAO;
import kr.koreait.chat.Member.MemberVO;
import kr.koreait.chat.Room.RoomMakeDAO;
import kr.koreait.chat.Room.RoomMakeWindow;
import kr.koreait.chat.Room.RoomVO;
import kr.koreait.chat.Util.DBUtil;

public class MultiChatClient implements ActionListener, Runnable{

    public ChattingRoomWindow window;
    public JScrollBar sb;
    
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
		sb = window.conversation_scrollPane.getVerticalScrollBar();
		
		window.setVisible(false); // 방 접속 허용되면 보여주자
	}

	public void backEnd() {

		window.message_textfield.addActionListener(this);
		window.message_send_button.addActionListener(this);
		window.exit_button.addActionListener(this);
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				roomOutProcess();
			}

		});
		window.message_textfield.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {				
				if(e.getKeyCode() == 10) {
					sendMessage();
				}				
			}
		});
		
		window.getLayeredPane().addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {

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
		});
		
	}
	
	public void accessTrial_threadStart() {
		
		if(RoomMakeDAO.roomJoin(rvo.getRoomnumber(), mvo_me.getId())) {
			window.setVisible(true);
			
			try {
			    socket = new Socket(rvo.getServerip(), rvo.getServerLocalPort());
			    msg = rvo.getServerip() +"서버 "+ rvo.getServerLocalPort() +"번 포트로 접속 시도\n";
				msg += socket + " 접속 성공\n";
			
				window.conversation_textArea.setText(msg);
				window.message_textfield.requestFocus();
	
				sc = new Scanner(socket.getInputStream());
				pw = new PrintWriter(socket.getOutputStream());
				
				// 여기 중요하지, 굳이 데몬 쓰레드로 설정 안 해도 메인 윈도우가 꺼지면 채팅 윈도우도 꺼지네? 오케이
				Thread thread = new Thread(this);
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
	
			while(true) {
	
//				String str = sc.nextLine().trim();		
				StringBuffer str = new StringBuffer();
				str.append(sc.nextLine().trim());

				if(str.length() != 0) {
					msg += str + "\n\n";
					window.conversation_textArea.setText(msg);
					int value = sb.getMaximum();			
					sb.setValue(value);	// 화면 넘치면 스크롤 자동 아래고정
					
					//		추가
					RoomVO new_rvo = MainDAO.selectByRoomnumber(rvo.getRoomnumber());
					String title = "";
					title += "[방 제목 : " + new_rvo.getRoomname() + "]";
					title += "  [채널 : " + new_rvo.getChannel()+ "]";
					window.setTitle(new_rvo.getRoomname());
					window.roomName_label.setText(title);
					
				}
				renewal_participantList_in_yourChattingWindow(rvo.getRoomnumber());
				try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			}	

		} catch (Exception e) {
//			e.printStackTrace();
//			본인 퇴장 시 : 위 try 내부의 String str = sc.nextLine().trim(); 에서 Scanner closed 예외
//			방장 퇴장 시 : 위 try 내부의 String str = sc.nextLine().trim(); 에서 No line found  예외
//			그럼 본인이 방장으로서 퇴장 시? : 방장 퇴장 시의 오류가 같은 위치에서 뜸.
		} finally {
			
			boolean condition1 = socket.isClosed();
			boolean condition2 = (socket == null);
			
			if(condition1 || condition2) {
				
				// 오히려 여기가 본인이 나가는 곳이네, 그렇지 roomoutProcess 메서드에서 자기 socket을 꺼버린다!
				// 그렇다면 MCT 쪽의 couple socket은 그쪽 socketList에서 remove 제대로 되나 확인해야한다.
				// condition1은 true, condition2는 false
				
				JOptionPane.showMessageDialog(null, "본인이 퇴장합니다.");
				window.dispose();
				MainWindow.view(); // 이미 윈도우는 꺼져있지만 확인 차원에서
				
			} else {
				
				// 방장 퇴장하면서 실행되는 mct의 lists_renewal에서 참여자와 연결된 소켓(MCT쪽)을 다 close()해도
				// condition1은 true, condition2는 false
				// 이렇게 생각하면된다. MCT 쪽의 couple socket이 꺼져도 여기 socket은 안 꺼지지만
				// try 내부 String str = sc.nextLine().trim();에서 no line found 예외가 발생하여 여기로 오게 된다.
				
				JOptionPane.showMessageDialog(null, "방장이 퇴장하였습니다." );
				window.dispose();
				MainWindow.view();
				
			}
		}

	}

	private void sendMessage() {
		String str = window.message_textfield.getText().trim();
		
		if(str.length() > 0 ) {
			if(pw != null) {
				pw.write("["+ mvo_me.getId()+ "] >>> " +str + "\n");
				pw.flush();
				int value = sb.getMaximum();			
				sb.setValue(value);	// 화면 넘치면 스크롤 자동 아래고정
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
		RoomMakeDAO.roomOut(rvo.getRoomnumber(), mvo_me.getId());
		
		if(socket != null) { try { socket.close(); } catch (Exception e1) { e1.printStackTrace(); } }
		if(pw != null) 	   { try { pw.close(); } catch (Exception e1) { e1.printStackTrace(); } }
		if(sc != null)     { try { sc.close(); } catch (Exception e1) { e1.printStackTrace(); } }
		
		window.dispose(); 	
	}

	// 유재욱 조장님.userListSelect의 이름을 바꾸고 내부를 수정함. 이건 static 아님
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
			
			
			mvoList.clear(); // 이게 핵심이구나. 일단 다 비우고 채운다.
			
			
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
