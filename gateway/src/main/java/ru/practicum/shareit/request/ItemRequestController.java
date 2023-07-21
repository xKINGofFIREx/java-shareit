package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String SHARER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(SHARER_HEADER) long userId,
                                                @RequestBody @Valid ItemRequestDto itemRequestDto) throws NotFoundException {
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllOwnerRequests(@RequestHeader(SHARER_HEADER) long userId) throws NotFoundException {
        return itemRequestClient.findAllOwnerRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequests(@RequestParam(required = false) Integer from,
                                                  @RequestParam(required = false) Integer size,
                                                  @RequestHeader(SHARER_HEADER) long userId) throws ValidationException, NotFoundException {
        return itemRequestClient.findAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable long requestId,
                                                 @RequestHeader(SHARER_HEADER) long userId) throws NotFoundException {
        return itemRequestClient.getRequestById(requestId, userId);
    }
}
