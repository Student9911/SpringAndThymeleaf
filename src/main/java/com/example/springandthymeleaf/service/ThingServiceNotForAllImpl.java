package com.example.springandthymeleaf.service;

import com.example.springandthymeleaf.entity.Role;
import com.example.springandthymeleaf.entity.Thing;
import com.example.springandthymeleaf.entity.User;
import com.example.springandthymeleaf.repository.ThingRepository;
import com.example.springandthymeleaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class ThingServiceNotForAllImpl implements ThingServiceNotForAll{

    private final ThingRepository thingRepository;
    private final UserRepository userRepository;

    @Autowired
    public ThingServiceNotForAllImpl(ThingRepository thingRepository, UserRepository userRepository) {
        this.thingRepository = thingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Thing> thingsForUsers(Principal principal) {
        User user = userRepository.findByUserName(principal.getName());
        String s = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));
        System.out.println(s);
        List<Thing> things;
        if (s.equals("ROLE_USER")) {
            things = thingRepository.findAll().stream()
                    .filter(thing -> thing.getUserNames().equals(principal.getName()))
                    .toList();

        } else {
            things = thingRepository.findAll();

        }  return things;







    }
}
