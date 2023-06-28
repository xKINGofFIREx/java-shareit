package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exception.InvalidArgumentException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {
    UserDto getUser(long userId);

    UserDto addUser(UserDto userDto) throws InvalidArgumentException;

    UserDto patchUser(long userId, UserDto userDto) throws InvalidArgumentException;

    void deleteUser(long userId);

    List<UserDto> findAll();
}
