package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exception.InvalidArgumentException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {
    UserDto getUser(int userId);

    UserDto addUser(UserDto userDto) throws InvalidArgumentException;

    UserDto patchUser(int userId, UserDto userDto) throws InvalidArgumentException;

    void deleteUser(int userId);

    List<UserDto> findAll();
}
