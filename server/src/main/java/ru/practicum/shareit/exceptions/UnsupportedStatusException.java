package ru.practicum.shareit.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class UnsupportedStatusException extends RuntimeException {

        public UnsupportedStatusException(String message) {
                super(message);
        }
}
