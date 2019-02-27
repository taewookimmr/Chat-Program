package kr.koreait.chat.Room;

public class RoomVO {
	
	private int roomnumber;
	private String roomname;
	private String id;
	private String nowmem;
	private String maxmem;
	private String roompw;
	private String serverip;
	private String channel;
	private String privateChoice;
	private int serverLocalPort;
	public int getRoomnumber() {
		return roomnumber;
	}
	public void setRoomnumber(int roomnumber) {
		this.roomnumber = roomnumber;
	}
	public String getRoomname() {
		return roomname;
	}
	public void setRoomname(String roomname) {
		this.roomname = roomname;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNowmem() {
		return nowmem;
	}
	public void setNowmem(String string) {
		this.nowmem = string;
	}
	public String getMaxmem() {
		return maxmem;
	}
	public void setMaxmem(String maxmem) {
		this.maxmem = maxmem;
	}
	public String getRoompw() {
		return roompw;
	}
	public void setRoompw(String roompw) {
		this.roompw = roompw;
	}
	public String getServerip() {
		return serverip;
	}
	public void setServerip(String serverip) {
		this.serverip = serverip;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getPrivateChoice() {
		return privateChoice;
	}
	public void setPrivateChoice(String privateChoice) {
		this.privateChoice = privateChoice;
	}
	public int getServerLocalPort() {
		return serverLocalPort;
	}
	public void serServerLocalPort(int serverLocalPort) {
		this.serverLocalPort = serverLocalPort;
	}
	
	@Override
	public String toString() {
		return "RoomVO [roomnumber=" + roomnumber + ", roomname=" + roomname + ", id=" + id + ", nowmem=" + nowmem
				+ ", maxmem=" + maxmem + ", roompw=" + roompw + ", serverip=" + serverip + ", channel=" + channel
				+ ", privateChoice=" + privateChoice + ", serverPortNum=" + serverLocalPort + "]";
	}


	
	
	
	

}
