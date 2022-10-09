package com.telegram.payload.request;

public class SignupDto {

	private String bios;
	private String process;
	private String board;
	private String email;
	private String role = "ROLE_USER";
	public String getBios() {
		return bios;
	}
	public void setBios(String bios) {
		this.bios = bios;
	}
	public SignupDto(String email,String bios, String process, String board) {
		super();
		this.bios = bios;
		this.process = process;
		this.board = board;
		this.email = email;
		
	}
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
	public String getBoard() {
		return board;
	}
	public void setBoard(String board) {
		this.board = board;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public SignupDto() {
		// TODO Auto-generated constructor stub
	}

}
