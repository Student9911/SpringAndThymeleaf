package com.example.springandthymeleaf.service;

import com.example.springandthymeleaf.DTO.ThingDto;
import com.example.springandthymeleaf.entity.Location;
import com.example.springandthymeleaf.entity.Role;
import com.example.springandthymeleaf.entity.Thing;
import com.example.springandthymeleaf.entity.User;
import com.example.springandthymeleaf.repository.LocationRepository;
import com.example.springandthymeleaf.repository.ThingRepository;
import com.example.springandthymeleaf.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j

@Service
public class ThingServiceImpl implements ThingService {

    private final ThingRepository thingRepository;
    private final LogService logService;

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    @Autowired
    public ThingServiceImpl(ThingRepository thingRepository, LogService logService, LocationRepository locationRepository, UserRepository userRepository) {
        this.thingRepository = thingRepository;
        this.logService = logService;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveThing(ThingDto thingDto, Principal principal) {

        Thing thing = new Thing();
        thing.setName(thingDto.getName());
        thing.setThingName(principal.getName());
        thing.setQuantity(thingDto.getQuantity());
        log.error("в saveThing " + thing.getName() + thing.getThingName() + thing.getQuantity());

        Location location = locationRepository.findByName("OnTheWay");
        if (location == null) {
            location = checkLocationExist();
        }
        thing.setLocation(List.of(location));

        thingRepository.save(thing);

    }

    @Override
    public List<ThingDto> findAllThings(Principal principal) {
        User user = userRepository.findByUserName(principal.getName());
        String s = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));
        System.out.println(s);
        List<Thing> things;
        if (s.equals("ROLE_USER")) {
            things = thingRepository.findAll().stream()
                    .filter(thing -> thing.getThingName().equals(principal.getName()))
                    .toList();

        } else {
            things = thingRepository.findAll();

        }
        return things.stream()
                .map((thing) -> mapToThingDto(thing))
                .collect(Collectors.toList());
    }

    private ThingDto mapToThingDto(Thing thing) {
        ThingDto thingDto = new ThingDto();

        thingDto.setName(thing.getName());
        thingDto.setUserName(thing.getThingName());
        thingDto.setId(thing.getId());
        thingDto.setQuantity(thing.getQuantity());
        thingDto.setLocationThing(thing.getLocation().stream()
                .map(Location::getName)
                .collect(Collectors.joining(", ")));
        return thingDto;
    }

    @Override
    public void updateThing(Thing thing) {
        thingRepository.save(thing);

    }

    @Override
    public User findThingById(Long thingId) {
        return null;
    }

    @Override
    public Thing findThingByThingName(String thingName) {
        return thingRepository.findByThingName(thingName);
    }

    private Location checkLocationExist() {
        Location location = new Location();
        location.setName("OnTheWay");
        return locationRepository.save(location);
    }
}
