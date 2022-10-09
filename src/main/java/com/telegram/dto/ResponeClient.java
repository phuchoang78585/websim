package com.telegram.dto;

import com.telegram.dto.twoline.Order;

import lombok.Getter;

public class ResponeClient {

	private String accessToken;
	private String token;
	private Order order;
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public ResponeClient(String accessToken, String token, Order order) {
		super();
		this.accessToken = accessToken;
		this.token = token;
		this.order = order;
	}

	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public ResponeClient() {
		
	}
	

}
