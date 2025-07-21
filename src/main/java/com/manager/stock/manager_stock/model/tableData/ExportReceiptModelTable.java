package com.manager.stock.manager_stock.model.tableData;

import javafx.beans.property.*;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptModelTable {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty invoiceNumber = new SimpleStringProperty();
    private final StringProperty createAt = new SimpleStringProperty();
    private final StringProperty receiver = new SimpleStringProperty();
    private final StringProperty receiveAddress = new SimpleStringProperty();
    private final StringProperty reason = new SimpleStringProperty();
    private final StringProperty wareHouse = new SimpleStringProperty();
    private final DoubleProperty totalPrice = new SimpleDoubleProperty();
    private final StringProperty totalPriceInWord = new SimpleStringProperty();
    private final StringProperty totalPriceFormat = new SimpleStringProperty();

    public ExportReceiptModelTable(long id, String invoiceNumber, String createAt, String receiver, String receiveAddress,
                                   String reason, String wareHouse, Double totalPrice, String totalPriceInWord, String totalPriceFormat) {

        this.id.set(id);
        this.invoiceNumber.set(invoiceNumber);
        this.createAt.set(createAt);
        this.receiver.set(receiver);
        this.receiveAddress.set(receiveAddress);
        this.reason.set(reason);
        this.wareHouse.set(wareHouse);
        this.totalPrice.set(totalPrice);
        this.totalPriceInWord.set(totalPriceInWord);
        this.totalPriceFormat.set(totalPriceFormat);
    }

    public LongProperty idProperty() {return this.id;}
    public StringProperty invoiceNumberProperty() {return this.invoiceNumber;}
    public StringProperty createAtProperty() {return this.createAt;}
    public StringProperty receiverProperty() {return this.receiver;}
    public StringProperty receiveAddressProperty() {return this.receiveAddress;}
    public StringProperty reasonProperty() {return this.reason;}
    public StringProperty wareHouseProperty() {return this.wareHouse;}
    public DoubleProperty totalPriceProperty() {return this.totalPrice;}
    public StringProperty totalPriceInWordProperty() {return this.totalPriceInWord;}
    public StringProperty totalPriceFormatProperty() {return this.totalPriceFormat;}

    public long getId() {return this.id.get();}
    public String getInvoiceNumber() {return this.invoiceNumber.get();}
    public String getCreateAt() {return this.createAt.get();}
    public String getReceiver() {return this.receiver.get();}
    public String getReceiveAddress() {return this.receiveAddress.get();}
    public String getReason() {return this.reason.get();}
    public String getWareHouse() {return this.wareHouse.get();}
    public Double getTotalPrice() {return this.totalPrice.get();}
    public String getTotalPriceInWord() {return this.totalPriceInWord.get();}

}
