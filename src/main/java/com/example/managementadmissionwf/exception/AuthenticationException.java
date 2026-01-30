package com.example.managementadmissionwf.exception;

import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {
    private String errorCode;
    public AuthenticationException(String message) {
        super(message);
        this.errorCode = "LOGIN_ERROR";
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);

    }

}