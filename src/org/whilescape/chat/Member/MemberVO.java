package org.whilescape.chat.Member;

public class MemberVO {
	private String id;
	private String pw;
	private String gender;
	private int age;
	private String nowIp;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getNowIp() {
		return nowIp;
	}
	public void setNowIp(String nowIp) {
		this.nowIp = nowIp;
	}
	@Override
	public String toString() {
		return "MemberVO [id=" + id + ", pw=" + pw + ", gender=" + gender + ", age=" + age + ", nowIp=" + nowIp + "]";
	}
	

	
	
}
