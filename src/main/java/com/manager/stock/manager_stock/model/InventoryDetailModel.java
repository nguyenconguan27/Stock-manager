package com.manager.stock.manager_stock.model;

/**
 * @author Trọng Hướng
 */
public class InventoryDetailModel {
    private long id;
    private long productId;
    private int quantity;
    private double totalPrice;
    private int academicYear;

    public InventoryDetailModel() {
    }

    public InventoryDetailModel(long id, long productId, int quantity, double totalPrice, int academicYear) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.academicYear = academicYear;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        this.academicYear = academicYear;
    }
}
