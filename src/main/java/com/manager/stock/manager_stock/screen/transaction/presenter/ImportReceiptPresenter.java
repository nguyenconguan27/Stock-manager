package com.manager.stock.manager_stock.screen.transaction.presenter;

import com.manager.stock.manager_stock.exception.CanNotFoundException;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.exception.DivisionByZeroException;
import com.manager.stock.manager_stock.exception.StockUnderFlowException;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptDetailModelMapper;
import com.manager.stock.manager_stock.model.*;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndExportTimeAndExportPrice;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptModelTable;
import com.manager.stock.manager_stock.service.*;
import com.manager.stock.manager_stock.service.impl.*;
import com.manager.stock.manager_stock.utils.AlertUtils;
import com.manager.stock.manager_stock.utils.FormatMoney;
import com.manager.stock.manager_stock.utils.GenericConverterBetweenModelAndTableData;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptPresenter {
    private final IImportReceiptService importReceiptService;
    private final IImportReceiptDetailService importReceiptDetailService;
    private final IInventoryDetailService inventoryDetailService;
    private final IExportPriceService exportPriceService;
    private final IExportReceiptService exportReceiptService;
    private final IReceiptTransactionService receiptTransactionService;
    private final IExportReceiptDetailService exportReceiptDetailService;
    private static ImportReceiptPresenter instance;
    private final ProductService productService;
    private final DateTimeFormatter formatter;
    private final DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ImportReceiptPresenter() {
        importReceiptService = ImportReceiptServiceImpl.getInstance();
        importReceiptDetailService = ImportReceiptDetailServiceImpl.getInstance();
        inventoryDetailService = InventoryDetailServiceImpl.getInstance();
        exportPriceService = ExportPriceServiceImpl.getInstance();
        exportReceiptService = ExportReceiptServiceImpl.getInstance();
        receiptTransactionService = ReceiptTransactionServiceImpl.getInstance();
        exportReceiptDetailService = ExportReceiptDetailServiceImpl.getInstance();
        productService = new ProductServiceImpl();
        formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
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
                    double totalPrice = importReceiptDetailModel.getUnitPrice() * importReceiptDetailModel.getActualQuantity();
//                    System.out.println("Total price: " + totalPrice);
                    importReceiptDetailModel.setUnitPriceFormat(FormatMoney.format(importReceiptDetailModel.getUnitPrice()));
                    importReceiptDetailModel.setTotalPriceFormat(FormatMoney.format(totalPrice));
                    importReceiptDetailModel.setTotalPrice(totalPrice);
//                    System.out.println(importReceiptDetailModel);
                });
        return importReceiptDetailModels;
    }

    public List<ProductModel> loadAllProduct() {
        return productService.getAllProducts();
    }

    public void saveImportReceipt(ImportReceiptModel importReceiptModel, ObservableList<ImportReceiptDetailModelTable> importReceiptDetailModelsTable,
                                  HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap) throws DaoException, StockUnderFlowException{
        try {
            int academicYear = getYearOfImportReceipt(importReceiptModel.getCreateAt());
            importReceiptModel.setAcademicYear(academicYear);
            long importReceiptId = importReceiptService.save(importReceiptModel);
            importReceiptModel.setId(importReceiptId);
            List<ImportReceiptDetailModel> importReceiptDetailModels = GenericConverterBetweenModelAndTableData.convertToListModel(
                    importReceiptDetailModelsTable, ImportReceiptDetailModelMapper.INSTANCE::fromViewModelToModel);
            importReceiptDetailService.save(importReceiptDetailModels, importReceiptId);
            LocalDateTime importDate = LocalDateTime.parse(importReceiptModel.getCreateAt(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            List<Long> productIds = importReceiptDetailModels.stream()
                    .map(ImportReceiptDetailModel::getProductId)
                    .collect(Collectors.toList());
            if (importNextExportDate(productIds, importDate, importReceiptModel.getId(), changeQuantityByProductMap, changeTotalPriceByProductMap)) {
                importReceiptService.commit();
                return;
            }
            updateInventory(academicYear, importReceiptDetailModels, changeQuantityByProductMap, changeTotalPriceByProductMap, true, importDate, importReceiptId);
            LocalDateTime newImportDate = LocalDateTime.parse(importReceiptModel.getCreateAt(), formatter);
//            updateForeignExportDetail(importReceiptDetailModels, newImportDate, newImportDate, false);
            importReceiptService.commit();
        }
        catch (Exception e) {
            importReceiptService.rollback();
            e.printStackTrace();
        }
    }

    public void updateImportReceipt(ImportReceiptModel importReceiptModel, List<ImportReceiptDetailModelTable> importReceiptDetailModelTables,
                                    HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap,
                                    Set<Long> receiptDetailIds, String oldImportDateStr, List<ImportReceiptDetailModelTable> importReceiptDetails) throws DaoException, StockUnderFlowException {
        try {
            // cập nhật thông tin của phiếu nhập
            List<ImportReceiptDetailModel> allProductOfImportReceipt = GenericConverterBetweenModelAndTableData.convertToListModel(importReceiptDetails,
                    ImportReceiptDetailModelMapper.INSTANCE::fromViewModelToModel);
            LocalDateTime oldImportDate = LocalDateTime.parse(oldImportDateStr.trim(), formatter);
            LocalDateTime newImportDate = LocalDateTime.parse(importReceiptModel.getCreateAt().trim(), formatter);
            updateForeignExportDetail(allProductOfImportReceipt, newImportDate, oldImportDate);
            importReceiptService.update(importReceiptModel);

            if(!changeQuantityByProductMap.isEmpty() || !changeTotalPriceByProductMap.isEmpty()) {
                // danh sách sản phẩm thêm mới
                List<ImportReceiptDetailModelTable> newImportReceiptDetailModelTable = new ArrayList<>();
                // danh sách sản phẩm chỉnh sửa
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

                if(!newImportReceiptDetailModel.isEmpty()) {
                    importReceiptDetailService.save(newImportReceiptDetailModel, importReceiptModel.getId());
                }
                else if(!editImportReceiptDetailModel.isEmpty()) {
                    importReceiptDetailService.update(editImportReceiptDetailModel);
                }
                List<ImportReceiptDetailModel> importReceiptDetailModelsOverNewAndEdit = new ArrayList<>(newImportReceiptDetailModel);
                importReceiptDetailModelsOverNewAndEdit.addAll(editImportReceiptDetailModel);
                int year = getYearOfImportReceipt(importReceiptModel.getCreateAt());
                LocalDateTime importDate = LocalDateTime.parse(importReceiptModel.getCreateAt(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                updateInventory(year, importReceiptDetailModelsOverNewAndEdit, changeQuantityByProductMap, changeTotalPriceByProductMap, false, importDate, importReceiptModel.getId());
            }
            // xóa đi những chi tiết phiếu nhập đã bị xóa
            // danh sách sản phẩm bị xóa trong phiếu nhập
            if(!receiptDetailIds.isEmpty()) {
                importReceiptDetailService.deleteByIds(receiptDetailIds);
            }
            importReceiptService.commit();
        }
        catch (DaoException | StockUnderFlowException exception) {
            importReceiptService.rollback();
            exception.printStackTrace();
            throw exception;
        }
        catch (Exception e) {
            importReceiptService.rollback();
            e.printStackTrace();
        }
    }

    public ExportPriceModel exportPrice2Update(Long productId, ExportPriceModel current, ExportPriceModel pre,
                                               int quantity, double totalPrice, LocalDateTime time, Long importReceiptId) {
        if (current == null) {
            current = new ExportPriceModel();
            current.setProductId(productId);
            current.setExportTime(time);
            current.setImportReceiptId(importReceiptId);
            current.setQuantityImported(quantity);
            current.setTotalImportPrice(totalPrice);
            current.setQuantityInStock(pre.getQuantityInStock());
            current.setTotalPriceInStock(pre.getTotalPriceInStock());
        }
        double newUnitPrice = Math.round((current.getTotalPriceInStock() + current.getTotalImportPrice()) / (current.getQuantityInStock() + current.getQuantityImported()));
        current.setExportPrice(newUnitPrice);
        return current;
    }

    public void addExportPriceAndUpdateNext(LocalDateTime time, Long productId, long importReceiptId, int quantity, double totalPrice) {
        Map<Long, List<ExportPriceModel>> exportPriceMap = exportPriceService.findAllByProductAndMinTime(List.of(productId), time);
        ExportPriceModel pre = exportPriceService.findByProductAndLastTime(productId, time);
        ExportPriceModel next = exportPriceService.findByProductAndMinTime(productId, time);
        List<ExportReceiptDetailModel> preExportDetail;
        if (pre == null) {
            preExportDetail = exportReceiptDetailService.findByRangeTime(productId, LocalDateTime.of(2000, 1, 1, 0, 0), time);
        } else {
            preExportDetail = exportReceiptDetailService.findByRangeTime(productId, pre.getExportTime(), time);
        }
        List<ExportReceiptDetailModel> exportDetail2Update;
        if (next != null) {
            exportDetail2Update = exportReceiptDetailService.findByRangeTime(productId, time, next.getExportTime());
        } else {
            exportDetail2Update = exportReceiptDetailService.findByRangeTime(productId, time, LocalDateTime.now());
        }
        double totalPriceInTimeRangeExported = 0;
        int totalQuanInTimeRangeExported = 0;
        for (ExportReceiptDetailModel exportReceiptDetailModel : preExportDetail) {
            totalQuanInTimeRangeExported += exportReceiptDetailModel.getActualQuantity();
            totalPriceInTimeRangeExported += exportReceiptDetailModel.getTotalPrice();
        }
        ExportPriceModel exportPriceModel;
        // cập nhập pre nhưng không luu lại
        if(pre == null) {
            pre = new ExportPriceModel(0, productId,  LocalDateTime.of(2000, 1, 1, 0, 0, 0), 0,0,0,0,0);
        }
        pre.setQuantityInStock(pre.getQuantityInStock() + pre.getQuantityImported() - totalQuanInTimeRangeExported);
        pre.setTotalPriceInStock(pre.getTotalPriceInStock() - totalPriceInTimeRangeExported + pre.getTotalImportPrice());
        exportPriceModel = exportPrice2Update(productId, null, pre, quantity, totalPrice, time, importReceiptId);
        List<ExportPriceModel> exportPriceModelList = exportPriceMap.get(productId) == null ? new ArrayList<>() : exportPriceMap.get(productId);
        exportPriceModelList.add(0, exportPriceModel);
        long exportPriceId;

        if(pre.getExportTime().getYear() != exportPriceModel.getExportTime().getYear()) {
            List<ExportReceiptDetailModel> exportReceiptDetailModels = exportReceiptDetailService.findByRangeTime(
                    productId, pre.getExportTime(), exportPriceModel.getExportTime()
            );
            updateInventory(pre, exportReceiptDetailModels);
        }

        for (int i = 0; i < exportPriceModelList.size(); i++) {
            ExportPriceModel preExportPrice = exportPriceModelList.get(i);
            ExportPriceModel nextExportPrice = i < exportPriceModelList.size() - 1 ? exportPriceModelList.get(i + 1) : null;
            List<ExportReceiptDetailModel> exportReceiptDetailModels;
            if(i == exportPriceModelList.size() - 1) {
                exportReceiptDetailModels = exportReceiptDetailService.findByRangeTime(
                        productId, preExportPrice.getExportTime(), LocalDateTime.now()
                );
            } else {
                exportReceiptDetailModels = exportReceiptDetailService.findByRangeTime(
                        productId, preExportPrice.getExportTime(), nextExportPrice.getExportTime()
                );
            }

            for (ExportReceiptDetailModel exportReceiptDetailModel : exportReceiptDetailModels) {
                exportReceiptDetailModel.setOriginalUnitPrice(preExportPrice.getExportPrice());
                exportReceiptDetailModel.setDisplayUnitPrice(preExportPrice.getExportPrice());
                exportReceiptDetailModel.setTotalPrice(preExportPrice.getExportPrice() * exportReceiptDetailModel.getActualQuantity());
            }
            totalQuanInTimeRangeExported = 0; totalPriceInTimeRangeExported = 0;
            for (ExportReceiptDetailModel exportReceiptDetailModel : exportReceiptDetailModels) {
                totalQuanInTimeRangeExported += exportReceiptDetailModel.getActualQuantity();
                totalPriceInTimeRangeExported += exportReceiptDetailModel.getTotalPrice();
            }
            if(nextExportPrice != null) {
                nextExportPrice.setQuantityInStock(preExportPrice.getQuantityInStock() +
                        preExportPrice.getQuantityImported() - totalQuanInTimeRangeExported);
                nextExportPrice.setTotalPriceInStock(preExportPrice.getTotalPriceInStock() +
                        preExportPrice.getTotalImportPrice() - totalPriceInTimeRangeExported);
                exportPrice2Update(productId, nextExportPrice, null, 0, 0, time, importReceiptId);
            }
            ExportPriceModel exportPriceModel2Check = exportPriceService.findLastByProductIdAndYear(preExportPrice.getProductId(), preExportPrice.getExportTime().getYear());
            if(exportPriceModel2Check == null || exportPriceModel2Check.getId() == preExportPrice.getId()) {
                updateInventory(preExportPrice, exportReceiptDetailModels);
            }
        }

//
//        for (int i = 1; i < exportPriceModelList.size(); i++) {
//            ExportPriceModel current = exportPriceModelList.get(i);
//            ExportPriceModel preExportPrice = exportPriceModelList.get(i - 1);
//            List<ExportReceiptDetailModel> exportReceiptDetailModels = exportReceiptDetailService.findByRangeTime(
//                    productId, exportPriceModelList.get(i - 1).getExportTime(), current.getExportTime()
//            );
//            for (ExportReceiptDetailModel exportReceiptDetailModel : exportReceiptDetailModels) {
//                exportReceiptDetailModel.setOriginalUnitPrice(exportPriceModelList.get(i - 1).getExportPrice());
//                exportReceiptDetailModel.setDisplayUnitPrice(exportPriceModelList.get(i - 1).getExportPrice());
//                exportReceiptDetailModel.setTotalPrice(exportPriceModelList.get(i - 1).getExportPrice() * exportReceiptDetailModel.getActualQuantity());
//            }
//            totalQuanInTimeRangeExported = 0; totalPriceInTimeRangeExported = 0;
//            for (ExportReceiptDetailModel exportReceiptDetailModel : exportReceiptDetailModels) {
//                totalQuanInTimeRangeExported += exportReceiptDetailModel.getActualQuantity();
//                totalPriceInTimeRangeExported += exportReceiptDetailModel.getTotalPrice();
//            }
//            current.setQuantityInStock(exportPriceModelList.get(i - 1).getQuantityInStock() +
//                    exportPriceModelList.get(i - 1).getQuantityImported() - totalQuanInTimeRangeExported);
//            current.setTotalPriceInStock(exportPriceModelList.get(i - 1).getTotalPriceInStock() +
//                    exportPriceModelList.get(i - 1).getTotalImportPrice() - totalPriceInTimeRangeExported);
//
//            exportPrice2Update(productId, current, null, 0, 0, time, importReceiptId);
////
////            ExportPriceModel exportPriceModel2Check = exportPriceService.findLastByProductIdAndYear(preExportPrice.getProductId(), preExportPrice.getExportTime().getYear());
////            if(exportPriceModel2Check.getId() == pre.getId() || exportPriceModel2Check.getId() == preExportPrice.getId()) {
////                updateInventory(preExportPrice, exportReceiptDetailModels);
////            }
//        }
        exportPriceId = exportPriceService.save(exportPriceModel);
        exportPriceModelList.remove(0);
        if(!exportPriceModelList.isEmpty()) {
            exportPriceService.update(exportPriceModelList);
        }
        for (ExportReceiptDetailModel exportReceiptDetailModel : exportDetail2Update) {
            exportReceiptDetailModel.setExportPriceId(exportPriceId);
            exportReceiptDetailModel.setOriginalUnitPrice(exportPriceModel.getExportPrice());
            exportReceiptDetailModel.setDisplayUnitPrice(exportPriceModel.getExportPrice());
        }
        exportReceiptDetailService.update(exportDetail2Update);
    }

    public void updateInventory(ExportPriceModel preExportPrice, List<ExportReceiptDetailModel> exportReceiptDetailModels) {
        InventoryDetailModel inventoryDetailModel2Update = inventoryDetailService.findByMinYearAndProduct(preExportPrice.getProductId(), preExportPrice.getExportTime().getYear()).get(0);
        int totalQExported = 0;
        for(ExportReceiptDetailModel exportReceiptDetailModel: exportReceiptDetailModels) {
            ExportReceiptModel exportReceiptModel = exportReceiptService.findById(exportReceiptDetailModel.getExportReceiptId());
            if(exportReceiptModel.getAcademicYear() == inventoryDetailModel2Update.getAcademicYear()) {
                totalQExported += exportReceiptDetailModel.getActualQuantity();
            }
        }
        inventoryDetailModel2Update.setQuantity(preExportPrice.getQuantityImported() + preExportPrice.getQuantityInStock() - totalQExported);
        inventoryDetailModel2Update.setTotalPrice(inventoryDetailModel2Update.getQuantity() * preExportPrice.getExportPrice());
        inventoryDetailService.update(List.of(inventoryDetailModel2Update));
    }

    public boolean importNextExportDate(List<Long> productIds, LocalDateTime time, long importReceiptId,
                                        HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap) {
        Map<Long, List<ExportReceiptDetailModel>> detailModelMap = exportReceiptDetailService.findAllByProductAndMinTime(productIds, time);
        Map<Long, List<ExportPriceModel>> exportPriceMap = exportPriceService.findAllByProductAndMinTime(productIds, time);
        if ((detailModelMap == null || detailModelMap.isEmpty()) && (exportPriceMap == null || exportPriceMap.isEmpty())) {
            return false;
        }
//        if(detailModelMap != null && !detailModelMap.isEmpty()) {
//            for (Map.Entry<Long, List<ExportReceiptDetailModel>> entry : detailModelMap.entrySet()) {
//                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
//                    addExportPriceAndUpdateNext(time, entry.getKey(), importReceiptId,
//                            changeQuantityByProductMap.get(entry.getKey()), changeTotalPriceByProductMap.get(entry.getKey()));
//                    updateInventory(entry.getKey(), time, changeQuantityByProductMap.get(entry.getKey()), changeTotalPriceByProductMap.get(entry.getKey()));
//                }
//            }
//        }
//        else {
//            for(Map.Entry<Long, List<ExportPriceModel>> entry: exportPriceMap.entrySet()) {
//                addExportPriceAndUpdateNext(time, entry.getKey(), importReceiptId,
//                        changeQuantityByProductMap.get(entry.getKey()), changeTotalPriceByProductMap.get(entry.getKey()));
//                updateInventory(entry.getKey(), time, changeQuantityByProductMap.get(entry.getKey()), changeTotalPriceByProductMap.get(entry.getKey()));
//            }
//        }

        for (Long productId : productIds) {
            addExportPriceAndUpdateNext(time, productId, importReceiptId,
                    changeQuantityByProductMap.get(productId), changeTotalPriceByProductMap.get(productId));
        }
        return true;
    }

    // cập nhật hoặc thêm mới tồn kho và đơn giá
    private void updateInventory(int academicYear, List<ImportReceiptDetailModel> importReceiptDetailModels, HashMap<Long, Integer> changeQuantityByProductMap,
                                 HashMap<Long, Double> changeTotalPriceByProductMap, boolean isInsert, LocalDateTime importDate,
                                 long importReceiptId) throws DaoException, StockUnderFlowException {
        // lấy ra tồn kho theo năm
        // Lấy danh dách product id có trong hóa đơn
        List<Long> productIds = importReceiptDetailModels.stream()
                                .map(ImportReceiptDetailModel::getProductId)
                                .collect(Collectors.toList());

        HashMap<Long, InventoryDetailModel> inventoryDetailModelMapCurrentYear = inventoryDetailService.findAllByAcademicYearAndProductId(academicYear, productIds);
        HashMap<Long, InventoryDetailModel> inventoryDetailModelMapPreviousYear = inventoryDetailService.findAllByAcademicYearAndProductId(academicYear-1, productIds);

        List<InventoryDetailModel> inventoryDetailModelsToInsert = new ArrayList<>();
        List<InventoryDetailModel> inventoryDetailModelsToUpdate = new ArrayList<>();
        List<ExportPriceModel> exportPriceModelsToInsert = new ArrayList<>();

        // duyệt danh sách chi tiết phiếu nhập cần sửa hoặc thêm mới
        for(ImportReceiptDetailModel importReceiptDetailModel : importReceiptDetailModels) {
            // lấy ra tồn kho của sản phẩm trong bảng tồn kho của năm hiện tại
            long productId = importReceiptDetailModel.getProductId();
            int actualQuantity = importReceiptDetailModel.getActualQuantity();
            double totalPrice = importReceiptDetailModel.getTotalPrice();
            InventoryDetailModel inventoryDetailModel = inventoryDetailModelMapCurrentYear.getOrDefault(productId, null);
            // Trường hợp sản phẩm này chưa từng được nhập trong năm nay (tồn kho trong năm đang không có)
            // ==> chưa có đơn giá mới ==> chỉ có thể insert đơn giá mới
            if(inventoryDetailModel == null) {
                // lấy tồn kho đầu năm(tồn kho cuối kì của năm trước) của sản phẩm
//                inventoryDetailModel = inventoryDetailModelMapPreviousYear.getOrDefault(productId, null);
                for(int i = academicYear - 1; i >= 2020; i--) {
                    inventoryDetailModel = inventoryDetailService.findByYearAndProduct(productId, i);
                    if(inventoryDetailModel != null) break;
                }
                // trong năm trước cũng chưa từng được nhập ==> sẽ tạo mới tồn kho của sản phẩm này trong năm hiện tại
//                ExportPriceModel exportPriceModel = new ExportPriceModel();
                if(inventoryDetailModel == null) {
                    inventoryDetailModel = new InventoryDetailModel();
                    inventoryDetailModel.setProductId(productId);
                    // tính lại giá xuất trước khi update lại tồn kho
                    // 2 năm gần đây không có nhập hàng ==> không có tồn kho của năm hiện tại ==> đơn gia mới tính bằng giá nhập / số lượng
                    exportPriceModelsToInsert.add(calculateUnitPriceOfProduct(
                            productId, 0, 0, totalPrice, actualQuantity, null, importReceiptId, importDate
                    ));
                    inventoryDetailModel.setAcademicYear(academicYear);
                    inventoryDetailModel.setQuantity(actualQuantity);
                    inventoryDetailModel.setTotalPrice(totalPrice);
                    inventoryDetailModelsToInsert.add(inventoryDetailModel);
                }
                else {
                    // tồn kho đầu năm của sản phẩm đang xét(product id)
                    // đây là lần đầu tiên nhập hàng trong năm
                    exportPriceModelsToInsert.add(calculateUnitPriceOfProduct(
                            productId, inventoryDetailModel.getQuantity(), inventoryDetailModel.getTotalPrice(), totalPrice, actualQuantity, null, importReceiptId, importDate
                    ));
                    inventoryDetailModel.setQuantity(inventoryDetailModel.getQuantity() + importReceiptDetailModel.getActualQuantity());
                    inventoryDetailModel.setTotalPrice(inventoryDetailModel.getTotalPrice() + importReceiptDetailModel.getTotalPrice());
                    inventoryDetailModel.setAcademicYear(academicYear);
                    inventoryDetailModelsToInsert.add(inventoryDetailModel);
                }
                // cập nhật lại toàn bộ phiếu xuất
            }
            // trường hợp năm nay đã có tồn kho của sản phẩm rồi ==> cập nhật lại
            // ==> chắc chắn có đơn giá mới rồi
            // ==> cần cập nhật lại đơn giá đối với trường hợp cập nhật phiếu nhập
            // và thêm mới đơn giá đối với thêm mới phiếu nhập
            else {
                // cập nhật lại số lượng cũng như tổng tiền tồn kho của sản phẩm
                // trước khi cập nhật, xem sản phẩm này có phải là update thêm số lượng từ phiếu cũ hay không
                int changeQuantityByProduct = changeQuantityByProductMap.getOrDefault(productId, 0); // số lượng nhập thêm
                int currentQuantityInStock = inventoryDetailModel.getQuantity() + changeQuantityByProduct;
                double changeTotalPriceByProduct = changeTotalPriceByProductMap.getOrDefault(productId, 0.0); // giá nhập thêm (quantity_imported)
                double currentTotalPriceByProduct = inventoryDetailModel.getTotalPrice() + changeTotalPriceByProduct;

                // trường hợp cập nhật lại mà laàm cho tồn kho âm ==> không cho cập nhật
                if(currentQuantityInStock < 0) {
                    throw new StockUnderFlowException(String.format("Sản phẩm %s đã được xuất, số lượng trong kho hiện tại không đủ so với số lượng nhập hiện tại, không thể xóa sản phẩm khỏi phiếu xuất.", importReceiptDetailModel.getProductName()));
                }
                // cập nhật lại giá xuất theo đúng số lượng nhập thêm vào
                // thêm phiếu nhâập ==> tạo mới đơn giá xuất
                if(isInsert) {
                    // trường hợp thêm mới phiếu nhập ==> thêm mới đơn giá
                    exportPriceModelsToInsert.add(calculateUnitPriceOfProduct(
                            productId,
                            inventoryDetailModel.getQuantity(),
                            inventoryDetailModel.getTotalPrice(),
                            totalPrice, actualQuantity, null,
                            importReceiptId,
                            importDate
                    ));
                }
                // sửa phiếu nhập
                else {
                    updateExportPrice(productIds, importReceiptDetailModels, importDate, changeQuantityByProductMap, importReceiptId);
                }
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
        if(!exportPriceModelsToInsert.isEmpty()) {
            exportPriceService.save(exportPriceModelsToInsert);
        }
    }

    // cập nhật hoặc thêm mới đơnn giá
    private void updateExportPrice(List<Long> productIds, List<ImportReceiptDetailModel> importReceiptDetailModels,
                                   LocalDateTime importDate, HashMap<Long, Integer> changeQuantityByProductMap, long importReceiptId) throws DaoException {
        // danh sách đơn giá xuất theo từng sản phẩm tính từ ngày nhập của phiếu nhạp trở đi
        Map<Long, List<ExportPriceModel>> exportPriceModelsByProductAfterImportDate = exportPriceService.findAllByProductAndMinTime(productIds, importDate);
        List<ExportPriceModel> exportPriceModelsToUpdate = new ArrayList<>();
        Map<Long, ExportPriceModel> exportPriceModelByIdMap = new HashMap<>();
        // duyệt toàn bộ sản phẩm trong phiếu nhập cần sửa
        for(ImportReceiptDetailModel importReceiptDetailModel : importReceiptDetailModels) {
            long productId = importReceiptDetailModel.getProductId();
            // lấy ra số lượng sản phẩm thay đổi trong phiếu nhập đang sửa
            // ví dụ: ban đầu nhập 100 -> giờ sửa chỉ nhập 30 ==> changeQuantityByProductMap.getOrDefault(productId, 0) = 30
            int changeQuantity = changeQuantityByProductMap.getOrDefault(productId, 0); // 30
            int currentActualQuantity = importReceiptDetailModel.getActualQuantity(); // 30
            List<ExportPriceModel> exportPriceModels = exportPriceModelsByProductAfterImportDate.getOrDefault(productId, null);
            // trường hợp sản phẩm này được thêm mới vào phiếu nhập nhưng chưa có đơn giá xuất mới nào sau đó
            if(exportPriceModels == null) {

            }
            else {
                // cập nhật lại toàn bộ đơn giá cho sản phẩm hiện tại
                int quantityDiffFromPrevReceipt = 0;
                double totalPriceDiffFromPrevReceipt = 0;
                for (ExportPriceModel exportPriceModel : exportPriceModels) {
                    // số lượng và thành tiền thay đổi của sản phẩm trong hóa đơn nhập đang chỉnh sửa này so với ban đầu
                    if (exportPriceModel.getImportReceiptId() == importReceiptId) {
                        quantityDiffFromPrevReceipt = importReceiptDetailModel.getActualQuantity() - exportPriceModel.getQuantityImported();
                        totalPriceDiffFromPrevReceipt = importReceiptDetailModel.getTotalPrice() - exportPriceModel.getTotalImportPrice();
                        // tính lại đơn giá của hóa đơn nhập cùng ngày
                        ExportPriceModel exportPriceModelToUpdate = calculateUnitPriceOfProduct(
                                productId,
                                exportPriceModel.getQuantityInStock(),
                                exportPriceModel.getTotalPriceInStock(),
                                importReceiptDetailModel.getTotalPrice(), // thanhf tieenf nhaap
                                importReceiptDetailModel.getActualQuantity(), // soos luoqwngj nhaap,
                                exportPriceModel,
                                importReceiptId,
                                importDate
                        );
                        exportPriceModelsToUpdate.add(exportPriceModelToUpdate);
                        exportPriceModelByIdMap.put(exportPriceModel.getId(), exportPriceModelToUpdate);
                    } else {
                        int newQuantityInStock = exportPriceModel.getQuantityInStock() + quantityDiffFromPrevReceipt;
                        double newTotalPriceInStock = exportPriceModel.getTotalPriceInStock() + totalPriceDiffFromPrevReceipt;
                        ExportPriceModel exportPriceModelToUpdate = calculateUnitPriceOfProduct(
                                productId,
                                newQuantityInStock,
                                newTotalPriceInStock,
                                exportPriceModel.getTotalImportPrice(),
                                exportPriceModel.getQuantityImported(),
                                exportPriceModel,
                                importReceiptId,
                                importDate
                        );
                        exportPriceModelsToUpdate.add(exportPriceModelToUpdate);
                        exportPriceModelByIdMap.put(exportPriceModel.getId(), exportPriceModelToUpdate);
                    }
                }
            }
        }
        if(!exportPriceModelsToUpdate.isEmpty()) {
            exportPriceService.update(exportPriceModelsToUpdate);
        }
        updateExportReceiptByProduct(importReceiptDetailModels, importDate, productIds, exportPriceModelByIdMap);
    }

    // Cập nhật lại trạng thái và message của phiếu xuất
    private void updateExportReceiptByProduct(List<ImportReceiptDetailModel> importReceiptDetailModels, LocalDateTime importDate, List<Long> productIds, Map<Long, ExportPriceModel> exportPriceModelByIdMap) {
        // danh sách phiếu xuất chi tiết theo sản phẩm tính từ ngày nhập của phiếu nhập trở đi
        Map<Long, List<ExportReceiptDetailModel>> exportReceiptDetailModelsByProductAfterImportDateMap = exportReceiptDetailService.findAllByProductAndMinTime(productIds, importDate);
        List<ExportReceiptDetailModel> exportReceiptDetailModelsToUpdate = new ArrayList<>();
        for(ImportReceiptDetailModel importReceiptDetailModel : importReceiptDetailModels) {
            long productId = importReceiptDetailModel.getProductId();
            List<ExportReceiptDetailModel> exportReceiptDetailModelsByProductAfterImportDate = exportReceiptDetailModelsByProductAfterImportDateMap.getOrDefault(productId, null);
            // trường hợp sản phẩm này chưa có phiếu xuất nào => không cần cập nhật
            if(exportReceiptDetailModelsByProductAfterImportDate == null) continue;
            for (ExportReceiptDetailModel exportReceiptDetailModel : exportReceiptDetailModelsByProductAfterImportDate) {
                // cập nhật lại số lượng theo tồn kho và đơn giá mới
                // 1. lấy ra đơn giá mới được cập nhật
                ExportPriceModel exportPriceModel = exportPriceModelByIdMap.getOrDefault(exportReceiptDetailModel.getExportPriceId(), null);
                // trường hợp đơn giá của sản phẩm không thay đổi
                if(exportPriceModel == null) continue;
                // 2. Kiểm tra xem số lượng xuất này so với số lượng tồn kho tại thời điểm của đơn giá
                int actualQuantityOfReceipt = exportReceiptDetailModel.getActualQuantity();
                int quantityInStockOfExportDate = exportPriceModel.getQuantityInStock();
                if(actualQuantityOfReceipt > quantityInStockOfExportDate) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    exportReceiptDetailModel.setActualQuantity(0);
                    StringBuilder message = new StringBuilder();
                    message.append("Khi thay đổi số lượng của sản phẩm: " + exportReceiptDetailModel.getProductCode());
                    message.append(", Tại thời điểm: " + formatter.format(LocalDateTime.now()));
                    message.append(", Thì số lượng tồn kho chỉ còn lại: ").append(quantityInStockOfExportDate);
                    message.append(", Mà số lượng xuất lại là: ").append(actualQuantityOfReceipt);
                    message.append("==> Phiếu xuất này không hợp lệ.");
                    exportReceiptDetailModel.setMessage(message.toString());
                    exportReceiptDetailModel.setStatus(0);
                    exportReceiptDetailModelsToUpdate.add(exportReceiptDetailModel);
                }
            }
        }
        if(!exportReceiptDetailModelsToUpdate.isEmpty()) {
            exportReceiptDetailService.update(exportReceiptDetailModelsToUpdate);
        }
    }

    // Đơn giá mới = (Thành tiên tồn kho + Thành tiền nhập) / (Số lượng tồn kho + Số lượng nhập)
    private ExportPriceModel calculateUnitPriceOfProduct(long productId, int quantityInStock, double totalPriceInStock,
                                                         double totalPriceImported, int quantityImported,
                                                         ExportPriceModel exportPriceModel, long importReceiptId,
                                                         LocalDateTime importDate) {
        try {
            if(exportPriceModel == null) {
                exportPriceModel = new ExportPriceModel();
                exportPriceModel.setProductId(productId);
                exportPriceModel.setExportTime(importDate);
                exportPriceModel.setImportReceiptId(importReceiptId);
            }
            exportPriceModel.setQuantityInStock(quantityInStock);
            exportPriceModel.setQuantityImported(quantityImported);
            exportPriceModel.setTotalImportPrice(totalPriceImported);
            // tính giá xuất mới cho sản phẩm

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

    private int getYearOfImportReceipt(String createAt) {
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

    public boolean deleteImportReceipt(ImportReceiptModelTable importReceiptModelTable) throws DaoException, StockUnderFlowException {
        try {
            int academicYear = getYearOfImportReceipt(importReceiptModelTable.getCreateAt());
            if (academicYear <= 0) {
                AlertUtils.alert("Không xác định được năm học của phiếu nhập, vui lòng kiểm tra lại ngày tạo.", "ERROR", "Lỗi dữ liệu", "");
                return false;
            }
            // xóa phiếu nhập mà làm âm tồn kho ==> không cho xóa
            // lấy danh sách sản phẩm trong phiếu nhập
            List<ImportReceiptDetailModel> importReceiptDetailModels = importReceiptDetailService.findAllByImportReceiptId(importReceiptModelTable.getId());
            HashMap<Long, Integer> changeQuantityMap = new HashMap<>();
            HashMap<Long, Double> changeTotalPriceMap = new HashMap<>();
            importReceiptDetailModels.forEach(importReceiptDetailModel -> {
                changeQuantityMap.put(importReceiptDetailModel.getProductId(), (-1) * importReceiptDetailModel.getActualQuantity());
                changeTotalPriceMap.put(importReceiptDetailModel.getProductId(), (-1) * importReceiptDetailModel.getActualQuantity() * importReceiptDetailModel.getUnitPrice());
                importReceiptDetailModel.setTotalPrice(0.0);
                importReceiptDetailModel.setActualQuantity(0);
            });
            // cập nhật tồn kho
            LocalDateTime importDate = LocalDateTime.parse(importReceiptModelTable.getCreateAt(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            updateInventory(academicYear, importReceiptDetailModels, changeQuantityMap, changeTotalPriceMap, false, importDate, importReceiptModelTable.getId());

            updateForeignExportDetail(importReceiptDetailModels, null, importDate);
            // cập nhật tồn kho + giá xuất thành công ==> xóa phiếu nhập
            importReceiptService.delete(importReceiptModelTable.getId());

            importReceiptService.commit();
            return true;
        }
        catch (Exception e) {
            importReceiptService.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public int findQuantityInStockByProductIdAndAcademicYear(long productId, int academicYear) throws DaoException{
        List<Integer> yearsToTry = new ArrayList<>();
        for(int i = academicYear; i >= 2020; i--)
            yearsToTry.add(i);
        for (int year : yearsToTry) {
            try {
                return inventoryDetailService.findQuantityInStockByProductIdAndAcademicYear(productId, year);
            } catch (CanNotFoundException e) {
            }
        }
        return 0;
    }

    // cập nhật lại đơn giá cho phiếu xuất trong trường hợp xóa phiếu nhập hoặc sửa ngày phiếu nhập hoặc xóa sản phẩm trong phiêếu nhập
    // hoặc thêm mới phiếu nhập vào giữa phiếu xuất + phiếu nhập
    private void updateForeignExportDetail(List<ImportReceiptDetailModel> importReceiptDetailModels, LocalDateTime newImportDate, LocalDateTime oldImportDate) throws DaoException, StockUnderFlowException {
        // lấy ra năm của ngày cũ và ngày mới
        int oldYear = oldImportDate.getYear();
        int newYear = newImportDate == null ? oldImportDate.getYear() : newImportDate.getYear();
        // 1. Lấy ra danh sách export_receipt_detail có ngày >= ngày của phiếu nhập
        for(ImportReceiptDetailModel importReceiptDetailModel : importReceiptDetailModels) {
            long productId = importReceiptDetailModel.getProductId();
            // truong hop xoa phieu nhap(newDate = null) ==> gan lai newDate = ngay xuat moi nhat
            if(newImportDate == null) {
                newImportDate = LocalDateTime.parse(exportReceiptService.findLatestCreatedByProduct(productId), formatter).plusMinutes(10);
            }
            // đơn giá của ngày (old - 1)
            ExportPriceIdAndExportTimeAndExportPrice beforeOldDayExportPriceInfo = exportPriceService.findByProductIdAndMaxTimeByImportDate(productId, oldImportDate, oldImportDate);
            // lấy đơn giá của ngày (new - 1)
            ExportPriceIdAndExportTimeAndExportPrice beforeNewDayExportPriceInfo = exportPriceService.findByProductIdAndMaxTimeByImportDate(productId, newImportDate, oldImportDate);
            // đơn giá của ngày mới và ngày cũ (đơn giá 2 ngày này thực chất là cùng 1 giá trị)
            ExportPriceIdAndExportTimeAndExportPrice newDayExportPriceInfo = exportPriceService.findByProductIdAndImportDate(productId, oldImportDate);
            // TH chuyen tu ngay be len ngay lớn
            double totalPriceBeforeUpdate = 0;
            double totalPriceAfterUpdate = 0;
            long totalQuantityExported = 0;
            // truong hop chuyen tu ngay be qua ngay lon
            if(newImportDate.isAfter(oldImportDate)) {
                // 1. Lấy danh sách các ngày của phiếu nhập trong khoảng từ ngày (old - 1) -> new
                List<LocalDateTime> importDatesInRange = exportPriceService.findAllExportTimeByProductAndBetweenImportDates(beforeOldDayExportPriceInfo.exportTime(), newImportDate, oldImportDate, productId);
                Map<Integer, Double> totalPriceBeforeUpdateMap = new HashMap<>();
                // 2. check điều kiện trong từng khoảng ngày nhập
                importDatesInRange.add(newImportDate);
                for(int i = 0; i < importDatesInRange.size() - 1; i++) {
                    // lấy tổng số lượng tồn kho + đã nhập theo ngày của phiếu nhập i
                    long totalQuantityByImportDate = exportPriceService.calculateTotalQuantityImportAndQuantityInStockByImportDateAndProduct(productId, importDatesInRange.get(i));
                    // tính tổng số lượng đã xuất trong khoảng ngày thứ i và i+1
                    if(i > 0) {
                        totalQuantityByImportDate -= importReceiptDetailModel.getActualQuantity();
                    }
                    if(totalQuantityByImportDate < 0) continue;
                    long totalQuantityExportBetweenImportDates = exportReceiptDetailService.calculateActualQuantityByProductBetweenImportDates(importDatesInRange.get(i), importDatesInRange.get(i+1), productId);
                    System.out.println(totalQuantityExportBetweenImportDates);
                    System.out.println(totalQuantityByImportDate);
                    if(totalQuantityExportBetweenImportDates > totalQuantityByImportDate) {
                        throw new DaoException(String.format("Số lượng xuất từ ngày %s đến ngày %s lớn hơn số lượng tồn kho tại thời điểm %s", formatter.format(importDatesInRange.get(i)), formatter.format(importDatesInRange.get(i+1)), formatter.format(importDatesInRange.get(i))));
                    }

                    // tính tổng tieefnf xuất từ ngày thứ nhập thứ i->i+1
                    double totalPriceBeforeUpdateBetweenImportDates = exportReceiptDetailService.calculateTotalPriceByProductAndTimeRange(
                            importDatesInRange.get(i), importDatesInRange.get(i), importDatesInRange.get(i+1), productId
                    );
                    totalQuantityExported += totalQuantityExportBetweenImportDates;
                    totalPriceBeforeUpdateMap.put(i, totalPriceBeforeUpdateBetweenImportDates);
                }
                // tính tổng tiền xuất trước khi cập nhật
                totalPriceBeforeUpdate += exportReceiptDetailService.calculateTotalPriceByProductAndTimeRange(oldImportDate, oldImportDate, newImportDate, productId);
                /*
                *  cập nhật khóa ngọoại với điều kiện:
                * - old <= x < new && id = old
                * */
                // lấy ra id cần cập nhật (old)
                exportReceiptDetailService.updateExportReceiptDetailPriceByProductAndTimeRange(beforeOldDayExportPriceInfo.exportPriceId(), beforeOldDayExportPriceInfo.exportPrrice(),
                        oldImportDate, productId, oldImportDate, newImportDate);
                // tính lại tổng tiền xuất sau khi cập nhật
                totalPriceAfterUpdate += exportReceiptDetailService.calculateTotalPriceByProductAndTimeRange(beforeOldDayExportPriceInfo.exportTime(), oldImportDate, newImportDate, productId);

                // kiểm tra xem nếu là cập nhật từ năm này sang năm khác ==> cần cập nhật lại tồn kho của năm cũ
                if(newYear != oldYear) {
                    inventoryDetailService.updateByProductId(productId, totalPriceBeforeUpdate - totalPriceAfterUpdate, Math.min(newYear, oldYear));
                }
                /*
                *    TH new - 1 != old - 1
                *  ==> Cập nhật thêm khóa ngoại với điều kiện:
                * x >= new && id = new - 1
                * */
                if(beforeNewDayExportPriceInfo.exportPriceId() != beforeOldDayExportPriceInfo.exportPriceId()) {
                    totalPriceBeforeUpdate += exportReceiptDetailService.calculateTotalPriceByProductAndTimeRange(beforeNewDayExportPriceInfo.exportTime(), newImportDate, null, productId);
                    // lấy đơn giá của ngày new
                    exportReceiptDetailService.updateExportReceiptDetailPriceByProductAndTimeRange(newDayExportPriceInfo.exportPriceId(), newDayExportPriceInfo.exportPrrice(),
                            beforeNewDayExportPriceInfo.exportTime(), productId, newImportDate, null);
                    totalPriceAfterUpdate += exportReceiptDetailService.calculateTotalQuantityByProductAndTimeRange(newDayExportPriceInfo.exportTime(), newImportDate, null, productId) * newDayExportPriceInfo.exportPrrice();
                    // cập nhật tồn kho trong năm của ngày mới
//                    inventoryDetailService.updateByProductId(productId, totalPriceBeforeUpdate - totalPriceAfterUpdate, newYear);
                }

                // cập nhật lại toàn bộ đơn giá với điều kiện: old <= x < new
                totalPriceBeforeUpdateMap.put(-1, 0.0);
                importDatesInRange.remove(newImportDate);
                for(int i = 0; i < importDatesInRange.size() - 1; i++) {
                    LocalDateTime importDate_i = importDatesInRange.get(i);
                    LocalDateTime importDate_i_1 = importDatesInRange.get(i+1);
                    double totalPriceBeforeUpdateByRangeTime = totalPriceBeforeUpdateMap.get(i);
                    // tính tổng tiền xuất từ ngày nhập thứ i -> i+1 sau khi đã cập nhật
                    double totalPriceAfterUpdateByRangeTime = exportReceiptDetailService.calculateTotalPriceByProductAndTimeRange(
                            importDate_i, importDate_i, importDate_i_1, productId
                    );
                    // tính tổng tiền xuất chênh lệch
                    double totalPriceDifference = totalPriceAfterUpdateByRangeTime - totalPriceBeforeUpdateByRangeTime + totalPriceBeforeUpdateMap.get(i - 1);
                    exportPriceService.updateExportPriceByProductIdAndImportDate(importReceiptDetailModel.getActualQuantity(), totalPriceDifference, importReceiptDetailModel.getTotalPrice(), importDate_i_1, productId);
                }
                // cập nhật lại đơn giá cho đơn giá của phiếu nhập đang chỉnh sửa.
                // 1. Tính tổng số lượng nhập trong khoảng từ ngày old - 1 <= x < new
                double totalPriceImported = importReceiptDetailService.calculateTotalPriceImportedByProduct(productId, beforeOldDayExportPriceInfo.exportTime(), newImportDate, oldImportDate);
                long totalQuantityImported = importReceiptDetailService.calculateTotalQuantityImportedByProduct(productId, beforeOldDayExportPriceInfo.exportTime(), newImportDate, oldImportDate);
                double totalPriceChanged = totalPriceAfterUpdate - totalPriceImported;
                long totalQuantityChanged = totalQuantityExported - totalQuantityImported;
                // nếu totalPriceImported == 0: tức không có phiếu nhập nào ==> không cần cập nhật lại đơn giá
                if(totalPriceImported <= 0) continue;
                // cập nhật lại đơn giá cho đơn giá đang chỉnh sửa
                exportPriceService.updateExportPriceAfterImportCorrectionByProductIdAndImportDate(totalPriceChanged, totalQuantityChanged, oldImportDate, productId);
            }
            else {
                // cập nhật trường hợp khóa export_price_id = (new_export_price_id)
                // 1. tính tổng thành tiền trước khi cập nhật đơn giá
                totalPriceBeforeUpdate += exportReceiptDetailService.calculateTotalPriceByProductAndTimeRange(beforeNewDayExportPriceInfo.exportTime(), newImportDate, oldImportDate, productId);
                // 2. cập nhật lại khóa ngoại
                exportReceiptDetailService.updateExportReceiptDetailPriceByProductAndTimeRange(
                        newDayExportPriceInfo.exportPriceId(), newDayExportPriceInfo.exportPrrice(),
                        beforeNewDayExportPriceInfo.exportTime(), productId, newImportDate, oldImportDate);
                // 3. tính tổng tiền mới sau khi thay đổi
                totalPriceAfterUpdate += exportReceiptDetailService.calculateTotalPriceByProductAndTimeRange(newDayExportPriceInfo.exportTime(), newImportDate, oldImportDate, productId);

                if(newYear != oldYear) {
                    inventoryDetailService.updateByProductId(productId, totalPriceBeforeUpdate - totalPriceAfterUpdate, Math.min(newYear, oldYear));
                }

                // trường hợp old - 1 != new - 1 ==> cập nhật thêm đoạn từ old -> vô cùng
                if(beforeOldDayExportPriceInfo.exportPriceId() != beforeNewDayExportPriceInfo.exportPriceId()) {
                    totalPriceBeforeUpdate += exportReceiptDetailService.calculateTotalPriceByProductAndTimeRange(
                            oldImportDate, oldImportDate, null, productId);
                    // cập nhật lại khóa ngoại
                    exportReceiptDetailService.updateExportReceiptDetailPriceByProductAndTimeRange(beforeOldDayExportPriceInfo.exportPriceId(),
                            beforeOldDayExportPriceInfo.exportPrrice(), oldImportDate, productId, oldImportDate, null);
                    // tính lại tổng tiền sau khi đã thay đổi
                    totalPriceAfterUpdate += exportReceiptDetailService.calculateTotalPriceByProductAndTimeRange(
                            oldImportDate, oldImportDate, null, productId);
                }

                // cập nhật lại đơn giá tính từ ngày new -> vô cùng
                //  1. lấy danh sách các ngày nhập tính từ ngày new - 1
                List<LocalDateTime> importDatesInRange = exportPriceService.findAllExportTimeByProductAndMoreThanImportDate(
                        beforeNewDayExportPriceInfo.exportTime(), oldImportDate, productId);
                for(int i = 0; i < importDatesInRange.size() - 1; i++) {
                    // lấy đơn giá (exportPriceModel) theo ngày
                    ExportPriceModel exportPriceModel_i = exportPriceService.findByProductAndImportDate(productId, importDatesInRange.get(i));
                    // tính tổng số lượng đã xuất trong khoảng ngày (i -> i + 1)
                    long totalQuantityExportBetweenImportDates = exportReceiptDetailService.calculateActualQuantityByProductBetweenImportDates(importDatesInRange.get(i), importDatesInRange.get(i+1), productId);
                    // tính tổng tiền xuất từ ngày thứ nhập thứ i->i+1
                    double totalPriceBeforeUpdateBetweenImportDates = exportReceiptDetailService.calculateTotalPriceByProductAndTimeRange(
                            importDatesInRange.get(i), importDatesInRange.get(i), importDatesInRange.get(i+1), productId
                    );
                    // tính lại tồn kho và đơn giá của của ngày i+1
                    long newQuantityInStock = exportPriceModel_i.getQuantityInStock() + exportPriceModel_i.getQuantityImported() - totalQuantityExportBetweenImportDates;
                    double newTotalPriceInStock = exportPriceModel_i.getTotalPriceInStock() + exportPriceModel_i.getTotalImportPrice() - totalPriceBeforeUpdateBetweenImportDates;
                    // tính lại đơn giá và cập nhật
                    exportPriceService.updateExportPriceByImportTimeAndProduct(newQuantityInStock, newTotalPriceInStock, importDatesInRange.get(i+1), productId);
                }
            }
            // cập nhật lại tồn kho
            inventoryDetailService.updateByProductId(productId, totalPriceBeforeUpdate - totalPriceAfterUpdate, newYear);
        }
    }
}
