package com.example.springandthymeleaf.controller;

import com.example.springandthymeleaf.DTO.UserDto;
import com.example.springandthymeleaf.entity.User;
import com.example.springandthymeleaf.repository.UserRepository;
import com.example.springandthymeleaf.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;


@Slf4j


@Controller
public class LoginController {
    private UserService userService;


    public LoginController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "login");
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "Login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("User") UserDto userDto,
                               BindingResult result,
                               Model model) {
        log.error("connect -> registration" + " ---" + userDto.getUserName());
        if (userService != null) {
            User existingUser = userService.findUserByUserName(userDto.getUserName());
            if (existingUser != null && existingUser.getUserName() != null && !existingUser.getUserName().isEmpty()) {
                log.error("пользователь с таким именем уже существует");
                result.rejectValue("userName", null,
                        "пользователь с таким именем уже существует");
                return "redirect:/register?error";
            }
        } else {

        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "/register";
        }

        userService.saveUser(userDto);
        return "redirect:/register?success";
    }


    @GetMapping("/users")
    public String users(Model model) {
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }


}
