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
}
