package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserDto {
    long id;
    @NotBlank(message = "Имя пользоваетля не может быть пустым")
    String name;
    @Email(message = "email не соответсвует формату")
    @NotBlank(message = "email не указан")
    String email;
}