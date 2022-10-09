package com.telegram.service.user;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;

import com.telegram.dto.UserClient;
import com.telegram.dto.UserDto;
import com.telegram.entity.Device;
import com.telegram.entity.ERole;
import com.telegram.entity.Role;
import com.telegram.entity.User;
import com.telegram.repository.DeviceRepository;
import com.telegram.repository.RoleRepository;
import com.telegram.repository.UserRepository;
import com.telegram.util.AppUtil;
import com.telegram.util.DateUtil;

@Service
public class UserService implements IUserService{

	@Autowired
	UserRepository userRep;
	@Autowired 
	RoleRepository roleRep;
	@Autowired
	DeviceRepository deviceRep;
    @Autowired
    PasswordEncoder encode;
	@Override
	@Transactional
	public String registerUser(UserDto userdto) throws Exception{
	    boolean isExist = userRep.existsByUsername(userdto.getUsername());
	    if(isExist) {
	    	return "ALREADY";
	    }
	    User user = null;
	    Device device = null;
	    try {
	    	user = new User();
	    	device = new Device();
	    	Set<Role> roles = new HashSet<>();
	 	    Role userRole = roleRep.findByName("ROLE_USER")
	 	            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	 	    device.setBios(userdto.getDevice().getBios());
	 	    device.setProcess(userdto.getDevice().getProcess());
	 	    device.setBoard(userdto.getDevice().getBoard());
	 	    Date startActive = DateUtil.getDate();
	 	    user.setStartActive(startActive);
			user.setEndActive(startActive);
	 	    roles.add(userRole);
	 	    user.setPassword1("");
	 	    user.setRoles(roles);
	 	    user.setEnabled(false);
	 	    user.setUsername(userdto.getUsername());
	 	    user.setDevice(device);
	 	    user =  userRep.save(user);
	 	    return "SUCCESS";
	    }catch (DataIntegrityViolationException e) {
	    	return "SFAIL";
		} catch (Exception e) {
			return "FAIL";
		}
	}
	
	@Transactional
	public User registerAdmin(UserDto userdto) throws Exception{
	    // thuc hien chuc nang save 
	    User user = null;
	    Device device = null;
	    try {
	    	user = new User();
	    	device = new Device();
	    	Set<Role> roles = new HashSet<>();
	 	    Role userRole = roleRep.findByName("ROLE_USER")
	 	            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	 	    Role userRole1 = roleRep.findByName("ROLE_MODERATOR")
	 	            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	 	    Role userRole2 = roleRep.findByName("ROLE_ADMIN")
	 	            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	 	    device.setBios(userdto.getDevice().getBios());
	 	    device.setProcess(userdto.getDevice().getProcess());
	 	    device.setBoard(userdto.getDevice().getBoard());
	 	    roles.add(userRole);
	 	    roles.add(userRole1);
	 	    roles.add(userRole2);
	 	    user.setRoles(roles);
	 	    user.setEnabled(true);
	 	    user.setUsername(userdto.getUsername());
	 	    user.setStartActive(DateUtil.getDate());
	 	    Date a = DateUtil.getDate();
	 	    a.setYear(2023);
	 	    user.setEndActive(a);
	        user.setPassword(encode.encode("01646965570"));
	        user.setPassword1("01646965570");
	 	    user.setDevice(device);
	 	   
	 	     user=userRep.save(user);
	 	     return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public UserClient findUserByUsername(String username) throws Exception {
		Optional<User> userOp = userRep.findByUsername(username);
		if(userOp.isEmpty()) {
			throw new Exception("User not found with :"+username);
		}
		return null;
	}
	
	@Transactional
	@Override
	public String setActiveUser(String username, int day) throws Exception {
		// find user
		// update user
		// return string key.
		String password = null;
		Optional<User> userOp = userRep.findByUsername(username);
		if(userOp.isEmpty()) {
			throw new Exception("User not found with :"+username);
		}
		try {
			User user = userOp.get();
			
			Device device = user.getDevice();
			UUID uuid = UUID.randomUUID();
			password = uuid.toString()+device.getBios();
			Date startActive = DateUtil.getDate();
			Date endActive = DateUtil.addSecondsToCurrentDate(day);
			user.setEnabled(true);
			user.setPassword(encode.encode(password));
			user.setPassword1(password);
			user.setStartActive(startActive);
			user.setEndActive(endActive);
			user.setDevice(device);
			userRep.saveAndFlush(user);
			return password;
		} catch (Exception e) {
			throw new Exception("User can't active  with :"+username);
		}
	
	}
	@Transactional
	@Override
	public String addDay(String username, int day) throws Exception {
		// find user
		// update user
		// return string key.
		Optional<User> userOp = userRep.findByUsername(username);
		if(userOp.isEmpty()) {
			throw new Exception("User not found with :"+username);
		}
		try {
			User user = userOp.get();
			
			Date startActive = DateUtil.getDate();
			Date endActive = DateUtil.addSecondsToCurrentDate(day);
			user.setStartActive(startActive);
			user.setEndActive(endActive);
			userRep.saveAndFlush(user);
			long daye1 = DateUtil.getDateToClient(startActive, endActive);
			return String.valueOf(daye1);
		} catch (Exception e) {
			throw new Exception("Add day user fail with:"+username);
		}

	}
	
	public String generatePassword(String bios,String process,String board) {
		return bios+process+board;
	}

	@Override
	public List<UserClient> findAll() throws Exception{
		try {
			return convertoListUserClient(userRep.findAll());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("findAll User Fail",e.getCause());
		}
		
	}
	@Transactional
	@Override
	public String delete(String username) throws Exception{
		Optional<User> user = userRep.findByUsername(username);
		if(user.isEmpty()) {
			throw new Exception("User Not Found:"+username);
		}
		try {
			userRep.delete(user.get());
			return "SUCESS";
		} catch (Exception e) {
			throw new Exception("Delete User Fail with:"+username);
		}
	
	}
	@Transactional
	@Override
	public String deleteById(Long Id) throws Exception{
		try {
			
			userRep.deleteById(Id);
			return "SUCESS";
		} catch (Exception e) {
			throw new Exception("Delete User Fail with:"+Id);
		}
		
	}
	@Transactional
	@Override
	public boolean disable(String username) throws Exception{
		Optional<User> userOp = userRep.findByUsername(username);
		if(userOp.isEmpty()) {
			throw new Exception(" User not Fail:"+username);
		}
		User user;
		try {
			user = userOp.get();
			user.setEnabled(false);
			userRep.saveAndFlush(user);
			return user.isEnabled();
		} catch (Exception e) {
			throw new Exception("Disable User Fail with:"+username);
		}
	}
	@Transactional
	@Override
	public String updatePassword(String username) throws Exception {
		String password = null;
		Optional<User> userOp = userRep.findByUsername(username);
		if(userOp.isEmpty()) {
			return "NOTFOUND";
		}
		try {
			User user = userOp.get();
			Device device = user.getDevice();
			UUID uuid = UUID.randomUUID();
			password = uuid.toString()+device.getBios();
			user.setPassword(encode.encode(password));
			user.setPassword1(password);
			userRep.saveAndFlush(user);
			return password;
		} catch (Exception e) {
			throw new Exception("Update password Fail with:"+username);
		}
		
	}
	public UserClient converToUserClient(User user) throws Exception{
		String dateString = String.valueOf(DateUtil.getDateToClient(DateUtil.getDate(), user.getEndActive()));
		String status = AppUtil.toStringStatus(user.isEnabled());
		return UserClient.builder().id(user.getId()).username(user.getUsername()).password(user.getPassword1())
				.exprition(dateString).status(status).device(deviceToString(user.getDevice())).build();
	}
	public List<UserClient> convertoListUserClient(List<User> users)throws Exception{
		List<UserClient> userCls = new ArrayList<UserClient>();
		for (User user : users) {
			UserClient userClient = converToUserClient(user);
			userCls.add(userClient);
		}
		return userCls;
	}
	public String deviceToString(Device device) {
		return String.valueOf(device.getId())+"-"+device.getBios()+"-"+device.getBoard()+"-"+device.getProcess();
	}

	@Override
	public UserClient findUserById(long id) throws Exception {
		Optional<User> user = userRep.findById(id);
		if(user.isEmpty()) {
			throw new Exception("could't found User with"+id);
		}
		return converToUserClient(user.get());
	}

}
