package com.manager.stock.manager_stock.exception;

/**
 * @author Trọng Hướng
 */
public class StockUnderFlowException extends RuntimeException {
    public StockUnderFlowException(String message) {
        super(message);
    }
}
