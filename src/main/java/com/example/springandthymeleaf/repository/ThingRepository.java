package com.example.springandthymeleaf.repository;

import com.example.springandthymeleaf.entity.Thing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThingRepository extends JpaRepository<Thing, Long> {
}
