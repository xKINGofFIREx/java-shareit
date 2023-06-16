package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String SHARER_HEADER = "X-Sharer-User-Id";

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        return itemService.getItem(itemId);
    }

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto,
                           @RequestHeader(SHARER_HEADER) int sharerId) throws NotFoundException {
        return itemService.addItem(itemDto, sharerId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId) {
        itemService.deleteItem(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@PathVariable int itemId,
                             @RequestBody ItemDto itemDto,
                             @RequestHeader(SHARER_HEADER) int sharerId) throws NotFoundException {
        return itemService.patchItem(itemId, itemDto, sharerId);
    }

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(SHARER_HEADER) int sharerId) {
        return itemService.findAll(sharerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam String text) {
        return itemService.getItemByText(text);
    }
}
