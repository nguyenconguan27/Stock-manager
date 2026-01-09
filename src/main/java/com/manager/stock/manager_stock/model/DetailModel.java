package com.manager.stock.manager_stock.model;

public class DetailModel {
    protected  Long productId;
    public DetailModel() {

    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }

    public DetailModel(Long productId) {
        this.productId = productId;
    }
}
