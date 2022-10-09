package com.telegram.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "username") })
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column
	private String username;
	@Column
	private String password;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
	inverseJoinColumns = @JoinColumn(name = "role_id")) 
	private Set<Role> roles =new HashSet<>();
	
	@OneToOne(targetEntity = Device.class,cascade = CascadeType.ALL) @JoinColumn( name = "device_id" )
	
    private Device device;
	@Column
    private boolean enabled;
	

	@Column
    private String password1;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_active")
	private Date startActive;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_active")
	private Date endActive;
	public User() {
	}
	public Date getStartActive() {
		return startActive;
	}

	public void setStartActive(Date startActive) {
		this.startActive = startActive;
	}

	public Date getEndActive() {
		return endActive;
	}

	public void setEndActive(Date endActive) {
		this.endActive = endActive;
	}
	
	public User( String username,Device device) {
		super();
		this.username = username;
		this.password = null;
		this.device = device;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	  public Set<Role> getRoles() { return roles; } public void setRoles(Set<Role>
	  roles) { this.roles = roles; }
	  public Device getDevice() {
			return device;
		}

		public void setDevice(Device device) {
			this.device = device;
		}

		public String getPassword1() {
			return password1;
		}
		public void setPassword1(String password1) {
			this.password1 = password1;
		}
		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		
	 
}
