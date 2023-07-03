package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {
    ItemDto getItem(long itemId);

    ItemDto addItem(ItemDto itemDto, long sharerId) throws NotFoundException;

    ItemDto patchItem(long itemId, ItemDto itemDto, long sharerId) throws NotFoundException;

    void deleteItem(long itemId);

    List<ItemDto> findAll(long sharerId);
}
