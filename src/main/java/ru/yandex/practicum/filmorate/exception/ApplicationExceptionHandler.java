package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameter(final MethodArgumentNotValidException e) {
        return new ErrorResponse(
                "Ошибка валидации.", e.getMessage()
        );
    }

    @ExceptionHandler(ExceptionNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFound() {
        return new ErrorResponse("Запрошенный объект не найден.", "Такой страницы не существует или запрошенный объект не найден.");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException() {
        return new ErrorResponse("Внутренняя ошибка сервера.", "Попробуйте обратиться позже.");
    }

}