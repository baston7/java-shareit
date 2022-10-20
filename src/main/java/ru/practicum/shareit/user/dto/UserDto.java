package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class UserDto {
    private long id;
    private String name;
    @Email
    private String email;
}

