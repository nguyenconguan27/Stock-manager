package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.impl.DatasourceInitialize;
import com.manager.stock.manager_stock.exception.DivisionByZeroException;
import com.manager.stock.manager_stock.exception.StockUnderFlowException;
import com.manager.stock.manager_stock.model.*;
import com.manager.stock.manager_stock.service.*;
import com.manager.stock.manager_stock.utils.AlertUtils;
import com.manager.stock.manager_stock.utils.FormatMoney;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ComputeServiceImpl implements ComputService{

    private IImportReceiptService importReceiptService;
    private IExportReceiptService exportReceiptService;
    private IInventoryDetailService inventoryDetailService;
    private IImportReceiptDetailService importReceiptDetailService;
    private IExportReceiptDetailService exportReceiptDetailService;
    private IExportPriceService exportPriceService;
    private static ComputeServiceImpl instance;
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

    public static ComputeServiceImpl getInstance() {
        if (instance == null) {
            instance = new ComputeServiceImpl();
        }
        return instance;
    }

    public void compute(ReceiptModel receipt, List<? extends DetailModel> details, String action) throws SQLException {
        int year, minYear;
        List<ImportReceiptModel> importReceipts;
        List<ExportReceiptModel> exportReceipts;
        LocalDateTime dateTime;
        dateTime = LocalDateTime.parse(receipt.getCreateAt(), formatter);
        year = dateTime.getYear();
        minYear = year;
        long id = receipt.getId() != null ? receipt.getId() : -1;
        try {
            if (receipt instanceof ImportReceiptModel) {
                if(action.equals("delete")) {
                    exportPriceService.deleteByImportReceiptId(receipt.getId());
                }
                ImportReceiptModel importReceiptModel = importReceiptService.findById(id);
                if (importReceiptModel != null) {
                    LocalDateTime preDate = LocalDateTime.parse(importReceiptModel.getCreateAt(), formatter);
                    if (preDate.isBefore(dateTime)) {
                        minYear = preDate.getYear();
                    }
                }
            } else {
                ExportReceiptModel exportReceiptModel = exportReceiptService.findById(id);
                if (exportReceiptModel != null) {
                    LocalDateTime preDate = LocalDateTime.parse(exportReceiptModel.getCreateAt(), formatter);
                    if (preDate.isBefore(dateTime)) {
                        minYear = preDate.getYear();
                    }
                }
            }
            if (action.equals("delete")) {
                exportReceiptService.delete(receipt.getId());
                importReceiptService.delete(receipt.getId());
            }
            for (DetailModel detailModel : details) {
                for (int y = minYear; y <= LocalDateTime.now().getYear(); y++) {
                    importReceipts = importReceiptService.findAllByProductAndYear(detailModel.getProductId(), y);
                    exportReceipts = exportReceiptService.findAllByProductAndYear(detailModel.getProductId(), y);
                    for (ImportReceiptModel importReceipt : importReceipts) {
                        importReceipt.setImportReceiptDetails(importReceiptDetailService.findAllByImportReceiptId(importReceipt.getId()));
                    }
                    for (ExportReceiptModel exportReceipt : exportReceipts) {
                        exportReceipt.setExportReceiptDetailModels(exportReceiptDetailService.findAllByExportReceipt(exportReceipt.getId()));
                    }
                    if (receipt instanceof ImportReceiptModel) {
                        int i = updateList(receipt, importReceipts, dateTime);
                        if (!action.equals("delete") && y == year) {
                            ImportReceiptModel importReceipt = (ImportReceiptModel) receipt;
                            importReceipt.setAcademicYear(year);
                            importReceipt.setImportReceiptDetails(details.stream()
                                    .map(d -> (ImportReceiptDetailModel) d)
                                    .toList());
                            importReceipts.add(i, importReceipt);
                        }
                    } else {
                        int i = updateList(receipt, exportReceipts, dateTime);
                        if (!action.equals("delete") && y == year) {
                            ExportReceiptModel exportReceipt = (ExportReceiptModel) receipt;
                            exportReceipt.setAcademicYear(year);
                            exportReceipt.setExportReceiptDetailModels(details.stream()
                                    .map(d -> (ExportReceiptDetailModel) d)
                                    .toList());
                            exportReceipts.add(i, exportReceipt);
                        }
                    }
                    computeForProduct(detailModel.getProductId(), y, importReceipts, exportReceipts, dateTime);
                }
            }
            DatasourceInitialize.getInstance().commit();
        } catch (StockUnderFlowException e) {
            DatasourceInitialize.getInstance().rollback();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            DatasourceInitialize.getInstance().rollback();
            throw e;
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

    public ExportPriceModel findExportPriceModel(long productId, int year) {
        for(int y = year; y >= 2020; y--) {
            List<ExportPriceModel> exportPriceModels = exportPriceService.findByProductAndYear(productId, y);
            if(exportPriceModels != null && !exportPriceModels.isEmpty()) {
                return exportPriceModels.get(0);
            }
        }
        return new ExportPriceModel();
    }

    public InventoryDetailModel findInventory(long productId, int year) {
        for(int y = year; y >= 2020; y--) {
            InventoryDetailModel pre = inventoryDetailService.findByYearAndProduct(productId, year);
            if(pre != null && pre.getId() != null) {
                return pre;
            }
        }
        return new InventoryDetailModel();
    }

    public void computeForProduct(long productId, int year, List<ImportReceiptModel> imports, List<ExportReceiptModel> exports, LocalDateTime time) {
        int i = 0, j = 0;
        InventoryDetailModel current = inventoryDetailService.findByYearAndProduct(productId, year);
        InventoryDetailModel pre = findInventory(productId, year - 1);
        ExportPriceModel exportPriceModel = findExportPriceModel(productId, year - 1);
        if(pre.getId() != null) {
            current.setQuantity(pre.getQuantity());
            current.setTotalPrice(pre.getTotalPrice());
        } else {
            current.setQuantity(0);
            current.setTotalPrice((double) 0);
        }
        current.setProductId(productId);
        current.setAcademicYear(year);
        while (i < imports.size() && j < exports.size()) {
            LocalDateTime importDate = LocalDateTime.parse(imports.get(i).getCreateAt(), formatter);
            LocalDateTime exportDate = LocalDateTime.parse(exports.get(j).getCreateAt(), formatter);
            if (importDate.isBefore(exportDate)) {
                exportPriceModel = doImport(current, imports.get(i), time, importDate);
                i++;
            } else {
                doExport(exportPriceModel, exports.get(j), current, time);
                j++;
            }
        }
        while (i < imports.size()) {
            LocalDateTime importDate = LocalDateTime.parse(imports.get(i).getCreateAt(), formatter);
            exportPriceModel = doImport(current, imports.get(i), time, importDate);
            i++;
        }
        while (j < exports.size()) {
            doExport(exportPriceModel, exports.get(j), current, time);
            j++;
        }
//        save or update
        inventoryDetailService.save(current);
    }

    public ExportPriceModel doImport(InventoryDetailModel inventoryDetail, ImportReceiptModel importReceipt, LocalDateTime updateTime, LocalDateTime importDate) {
        ImportReceiptDetailModel importReceiptDetail = null;
        for (ImportReceiptDetailModel importReceiptDetailModel : importReceipt.getImportReceiptDetails()) {
            if (Objects.equals(importReceiptDetailModel.getProductId(), inventoryDetail.getProductId())) {
                importReceiptDetail = importReceiptDetailModel;
                break;
            }
        }
        assert importReceiptDetail != null;
        long importReceiptId = importReceiptService.save(importReceipt);
        ExportPriceModel exportPrice = exportPriceService.findByProductIdAndImportReceipt(inventoryDetail.getProductId(), importReceiptId);
        exportPrice = calculateUnitPriceOfProduct(inventoryDetail, importReceiptDetail.getTotalPrice(),
                importReceiptDetail.getActualQuantity(), importReceiptId, importDate, exportPrice);
        exportPrice.setExportTime(LocalDateTime.parse(importReceipt.getCreateAt(), formatter));
        long id = exportPriceService.save(exportPrice);
        exportPrice.setId(id);
        inventoryDetail.setQuantity(inventoryDetail.getQuantity() + importReceiptDetail.getActualQuantity());
        inventoryDetail.setTotalPrice(inventoryDetail.getTotalPrice() + importReceiptDetail.getUnitPrice() * importReceiptDetail.getActualQuantity());
//       save or update
        importReceipt.setTotalPriceInWord(FormatMoney.formatMoneyToWord((long) importReceipt.getTotalPrice()));
        importReceiptDetailService.save(importReceiptDetail, importReceiptId);
        return exportPrice;
    }

    public void setTotalPrice(ExportReceiptModel exportReceiptModel) {
        long total = 0;
        for(ExportReceiptDetailModel exportReceiptDetailModel: exportReceiptModel.getExportReceiptDetailModels()) {
            total += exportReceiptDetailModel.getOriginalUnitPrice() * exportReceiptDetailModel.getActualQuantity();
        }
        exportReceiptModel.setTotalPrice((double) total);
        exportReceiptModel.setTotalPriceInWord(FormatMoney.formatMoneyToWord(total));
    }

    public void doExport(ExportPriceModel exportPrice, ExportReceiptModel exportReceipt, InventoryDetailModel inventoryDetail, LocalDateTime updateTime) {
        setTotalPrice(exportReceipt);
        long exportReceiptId = exportReceiptService.save(exportReceipt);
        for (ExportReceiptDetailModel exportReceiptDetail : exportReceipt.getExportReceiptDetailModels()) {
            if (Objects.equals(exportReceiptDetail.getProductId(), inventoryDetail.getProductId())) {
                exportReceiptDetail.setOriginalUnitPrice(exportPrice.getExportPrice());
                exportReceiptDetail.setDisplayUnitPrice(exportPrice.getExportPrice());
                exportReceiptDetail.setTotalPrice(exportPrice.getExportPrice() * exportReceiptDetail.getActualQuantity());
                exportReceiptDetail.setExportPriceId(exportPrice.getId());
                if (inventoryDetail.getQuantity() < exportReceiptDetail.getActualQuantity()) {
                    throw new StockUnderFlowException("Số lượng hàng trong kho không đủ");
                }
                inventoryDetail.setTotalPrice(inventoryDetail.getTotalPrice() - exportReceiptDetail.getTotalPrice());
                inventoryDetail.setQuantity(inventoryDetail.getQuantity() - exportReceiptDetail.getActualQuantity());
                exportReceiptDetailService.save(exportReceiptDetail, exportReceiptId);
//               save or update
                break;
            }
        }
    }

    private ExportPriceModel calculateUnitPriceOfProduct(InventoryDetailModel inventoryDetail,
                                                         double totalPriceImported, int quantityImported, long importReceiptId,
                                                         LocalDateTime importDate, ExportPriceModel exportPriceModel) {
        try {
            exportPriceModel.setProductId(inventoryDetail.getProductId());
            exportPriceModel.setExportTime(importDate);
            exportPriceModel.setImportReceiptId(importReceiptId);
            exportPriceModel.setQuantityInStock(inventoryDetail.getQuantity());
            exportPriceModel.setQuantityImported(quantityImported);
            exportPriceModel.setTotalImportPrice(totalPriceImported);
            exportPriceModel.setTotalPriceInStock(inventoryDetail.getTotalPrice());
            double newUnitPrice = (inventoryDetail.getTotalPrice() + totalPriceImported) / (quantityImported + inventoryDetail.getQuantity());
            exportPriceModel.setExportPrice(newUnitPrice);
            return exportPriceModel;
        } catch (ArithmeticException e) {
            e.printStackTrace();
            throw new DivisionByZeroException("Số lượng tồn kho và số lượng nhập không hợp lệ, vui lòng kiểm tra lại.");
        }
    }

}
