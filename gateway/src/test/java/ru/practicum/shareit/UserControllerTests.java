package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTests {
    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserController controller;
    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;
    private UserDto userDto;
    private UserDto userDtoNotValidEmail;
    private UserDto userDtoNotEmail;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        userDtoNotValidEmail = new UserDto(1L, "Vasia", "vasiaail.ru");
        userDtoNotEmail = new UserDto(1L, "Vasia", "");
        userDto = new UserDto(1L, "Vasia", "vasia@mail.ru");
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
    void testSaveNewUser() throws Exception {
        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        Mockito.verify(userClient, Mockito.times(1))
                .createUser(any(UserDto.class));
    }
}
