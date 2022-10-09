package com.telegram.dto;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter

public class DeviceDto {
	
	private Long id;

	private String bios;
	
	private String process;
	private String board;
	
    private UserDto user;
    public DeviceDto(String bios,String process,String board) {
    	this.bios = bios;	
        this.process= process;
        this.board = board;
    }

}
