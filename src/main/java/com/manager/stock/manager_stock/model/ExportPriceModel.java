package com.manager.stock.manager_stock.model;

/**
 * @author Trọng Hướng
 */
public class ExportPriceModel {
    private long id;
    private long productId;
    private long exportTime;
    private double exportPrice;
    private int quantityInStock;
    private int quantityImported;
    private double importPrice;

    public ExportPriceModel() {
    }

    public ExportPriceModel(long id, long productId, long exportTime, double exportPrice, int quantityInStock, int quantityImported, double importPrice) {
        this.id = id;
        this.productId = productId;
        this.exportTime = exportTime;
        this.exportPrice = exportPrice;
        this.quantityInStock = quantityInStock;
        this.quantityImported = quantityImported;
        this.importPrice = importPrice;
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

    public long getExportTime() {
        return exportTime;
    }

    public void setExportTime(long exportTime) {
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

    public double getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(double importPrice) {
        this.importPrice = importPrice;
    }
}
