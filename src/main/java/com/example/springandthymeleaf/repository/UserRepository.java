package com.example.springandthymeleaf.repository;

import com.example.springandthymeleaf.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String userName);
}
