package com.manager.stock.manager_stock.model;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptDetailModel {
    private long id;
    private long importReceiptId;
    private long productId;
    private int plannedQuantity;
    private int actualQuantity;
    private double unitPrice;
    private double totalPrice;
    private String productName;
    private String unitPriceFormat;
    private String totalPriceFormat;
    private String productCode;

    public ImportReceiptDetailModel() {}

    public ImportReceiptDetailModel(long id, long importReceiptId, long productId, int plannedQuantity, int actualQuantity, double unitPrice, double totalPrice, String productName, String productCode) {
        this.id = id;
        this.importReceiptId = importReceiptId;
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

    public long getImportReceiptId() {
        return importReceiptId;
    }

    public void setImportReceiptId(long importReceiptId) {
        this.importReceiptId = importReceiptId;
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

    public String getUnitPriceFormat() {
        return unitPriceFormat;
    }

    public void setUnitPriceFormat(String unitPriceFormat) {
        this.unitPriceFormat = unitPriceFormat;
    }

    public String getTotalPriceFormat() {
        return totalPriceFormat;
    }

    public void setTotalPriceFormat(String totalPriceFormat) {
        this.totalPriceFormat = totalPriceFormat;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @Override
    public String toString() {
        return "ImportReceiptDetailModel{" +
                "id=" + id +
                ", importReceiptId=" + importReceiptId +
                ", productId='" + productId + '\'' +
                ", plannedQuantity=" + plannedQuantity +
                ", actualQuantity=" + actualQuantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                ", productCode=" + productCode +
                ", productName='" + productName + '\'' +
                '}';
    }
}
