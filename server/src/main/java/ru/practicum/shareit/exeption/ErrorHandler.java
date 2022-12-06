package ru.practicum.shareit.exeption;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException exception) {
        log.error("Ошибка в поиске пользователя. Пользователь не найден");
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFoundException(final ItemNotFoundException exception) {
        log.error("Ошибка в поиске вещи. Вещи не существует");
        return new ErrorResponse(
                exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingNotFoundException(final BookingNotFoundException exception) {
        log.error("Ошибка в поиске аренды. Аренды не существует");
        return new ErrorResponse(
                exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemRequestNotFoundException(final ItemRequestNotFoundException exception) {
        log.error("Ошибка в поиске запроса на вещь. Запроса не существует");
        return new ErrorResponse(
                exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserValidationException(final ValidationException exception) {
        log.error("Ошибка валидации");
        return new ErrorResponse(
                exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onConstraintViolationException(final ConstraintViolationException exception) {
        log.error("Ошибка запроса");
        return new ErrorResponse(
                exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse onMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        log.error("Ошибка запроса");
        return new ErrorResponse(
                exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable throwable) {
        log.error("Внутренняя ошибка сервера");
        return new ErrorResponse(
                throwable.getMessage());
    }
}