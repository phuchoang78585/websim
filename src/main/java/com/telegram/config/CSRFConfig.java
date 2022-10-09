package com.telegram.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import com.telegram.service.SecrettService;

/*
 * @Configuration public class CSRFConfig {
 * 
 * @Autowired SecrettService secretService;
 * 
 * @Bean public CsrfTokenRepository jwtCsrfTokenRepository() {
 * secretService.refreshSecrets(); return new
 * JWTCsrfTokenRepository(secretService.getHS256SecretBytes()); } }
 */
