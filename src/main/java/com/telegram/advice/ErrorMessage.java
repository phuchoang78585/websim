package com.telegram.advice;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorMessage {
  private int statusCode;
  private String message;
  private Object data;
  private String description;
public ErrorMessage(int statusCode, String message, Object data, String description) {
	super();
	this.statusCode = statusCode;
	this.message = message;
	this.data = data;
	this.description = description;
}
  
}
