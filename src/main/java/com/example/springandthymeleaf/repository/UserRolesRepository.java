package com.example.springandthymeleaf.repository;

import com.example.springandthymeleaf.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRolesRepository extends JpaRepository<UserRole, Long> {
}
