package com.manager.stock.manager_stock.exception;

/**
 * @author Trọng Hướng
 */
public class DivisionByZeroException extends RuntimeException {
    public DivisionByZeroException(String message) {
        super(message);
    }
}
