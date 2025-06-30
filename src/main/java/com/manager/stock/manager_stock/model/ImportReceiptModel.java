package com.manager.stock.manager_stock.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptModel {
    private Long id;
    private String invoiceNumber;
    private String createAt;
    private String deliveredBy;
    private String invoice;
    private String companyName;
    private String warehouseName;
    private double totalPrice;
    private String totalPriceInWord;
    private List<ImportReceiptDetailModel> importReceiptDetails;

    public ImportReceiptModel(Long id, String invoiceNumber, String createAt, String deliveredBy, String invoice, String companyName, String warehouseName, double totalPrice, String totalPriceInWord) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.createAt = createAt;
        this.deliveredBy = deliveredBy;
        this.invoice = invoice;
        this.companyName = companyName;
        this.warehouseName = warehouseName;
        this.totalPrice = totalPrice;
        this.totalPriceInWord = totalPriceInWord;
        this.importReceiptDetails = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getDeliveredBy() {
        return deliveredBy;
    }

    public void setDeliveredBy(String deliveredBy) {
        this.deliveredBy = deliveredBy;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalPriceInWord() {
        return totalPriceInWord;
    }

    public void setTotalPriceInWord(String totalPriceInWord) {
        this.totalPriceInWord = totalPriceInWord;
    }

    public List<ImportReceiptDetailModel> getImportReceiptDetails() {
        return importReceiptDetails;
    }

    public void setImportReceiptDetails(List<ImportReceiptDetailModel> importReceiptDetails) {
        this.importReceiptDetails = importReceiptDetails;
    }

    @Override
    public String toString() {
        return "ImportReceiptModel{" +
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
