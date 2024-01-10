package com.example.springandthymeleaf.controller;

import com.example.springandthymeleaf.DTO.ThingDto;
import com.example.springandthymeleaf.entity.Location;
import com.example.springandthymeleaf.entity.Role;
import com.example.springandthymeleaf.entity.Thing;
import com.example.springandthymeleaf.repository.LocationRepository;
import com.example.springandthymeleaf.repository.ThingLocationRepository;
import com.example.springandthymeleaf.repository.ThingRepository;
import com.example.springandthymeleaf.repository.UserRepository;
import com.example.springandthymeleaf.service.LogService;
import com.example.springandthymeleaf.service.ThingService;
import com.example.springandthymeleaf.service.ThingServiceNotForAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class ThingController {

    private final LogService logService;
    private final UserRepository userRepository;

    private final ThingRepository thingRepository;
    private final ThingServiceNotForAll thingServiceNotForAll;
    private final ThingService thingService;
    private final ThingLocationRepository thingLocationRepository;
    private final LocationRepository locationRepository;

    public ThingController(LogService logService, UserRepository userRepository, ThingRepository thingRepository, ThingServiceNotForAll thingServiceNotForAll, ThingService thingService, ThingLocationRepository thingLocationRepository, LocationRepository locationRepository) {
        this.logService = logService;
        this.userRepository = userRepository;
        this.thingRepository = thingRepository;
        this.thingServiceNotForAll = thingServiceNotForAll;
        this.thingService = thingService;
        this.thingLocationRepository = thingLocationRepository;
        this.locationRepository = locationRepository;
    }


    @GetMapping("/thing/things")
    public String things(Model model, Principal principal) {
        List<ThingDto> things = thingService.findAllThings(principal);
        model.addAttribute("things", things);
        String user = userRepository.findByUserName(principal.getName())
                .getRoles().stream().map(Role::getName)
                .collect(Collectors.joining(", "));
        if (user.equals("READ_ONLY")) {
            return "thingsForReadOnly";
        }
        return "things";
    }

    @GetMapping("/thing/addThingForm")
    public ModelAndView addThingForm(Principal principal) {
        ModelAndView mav = new ModelAndView("add-thing-form");
        Thing thing = new Thing();
        thing.setThingName(principal.getName());
        mav.addObject("thing", thing);
        return mav;
    }

    @PostMapping("/thing/saveThing")
    public String saveThing(@ModelAttribute("Thing") ThingDto thingDto, Model model, BindingResult result,
                            Principal principal) {
        log.error("connect -> registration" + " ---" + thingDto.getName());
        if (thingService != null) {
            Thing existingThing = thingService.findThingByThingName(thingDto.getName());
            if (existingThing != null && existingThing.getName() != null && !existingThing.getName().isEmpty()) {
                log.error("пользователь с таким именем уже существует");
                result.rejectValue("ThingName", null,
                        "вещь с таким именем уже существует");
                return "redirect:/thing/things?error";
            } else {

            }
            if (result.hasErrors()) {
                model.addAttribute("thing", thingDto);
                return "/thing/showUpdateForm";
            }

            thingService.saveThing(thingDto, principal);

        }
        return "redirect:/thing/things";
    }

    @GetMapping("/thing/showUpdateForm")
    public ModelAndView showUpdateFormThing(@RequestParam Long thingId, Principal principal) {

        logService.saveLog("INFO", "User " + principal.getName() +
                " перешел на страницу showUpdateForm и пытается изменить вещь с ID = " + thingId);
        ModelAndView mav = new ModelAndView("update-thing-form");
        Optional<Thing> optionalThing = thingRepository.findById(thingId);
        Thing thing = new Thing();
        if (optionalThing.isPresent()) {
            thing = optionalThing.get();
        }
        mav.addObject("thing", thing);
        return mav;
    }

    @PostMapping("/things/update")
    public String updateUser(@Valid @ModelAttribute("thing") ThingDto thingDto,
                             @RequestParam("location") String rolesValue,
                             Principal principal) {
        log.error("в /things/update " + thingDto.getId() + " " + thingDto.getName());

        if (thingService != null) {
            log.error("в /things/update thingService != null");

            Thing updatedThing = new Thing();
            updatedThing.setId(thingDto.getId());
            updatedThing.setThingName(principal.getName());
            updatedThing.setName(thingDto.getName());
            updatedThing.setQuantity(thingDto.getQuantity());

            String s;
            if (rolesValue.equals("1")) {
                s = "MOSCOW";
            } else if (rolesValue.equals("2")) {
                s = "EKATERINBURG";

            } else {
                s = "OnTheWay";

            }
            Location location = locationRepository.findByName(s);
            logService.saveLog("INFO", "User " + principal.getName() +
                    " изменил пользователя с логином: " + updatedThing.getName() +
                    "\nпользователь после изменения: имя пользователя   '" + thingDto.getUserName()
                    + "'\n его роль: " + s);


            updatedThing.setLocation(Arrays.asList(location));
            log.error("в /things/update thingService != null " + updatedThing.getName() + " "
                    + updatedThing.getThingName() + updatedThing.getQuantity() + " " + s);

            // Сохраняем обновленного пользователя в базе данных
            thingService.updateThing(updatedThing);

            return "redirect:/thing/things";


        }


        return "redirect:/thing/things/profile?error";

    }

    @GetMapping("/thing/deleteThing")
    public String deleteThing(@RequestParam Long thingId) {
        thingLocationRepository.deleteById(thingId);
        thingRepository.deleteById(thingId);
        return "redirect:/thing/things";
    }


}


