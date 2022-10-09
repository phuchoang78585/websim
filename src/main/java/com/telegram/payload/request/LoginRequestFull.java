package com.telegram.payload.request;

import javax.validation.constraints.NotBlank;

import com.telegram.dto.DeviceDto;

public class LoginRequestFull {
	@NotBlank
	private String username;
	
	@NotBlank
	private String password;
	
	private DeviceDto device;

	public DeviceDto getDevice() {
		return device;
	}

	public void setDevice(DeviceDto device) {
		this.device = device;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
