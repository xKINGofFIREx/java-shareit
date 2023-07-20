package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
public class BookingController {
    BookingClient bookingClient;
    private static final String SHARER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody @Valid BookingDto bookingDto,
                                                @RequestHeader(SHARER_HEADER) long userId) throws NotFoundException, ValidationException {
        return bookingClient.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBooking(@PathVariable long bookingId,
                                               @RequestParam boolean approved,
                                               @RequestHeader(SHARER_HEADER) long userId) throws NotFoundException, ValidationException {
        return bookingClient.patchBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable long bookingId,
                                                 @RequestHeader(SHARER_HEADER) long userId) throws NotFoundException {
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getCurrentUserBookings(@RequestParam(defaultValue = "ALL", required = false) State state,
                                                         @RequestHeader(SHARER_HEADER) long userId,
                                                         @RequestParam(required = false) Integer from,
                                                         @RequestParam(required = false) Integer size) throws NotFoundException, ValidationException {
        return bookingClient.getCurrentUserBookings(state, userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestParam(defaultValue = "ALL", required = false) State state,
                                                   @RequestHeader(SHARER_HEADER) long ownerId,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size) throws NotFoundException, ValidationException {
        return bookingClient.getOwnerBookings(state, ownerId, from, size);
    }

}
