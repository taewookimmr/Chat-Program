package org.whilescape.chat.Network;

import org.whilescape.chat.DTO.MemberVO;
import org.whilescape.chat.DTO.RoomVO;

public class Thread_Ver_Server implements Runnable{
	
	public RoomVO rvo;
	
	// 생성자 
	public Thread_Ver_Server(RoomVO rvo) {
		this.rvo = rvo;
	}
	
	@Override
	public void run() {
		MultiChatServer multiChatServer	= new MultiChatServer(rvo);
	}

}
