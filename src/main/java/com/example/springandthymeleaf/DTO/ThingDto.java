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
public class ThingDto {

    private Long id;
    @NotEmpty
    private String name;
    private String userName;
    private String quantity;
    private String locationThing;
}
