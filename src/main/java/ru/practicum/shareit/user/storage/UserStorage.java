package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserStorage {
    UserDto getUser(int userId);

    UserDto addUser(UserDto userDto);

    UserDto patchUser(int userId, UserDto userDto);

    void deleteUser(int userId);

    Collection<UserDto> findAll();
}
