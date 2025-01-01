package com.tuanpham.smart_lib_be.util.error;

public class IdInvalidException extends Exception {
    
    public IdInvalidException(String message) {
        super(message);
    }
    
    public IdInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public IdInvalidException(Throwable cause) {
        super(cause);
    }
    
    public IdInvalidException() {
        super();
    }
}
