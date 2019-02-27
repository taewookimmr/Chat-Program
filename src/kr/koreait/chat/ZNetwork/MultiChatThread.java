package kr.koreait.chat.ZNetwork;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.tree.ExpandVetoException;

import kr.koreait.chat.Initiator.LoginWindow;
import kr.koreait.chat.Main.MainWindow;
import kr.koreait.chat.Member.MemberDAO;
import kr.koreait.chat.Member.MemberVO;
import kr.koreait.chat.Room.RoomVO;

public class MultiChatThread extends Thread{

	public List<PrintWriter> pwList     = Collections.synchronizedList(new ArrayList<PrintWriter>());
	public List<Socket>  socketList     = Collections.synchronizedList(new ArrayList<Socket>());
	public List<MemberVO>    mvoList    = Collections.synchronizedList(new ArrayList<MemberVO>());
	
	public RoomVO rvo;
	public MemberVO mvo_me;
	
	public ServerSocket serverSocket = null;
	public Socket  socket  = null;
	public PrintWriter pw  = null;
	
	public MultiChatThread(ServerSocket serverSocket, Socket socket, RoomVO rvo, List<PrintWriter> pwList, List<Socket> socketList, List<MemberVO> mvoList) {
		
		this.rvo    		= rvo;
		this.serverSocket 	= serverSocket;
		this.socket 		= socket;
		this.pwList 	    = pwList;
		this.socketList 	= socketList;
		this.mvoList    	= mvoList;
		
		try {
			pw = new PrintWriter(socket.getOutputStream());
			pwList.add(pw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.socketList.add(this.socket);

		this.mvo_me = MemberDAO.getMVO_from_RemoteSocketAddress_of(this.socket);
		this.mvoList.add(this.mvo_me);
		

	}

	@Override
	public void run() {
		
		String name = mvo_me.getId();
		
		try {
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			for(PrintWriter pw : pwList) {
				String str = "";
				str += "##########";
				str +="["+ mvo_me.getId()+ "]님이 방에 입장하였습니다.";
				str += "##########\n";
				pw.write(str + "\n");
				pw.flush();
		    }
			
			while(true) {
//				String str = reader.readLine().trim();
				StringBuffer str = new StringBuffer();
				str.append(reader.readLine().trim());
				if(str == null) {break;}
				if(str.length() > 0) {
					for(PrintWriter writer : pwList) {
						writer.write(str + "\n");
						writer.flush();
					}
				}
			}
			
		} catch (Exception e) {
//			e.printStackTrace();
		//  자신이 방장으로서 종료하게 되면 
		//  	1) 위 try의 String str = reader.readLine().trim();에서 NullPointerException
		//  	2) 같은 곳에서 java.net.SocketException: Socket closed
		} finally {
			
			// 이 부분은 필요없을 것 같음. 어차피 MCC쪽에서 본인 퇴장시 자기 퇴장한다는 메시지 보내고 퇴장하니
			for(PrintWriter writer : pwList) {
				writer.write("[" + name + "]님의 채팅방 퇴장 재확인 완료 \n\n");
				writer.flush();
			}
			
			lists_renewal();   // 이 부분 디버깅시 체크 많이 해주고 수정할 필요있으면 해야지.
			MainWindow.view(); // 변화된 것을 바로 보여준다.
			
			if(socket != null) try {socket.close();} catch (IOException e1) 		{e1.printStackTrace();}
			if(pw     != null) try {pw.close();	} catch (Exception e1) 			    {e1.printStackTrace();}
		}
	}

	// 이 메서드는 잘 작동하는 것 같다. 계속 디버깅 테스트 필요하긴 함
	public void lists_renewal() {
		int index = mvoList.indexOf(mvo_me);

        if(index == 0) {
        	// 방장이면 해당 서버의 list를 모두 지운다
        	pwList.clear();
        	for(Socket socket : socketList) {
        		try {
        			socket.close(); // 이게 꺼지는 것은 정상적으로 작동함. 
        			socket = null;
        		} 
        		catch (Exception e) {e.printStackTrace();}
        	}
		    socketList.clear();
		    mvoList.clear();
		    
		    // 서버소켓도 아예 꺼버린다.
		    try {
				this.serverSocket.close();
				this.serverSocket = null;
			} catch (Exception e) {
				e.printStackTrace();
			}  
        }
        else if(index > 0){
        	// 방장이 아니면
            try   {
            	pwList.remove(index);
            	
            	Socket tempSocket = socketList.get(index);
            	tempSocket.close();
            	tempSocket = null;
            	socketList.remove(index);
            	
    	        mvoList.remove(index);
            } 
            catch (Exception e) {
            	e.printStackTrace();
            }
	        
        }
	}

}


