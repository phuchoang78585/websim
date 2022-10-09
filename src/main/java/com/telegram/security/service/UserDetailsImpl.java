package com.telegram.security.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.telegram.entity.Device;
import com.telegram.entity.User;
import com.telegram.util.DateUtil;
public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;
  private Long id;
  private String username;
  private String email;
  private Boolean enable;
  private Device device;
  private String secret;
  private String jti;
  private Boolean expri;
  private long secondLogin;
 
public String getJti() {
	return jti;
}
public void setJti(String jti) {
	this.jti = jti;
}
@JsonIgnore
  private String password;
  
  private Collection<? extends GrantedAuthority> authorities;
  public UserDetailsImpl(Long id, String username, String password,Device device,Boolean enable,String secret,Boolean expri,
      Collection<? extends GrantedAuthority> authorities,long second) {
    this.id = id;
    this.username = username;
    this.enable = enable;
    this.device =device;
    this.password = password;
    this.secret = secret;
    this.authorities = authorities;
    this.expri = expri;
    this.secondLogin = second;
  }
  public static UserDetailsImpl build(User user) {
	  long seoncd = 0;
	Date expried = user.getEndActive();
	Date now = DateUtil.getDate();
	if(expried != null) {
		seoncd  = DateUtil.getDateToClient(now, user.getEndActive());
	}
	boolean isExipri = DateUtil.dateBeforeEqualsDate(DateUtil.getDate(), expried);
    List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority(role.getName()))
        .collect(Collectors.toList());
    
    return new UserDetailsImpl(
        user.getId(), 
        user.getUsername(), 
        user.getPassword(),
        user.getDevice(), 
        user.isEnabled(),
        user.getPassword1(),
        isExipri,
        authorities,
        seoncd);
  }
  public long GetSecondLogin() {
	  return secondLogin;
  }
  public String getSecret() {
		return secret;
	}
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }
  public Device getDevice() {
	 return device;  
  }
  public Long getId() {
    return id;
  }
  public String getEmail() {
    return email;
  }
  @Override
  public String getPassword() {
    return password;
  }
  @Override
  public String getUsername() {
    return username;
  }
  @Override
  public boolean isAccountNonExpired() {
    return expri;
  }
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }
  @Override
  public boolean isEnabled() {
    return enable;
  }
  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }
}
