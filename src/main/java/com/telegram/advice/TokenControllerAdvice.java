package com.telegram.advice;
import java.util.Date;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.telegram.exception.TokenRefreshException;

@RestControllerAdvice
public class TokenControllerAdvice{
	 @ExceptionHandler(value = TokenRefreshException.class)
	  @ResponseStatus(HttpStatus.FORBIDDEN)
	  public ErrorMessage handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
	    return new ErrorMessage(
	        HttpStatus.FORBIDDEN.value(),
	        ex.getMessage(),
	        "",
	        request.getDescription(false));
	  }
	  @ExceptionHandler(value = DataIntegrityViolationException.class)
	  @ResponseStatus(HttpStatus.ALREADY_REPORTED)
	  public ErrorMessage handleTokenRefreshException1(DataIntegrityViolationException ex, WebRequest request) {
	    return new ErrorMessage(
	        HttpStatus.ALREADY_REPORTED.value(),
	        ex.getMessage(),
	        "",
	        request.getDescription(false));
	  }
	 @ExceptionHandler(value = Exception.class)
	  @ResponseStatus(HttpStatus.ALREADY_REPORTED)
	  public ErrorMessage handleTokenRefreshException12(Exception ex, WebRequest request) {
	    return new ErrorMessage(
	        HttpStatus.BAD_REQUEST.value(),
	        ex.getMessage(),
	        "",
	        request.getDescription(false));
	  }
}
