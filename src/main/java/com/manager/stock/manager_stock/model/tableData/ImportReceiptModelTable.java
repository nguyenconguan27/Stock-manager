package com.manager.stock.manager_stock.model.tableData;

import com.manager.stock.manager_stock.model.ImportReceiptModel;
import javafx.beans.property.*;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptModelTable {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty invoiceNumber = new SimpleStringProperty();
    private final StringProperty createAt = new SimpleStringProperty();
    private final StringProperty deliveredBy = new SimpleStringProperty();
    private final StringProperty invoice = new SimpleStringProperty();
    private final StringProperty companyName = new SimpleStringProperty();
    private final StringProperty warehouseName = new SimpleStringProperty();
    private final DoubleProperty totalPrice = new SimpleDoubleProperty();
    private final StringProperty totalPriceInWord = new SimpleStringProperty();

    public ImportReceiptModelTable(Long id, String invoiceNumber, String createAt, String deliveredBy,
                                   String invoice, String companyName, String warehouseName,
                                   double totalPrice, String totalPriceInWord) {
        this.id.set(id);
        this.invoiceNumber.set(invoiceNumber);
        this.createAt.set(createAt);
        this.deliveredBy.set(deliveredBy);
        this.invoice.set(invoice);
        this.companyName.set(companyName);
        this.warehouseName.set(warehouseName);
        this.totalPrice.set(totalPrice);
        this.totalPriceInWord.set(totalPriceInWord);

    }

    // Getters for properties
    public LongProperty idProperty() { return id; }
    public StringProperty invoiceNumberProperty() { return invoiceNumber; }
    public StringProperty createAtProperty() { return createAt; }
    public StringProperty deliveredByProperty() { return deliveredBy; }
    public StringProperty invoiceProperty() { return invoice; }
    public StringProperty companyNameProperty() { return companyName; }
    public StringProperty warehouseNameProperty() { return warehouseName; }
    public DoubleProperty totalPriceProperty() { return totalPrice; }
    public StringProperty totalPriceInWordProperty() { return totalPriceInWord; }

    // Getters for values (optional if needed)
    public Long getId() { return id.get(); }
    public String getInvoiceNumber() { return invoiceNumber.get(); }
    public String getCreateAt() { return createAt.get(); }
    public String getDeliveredBy() { return deliveredBy.get(); }
    public String getInvoice() { return invoice.get(); }
    public String getCompanyName() { return companyName.get(); }
    public String getWarehouseName() { return warehouseName.get(); }
    public double getTotalPrice() { return totalPrice.get(); }
    public String getTotalPriceInWord() { return totalPriceInWord.get(); }

    @Override
    public String toString() {
        return "ImportReceiptModelTable{" +
                "id=" + id +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", createAt='" + createAt + '\'' +
                ", deliveredBy='" + deliveredBy + '\'' +
                ", invoice='" + invoice + '\'' +
                ", companyName='" + companyName + '\'' +
                ", warehouseName='" + warehouseName + '\'' +
                ", totalPrice=" + totalPrice +
                ", totalPriceInWord='" + totalPriceInWord + '\'' +
                '}';
    }
}
