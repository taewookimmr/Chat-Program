package org.whilescape.chat.ZNetwork;

import org.whilescape.chat.Member.MemberVO;
import org.whilescape.chat.Room.RoomVO;

public class Thread_Ver_Server implements Runnable{
	
	public RoomVO rvo;
	public Thread_Ver_Server(RoomVO rvo) {
		this.rvo = rvo;

	}
	
	@Override
	public void run() {
		MultiChatServer multiChatServer	= new MultiChatServer(rvo);
	}

}
