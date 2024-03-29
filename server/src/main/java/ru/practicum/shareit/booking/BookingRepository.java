package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b where b.booker.id = :bookerId")
    List<Booking> findBookingsByBookerId(@Param("bookerId") long bookerId);

    @Query("select b from Booking b where b.item.owner.id = :ownerId")
    List<Booking> findBookingsByItemOwnerId(@Param("ownerId") long ownerId);

    @Query("select b from Booking b where b.id in :bookingIds and b.status = :status")
    List<Booking> findBookingsByBookingIdsAndStatus(@Param("bookingIds") Iterable<Long> bookingIds,
                                                    @Param("status") BookingStatus status);

    @Query("select b from Booking b where b.id in :bookingIds")
    List<Booking> findBookingsByBookingIds(@Param("bookingIds") Iterable<Long> bookingIds);

    @Query("select b from Booking b where b.id in :bookingIds and (b.start <= :now and b.end >= :now)")
    List<Booking> findBookingsByBookingIdsCurrent(@Param("bookingIds") Iterable<Long> bookingIds,
                                                  @Param("now") LocalDateTime now);

    @Query("select b from Booking b where b.id in :bookingIds and b.start >= :now")
    List<Booking> findBookingsByBookingIdsFuture(@Param("bookingIds") Iterable<Long> bookingIds,
                                                 @Param("now") LocalDateTime now);

    @Query("select b from Booking b where b.id in :bookingIds and b.end <= :now")
    List<Booking> findBookingsByBookingIdsPast(@Param("bookingIds") Iterable<Long> bookingIds,
                                               @Param("now") LocalDateTime now);

}
