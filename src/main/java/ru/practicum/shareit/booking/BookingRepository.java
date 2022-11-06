package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdAndStartIsAfterAndEndIsAfterOrderByEndDesc(long bookerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findByBooker_IdOrderByEndDesc(long BookerID);

    List<Booking> findByBooker_IdAndStartIsAfterAndEndIsBeforeOrderByEndDesc(long bookerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(long bookerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findByBooker_IdAndStatusEqualsOrderByEndDesc(long bookerID, Status status);

    //--------------------------------------------
    List<Booking> findByItem_Owner_IdAndStartIsAfterAndEndIsAfterOrderByEndDesc(long ownerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findByItem_Owner_IdOrderByEndDesc(long ownerId);

    List<Booking> findByItem_Owner_IdAndStartIsAfterAndEndIsBeforeOrderByEndDesc(long ownerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(long ownerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findByItem_Owner_IdAndStatusEqualsOrderByEndDesc(long ownerId, Status status);

    Optional<Booking> findTopByItem_IdAndEndIsAfterAndStatusIsNotAndStatusIsNotOrderByEndDesc(long itemId, LocalDateTime time, Status status, Status status1);

    Optional<Booking> findTopByItem_IdAndEndIsBeforeAndStatusIsNotAndStatusIsNotOrderByEndDesc(long itemId, LocalDateTime time, Status status, Status status1);
}
