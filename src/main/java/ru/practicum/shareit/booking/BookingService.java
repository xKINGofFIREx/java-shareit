package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingService {
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    public BookingDto createBooking(BookingDto bookingDto, long userId) throws NotFoundException, ValidationException {
        Booking booking = BookingMapper.toBooking(bookingDto);


        if (bookingDto.getStart().equals(bookingDto.getEnd()))
            throw new ValidationException("Дата начала равна дате конца");
        if (bookingDto.getStart().isAfter(bookingDto.getEnd()))
            throw new ValidationException("Дата начала позже даты конца");

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещи с таким номером не существует"));
        if (item.getOwner().getId() == userId)
            throw new NotFoundException("Пользователь вещи - её владелец");
        if (!item.isAvailable())
            throw new ValidationException("Вещь не доступна");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким номером не существует"));

        booking.setItem(item);
        booking.setBooker(user);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto patchBooking(long bookingId, boolean approved, long userId) throws NotFoundException, ValidationException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Букинга с таким номером не существует"));
        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Вещи с таким номером не существует"));

        if (userId != item.getOwner().getId()) {
            throw new NotFoundException("Пользователь не владелец вещи");
        } else if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Попытка изменения изменненного статуса вещи ");
        }


        if (approved)
            booking.setStatus(BookingStatus.APPROVED);
        else
            booking.setStatus(BookingStatus.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto getBookingById(long bookingId, long userId) throws NotFoundException {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Букинга с таким номером не существует"));

        if (booking.getBooker().getId() != userId
                && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("У данного пользователя нет букинга - " + bookingId);
        }

        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getCurrentUserBookings(State state, long userId) throws NotFoundException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким номером не существует"));

        List<Long> itemIds = bookingRepository
                .findBookingsByBooker_Id(userId)
                .stream()
                .map(Booking::getItem)
                .map(Item::getId)
                .collect(Collectors.toList());

        return BookingMapper.toBookingDtos(getBookingsByState(itemIds, state));
    }

    public List<BookingDto> getOwnerBookings(State state, long ownerId) throws NotFoundException {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Владельца вещи с таким номером не существует"));

        List<Long> itemIds = itemRepository
                .findAllByOwnerId(ownerId)
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        return BookingMapper.toBookingDtos(getBookingsByState(itemIds, state));
    }

    private List<Booking> getBookingsByState(List<Long> itemIds, State state) {
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                bookings = bookingRepository
                        .findBookingsByItemIdsCurrent(itemIds, now);
                break;
            case PAST:
                bookings = bookingRepository
                        .findBookingsByItemIdsPast(itemIds, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByItemIdsFuture(itemIds, now);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByItemIdsAndStatus(itemIds, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository
                        .findBookingsByItemIdsAndStatus(itemIds, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository
                        .findBookingsByItemIds(itemIds);
        }

        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
    }
}
