package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String SHARER_HEADER = "X-Sharer-User-Id";

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId,
                           @RequestHeader(SHARER_HEADER) long sharerId) throws NotFoundException {
        return itemService.getItem(itemId, sharerId);
    }

    @PostMapping
    public ItemDto addItem(@RequestBody @Valid ItemDto itemDto,
                           @RequestHeader(SHARER_HEADER) long sharerId) throws NotFoundException {
        return itemService.addItem(itemDto, sharerId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        itemService.deleteItem(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable long itemId,
                             @RequestBody ItemDto itemDto,
                             @RequestHeader(SHARER_HEADER) long sharerId) throws NotFoundException {
        return itemService.patchItem(itemId, itemDto, sharerId);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(SHARER_HEADER) long sharerId,
                                 @RequestParam(required = false) Integer from,
                                 @RequestParam(required = false) Integer size) throws ValidationException {
        return itemService.findAll(sharerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam String text,
                                       @RequestParam(required = false) Integer from,
                                       @RequestParam(required = false) Integer size) throws ValidationException {
        return itemService.getItemByText(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@PathVariable long itemId,
                                    @RequestBody @Valid CommentDto commentDto,
                                    @RequestHeader(SHARER_HEADER) long sharerId) throws NotFoundException, ValidationException {
        return itemService.createComment(itemId, commentDto, sharerId);
    }
}
