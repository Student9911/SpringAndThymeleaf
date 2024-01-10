package com.example.springandthymeleaf.config;
import com.example.springandthymeleaf.service.LogService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, LogService logService) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/register/").permitAll()
                .antMatchers("/index").permitAll()
                .antMatchers("/users/").hasRole("ADMIN")
                .antMatchers("/user/").hasAnyRole("ONLY", "USER", "ADMIN")
                .and()
                .formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/index") // Общая страница для всех пользователей
                                .successHandler((request, response, authentication) -> {
                                    for (GrantedAuthority authority : authentication.getAuthorities()) {
                                        if (authority.getAuthority().equals("ROLE_ADMIN")) {
                                            response.sendRedirect("/users"); // Перенаправляем администраторов
                                            return;
                                        } else if (authority.getAuthority().equals("ROLE_USER")) {
                                            response.sendRedirect("/user"); // Перенаправляем обычных пользователей
                                            return;
                                        } else if (authority.getAuthority().equals("READ_ONLY")) {
                                            response.sendRedirect("/user"); // Перенаправляем обычных пользователей
                                            return;
                                        }
                                    }
                                })
                                .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessHandler((request, response, authentication) -> {
                            logService.saveLog("выход из системы", "пользователь: " + authentication.getName() + " вышел из системы");
                            response.sendRedirect("/login");
                        })
                        .permitAll()
                );

        return http.build();
    }
}