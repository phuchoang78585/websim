package com.telegram.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegram.dto.ResponeClient;
import com.telegram.dto.UserDetailDto;
import com.telegram.dto.twoline.Order;
import com.telegram.security.jwt.JwtUtils;
import com.telegram.service.cache.CacheService;
import com.telegram.service.twoline.TwondLineService;
import com.telegram.service.user.UserService;

@RestController
public class TwondlineController {
	@Autowired
	@Qualifier("Host")
	WebClient webClient;
	@Autowired
	TwondLineService twondLineService;
	@Autowired
	UserService userService;
	@Autowired
	CacheService cacheService;
	@Autowired
	JwtUtils jwtUtils;
	@Autowired
	ObjectMapper mapper;
	@GetMapping(value = "/api/phone/order")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> getOrder(@RequestParam("api")String api,@RequestParam("id") Integer id) throws Exception {
		Order order = twondLineService.getOrderCheck(id, api).block();
		Boolean check =  SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
		UserDetailDto userDetails =(UserDetailDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String jwtCookie = jwtUtils.generateTokenFromUserDetails(userDetails);
		String accesToken  =UUID.randomUUID().toString();
		userDetails.setJti(accesToken);
		String carAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userDetails);
		cacheService.replace(userDetails.getUsername(), carAsString, 300);
		Object userCache1 =  cacheService.get(userDetails.getUsername());
		if(userCache1 == null) {
			
		}
		return ResponseEntity.ok()
		        .body(new ResponeClient(accesToken, jwtCookie, order));
	}
	@GetMapping(value = "/api/phone/buy")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> buyPhone(@RequestParam("api")String api) throws Exception {
		
		Order order = null;
		try {
			order = twondLineService.buyPhone(api);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		UserDetailDto userDetails =(UserDetailDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String jwtCookie = jwtUtils.generateTokenFromUserDetails(userDetails);
		String accesToken  =UUID.randomUUID().toString();
		userDetails.setJti(accesToken);
		String carAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userDetails);
		cacheService.replace(userDetails.getUsername(), carAsString, 300);
		Object userCache1 =  cacheService.get(userDetails.getUsername());
		if(userCache1 == null) {
			
		}
		return ResponseEntity.ok()
		        .body(new ResponeClient(accesToken, jwtCookie, order));
	}

}
