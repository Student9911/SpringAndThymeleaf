package com.example.springandthymeleaf.service;

import com.example.springandthymeleaf.entity.Role;
import com.example.springandthymeleaf.entity.User;
import com.example.springandthymeleaf.repository.RoleRepository;
import com.example.springandthymeleaf.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;
    private LogService logService;




    public CustomUserDetailsService(UserRepository userRepository, LogService logService) {
        this.userRepository = userRepository;
        this.logService = logService;



    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (user != null) {
            String roles = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", "));

            logService.saveLog("Вход в систему ", user.getUserName() + " вошел в систему " + "в роли " + roles);
            return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
                    user.getRoles().stream()
                            .map((role) -> new SimpleGrantedAuthority(role.getName()))
                            .collect(Collectors.toList()));
        } else {
            logService.saveLog("неудачный вход", "неправильный логин или пароль");
            throw new UsernameNotFoundException("Invalid nikName or password");

        }
    }
}
