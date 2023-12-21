package com.example.springandthymeleaf.service;

import com.example.springandthymeleaf.entity.Thing;

import java.security.Principal;
import java.util.List;

public interface ThingServiceNotForAll {

    List<Thing> thingsForUsers(Principal principal);
}
