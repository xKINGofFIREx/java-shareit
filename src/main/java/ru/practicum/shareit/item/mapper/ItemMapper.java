package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwner(),
                item.getRequest() == null ? 0 : item.getRequest().getId(),
                new ArrayList<>(),
                null,
                null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                null
        );
    }

    public static List<ItemDto> toItemDtos(List<Item> items) {
        List<ItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            itemDtos.add(toItemDto(item));
        }

        return itemDtos;
    }
}
