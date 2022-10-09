package com.telegram.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.telegram.entity.ERole;
import com.telegram.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long>{
	Optional<Role> findByName(String name);
}