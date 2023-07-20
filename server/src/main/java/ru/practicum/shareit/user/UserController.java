package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.InvalidArgumentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) throws NotFoundException {
        return userService.getUser(userId);
    }

    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserDto userDto) throws InvalidArgumentException {
        return userService.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto patchUser(@PathVariable long userId,
                             @RequestBody UserDto userDto) throws InvalidArgumentException {
        return userService.patchUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }
}
