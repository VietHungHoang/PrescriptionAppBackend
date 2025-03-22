package com.vhh.PrescriptionAppBackend.exception;

public class ExpiredTokenException extends Exception {
    public ExpiredTokenException(String message) {
        super(message);
    }
    
}
