package com.manager.stock.manager_stock.screen.transaction.presenter;

import com.google.common.util.concurrent.AtomicDouble;
import com.manager.stock.manager_stock.dao.*;
import com.manager.stock.manager_stock.dao.impl.*;
import com.manager.stock.manager_stock.exception.DivisionByZeroException;
import com.manager.stock.manager_stock.exception.StockUnderFlowException;
import com.manager.stock.manager_stock.model.*;
import com.manager.stock.manager_stock.model.tableData.ExportReceiptDetailModelTable;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptModelTable;
import com.manager.stock.manager_stock.service.*;
import com.manager.stock.manager_stock.service.impl.*;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Trọng Hướng
 */
public class InventoryReceiptService {

    private final IImportReceiptService importReceiptService;
    private final IExportPriceService exportPriceService;
    private final IImportReceiptDetailService importReceiptDetailService;
    private final IExportReceiptDetailService exportReceiptDetailService;
    private final IInventoryDetailService inventoryDetailService;
    private static InventoryReceiptService INSTANCE;

    private InventoryReceiptService() {
        importReceiptDetailService = ImportReceiptDetailServiceImpl.getInstance();
        exportPriceService = ExportPriceServiceImpl.getInstance();
        importReceiptService = ImportReceiptServiceImpl.getInstance();
        exportReceiptDetailService = ExportReceiptDetailServiceImpl.getInstance();
        inventoryDetailService = InventoryDetailServiceImpl.getInstance();
    }

    public static InventoryReceiptService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new InventoryReceiptService();
        }
        return INSTANCE;
    }

    public void solveImportReceipt(ImportReceiptModel importReceiptModel, ObservableList<ImportReceiptDetailModelTable> importReceiptDetailModelsTable) {

    }

    public void solveExportReceipt(ExportReceiptModel exportReceiptModel, ObservableList<ExportReceiptDetailModelTable> exportReceiptDetailModelsTable) {

    }

    // danh sách product ở đây là danh sách trong phiếu hiện tại
    private void updateInventoryAndExportPrice(Map<String, Long> productIds, LocalDateTime receiptDate) {
        AtomicInteger currentYear = new AtomicInteger(2025);
        List<InventoryDetailModel> inventoryDetailModels = new ArrayList<>();
        for(Map.Entry<String, Long> entry : productIds.entrySet()) {
            long productId = entry.getValue();
            String productCode = entry.getKey();
            // 1. lấy danh sách toàn bộ đơn giá
            List<ExportPriceModel> exportPricesByProduct = exportPriceService.findAllExportPriceByProductAndOrderByAsc(productId);
            // 2. Lấy danh sách export receipt detail
            List<ExportReceiptDetailModel> exportReceiptDetailsByProduct = exportReceiptDetailService.findAllByProduct(productId);
            // 3. lấy tồn kho đầu kì của current year
            InventoryDetailModel inventoryDetailModel = null;
            for(int year = currentYear.get() - 1; year >= 2020;  year--) {
                inventoryDetailModel = inventoryDetailService.findByYearAndProduct(productId, year);
                if(inventoryDetailModel != null) break;
            }
            // TH sản phẩm chưa có tồn kho đầu kì
            if(inventoryDetailModel == null) {
                inventoryDetailModel = new InventoryDetailModel(
                    System.currentTimeMillis(),
                    productId, 0, 0.0, currentYear.get()
                );
                inventoryDetailService.save(List.of(inventoryDetailModel));
            }
            int i = 0, j = 0;
            AtomicInteger quantityInStock = new AtomicInteger(inventoryDetailModel.getQuantity());
            AtomicDouble totalPriceInStock = new AtomicDouble(inventoryDetailModel.getTotalPrice());
            while(i <= exportPricesByProduct.size() && j <= exportReceiptDetailsByProduct.size()) {
                ExportPriceModel exportPriceModel = exportPricesByProduct.get(i);
                ExportReceiptDetailModel exportReceiptDetailModel = exportReceiptDetailsByProduct.get(j);
                // TH phiếu nhập trước
                if(exportPriceModel.getExportTime().isBefore(exportReceiptDetailModel.getExportDate())) {
                    // TH phiêếu nhập này bước sang năm khác

                    i++;
                }
                // trường hợp là phiếu xuất
                else {

                    j++;
                }
            }
        }
    }

    private void updateImportReceipt(ExportPriceModel exportPriceModel, Integer currentYear, Integer quantityInStock, Double totalPriceInStock, long productId) {
        if(exportPriceModel.getExportTime().getYear() != currentYear) {
            saveInventory(quantityInStock, totalPriceInStock, currentYear, productId);
            currentYear = exportPriceModel.getExportTime().getYear();
        }
        quantityInStock += exportPriceModel.getQuantityImported();
        totalPriceInStock += exportPriceModel.getTotalImportPrice();
        // tính lại đơn giá
        calculateUnitPriceOfProduct(quantityInStock, totalPriceInStock, exportPriceModel.getTotalImportPrice(), exportPriceModel.getQuantityImported(), exportPriceModel);
    }

    private void updateExportReceipt(ExportReceiptDetailModel exportReceiptDetailModel, ExportPriceModel exportPriceModel, Integer currentYear, Integer quantityInStock, Double totalPriceInStock, long productId, String productCode) {
        if(exportReceiptDetailModel.getExportDate().getYear() != currentYear) {
            saveInventory(quantityInStock, totalPriceInStock, currentYear, productId);
            currentYear = exportReceiptDetailModel.getExportDate().getYear();
        }
        // 1.1: Kiem tra xem ton kho con du khong
        if(exportReceiptDetailModel.getActualQuantity() > quantityInStock) {
            StringBuilder message = new StringBuilder("- Sản phẩm: ").append(productCode);
            message.append("\n - Xuất tại thời điểm: ").append(exportReceiptDetailModel.getExportDate());
            message.append("\n - Nhưng tồn kho tại thời điểm đó còn: ").append(quantityInStock);
            throw new StockUnderFlowException(message.toString());
        }
        exportReceiptDetailModel.setOriginalUnitPrice(exportPriceModel.getExportPrice());
        quantityInStock -= exportReceiptDetailModel.getActualQuantity();
        totalPriceInStock -= (exportReceiptDetailModel.getActualQuantity() * exportPriceModel.getExportPrice());
    }

    private void calculateUnitPriceOfProduct(int quantityInStock, double totalPriceInStock, double totalPriceImported, int quantityImported, ExportPriceModel exportPriceModel) {
        try {
            exportPriceModel.setQuantityInStock(quantityInStock);
            double newUnitPrice = Math.round((totalPriceInStock + totalPriceImported) / (quantityImported + quantityInStock));
            exportPriceModel.setExportPrice(newUnitPrice);
            exportPriceModel.setTotalPriceInStock(totalPriceInStock);
        } catch (ArithmeticException e) {
            e.printStackTrace();
            throw new DivisionByZeroException("Số lượng tồn kho và số lượng nhập không hợp lệ, vui lòng kiểm tra lại.");
        }
    }

    private void saveInventory(int quantityInStock, double totalPriceInStock, int year, long productId) {
        InventoryDetailModel inventoryDetailModelCurrentYear = inventoryDetailService.findByYearAndProduct(productId, year);
        if(inventoryDetailModelCurrentYear == null) {
            // năm hiện tại chưa có tồn kho ==> thêm mới
            inventoryDetailModelCurrentYear = new InventoryDetailModel();
        }
        inventoryDetailModelCurrentYear.setQuantity(quantityInStock);
        inventoryDetailModelCurrentYear.setTotalPrice(totalPriceInStock);
        inventoryDetailService.save(List.of(inventoryDetailModelCurrentYear));
    }
}
