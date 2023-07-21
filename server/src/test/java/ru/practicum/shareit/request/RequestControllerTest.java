package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

@SpringBootTest
public class RequestControllerTest {
    private ItemRequestService itemRequestService;
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @BeforeEach
    public void initialize() {
        itemRequestService = Mockito.mock(ItemRequestService.class);
        ItemRequestController itemRequestController = new ItemRequestController(itemRequestService);
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
    }

    @Test
    public void createRequestTest() throws Exception {
        User user = new User(1L, "user", "user@mail.ru");
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setRequester(UserMapper.toUserDto(user));
        itemRequestDto.setDescription("test");

        Mockito
                .when(itemRequestService.createRequest(1L, itemRequestDto))
                .thenReturn(itemRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void findAllOwnerRequestsTest() throws Exception {
        User user = new User(1L, "user", "user@mail.ru");
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setRequester(UserMapper.toUserDto(user));
        itemRequestDto.setDescription("test");

        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setId(2L);
        itemRequestDto1.setRequester(UserMapper.toUserDto(user));
        itemRequestDto1.setDescription("test");

        Mockito
                .when(itemRequestService.findAllOwnerRequests(1L))
                .thenReturn(List.of(itemRequestDto, itemRequestDto1));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(List.of(itemRequestDto, itemRequestDto1)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void findAllRequestsTest() throws Exception {
        User user = new User(1L, "user", "user@mail.ru");
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setRequester(UserMapper.toUserDto(user));
        itemRequestDto.setDescription("test");

        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setId(2L);
        itemRequestDto1.setRequester(UserMapper.toUserDto(user));
        itemRequestDto1.setDescription("test");

        Mockito
                .when(itemRequestService.findAllRequests(null, null, 2L))
                .thenReturn(List.of(itemRequestDto, itemRequestDto1));

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(List.of(itemRequestDto, itemRequestDto1)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getRequestByIdTest() throws Exception {
        User user = new User(1L, "user", "user@mail.ru");
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setRequester(UserMapper.toUserDto(user));
        itemRequestDto.setDescription("test");

        Mockito
                .when(itemRequestService.getRequestById(1L, 1L))
                .thenReturn(itemRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
