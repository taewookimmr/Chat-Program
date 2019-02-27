package kr.koreait.chat.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import kr.koreait.chat.Room.RoomVO;
import kr.koreait.chat.Util.DBUtil;

public class MainDAO {
	
	// 채널 테이블을 만들어서 DB에서 가져오도록  만들기
	public static RoomVO selectByRoomnumber(int roomNumber) {
		RoomVO rvo = null;
		try {			
			Connection conn = DBUtil.getMysqlConnection();
			String sql = "select * from chatroom where roomnumber = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomNumber);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			rvo = new RoomVO();
			rvo.setRoomnumber(rs.getInt("roomnumber")); // 채팅방 리스트가 최신화되지 않은 상태에서 더블클릭하거나 입장버튼을 누르면 이 줄에서 오류가 생기고 catch로 넘어가게 된다.
			rvo.setRoomname(rs.getString("roomname"));
			rvo.setId(rs.getString("id"));
			rvo.setChannel(rs.getString("channel"));
			rvo.setNowmem(rs.getString("nowmem"));
			rvo.setMaxmem(rs.getString("maxmem"));
			rvo.setRoompw(rs.getString("roompw"));	
			rvo.setServerip(rs.getString("serverip"));
			rvo.serServerLocalPort(rs.getInt("serverlocalport"));
			
			DBUtil.close(conn);
			DBUtil.close(pstmt);
			DBUtil.close(rs);
			
		} catch (SQLException e) {
//			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "해당 방은 종료되었습니다. 목록을 최신화합니다.");
			MainWindow.view();
		} 
		
		return rvo;
	}
	
    //	내가만든방 삭제
	public static void myRoomDelete(String id) {
		try {			
			Connection conn = DBUtil.getMysqlConnection();
			String sql = "delete from chatroom where id = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.executeUpdate();
			
			DBUtil.close(conn);
			DBUtil.close(pstmt);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
}



