package com.manager.stock.manager_stock.model.tableData;

import javafx.beans.property.*;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptDetailModelTable {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty productName = new SimpleStringProperty();
    private final StringProperty productCode = new SimpleStringProperty();
    private final LongProperty exportReceiptId = new SimpleLongProperty();
    private final LongProperty productId = new SimpleLongProperty();
    private final IntegerProperty plannedQuantity = new SimpleIntegerProperty();
    private final IntegerProperty actualQuantity = new SimpleIntegerProperty();
    private final DoubleProperty totalPrice = new SimpleDoubleProperty();
    private final DoubleProperty unitPrice = new SimpleDoubleProperty();
    private final StringProperty unitPriceFormat = new SimpleStringProperty();
    private final StringProperty totalPriceFormat = new SimpleStringProperty();

    public ExportReceiptDetailModelTable(long id, long exportReceiptId, long productId, int plannedQuantity, int actualQuantity, Double totalPrice, Double unitPrice,
                                         String productName, String unitPriceFormat, String totalPriceFormat, String productCoe) {
        this.id.set(id);
        this.exportReceiptId.set(exportReceiptId);
        this.productId.set(productId);
        this.plannedQuantity.set(plannedQuantity);
        this.actualQuantity.set(actualQuantity);
        this.totalPrice.set(totalPrice);
        this.unitPrice.set(unitPrice);
        this.productName.set(productName);
        this.unitPriceFormat.set(unitPriceFormat);
        this.totalPriceFormat.set(totalPriceFormat);
        this.productCode.set(productCoe);
    }

    public LongProperty idProperty() {return this.id;}
    public LongProperty exportReceiptIdProperty() {return this.exportReceiptId;}
    public LongProperty productIdProperty() {return this.productId;}
    public IntegerProperty plannedQuantityProperty() {return this.plannedQuantity;}
    public IntegerProperty actualQuantityProperty() {return this.actualQuantity;}
    public DoubleProperty totalPriceProperty() {return this.totalPrice;}
    public DoubleProperty unitPriceProperty() {return this.unitPrice;}
    public StringProperty productNameProperty() {return this.productName;}
    public StringProperty productCodeProperty() {return this.productCode;}
    public StringProperty unitPriceFormatProperty() {return this.unitPriceFormat;}
    public StringProperty totalPriceFormatProperty() {return this.totalPriceFormat;}
    public StringProperty productCoeProperty() {return this.productCode;}

    public long getId() {return this.id.get();}
    public long getExportReceiptId() {return this.exportReceiptId.get();}
    public long getProductId() {return this.productId.get();}
    public int getPlannedQuantity() {return this.plannedQuantity.get();}
    public int getActualQuantity() {return this.actualQuantity.get();}
    public double getTotalPrice() {return this.totalPrice.get();}
    public double getUnitPrice() {return this.unitPrice.get();}
    public String getProductName() {return this.productName.get();}
    public String getProductCode() {return this.productCode.get();}
}
