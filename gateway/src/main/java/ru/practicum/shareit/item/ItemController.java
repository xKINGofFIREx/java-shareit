package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemClient itemClient;
    private static final String SHARER_HEADER = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                          @RequestHeader(SHARER_HEADER) long sharerId) throws NotFoundException {
        return itemClient.getItem(itemId, sharerId);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestBody @Valid ItemDto itemDto,
                                          @RequestHeader(SHARER_HEADER) long sharerId) throws NotFoundException {
        return itemClient.addItem(itemDto, sharerId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        itemClient.deleteItem(itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItem(@PathVariable long itemId,
                                            @RequestBody ItemDto itemDto,
                                            @RequestHeader(SHARER_HEADER) long sharerId) throws NotFoundException {
        return itemClient.patchItem(itemId, itemDto, sharerId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(SHARER_HEADER) long sharerId,
                                          @RequestParam(required = false) Integer from,
                                          @RequestParam(required = false) Integer size) throws ValidationException {
        return itemClient.findAll(sharerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemByText(@RequestHeader(SHARER_HEADER) long sharerId,
                                                @RequestParam String text,
                                                @RequestParam(required = false) Integer from,
                                                @RequestParam(required = false) Integer size) throws ValidationException {
        return itemClient.getItemByText(sharerId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable long itemId,
                                                @RequestBody @Valid CommentDto commentDto,
                                                @RequestHeader(SHARER_HEADER) long sharerId) throws NotFoundException, ValidationException {
        return itemClient.createComment(itemId, commentDto, sharerId);
    }
}
