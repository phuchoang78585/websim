package com.telegram.security.jwt;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.telegram.dto.DeviceDto;
import com.telegram.dto.UserDetailDto;
import com.telegram.entity.Device;
import com.telegram.security.service.UserDetailsImpl;
import com.telegram.util.DateUtil;

import io.jsonwebtoken.*;
@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
  @Value("${bezkoder.app.jwtSecret}")
  private String jwtSecret;
  @Value("${bezkoder.app.jwtExpirationMs}")
  private int jwtExpirationMs;
  @Value("${bezkoder.app.jwtCookieName}")
  private String jwtCookie;
  public String getJwtFromCookies(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, jwtCookie);
    if (cookie != null) {
      return cookie.getValue();
    } else {
      return null;
    }
  }
  public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal) {
    String jwt = generateTokenFromUsername(userPrincipal.getUsername());
    ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(24 * 60 * 60).httpOnly(true).build();
    
    return cookie;
  }
  public ResponseCookie getCleanJwtCookie() {
    ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/api").build();
    return cookie;
  }
  public String getUserNameFromJwtToken(String token) {
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }
  public boolean validateJwtToken(String authToken) {
    String subject = getUsernameFromToken(authToken);
    Date expri = getExpirationDateFromToken(authToken);
    Map<String,Object> deviceMap = getAllClaimsFromToken(authToken);
    
    return false;
  }
  public boolean validateJwtToken1(String authToken,UserDetailDto user) {
	    String subject = getUsernameFromToken(authToken);
	    Date expri = getExpirationDateFromToken(authToken);
	    Map<String,Object> deviceMap = getAllClaimsFromToken(authToken);
	    
	    Device deviceOld = user.getDevice();
	    String bios =(String) deviceMap.get("bios");
	    String board =(String) deviceMap.get("board");
	    String process =(String) deviceMap.get("process");
	    Boolean isSubject = subject.equalsIgnoreCase(user.getUsername());
	    Boolean isDevice = checkDevice(bios,board,process, deviceOld);
	    Boolean isExipri = DateUtil.dateBeforeEqualsDate(DateUtil.getDate(), expri);
	    if(isSubject && isDevice && isExipri ) {
	    	return true;
	    }
	    return false;
	  }
  public boolean checkDevice(String bios,String board,String process,Device device) {
	  if(!bios.equalsIgnoreCase(device.getBios())) {
		  return false;
	  }
	  if(!board.equalsIgnoreCase(device.getBoard())) {
		  return false;
	  }
	  if(!process.equalsIgnoreCase(device.getProcess())) {
		  return false;
	  }
	  return true;
  }
  public String generateTokenFromUserDetails(UserDetailDto user) {
	  final Date createdDate = DateUtil.getDate();
      final Date expirationDate = calculateExpirationDate(createdDate,86000);
      Map<String, Object> claims = new HashMap<>();
      Device device = user.getDevice();
      claims.put("device", user.getDevice());
	    return Jwts.builder()
	    	.claim("bios",device.getBios())
	    	.claim("process",device.getProcess())
	    	.claim("board", device.getBoard())
	        .setSubject(user.getUsername())
	        .setId(user.getJti())
	        .setIssuer(user.getUsername())
	        .setIssuedAt(createdDate)
	        .setExpiration(expirationDate)
	        .signWith(SignatureAlgorithm.HS512, jwtSecret)
	        .compact();
  }
  public String generateTokenFromADminDetails(UserDetailDto user) {
	  final Date createdDate = DateUtil.getDate();
      final Date expirationDate = calculateExpirationDate(createdDate,600000);
      Map<String, Object> claims = new HashMap<>();
      Device device = user.getDevice();
      claims.put("device", user.getDevice());
	    return Jwts.builder()
	    	.claim("bios",device.getBios())
	    	.claim("process",device.getProcess())
	    	.claim("board", device.getBoard())
	    	.setId(user.getJti())
	        .setSubject(user.getUsername())
	        .setIssuer(user.getUsername())
	        .setIssuedAt(createdDate)
	        .setExpiration(expirationDate)
	        .signWith(SignatureAlgorithm.HS512, jwtSecret)
	        .compact();
  }
  public String getUsernameFromToken(String token) {
      return getClaimFromToken(token, Claims::getSubject);
  }

  public Date getIssuedAtDateFromToken(String token) {
      return getClaimFromToken(token, Claims::getIssuedAt);
  }

  public Date getExpirationDateFromToken(String token) {
      return getClaimFromToken(token, Claims::getExpiration);
  }
  public String getJtiFromToken(String token) {
      return getClaimFromToken(token, Claims::getId);
  }
  public String getAudienceFromToken(String token) {
      return getClaimFromToken(token, Claims::getAudience);
  }

  public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
      final Claims claims = getAllClaimsFromToken(token);
      return claimsResolver.apply(claims);
  }

  private Claims getAllClaimsFromToken(String token) {
      return Jwts.parser()
              .setSigningKey(jwtSecret)
              .parseClaimsJws(token)
              .getBody();
  }
  private  Date addSeconds(Date date, Integer seconds) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      cal.add(Calendar.SECOND, seconds);
      return cal.getTime();
    }
  private  Date calculateExpirationDate(Date createdDate,int second) {
      return addSeconds(createdDate,second);
  }
  public String generateTokenFromUsername(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(SignatureAlgorithm.HS512, jwtSecret)
        .compact();
  }
}
