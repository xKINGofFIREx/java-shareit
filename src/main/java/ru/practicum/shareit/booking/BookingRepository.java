package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingsByBooker_Id(long bookerId);

    @Query("select b from Booking b where b.item.id in :itemIds and b.status = :status")
    List<Booking> findBookingsByItemIdsAndStatus(@Param("itemIds") Iterable<Long> itemIds,
                                                 @Param("status") BookingStatus status);

    @Query("select b from Booking b where b.item.id in :itemIds")
    List<Booking> findBookingsByItemIds(@Param("itemIds") Iterable<Long> itemIds);

    @Query("select b from Booking b where b.item.id in :itemIds and (b.start <= :now and b.end >= :now)")
    List<Booking> findBookingsByItemIdsCurrent(@Param("itemIds") Iterable<Long> itemIds,
                                               @Param("now") LocalDateTime now);

    @Query("select b from Booking b where b.item.id in :itemIds and b.start >= :now")
    List<Booking> findBookingsByItemIdsFuture(@Param("itemIds") Iterable<Long> itemIds,
                                              @Param("now") LocalDateTime now);

    @Query("select b from Booking b where b.item.id in :itemIds and b.end <= :now")
    List<Booking> findBookingsByItemIdsPast(@Param("itemIds") Iterable<Long> itemIds,
                                            @Param("now") LocalDateTime now);

}
