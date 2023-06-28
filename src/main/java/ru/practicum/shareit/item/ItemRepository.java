package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(long ownerId);

    @Query("select b from Booking b where b.item.id = :itemId and b.start >= :now order by b.start")
    Optional<List<Booking>> findNextBookingByItemId(@Param("itemId") long itemId,
                                                    @Param("now") LocalDateTime now);

    @Query("select b from Booking b where b.item.id = :itemId and b.start <= :now order by b.start")
    Optional<List<Booking>> findLastBookingByItemId(@Param("itemId") long itemId,
                                                    @Param("now") LocalDateTime now);

    @Query("select b from Booking b where b.item.id = :itemId and b.booker.id = :bookerId order by b.start asc")
    Optional<List<Booking>> findBookingByItemIdAndBookerId(@Param("itemId") long itemId,
                                                           @Param("bookerId") long bookerId);
}
