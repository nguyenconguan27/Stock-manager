package com.manager.stock.manager_stock.model.tableData;

import javafx.beans.property.*;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptDetailModelTable {
    private final LongProperty id = new SimpleLongProperty();
    private final LongProperty importReceiptId = new SimpleLongProperty();
    private final LongProperty productId = new SimpleLongProperty();
    private final IntegerProperty plannedQuantity = new SimpleIntegerProperty();
    private final IntegerProperty actualQuantity = new SimpleIntegerProperty();
    private final DoubleProperty unitPrice = new SimpleDoubleProperty();
    private final DoubleProperty totalPrice = new SimpleDoubleProperty();

    public ImportReceiptDetailModelTable(long id, long importReceiptId, long productId,
                                         int plannedQuantity, int actualQuantity,
                                         double unitPrice, double totalPrice) {
        this.id.set(id);
        this.importReceiptId.set(importReceiptId);
        this.productId.set(productId);
        this.plannedQuantity.set(plannedQuantity);
        this.actualQuantity.set(actualQuantity);
        this.unitPrice.set(unitPrice);
        this.totalPrice.set(totalPrice);
    }

    // Property methods
    public LongProperty idProperty() { return id; }
    public LongProperty importReceiptIdProperty() { return importReceiptId; }
    public LongProperty productIdProperty() { return productId; }
    public IntegerProperty plannedQuantityProperty() { return plannedQuantity; }
    public IntegerProperty actualQuantityProperty() { return actualQuantity; }
    public DoubleProperty unitPriceProperty() { return unitPrice; }
    public DoubleProperty totalPriceProperty() { return totalPrice; }

    // Getter methods
    public long getId() { return id.get(); }
    public long getImportReceiptId() { return importReceiptId.get(); }
    public long getProductId() { return productId.get(); }
    public int getPlannedQuantity() { return plannedQuantity.get(); }
    public int getActualQuantity() { return actualQuantity.get(); }
    public double getUnitPrice() { return unitPrice.get(); }
    public double getTotalPrice() { return totalPrice.get(); }
}
