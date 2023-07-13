package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestJsonTest {

    private JacksonTester<ItemRequestDto> json;

    @Test
    public void itemRequestDtoTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        JacksonTester.initFields(this, mapper);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        LocalDateTime now = LocalDateTime.parse(LocalDateTime.now().format(formatter));

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                1L,
                "test",
                new User(1L, "test", "test@mail.ru"),
                now,
                new ArrayList<>());

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.requester.email").isEqualTo("test@mail.ru");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(now.format(formatter));

    }
}
