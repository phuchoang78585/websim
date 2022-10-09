package com.telegram.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telegram.entity.Device;

public interface DeviceRepository extends JpaRepository<Device,Long>{

}
