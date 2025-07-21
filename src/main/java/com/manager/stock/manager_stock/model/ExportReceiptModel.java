package com.manager.stock.manager_stock.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptModel {
    private long id;
    private String invoiceNumber;
    private String createAt;
    private String receiver;
    private String receiveAddress;
    private String reason;
    private String wareHouse;
    private Double totalPrice;
    private String totalPriceInWord;
    private int academicYear;
    private List<ExportReceiptDetailModel> exportReceiptDetailModels;

    public ExportReceiptModel() {
        this.exportReceiptDetailModels = new ArrayList<>();
    }

    public ExportReceiptModel(Long id, String invoiceNumber, String createAt, String receiver, String receiveAddress, String reason, String wareHouse, Double totalPrice, String totalPriceInWord) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.createAt = createAt;
        this.receiver = receiver;
        this.receiveAddress = receiveAddress;
        this.reason = reason;
        this.wareHouse = wareHouse;
        this.totalPrice = totalPrice;
        this.totalPriceInWord = totalPriceInWord;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getWareHouse() {
        return wareHouse;
    }

    public void setWareHouse(String wareHouse) {
        this.wareHouse = wareHouse;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalPriceInWord() {
        return totalPriceInWord;
    }

    public void setTotalPriceInWord(String totalPriceInWord) {
        this.totalPriceInWord = totalPriceInWord;
    }

    public List<ExportReceiptDetailModel> getExportReceiptDetailModels() {
        return exportReceiptDetailModels;
    }

    public void setExportReceiptDetailModels(List<ExportReceiptDetailModel> exportReceiptDetailModels) {
        this.exportReceiptDetailModels = exportReceiptDetailModels;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        this.academicYear = academicYear;
    }

    @Override
    public String toString() {
        return "ReceiptModel{" +
                "id=" + id +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", createAt='" + createAt + '\'' +
                ", receiver='" + receiver + '\'' +
                ", receiveAddress='" + receiveAddress + '\'' +
                ", reason='" + reason + '\'' +
                ", wareHouse='" + wareHouse + '\'' +
                ", totalPrice=" + totalPrice +
                ", totalPriceInWord='" + totalPriceInWord + '\'' +
                ", academicYear=" + academicYear +
                '}';
    }

}
