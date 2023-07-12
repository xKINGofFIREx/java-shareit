package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import java.util.List;
import java.util.Optional;

public class BookingServiceTest {
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private BookingService bookingService;

    @BeforeEach
    public void initialize() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);

        bookingService = new BookingService(bookingRepository, userRepository, itemRepository);
    }

    @Test
    public void createBookingTest() throws ValidationException, NotFoundException {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setEnd(LocalDateTime.now().plusSeconds(10));

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User(1L, "Owner", "owner@mail.ru"));
        item.setAvailable(true);

        User user = new User();
        user.setId(2L);

        booking.setItem(item);
        booking.setBooker(user);

        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        Assertions.assertEquals(bookingDto, bookingService.createBooking(bookingDto, 2L));
    }

    @Test
    public void patchBookingTest() throws ValidationException, NotFoundException {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setEnd(LocalDateTime.now().plusSeconds(10));

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User(1L, "Owner", "owner@mail.ru"));
        item.setAvailable(true);

        User user = new User();
        user.setId(2L);

        booking.setItem(item);
        booking.setBooker(user);

        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        bookingDto.setStatus(BookingStatus.APPROVED);

        Assertions.assertEquals(bookingDto, bookingService.patchBooking(1L, true, 1L));
    }

    @Test
    public void getBookingByIdTest() throws NotFoundException {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setEnd(LocalDateTime.now().plusSeconds(10));

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User(1L, "Owner", "owner@mail.ru"));
        item.setAvailable(true);

        User user = new User();
        user.setId(2L);

        booking.setItem(item);
        booking.setBooker(user);

        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));

        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        Assertions.assertEquals(bookingDto, bookingService.getBookingById(1L, 2L));
    }

    @Test
    public void getCurrentUserBookingsTest() throws ValidationException, NotFoundException {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setEnd(LocalDateTime.now().plusSeconds(10));

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User(1L, "Owner", "owner@mail.ru"));
        item.setAvailable(true);

        User user = new User();
        user.setId(2L);

        booking.setItem(item);
        booking.setBooker(user);

        Mockito
                .when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));
        Mockito
                .when(bookingRepository.findBookingsByBookerId(2L))
                .thenReturn(List.of(booking));

        Mockito
                .when(bookingRepository.findBookingsByBookingIdsAndStatus(List.of(booking.getId()), BookingStatus.WAITING))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(BookingMapper.toBookingDtos(List.of(booking)),
                bookingService.getCurrentUserBookings(State.WAITING, 2L, null, null));
    }

    @Test
    public void getOwnerBookingsTest() throws ValidationException, NotFoundException {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setEnd(LocalDateTime.now().plusSeconds(10));

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User(1L, "Owner", "owner@mail.ru"));
        item.setAvailable(true);

        User user = new User();
        user.setId(2L);

        booking.setItem(item);
        booking.setBooker(user);

        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(item.getOwner()));
        Mockito
                .when(bookingRepository.findBookingsByItemOwnerId(1L))
                .thenReturn(List.of(booking));

        Mockito
                .when(bookingRepository.findBookingsByBookingIdsAndStatus(List.of(booking.getId()), BookingStatus.WAITING))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(BookingMapper.toBookingDtos(List.of(booking)),
                bookingService.getOwnerBookings(State.WAITING, 1L, null, null));
    }

    @Test
    public void getBookingsByState() throws ValidationException, NotFoundException {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setEnd(LocalDateTime.now().plusSeconds(10));

        Item item = new Item();
        item.setId(1L);
        item.setOwner(new User(1L, "Owner", "owner@mail.ru"));
        item.setAvailable(true);

        User user = new User();
        user.setId(2L);

        booking.setItem(item);
        booking.setBooker(user);

        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(item.getOwner()));
        Mockito
                .when(bookingRepository.findBookingsByItemOwnerId(1L))
                .thenReturn(List.of(booking));

        booking.setStatus(BookingStatus.REJECTED);

        Mockito
                .when(bookingRepository.findBookingsByBookingIdsAndStatus(List.of(booking.getId()), BookingStatus.REJECTED))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(BookingMapper.toBookingDtos(List.of(booking)),
                bookingService.getOwnerBookings(State.REJECTED, 1L, null, null));

        LocalDateTime current = LocalDateTime.now();
        booking.setStart(current);

        Mockito
                .when(bookingRepository.findBookingsByBookingIdsCurrent(List.of(booking.getId()), current))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(BookingMapper.toBookingDtos(List.of(booking)),
                bookingService.getOwnerBookings(State.REJECTED, 1L, null, null));

        LocalDateTime past = LocalDateTime.now().minusSeconds(10);
        booking.setEnd(past);

        Mockito
                .when(bookingRepository.findBookingsByBookingIdsPast(List.of(booking.getId()), current))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(BookingMapper.toBookingDtos(List.of(booking)),
                bookingService.getOwnerBookings(State.REJECTED, 1L, null, null));

        LocalDateTime future = LocalDateTime.now().plusSeconds(10);
        booking.setStart(future);

        Mockito
                .when(bookingRepository.findBookingsByBookingIdsFuture(List.of(booking.getId()), current))
                .thenReturn(List.of(booking));

        Assertions.assertEquals(BookingMapper.toBookingDtos(List.of(booking)),
                bookingService.getOwnerBookings(State.REJECTED, 1L, null, null));
    }
}
