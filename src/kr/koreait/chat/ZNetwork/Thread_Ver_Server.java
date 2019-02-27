package kr.koreait.chat.ZNetwork;

import kr.koreait.chat.Member.MemberVO;
import kr.koreait.chat.Room.RoomVO;

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
