package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.ValidationException;
import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        return itemService.getItem(itemId);
    }

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") int sharerId) throws NotFoundException {
        return itemService.addItem(itemDto, sharerId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId) {
        itemService.deleteItem(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable int itemId,
                             @RequestBody ItemDto itemDto,
                             @RequestHeader("X-Sharer-User-Id") int sharerId) throws NotFoundException {
        return itemService.patchItem(itemId, itemDto, sharerId);
    }

    @GetMapping
    public Collection<ItemDto> findAll(@RequestHeader("X-Sharer-User-Id") int sharerId) {
        return itemService.findAll(sharerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemByText(@RequestParam String text) {
        return itemService.getItemByText(text);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Exception handleNotFoundException(NotFoundException e) {
        return new Exception("error", e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Exception handleValidationException(ValidationException e) {
        return new Exception("error", e);
    }
}
