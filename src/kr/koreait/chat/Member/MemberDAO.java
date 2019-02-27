package kr.koreait.chat.Member;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import kr.koreait.chat.Initiator.LoginWindow;
import kr.koreait.chat.Util.DBUtil;

public class MemberDAO {
	
	
	static public boolean member_insert(MemberVO vo) {
		boolean flag = true;
		if(vo.getId().length() == 0) {
			JOptionPane.showMessageDialog(null, "아이디가 입력되지 않았습니다.", "아이디없음", JOptionPane.WARNING_MESSAGE);
			flag = false;
			return flag;
		} else if(vo.getPw().length() == 0){
			JOptionPane.showMessageDialog(null, "비밀번호가 입력되지 않았습니다.", "패스워드없음", JOptionPane.WARNING_MESSAGE);			
			flag = false;
			return flag;
		} else if (vo.getGender().length() == 0) {
			JOptionPane.showMessageDialog(null, "성별이 입력되지 않았습니다.", "성별없음", JOptionPane.WARNING_MESSAGE);			
			flag = false;
			return flag;
		}  else if (vo.getAge() == 0) {
			JOptionPane.showMessageDialog(null, "나이가 입력되지 않았습니다.", "나이없음", JOptionPane.WARNING_MESSAGE);			
			flag = false;
			return flag;
		}
//		이름, 비밀번호, 메모가 모두 입력되었으면 테이블에 저장한다.
		if(flag) {
			try {
				Connection conn = DBUtil.getMysqlConnection();
				String sql = "insert into member(id,pw,gender,age,nowip) values (?,?,?,?,?)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, vo.getId());
				pstmt.setString(2, vo.getPw());
				pstmt.setString(3, vo.getGender());
				pstmt.setInt(4, vo.getAge());
				pstmt.setString(5, "logout");
				pstmt.executeUpdate();
				JOptionPane.showMessageDialog(null, "가입완료!!!", "가입완료", JOptionPane.INFORMATION_MESSAGE);
				DBUtil.close(conn);
				DBUtil.close(pstmt);
			} catch (SQLException e) {
				e.printStackTrace();
			} 
		}
		return flag;
	}

	public static boolean member_overlap(String id) {
		if(id.length() == 0) {
			JOptionPane.showMessageDialog(null, "아이디가 입력되지 않았습니다.", "아이디없음", JOptionPane.WARNING_MESSAGE);
			return false;
		} 
		String str = "";
		try {
			Connection conn = DBUtil.getMysqlConnection();
			String sql = "select id from member where id=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				str = rs.getString("id");
			}
			
			DBUtil.close(conn);
			DBUtil.close(pstmt);
			DBUtil.close(rs);
			if(str.length() == 0) {
				JOptionPane.showMessageDialog(null, "사용가능한 아이디입니다.", "검사완료", JOptionPane.INFORMATION_MESSAGE);
				return true;
			} else {
				JOptionPane.showMessageDialog(null, "사용불가능한 아이디입니다.", "검사완료", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}

	public static boolean login(MemberVO vo) {
		// 여기에 인수로 들어오는 vo에는 로그인 화면의 아이디필드에 입력한 아이디,
		// 패스워드필드에 입력한 비번, 그리고 사용하고 있는 컴퓨터의 nowIp가 들어있다.
		boolean flag = true;
		if(vo.getId().length() == 0) {
			JOptionPane.showMessageDialog(null, "아이디가 입력되지 않았습니다.", "아이디없음", JOptionPane.WARNING_MESSAGE);
			flag = false;
		} else if(vo.getPw().length() == 0){
			JOptionPane.showMessageDialog(null, "비밀번호가 입력되지 않았습니다.", "패스워드없음", JOptionPane.WARNING_MESSAGE);			
			flag = false;
		}
//		아이디, 비밀번호가 모두 입력되었으면 확인
		String str = "";
		if(flag) {
			try {
				Connection conn = DBUtil.getMysqlConnection();
				String sql = "select id from member where id=?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, vo.getId());
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					str = rs.getString("id");
				}
				if(str.length() != 0) {
					sql = "select pw from member where id=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, vo.getId());
					rs = pstmt.executeQuery();
					while (rs.next()) {
						str = rs.getString("pw");
					}
	
					if(str.equals(vo.getPw())) {
						// 아이디, 비번 정상적으로 입력되었다면
						String loginState = "";
						sql = "select nowip from member where id=?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, vo.getId());
						rs = pstmt.executeQuery();
						while (rs.next()) {
							loginState = rs.getString("nowip");
						}
			
						sql = "select nowip from member where nowip=?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, vo.getNowIp());
						rs = pstmt.executeQuery();
						while (rs.next()) {
							str = rs.getString("nowip");
						}
						DBUtil.close(conn);
						DBUtil.close(pstmt);
						DBUtil.close(rs);
						if(loginState.equals("logout") && !str.equals(vo.getNowIp())) {
							// 로그인이 안되어있고, 자신이 사용하려는 ip를 아무도 안쓰고 있는 경우에 정상로그인가능
							nowIp_insert_when_login(vo);	
							return true;
						} else if(str.equals(vo.getNowIp())) {
							// 사용하려는 ip가 사용중인 경우
							conn = DBUtil.getMysqlConnection();
							sql = "select id from member where nowip=?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setString(1, vo.getNowIp());
							rs = pstmt.executeQuery();
							while (rs.next()) {
								str = rs.getString("id");
							}
							DBUtil.close(conn);
							DBUtil.close(pstmt);
							DBUtil.close(rs);
							
							if(str.equals(vo.getId())) {
								JOptionPane.showMessageDialog(null, "현재 id, 현재 ip로 이미 접속중입니다.", "중복 로그인", JOptionPane.INFORMATION_MESSAGE);
							}else {
								JOptionPane.showMessageDialog(null, "다른 id가 현재 ip로 이미 접속하였습니다.", "중복 ip", JOptionPane.INFORMATION_MESSAGE);
							}
							return false;
						} else if(!loginState.equals("logout")) {
							// 본인이 로그인 상태라면
							conn = DBUtil.getMysqlConnection();
							sql = "select nowIp from member where id=?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setString(1, vo.getId());
							rs = pstmt.executeQuery();
							while (rs.next()) {
								str = rs.getString("nowIp");
							}
							DBUtil.close(conn);
							DBUtil.close(pstmt);
							DBUtil.close(rs);
							
							if(!str.equals(vo.getNowIp())) {
								JOptionPane.showMessageDialog(null, "현재 id, 다른 ip로 이미 접속중입니다.", "중복 로그인", JOptionPane.INFORMATION_MESSAGE);
							}else {
								JOptionPane.showMessageDialog(null, "현재 id, 현재 ip로 이미 접속중입니다.", "중복 로그인", JOptionPane.INFORMATION_MESSAGE);
							}
							return false;
						}else {
							return false;
						}
							
					} else {
						JOptionPane.showMessageDialog(null, "비밀번호가 틀렸습니다.", "검사완료", JOptionPane.INFORMATION_MESSAGE);
						return false;
					}
				} else {
					JOptionPane.showMessageDialog(null, "회원이 아닙니다.", "검사완료", JOptionPane.INFORMATION_MESSAGE);
				}
				DBUtil.close(conn);
				DBUtil.close(pstmt);
				DBUtil.close(rs);
			} catch (SQLException e) {
				e.printStackTrace();
				
			} 
			
		}
		return false;
		
	}

	public static void nowIp_insert_when_login(MemberVO vo) {
		try {
			Connection conn = DBUtil.getMysqlConnection();
			String sql = "update member SET nowip = ? WHERE id = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			String ip = null;
			try {ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {e.printStackTrace();}
			vo.setNowIp(ip);
			
			pstmt.setString(1, ip);
			pstmt.setString(2, vo.getId());
			pstmt.executeUpdate();
			DBUtil.close(conn);
			DBUtil.close(pstmt);	
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	public static boolean nowIp_setLogout_when_logout(MemberVO vo) {
		// 이건 main 윈도우를 종료할때 실행해야하는 메서드
		// 그런데 빨간 버튼으로 강제 종료할 경우에는 이게 (당연히) 안먹히지. 이것이 해결하면 좋은 부분. 안되면 말고.
		try {
			Connection conn = DBUtil.getMysqlConnection();
			String sql = "update member SET nowip = ? WHERE id = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			String ip = "logout";
			
			pstmt.setString(1, ip);
			pstmt.setString(2, vo.getId());
			pstmt.executeUpdate();
			DBUtil.close(conn);
			DBUtil.close(pstmt);	
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		return true;
	}
	
	// 이건 인수 vo의 id만 사용하는 메서드
	public static MemberVO getMemberProfile(MemberVO vo) {
		
		MemberVO mvo = new MemberVO();
		try {
			Connection conn = DBUtil.getMysqlConnection();
			String sql = "select id, gender, age, nowip from  member where id=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getId());
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				mvo.setId(rs.getString("id"));
				mvo.setGender(rs.getString("gender"));
				mvo.setAge(rs.getInt("age"));
				mvo.setNowIp(rs.getString("nowip"));
			}
			DBUtil.close(conn);
			DBUtil.close(pstmt);
			DBUtil.close(rs);

		} catch (SQLException e) {
			e.printStackTrace();
			return mvo;
		}
		return mvo;
	}
	
	public static MemberVO getMemberProfile(String nowIp) {
		MemberVO mvo = new MemberVO();
		try {
			Connection conn = DBUtil.getMysqlConnection();
			String sql = "select id, gender, age, nowip from member where nowip=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, nowIp);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				mvo.setId(rs.getString("id"));
				mvo.setGender(rs.getString("gender"));
				mvo.setAge(rs.getInt("age"));
				mvo.setNowIp(rs.getString("nowip"));
			}

			DBUtil.close(conn);
			DBUtil.close(pstmt);
			DBUtil.close(rs);

		} catch (SQLException e) {
			e.printStackTrace();
			return mvo;
		}
		return mvo;
		
	}

	public static MemberVO getMVO_from_RemoteSocketAddress_of(Socket socket) {
		String rsa = socket.getRemoteSocketAddress().toString(); // 이건 /172.30.1.23:51000 꼴, 여기서 ip를 뽑자.
		int start = rsa.indexOf("/") + 1;
		int end   = rsa.indexOf(":") ;
		String clientIp = rsa.substring(start, end);
		MemberVO mvo = MemberDAO.getMemberProfile(clientIp);
		return mvo;
	}
	
	// 아이디와 비밀번호 둘 다 변경하기
	static public boolean member_update_id_pw(MemberVO vo) {
			boolean flag = true;
			if(vo.getId().length() == 0) {
				JOptionPane.showMessageDialog(null, "아이디가 입력되지 않았습니다.", "아이디없음", JOptionPane.WARNING_MESSAGE);
				flag = false;
				return flag;
			} else if(vo.getPw().length() == 0){
				JOptionPane.showMessageDialog(null, "비밀번호가 입력되지 않았습니다.", "패스워드없음", JOptionPane.WARNING_MESSAGE);			
				flag = false;
				return flag;
			} 
							
			
//			이름, 비밀번호, 메모가 모두 입력되었으면 테이블에 저장한다.
			if(flag) {
				try {
//					member DB 정보 변경하기
						Connection conn = DBUtil.getMysqlConnection();
						String sql = "update member set id = ? , pw = ? where id = ?";
						PreparedStatement pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, vo.getId());		// 변경하고자 하는 ID
						pstmt.setString(2, vo.getPw());
						pstmt.setString(3, LoginWindow.getLoginMem().getId());	// 변경하기 전 로그인했던 아이디
						pstmt.executeUpdate();
						
//					chatroom DB 정보 변경하기
						sql = "update chatroom set id = ? where id = ?";
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, vo.getId());		// 변경하고자 하는 ID
						pstmt.setString(2, LoginWindow.getLoginMem().getId());	// 변경하기 전 로그인했던 아이디
						pstmt.executeUpdate();					
						
						JOptionPane.showMessageDialog(null, "변경되었습니다.", "프로필 수정 완료", JOptionPane.INFORMATION_MESSAGE);					
						DBUtil.close(conn);
						DBUtil.close(pstmt);
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
			LoginWindow.setLoginMem(vo);	// 변경된 아이디로 LoginMem 변경 (계속 프로필수정해도 가능하도록)
			return flag;
		}
		
	// 비밀번호만 변경하기
	static public boolean member_update_pw(MemberVO vo) {
			boolean flag = true;
			if(vo.getPw().length() == 0){
				JOptionPane.showMessageDialog(null, "비밀번호가 입력되지 않았습니다.", "패스워드없음", JOptionPane.WARNING_MESSAGE);			
				flag = false;
				return flag;
			} 
			
			
//			비밀번호가 입력되었으면 테이블에 저장한다.
			if(flag) {
				try {
					Connection conn = DBUtil.getMysqlConnection();
					String sql = "update member set pw = ? where id = ?";
					PreparedStatement pstmt = conn.prepareStatement(sql);				
					pstmt.setString(1, vo.getPw());
					pstmt.setString(2, LoginWindow.getLoginMem().getId());
					pstmt.executeUpdate();
					JOptionPane.showMessageDialog(null, "변경되었습니다.", "프로필 수정 완료", JOptionPane.INFORMATION_MESSAGE);
					DBUtil.close(conn);
					DBUtil.close(pstmt);
				} catch (SQLException e) {
					e.printStackTrace();
				} 
			}
			return flag;
		}
	
}
