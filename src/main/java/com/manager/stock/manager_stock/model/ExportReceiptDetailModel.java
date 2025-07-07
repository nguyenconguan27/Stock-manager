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
    private String productName;
    private String productCode;

    public ExportReceiptDetailModel() {}

    public ExportReceiptDetailModel(long id, long exportReceiptId, long productId, int plannedQuantity, int actualQuantity, double unitPrice, double totalPrice, String productName, String productCode) {
        this.id = id;
        this.exportReceiptId = exportReceiptId;
        this.productId = productId;
        this.plannedQuantity = plannedQuantity;
        this.actualQuantity = actualQuantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.productName = productName;
        this.productCode = productCode;
    }

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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
