package com.manager.stock.manager_stock.screen.transaction.presenter;

import com.google.api.client.util.DateTime;
import com.google.common.util.concurrent.AtomicDouble;
import com.manager.stock.manager_stock.dao.*;
import com.manager.stock.manager_stock.dao.impl.*;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.exception.DivisionByZeroException;
import com.manager.stock.manager_stock.exception.StockUnderFlowException;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptDetailModelMapper;
import com.manager.stock.manager_stock.model.*;
import com.manager.stock.manager_stock.model.tableData.ExportReceiptDetailModelTable;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptModelTable;
import com.manager.stock.manager_stock.service.*;
import com.manager.stock.manager_stock.service.impl.*;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

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

    public void solveImportReceipt(ImportReceiptModel newImportReceiptModel, ObservableList<ImportReceiptDetailModelTable> importReceiptDetailModelsTable, String oldCreateAt) {
        try {
            List<ImportReceiptDetailModel> importDetailToUpdate = new ArrayList<>();
            List<ImportReceiptDetailModel> importDetailToInsert = new ArrayList<>();
            Set<Long> importDetailIdsToDelete = new HashSet<>();
            Map<String, Long> productIdsToUpdate = new HashMap<>();
            List<ExportPriceModel> exportPriceModelsToInsert = new ArrayList<>();
            long newImportReceiptId;
            int currentYear = Math.min(LocalDateTime.parse(newImportReceiptModel.getCreateAt(), formatter2).getYear(),
                                        LocalDateTime.parse(oldCreateAt, formatter2).getYear());
            // 2. cập nhật hoặc thêm mới thông tin phiếu nhập
            if (newImportReceiptModel.isInsert()) {
                newImportReceiptId = importReceiptService.save(newImportReceiptModel);
            } else {
                importReceiptService.update(newImportReceiptModel);
                newImportReceiptId = newImportReceiptModel.getId();
            }
//         = (newImportReceiptModel.getId() != -1 ? newImportReceiptModel.getId() : System.currentTimeMillis());
            // 1. Lấy danh sách sản phẩm cần cập nhật
            importReceiptDetailModelsTable.forEach(viewModel -> {
                boolean isDeleted = viewModel.isDeleted();
                boolean isNew = viewModel.getId() == -1;
                if (isDeleted) {
                    importDetailIdsToDelete.add(viewModel.getId());
                    return;
                }
                // Chỉ update product khi không bị xóa
                productIdsToUpdate.put(viewModel.getProductCode(), viewModel.getProductId());
                ImportReceiptDetailModel model =
                        ImportReceiptDetailModelMapper.INSTANCE.fromViewModelToModel(viewModel);
                if (isNew) {
                    importDetailToInsert.add(model);
                    exportPriceModelsToInsert.add(
                            new ExportPriceModel(
                                    System.currentTimeMillis(),
                                    viewModel.getProductId(),
                                    LocalDateTime.parse(newImportReceiptModel.getCreateAt(), formatter2),
                                    0, 0,
                                    viewModel.getActualQuantity(),
                                    viewModel.getTotalPrice(),
                                    0.0
                            )
                    );
                } else {
                    importDetailToUpdate.add(model);
                }
            });
            // Nếu đang thực hiện xóa phiếu nhập ==> xóa phiếu nhâp + exportPrice
            if(newImportReceiptModel.isIsDeleted()) {
                importReceiptService.delete(newImportReceiptModel.getId());
                exportPriceService.deleteByImportReceipt(newImportReceiptModel.getId());
                importReceiptDetailService.deleteImportReceiptByImportReceipt(newImportReceiptId);
            }
            else {
                exportPriceService.updateExportTimeByImportReceipt(newImportReceiptModel.getId(), LocalDateTime.parse(newImportReceiptModel.getCreateAt(), formatter2));
            }
            // 3. Xóa chi tiết phiếu xuất
            importReceiptDetailService.deleteByIds(importDetailIdsToDelete);
            // 4. Thêm mới nếu có sản phẩm mới
            importReceiptDetailService.save(importDetailToInsert, newImportReceiptId);
            // 5. update số lượng + đơn giá sản phẩm trong phiếu nhập
            importReceiptDetailService.update(importDetailToUpdate);
            // 6. thêm mới exportPrice nếu có sản phẩm thêm mới
            exportPriceService.save(exportPriceModelsToInsert);
            // 7 Cập nhật lại toàn bộ đơn giá, tồn kho
            updateInventoryAndExportPrice(productIdsToUpdate, currentYear);
            importReceiptService.commit();
        }
        catch (StockUnderFlowException | DaoException exception) {
            importReceiptService.rollback();
            throw exception;
        }
        catch (Exception e) {
            importReceiptService.rollback();
            e.printStackTrace();
        }
    }

    public void solveExportReceipt(ExportReceiptModel exportReceiptModel, ObservableList<ExportReceiptDetailModelTable> exportReceiptDetailModelsTable) {

    }

    // danh sách product ở đây là danh sách trong phiếu hiện tại
    private void updateInventoryAndExportPrice(Map<String, Long> productIds, int y) {
        AtomicInteger currentYear = new AtomicInteger(y);
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
            while(i < exportPricesByProduct.size() && j < exportReceiptDetailsByProduct.size()) {
                ExportPriceModel exportPriceModel = exportPricesByProduct.get(i);
                ExportReceiptDetailModel exportReceiptDetailModel = exportReceiptDetailsByProduct.get(j);
                // TH phiếu xuất
                if(exportPriceModel.getExportTime().isAfter(exportReceiptDetailModel.getExportDate())) {
                    exportPriceModel = exportPricesByProduct.get(i < 1 ? 0 : (i - 1));
                    updateExportReceipt(exportReceiptDetailModel, exportPriceModel, currentYear, quantityInStock, totalPriceInStock, productId, productCode);
                    j++;
                }
                // TH phiếu nhập
                else {
                    updateImportReceipt(exportPriceModel, currentYear, quantityInStock, totalPriceInStock, productId);
                    i++;
                }
            }
            while(i < exportPricesByProduct.size()) {
                ExportPriceModel exportPriceModel = exportPricesByProduct.get(i);
                updateImportReceipt(exportPriceModel, currentYear, quantityInStock, totalPriceInStock, productId);
                i++;
            }
            while(j < exportReceiptDetailsByProduct.size()) {
                ExportPriceModel exportPriceModel = exportPricesByProduct.get(i < 1 ? 0 : (i - 1));
                ExportReceiptDetailModel exportReceiptDetailModel = exportReceiptDetailsByProduct.get(j);
                updateExportReceipt(exportReceiptDetailModel, exportPriceModel, currentYear, quantityInStock, totalPriceInStock, productId, productCode);
                j++;
            }

            exportPriceService.update(exportPricesByProduct);
            exportReceiptDetailService.update(exportReceiptDetailsByProduct);
        }
    }

    private void updateImportReceipt(ExportPriceModel exportPriceModel, AtomicInteger currentYear, AtomicInteger quantityInStock, AtomicDouble totalPriceInStock, long productId) {
        if(exportPriceModel.getExportTime().getYear() != currentYear.get()) {
            saveInventory(quantityInStock.get(), totalPriceInStock.get(), currentYear.get(), productId);
            currentYear.set(exportPriceModel.getExportTime().getYear());
        }
        quantityInStock.set(quantityInStock.get() + exportPriceModel.getQuantityImported());
        totalPriceInStock.set(totalPriceInStock.get() + exportPriceModel.getTotalImportPrice());
        // tính lại đơn giá
        calculateUnitPriceOfProduct(quantityInStock.get(), totalPriceInStock.get(), exportPriceModel.getTotalImportPrice(), exportPriceModel.getQuantityImported(), exportPriceModel);
    }

    private void updateExportReceipt(ExportReceiptDetailModel exportReceiptDetailModel, ExportPriceModel exportPriceModel, AtomicInteger currentYear, AtomicInteger quantityInStock, AtomicDouble totalPriceInStock, long productId, String productCode) {
        if(exportReceiptDetailModel.getExportDate().getYear() != currentYear.get()) {
            saveInventory(quantityInStock.get(), totalPriceInStock.get(), currentYear.get(), productId);
            currentYear.set(exportReceiptDetailModel.getExportDate().getYear());
        }
        // 1.1: Kiem tra xem ton kho con du khong
        if(exportReceiptDetailModel.getActualQuantity() > quantityInStock.get()) {
            StringBuilder message = new StringBuilder("- Sản phẩm: ").append(productCode);
            message.append("\n - Xuất tại thời điểm: ").append(exportReceiptDetailModel.getExportDate());
            message.append("\n - Nhưng tồn kho tại thời điểm đó còn: ").append(quantityInStock);
            throw new StockUnderFlowException(message.toString());
        }
        exportReceiptDetailModel.setOriginalUnitPrice(exportPriceModel.getExportPrice());
        quantityInStock.set(quantityInStock.get() - exportReceiptDetailModel.getActualQuantity());
        totalPriceInStock.set(totalPriceInStock.get() - (exportReceiptDetailModel.getActualQuantity() * exportPriceModel.getExportPrice()));
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
            inventoryDetailModelCurrentYear.setProductId(productId);
        }
        inventoryDetailModelCurrentYear.setQuantity(quantityInStock);
        inventoryDetailModelCurrentYear.setTotalPrice(totalPriceInStock);
        inventoryDetailService.save(List.of(inventoryDetailModelCurrentYear));
    }

//    private LocalDateTime parseDate(String date) {
//        LocalDateTime time1 = LocalDateTime.parse(date, formatter2);
//        return LocalDateTime.parse(time1.format(formatter), formatter);
//    }
}
