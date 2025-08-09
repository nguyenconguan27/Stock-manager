package com.manager.stock.manager_stock.model.dto;

/**
 * @author Trọng Hướng
 */
public record ProductIdAndCodeAndNameAndQuantityInStock(long productId, String productCode, String productName, int quantityInStock) {
}
