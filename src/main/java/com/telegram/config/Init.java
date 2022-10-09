package com.telegram.config;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.telegram.entity.Role;
import com.telegram.repository.RoleRepository;
import com.telegram.service.cache.CacheService;

@Component
public class Init {
    
	@Inject
	RoleRepository rolea;
	@Autowired
	CacheService cache;
	@PostConstruct
	public void Init() {
		
	
	}

}
