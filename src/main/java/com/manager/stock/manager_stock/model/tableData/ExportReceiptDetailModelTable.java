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
    private final StringProperty displayUnitPriceFormat = new SimpleStringProperty();
    private final StringProperty displayTotalPriceFormat = new SimpleStringProperty();
    private final LongProperty exportPriceId = new SimpleLongProperty();
    private final DoubleProperty displayUnitPrice = new SimpleDoubleProperty();
    private final DoubleProperty originalUnitPrice = new SimpleDoubleProperty();

    public ExportReceiptDetailModelTable(long id, long exportReceiptId, long productId, int plannedQuantity, int actualQuantity, double totalPrice, double displayUnitPrice,
                                         String productName, String displayUnitPriceFormat, String displayTotalPriceFormat, String productCoe, long exportPriceId, double originalUnitPrice) {
        this.id.set(id);
        this.exportReceiptId.set(exportReceiptId);
        this.productId.set(productId);
        this.plannedQuantity.set(plannedQuantity);
        this.actualQuantity.set(actualQuantity);
        this.totalPrice.set(totalPrice);
        this.displayUnitPrice.set(displayUnitPrice);
        this.productName.set(productName);
        this.displayUnitPriceFormat.set(displayUnitPriceFormat);
        this.displayTotalPriceFormat.set(displayTotalPriceFormat);
        this.productCode.set(productCoe);
        this.exportPriceId.set(exportPriceId);
        this.originalUnitPrice.set(originalUnitPrice);
    }

    public LongProperty idProperty() {return this.id;}
    public LongProperty exportReceiptIdProperty() {return this.exportReceiptId;}
    public LongProperty productIdProperty() {return this.productId;}
    public IntegerProperty plannedQuantityProperty() {return this.plannedQuantity;}
    public IntegerProperty actualQuantityProperty() {return this.actualQuantity;}
    public DoubleProperty totalPriceProperty() {return this.totalPrice;}
    public DoubleProperty unitPriceProperty() {return this.displayUnitPrice;}
    public StringProperty productNameProperty() {return this.productName;}
    public StringProperty productCodeProperty() {return this.productCode;}
    public StringProperty productCoeProperty() {return this.productCode;}
    public StringProperty displayUnitPriceFormatProperty() {return this.displayUnitPriceFormat;}
    public StringProperty displayTotalPriceFormatProperty() {return this.displayTotalPriceFormat;}

    public Long getId() {return this.id.get();}
    public long getExportReceiptId() {return this.exportReceiptId.get();}
    public Long getProductId() {return this.productId.get();}
    public int getPlannedQuantity() {return this.plannedQuantity.get();}
    public int getActualQuantity() {return this.actualQuantity.get();}
    public double getTotalPrice() {return this.totalPrice.get();}
    public String getProductName() {return this.productName.get();}
    public String getProductCode() {return this.productCode.get();}
    public long getExportPriceId() {return this.exportPriceId.get();}
    public double getDisplayUnitPrice() {return this.displayUnitPrice.get();}
    public double getOriginalUnitPrice() {return this.originalUnitPrice.get();}

    public void setActualQuantity(int actualQuantity) {this.actualQuantity.set(actualQuantity);}
    public void setTotalPrice(double totalPrice) {this.totalPrice.set(totalPrice);}
    public void setPlannedQuantity(int plannedQuantity) {this.plannedQuantity.set(plannedQuantity);}
    public void setDisplayTotalPriceFormat(String displayTotalPriceFormat) {this.displayTotalPriceFormat.set(displayTotalPriceFormat);}
}
