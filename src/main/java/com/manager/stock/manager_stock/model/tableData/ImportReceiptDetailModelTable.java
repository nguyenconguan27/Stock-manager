package com.manager.stock.manager_stock.model.tableData;

import javafx.beans.property.*;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptDetailModelTable {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty code = new SimpleStringProperty();
    private final LongProperty importReceiptId = new SimpleLongProperty();
    private final LongProperty productId = new SimpleLongProperty();
    private final IntegerProperty plannedQuantity = new SimpleIntegerProperty();
    private final IntegerProperty actualQuantity = new SimpleIntegerProperty();
    private final DoubleProperty unitPrice = new SimpleDoubleProperty();
    private final DoubleProperty totalPrice = new SimpleDoubleProperty();
    private final StringProperty productName = new SimpleStringProperty();
    private final StringProperty unitPriceFormat = new SimpleStringProperty();
    private final StringProperty totalPriceFormat = new SimpleStringProperty();

    public ImportReceiptDetailModelTable(long id, long importReceiptId, long productId,
                                         int plannedQuantity, int actualQuantity,
                                         double unitPrice, double totalPrice, String productName, String unitPriceFormat, String totalPriceFormat, String productCode) {
        this.id.set(id);
        this.importReceiptId.set(importReceiptId);
        this.productId.set(productId);
        this.plannedQuantity.set(plannedQuantity);
        this.actualQuantity.set(actualQuantity);
        this.unitPrice.set(unitPrice);
        this.totalPrice.set(totalPrice);
        this.productName.set(productName);
        this.unitPriceFormat.set(unitPriceFormat);
        this.totalPriceFormat.set(totalPriceFormat);
        this.code.set(productCode);
    }

    // Property methods
    public LongProperty idProperty() { return id; }
    public LongProperty importReceiptIdProperty() { return importReceiptId; }
    public LongProperty productIdProperty() { return productId; }
    public IntegerProperty plannedQuantityProperty() { return plannedQuantity; }
    public IntegerProperty actualQuantityProperty() { return actualQuantity; }
    public DoubleProperty unitPriceProperty() { return unitPrice; }
    public DoubleProperty totalPriceProperty() { return totalPrice; }
    public StringProperty productNameProperty() { return productName; }
    public StringProperty unitPriceFormatProperty() { return unitPriceFormat; }
    public StringProperty totalPriceFormatProperty() { return totalPriceFormat; }
    public StringProperty codeProperty() {return code;}


    // Getter methods
    public Long getId() { return id.get(); }
    public long getImportReceiptId() { return importReceiptId.get(); }
    public Long getProductId() { return productId.get(); }
    public int getPlannedQuantity() { return plannedQuantity.get(); }
    public int getActualQuantity() { return actualQuantity.get(); }
    public double getUnitPrice() { return unitPrice.get(); }
    public double getTotalPrice() { return totalPrice.get(); }
    public String getProductName() { return productName.get(); }
    public String getProductCode() { return code.get();}

    public void setActualQuantity(int  actualQuantity) { this.actualQuantity.set(actualQuantity); }
    public void setTotalPrice(double totalPrice) { this.totalPrice.set(totalPrice); }
    public void setUnitPriceFormat(String unitPriceFormat) { this.unitPriceFormat.set(unitPriceFormat); }
    public void setTotalPriceFormat(String totalPriceFormat) { this.totalPriceFormat.set(totalPriceFormat); }
    public void setPlannedQuantity(int plannedQuantity) { this.plannedQuantity.set(plannedQuantity); }
}
