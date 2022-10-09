package com.telegram.entity;

import javax.persistence.*;

@Entity
@Table(name = "roles",uniqueConstraints = { @UniqueConstraint(columnNames = "name") })
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(name = "name")
  private String name;
  public Role() {
  }
  public Role(String name) {
    this.name = name;
  }
  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
}
