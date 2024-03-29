package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
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

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("У данного пользователя нет букинга - " + bookingId);
        }

        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getCurrentUserBookings(State state, long userId,
                                                   Integer from, Integer size) throws NotFoundException, ValidationException {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким номером не существует"));

        List<Long> bookingIds = bookingRepository.findBookingsByBookerId(userId).stream().map(Booking::getId)
                .collect(Collectors.toList());

        return BookingMapper.toBookingDtos(getBookingsByState(bookingIds, state, from, size));
    }

    public List<BookingDto> getOwnerBookings(State state, long ownerId,
                                             Integer from, Integer size) throws NotFoundException, ValidationException {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Владельца вещи с таким номером не существует"));

        List<Long> bookingIds = bookingRepository.findBookingsByItemOwnerId(ownerId).stream().map(Booking::getId)
                .collect(Collectors.toList());

        return BookingMapper.toBookingDtos(getBookingsByState(bookingIds, state, from, size));
    }

    private List<Booking> getBookingsByState(List<Long> bookingIds, State state,
                                             Integer from, Integer size) throws ValidationException {

        if (from != null && from < 0 || size != null && size < 1)
            throw new ValidationException("Ошибка пагинации");

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findBookingsByBookingIdsCurrent(bookingIds, now);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsByBookingIdsPast(bookingIds, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsByBookingIdsFuture(bookingIds, now);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByBookingIdsAndStatus(bookingIds, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByBookingIdsAndStatus(bookingIds, BookingStatus.REJECTED);
                break;
            default:
                bookings = bookingRepository.findBookingsByBookingIds(bookingIds);
        }

        if (from == null || size == null)
            return bookings.stream().sorted(Comparator.comparing(Booking::getStart).reversed()).collect(Collectors.toList());

        return bookings.stream().sorted(Comparator.comparing(Booking::getStart).reversed()).skip(from).limit(size).collect(Collectors.toList());
    }
}
