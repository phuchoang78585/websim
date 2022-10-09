package com.telegram.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telegram.entity.Device;
import com.telegram.security.service.UserDetailsImpl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDto implements Serializable{
	 private Long id;
	 private String username;
	 
	 private List<String> roles;
	 public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Boolean getEnable() {
		return enable;
	}
	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	public Device getDevice() {
		return device;
	}
	public void setDevice(Device device) {
		this.device = device;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getJti() {
		return jti;
	}
	public void setJti(String jti) {
		this.jti = jti;
	}
	private Boolean enable;
	 
	 private Device device;
	 private String secret;
	 private String jti;
	 private String password;
	 private Boolean expri;
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public UserDetailDto(UserDetailsImpl user) {
		this.device = user.getDevice();
		this.username= user.getUsername();
		this.jti = user.getJti();
		this.password = user.getPassword();
		this.id = user.getId();
		this.enable = user.isEnabled();
		this.expri = user.isAccountNonExpired();
		
		
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

}
