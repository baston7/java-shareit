package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeption.BookingNotFoundException;
import ru.practicum.shareit.exeption.ItemNotFoundException;
import ru.practicum.shareit.exeption.UserNotFoundException;
import ru.practicum.shareit.exeption.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking addBooking(Booking booking, long creatorId) {
        bookingAddValidator(booking, creatorId);
        return bookingRepository.save(booking);
    }

    public Booking updateStatusBooking(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ItemNotFoundException("Заявка на аренду не найдена"));
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new UserNotFoundException("Не найден пользователь с правом на обновление статуса заявки");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Нельзя изменять статус заявки после подтверждения");
        }
        if (approved && bookingUpdateStatusValidator(booking, ownerId)) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    public Booking findBookingByOwnerItemOrCreator(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ItemNotFoundException("Заявка на аренду не найдена"));
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new UserNotFoundException("Не найден пользователь с правом на просмотр статуса заявки");
        }
        return booking;
    }

    public List<Booking> findCreatorBookings(Long creatorId, String state) {
        try {
            State state1 = State.valueOf(state);
            List<Booking> bookingList;
            switch (state1) {
                case ALL:
                    bookingList = bookingRepository.findByBooker_IdOrderByEndDesc(creatorId);
                    break;
                case PAST:
                    bookingList = bookingRepository
                            .findByBooker_IdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(creatorId,
                                    LocalDateTime.now(), LocalDateTime.now());
                    break;
                case FUTURE:
                    bookingList = bookingRepository
                            .findByBooker_IdAndStartIsAfterAndEndIsAfterOrderByEndDesc(creatorId,
                                    LocalDateTime.now(), LocalDateTime.now());
                    break;
                case CURRENT:
                    bookingList = bookingRepository
                            .findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(creatorId,
                                    LocalDateTime.now(), LocalDateTime.now());
                    break;
                default:
                    Status status = Status.valueOf(state);
                    bookingList = bookingRepository.findByBooker_IdAndStatusEqualsOrderByEndDesc(creatorId, status);
                    break;
            }
            return bookingList;
        } catch (RuntimeException e) {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }
    }

    public List<Booking> findOwnerBookings(Long ownerId, String state) {
        try {
            State state1 = State.valueOf(state);
            List<Booking> bookingList;
            switch (state1) {
                case ALL:
                    bookingList = bookingRepository.findByItem_Owner_IdOrderByEndDesc(ownerId);
                    break;
                case PAST:
                    bookingList = bookingRepository
                            .findByItem_Owner_IdAndStartIsBeforeAndEndIsBeforeOrderByEndDesc(ownerId,
                                    LocalDateTime.now(), LocalDateTime.now());
                    break;
                case FUTURE:
                    bookingList = bookingRepository
                            .findByItem_Owner_IdAndStartIsAfterAndEndIsAfterOrderByEndDesc(ownerId,
                                    LocalDateTime.now(), LocalDateTime.now());
                    break;
                case CURRENT:
                    bookingList = bookingRepository
                            .findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(ownerId,
                                    LocalDateTime.now(), LocalDateTime.now());
                    break;
                default:
                    Status status = Status.valueOf(state);
                    bookingList = bookingRepository.findByItem_Owner_IdAndStatusEqualsOrderByEndDesc(ownerId, status);
                    break;
            }
            return bookingList;
        } catch (RuntimeException e) {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }
    }

    private void bookingAddValidator(Booking booking, long creatorId) {
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException("Время начала аренды должны быть раньше времени окончания");
        }
        if (booking.getItem().getOwner().getId() == creatorId) {
            throw new BookingNotFoundException("Нельзя брать вещь в аренду у самого себя");
        }
    }

    /*валидация пересечения времени, если владелец захочет подтвердить бронь,
     но уже есть подтвержденные будущие брони*/
    private boolean bookingUpdateStatusValidator(Booking booking, long ownerId) {
        LocalDateTime startTime = booking.getStart();
        LocalDateTime endTime = booking.getEnd();
        List<Booking> approvedBookingsFutureOrPresent = bookingRepository.
                findByItem_Owner_IdAndEndIsAfterAndStatusIs(ownerId, LocalDateTime.now(), Status.APPROVED);
        if (approvedBookingsFutureOrPresent.isEmpty()) {
            return true;
        }
        return approvedBookingsFutureOrPresent.stream()
                .anyMatch(booking1 -> (startTime.isBefore(booking1.getStart()) && endTime.isBefore(booking1.getStart()))
                        || (startTime.isAfter(booking1.getEnd()) && endTime.isAfter(booking1.getEnd()))
                );
    }
}