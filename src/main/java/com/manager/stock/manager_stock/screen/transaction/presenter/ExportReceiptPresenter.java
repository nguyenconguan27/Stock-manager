package com.manager.stock.manager_stock.screen.transaction.presenter;

import com.almasb.fxgl.physics.CollisionDetectionStrategy;
import com.manager.stock.manager_stock.exception.CanNotFoundException;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.exception.StockUnderFlowException;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ExportReceiptDetailModelTableMapper;
import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ExportReceiptModel;
import com.manager.stock.manager_stock.model.InventoryDetailModel;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndPrice;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;
import com.manager.stock.manager_stock.model.tableData.ExportReceiptDetailModelTable;
import com.manager.stock.manager_stock.service.*;
import com.manager.stock.manager_stock.service.impl.*;
import com.manager.stock.manager_stock.utils.GenericConverterBetweenModelAndTableData;

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
        List<Long> productIds = exportReceiptDetailModels.stream().map(ExportReceiptDetailModel::getProductId).collect(Collectors.toList());
        // thêm mới phiếu xuất
        List<Long> exportReceiptDetailIds = new ArrayList<>();
        long exportReceiptId = -1;
        try {
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
}
