package ru.practicum.shareit.item;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ItemServiceTest {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private ItemService itemService;

    @BeforeEach
    public void initialize() {
        itemRepository = Mockito.mock(ItemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        itemService = new ItemService(itemRepository, userRepository, commentRepository);
    }

    @Test
    public void getItemTest() throws NotFoundException {
        Item item = new Item(1L, "test", "test", true,
                new User(1L, "user", "test@mail.ru"), null);

        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        Mockito
                .when(commentRepository.findAllCommentsByItemId(1L))
                .thenReturn(Optional.of(new ArrayList<>()));

        Mockito
                .when(itemRepository.findNextBookingByItemId(1L, LocalDateTime.now()))
                .thenReturn(Optional.of(new ArrayList<>()));

        Assertions.assertEquals(ItemMapper.toItemDto(item), itemService.getItem(1L, 1L));
    }

    @Test
    public void addItemTest() throws NotFoundException {
        User user = new User(1L, "user", "test@mail.ru");
        Item item = new Item(1L, "test", "test", true,
                user, null);

        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Mockito
                .when(itemRepository.save(item))
                .thenReturn(item);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        Assertions.assertEquals(itemDto, itemService.addItem(itemDto, 1L));
    }

    @Test
    public void patchItemTest() throws NotFoundException {
        User user = new User(1L, "user", "test@mail.ru");
        Item item = new Item(1L, "test", "test", true,
                user, null);

        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito
                .when(userRepository.existsById(1L))
                .thenReturn(true);

        Mockito
                .when(itemRepository.save(item))
                .thenReturn(item);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        Assertions.assertEquals(itemDto, itemService.patchItem(1L, itemDto, 1L));
    }

    @Test
    public void deleteItemTest() {
        itemRepository.deleteById(1L);

        Mockito.verify(itemRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void findAllTest() throws ValidationException {
        Item item = new Item(1L, "test", "test", true,
                new User(1L, "user", "test@mail.ru"), null);
        Item item1 = new Item(2L, "test1", "test1", true,
                new User(1L, "user", "test@mail.ru"), null);

        Mockito
                .when(itemRepository.findAllByOwnerId(1L))
                .thenReturn(Arrays.asList(item, item1));

        Mockito
                .when(commentRepository.findAllCommentsByItemId(1L))
                .thenReturn(Optional.of(new ArrayList<>()));

        Mockito
                .when(itemRepository.findNextBookingByItemId(1L, LocalDateTime.now()))
                .thenReturn(Optional.of(new ArrayList<>()));

        List<ItemDto> itemDtos = ItemMapper.toItemDtos(Arrays.asList(item, item1));

        Assertions.assertEquals(itemDtos, itemService.findAll(1L, null, null));
        Assertions.assertThrows(ValidationException.class, () -> itemService.findAll(1L, 0, 0));
    }

    @Test
    public void getItemByTextTest() throws ValidationException {
        Item item = new Item(1L, "test", "test", true,
                new User(1L, "user", "test@mail.ru"), null);
        Item item1 = new Item(2L, "test1", "test1", true,
                new User(1L, "user", "test@mail.ru"), null);

        Mockito
                .when(itemRepository.findAll())
                .thenReturn(Arrays.asList(item, item1));

        List<ItemDto> itemDtos = ItemMapper.toItemDtos(Arrays.asList(item, item1));

        Assertions.assertEquals(itemDtos, itemService.getItemByText("test", null, null));
    }

    @Test
    public void createCommentTest() throws ValidationException, NotFoundException {
        Item item = new Item(1L, "test", "test", true,
                new User(1L, "user", "test@mail.ru"), null);
        User booker = new User(2L, "booker", "test@test.ru");
        Comment comment = new Comment(1L, "test", LocalDateTime.now(), item, booker);
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);


        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        Mockito
                .when(itemRepository.findBookingByItemIdAndBookerId(1L, 2L))
                .thenReturn(Optional.of(List.of(booking)));

        Mockito
                .when(commentRepository.save(comment))
                .thenReturn(comment);

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        Assertions.assertEquals(commentDto, itemService.createComment(1L, commentDto, 2L));
    }

}
