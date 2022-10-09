package com.telegram.service.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.telegram.dto.UserClient;
import com.telegram.dto.UserDto;


public interface IUserService  {

	public String registerUser(UserDto user) throws Exception;
	public UserClient findUserByUsername(String username) throws Exception;
	public UserClient findUserById(long id) throws Exception;
	public String setActiveUser(String username,int day)throws Exception;
	public List<UserClient> findAll()  throws Exception;
	public String delete(String username) throws Exception;
	public String deleteById(Long Id)throws Exception ;
	public boolean disable(String username)throws Exception ;
	public String updatePassword(String username)throws Exception ;
	public String addDay(String username, int day) throws Exception ;
	
	
}
