package com.example.springandthymeleaf.controller;

import com.example.springandthymeleaf.DTO.UserDto;
import com.example.springandthymeleaf.entity.Role;
import com.example.springandthymeleaf.entity.User;
import com.example.springandthymeleaf.repository.LogEntryRepository;
import com.example.springandthymeleaf.repository.RoleRepository;
import com.example.springandthymeleaf.repository.UserRepository;
import com.example.springandthymeleaf.service.LogService;
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
import java.security.Principal;
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

    private final LogService logService;


    public PersonalController(LogEntryRepository logEntryRepository,
                              PasswordEncoder passwordEncoder,
                              UserRepository userRepository, UserService userService,
                              RoleRepository roleRepository, LogService logService) {
        this.logEntryRepository = logEntryRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.logService = logService;
    }


    @GetMapping("/log")
    public String getPeople(Model model) {
        //logService.saveLog("переход /log", "");
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
    public ModelAndView getAllLogs(Principal principal) {
        logService.saveLog("INFO", "User " +
                principal.getName() +
                " перешел на страницу логов ");

        ModelAndView mav = new ModelAndView("list-logs");
        mav.addObject("logs", logEntryRepository.findAll());
        return mav;
    }

    @GetMapping("/deleteUser")
    public String deleteUser(@RequestParam Long userId, Principal principal) {
        logService.saveLog("INFO", "User " + principal.getName() +
                " удалил пользователя с ID: " + userId);
        log.info("connection -> deleteStudent");
        userRepository.deleteById(userId);
        return "redirect:/users";
    }

    @GetMapping("/showUpdateForm")
    public ModelAndView showUpdateForm(@RequestParam Long userId, Principal principal) {
        logService.saveLog("INFO", "User " + principal.getName() +
                " перешел на страницу showUpdateForm");
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
    public String saveStudent(@ModelAttribute User student, Principal principal) {
        logService.saveLog("INFO", "User " + principal.getName() +
                " сохранил пользователя с логином: " + student.getUserName());
        userRepository.save(student);
        return "redirect:/users";

    }

    @PostMapping("/user/update")
    public String updateUser(@Valid @ModelAttribute("user") UserDto userDto,
                             @RequestParam("roles") String rolesValue,
                             Principal principal) {

        if (userService != null) {
            User existingUser = userService.findUserById(userDto.getId());
            if (existingUser.getId() == 1) {
                return "redirect:/user/profile?error";
            }
            // Создаем новый объект User с обновленной ролью
            User updatedUser = new User();
            updatedUser.setId(existingUser.getId());

            String s;
            if (rolesValue.equals("1")) {
                s = "ROLE_ADMIN";
            } else {
                s = "ROLE_USER";
            }
            Role role = roleRepository.findByName(s);
            logService.saveLog("INFO", "User " + principal.getName() +
                    " изменил пользователя с логином: " + existingUser.getUserName() +
                    "\nпользователь после изменения: имя пользователя   '" + userDto.getUserName() + "'\n его роль: " + s);

            updatedUser.setUserName(userDto.getUserName());

            updatedUser.setRoles(Arrays.asList(role));

            // Обновляем пароль пользователя, если он был изменен
            if (!userDto.getPassword().isEmpty()) {
                updatedUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }
            // Сохраняем обновленного пользователя в базе данных
            userService.updateUser(updatedUser);
            return "redirect:/users";
        }
        // Handle the case when userService is null
        // Log an error or throw an exception

        return "redirect:/user/profile?error";
    }

}
