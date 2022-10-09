package com.telegram.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserClient {

	private long id;
	private String username;
	private String password;
	private String exprition;
	private String status;
	private String device;
	

}
