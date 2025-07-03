package com.manager.stock.manager_stock.model;

/**
 * @author Trọng Hướng
 */
public class ProductModel {
    private Long id;
    private String code;
    private String name;
    private int quantity;
    private String unit;
    private int unitPrice;
    private String createdAt;

    public ProductModel(long id, String code, String name, int quantity, String unit, int unitPrice) {
        this.id = id;
        this.code = code;
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

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }
}
