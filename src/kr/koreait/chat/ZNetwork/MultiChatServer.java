package kr.koreait.chat.ZNetwork;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import kr.koreait.chat.Main.MainWindow;
import kr.koreait.chat.Member.MemberVO;
import kr.koreait.chat.Room.RoomVO;

public class MultiChatServer {
	public static int server_localport = 10000;
	
	List<PrintWriter>  list = Collections.synchronizedList(new ArrayList<PrintWriter>());
	List<Socket>  socketList = Collections.synchronizedList(new ArrayList<Socket>());
	List<MemberVO> mvoList  = Collections.synchronizedList(new ArrayList<MemberVO>());
	RoomVO rvo;
	ServerSocket serverSocket = null;
	
	public MultiChatServer(RoomVO rvo) {
		this.rvo = rvo;

		try {
			serverSocket = new ServerSocket(rvo.getServerLocalPort());
	
			while(serverSocket != null) {
				
				Socket socket = serverSocket.accept();
//				socket.getRemoteSocketAddress() 이게 방에 들어온 클라이언트의 ip, localport를 담고 있다.
//				이것으로 그 클라이언트의 id정보를 얻어올 수 있을까? 디비에 nowip를 넣어서 해결하였다.
				
				MultiChatThread mct = new MultiChatThread(serverSocket, socket,  rvo, this.list, this.socketList, this.mvoList);
				mct.start();
				MainWindow.view();
			}
			
		} catch(Exception e) {
//			e.printStackTrace();
		} finally {
			JOptionPane.showMessageDialog(null, "채팅방의 서버가 종료되었습니다.");
			// 이건 방장이 아닌 사람들에게는 당연히 안보이겠지
		}
		
	}



		
}

