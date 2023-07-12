package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class UserJsonTest {

    private JacksonTester<UserDto> json;

    @Test
    public void userDtoTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JacksonTester.initFields(this, mapper);

        UserDto userDto = new UserDto(1L, "test", "test@mail.ru");

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@mail.ru");
    }
}
