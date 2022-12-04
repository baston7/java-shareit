package user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import user.dto.UserDto;
import user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        User user = userService.getUser(userId);
        return UserMapper.toUserDto(user);
    }

    @GetMapping
    public List<UserDto> findAll() {
        List<User> users = userService.findAll();
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User createdUser = userService.createUser(user);
        return UserMapper.toUserDto(createdUser);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable long id) {
        userDto.setId(id);
        User oldUser = userService.getUser(id);
        User newUser = UserMapper.toUser(userDto);
        userService.setNewFieldsForUpdate(newUser, oldUser);
        User updatedUser = userService.updateUser(newUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
