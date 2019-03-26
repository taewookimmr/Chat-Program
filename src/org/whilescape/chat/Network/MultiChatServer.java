package org.whilescape.chat.Network;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import org.whilescape.chat.DTO.MemberVO;
import org.whilescape.chat.DTO.RoomVO;
import org.whilescape.chat.Window.MainWindow;

public class MultiChatServer {
	public static int server_localport = 10000;
	
	List<PrintWriter>  list  = Collections.synchronizedList(new ArrayList<PrintWriter>());
	List<Socket>  socketList = Collections.synchronizedList(new ArrayList<Socket>());
	List<MemberVO> mvoList   = Collections.synchronizedList(new ArrayList<MemberVO>());
	
	RoomVO rvo;
	ServerSocket serverSocket = null;
	
	public MultiChatServer(RoomVO rvo) {
		this.rvo = rvo;

		try {
			serverSocket = new ServerSocket(rvo.getServerLocalPort());
	
			while(serverSocket != null) {
				
				Socket socket = serverSocket.accept();
//				socket.getRemoteSocketAddress() 이게 방에 들어온 클라이언트의 ip, localport를 담고 있다.
				
				MultiChatThread mct = new MultiChatThread(serverSocket, socket,  rvo, this.list, this.socketList, this.mvoList);
				mct.setDaemon(true);
				mct.start();
				MainWindow.view();
			}
			
		} catch(Exception e) {
//			e.printStackTrace();
		} finally {
			JOptionPane.showMessageDialog(null, "채팅방의 서버가 종료되었습니다.");
		}
		
	}



		
}

