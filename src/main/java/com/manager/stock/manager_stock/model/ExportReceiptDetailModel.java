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
    private double totalPrice;
    private String productName;
    private String unit;
    private String productCode;
    private long exportPriceId;
    private double originalUnitPrice;
    private double displayUnitPrice;

    // thêm trường đánh dấu nếu sau khi sửa hoặc xóa phiếu nhập mà số lượng tồn kho tại thời điểm
    // đó không còn đáp ứng được số lượng của phiếu xuất ==> lại lại actualQuantity = 0 và thêm status + message
    private int status;
    private String message;

    public ExportReceiptDetailModel() {}

    public ExportReceiptDetailModel(long id, long exportReceiptId, long productId, int plannedQuantity, int actualQuantity, double displayUnitPrice, double totalPrice, String productName, String productCode, long exportPriceId, int status, String message, double originalUnitPrice) {
        this.id = id;
        this.exportReceiptId = exportReceiptId;
        this.productId = productId;
        this.plannedQuantity = plannedQuantity;
        this.actualQuantity = actualQuantity;
        this.displayUnitPrice = displayUnitPrice;
        this.totalPrice = totalPrice;
        this.productName = productName;
        this.productCode = productCode;
        this.exportPriceId = exportPriceId;
        this.status = status;
        this.message = message;
        this.originalUnitPrice = originalUnitPrice;
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

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setActualQuantity(int actualQuantity) {
        this.actualQuantity = actualQuantity;
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

    public long getExportPriceId() {
        return exportPriceId;
    }

    public void setExportPriceId(long exportPriceId) {
            this.exportPriceId = exportPriceId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getOriginalUnitPrice() {
        return originalUnitPrice;
    }

    public void setOriginalUnitPrice(double originalUnitPrice) {
        this.originalUnitPrice = originalUnitPrice;
    }

    public double getDisplayUnitPrice() {
        return displayUnitPrice;
    }

    public void setDisplayUnitPrice(double displayUnitPrice) {
        this.displayUnitPrice = displayUnitPrice;
    }
}
