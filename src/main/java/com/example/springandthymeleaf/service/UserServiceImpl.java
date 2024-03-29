package com.example.springandthymeleaf.service;

import com.example.springandthymeleaf.DTO.UserDto;
import com.example.springandthymeleaf.entity.Role;
import com.example.springandthymeleaf.entity.User;
import com.example.springandthymeleaf.repository.RoleRepository;
import com.example.springandthymeleaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private LogService logService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, LogService logService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.logService = logService;
    }

    @Override
    public void saveUser(UserDto userDto) {

        User user = new User();
        user.setUserName(userDto.getUserName());

        // encrypt the password using spring security
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = roleRepository.findByName("READ_ONLY");
        if (role == null) {
            role = checkRoleExist();
        }
        user.setRoles(Arrays.asList(role));
        userRepository.save(user);

    }

    @Override
    public User findUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public User findUserById(Long userId) {
        logService.saveLog("INFO", "userId = " + userId);
        try {
            return userRepository.findById(userId).orElse(null);
        } catch (Exception e) {
            // обработка исключения, если не удалось выполнить запрос к базе данных
            logService.saveLog("INFO", "findUserById пустой" + " " + userId);
            return null;
        }
    }


    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }


    private UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setUserName(user.getUserName());
        userDto.setId(user.getId());
        userDto.setRole(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(", ")));

        return userDto;
    }

    private Role checkRoleExist() {
        Role role = new Role();
        role.setName("READ_ONLY");
        return roleRepository.save(role);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }
}
