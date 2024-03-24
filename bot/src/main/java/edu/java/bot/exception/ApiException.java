package edu.java.bot.exception;

import edu.java.bot.dto.response.ApiErrorResponse;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final String code;

    public ApiException(ApiErrorResponse response) {
        super(response.exceptionMessage());
        code = response.code();
    }
}
