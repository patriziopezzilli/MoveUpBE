package com.moveup.exception;

/**
 * Custom exception for QR Code validation errors
 */
public class QRCodeException extends RuntimeException {
    
    private final String errorCode;
    
    public QRCodeException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
