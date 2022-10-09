package com.telegram.security.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegram.dto.UserDetailDto;
import com.telegram.payload.response.JwtResponse;
import com.telegram.security.service.UserDetailsImpl;
import com.telegram.security.service.UserDetailsServiceImpl;
import com.telegram.service.cache.CacheService;

public class AuthTokenFilter extends OncePerRequestFilter {
  @Autowired
  private JwtUtils jwtUtils;
  @Autowired
  CacheService cacheService;
  @Autowired
  private UserDetailsServiceImpl userDetailsService;
  @Autowired
  ObjectMapper mapper;
  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
  @Override
  // 
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      
      String jwt = request.getHeader("jwt");
      if (jwt == null) {
    	  throw new Exception();
      }
      String subject = jwtUtils.getUsernameFromToken(jwt);
      String accessToken = jwtUtils.getJtiFromToken(jwt);
      
      Object userCache = (Object) cacheService.get(subject);
      if(userCache == null) {
    	  throw new Exception();
      }
      String jsonUser =  (String) userCache;
      UserDetailDto user = mapper.readValue(jsonUser,UserDetailDto.class);
      if(validateJti(accessToken, user.getJti()) == false) {
    	  throw new Exception();
      }
      if (jwtUtils.validateJwtToken1(jwt,user)) {  
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(user,
                                                    null,
                                                    user.getRoles().stream()
                                                    .map(role -> new SimpleGrantedAuthority(role))
                                                    .collect(Collectors.toList())   
                                                    );
       // authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e);
        
    }
    filterChain.doFilter(request, response);
  }
  private boolean validateJti(String jtiOld,String jtiCach) {
	  System.out.println(jtiOld);
	  System.out.println(jtiCach);
	  return jtiOld.equalsIgnoreCase(jtiCach);
  }
  private String parseJwt(HttpServletRequest request) {
    String jwt = jwtUtils.getJwtFromCookies(request);
    return jwt;
  }
}
