package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    public ItemRequestDto createRequest(long userId, ItemRequestDto itemRequestDto) throws NotFoundException {
        ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestDto);

        itemRequest.setRequester(itemRequestRepository.getUserIfExist(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден")));

        return RequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    public List<ItemRequestDto> findAllOwnerRequests(long userId) throws NotFoundException {
        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllRequestsByUserId(userId);

        List<ItemRequestDto> itemRequestDtos = setItems(itemRequests);

        itemRequestRepository.getUserIfExist(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return itemRequestDtos.stream()
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> findAllRequests(Integer from, Integer size, long userId) throws ValidationException, NotFoundException {
        if (from == null || size == null)
            return new ArrayList<>();

        if (from < 0 || size < 1)
            throw new ValidationException("Ошибка валидации пагинации запроса");

        itemRequestRepository.getUserIfExist(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Pageable pageable = PageRequest.of(from, size, Sort.by("created"));
        List<ItemRequest> itemRequest = itemRequestRepository.findAllRequestsExceptUserId(userId, pageable).getContent();
        return setItems(itemRequest);
    }

    public ItemRequestDto getRequestById(long requestId, long userId) throws NotFoundException {
        itemRequestRepository.getUserIfExist(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequestDto itemRequestDto = RequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден")));

        itemRequestDto.setItems(ItemMapper.toItemDtos(itemRequestRepository.getItemsByRequestId(requestId)));
        return itemRequestDto;
    }

    private List<ItemRequestDto> setItems(List<ItemRequest> itemRequests) {
        List<ItemDto> itemDtos = ItemMapper.toItemDtos(itemRequestRepository.findItemsByRequests(itemRequests));

        List<ItemRequestDto> itemRequestDtos = RequestMapper.toItemRequestDtos(itemRequests);

        for (ItemRequestDto itemRequestDto : itemRequestDtos) {
            for (ItemDto itemDto : itemDtos) {
                if (itemDto.getRequestId() == itemRequestDto.getId())
                    itemRequestDto.getItems().add(itemDto);
            }
        }

        return itemRequestDtos;
    }
}
