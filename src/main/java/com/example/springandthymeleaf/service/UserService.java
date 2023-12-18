package com.example.springandthymeleaf.service;

import com.example.springandthymeleaf.DTO.UserDto;
import com.example.springandthymeleaf.entity.User;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;


public interface UserService {

    void saveUser(UserDto userDto);


    User findUserByUserName(String userName);

    List<UserDto> findAllUsers();

}
