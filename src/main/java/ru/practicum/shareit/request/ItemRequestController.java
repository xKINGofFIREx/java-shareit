package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String SHARER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(SHARER_HEADER) long userId,
                                        @RequestBody @Valid ItemRequestDto itemRequestDto) throws NotFoundException {
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> findAllOwnerRequests(@RequestHeader(SHARER_HEADER) long userId) throws NotFoundException {
        return itemRequestService.findAllOwnerRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequests(@RequestParam(required = false) Integer from,
                                                @RequestParam(required = false) Integer size,
                                                @RequestHeader(SHARER_HEADER) long userId) throws ValidationException, NotFoundException {
        return itemRequestService.findAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable long requestId,
                                         @RequestHeader(SHARER_HEADER) long userId) throws NotFoundException {
        return itemRequestService.getRequestById(requestId, userId);
    }
}
