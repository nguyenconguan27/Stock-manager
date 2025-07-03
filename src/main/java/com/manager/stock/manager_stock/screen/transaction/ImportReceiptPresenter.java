package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.exception.CanNotFoundException;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.exception.DivisionByZeroException;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptDetailModelMapper;
import com.manager.stock.manager_stock.model.*;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptModelTable;
import com.manager.stock.manager_stock.service.*;
import com.manager.stock.manager_stock.service.impl.*;
import com.manager.stock.manager_stock.utils.FormatMoney;
import com.manager.stock.manager_stock.utils.GenericConverterBetweenModelAndTableData;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptPresenter {
    private final IImportReceiptService importReceiptService;
    private final IImportReceiptDetailService importReceiptDetailService;
    private final IInventoryDetailService inventoryDetailService;
    private final IExportPriceService exportPriceService;
    private static ImportReceiptPresenter instance;
    private final ProductService productService;

    private ImportReceiptPresenter() {
        importReceiptService = ImportReceiptServiceImpl.getInstance();
        importReceiptDetailService = ImportReceiptDetailServiceImpl.getInstance();
        inventoryDetailService = InventoryDetailServiceImpl.getInstance();
        exportPriceService = ExportPriceServiceImpl.getInstance();
        productService = new ProductServiceImpl();
    }

    public static ImportReceiptPresenter getInstance() {
        if (instance == null) {
            instance = new ImportReceiptPresenter();
        }
        return instance;
    }

    public List<ImportReceiptModel> loadImportReceiptList(Optional<Integer> academicYear) throws DaoException {
        return importReceiptService.findAllByAcademicYear(academicYear);
    }

    public List<ImportReceiptDetailModel> loadImportReceiptDetailList(long importReceiptId) {
        List<ImportReceiptDetailModel> importReceiptDetailModels = importReceiptDetailService.findAllByImportReceiptId(importReceiptId);
        importReceiptDetailModels
                .forEach(importReceiptDetailModel -> {
                    System.out.println(importReceiptDetailModel);
                   importReceiptDetailModel.setUnitPriceFormat(FormatMoney.format(importReceiptDetailModel.getUnitPrice()));
                   importReceiptDetailModel.setTotalPriceFormat(FormatMoney.format(importReceiptDetailModel.getTotalPrice()));
                });
        return importReceiptDetailModels;
    }

    public List<ProductModel> loadAllProduct() {
        return productService.getAllProducts();
    }

    public ImportReceiptModel saveImportReceipt(ImportReceiptModel importReceiptModel, ObservableList<ImportReceiptDetailModelTable> importReceiptDetailModelsTable, HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap) throws DaoException{
        long importReceiptId = importReceiptService.save(importReceiptModel);
        importReceiptModel.setId(importReceiptId);
        List<ImportReceiptDetailModel> importReceiptDetailModels = GenericConverterBetweenModelAndTableData.convertToListModel(importReceiptDetailModelsTable, ImportReceiptDetailModelMapper.INSTANCE::fromViewModelToModel);
        try {
            importReceiptDetailService.save(importReceiptDetailModels, importReceiptId);
        }
        // rollback receipt(Xóa receipt khi lưu thành công) khi thêm mới chi tiết bị lỗi
        catch (Exception e) {
            importReceiptService.delete(importReceiptId);
            throw e;
        }
        // cập nhật tồn kho
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(importReceiptModel.getCreateAt(), formatter);
        int year = date.getYear();
        try {
            updateOrSaveInventoryDetail(year, importReceiptDetailModels, changeQuantityByProductMap, changeTotalPriceByProductMap);
        }
        catch (Exception e) {
            System.out.println("Cập nhật tồn kho lỗi rồi ==> rollback phiếu nhập và chi tiết phiếu nhập.");
            e.printStackTrace();
        }
        return importReceiptModel;
    }

    public void updateImportReceipt(ImportReceiptModel importReceiptModel, ImportReceiptModel oldImportReceiptModel, List<ImportReceiptDetailModelTable> importReceiptDetailModelTables, HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap) throws DaoException {
        importReceiptService.update(importReceiptModel);
        List<ImportReceiptDetailModelTable> newImportReceiptDetailModelTable = new ArrayList<>();
        List<ImportReceiptDetailModelTable> editImportReceiptDetailModelTable = new ArrayList<>();

        for(ImportReceiptDetailModelTable importReceiptDetailModelTable : importReceiptDetailModelTables) {
            if(importReceiptDetailModelTable.getId() == -1) {
                newImportReceiptDetailModelTable.add(importReceiptDetailModelTable);
            }
            else {
                editImportReceiptDetailModelTable.add(importReceiptDetailModelTable);
            }
        }

        List<ImportReceiptDetailModel> newImportReceiptDetailModel = GenericConverterBetweenModelAndTableData.convertToListModel(newImportReceiptDetailModelTable,
                ImportReceiptDetailModelMapper.INSTANCE::fromViewModelToModel);
        List<ImportReceiptDetailModel> editImportReceiptDetailModel = GenericConverterBetweenModelAndTableData.convertToListModel(editImportReceiptDetailModelTable,
                ImportReceiptDetailModelMapper.INSTANCE::fromViewModelToModel);

        try {
            if(!newImportReceiptDetailModel.isEmpty()) {
                importReceiptDetailService.save(newImportReceiptDetailModel, importReceiptModel.getId());
            }
            else if(!editImportReceiptDetailModel.isEmpty()) {
                importReceiptDetailService.update(editImportReceiptDetailModel);
            }
        }
        // rollback receipt khi cập nhật chi tiết bị lỗi
        catch (Exception e) {
            System.out.println(oldImportReceiptModel);
            importReceiptService.update(oldImportReceiptModel);
            throw e;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(importReceiptModel.getCreateAt(), formatter);
        int year = date.getYear();
        try {
            newImportReceiptDetailModel.addAll(editImportReceiptDetailModel);
            updateOrSaveInventoryDetail(year, newImportReceiptDetailModel, changeQuantityByProductMap, changeTotalPriceByProductMap);
        }
        catch (Exception e) {
            System.out.println("Cập nhật tồn kho lỗi rồi ==> rollback phiếu nhập và chi tiết phiếu nhập.");
            e.printStackTrace();
        }
    }

    private void updateOrSaveInventoryDetail(int academicYear, List<ImportReceiptDetailModel> importReceiptDetailModels, HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap) throws DaoException {
        // lấy ra tồn kho theo năm
        // Lấy danh dách product id có trong hóa đơn
        List<Long> productIds = importReceiptDetailModels.stream()
                                .map(ImportReceiptDetailModel::getProductId)
                                .collect(Collectors.toList());
        HashMap<Long, InventoryDetailModel> inventoryDetailModelMapCurrentYear = inventoryDetailService.findAllByAcademicYearAndProductId(academicYear, productIds);
        HashMap<Long, InventoryDetailModel> inventoryDetailModelMapPreviousYear = inventoryDetailService.findAllByAcademicYearAndProductId(academicYear-1, productIds);
        List<InventoryDetailModel> inventoryDetailModelsToInsert = new ArrayList<>();
        List<InventoryDetailModel> inventoryDetailModelsToUpdate = new ArrayList<>();
        List<ExportPriceModel> exportPriceModels = new ArrayList<>();
        for(ImportReceiptDetailModel importReceiptDetailModel : importReceiptDetailModels) {
            // lấy ra tồn kho của sản phẩm trong bảng tồn kho của năm hiện tại
            long productId = importReceiptDetailModel.getProductId();
            int actualQuantity = importReceiptDetailModel.getActualQuantity();
            double totalPrice = importReceiptDetailModel.getTotalPrice();
            InventoryDetailModel inventoryDetailModel = inventoryDetailModelMapCurrentYear.getOrDefault(productId, null);
            // Trường hợp sản phẩm này chưa từng được nhập trong năm nay (tồn kho trong năm đang không có)
            if(inventoryDetailModel == null) {
                // lấy tồn kho đầu năm(của năm trước) của sản phẩm
                inventoryDetailModel = inventoryDetailModelMapPreviousYear.getOrDefault(productId, null);
                // trong năm trước cùng chưa từng được nhập ==> sẽ tạo mới tồn kho của sản phẩm này trong năm hiện tại
                if(inventoryDetailModel == null) {
                    inventoryDetailModel = new InventoryDetailModel();
                    inventoryDetailModel.setProductId(productId);
                    inventoryDetailModel.setAcademicYear(academicYear);
                    inventoryDetailModel.setQuantity(actualQuantity);
                    inventoryDetailModel.setTotalPrice(totalPrice);
                    inventoryDetailModelsToInsert.add(inventoryDetailModel);
                    exportPriceModels.add(calculateUnitPriceOfProduct(inventoryDetailModel, totalPrice, actualQuantity));
                }
                else {
                    // tồn kho đầu năm của sản phẩm đang xét(product id)
                    inventoryDetailModel.setQuantity(inventoryDetailModel.getQuantity() + importReceiptDetailModel.getActualQuantity());
                    inventoryDetailModel.setTotalPrice(inventoryDetailModel.getTotalPrice() + importReceiptDetailModel.getTotalPrice());
                    inventoryDetailModel.setAcademicYear(academicYear);
                    inventoryDetailModelsToInsert.add(inventoryDetailModel);
                }
            }
            // trường hợp năm nay đã có tồn kho của sản phẩm rồi ==> cập nhật lại
            else {
                // cập nhật lại số lượng cũng như tổng tiền tồn kho của sản phẩm
                // trước khi cập nhật, xem sản phẩm này có phải là update thêm số lượng từ phiếu cũ hay không
                int changeQuantityByProduct = changeQuantityByProductMap.getOrDefault(productId, 0);
                int currentQuantityInStock = inventoryDetailModel.getQuantity() + changeQuantityByProduct;
                double changeTotalPriceByProduct = changeTotalPriceByProductMap.getOrDefault(productId, 0.0);
                double currentTotalPriceByProduct = inventoryDetailModel.getTotalPrice() + changeTotalPriceByProduct;

                inventoryDetailModel.setQuantity(currentQuantityInStock);
                inventoryDetailModel.setTotalPrice(currentTotalPriceByProduct);
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
    // Đơn giá mới = (Thành tiên tồn kho + Thành tiền nhập) / (Số lượng tồn kho + Số lượng nhập)
    private ExportPriceModel calculateUnitPriceOfProduct(InventoryDetailModel inventoryDetailModel, double totalPriceImported, int quantityImported) {
        try {
            ExportPriceModel exportPriceModel = new ExportPriceModel();
            exportPriceModel.setQuantityInStock(inventoryDetailModel.getQuantity());
            exportPriceModel.setExportTime(System.currentTimeMillis());
            exportPriceModel.setProductId(inventoryDetailModel.getProductId());
            exportPriceModel.setQuantityImported(quantityImported);
            exportPriceModel.setImportPrice(totalPriceImported);
            // tính giá xuất mới cho sản phẩm
            double newUnitPrice = (inventoryDetailModel.getTotalPrice() + totalPriceImported) / (quantityImported + inventoryDetailModel.getQuantity());
            exportPriceModel.setExportPrice(newUnitPrice);
            return exportPriceModel;
        }
        catch (ArithmeticException e) {
            throw new DivisionByZeroException("Số lượng tồn kho và số lượng nhập không hợp lệ, vui lòng kiểm tra lại.");
        }
    }
}
