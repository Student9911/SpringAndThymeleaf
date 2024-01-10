package com.example.springandthymeleaf.controller;

import com.example.springandthymeleaf.DTO.UserDto;
import com.example.springandthymeleaf.entity.Role;
import com.example.springandthymeleaf.entity.User;
import com.example.springandthymeleaf.repository.*;
import com.example.springandthymeleaf.service.LogService;
import com.example.springandthymeleaf.service.LogToFile;
import com.example.springandthymeleaf.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

@Slf4j
@Controller
public class PersonalController {
    private LogEntryRepository logEntryRepository;

    @Autowired
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    private final LogService logService;
    private final ThingRepository thingRepository;
    private final UserRolesRepository userRolesRepository;


    public PersonalController(LogEntryRepository logEntryRepository,
                              PasswordEncoder passwordEncoder,
                              UserRepository userRepository, UserService userService,
                              RoleRepository roleRepository, LogService logService,
                              ThingRepository thingRepository, UserRolesRepository userRolesRepository) {
        this.logEntryRepository = logEntryRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.logService = logService;
        this.thingRepository = thingRepository;
        this.userRolesRepository = userRolesRepository;
    }

    @GetMapping("/index")
    public String home(Model model) {
        model.addAttribute("title", "index");
        return "index";
    }

    @GetMapping("/info")
    public String info(Model model) {
        model.addAttribute("title", "infoPage");
        model.addAttribute("info", "Курсовой проект |\n" +
                "  Тема: Создать веб-приложение | группа: РИВ 220907у \n" +
                " | Студент: Урахов А.");
        return "infoPage";
    }

    @GetMapping("/user")
    public String users(Model model) {
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "list-usersForUser";
    }

    @GetMapping("/logs")
    public ModelAndView getAllLogs(Principal principal) {
        logService.saveLog("INFO", "User " +
                principal.getName() +
                " перешел на страницу логов ");

        ModelAndView mav = new ModelAndView("list-logs");
        mav.addObject("logs", logEntryRepository.findAll());
        return mav;
    }

    @GetMapping("/users/log")
    public String saveLogToFile(Principal principal) {
        LogToFile logToFile = new LogToFile(logEntryRepository, logService);
        return logToFile.exportLogs(principal);
    }


    @GetMapping("/users/deleteUser")
    public String deleteUser(@RequestParam Long userId, Principal principal) {
        logService.saveLog("INFO", "User " + principal.getName() +
                " удалил пользователя с ID: " + userId);
        log.info("connection -> deleteStudent");
        userRolesRepository.deleteById(userId);
        userRepository.deleteById(userId);

        return "redirect:/users";
    }

    @GetMapping("/showUpdateForm")
    public ModelAndView showUpdateForm(@RequestParam Long userId, Principal principal) {
        logService.saveLog("INFO", "User " + principal.getName() +
                " перешел на страницу showUpdateForm");
        ModelAndView mav = new ModelAndView("add-user-form");
        Optional<User> optionalStudent = userRepository.findById(userId);
        User user = new User();
        if (optionalStudent.isPresent()) {
            user = optionalStudent.get();
        }
        mav.addObject("user", user);
        return mav;
    }


    @PostMapping("/users/update")
    public String updateUser(@Valid @ModelAttribute("user") UserDto userDto,
                             @RequestParam("roles") String rolesValue,
                             Principal principal) {

        if (userService != null) {
            User existingUser = userService.findUserById(userDto.getId());
            if (existingUser.getId() == 1) {
                return "redirect:/showUpdateForm?error";
            }
            // Создаем новый объект User с обновленной ролью
            User updatedUser = new User();
            updatedUser.setId(existingUser.getId());

            String s;
            if (rolesValue.equals("1")) {
                s = "ROLE_ADMIN";
            } else if (rolesValue.equals("2")) {
                s = "ROLE_USER";
            } else {
                s = "READ_ONLY";

            }
            Role role = roleRepository.findByName(s);
            logService.saveLog("INFO", "User " + principal.getName() +
                    " изменил пользователя с логином: " + existingUser.getUserName() +
                    "\nпользователь после изменения: имя пользователя   '" + userDto.getUserName() + "'\n его роль: " + s);

            updatedUser.setUserName(userDto.getUserName());

            updatedUser.setRoles(Arrays.asList(role));

            // Обновляем пароль пользователя, если он был изменен
            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                if (!userDto.getPassword().equals(existingUser.getPassword())) {
                    updatedUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
                } else {
                    updatedUser.setPassword(existingUser.getPassword());
                }
                // Сохраняем обновленного пользователя в базе данных
                userService.updateUser(updatedUser);
                return "redirect:/users";
            }



        }
        return "redirect:/user/profile?error";

    }
}





