package kr.koreait.chat.Room;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import kr.koreait.chat.Initiator.LoginWindow;
import kr.koreait.chat.Main.MainDAO;
import kr.koreait.chat.Main.MainWindow;
import kr.koreait.chat.Util.DBUtil;

public class RoomMakeDAO {
	
	public static boolean chat_insert(RoomVO rvo) {
		
		String privateChoice = RoomMakeWindow.privateChoice;
		
		boolean flag = true;
			if(rvo.getRoomname().length() == 0) {
				JOptionPane.showMessageDialog(null, "채팅방 제목을 입력하세요.", "채팅방 제목없음", JOptionPane.WARNING_MESSAGE);
				flag = false;
			}else if(rvo.getPrivateChoice() == null) {
				JOptionPane.showMessageDialog(null, "공개여부가 선택되지 않았습니다.", "공개여부 없음", JOptionPane.WARNING_MESSAGE);			
				flag = false;
			}else if(privateChoice.equals("비공개 : 비밀번호")){
				if(rvo.getRoompw().length() == 0){
					JOptionPane.showMessageDialog(null, "비밀번호가 입력되지 않았습니다.", "패스워드없음", JOptionPane.WARNING_MESSAGE);			
					flag = false;
			}
			
		}
		
		if(flag) {
			try {
				Connection conn = DBUtil.getMysqlConnection();			// 방 번호는 자동증가
				
				String sql = "insert into chatroom(roomname, id, nowmem, maxmem, roompw, serverip, channel, serverlocalport) values (?, ? ,?, ? ,?, ? ,?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, rvo.getRoomname());													//roomname char
				pstmt.setString(2, LoginWindow.getLoginMem().getId());									//id char
				pstmt.setInt(3 , 0); // 1말고 0으로 해야한다. 그 이유는	roomJoin 메서드에서 에서 방장이 이중 카운트 되기 때문	      				//nowmem int
				pstmt.setInt(4, Integer.parseInt(rvo.getMaxmem()));										//maxmem int
				pstmt.setString(5, rvo.getRoompw());													//roompw char
				pstmt.setString(6, rvo.getServerip());													//serverip char
				pstmt.setString(7, rvo.getChannel());													//channel char
				pstmt.setInt(8, rvo.getServerLocalPort());
				pstmt.executeUpdate();
				
				DBUtil.close(conn);
				DBUtil.close(pstmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			JOptionPane.showMessageDialog(null, "입력완료");
		}	
		return flag;
	}
	
	public static ArrayList<RoomVO> select() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<RoomVO> list = null;
		
		try {			
			conn = DBUtil.getMysqlConnection();
			String sql = "select * from chatroom order by roomnumber desc";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			list = new ArrayList<>();
			while(rs.next()) {
				RoomVO vo = new RoomVO();
				vo.setRoomnumber(rs.getInt("roomnumber"));
				vo.setRoomname(rs.getString("roomname"));
				vo.setId(rs.getString("id"));
				vo.setChannel(rs.getString("channel"));
				vo.setNowmem(rs.getString("nowmem"));
				vo.setMaxmem(rs.getString("maxmem"));
				vo.setRoompw((rs.getString("roompw")).equals("")?"공개":"비공개");
				list.add(vo);				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(conn);
			DBUtil.close(pstmt);
			DBUtil.close(rs);
		}
		return list;
	}

	
	// 여기에 채팅방 중복입장방지 기능이 있다.
	// 방지기능을 풀려면 return 값을 무조건 true로만 반환한다. 
	public static boolean roomJoin(int roomNumber, String id) {

		String idCheck = "";
		int roomNumberCheck = -1;
		boolean welcome = true;
		
		try {
			Connection conn = DBUtil.getMysqlConnection();			
			String sql = "select * from roommember where roomnumber = ? && id = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomNumber);
			pstmt.setString(2, id);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				idCheck = rs.getString("id");
				roomNumberCheck = rs.getShort("roomnumber");
				if(id.equals(idCheck) && (roomNumber == roomNumberCheck)) {welcome = false; break;}
				else {welcome = true;}
			}

			if(welcome) {
				sql = "insert into roommember(roomnumber, id) values (?, ?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, roomNumber);
				pstmt.setString(2, id);
				pstmt.executeUpdate();
				
				sql = "update chatroom set nowmem = nowmem + 1 where roomnumber = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, roomNumber);
				pstmt.executeUpdate();
				
				DBUtil.close(conn);
				DBUtil.close(pstmt);
				DBUtil.close(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		MainWindow.view();
		
		if(welcome) {return true;   }
		else 		{return false;	}
	}

	// 이건 디비와 관련된 것이다. 서버 쓰래드나, 클아이언트 쓰래드는 따로 꺼줘야한다
	// 방장이 방을 나가면, chatroom 디비에서 해당 방 삭제, roommember 디비에서 해당 방에 해당하는 리스트? 다 삭제
	// 일반인?이 방을 나가면, chatroom 디비에서 해당 방의 현 인원 수를 1 감소, roommember 디비에서
	// (해당 방, 해당 아이디) 행?을 삭제 
	public static void roomOut(int roomNumber, String id) {
		
		String idCheck = "";
		int roomNumberCheck = -1;
		boolean isRoomKing = false;
		boolean goodBye = true;
		
		try {
			// 우선 방장인지 확인
			Connection conn = DBUtil.getMysqlConnection();			
			String sql = "select * from chatroom where roomnumber = ?" ;
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomNumber);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				if(id.equals(rs.getString("id"))) {
					isRoomKing = true; 
					break;
				}
			}
			
			conn = DBUtil.getMysqlConnection();			
			sql = "select * from roommember where roomnumber = ? && id = ?";
		    pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomNumber);
			pstmt.setString(2, id);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				idCheck = rs.getString("id");
				roomNumberCheck = rs.getShort("roomnumber");
				if(id.equals(idCheck) && (roomNumber == roomNumberCheck)) {goodBye = true; break;}
				else {goodBye = false;}
			}

			if(goodBye) {
				if(isRoomKing) {
					sql = "delete from roommember where roomnumber = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, roomNumber);
					pstmt.executeUpdate();
					
					sql = "delete from chatroom where roomnumber = ? ";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, roomNumber);
					pstmt.executeUpdate();
				}
				else {
					sql = "delete from roommember where roomnumber = ? && id = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, roomNumber);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
					
					sql = "update chatroom set nowmem = nowmem-1 where roomnumber = ? ";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, roomNumber);
					pstmt.executeUpdate();
				}
			}else {
				JOptionPane.showMessageDialog(null, "채팅방 퇴장하며 디비수정 실패");
			}
			
			DBUtil.close(conn);
			DBUtil.close(pstmt);
			DBUtil.close(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		MainWindow.view();

	
	}
	
	public static int getRoomNumberFromDB(RoomVO rvo) {
		int roomNumber = -1;	// 해당방이 없으면 -1을 반환
		try {
			Connection conn = DBUtil.getMysqlConnection();			
			String sql = "select roomnumber from chatroom where  id = ? && serverlocalport = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, rvo.getId());
			pstmt.setInt(2, rvo.getServerLocalPort());
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				roomNumber = rs.getInt("roomnumber");
			}
			DBUtil.close(conn);
			DBUtil.close(pstmt);
			DBUtil.close(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roomNumber;
	}
	
	public static boolean chat_room_update(RoomVO rvo) {
		
		String privateChoice = RoomUpdateWindow.privateChoice;
		boolean flag = true;
		int nowMem = 0;
		String roomName = "";
		try {
			Connection conn = DBUtil.getMysqlConnection();
			ResultSet rs = null;
			
			String sql = "select nowmem from chatroom where roomnumber = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, rvo.getRoomnumber());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				nowMem = rs.getInt("nowmem");
			}
			
			DBUtil.close(conn);
			DBUtil.close(pstmt);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
						
		
		if(rvo.getRoomname().length() == 0) {
			JOptionPane.showMessageDialog(null, "채팅방 제목을 입력하세요.", "채팅방 제목없음", JOptionPane.WARNING_MESSAGE);
			flag = false;
		} else if(privateChoice == null) {
			JOptionPane.showMessageDialog(null, "공개여부가 선택되지 않았습니다.", "공개여부 없음", JOptionPane.WARNING_MESSAGE);			
			flag = false;
		}else if(privateChoice.equals("비공개 : 비밀번호")){
			if(rvo.getRoompw().length() == 0){
				JOptionPane.showMessageDialog(null, "비밀번호가 입력되지 않았습니다.", "패스워드없음", JOptionPane.WARNING_MESSAGE);			
				flag = false;
			}			
		}else if( nowMem > Integer.parseInt(rvo.getMaxmem())) {			
			JOptionPane.showMessageDialog(null, "최대인원은 현재 인원보다 적을 수 없습니다.", "참여인원 오류", JOptionPane.WARNING_MESSAGE);			
			flag = false;
		}
		
		if(flag) {
			try {
				Connection conn = DBUtil.getMysqlConnection();			// 방 번호는 자동증가
				
//				생성에서 업데이트로 sql 변경
				
//				String sql = "insert into chatroom(roomname, id, nowmem, maxmem, roompw, serverip, channel, serverportnum) values (?, ? ,?, ? ,?, ? ,?, ?)";
				String sql = "update chatroom set roomname = ?, maxmem = ?, roompw = ? where roomnumber = ?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, rvo.getRoomname());									
				pstmt.setInt(2, Integer.parseInt(rvo.getMaxmem()));							
				pstmt.setString(3, rvo.getRoompw());				
				pstmt.setInt(4, rvo.getRoomnumber());												
				pstmt.executeUpdate();
				
				DBUtil.close(conn);
				DBUtil.close(pstmt);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			JOptionPane.showMessageDialog(null, "채팅방 수정 완료!!");
		}	
		
		return flag;
	}


}
