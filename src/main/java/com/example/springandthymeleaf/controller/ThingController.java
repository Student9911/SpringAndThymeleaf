package com.example.springandthymeleaf.controller;

import com.example.springandthymeleaf.entity.Thing;
import com.example.springandthymeleaf.repository.ThingRepository;
import com.example.springandthymeleaf.repository.UserRepository;
import com.example.springandthymeleaf.service.LogService;
import com.example.springandthymeleaf.service.ThingServiceNotForAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class ThingController {

    private final LogService logService;
    private final UserRepository userRepository;

    private final ThingRepository thingRepository;
    private final ThingServiceNotForAll thingServiceNotForAll;

    public ThingController(LogService logService, UserRepository userRepository, ThingRepository thingRepository, ThingServiceNotForAll thingServiceNotForAll) {
        this.logService = logService;
        this.userRepository = userRepository;
        this.thingRepository = thingRepository;
        this.thingServiceNotForAll = thingServiceNotForAll;
    }


    @GetMapping("/thing/things")
    public String things(Model model, Principal principal) {
        //List<Thing> things = thingRepository.findAll();
        List<Thing> things = thingServiceNotForAll.thingsForUsers(principal);
        model.addAttribute("things", things);
        return "things";
    }

    @GetMapping("/thing/addThingForm")
    public ModelAndView addThingForm(Principal principal) {
        ModelAndView mav = new ModelAndView("add-thing-form");
        Thing thing = new Thing();
        thing.setUserNames(principal.getName());
        mav.addObject("thing", thing);
        return mav;
    }

    @PostMapping("/thing/saveThing")
    public String saveThing(@ModelAttribute Thing thing, Principal principal) {
        thing.setUserNames(principal.getName());
        logService.saveLog("INFO", "пользователь: " + principal.getName() +
                " добавил или изменил вещь: " + thing.getName() +
                " c ID: " + thing.getId());
        thingRepository.save(thing);
        return "redirect:/thing/things";
    }

    @GetMapping("/thing/showUpdateForm")
    public ModelAndView showUpdateFormThing(@RequestParam Long thingId, Principal principal) {
        System.out.println("в методе /thing/showUpdateForm");
        logService.saveLog("INFO", "User " + principal.getName() +
                " перешел на страницу showUpdateForm и пытается изменить вещь с ID = " + thingId);
        ModelAndView mav = new ModelAndView("add-thing-form");
        Optional<Thing> optionalThing = thingRepository.findById(thingId);
        Thing thing = new Thing();
        if (optionalThing.isPresent()) {
            thing = optionalThing.get();
        }
        mav.addObject("thing", thing);
        return mav;
    }

    @GetMapping("/thing/deleteThing")
    public String deleteThing(@RequestParam Long thingId) {
        thingRepository.deleteById(thingId);
        return "redirect:/thing/things";
    }
}
