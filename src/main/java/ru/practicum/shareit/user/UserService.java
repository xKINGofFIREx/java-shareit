package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidArgumentException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService() {
        this.userStorage = InMemoryUserStorage.getInstance();
    }

    public UserDto getUser(int userId) {
        return userStorage.getUser(userId);
    }

    public UserDto addUser(UserDto userDto) throws InvalidArgumentException {
        return userStorage.addUser(userDto);
    }

    public UserDto patchUser(int userId, UserDto userDto) throws InvalidArgumentException {
        return userStorage.patchUser(userId, userDto);
    }

    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }

    public Collection<UserDto> findAll() {
        return userStorage.findAll();
    }
}
