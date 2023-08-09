package ru.practicum.shareit.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.users.dto.UserDto;
import ru.practicum.shareit.users.dto.UserMapper;
import ru.practicum.shareit.users.model.User;
import ru.practicum.shareit.users.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserMapper mapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(ErrorHandler.class).build();
    }

    @Test
    void findById_whenStatusIsOk() throws Exception {
        //given
        User expectedUser = new User(1L, "name", "email@mail.ru");
        UserDto expectedUserDto = new UserDto(1L, "name", "email@mail.ru");
        Long id = 1L;
        when(userService.findById(id)).thenReturn(expectedUser);
        when(mapper.toUserDto(expectedUser)).thenReturn(expectedUserDto);

        //when
        mvc.perform(get("/users/{userId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(expectedUserDto.getName())))
                .andExpect(jsonPath("$.email", is(expectedUserDto.getEmail())));
    }

    @Test
    void findById_whenIdWrong_thenThrowException() throws Exception {
        //given
        when(userService.findById(anyLong())).thenThrow(new NotFoundException(User.class));
        //when
        mvc.perform(get("/users/{userId}", 0L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isNotFound());
    }

    @Test
    void save_whenStatusIsOk() throws Exception {
        // given
        User user = new User(null, "name", "email@mail.ru");
        UserDto userDto = new UserDto(0, "name", "email@mail.ru");
        User expectedUser = new User(1L, "name", "email@mail.ru");
        UserDto expectedUserDto = new UserDto(1L, "name", "email@mail.ru");

        when(mapper.fromUserDto(userDto)).thenReturn(user);
        when(userService.save(user)).thenReturn(expectedUser);
        when(mapper.toUserDto(expectedUser)).thenReturn(expectedUserDto);

        // when
        mvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(expectedUserDto.getName())))
                .andExpect(jsonPath("$.email", is(expectedUserDto.getEmail())));

        verify(mapper).toUserDto(expectedUser);
        verify(mapper).fromUserDto(any());
        verify(userService).save(user);
    }

    @Test
    void save_whenNameNull_thenThrowsException() throws Exception {
        //given
        User user = new User(null, null, "email@mail.ru");
        //when
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void save_whenNameLong_thenThrowsException() throws Exception {
        //given
        User user = new User(null, "nameTooLongNameTooLongNameTooLongNameTooLongNameTooLong", "email@mail.ru");
        //when
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void save_whenEmailBlank_thenThrowsException() throws Exception {
        //given
        User user = new User(null, "name", "   ");
        //when
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void save_whenEmailWrong_thenThrowsException() throws Exception {
        //given
        User user = new User(null, "name", "email");
        //when
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void findAll() {
    }
}