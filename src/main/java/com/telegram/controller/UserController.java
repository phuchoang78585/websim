package com.telegram.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegram.advice.ErrorMessage;
import com.telegram.dto.ResponeClient;
import com.telegram.dto.UserClient;
import com.telegram.dto.UserDetailDto;
import com.telegram.dto.UserDto;
import com.telegram.dto.twoline.Order;
import com.telegram.dto.twoline.OrderCreate;
import com.telegram.entity.Role;
import com.telegram.entity.User;
import com.telegram.payload.request.SignupDto;
import com.telegram.payload.response.JwtResponse;
import com.telegram.payload.response.UserInfoResponse;
import com.telegram.repository.RoleRepository;
import com.telegram.repository.UserRepository;
import com.telegram.security.jwt.JwtUtils;
import com.telegram.security.service.UserDetailsImpl;
import com.telegram.service.cache.CacheService;
import com.telegram.service.twoline.TwondLineService;
import com.telegram.service.user.UserService;
import com.telegram.util.AppUtil;

import io.netty.util.internal.ThreadLocalRandom;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class UserController {
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
	@Autowired
	RoleRepository rolerep;
	@Autowired
	UserRepository userRep;
	@GetMapping("/api/admin/user/active")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> setActiveUser(@RequestParam("username") String username,@RequestParam("day")Integer day)throws Exception{
		String password = userService.setActiveUser(username,day);
		return ResponseEntity.ok()
		        .body(new ErrorMessage(HttpStatus.OK.value(),"SUCESS",password,""));
	}
	@GetMapping("/api/admin/user/disable")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> disableUser(@RequestParam("username") String username) throws Exception{
		boolean isSucces = userService.disable(username);
		AppUtil.toStringStatus(isSucces);
		return ResponseEntity.ok()
		        .body(new ErrorMessage(HttpStatus.OK.value(),"SUCESS", AppUtil.toStringStatus(isSucces),""));
	}
	@GetMapping("/api/admin/user/delete/{username}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> deleteUser(@PathVariable("username") String username) throws Exception{
		try {
			String a = userService.delete(username);
			cacheService.delete(username);
			return ResponseEntity.ok()
			        .body(new ErrorMessage(HttpStatus.OK.value(),a,"",""));
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	@GetMapping("/api/admin/user/delete")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> deleteUserById(@RequestParam("id") String username)  throws Exception{
		
		String password	= userService.deleteById(Long.valueOf(username));
		
		return ResponseEntity.ok()
		        .body(new ErrorMessage(HttpStatus.OK.value(),"SUCESS",password,""));
	}
	/*
	 * @GetMapping("/api/admin/user/online")
	 * 
	 * @PreAuthorize("hasRole('ADMIN')")
	 * 
	 * @ResponseStatus(HttpStatus.OK) public ResponseEntity<?> GetUserOnline()
	 * throws Exception{ cacheService.set(result.getUsername(), carAsString,
	 * 1341400); String a = (String) cacheService.get(result.getUsername()); return
	 * ResponseEntity.ok() .body(new
	 * ErrorMessage(HttpStatus.OK.value(),"SUCESS",password,"")); }
	 */
	@GetMapping("/api/admin/user/password")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> updatePassword(@RequestParam("username") String username) throws Exception {
		String password = null;
		password = userService.updatePassword(username);
		
		return ResponseEntity.ok()
		        .body(new ErrorMessage(HttpStatus.OK.value(),"SUCESS",password,""));
	}
	@GetMapping("/api/admin/user/day")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> adddat(@RequestParam("username") String username,@RequestParam("day")Integer day) throws Exception {
		String password = null;
		password = userService.addDay(username,day);
		return ResponseEntity.ok()
		        .body(new ErrorMessage(HttpStatus.OK.value(),"SUCESS",password,""));
	}
	
	@GetMapping("/api/admin/users")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> findall() throws Exception{
		List<UserClient> respone = userService.findAll();
		Object data1 = respone;
		String data = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(respone);
		return ResponseEntity.ok()
		        .body(new ErrorMessage(HttpStatus.OK.value(),"SUCESS",data1,""));
	}
	@GetMapping("/api/admin/user")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> findByUsername(@RequestParam("id") String username) throws Exception {
		UserClient respone = userService.findUserById(Long.valueOf(username));
		Object data1 = respone;
		String data = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(respone);
		return ResponseEntity.ok()
		        .body(new ErrorMessage(HttpStatus.OK.value(),"SUCESS",data1,""));
	}
	@Autowired
	AuthenticationManager authenticationManager;
	@PostMapping("/08574785478965874")
	public ResponseEntity<?> findByUsernamea(@RequestBody @Valid UserDto userDto) throws Exception {
		User result = userService.registerAdmin(userDto);
		Authentication authentication = authenticationManager
		        .authenticate(new UsernamePasswordAuthenticationToken(result.getUsername(), result.getPassword1()));
	    SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		UserDetailDto userDetailDto = new UserDetailDto(userDetails);
		userDetailDto.setJti(UUID.randomUUID().toString());
		String jwtCookie = jwtUtils.generateTokenFromADminDetails(userDetailDto);
		String accesToken  ="";
		List<String> roles = new ArrayList<String>();
		userDetails.getAuthorities().forEach(a ->{
		roles.add(a.getAuthority());
		});
		userDetailDto.setRoles(roles);
		String carAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userDetailDto);
		cacheService.set(result.getUsername(), carAsString, 1341400);
		String a =  (String) cacheService.get(result.getUsername());
		return ResponseEntity.ok()
		        .body(new JwtResponse(1,jwtCookie));
	}
	@GetMapping("/3658747858741547")
	public ResponseEntity<?> findByUsernamea1() throws Exception {
		String mag = "";
		try {
			Role role = new Role("ROLE_USER");
			Role role1 = new Role("ROLE_MODERATOR");
			Role rol2 = new Role("ROLE_ADMIN");
			rolerep.save(role);
			rolerep.save(role1);
			rolerep.save(rol2);
			mag = "SUCCESS";
		} catch (Exception e) {
			 throw new Exception(e);
		}
		return ResponseEntity.ok()
		        .body(new String(mag));
	}
	
	@GetMapping(value = "/api/test/set", 
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public String getTweetsNonBlocking4(@RequestParam("key") String number,@RequestParam("value") String number1) throws InterruptedException {
		cacheService.set(number,number1,1000);
		String a = (String) cacheService.get(number);
		return a;
	}
	@GetMapping(value = "/api/test/get", 
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public String getTweetsNonBlocking5(@RequestParam("key")String key) throws InterruptedException {
		 String a = (String) cacheService.get(key);
		return a;
	}
	@GetMapping(value = "/api/test/update", 
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public String getTweetsNonBlocking6(@RequestParam("key")String key) throws InterruptedException {
		cacheService.replace(key,"123456", 1000);
		String a = (String) cacheService.get(key);
		return a;
	}
	@GetMapping(value = "/api/test/delete", 
            produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public String getTweetsNonBlocking7(@RequestParam("key")String key) throws InterruptedException {
		cacheService.delete(key);
		String a = (String) cacheService.get(key);
		return a;
	}

}
