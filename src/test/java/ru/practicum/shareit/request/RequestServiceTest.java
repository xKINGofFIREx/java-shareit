package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RequestServiceTest {
    private ItemRequestRepository itemRequestRepository;
    private ItemRequestService itemRequestService;

    @BeforeEach
    public void initialize() {
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        itemRequestService = new ItemRequestService(itemRequestRepository);
    }

    @Test
    public void createRequestTest() throws NotFoundException {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequester(new User(1L, "requester", "requester@mail.ru"));
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("test-request");

        Mockito
                .when(itemRequestRepository.getUserIfExist(1L))
                .thenReturn(Optional.ofNullable(itemRequest.getRequester()));

        Mockito
                .when(itemRequestRepository.save(itemRequest))
                .thenReturn(itemRequest);

        ItemRequestDto itemRequestDto = RequestMapper.toItemRequestDto(itemRequest);
        Assertions.assertEquals(itemRequestDto, itemRequestService.createRequest(1L, itemRequestDto));
    }

    @Test
    public void findAllOwnerRequestsTest() throws NotFoundException {
        User owner = new User(1L, "requester", "requester@mail.ru");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequester(owner);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("test-request");

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(2L);
        itemRequest1.setRequester(owner);
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setDescription("test-request");

        Mockito
                .when(itemRequestRepository.getUserIfExist(1L))
                .thenReturn(Optional.ofNullable(itemRequest.getRequester()));

        List<ItemRequestDto> itemRequestDtos = RequestMapper.toItemRequestDtos(List.of(itemRequest, itemRequest1));
        Mockito
                .when(itemRequestRepository.findAllRequestsByUserId(owner.getId()))
                .thenReturn(List.of(itemRequest, itemRequest1));

        Assertions.assertEquals(itemRequestDtos, itemRequestService.findAllOwnerRequests(1L));
    }

    @Test
    public void findAllRequestsTest() throws ValidationException, NotFoundException {
        User requester = new User(1L, "requester", "requester@mail.ru");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("test-request");

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setId(2L);
        itemRequest1.setRequester(requester);
        itemRequest1.setCreated(LocalDateTime.now().plusSeconds(1));
        itemRequest1.setDescription("test-request");

        Item item = new Item(1L, "item", "test", true,
                new User(3L, "itemOwner", "owner@user.ru"),
                itemRequest);

        Mockito
                .when(itemRequestRepository.findItemsByRequests(List.of(itemRequest, itemRequest1)))
                .thenReturn(List.of(item));

        Mockito
                .when(itemRequestRepository.getUserIfExist(2L))
                .thenReturn(Optional.of(new User(2L, "user", "user@user.ru")));

        List<ItemRequestDto> itemRequestDtos = RequestMapper.toItemRequestDtos(List.of(itemRequest, itemRequest1));
        Page<ItemRequest> page = new PageImpl<>(List.of(itemRequest, itemRequest1));

        Mockito
                .when(itemRequestRepository.findAllRequestsExceptUserId(2L, PageRequest.of(0, 20, Sort.by("created"))))
                .thenReturn(page);
        itemRequestDtos.get(0).getItems().add(ItemMapper.toItemDto(item));

        Assertions.assertEquals(itemRequestDtos, itemRequestService.findAllRequests(0, 20, 2L));
    }

    @Test
    public void getRequestById() throws NotFoundException {
        User owner = new User(1L, "requester", "requester@mail.ru");
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequester(owner);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("test-request");

        Mockito
                .when(itemRequestRepository.getUserIfExist(1L))
                .thenReturn(Optional.ofNullable(itemRequest.getRequester()));

        Mockito
                .when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestDto itemRequestDto = RequestMapper.toItemRequestDto(itemRequest);

        Assertions.assertEquals(itemRequestDto, itemRequestService.getRequestById(1L, 1L));

    }
}
