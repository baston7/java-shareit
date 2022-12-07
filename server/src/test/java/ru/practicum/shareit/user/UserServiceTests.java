package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    private UserService userService;
    User user = new User("Вася", "b@mail.ru");
    User user2 = new User("Петя", "p@mail.ru");
    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void createService() {
        userService = new UserService(userRepository);
    }

    @Test
    public void testAddUser() {

        Mockito
                .when(userRepository.save(any(User.class)))
                .thenReturn(user);
        User user1 = userService.createUser(new User("Вася", "b@mail.ru"));
        assertEquals(user.getName(), user1.getName());
    }

    @Test
    public void testGetWithoutUser() {
        assertThrows(UserNotFoundException.class, () -> userService.getUser(6));
    }

    @Test
    public void testGetCreatedUser() {
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        User user1 = userService.getUser(1);
        assertEquals(user.getName(), user1.getName());
    }

    @Test
    public void testSetNewFieldsForUpdateEmptyUser() {
        user2.setName(null);
        user2.setEmail(null);
        assertThrows(ValidationException.class, () -> userService.setNewFieldsForUpdate(user2, user));
    }

    @Test
    public void testSetNewFieldsForUpdateEmptyName() {
        user2.setName(null);
        userService.setNewFieldsForUpdate(user2, user);
        assertEquals(user2.getName(), user.getName());
    }

    @Test
    public void testSetNewFieldsForUpdateEmptyEmail() {
        user2.setEmail(null);
        userService.setNewFieldsForUpdate(user2, user);
        assertEquals(user2.getEmail(), user.getEmail());
    }

    @Test
    public void testFindAll() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(user, user2));
        List<User> users = userService.findAll();
        assertEquals(2, users.size());
    }

    @Test
    public void testUpdateUser() {
        Mockito
                .when(userRepository.save(any(User.class)))
                .thenReturn(user);
        User user1 = userService.updateUser(new User("Вася", "b@mail.ru"));
        assertEquals(user.getName(), user1.getName());
    }
}
