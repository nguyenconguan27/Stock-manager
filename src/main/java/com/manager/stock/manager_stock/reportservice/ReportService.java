package com.manager.stock.manager_stock.reportservice;

import com.manager.stock.manager_stock.dao.impl.ImportReceiptDetailDaoImpl;
import com.manager.stock.manager_stock.model.*;
import com.manager.stock.manager_stock.service.*;
import com.manager.stock.manager_stock.service.impl.*;

import java.util.*;

public class ReportService {
    final ProductGroupService productGroupService;
    final IImportReceiptService importReceiptService;
    final IExportReceiptService exportReceiptService;
    final ProductService productService;
    final IExportReceiptDetailService exportReceiptDetailService;
    final IImportReceiptDetailService importReceiptDetailService;
    final IInventoryDetailService inventoryDetailService;

    public ReportService() {
        this.productGroupService = ProductGroupServiceImpl.getInstance();
        this.importReceiptService = ImportReceiptServiceImpl.getInstance();
        this.exportReceiptService = ExportReceiptServiceImpl.getInstance();
        this.productService = ProductServiceImpl.getInstance();
        this.exportReceiptDetailService = ExportReceiptDetailServiceImpl.getInstance();
        this.importReceiptDetailService = ImportReceiptDetailServiceImpl.getInstance();
        this.inventoryDetailService = InventoryDetailServiceImpl.getInstance();
    }

    public List<ImportReceiptModel> getImport(int year) {
        List<ImportReceiptModel> receipts = importReceiptService.findAllByAcademicYear(Optional.of(year));
        return receipts;
    }

    public List<ExportReceiptModel> getExport(int year) {
        List<ExportReceiptModel> exports = exportReceiptService.findAllByAcademicYear(year);
        return exports;
    }

    public List<ReportModel> getData(int year) {
        List<ProductGroup> groupList = productGroupService.getAll();
        List<ProductModel> productList = productService.getAllProducts();
        List<ImportReceiptModel> importList = importReceiptService.findAllByAcademicYear(Optional.of(year));
        List<ExportReceiptModel> exportList = exportReceiptService.findAllByAcademicYear(year);
        List<ImportReceiptDetailModel> importReceiptDetailList = new ArrayList<>();
        List<ExportReceiptDetailModel> exportReceiptDetailList = new ArrayList<>();
        for(ImportReceiptModel importReceipt: importList) {
            importReceiptDetailList.addAll(importReceiptDetailService.findAllByImportReceiptId(importReceipt.getId()));
        }
        for(ExportReceiptModel exportReceipt: exportList) {
            exportReceiptDetailList.addAll(exportReceiptDetailService.findAllByExportReceipt(exportReceipt.getId()));
        }
        List<ReportModel> reportModelList = new ArrayList<>();
        for(ProductGroup group: groupList) {
            ReportModel reportModel = new ReportModel();
            reportModel.setGroupId(group.getId());
            reportModel.setGroupName(group.getName());
            List<ReportModel.ReportProduct> reportProductList = new ArrayList<>();
            for(ProductModel productModel: productList) {
                InventoryDetailModel sInventoryDetail = inventoryDetailService.findAllByAcademicYearAndProductId(year - 1, Arrays.asList(productModel.getId())).get(productModel.getId());
                InventoryDetailModel eInventoryDetail = inventoryDetailService.findAllByAcademicYearAndProductId(year, Arrays.asList(productModel.getId())).get(productModel.getId());
                ReportModel.ReportDetail startSem = new ReportModel.ReportDetail("startsem", sInventoryDetail.getQuantity(), (int)(sInventoryDetail.getTotalPrice() / (sInventoryDetail.getQuantity() == 0 ? 1 : sInventoryDetail.getQuantity())), (int) (sInventoryDetail.getTotalPrice() / 1));
                ReportModel.ReportDetail endSem = new ReportModel.ReportDetail("endsem", eInventoryDetail.getQuantity(), (int)(eInventoryDetail.getTotalPrice() / (eInventoryDetail.getQuantity() == 0 ? 1 : eInventoryDetail.getQuantity())), (int) (eInventoryDetail.getTotalPrice() / 1));
                int totalImportQ = 0; int totalImportP = 0;
                int totalExportQ = 0; int totalExportP = 0;
                if(productModel.getGroupId() == group.getId()) {
                    ReportModel.ReportProduct reportProduct = new ReportModel.ReportProduct();
                    reportProduct.setId(productModel.getId());
                    reportProduct.setName(productModel.getName());
                    reportProduct.setUnit(productModel.getUnit());
                    reportProduct.setCode(productModel.getCode());
                    List<ReportModel.ReportDetail> importDetails = new ArrayList<>();
                    List<ReportModel.ReportDetail> exportDetails = new ArrayList<>();
                    for(ImportReceiptDetailModel detailModel: importReceiptDetailList) {
                        if(detailModel.getProductId() == productModel.getId()) {
                            totalImportQ += detailModel.getActualQuantity();
                            totalImportP += (int) detailModel.getUnitPrice();
                            ReportModel.ReportDetail reportDetail = new ReportModel.ReportDetail();
                            reportDetail.setId("i" + detailModel.getImportReceiptId());
                            reportDetail.setQuantity(detailModel.getActualQuantity());
                            reportDetail.setUnit_price((int) detailModel.getUnitPrice());
                            reportDetail.setTotal((int) detailModel.getTotalPrice());
                            importDetails.add(reportDetail);
                        }
                    }
                    for(ExportReceiptDetailModel detailModel: exportReceiptDetailList) {
                        if(detailModel.getProductId() == productModel.getId()) {
                            totalExportQ += detailModel.getActualQuantity();
                            totalExportP += (int )detailModel.getDisplayUnitPrice();
                            ReportModel.ReportDetail reportDetail = new ReportModel.ReportDetail();
                            reportDetail.setId("e" + detailModel.getExportReceiptId());
                            reportDetail.setQuantity(detailModel.getActualQuantity());
                            reportDetail.setUnit_price((int) detailModel.getDisplayUnitPrice());
                            reportDetail.setTotal((int) detailModel.getTotalPrice());
                            exportDetails.add(reportDetail);
                        }
                    }
                    int importCount = importDetails.size(); int exportCount = exportDetails.size();
                    ReportModel.ReportDetail totalImport = new ReportModel.ReportDetail("totalimport", totalImportQ, totalImportP / (importCount == 0 ? 1 : importCount), totalImportQ * ( totalImportP / (importCount == 0 ? 1 : importCount)));
                    ReportModel.ReportDetail totalExport = new ReportModel.ReportDetail("totalexport", totalExportQ, totalExportP / (exportCount == 0 ? 1 : exportCount), totalExportQ * (totalExportP / (exportCount == 0 ? 1 : exportCount)));
                    reportProduct.setStartSem(startSem);
                    reportProduct.setEndSem(endSem);
                    reportProduct.setTotalImport(totalImport);
                    reportProduct.setTotalExport(totalExport);
                    reportProduct.setImportDetail(importDetails);
                    reportProduct.setExportDetail(exportDetails);
                    reportProductList.add(reportProduct);
                }
            }
            reportModel.setReportProducts(reportProductList);
            reportModelList.add(reportModel);
        }
        return reportModelList;
    }
}
