package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidArgumentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto getUser(long userId) throws NotFoundException {
        return UserMapper.toUserDto(userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким номером не существует")));
    }

    public UserDto addUser(UserDto userDto) throws InvalidArgumentException {
        checkEmail(userDto);
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    public UserDto patchUser(long userId, UserDto userDto) throws InvalidArgumentException {
        User userToPatch = userRepository.getReferenceById(userId);

        if (!userToPatch.getEmail().equals(userDto.getEmail()))
            checkEmail(userDto);
        if (userDto.getName() != null)
            userToPatch.setName(userDto.getName());
        if (userDto.getEmail() != null)
            userToPatch.setEmail(userDto.getEmail());

        return UserMapper.toUserDto(userRepository.save(userToPatch));
    }

    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    public List<UserDto> findAll() {
        return UserMapper.toUserDtoList(userRepository.findAll());
    }

    private void checkEmail(UserDto userDto) throws InvalidArgumentException {
        if (userRepository.countAllByEmail(userDto.getEmail()) > 1)
            throw new InvalidArgumentException("Пользователь с такой почтой уже существует");
    }
}
