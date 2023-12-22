package com.example.springandthymeleaf.service;

import com.example.springandthymeleaf.DTO.ThingDto;
import com.example.springandthymeleaf.entity.Thing;
import com.example.springandthymeleaf.entity.User;

import java.security.Principal;
import java.util.List;

public interface ThingService {

    void saveThing(ThingDto thingDto, Principal principal);

    List<ThingDto> findAllThings(Principal principal);

    void updateThing(Thing thing);

    User findThingById(Long thingId);
    Thing findThingByThingName(String thingName);
}
