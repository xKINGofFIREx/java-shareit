package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.ControllerExceptionHandler;
import ru.practicum.shareit.exception.InvalidArgumentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class UserControllerTest {

    private UserService userService;
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @BeforeEach
    public void initialize() {
        userService = Mockito.mock(UserService.class);
        UserController userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(ControllerExceptionHandler.class)
                .build();
        mapper = new ObjectMapper();
    }

    @Test
    public void getUserTest() throws Exception {
        UserDto userDto = new UserDto(1, "test", "test@mail.ru");
        Mockito
                .when(userService.getUser(1))
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(userDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(userDto.getEmail())));

        Mockito
                .when(userService.getUser(99))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/99"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void addUserTest() throws Exception {
        UserDto userDto = new UserDto(1, "test", "test@mail.ru");

        Mockito
                .when(userService.addUser(userDto))
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(userDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(userDto.getEmail())));

        UserDto userWithSameEmail = new UserDto(2, "test", "test@mail.ru");

        Mockito
                .when(userService.addUser(userWithSameEmail))
                .thenThrow(InvalidArgumentException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(mapper.writeValueAsString(userWithSameEmail))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void patchUserTest() throws Exception {
        UserDto userDto = new UserDto(1, "test", "test@mail.ru");
        Mockito
                .when(userService.patchUser(1L, userDto))
                .thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(userDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", Matchers.is(userDto.getEmail())));
    }

    @Test
    public void deleteUserTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void findAllTest() throws Exception {
        UserDto userDto = new UserDto(1, "test", "test@mail.ru");
        UserDto userDto1 = new UserDto(2, "test1", "test1@mail.ru");

        List<UserDto> userDtos = Arrays.asList(userDto, userDto1);

        Mockito
                .when(userService.findAll())
                .thenReturn(userDtos);

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .content(mapper.writeValueAsString(userDtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(userDto.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(userDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email", Matchers.is(userDto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is(userDto1.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is(userDto1.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].email", Matchers.is(userDto1.getEmail())));
    }
}
