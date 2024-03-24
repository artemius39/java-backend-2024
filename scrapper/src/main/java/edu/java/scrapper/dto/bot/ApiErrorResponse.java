package edu.java.scrapper.dto.bot;

import java.util.Arrays;
import java.util.List;

public record ApiErrorResponse(
    String description,
    String code,
    String exceptionName,
    String exceptionMessage,
    List<String> stackTrace
) {
    public ApiErrorResponse(String description, String code, Throwable cause) {
        this(description, code, cause.getMessage(), cause);
    }

    public ApiErrorResponse(String description, String code, String message, Throwable cause) {
        this(
            description,
            code,
            cause.getClass().getSimpleName(),
            message,
            Arrays.stream(cause.getStackTrace())
                .map(StackTraceElement::toString)
                .toList()
        );
    }
}
