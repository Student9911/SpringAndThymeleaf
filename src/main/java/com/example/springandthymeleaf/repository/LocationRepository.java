package com.example.springandthymeleaf.repository;

import com.example.springandthymeleaf.entity.Location;
import com.example.springandthymeleaf.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByName(String name);
}
