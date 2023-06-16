package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.InvalidArgumentException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private int newUserId = 1;
    private final Map<Integer, UserDto> users = new HashMap<>();
    private static final InMemoryUserStorage instance = new InMemoryUserStorage();

    public static InMemoryUserStorage getInstance() {
        return instance;
    }

    @Override
    public UserDto getUser(int userId) {
        return users.get(userId);
    }

    @Override
    public UserDto addUser(UserDto userDto) throws InvalidArgumentException {
        isEmailExists(userDto);
        userDto.setId(newUserId++);
        users.put(userDto.getId(), userDto);
        return userDto;
    }

    @Override
    public UserDto patchUser(int userId, UserDto userDto) throws InvalidArgumentException {
        UserDto userToPatch = users.get(userId);

        if (!userToPatch.getEmail().equals(userDto.getEmail()))
            isEmailExists(userDto);
        if (userDto.getName() != null)
            userToPatch.setName(userDto.getName());
        if (userDto.getEmail() != null)
            userToPatch.setEmail(userDto.getEmail());
        return userToPatch;
    }

    @Override
    public void deleteUser(int userId) {
        users.remove(userId);
    }

    @Override
    public Collection<UserDto> findAll() {
        return users.values();
    }

    private void isEmailExists(UserDto userDto) throws InvalidArgumentException {
        boolean isEmailExists = users.values()
                .stream()
                .anyMatch(u -> u.getEmail().equals(userDto.getEmail()));

        if (isEmailExists)
            throw new InvalidArgumentException("Пользователь с такой почтой уже существует");
    }
}
