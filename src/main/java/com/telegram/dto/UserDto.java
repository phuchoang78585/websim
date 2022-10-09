package com.telegram.dto;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })*/


@Getter
@Setter

public class UserDto {

	@Email
	private String username;
	
    private DeviceDto device;
  
 
	public UserDto(String username, DeviceDto device) {
		super();
		this.username = username;
		this.device = device;
	}
    
}
