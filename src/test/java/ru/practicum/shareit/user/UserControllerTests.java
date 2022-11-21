package ru.practicum.shareit.user;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTests {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;
    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDto userDto;
    private UserDto userDto2;
    private UserDto userDtoUpdate;
    private UserDto userDtoNotValidEmail;
    private UserDto userDtoNotEmail;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        userDto = new UserDto(1L, "Vasia", "vasia@mail.ru");
        userDtoUpdate = new UserDto( "Vasiliy", "vasia@mail.ru");
        userDtoNotValidEmail = new UserDto(1L, "Vasia", "vasiaail.ru");
        userDtoNotEmail = new UserDto(1L, "Vasia", "");
        userDto2 = new UserDto(2L, "Petia", "petia@mail.ru");
    }

    @Test
    void testSaveNewUser() throws Exception {
        when(userService.createUser(any()))
                .thenReturn(UserMapper.toUser(userDto));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void testSaveNewUserNotValidEmail() throws Exception {
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoNotValidEmail))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void testSaveNewUserNoEmail() throws Exception {
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoNotEmail))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }
    @Test
    void testUserUpdate() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toUser(userDto));
        when(userService.updateUser(any()))
                .thenReturn(UserMapper.toUser(userDtoUpdate));
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDtoUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDtoUpdate.getName())))
                .andExpect(jsonPath("$.email", is(userDtoUpdate.getEmail())));
    }
    @Test
    void testGetUser() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(UserMapper.toUser(userDto));
        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void testFindAll() throws Exception {
        List<User> users= Stream.of(userDto2,userDto).map(UserMapper::toUser).collect(Collectors.toList());;

        when(userService.findAll())
                .thenReturn(users);

        mvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].name", is(userDto2.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto2.getEmail())))
                .andExpect(jsonPath("$[1].id", is(1)))
                .andExpect(jsonPath("$[1].name", is(userDto.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto.getEmail())));
    }
    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
