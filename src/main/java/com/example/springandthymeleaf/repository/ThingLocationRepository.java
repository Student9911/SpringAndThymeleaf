package com.example.springandthymeleaf.repository;

import com.example.springandthymeleaf.entity.ThingLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThingLocationRepository extends JpaRepository<ThingLocation, Long> {
}
