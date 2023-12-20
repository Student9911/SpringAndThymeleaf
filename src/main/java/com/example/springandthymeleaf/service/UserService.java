package com.example.springandthymeleaf.service;

import com.example.springandthymeleaf.DTO.UserDto;
import com.example.springandthymeleaf.entity.User;

import java.util.List;
import java.util.Optional;


public interface UserService {

    void saveUser(UserDto userDto);


    User findUserByUserName(String userName);

    List<UserDto> findAllUsers();
    void updateUser(User user);
    User findUserById(Long userId);

}
