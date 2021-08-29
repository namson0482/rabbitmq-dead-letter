package com.sonvu.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BusinessException extends Exception {

    private String message;
    private int code;

    public BusinessException(String message) {
        this.message = message;
    }

    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }
}
