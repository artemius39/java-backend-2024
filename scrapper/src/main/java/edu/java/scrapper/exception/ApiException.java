package edu.java.scrapper.exception;

import edu.java.scrapper.dto.bot.ApiErrorResponse;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final String code;

    public ApiException(ApiErrorResponse response) {
        super(response.exceptionMessage());
        code = response.code();
    }
}

