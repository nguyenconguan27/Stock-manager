package com.manager.stock.manager_stock.reportservice;

import java.util.ArrayList;
import java.util.List;

public class ReportModel {
    long groupId;
    String groupName;
    List<ReportProduct> reportProducts = new ArrayList<>();

    public ReportModel(long groupId, String groupName, List<ReportProduct> reportProducts) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.reportProducts = reportProducts;
    }

    public ReportModel() {
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setReportProducts(List<ReportProduct> reportProducts) {
        this.reportProducts = reportProducts;
    }

    public long getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<ReportProduct> getReportProducts() {
        return reportProducts;
    }

    public static class ReportProduct {
        long id;
        String name;
        String unit;
        String code;
        ReportDetail startSem;
        ReportDetail totalImport;
        ReportDetail totalExport;
        ReportDetail endSem;
        List<ReportDetail> importDetail = new ArrayList<>();;
        List<ReportDetail> exportDetail = new ArrayList<>();;

        public ReportProduct(long id, String name, String unit, String code, ReportDetail startSem, ReportDetail totalImport, ReportDetail totalExport, ReportDetail endSem, List<ReportDetail> importDetail, List<ReportDetail> exportDetail) {
            this.id = id;
            this.name = name;
            this.unit = unit;
            this.startSem = startSem;
            this.totalImport = totalImport;
            this.totalExport = totalExport;
            this.endSem = endSem;
            this.importDetail = importDetail;
            this.exportDetail = exportDetail;
            this.code = code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public ReportProduct() {
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public void setStartSem(ReportDetail startSem) {
            this.startSem = startSem;
        }

        public void setTotalImport(ReportDetail totalImport) {
            this.totalImport = totalImport;
        }

        public void setTotalExport(ReportDetail totalExport) {
            this.totalExport = totalExport;
        }

        public void setEndSem(ReportDetail endSem) {
            this.endSem = endSem;
        }

        public void setImportDetail(List<ReportDetail> importDetail) {
            this.importDetail = importDetail;
        }

        public void setExportDetail(List<ReportDetail> exportDetail) {
            this.exportDetail = exportDetail;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getUnit() {
            return unit;
        }

        public ReportDetail getStartSem() {
            return startSem;
        }

        public ReportDetail getTotalImport() {
            return totalImport;
        }

        public ReportDetail getTotalExport() {
            return totalExport;
        }

        public ReportDetail getEndSem() {
            return endSem;
        }

        public List<ReportDetail> getImportDetail() {
            return importDetail;
        }

        public List<ReportDetail> getExportDetail() {
            return exportDetail;
        }
    }

    public static class ReportDetail {
        String idReceipt;
        int quantity;
        int unit_price;
        int total;

        public ReportDetail(String idReceipt, int quantity, int unit_price, int total) {
            this.idReceipt = idReceipt;
            this.quantity = quantity;
            this.unit_price = unit_price;
            this.total = total;
        }

        public ReportDetail() {
        }

        public void setId(String idReceipt) {
            this.idReceipt = idReceipt;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public void setUnit_price(int unit_price) {
            this.unit_price = unit_price;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public String getId() {
            return idReceipt;
        }

        public int getQuantity() {
            return quantity;
        }

        public int getUnit_price() {
            return unit_price;
        }

        public int getTotal() {
            return total;
        }
    }
}
