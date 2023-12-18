package com.example.springandthymeleaf.repository;

import com.example.springandthymeleaf.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

}
