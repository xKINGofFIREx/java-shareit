package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.exception.ControllerExceptionHandler;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class ItemControllerTest {
    private ItemService itemService;
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @BeforeEach
    public void initialize() {
        itemService = Mockito.mock(ItemService.class);
        ItemController itemController = new ItemController(itemService);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setControllerAdvice(ControllerExceptionHandler.class)
                .build();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
    }

    @Test
    public void getItemTest() throws Exception {
        User user = new User(1L, "user", "test@mail.ru");
        Item item = new Item(1L, "test", "test", true,
                user, null);

        Mockito
                .when(itemService.getItem(1L, 1L))
                .thenReturn(ItemMapper.toItemDto(item));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1").header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(item.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(item.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(item.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(item.isAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.owner.name", Matchers.is(item.getOwner().getName())));

    }

    @Test
    public void addItemTest() throws Exception {
        User user = new User(1L, "user", "test@mail.ru");
        Item item = new Item(1L, "test", "test", true,
                user, null);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        Mockito
                .when(itemService.addItem(itemDto, 1L))
                .thenReturn(itemDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(item.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(item.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(item.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(item.isAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.owner.name", Matchers.is(item.getOwner().getName())));
    }

    @Test
    public void deleteItemTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/items/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void patchItemTest() throws Exception {
        User user = new User(1L, "user", "test@mail.ru");
        Item item = new Item(1L, "test", "test", true,
                user, null);

        ItemDto itemDto = ItemMapper.toItemDto(item);

        Mockito
                .when(itemService.patchItem(1L, itemDto, 1L))
                .thenReturn(itemDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(item.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(item.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.is(item.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available", Matchers.is(item.isAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.owner.name", Matchers.is(item.getOwner().getName())));

    }

    @Test
    public void findAllTest() throws Exception {
        User user = new User(1L, "user", "test@mail.ru");
        Item item = new Item(1L, "test", "test", true,
                user, null);
        Item item1 = new Item(2L, "test1", "test1", true,
                user, null);

        Mockito
                .when(itemService.findAll(1L, null, null))
                .thenReturn(ItemMapper.toItemDtos(List.of(item, item1)));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(item.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(item.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is(item.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available", Matchers.is(item.isAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].owner.name", Matchers.is(item.getOwner().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is(item1.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is(item1.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description", Matchers.is(item1.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].available", Matchers.is(item1.isAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].owner.name", Matchers.is(item1.getOwner().getName())));

        Mockito
                .when(itemService.findAll(1L, 0, 0))
                .thenThrow(ValidationException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/items?from=0&size=0")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getItemByTextTest() throws Exception {
        User user = new User(1L, "user", "test@mail.ru");
        Item item = new Item(1L, "test", "test", true,
                user, null);
        Item item1 = new Item(2L, "test1", "test1", true,
                user, null);

        Mockito
                .when(itemService.getItemByText("test", null, null))
                .thenReturn(ItemMapper.toItemDtos(List.of(item, item1)));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text=test"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(item.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(item.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", Matchers.is(item.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available", Matchers.is(item.isAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].owner.name", Matchers.is(item.getOwner().getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is(item1.getId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is(item1.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description", Matchers.is(item1.getDescription())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].available", Matchers.is(item1.isAvailable())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].owner.name", Matchers.is(item1.getOwner().getName())));

        Mockito
                .when(itemService.getItemByText("test", 0, 0))
                .thenThrow(ValidationException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text=test&from=0&size=0"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void createComment() throws Exception {
        User user = new User(1L, "user", "test@mail.ru");
        Item item = new Item(1L, "test", "test", true,
                user, null);
        User user1 = new User(2L, "user1", "test1@mail.ru");


        Comment comment = new Comment(1L, "test-comment", LocalDateTime.now(), item, user1);

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        Mockito
                .when(itemService.createComment(1L, commentDto, 1L))
                .thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
