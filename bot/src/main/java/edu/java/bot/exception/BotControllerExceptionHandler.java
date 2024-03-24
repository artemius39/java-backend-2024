package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class BotControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String BAD_REQUEST = "400";

    @ExceptionHandler(ChatNotFoundException.class)
    protected ResponseEntity<ApiErrorResponse> handleChatNotFoundException(ChatNotFoundException e) {
        ApiErrorResponse response = new ApiErrorResponse("Chat not found", BAD_REQUEST, e);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    protected ResponseEntity<ApiErrorResponse> handleHttpMessageConversionException(
        HttpMessageConversionException e
    ) {
        ApiErrorResponse response = new ApiErrorResponse("Invalid request format", BAD_REQUEST, e);
        return ResponseEntity.badRequest().body(response);
    }
}
