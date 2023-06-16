package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

@Service
public class ItemService {
    private final ItemStorage itemStorage;

    public ItemService(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    public ItemDto getItem(int itemId) {
        return itemStorage.getItem(itemId);
    }

    public ItemDto addItem(ItemDto itemDto, int sharerId) throws NotFoundException {
        return itemStorage.addItem(itemDto, sharerId);
    }

    public ItemDto patchItem(int itemId, ItemDto itemDto, int sharerId) throws NotFoundException {
        return itemStorage.patchItem(itemId, itemDto, sharerId);
    }

    public void deleteItem(int itemId) {
        itemStorage.deleteItem(itemId);
    }

    public List<ItemDto> findAll(int sharerId) {
        return itemStorage.findAll(sharerId);
    }

    public List<ItemDto> getItemByText(String text) {
        return ((InMemoryItemStorage) itemStorage).getItemByText(text);
    }
}
