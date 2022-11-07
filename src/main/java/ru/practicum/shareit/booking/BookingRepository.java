package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    //поиск аренд создателя в будущем
    List<Booking> findByBooker_IdAndStartIsAfterAndEndIsAfterOrderByEndDesc(long bookerId, LocalDateTime time1,
                                                                            LocalDateTime time2);

    //поиск всех аренд создателя
    List<Booking> findByBooker_IdOrderByEndDesc(long BookerID);

    //поиск текущих аренд создателя
    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(long bookerId, LocalDateTime time1,
                                                                             LocalDateTime time2);

    //поиск прошедших аренд создателя
    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(long bookerId, LocalDateTime time1,
                                                                              LocalDateTime time2);

    //поиск аренд создателя по статусу заявки
    List<Booking> findByBooker_IdAndStatusEqualsOrderByEndDesc(long bookerID, Status status);

    //поиск будущих аренд по владельцу вещи
    List<Booking> findByItem_Owner_IdAndStartIsAfterAndEndIsAfterOrderByEndDesc(long ownerId, LocalDateTime time1,
                                                                                LocalDateTime time2);

    //поиск всех аренд по владельцу вещи
    List<Booking> findByItem_Owner_IdOrderByEndDesc(long ownerId);

    //поиск текущих аренд по владельцу вещи
    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(long ownerId, LocalDateTime time1,
                                                                                 LocalDateTime time2);

    //поиск прошлых аренд по владельцу вещи
    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(long ownerId, LocalDateTime time1,
                                                                                  LocalDateTime time2);

    //поиск аренд по владельцу вещи и статусу заявки
    List<Booking> findByItem_Owner_IdAndStatusEqualsOrderByEndDesc(long ownerId, Status status);

    //поиск первой будущей аренды по вещи, где статусы заявки не соответсвуют заданным
    Optional<Booking> findTopByItem_IdAndStartIsAfterAndStatusIsNotAndStatusIsNotOrderByEndDesc(long itemId,
                                                                                                LocalDateTime time,
                                                                                                Status status,
                                                                                                Status status1);

    //поиск последней аренды по вещи, где статус соответсвует заданному
    Optional<Booking> findTopByItem_IdAndEndIsBeforeAndStatusIsOrderByEndDesc(long itemId,
                                                                              LocalDateTime time,
                                                                              Status status
    );

    //поиск текущих и будущих аренд по владельцу вещи с заданным статусом
    List<Booking> findByItem_Owner_IdAndEndIsAfterAndStatusIs(long ownerId, LocalDateTime time1,
                                                                                 Status status);

}
