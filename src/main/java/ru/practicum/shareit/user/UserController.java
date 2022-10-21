package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

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
    public void getUser(@PathVariable long userId) {

    }
    @GetMapping
    public void findAll(@PathVariable long userId) {

    }
    @PostMapping
    public void createUser(@RequestBody @Valid UserDto userDto) {

    }

    @PatchMapping
    public void updateUser(@RequestBody @Valid UserDto userDto) {

    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {

    }
}
