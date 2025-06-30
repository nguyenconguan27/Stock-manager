package com.manager.stock.manager_stock.model;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptDetailModel {
    private long id;
    private long exportReceiptId;
    private long productId;
    private int plannedQuantity;
    private int actualQuantity;
    private double unitPrice;
    private double totalPrice;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getExportReceiptId() {
        return exportReceiptId;
    }

    public void setExportReceiptId(long exportReceiptId) {
        this.exportReceiptId = exportReceiptId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public int getPlannedQuantity() {
        return plannedQuantity;
    }

    public void setPlannedQuantity(int plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
    }

    public int getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(int actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
