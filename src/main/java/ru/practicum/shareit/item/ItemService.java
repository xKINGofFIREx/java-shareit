package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public ItemDto getItem(long itemId, long userId) throws NotFoundException {


        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещи с таким номером не существует")));

        if (userId == itemDto.getOwner().getId())
            setBookings(itemId, itemDto);

        itemDto.setComments(CommentMapper.toCommentDtos(commentRepository.findAllCommentsByItemId(itemId)
                .orElse(new ArrayList<>())));
        return itemDto;
    }

    public ItemDto addItem(ItemDto itemDto, long sharerId) throws NotFoundException {
        Item item = ItemMapper.toItem(itemDto);

        User owner = userRepository.findById(sharerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        item.setOwner(owner);

        if (itemDto.getRequestId() != 0)
            item.setRequest(itemRepository.findRequestById(itemDto.getRequestId()).orElseThrow(() -> new NotFoundException("Запрос не найден")));

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto patchItem(long itemId, ItemDto itemDto, long sharerId) throws NotFoundException {
        Item itemToPatch = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с таким номером не найдена"));
        if (!userRepository.existsById(sharerId))
            throw new NotFoundException("Пользователь не найден");
        if (itemToPatch.getOwner().getId() != sharerId)
            throw new NotFoundException("Использован другой пользователь");

        if (itemDto.getName() != null)
            itemToPatch.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            itemToPatch.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            itemToPatch.setAvailable(itemDto.getAvailable());

        return ItemMapper.toItemDto(itemRepository.save(itemToPatch));
    }

    public void deleteItem(long itemId) {
        itemRepository.deleteById(itemId);
    }

    public List<ItemDto> findAll(long sharerId, Integer from, Integer size) throws ValidationException {

        if (from != null && from < 0 || size != null && size < 1)
            throw new ValidationException("Ошибка пагинации");

        List<Item> items;
        if (from == null || size == null)
            items = itemRepository.findAllByOwnerId(sharerId);
        else
            items = itemRepository.findAllByOwnerId(sharerId, PageRequest.of(from, size)).getContent();

        List<ItemDto> itemDtos = ItemMapper.toItemDtos(items);

        for (ItemDto itemDto : itemDtos) {
            setBookings(itemDto.getId(), itemDto);
            itemDto.setComments(CommentMapper.toCommentDtos(commentRepository.findAllCommentsByItemId(itemDto.getId())
                    .orElse(new ArrayList<>())));
        }

        return itemDtos.stream()
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(Collectors.toList());
    }

    public List<ItemDto> getItemByText(String text, Integer from, Integer size) throws ValidationException {
        if (text.equals(""))
            return new ArrayList<>();

        if (from != null && from < 0 || size != null && size < 1)
            throw new ValidationException("Ошибка пагинации");

        List<Item> items;
        if (from == null || size == null)
            items = itemRepository.findAll();
        else
            items = itemRepository.findAll(PageRequest.of(from, size)).getContent();

        return ItemMapper.toItemDtos(items)
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable())
                .collect(Collectors.toList());
    }

    public CommentDto createComment(long itemId, CommentDto commentDto, long bookerId) throws NotFoundException, ValidationException {
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с таким номером не найдена")));

        List<Booking> bookings = itemRepository.findBookingByItemIdAndBookerId(itemId, bookerId)
                .orElseThrow(() -> new NotFoundException("Букинга не существует"));

        Booking booking = bookings.get(0);
        if (booking.getStart().isAfter(LocalDateTime.now())
                || booking.getStatus() != BookingStatus.APPROVED)
            throw new ValidationException("Комментарий не может быть написан");

        comment.setAuthor(booking.getBooker());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void setBookings(long itemId, ItemDto itemDto) {
        List<Booking> nextBooking = itemRepository.findNextBookingByItemId(itemId, LocalDateTime.now())
                .orElse(null);
        List<Booking> lastBooking = itemRepository.findLastBookingByItemId(itemId, LocalDateTime.now())
                .orElse(null);

        if (nextBooking != null && nextBooking.size() > 0 && nextBooking.get(0).getStatus() == BookingStatus.APPROVED)
            itemDto.setNextBooking(BookingMapper.toBookingDto(nextBooking.get(0)));
        if (lastBooking != null && lastBooking.size() > 0 && lastBooking.get(0).getStatus() == BookingStatus.APPROVED)
            itemDto.setLastBooking(BookingMapper.toBookingDto(lastBooking.get(lastBooking.size() - 1)));
    }
}
