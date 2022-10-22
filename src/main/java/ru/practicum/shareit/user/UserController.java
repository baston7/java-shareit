package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        User user = userService.getUser(userId);
        return UserMapper.toUserDto(user);
    }

    @GetMapping
    public List<UserDto> findAll() {
        List<User> users = userService.findAll();
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User createdUser = userService.createUser(user);
        return UserMapper.toUserDto(createdUser);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable long id) {
        userDto.setId(id);
        User oldUser = userService.getUser(id);
        UserDto oldUserDto = UserMapper.toUserDto(oldUser);
        if (userDto.getName()==null && userDto.getEmail()==null) {
            throw new ValidationException("Поля значений пустые");
        }
        if (userDto.getName()!=null && userDto.getEmail()!=null) {
            User user = UserMapper.toUser(userDto);
            User updatedUser = userService.updateUser(user);
            return UserMapper.toUserDto(updatedUser);
        }
        if (userDto.getName()==null && userDto.getEmail()!=null) {
            userDto.setName(oldUserDto.getName());
            User user = UserMapper.toUser(userDto);
            User updatedUser = userService.updateUser(user);
            return UserMapper.toUserDto(updatedUser);
        }
        userDto.setEmail(oldUserDto.getEmail());
        User user = UserMapper.toUser(userDto);
        User updatedUser = userService.updateUser(user);
        return UserMapper.toUserDto(updatedUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
