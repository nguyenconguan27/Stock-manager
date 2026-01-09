package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.impl.DatasourceInitialize;
import com.manager.stock.manager_stock.exception.DivisionByZeroException;
import com.manager.stock.manager_stock.exception.StockUnderFlowException;
import com.manager.stock.manager_stock.model.*;
import com.manager.stock.manager_stock.service.*;
import com.manager.stock.manager_stock.utils.AlertUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ComputeServiceImpl implements ComputService{

    private IImportReceiptService importReceiptService;
    private IExportReceiptService exportReceiptService;
    private IInventoryDetailService inventoryDetailService;
    private IImportReceiptDetailService importReceiptDetailService;
    private IExportReceiptDetailService exportReceiptDetailService;
    private IExportPriceService exportPriceService;
    private ComputeServiceImpl instance;
    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public ComputeServiceImpl() {
        importReceiptService = ImportReceiptServiceImpl.getInstance();
        exportReceiptService = ExportReceiptServiceImpl.getInstance();
        inventoryDetailService = InventoryDetailServiceImpl.getInstance();
        importReceiptDetailService = ImportReceiptDetailServiceImpl.getInstance();
        exportReceiptDetailService = ExportReceiptDetailServiceImpl.getInstance();
        exportPriceService = ExportPriceServiceImpl.getInstance();
    }

    public ComputeServiceImpl getInstance() {
        if (this.instance == null) {
            instance = new ComputeServiceImpl();
        }
        return instance;
    }

    public void compute(ReceiptModel receipt, List<? extends DetailModel> details) throws SQLException {
        int year;
        List<ImportReceiptModel> importReceipts;
        List<ExportReceiptModel> exportReceipts;
        LocalDateTime dateTime;
        dateTime = LocalDateTime.parse(receipt.getCreateAt(), formatter);
        year = dateTime.getYear();
        try {
            for (DetailModel detailModel : details) {
                importReceipts = importReceiptService.findAllByProductAndYear(detailModel.getProductId(), year);
                exportReceipts = exportReceiptService.findAllByProductAndYear(detailModel.getProductId(), year);
                for (ImportReceiptModel importReceipt : importReceipts) {
                    importReceipt.setImportReceiptDetails(importReceiptDetailService.findAllByImportReceiptId(importReceipt.getId()));
                }
                for (ExportReceiptModel exportReceipt : exportReceipts) {
                    exportReceipt.setExportReceiptDetailModels(exportReceiptDetailService.findAllByExportReceipt(exportReceipt.getId()));
                }
                if (receipt instanceof ImportReceiptModel) {
                    int i = updateList(receipt, importReceipts, dateTime);
                    ImportReceiptModel importReceipt = (ImportReceiptModel) receipt;
                    importReceipt.setImportReceiptDetails(details.stream()
                            .map(d -> (ImportReceiptDetailModel) d)
                            .toList());
                    importReceipts.add(i, importReceipt);
                } else {
                    int i = updateList(receipt, exportReceipts, dateTime);
                    ExportReceiptModel exportReceipt = (ExportReceiptModel) receipt;
                    exportReceipt.setExportReceiptDetailModels(details.stream()
                            .map(d -> (ExportReceiptDetailModel) d)
                            .toList());
                    exportReceipts.add(i, exportReceipt);
                }
                computeForProduct(detailModel.getProductId(), year, importReceipts, exportReceipts, dateTime);
            }
            DatasourceInitialize.INSTANCE.commit();
        } catch (StockUnderFlowException e) {
            AlertUtils.alert("", "", "", "");
            DatasourceInitialize.INSTANCE.rollback();
        } catch (Exception e) {
            DatasourceInitialize.INSTANCE.rollback();
        }



    }

    public int updateList(ReceiptModel receipt, List<? extends ReceiptModel> temp, LocalDateTime dateTime) {
        temp.remove(receipt);
        for (int i = 0; i < temp.size(); i++) {
            LocalDateTime ts = LocalDateTime.parse(temp.get(i).getCreateAt(), formatter);
            if (dateTime.isBefore(ts)) {
                return i;
            }
        }
        return temp.size();
    }

    public void computeForProduct(long productId, int year, List<ImportReceiptModel> imports, List<ExportReceiptModel> exports, LocalDateTime time) {
        int exportPrice = 0;
        int i = 0, j = 0;
        InventoryDetailModel current = inventoryDetailService.findByYearAndProduct(productId, year);
        InventoryDetailModel pre = inventoryDetailService.findByYearAndProduct(productId, year - 1);
        if (current == null) {
            current = new InventoryDetailModel();
            current.setProductId(productId);
            current.setQuantity(pre.getQuantity());
            current.setTotalPrice(pre.getTotalPrice());
            current.setAcademicYear(year);
        }
        exportPriceService.delete(productId, time);
        while (i < imports.size() && j < exports.size()) {
            LocalDateTime importDate = LocalDateTime.parse(imports.get(i).getCreateAt(), formatter);
            LocalDateTime exportDate = LocalDateTime.parse(exports.get(j).getCreateAt(), formatter);
            if (importDate.isBefore(exportDate)) {
                exportPrice = doImport(current, imports.get(i), time, importDate);
                i++;
            } else {
                doExport(exportPrice, exports.get(j), current);
                j++;
            }
        }
        while (i < imports.size()) {
            LocalDateTime importDate = LocalDateTime.parse(imports.get(i).getCreateAt(), formatter);
            exportPrice = doImport(current, imports.get(i), time, importDate);
            i++;
        }
        while (j < exports.size()) {
            doExport(exportPrice, exports.get(j), current);
            j++;
        }
//        save or update
        inventoryDetailService.save(List.of(current));
    }

    public int doImport(InventoryDetailModel inventoryDetail, ImportReceiptModel importReceipt, LocalDateTime updateTime, LocalDateTime importDate) {
        ImportReceiptDetailModel importReceiptDetail = null;
        for (ImportReceiptDetailModel importReceiptDetailModel : importReceipt.getImportReceiptDetails()) {
            if (importReceiptDetailModel.getProductId() == inventoryDetail.getProductId()) {
                importReceiptDetail = importReceiptDetailModel;
                break;
            }
        }
        assert importReceiptDetail != null;
        ExportPriceModel exportPrice = calculateUnitPriceOfProduct(inventoryDetail, importReceiptDetail.getTotalPrice(),
                importReceiptDetail.getActualQuantity(), importReceipt.getId(), importDate);
        if (updateTime.isBefore(importDate) || updateTime.isEqual(importDate)) {
            exportPriceService.save(exportPrice);
        }
        inventoryDetail.setQuantity(inventoryDetail.getQuantity() + importReceiptDetail.getActualQuantity());
        inventoryDetail.setTotalPrice(inventoryDetail.getTotalPrice() + importReceiptDetail.getUnitPrice() * importReceiptDetail.getActualQuantity());
//       save or update
        long importReceiptId = importReceiptService.save(importReceipt);
        importReceiptDetailService.save(List.of(importReceiptDetail), importReceiptId);
        return (int) exportPrice.getExportPrice();
    }

    public void doExport(int exportPrice, ExportReceiptModel exportReceipt, InventoryDetailModel inventoryDetail) {
        for (ExportReceiptDetailModel exportReceiptDetail : exportReceipt.getExportReceiptDetailModels()) {
            if (exportReceiptDetail.getProductId() == inventoryDetail.getProductId()) {
                exportReceiptDetail.setOriginalUnitPrice(exportPrice);
                exportReceiptDetail.setDisplayUnitPrice(exportPrice);
                exportReceiptDetail.setTotalPrice(exportPrice * exportReceiptDetail.getActualQuantity());
                if (inventoryDetail.getQuantity() < exportReceiptDetail.getActualQuantity()) {
                    throw new StockUnderFlowException("Số lượng hàng trong kho không đủ");
                }
                inventoryDetail.setTotalPrice(inventoryDetail.getTotalPrice() - exportReceiptDetail.getTotalPrice());
                inventoryDetail.setQuantity(inventoryDetail.getQuantity() - exportReceiptDetail.getActualQuantity());
//               save or update
                long exportReceiptId = exportReceiptService.save(exportReceipt);
                exportReceiptDetailService.update(List.of(exportReceiptDetail));
                break;
            }
        }
        exportReceiptService.save(exportReceipt);
    }

    private ExportPriceModel calculateUnitPriceOfProduct(InventoryDetailModel inventoryDetail,
                                                         double totalPriceImported, int quantityImported, long importReceiptId,
                                                         LocalDateTime importDate) {
        try {
            ExportPriceModel exportPriceModel = new ExportPriceModel();
            exportPriceModel.setProductId(inventoryDetail.getProductId());
            exportPriceModel.setExportTime(importDate);
            exportPriceModel.setImportReceiptId(importReceiptId);
            exportPriceModel.setQuantityInStock(inventoryDetail.getQuantity());
            exportPriceModel.setQuantityImported(quantityImported);
            exportPriceModel.setTotalImportPrice(totalPriceImported);
            exportPriceModel.setTotalPriceInStock(inventoryDetail.getTotalPrice());
            double newUnitPrice = Math.round((inventoryDetail.getTotalPrice() + totalPriceImported) / (quantityImported + inventoryDetail.getQuantity()));
            exportPriceModel.setExportPrice(newUnitPrice);
            return exportPriceModel;
        } catch (ArithmeticException e) {
            e.printStackTrace();
            throw new DivisionByZeroException("Số lượng tồn kho và số lượng nhập không hợp lệ, vui lòng kiểm tra lại.");
        }
    }

}
