package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.InvalidArgumentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class UserServiceTest {
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    public void initialize() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    public void getUserTest() throws NotFoundException {
        User user = new User();
        user.setId(1);
        user.setEmail("test@test.ru");
        user.setName("test");

        UserDto userDto = UserMapper.toUserDto(user);

        Mockito
                .when(userRepository.findById((long) 1))
                .thenReturn(Optional.of(user));

        Assertions.assertEquals(userDto, userService.getUser(1));
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUser(0));
    }

    @Test
    public void addUserTest() throws InvalidArgumentException {
        User user = new User();
        user.setId(1);
        user.setEmail("test@test.ru");
        user.setName("test");

        UserDto userDto = UserMapper.toUserDto(user);

        Mockito
                .when(userRepository.save(user))
                .thenReturn(user);

        Assertions.assertEquals(userDto, userService.addUser(userDto));
    }

    @Test
    public void patchUserTest() throws InvalidArgumentException {
        User user = new User();
        user.setId(1);
        user.setEmail("test@test.ru");
        user.setName("test");

        User user1 = new User();
        user1.setId(1);
        user1.setEmail("test1@test.ru");
        user1.setName("test1");

        Mockito
                .when(userRepository.save(user))
                .thenReturn(user1);

        Mockito
                .when(userRepository.getReferenceById(1L))
                .thenReturn(user);

        UserDto userDto = UserMapper.toUserDto(user);
        UserDto userDto1 = UserMapper.toUserDto(user1);
        Assertions.assertEquals(userDto1, userService.patchUser(1L, userDto));
    }

    @Test
    public void deleteUserTest() {
        userService.deleteUser(1);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void findAllTest() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@test.ru");
        user.setName("test");

        User user1 = new User();
        user1.setId(2);
        user1.setEmail("test1@test.ru");
        user1.setName("test1");

        List<User> users = Arrays.asList(user, user1);

        Mockito
                .when(userRepository.findAll())
                .thenReturn(users);


        Assertions.assertEquals(UserMapper.toUserDtoList(users), userService.findAll());
    }
}
