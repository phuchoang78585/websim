package com.telegram.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.telegram.security.jwt.JwtUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telegram.advice.ErrorMessage;
import com.telegram.dto.DeviceDto;
import com.telegram.dto.UserDetailDto;
import com.telegram.dto.UserDto;
import com.telegram.entity.Device;
import com.telegram.entity.ERole;
import com.telegram.entity.Role;
import com.telegram.entity.User;
import com.telegram.payload.request.LoginRequest;
import com.telegram.payload.request.LoginRequestFull;
import com.telegram.payload.request.SignupRequest;
import com.telegram.payload.response.JwtResponse;
import com.telegram.payload.response.MessageResponse;
import com.telegram.payload.response.UserInfoResponse;
import com.telegram.repository.*;
import com.telegram.security.service.UserDetailsImpl;
import com.telegram.service.cache.CacheService;
import com.telegram.service.user.IUserService;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;
  @Autowired
  UserRepository userRepository;
  @Autowired
  RoleRepository roleRepository;
  @Autowired
  PasswordEncoder encoder;
  @Autowired
  IUserService userService;
  @Autowired
  CacheService cacheService;
  @Autowired
  JwtUtils jwtUtils;
  @Autowired
  ObjectMapper mapper;
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .body(new UserInfoResponse(userDetails.getId(),
                                   userDetails.getUsername(),
                                   userDetails.getEmail(),
                                   roles));
  }
  @PostMapping("/signin1")
  public ResponseEntity<?> authenticateUser1(@Valid @RequestBody LoginRequestFull loginRequest) throws JsonProcessingException {
	// check user đã login chưa.
	String username = loginRequest.getUsername();
    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    boolean isDeviceAccept = checkDevice(userDetails.getDevice(),loginRequest.getDevice());
    if(isDeviceAccept == false) {
    	authentication = null;
    	SecurityContextHolder.getContext().setAuthentication(authentication);
    	return ResponseEntity.badRequest()
    	        .body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),"DNACTP","",""));
    }
    UserDetailDto userDetailDto = new UserDetailDto(userDetails);
    userDetailDto.setJti(UUID.randomUUID().toString());
    String jwtCookie = jwtUtils.generateTokenFromUserDetails(userDetailDto);
    String accesToken  ="";
    List<String> roles = new ArrayList<String>();
    userDetails.getAuthorities().forEach(a ->{
    	roles.add(a.getAuthority());
    });
    userDetailDto.setRoles(roles);
    String carAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userDetailDto);
    cacheService.set(username, carAsString, 86000);
    return ResponseEntity.ok()
        .body(new JwtResponse(userDetails.GetSecondLogin(),jwtCookie));
  }
  
  public boolean checkDevice(Device deviceOld,DeviceDto device) {
	  if(!deviceOld.getBios().equalsIgnoreCase(device.getBios())) {
		  return false;
	  }
	  if(!deviceOld.getBoard().equalsIgnoreCase(device.getBoard())) {
		  return false;
	  }
	  if(!deviceOld.getProcess().equalsIgnoreCase(device.getProcess())) {
		  return false;
	  }
	  return true;
  }
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@RequestBody @Valid UserDto userDto) throws Exception {
	DeviceDto deviceDto = userDto.getDevice();
	if(userDto.getUsername() == null || userDto.getUsername() == "") {
		return ResponseEntity.badRequest()
		        .body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),"UEMPTY","",""));
	}
	if(deviceDto == null) {
		return ResponseEntity.badRequest()
		        .body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),"DIFAIL","",""));
	}
	if(deviceDto.getBios() == null || deviceDto.getBoard() == null || deviceDto.getProcess() == null) {
		return ResponseEntity.badRequest()
		        .body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),"DIFAIL","",""));
	}
	if(deviceDto.getBios().isEmpty() || deviceDto.getBoard().isEmpty() || deviceDto.getProcess().isEmpty()) {
		return ResponseEntity.badRequest()
        .body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),"DIFAIL","",""));
	} 
	String result = userService.registerUser(userDto);
	if("ALREADY".equalsIgnoreCase(result)) {
		return ResponseEntity.badRequest()
		        .body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),result,"",""));
	}
    if("SFAIL".equalsIgnoreCase(result)) {
    	return ResponseEntity.badRequest()
    	        .body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(),result,"",""));
    }
    return ResponseEntity.ok()
	        .body(new ErrorMessage(HttpStatus.OK.value(),result,"",""));
  }
  
  @GetMapping("/logout")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> logoutUser() {
	UserDetailDto userDetails =(UserDetailDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	cacheService.delete(userDetails.getUsername());
    return ResponseEntity.ok()
        .body(new MessageResponse("You've been signed out!"));
  }
  @GetMapping("/checklive")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> CheckUser() throws Exception{
	    UserDetailDto userDetailDto =(UserDetailDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    userDetailDto.setJti(UUID.randomUUID().toString());
	    String jwtCookie = jwtUtils.generateTokenFromUserDetails(userDetailDto);
	    String accesToken  ="";
	    String carAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userDetailDto);
	    cacheService.set(userDetailDto.getUsername(), carAsString, 86000);
	    return ResponseEntity.ok()
	        .body(new JwtResponse(1,jwtCookie));
  }
}
