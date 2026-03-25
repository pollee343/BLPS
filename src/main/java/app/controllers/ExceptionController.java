package app.controllers;

import app.dto.MessageOnlyResponse;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;


@RestControllerAdvice
public class ExceptionController {

    private static Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public MessageOnlyResponse handleNotFoundException(EntityNotFoundException e) {
        logger.error(e.getMessage());
        e.printStackTrace();
        return new MessageOnlyResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public MessageOnlyResponse handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error(e.getMessage());
        e.printStackTrace();
        return new MessageOnlyResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({MessagingException.class, IOException.class})
    public MessageOnlyResponse handleExceptionForEmailSending(Exception e) {
        logger.error(e.getMessage());
        e.printStackTrace();
        return new MessageOnlyResponse("Ошибка при отправке отчета на почту");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public MessageOnlyResponse handleRuntimeException(RuntimeException e) {
        logger.error(e.getMessage());
        e.printStackTrace();
        return new MessageOnlyResponse("Внутренняя ошибка сервера");
    }
}
