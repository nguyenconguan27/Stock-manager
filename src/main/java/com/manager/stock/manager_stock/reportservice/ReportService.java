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

//    public ReportService() {
//    }

//    public List<ImportReceiptModel> getImportByYear(int year) {
//        return importReceiptService.findAllByAcademicYear(Optional.of(year));
//    }
//
//    public List<ExportReceiptModel> getExportByYear(int year) {
//        return exportReceiptService.findAllByAcademicYear(year);
//    }

//    public List<ReportModel> getData() {
//
//    }

//    public List<ReportModel> getReportData() {
//        List<ReportModel> reports = Arrays.asList(
//                new ReportModel(
//                        1L, "Group A",
//                        Arrays.asList(
//                                new ReportModel.ReportProduct(
//                                        101L, "Product A1", "pcs",
//                                        new ReportModel.ReportDetail("" +"startsem", 10, 100, 1000),
//                                        new ReportModel.ReportDetail("" +"totalimport", 20, 110, 2200),
//                                        new ReportModel.ReportDetail("" +"totalexport", 15, 120, 1800),
//                                        new ReportModel.ReportDetail("" +"endsem", 25, 130, 3250),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +1 + "", 5, 50, 250),
//                                                new ReportModel.ReportDetail("" +2 + "", 6, 60, 360),
//                                                new ReportModel.ReportDetail("" +3 + "", 7, 70, 490)
//                                        ),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +6+ "", 3, 40, 120),
//                                                new ReportModel.ReportDetail("" +7+ "", 4, 45, 180),
//                                                new ReportModel.ReportDetail("" +8+ "", 5, 50, 250)
//                                        )
//                                ),
//                                new ReportModel.ReportProduct(
//                                        102L, "Product A2", "box",
//                                        new ReportModel.ReportDetail("startsem", 12, 90, 1080),
//                                        new ReportModel.ReportDetail("totalimport", 22, 100, 2200),
//                                        new ReportModel.ReportDetail("totalexport", 17, 105, 1785),
//                                        new ReportModel.ReportDetail("endsem", 27, 125, 3375),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +4+ "", 6, 55, 330),
//                                                new ReportModel.ReportDetail("" +5+ "", 7, 65, 455),
//                                                new ReportModel.ReportDetail("" +1+ "", 8, 75, 600)
//                                        ),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +6, 4, 42, 168),
//                                                new ReportModel.ReportDetail("" +8, 5, 48, 240),
//                                                new ReportModel.ReportDetail("" +9, 6, 52, 312)
//                                        )
//                                ),
//                                new ReportModel.ReportProduct(
//                                        103L, "Product A3", "kg",
//                                        new ReportModel.ReportDetail("startsem", 12, 90, 1080),
//                                        new ReportModel.ReportDetail("totalimport", 22, 100, 2200),
//                                        new ReportModel.ReportDetail("totalexport", 17, 105, 1785),
//                                        new ReportModel.ReportDetail("endsem", 27, 125, 3375),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +1, 7, 58, 406),
//                                                new ReportModel.ReportDetail("" +4, 8, 68, 544),
//                                                new ReportModel.ReportDetail("" +5, 9, 78, 702)
//                                        ),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +7, 5, 44, 220),
//                                                new ReportModel.ReportDetail("" +8, 6, 46, 276),
//                                                new ReportModel.ReportDetail("" +9, 7, 54, 378)
//                                        )
//                                )
//                        )
//                ),
//
//                new ReportModel(
//                        2L, "Group B",
//                        Arrays.asList(
//                                new ReportModel.ReportProduct(
//                                        201L, "Product B1", "pcs",
//                                        new ReportModel.ReportDetail("startsem", 12, 90, 1080),
//                                        new ReportModel.ReportDetail("totalimport", 22, 100, 2200),
//                                        new ReportModel.ReportDetail("totalexport", 17, 105, 1785),
//                                        new ReportModel.ReportDetail("endsem", 27, 125, 3375),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +1, 3, 40, 120),
//                                                new ReportModel.ReportDetail("" +4, 4, 45, 180),
//                                                new ReportModel.ReportDetail("" +2, 5, 50, 250)
//                                        ),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +8, 2, 30, 60),
//                                                new ReportModel.ReportDetail("" +9, 3, 35, 105),
//                                                new ReportModel.ReportDetail("" +10, 4, 38, 152)
//                                        )
//                                ),
//                                new ReportModel.ReportProduct(
//                                        202L, "Product B2", "kg",
//                                        new ReportModel.ReportDetail("startsem", 12, 90, 1080),
//                                        new ReportModel.ReportDetail("totalimport", 22, 100, 2200),
//                                        new ReportModel.ReportDetail("totalexport", 17, 105, 1785),
//                                        new ReportModel.ReportDetail("endsem", 27, 125, 3375),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +1, 6, 60, 360),
//                                                new ReportModel.ReportDetail("" +2, 7, 65, 455),
//                                                new ReportModel.ReportDetail("" +3, 8, 70, 560)
//                                        ),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +6, 4, 45, 180),
//                                                new ReportModel.ReportDetail("" +7, 5, 50, 250),
//                                                new ReportModel.ReportDetail("" +10, 6, 55, 330)
//                                        )
//                                ),
//                                new ReportModel.ReportProduct(
//                                        203L, "Product B3", "box",
//                                        new ReportModel.ReportDetail("startsem", 12, 90, 1080),
//                                        new ReportModel.ReportDetail("totalimport", 22, 100, 2200),
//                                        new ReportModel.ReportDetail("totalexport", 17, 105, 1785),
//                                        new ReportModel.ReportDetail("endsem", 27, 125, 3375),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +1, 6, 62, 372),
//                                                new ReportModel.ReportDetail("" +2, 7, 72, 504),
//                                                new ReportModel.ReportDetail("" +4, 8, 82, 656)
//                                        ),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +7, 5, 47, 235),
//                                                new ReportModel.ReportDetail("" +8, 6, 52, 312),
//                                                new ReportModel.ReportDetail("" +10, 7, 57, 399)
//                                        )
//                                )
//                        )
//                ),
//
//                new ReportModel(
//                        3L, "Group C",
//                        Arrays.asList(
//                                new ReportModel.ReportProduct(
//                                        301L, "Product C1", "pcs",
//                                        new ReportModel.ReportDetail("startsem", 12, 90, 1080),
//                                        new ReportModel.ReportDetail("totalimport", 22, 100, 2200),
//                                        new ReportModel.ReportDetail("totalexport", 17, 105, 1785),
//                                        new ReportModel.ReportDetail("endsem", 27, 125, 3375),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +1, 7, 65, 455),
//                                                new ReportModel.ReportDetail("" +4, 8, 75, 600),
//                                                new ReportModel.ReportDetail("" +5, 9, 85, 765)
//                                        ),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +6, 6, 55, 330),
//                                                new ReportModel.ReportDetail("" +7, 7, 60, 420),
//                                                new ReportModel.ReportDetail("" +8, 8, 65, 520)
//                                        )
//                                ),
//                                new ReportModel.ReportProduct(
//                                        302L, "Product C2", "kg",
//                                        new ReportModel.ReportDetail("startsem", 12, 90, 1080),
//                                        new ReportModel.ReportDetail("totalimport", 22, 100, 2200),
//                                        new ReportModel.ReportDetail("totalexport", 17, 105, 1785),
//                                        new ReportModel.ReportDetail("endsem", 27, 125, 3375),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +1, 9, 70, 630),
//                                                new ReportModel.ReportDetail("" +2, 10, 80, 800),
//                                                new ReportModel.ReportDetail("" +3, 11, 90, 990)
//                                        ),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +8, 7, 62, 434),
//                                                new ReportModel.ReportDetail("" +9, 8, 68, 544),
//                                                new ReportModel.ReportDetail("" +10, 9, 75, 675)
//                                        )
//                                ),
//                                new ReportModel.ReportProduct(
//                                        303L, "Product C3", "box",
//                                        new ReportModel.ReportDetail("startsem", 12, 90, 1080),
//                                        new ReportModel.ReportDetail("totalimport", 22, 100, 2200),
//                                        new ReportModel.ReportDetail("totalexport", 17, 105, 1785),
//                                        new ReportModel.ReportDetail("endsem", 27, 125, 3375),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +1, 10, 85, 850),
//                                                new ReportModel.ReportDetail("" +2, 11, 95, 1045),
//                                                new ReportModel.ReportDetail("" +4, 12, 105, 1260)
//                                        ),
//                                        Arrays.asList(
//                                                new ReportModel.ReportDetail("" +6, 9, 70, 630),
//                                                new ReportModel.ReportDetail("" +7, 10, 80, 800),
//                                                new ReportModel.ReportDetail("" +9, 11, 90, 990)
//                                        )
//                                )
//                        )
//                )
//        );
//
//        return reports;
//    }
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
                            totalExportP += (int )detailModel.getUnitPrice();
                            ReportModel.ReportDetail reportDetail = new ReportModel.ReportDetail();
                            reportDetail.setId("e" + detailModel.getExportReceiptId());
                            reportDetail.setQuantity(detailModel.getActualQuantity());
                            reportDetail.setUnit_price((int) detailModel.getUnitPrice());
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
