package com.manager.stock.manager_stock.model;

/**
 * @author Trọng Hướng
 */
public class InventoryDetailModel {
    private Long id;
    private Long productId;
    private Integer quantity;
    private Double totalPrice;
    private Integer academicYear;

    public InventoryDetailModel() {
    }

    public InventoryDetailModel(Long id, Long productId, Integer quantity, Double totalPrice, Integer academicYear) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.academicYear = academicYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }

    @Override
    public String toString() {
        return "InventoryDetailModel{" +
                "id=" + id +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", academicYear=" + academicYear +
                '}';
    }
}
