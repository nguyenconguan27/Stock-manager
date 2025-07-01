package com.manager.stock.manager_stock.model;

/**
 * @author Trọng Hướng
 */
public class ProductModel {
    private String id;
    private String name;
    private int quantity;
    private String unit;
    private int unitPrice;
    private String createdAt;

    public ProductModel(String id, String name, int quantity, String unit, int unitPrice) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.unitPrice = unitPrice;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUnit() {
        return unit;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
