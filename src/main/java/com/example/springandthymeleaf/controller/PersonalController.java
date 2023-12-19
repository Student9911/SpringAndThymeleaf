package com.example.springandthymeleaf.controller;

import com.example.springandthymeleaf.DTO.UserDto;
import com.example.springandthymeleaf.entity.Role;
import com.example.springandthymeleaf.entity.User;
import com.example.springandthymeleaf.repository.LogEntryRepository;
import com.example.springandthymeleaf.repository.RoleRepository;
import com.example.springandthymeleaf.repository.UserRepository;
import com.example.springandthymeleaf.service.LogToFile;
import com.example.springandthymeleaf.service.Persons;
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
import java.util.Arrays;
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


    public PersonalController(LogEntryRepository logEntryRepository,
                              PasswordEncoder passwordEncoder,
                              UserRepository userRepository, UserService userService,
                              RoleRepository roleRepository) {
        this.logEntryRepository = logEntryRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }


    @GetMapping("/log")
    public String getPeople(Model model) {
        LogToFile log = new LogToFile(logEntryRepository);
        log.exportLogs();
        model.addAttribute("something", "hello in controller");
        model.addAttribute("people", Arrays.asList(
                new Persons("John", 20),
                new Persons("Sarah", 21),
                new Persons("Simon", 22)
        ));
        return "ListPeople";
    }

    @GetMapping("/logs")
    public ModelAndView getAllLogs() {
        log.info(("/Logs -> connection"));
        ModelAndView mav = new ModelAndView("list-logs");
        mav.addObject("logs", logEntryRepository.findAll());
        return mav;
    }

    @GetMapping("/deleteUser")
    public String deleteUser(@RequestParam Long userId) {
        log.info("connection -> deleteStudent");
        userRepository.deleteById(userId);
        return "redirect:/users";
    }

    @GetMapping("/showUpdateForm")
    public ModelAndView showUpdateForm(@RequestParam Long userId) {
        ModelAndView mav = new ModelAndView("add-student-form");
        Optional<User> optionalStudent = userRepository.findById(userId);
        User user = new User();
        if (optionalStudent.isPresent()) {
            user = optionalStudent.get();
        }
        mav.addObject("user", user);
        return mav;
    }

    @PostMapping("/saveStudent")
    public String saveStudent(@ModelAttribute User student) {
        userRepository.save(student);
        return "redirect:/users";

    }

    @PostMapping("/user/update")
    public String updateUser(@Valid @ModelAttribute("user") UserDto userDto, @RequestParam("roles") String rolesValue) {
        log.error("connect -> updateUser" + " ---" + userDto.getUserName() + " " + userDto.getId() + rolesValue);

        if (userService != null) {
            User existingUser = userService.findUserByUserName(userDto.getUserName());

            if (existingUser != null) {
                // Создаем новый объект User с обновленной ролью
                User updatedUser = new User();
                updatedUser.setId(existingUser.getId());
                updatedUser.setUserName(existingUser.getUserName());

                String s;
                if (rolesValue.equals("1")) {
                    s = "ROLE_ADMIN";
                } else {
                    s = "ROLE_USER";
                }
                Role role = roleRepository.findByName(s);

                updatedUser.setRoles(Arrays.asList(role));

                // Обновляем пароль пользователя, если он был изменен
                if (!userDto.getPassword().isEmpty()) {
                    updatedUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
                }

                // Сохраняем обновленного пользователя в базе данных
                userService.updateUser(updatedUser);
                return "redirect:/users";
            }
        } else {
            // Handle the case when userService is null
            // Log an error or throw an exception
        }
        return "redirect:/user/profile?error";
    }

}
