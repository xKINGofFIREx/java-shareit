package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {
    private int newItemId = 1;
    private final Map<Integer, ItemDto> items = new HashMap<>();
    private final InMemoryUserStorage users = InMemoryUserStorage.getInstance();

    @Override
    public ItemDto getItem(int itemId) {
        return items.get(itemId);
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, int sharerId) throws NotFoundException {
        UserDto userDto = users.getUser(sharerId);
        if (userDto == null)
            throw new NotFoundException("Пользователь не найден");

        if (itemDto.getName() == null || itemDto.getName().equals("")
                || itemDto.getDescription() == null || itemDto.getDescription().equals("")
                || itemDto.getAvailable() == null) {
            throw new ValidationException("Ошибка валидации");
        }
        itemDto.setId(newItemId++);
        itemDto.setOwner(UserMapper.toUser(userDto));
        items.put(itemDto.getId(), itemDto);
        return itemDto;
    }

    @Override
    public ItemDto patchItem(int itemId, ItemDto itemDto, int sharerId) throws NotFoundException {
        UserDto userDto = users.getUser(sharerId);

        if (userDto == null)
            throw new NotFoundException("Пользователь не найден");
        if (items.get(itemId).getOwner().getId() != sharerId)
            throw new NotFoundException("Использован другой пользователь");

        ItemDto itemToPatch = items.get(itemId);
        if (itemDto.getName() != null)
            itemToPatch.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            itemToPatch.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            itemToPatch.setAvailable(itemDto.getAvailable());
        return items.get(itemId);
    }

    @Override
    public void deleteItem(int itemId) {
        items.remove(itemId);
    }

    @Override
    public List<ItemDto> findAll(int sharerId) {
        return items.values()
                .stream()
                .filter(i -> i.getOwner().getId() == sharerId)
                .collect(Collectors.toList());
    }

    public List<ItemDto> getItemByText(String text) {
        if (text.equals(""))
            return new ArrayList<>();
        return items.values()
                .stream()
                .filter(i -> i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable())
                .collect(Collectors.toList());
    }
}
