package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {
    ItemDto getItem(int itemId);

    ItemDto addItem(ItemDto itemDto, int sharerId) throws NotFoundException;

    ItemDto patchItem(int itemId, ItemDto itemDto, int sharerId) throws NotFoundException;

    void deleteItem(int itemId);

    List<ItemDto> findAll(int sharerId);
}
