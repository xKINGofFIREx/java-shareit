package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    BookingService bookingService;
    private static final String SHARER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createBooking(@RequestBody @Valid BookingDto bookingDto,
                                    @RequestHeader(SHARER_HEADER) long userId) throws NotFoundException, ValidationException {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto patchBooking(@PathVariable long bookingId,
                                   @RequestParam boolean approved,
                                   @RequestHeader(SHARER_HEADER) long userId) throws NotFoundException, ValidationException {
        return bookingService.patchBooking(bookingId, approved, userId);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBookingById(@PathVariable long bookingId,
                                     @RequestHeader(SHARER_HEADER) long userId) throws NotFoundException {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getCurrentUserBookings(@RequestParam(defaultValue = "ALL", required = false) State state,
                                                   @RequestHeader(SHARER_HEADER) long userId,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size) throws NotFoundException, ValidationException {
        return bookingService.getCurrentUserBookings(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(defaultValue = "ALL", required = false) State state,
                                             @RequestHeader(SHARER_HEADER) long ownerId,
                                             @RequestParam(required = false) Integer from,
                                             @RequestParam(required = false) Integer size) throws NotFoundException, ValidationException {
        return bookingService.getOwnerBookings(state, ownerId, from, size);
    }

}
