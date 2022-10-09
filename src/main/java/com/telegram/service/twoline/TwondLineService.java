package com.telegram.service.twoline;

import org.springframework.stereotype.Service;

import com.telegram.dto.twoline.Order;
import com.telegram.dto.twoline.OrderCreate;

import reactor.core.publisher.Mono;


public interface TwondLineService {
	public Mono<Order> getOrderCheck(int idOrdered,String apiKey);
	public Mono<OrderCreate> createOrder(String apiKey);
	public Order buyPhone(String apiKey) throws InterruptedException;
	public Mono<String> delay();
	
}
