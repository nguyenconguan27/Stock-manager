package com.manager.stock.manager_stock.screen.transaction.presenter;

import com.almasb.fxgl.physics.CollisionDetectionStrategy;
import com.manager.stock.manager_stock.exception.CanNotFoundException;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.exception.DivisionByZeroException;
import com.manager.stock.manager_stock.exception.StockUnderFlowException;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ExportReceiptDetailModelTableMapper;
import com.manager.stock.manager_stock.model.*;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndPrice;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;
import com.manager.stock.manager_stock.model.tableData.ExportReceiptDetailModelTable;
import com.manager.stock.manager_stock.service.*;
import com.manager.stock.manager_stock.service.impl.*;
import com.manager.stock.manager_stock.utils.GenericConverterBetweenModelAndTableData;
import javafx.animation.ScaleTransition;

import javax.sound.midi.Soundbank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptPresenter {
    private final IExportReceiptService exportReceiptService;
    private final IExportReceiptDetailService exportReceiptDetailService;
    private final IInventoryDetailService inventoryDetailService;
    private final ProductService productService;
    private final IExportPriceService exportPriceService;
    private static ExportReceiptPresenter instance;

    private ExportReceiptPresenter() {
        exportReceiptDetailService = ExportReceiptDetailServiceImpl.getInstance();
        exportReceiptService = ExportReceiptServiceImpl.getInstance();
        productService = ProductServiceImpl.getInstance();
        inventoryDetailService = InventoryDetailServiceImpl.getInstance();
        exportPriceService = ExportPriceServiceImpl.getInstance();
    }

    public static ExportReceiptPresenter getInstance() {
        if(instance == null) {
            instance = new ExportReceiptPresenter();
        }
        return instance;
    }

    public HashMap<Long, ProductIdAndActualQuantityAndTotalPriceOfReceipt> findProductIdAndActualQuantityAndTotalPriceOfReceipt(List<Long> exportReceiptIds) throws DaoException {
        List<ProductIdAndActualQuantityAndTotalPriceOfReceipt> productIdAndActualQuantityAndTotalPriceOfReceipts = exportReceiptService.findProductIdAndTotalPriceAndTotalQuantityByExportReceipt(exportReceiptIds);
        return productIdAndActualQuantityAndTotalPriceOfReceipts.stream()
                .collect(Collectors.toMap(
                   ProductIdAndActualQuantityAndTotalPriceOfReceipt::productId,
                   Function.identity(),
                    (existing, replacement) -> replacement,
                    HashMap::new
                ));
    }

    public void deleteById(List<Long> exportReceiptIds) throws DaoException {
        exportReceiptService.deleteByIds(exportReceiptIds);
    }

    public List<ExportReceiptModel> findAllExportReceipt(Optional<Integer> academicYear) throws DaoException {
        int academicYearValue = academicYear.orElse(Calendar.getInstance().get(Calendar.YEAR));
        System.out.println("Academic year: " + academicYearValue);
        return exportReceiptService.findAllByAcademicYear(academicYearValue);
    }

    public List<ExportReceiptDetailModel> findAllExportReceiptDetailByExportReceipt(long exportReceiptId) throws DaoException {
        return exportReceiptDetailService.findAllByExportReceipt(exportReceiptId);
    }

    public List<ProductModel> loadAllProduct() {
        return productService.getAllProducts();
    }

    public ExportPriceIdAndPrice findExportPriceIdAndPriceByProductAndLastTime(long productId) throws DaoException {
        return exportPriceService.findExportPriceByProductAndLastTime(productId);
    }

    public void save(ExportReceiptModel exportReceiptModel, List<ExportReceiptDetailModelTable> exportReceiptDetailModelTables, HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap) {
        // lấy danh sách tồn kho theo sản phẩm và theo năm của phiếu xuất
        List<ExportReceiptDetailModel> exportReceiptDetailModels = GenericConverterBetweenModelAndTableData.convertToListModel(
                exportReceiptDetailModelTables, ExportReceiptDetailModelTableMapper.INSTANCE::fromViewModelToModel
        );
        int academicYearValue = getYearOfExportReceipt(exportReceiptModel.getCreateAt());
        System.out.println("Academic year(save): " + academicYearValue);
        List<Long> productIds = exportReceiptDetailModels.stream().map(ExportReceiptDetailModel::getProductId).collect(Collectors.toList());
        // thêm mới phiếu xuất
        List<Long> exportReceiptDetailIds = new ArrayList<>();
        long exportReceiptId = -1;
        try {
            exportReceiptModel.setAcademicYear(academicYearValue);
            exportReceiptId = exportReceiptService.save(exportReceiptModel);
            System.out.println("Export receipt id : " + exportReceiptId);
            // thêm mới danh sách phiếu xuất chi tiết
            exportReceiptDetailIds = exportReceiptDetailService.save(exportReceiptDetailModels, exportReceiptId);

            updateInventory(exportReceiptDetailModels, academicYearValue, productIds);
        }
        catch (DaoException | CanNotFoundException | StockUnderFlowException e) {
            if(exportReceiptId != -1) {
                List<Long> exportReceiptIds = new ArrayList<>();
                exportReceiptIds.add(exportReceiptId);
                exportReceiptService.deleteByIds(exportReceiptIds);
            }
//            if(!exportReceiptDetailIds.isEmpty()) {
//                exportReceiptDetailService.delete(exportReceiptDetailIds);
//            }
            throw e;
        }
    }

    private void updateInventory(List<ExportReceiptDetailModel> exportReceiptDetailModels, int academicYear, List<Long> productIds) throws DaoException {
        HashMap<Long, InventoryDetailModel> inventoryDetailByProductAndAcademicYear = inventoryDetailService.findAllByAcademicYearAndProductId(academicYear, productIds);
        System.out.println(inventoryDetailByProductAndAcademicYear.keySet());
        HashMap<Long, InventoryDetailModel> inventoryDetailByProductAndPreviousAcademicYear = inventoryDetailService.findAllByAcademicYearAndProductId(academicYear-1, productIds);
        List<InventoryDetailModel> inventoryDetailModelsToInsert = new ArrayList<>();
        List<InventoryDetailModel> inventoryDetailModelsToUpdate = new ArrayList<>();

        for(ExportReceiptDetailModel exportReceiptDetailModel : exportReceiptDetailModels) {
            long productId = exportReceiptDetailModel.getProductId();
            int actualQuantity = exportReceiptDetailModel.getActualQuantity();
            double exportReceiptTotalPriceByProduct = exportReceiptDetailModel.getTotalPrice();
            System.out.println("Total: " + exportReceiptTotalPriceByProduct);
            InventoryDetailModel inventoryDetailModel = inventoryDetailByProductAndAcademicYear.getOrDefault(productId, null);
            // trường hợp năm của phiếu xuất chưa từng nhập
            System.out.println(inventoryDetailModel);
            if(inventoryDetailModel == null) {
                inventoryDetailModel = inventoryDetailByProductAndPreviousAcademicYear.getOrDefault(productId, null);
                // trường hợp trong năm trước cũng chưa nhập ==> tạo mới
                if (inventoryDetailModel == null) {
                    // thông báo sản phẩm này chưa từng được nhập trong 2 năm trở lại đây
                    throw new CanNotFoundException("Sản phẩm: " + exportReceiptDetailModel.getProductName() + " chưa từng được nhập trong 2 năm gần đây, vui lòng kiểm tra lại.");
                }
                // trường hợp số lượng tồn kho không đủ cho lần xuất này
                if (inventoryDetailModel.getQuantity() < actualQuantity) {
                    // thông báo sản productId trong kho không đủ
                    throw new StockUnderFlowException("Số lượng tồn kho của " + exportReceiptDetailModel.getProductName() + " không đủ để xuất kho, hiện chỉ còn " + inventoryDetailModel.getQuantity() + " sản phẩm, vui lòng nhập thêm.");
                }
                // trường hợp sản phẩm này được nhập từ năm ngoái, năm nay chưa từng được nhập
                inventoryDetailModel.setQuantity(inventoryDetailModel.getQuantity() - actualQuantity);
                inventoryDetailModel.setTotalPrice(inventoryDetailModel.getTotalPrice() - exportReceiptTotalPriceByProduct);
                inventoryDetailModelsToInsert.add(inventoryDetailModel);
            }
            // trường hợp sản phẩm này đã từng được nhập ==> có tồn kho của năm nay
            else {
                if (inventoryDetailModel.getQuantity() < actualQuantity) {
                    // thông báo sản productId trong kho không đủ
                    throw new StockUnderFlowException("Số lượng tồn kho của " + exportReceiptDetailModel.getProductName() + " không đủ để xuất kho, hiện chỉ còn " + inventoryDetailModel.getQuantity() + " sản phẩm, vui lòng nhập thêm.");
                }
                inventoryDetailModel.setQuantity(inventoryDetailModel.getQuantity() - actualQuantity);
                inventoryDetailModel.setTotalPrice(inventoryDetailModel.getTotalPrice() - exportReceiptTotalPriceByProduct);
                inventoryDetailModelsToUpdate.add(inventoryDetailModel);
            }
        }
        if(!inventoryDetailModelsToInsert.isEmpty()) {
            inventoryDetailService.save(inventoryDetailModelsToInsert);
        }
        if(!inventoryDetailModelsToUpdate.isEmpty()) {
            inventoryDetailService.update(inventoryDetailModelsToUpdate);
        }
    }

    private void updateExportPrice(List<Long> productIds, List<ExportReceiptDetailModel> exportReceiptDetailModels, LocalDateTime exportDate, HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap) throws DaoException {
        // danh sách đơn giá xuất theo từng sản phẩm tính từ ngày nhập của phiếu xuất trở đi
        Map<Long, List<ExportPriceModel>> exportPriceModelsByProductAfterExportDate = exportPriceService.findAllByProductAndMinTime(productIds, exportDate);
        List<ExportPriceModel> exportPriceModelsToUpdate = new ArrayList<>();
        Map<Long, ExportPriceModel> exportPriceModelByIdMap = new HashMap<>();
//        List<Long> exportPriceIds = exportReceiptDetailModels.stream().map(
//                ExportReceiptDetailModel::getExportPriceId
//        ).collect(Collectors.toList());

        // danh sách đơn giá của từng sản phẩm trong phiếu nhập
        // đánh dấu theo id của đơn giá : đơn giá
//        Map<Long, Double> priceById = exportPriceService.findPriceById(exportPriceIds);

        // duyệt toàn bộ sản phẩm trong phiếu nhập cần sửa
        for(ExportReceiptDetailModel exportReceiptDetailModel : exportReceiptDetailModels) {
            long productId = exportReceiptDetailModel.getProductId();
            List<ExportPriceModel> exportPriceModels = exportPriceModelsByProductAfterExportDate.getOrDefault(productId, null);
            // trường hợp sản phẩm này sau đó chưa được nhập thêm ==> không cần cập nhật lại đơn giá
            if(exportPriceModels == null) {
                continue;
            }
            // cập nhật lại toàn bộ đơn giá cho sản phẩm hiện tại
            for (ExportPriceModel exportPriceModel : exportPriceModels) {
                // tính lại số lượng tồn kho của từng đơn giá
                int newQuantityInStock = exportPriceModel.getQuantityInStock() - changeQuantityByProductMap.getOrDefault(productId, 0);
                // tính bằng cách lấy tổng tiền trong kho khi tính đơn giá - tổng tiền thay đổi
                // của sản phẩm trong phiếu xuất hiện tại
                double newTotalPriceInStock = exportPriceModel.getTotalPriceInStock() - changeTotalPriceByProductMap.getOrDefault(productId, 0.0);
                ExportPriceModel exportPriceModelToUpdate = calculateUnitPriceOfProduct(
                        newQuantityInStock,
                        newTotalPriceInStock,
                        exportPriceModel.getTotalImportPrice(), // tổng tiền nhập không đổi
                        exportPriceModel.getQuantityImported(), // tổng số lượng nhập không thay đổi
                        exportPriceModel
                );
                exportPriceModelsToUpdate.add(exportPriceModelToUpdate);
                exportPriceModelByIdMap.put(exportPriceModel.getId(), exportPriceModelToUpdate);
            }
        }
        if(!exportPriceModelsToUpdate.isEmpty()) {
            exportPriceService.update(exportPriceModelsToUpdate);
        }
    }

    public void updateExportReceipt(ExportReceiptModel newExportReceipt, ExportReceiptModel oldExportReceipt, List<ExportReceiptDetailModelTable> exportReceiptDetailModelTables, HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap) throws DaoException {
        try {
            exportReceiptService.update(newExportReceipt);
            List<ExportReceiptDetailModel> exportReceiptDetailModels = GenericConverterBetweenModelAndTableData.convertToListModel(
                    exportReceiptDetailModelTables, ExportReceiptDetailModelTableMapper.INSTANCE::fromViewModelToModel);
            List<Long> productIds = exportReceiptDetailModelTables.stream().map(ExportReceiptDetailModelTable::getProductId).collect(Collectors.toList());
            int academicYear = getYearOfExportReceipt(oldExportReceipt.getCreateAt());
            // cập nhật tồn kho
            updateInventory(exportReceiptDetailModels, academicYear, productIds);
            // cập nhật giá xuất
            LocalDateTime exportDate = LocalDateTime.parse(oldExportReceipt.getCreateAt(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            updateExportPrice(productIds, exportReceiptDetailModels, exportDate, changeQuantityByProductMap, changeTotalPriceByProductMap);
        } catch (Exception e) {
            // rollback update export receipt
            if (oldExportReceipt != null) {
                exportReceiptService.update(oldExportReceipt);
            }
            // ??????? có thể cân rollback tồn kho nếu đơn giá update lỗi
            throw e;
        }
    }

    public void deleteExportReceipt(ExportReceiptModel exportReceiptModel, List<ExportReceiptDetailModelTable> exportReceiptDetailModelTables) throws DaoException {
        HashMap<Long, Integer> changeQuantityByProductMap = new HashMap<>();
        HashMap<Long, Double> changeTotalPriceByProductMap = new HashMap<>();
        List<Long> productIds = new ArrayList<>();
        exportReceiptDetailModelTables.forEach(exportReceiptDetailModelTable -> {
            changeQuantityByProductMap.put(exportReceiptDetailModelTable.getProductId(), (-1) * exportReceiptDetailModelTable.getActualQuantity());
            changeTotalPriceByProductMap.put(exportReceiptDetailModelTable.getProductId(), (-1) * exportReceiptDetailModelTable.getTotalPrice());
            productIds.add(exportReceiptDetailModelTable.getProductId());
        });
        List<ExportReceiptDetailModel> exportReceiptDetailModels = GenericConverterBetweenModelAndTableData.convertToListModel(
                exportReceiptDetailModelTables, ExportReceiptDetailModelTableMapper.INSTANCE::fromViewModelToModel
        );
        // cập nhật tồn kho
        int academicYear = getYearOfExportReceipt(exportReceiptModel.getCreateAt());
        LocalDateTime exportDate = LocalDateTime.parse(exportReceiptModel.getCreateAt(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        updateInventory(exportReceiptDetailModels, academicYear, productIds);

        // cập nhật lại đơn giá
        updateExportPrice(productIds, exportReceiptDetailModels, exportDate, changeQuantityByProductMap, changeTotalPriceByProductMap);
        List<Long> exportReceiptIds = new ArrayList<>();
        exportReceiptIds.add(exportReceiptModel.getId());
        exportReceiptService.deleteByIds(exportReceiptIds);
    }

    private int getYearOfExportReceipt(String createAt) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime date = LocalDateTime.parse(createAt, formatter);
            return date.getYear();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public int findQuantityInStockByProductIdAndAcademicYear(long productId, int academicYear) throws DaoException{
        int[] yearsToTry = { academicYear, academicYear - 1 };

        for (int year : yearsToTry) {
            try {
                return inventoryDetailService.findQuantityInStockByProductIdAndAcademicYear(productId, year);
            } catch (CanNotFoundException e) {
                System.out.println(String.format("Quantity not found for productId={%d} in academicYear={%d}: {%s}", productId, year, e.getMessage()));
            }
        }
        return 0;
    }

    // Đơn giá mới = (Thành tiên tồn kho + Thành tiền nhập) / (Số lượng tồn kho + Số lượng nhập)
    private ExportPriceModel calculateUnitPriceOfProduct(int quantityInStock, double totalPriceInStock, double totalPriceImported, int quantityImported, ExportPriceModel exportPriceModel) {
        try {
//            if(exportPriceModel == null) {
//                exportPriceModel = new ExportPriceModel();
//                exportPriceModel.setProductId(productId);
//                exportPriceModel.setExportTime(importDate);
//                exportPriceModel.setImportReceiptId(importReceiptId);
//            }
            exportPriceModel.setQuantityInStock(quantityInStock);
            // tính giá xuất mới cho sản phẩm
            System.out.println(String.format("Giá xuất mới dựa trên tham số: Số lượng trong kho = %d, số lượng nhập = %d, Thành tien trong kho = %f, Thành tiền nhập = %f",
                    quantityInStock, quantityImported, totalPriceInStock, totalPriceImported));

            double newUnitPrice = Math.round((totalPriceInStock + totalPriceImported) / (quantityImported + quantityInStock));
            exportPriceModel.setExportPrice(newUnitPrice);
            exportPriceModel.setTotalPriceInStock(totalPriceInStock);
            return exportPriceModel;
        }
        catch (ArithmeticException e) {
            e.printStackTrace();
            throw new DivisionByZeroException("Số lượng tồn kho và số lượng nhập không hợp lệ, vui lòng kiểm tra lại.");
        }
    }
}
