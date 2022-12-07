package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    //поиск аренд создателя в будущем
    List<Booking> findByBookerIdAndStartIsAfterAndEndIsAfterOrderByEndDesc(long bookerId, LocalDateTime time1,
                                                                           LocalDateTime time2,
                                                                           PageRequest pageRequest);

    //поиск всех аренд создателя
    List<Booking> findByBookerIdOrderByEndDesc(long bookerID, PageRequest pageRequest);

    //поиск текущих аренд создателя
    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(long bookerId, LocalDateTime time1,
                                                                            LocalDateTime time2,
                                                                            PageRequest pageRequest);

    //поиск прошедших аренд создателя
    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(long bookerId, LocalDateTime time1,
                                                                             LocalDateTime time2,
                                                                             PageRequest pageRequest);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(long bookerId, LocalDateTime time1,
                                                                             LocalDateTime time2);

    //поиск аренд создателя по статусу заявки
    List<Booking> findByBookerIdAndStatusEqualsOrderByEndDesc(long bookerID, Status status, PageRequest pageRequest);

    //поиск будущих аренд по владельцу вещи
    List<Booking> findByItemOwnerIdAndStartIsAfterAndEndIsAfterOrderByEndDesc(long ownerId, LocalDateTime time1,
                                                                              LocalDateTime time2,
                                                                              PageRequest pageRequest);

    //поиск всех аренд по владельцу вещи
    List<Booking> findByItemOwnerIdOrderByEndDesc(long ownerId, PageRequest pageRequest);

    //поиск текущих аренд по владельцу вещи
    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(long ownerId, LocalDateTime time1,
                                                                               LocalDateTime time2,
                                                                               PageRequest pageRequest);

    //поиск прошлых аренд по владельцу вещи
    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(long ownerId, LocalDateTime time1,
                                                                                LocalDateTime time2,
                                                                                PageRequest pageRequest);

    //поиск аренд по владельцу вещи и статусу заявки
    List<Booking> findByItemOwnerIdAndStatusEqualsOrderByEndDesc(long ownerId, Status status,
                                                                 PageRequest pageRequest);

    //поиск первой будущей аренды по вещи, где статусы заявки не соответсвуют заданным
    Optional<Booking> findTopByItemIdAndStartIsAfterAndStatusIsNotAndStatusIsNotOrderByEndDesc(long itemId,
                                                                                               LocalDateTime time,
                                                                                               Status status,
                                                                                               Status status1);

    //поиск последней аренды по вещи, где статус соответсвует заданному
    Optional<Booking> findTopByItemIdAndEndIsBeforeAndStatusIsOrderByEndDesc(long itemId,
                                                                             LocalDateTime time,
                                                                             Status status
    );

    //поиск текущих и будущих аренд по владельцу вещи с заданным статусом
    List<Booking> findByItemOwnerIdAndEndIsAfterAndStatusIs(long ownerId, LocalDateTime time1,
                                                            Status status);

}
