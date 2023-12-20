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
                .antMatchers("/register/**").permitAll()
                .antMatchers("/index").permitAll()
                .antMatchers("/users/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasRole("USER")
                .and()
                .formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/default") // Общая страница для всех пользователей
                                .permitAll()
                )  .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessHandler((request, response, authentication) -> {
                            logService.saveLog("выход из системы", "пользователь: " + authentication.getName() + " вышел из системы");
                            response.sendRedirect("/login");
                        })
                        .permitAll()
                );

        // Добавляем настройку перенаправления для пользователей с различными ролями
        http.formLogin().successHandler((request, response, authentication) -> {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    response.sendRedirect("/users"); // Перенаправляем администраторов
                    return;
                } else if (authority.getAuthority().equals("ROLE_USER")) {
                    response.sendRedirect("/user"); // Перенаправляем обычных пользователей
                    return;
                }
            }

        });

        return http.build();
    }
}