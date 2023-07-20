package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemJsonTest {

    private JacksonTester<ItemDto> json;

    @Test
    public void itemDtoTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JacksonTester.initFields(this, mapper);

        ItemDto itemDto = new ItemDto(
                1L,
                "test",
                "test",
                true,
                UserMapper.toUserDto(new User(1L, "test", "test@mail.ru")),
                1L,
                new ArrayList<>(),
                null,
                null);

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.owner.email").isEqualTo("test@mail.ru");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }
}
