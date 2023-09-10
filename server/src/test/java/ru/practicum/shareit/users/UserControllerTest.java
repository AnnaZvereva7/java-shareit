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
import ru.practicum.shareit.exception.NotUniqueEmailException;
import ru.practicum.shareit.users.dto.UserDto;
import ru.practicum.shareit.users.dto.UserMapper;
import ru.practicum.shareit.users.model.User;
import ru.practicum.shareit.users.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void update_NameEmail_whenStatusIsOk() throws Exception {
        // given
        Long userId = 1L;
        UserDto userDto = new UserDto(0L, "name2", "email2@mail.ru");
        User expectedUser = new User(1L, "name2", "email2@mail.ru");
        UserDto expectedUserDto = new UserDto(1L, "name2", "email2@mail.ru");

        when(userService.update(userId, userDto.getName(), userDto.getEmail())).thenReturn(expectedUser);
        when(mapper.toUserDto(expectedUser)).thenReturn(expectedUserDto);

        // when
        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", 1L)
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
        verify(userService).update(userId, userDto.getName(), userDto.getEmail());
    }

    @Test
    void update_Email_whenStatusIsOk() throws Exception {
        // given
        Long userId = 1L;
        UserDto userDto = new UserDto(0L, " ", "email2@mail.ru");
        User expectedUser = new User(1L, "name", "email2@mail.ru");
        UserDto expectedUserDto = new UserDto(1L, "name", "email2@mail.ru");

        when(userService.update(userId, userDto.getName(), userDto.getEmail())).thenReturn(expectedUser);
        when(mapper.toUserDto(expectedUser)).thenReturn(expectedUserDto);

        // when
        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", 1L)
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
        verify(userService).update(userId, userDto.getName(), userDto.getEmail());
    }

    @Test
    void update_whenEmailNotUnique_thenThrowException() throws Exception {
        // given
        Long userId = 1L;
        UserDto userDto = new UserDto(0L, " ", "email2@mail.ru");
        when(userService.update(userId, userDto.getName(), userDto.getEmail())).thenThrow(new NotUniqueEmailException());

        // when
        mvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", is("Email не уникален")));
        verify(mapper, never()).toUserDto(any());
        verify(userService).update(userId, userDto.getName(), userDto.getEmail());

    }

    @Test
    void delete_whenStatusIsOk() throws Exception {
        // given
        doNothing().when(userService).delete(anyLong());
        // when
        mvc.perform(MockMvcRequestBuilders.delete("/users/{userId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk());

        verify(userService).delete(anyLong());
    }

    @Test
    void findAll() throws Exception {
        // given
        User user1 = new User(1L, "name", "email@mail.ru");
        UserDto userDto1 = new UserDto(1L, "name", "email@mail.ru");
        when(userService.findAll()).thenReturn(List.of(user1));
        when(mapper.toUserDto(user1)).thenReturn(userDto1);
        // when
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto1.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto1.getEmail())));
        verify(userService).findAll();
        verify(mapper, times(1)).toUserDto(user1);
    }
}