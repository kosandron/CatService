package kosandron.controllers;

import kosandron.data.ErrorData;
import kosandron.exceptions.NotFoundException;
import kosandron.exceptions.SameCatException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerAdvisor {
    @ExceptionHandler(value = {NotFoundException.class, SameCatException.class})
    public ResponseEntity<ErrorData> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity.badRequest().body(ErrorData.builder()
                .message(exception.getMessage())
                .time(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorData> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest().body(ErrorData.builder()
                .message(exception.getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(" ")))
                .time(LocalDateTime.now())
                .build());
    }
}