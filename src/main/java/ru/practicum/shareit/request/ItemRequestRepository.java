package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select r from ItemRequest r where r.requester.id = :userId")
    List<ItemRequest> findAllRequestsByUserId(@Param("userId") long userId);

    @Query("select u from User u where u.id = :userId")
    Optional<User> getUserIfExist(@Param("userId") long userId);

    @Query("select i from Item i where i.request.id = :requestId")
    List<Item> getItemsByRequestId(@Param("requestId") long requestId);

    @Query("select r from ItemRequest r where r.requester.id = :userId")
    Page<ItemRequest> findAllRequestsByUserId(@Param("userId") long userId, Pageable pageable);
}
