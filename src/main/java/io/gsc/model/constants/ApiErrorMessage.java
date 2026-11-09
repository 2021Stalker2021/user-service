package io.gsc.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ApiErrorMessage {

    USER_NOT_FOUND_BY_ID("User with ID: %s was not found"),
    EMAIL_ALREADY_EXISTS("Email: %s already exists");

    private final String message;

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}

