package com.example.springandthymeleaf.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    @NotEmpty
    private String userName;
    @NotEmpty(message = "Password should be empty")
    private String password;
}
