package edu.java.scrapper.exception;

import edu.java.scrapper.dto.bot.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ScrapperControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String BAD_REQUEST = "400";
    private static final String NOT_FOUND = "404";

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    protected ResponseEntity<ApiErrorResponse> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException e) {
        ApiErrorResponse response = new ApiErrorResponse("User already registered", BAD_REQUEST, e);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InvalidParameterException.class)
    protected ResponseEntity<ApiErrorResponse> handleInvalidParameterException(InvalidParameterException e) {
        ApiErrorResponse response = new ApiErrorResponse("Invalid request parameter", BAD_REQUEST, e);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameterException(
        MissingServletRequestParameterException e
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
            "Missing required parameter",
                BAD_REQUEST,
            "Parameter " + e.getParameterName() + " is required",
            e
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    protected ResponseEntity<ApiErrorResponse> handleHttpMessageConversionException(
        HttpMessageConversionException e
    ) {
        ApiErrorResponse response = new ApiErrorResponse("Invalid request format", BAD_REQUEST, e);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ChatNotFoundException.class)
    protected ResponseEntity<ApiErrorResponse> handleChatNotFoundException(ChatNotFoundException e) {
        ApiErrorResponse response = new ApiErrorResponse("Chat not found", NOT_FOUND, e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(LinkAlreadyTrackedException.class)
    protected ResponseEntity<ApiErrorResponse> handleLinkAlreadyTrackedException(
        LinkAlreadyTrackedException e
    ) {
        ApiErrorResponse response = new ApiErrorResponse("Link already tracked", BAD_REQUEST, e);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(LinkNotTrackedException.class)
    protected ResponseEntity<ApiErrorResponse> handleLinkNotTrackedException(LinkNotTrackedException e) {
        ApiErrorResponse response = new ApiErrorResponse("Link not tracked", NOT_FOUND, e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
