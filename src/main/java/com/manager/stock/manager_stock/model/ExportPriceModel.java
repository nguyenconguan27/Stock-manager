package com.manager.stock.manager_stock.model;

import java.time.LocalDateTime;

/**
 * @author Trọng Hướng
 */
public class ExportPriceModel {
    private long id;
    private long productId;
    private LocalDateTime exportTime;
    private double exportPrice;
    private int quantityInStock;
    private double totalPriceInStock;
    private int quantityImported;
    private double totalImportPrice;
    private long importReceiptId;

    public ExportPriceModel() {
    }

    public ExportPriceModel(long id, long productId, LocalDateTime exportTime, double exportPrice, int quantityInStock, int quantityImported, double totalImportPrice, double totalPriceInStock) {
        this.id = id;
        this.productId = productId;
        this.exportTime = exportTime;
        this.exportPrice = exportPrice;
        this.quantityInStock = quantityInStock;
        this.quantityImported = quantityImported;
        this.totalImportPrice = totalImportPrice;
        this.totalPriceInStock = totalPriceInStock;
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

    public LocalDateTime getExportTime() {
        return exportTime;
    }

    public void setExportTime(LocalDateTime exportTime) {
        this.exportTime = exportTime;
    }

    public double getExportPrice() {
        return exportPrice;
    }

    public void setExportPrice(double exportPrice) {
        this.exportPrice = exportPrice;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public int getQuantityImported() {
        return quantityImported;
    }

    public void setQuantityImported(int quantityImported) {
        this.quantityImported = quantityImported;
    }

    public double getTotalImportPrice() {
        return totalImportPrice;
    }

    public void setTotalImportPrice(double totalImportPrice) {
        this.totalImportPrice = totalImportPrice;
    }

    public double getTotalPriceInStock() {
        return totalPriceInStock;
    }

    public void setTotalPriceInStock(double totalPriceInStock) {
        this.totalPriceInStock = totalPriceInStock;
    }

    public long getImportReceiptId() {
        return importReceiptId;
    }

    public void setImportReceiptId(long importReceiptId) {
        this.importReceiptId = importReceiptId;
    }
}
