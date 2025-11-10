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
import com.manager.stock.manager_stock.model.tableData.ExportReceiptModelTable;
import com.manager.stock.manager_stock.service.*;
import com.manager.stock.manager_stock.service.impl.*;
import com.manager.stock.manager_stock.utils.AlertUtils;
import com.manager.stock.manager_stock.utils.GenericConverterBetweenModelAndTableData;
import javafx.animation.ScaleTransition;
import javafx.scene.control.Alert;

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
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private ExportReceiptPresenter() {
        exportReceiptDetailService = ExportReceiptDetailServiceImpl.getInstance();
        exportReceiptService = ExportReceiptServiceImpl.getInstance();
        productService = ProductServiceImpl.getInstance();
        inventoryDetailService = InventoryDetailServiceImpl.getInstance();
        exportPriceService = ExportPriceServiceImpl.getInstance();
    }

    public static ExportReceiptPresenter getInstance() {
        if (instance == null) {
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

    public ExportPriceIdAndPrice findExportPriceIdAndPriceByProductAndLastTime(long productId, LocalDateTime exportDate) throws DaoException {
        return exportPriceService.findExportPriceByProductAndLastTime(productId, exportDate);
    }

    public boolean checkDate2AddNewReceipt(List<Long> productIds, LocalDateTime time) {
        Map<Long, List<ExportReceiptDetailModel>> detailModelMap = exportReceiptDetailService.findAllByProductAndMinTime(productIds, time);
        Map<Long, List<ExportPriceModel>> exportPriceMap = exportPriceService.findAllByProductAndMinTime(productIds, time);
        if ((detailModelMap == null || detailModelMap.isEmpty()) && (exportPriceMap == null || exportPriceMap.isEmpty())) {
            return false;
        }
        return true;
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
            exportReceiptModel.setAcademicYear(academicYearValue);
            exportReceiptId = exportReceiptService.save(exportReceiptModel);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime time = LocalDateTime.parse(exportReceiptModel.getCreateAt(), formatter);
            if(checkDate2AddNewReceipt(productIds, time)) {
                if (!updateExportReceiptDate(LocalDateTime.now(), time, exportReceiptId, true, exportReceiptDetailModels)) {
                    AlertUtils.alert("Không đủ điều kiện cập nhập phiếu nhập", "ERROR", "Lỗi", "Lỗi cập nhập ngày phiếu nhập");
                }
            }
            else {
                // thêm mới danh sách phiếu xuất chi tiết
                exportReceiptDetailIds = exportReceiptDetailService.save(exportReceiptDetailModels, exportReceiptId);

                updateInventory(exportReceiptDetailModels, academicYearValue, productIds, changeQuantityByProductMap);
            }
            exportReceiptService.commit();
        } catch (DaoException | CanNotFoundException | StockUnderFlowException e) {
            // gọi rollback
            exportReceiptService.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    private void updateInventory(List<ExportReceiptDetailModel> exportReceiptDetailModels, int academicYear, List<Long> productIds, HashMap<Long, Integer> changeQuantityByProductMap) throws DaoException {
        HashMap<Long, InventoryDetailModel> inventoryDetailByProductAndAcademicYear = inventoryDetailService.findAllByAcademicYearAndProductId(academicYear, productIds);
        HashMap<Long, InventoryDetailModel> inventoryDetailByProductAndPreviousAcademicYear = inventoryDetailService.findAllByAcademicYearAndProductId(academicYear - 1, productIds);

        List<InventoryDetailModel> inventoryDetailModelsToInsert = new ArrayList<>();
        List<InventoryDetailModel> inventoryDetailModelsToUpdate = new ArrayList<>();

        for (ExportReceiptDetailModel exportReceiptDetailModel : exportReceiptDetailModels) {
            long productId = exportReceiptDetailModel.getProductId();
//            // số lượng sản phẩm thay đổi
            int changeQuantity = changeQuantityByProductMap.getOrDefault(productId, 0);
            // tổng giá thay đổi
            double changeTotalPrice = changeQuantity * exportReceiptDetailModel.getOriginalUnitPrice();

            InventoryDetailModel inventoryDetailModel = inventoryDetailByProductAndAcademicYear.getOrDefault(productId, null);
            // TH sản phẩm này chưa có tồn kho của năm nay
            if (inventoryDetailModel == null) {
                // lấy ra tồn kho đầu kì (tức tồn kho của năm ngoái)
                inventoryDetailModel = inventoryDetailByProductAndPreviousAcademicYear.getOrDefault(productId, null);
                // trường hợp trong năm trước cũng chưa nhập ==> tạo mới
                if (inventoryDetailModel == null) {
                    // thông báo sản phẩm này chưa từng được nhập trong 2 năm trở lại đây
                    throw new CanNotFoundException("Sản phẩm: " + exportReceiptDetailModel.getProductName() + " chưa từng được nhập trong 2 năm gần đây, vui lòng kiểm tra lại.");
                }
                // TH tồn kho không đủ để xuất kho ==> alert
                if (inventoryDetailModel.getQuantity() < changeQuantity) {
                    // thông báo sản productId trong kho không đủ
                    throw new StockUnderFlowException("Số lượng tồn kho của " + exportReceiptDetailModel.getProductName() + " không đủ để xuất kho, hiện chỉ còn " + inventoryDetailModel.getQuantity() + " sản phẩm, vui lòng nhập thêm.");
                }
                // tính lại tồn kho
                int quantityInStock = inventoryDetailModel.getQuantity();
                double totalPriceInStock = inventoryDetailModel.getTotalPrice() - changeTotalPrice;

                InventoryDetailModel newInventoryModel = new InventoryDetailModel();
                newInventoryModel.setProductId(productId);
                newInventoryModel.setTotalPrice(totalPriceInStock);
                newInventoryModel.setQuantity(quantityInStock - changeQuantity);
                newInventoryModel.setAcademicYear(academicYear);

                inventoryDetailModelsToInsert.add(newInventoryModel);
            }
            // trường hợp sản phẩm này đã từng được nhập ==> có tồn kho của năm nay
            else {
                if (inventoryDetailModel.getQuantity() < changeQuantity) {
                    // thông báo sản productId trong kho không đủ
                    throw new StockUnderFlowException("Số lượng tồn kho của " + exportReceiptDetailModel.getProductName() + " không đủ để xuất kho, hiện chỉ còn " + inventoryDetailModel.getQuantity() + " sản phẩm, vui lòng nhập thêm.");
                }
                inventoryDetailModel.setQuantity(inventoryDetailModel.getQuantity() - changeQuantity);
                inventoryDetailModel.setTotalPrice(inventoryDetailModel.getTotalPrice() - changeTotalPrice);
                inventoryDetailModelsToUpdate.add(inventoryDetailModel);
            }
        }
        if (!inventoryDetailModelsToInsert.isEmpty()) {
            inventoryDetailService.save(inventoryDetailModelsToInsert);
        }
        if (!inventoryDetailModelsToUpdate.isEmpty()) {
            inventoryDetailService.update(inventoryDetailModelsToUpdate);
        }
    }

    private void updateExportPrice(List<Long> productIds, List<ExportReceiptDetailModel> exportReceiptDetailModels, LocalDateTime exportDate, HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap) throws DaoException {
        // danh sách đơn giá xuất theo từng sản phẩm tính từ ngày nhập của phiếu xuất trở đi
        Map<Long, List<ExportPriceModel>> exportPriceModelsByProductAfterExportDate = exportPriceService.findAllByProductAndMinTime(productIds, exportDate);
        List<ExportPriceModel> exportPriceModelsToUpdate = new ArrayList<>();
        Map<Long, ExportPriceModel> exportPriceModelByIdMap = new HashMap<>();

        // duyệt toàn bộ sản phẩm trong phiếu nhập cần sửa
        for (ExportReceiptDetailModel exportReceiptDetailModel : exportReceiptDetailModels) {
            long productId = exportReceiptDetailModel.getProductId();
            List<ExportPriceModel> exportPriceModels = exportPriceModelsByProductAfterExportDate.getOrDefault(productId, null);
            // trường hợp sản phẩm này sau đó chưa được nhập thêm ==> không cần cập nhật lại đơn giá
            if (exportPriceModels == null) {
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
        if (!exportPriceModelsToUpdate.isEmpty()) {
            exportPriceService.update(exportPriceModelsToUpdate);
        }
    }

    public void checkAndUpdateExportPriceWhenChangeDate(ExportPriceModel preExportPrice, ExportPriceModel nextExportPrice,
                                                        List<ExportReceiptDetailModel> exportReceiptDetailModelList) {
        int totalQuanExported = 0;
        long totalPriceExported = 0;
        for (ExportReceiptDetailModel exportReceiptDetailModel : exportReceiptDetailModelList) {
            totalQuanExported += exportReceiptDetailModel.getActualQuantity();
        }
        totalPriceExported = totalQuanExported * (long) preExportPrice.getExportPrice();
        if (totalQuanExported > preExportPrice.getQuantityInStock() + preExportPrice.getQuantityImported()) {
            throw new StockUnderFlowException("");
        }
        if(nextExportPrice == null)
            return;
        nextExportPrice.setQuantityInStock(preExportPrice.getQuantityInStock() + preExportPrice.getQuantityImported() - totalQuanExported);
        nextExportPrice.setTotalPriceInStock((preExportPrice.getQuantityInStock() + preExportPrice.getQuantityImported() - totalQuanExported) * preExportPrice.getExportPrice());
        nextExportPrice.setExportPrice((nextExportPrice.getTotalPriceInStock() + nextExportPrice.getTotalImportPrice()) / (nextExportPrice.getQuantityImported() + nextExportPrice.getQuantityInStock()));
    }

    public void updateExportReceiptDetail(List<ExportReceiptDetailModel> exportReceiptDetailToCheck, ExportReceiptDetailModel exportReceiptDetailModel,
                                          ExportPriceModel exportPriceModel, long exportReceiptId, boolean isNew, int fromYear, int toYear) {
        int checkNew = -1;
        double totalPriceExportedBeforeUpdate = 0; // Huong
        double totalPriceExportedAfterUpdate = 0; // Huong
        // lâấy danh sách tồn kho cần cập nhật
        List<InventoryDetailModel> oldInventoryDetailModels = inventoryDetailService.findByMinYearAndProduct(exportReceiptDetailModel.getProductId(), Math.min(fromYear, toYear));
        // nếu đổi từ ngày của năm này sang năm khác ==> cần cập nhật lại số lượng + tồn tiền tồn
        if(toYear > fromYear && !isNew) {
            // TH đổi từ ngày bé ==> lên ngày lớn ==> cập nhật lại tồn kho của năm cũ
            if(!oldInventoryDetailModels.isEmpty()) {
                InventoryDetailModel oldInventory = oldInventoryDetailModels.get(0);
                oldInventory.setQuantity(oldInventory.getQuantity() + exportReceiptDetailModel.getActualQuantity());
                oldInventory.setTotalPrice(oldInventory.getTotalPrice() + exportReceiptDetailModel.getTotalPrice());
            }
        }
        for(int j = 0; j < exportReceiptDetailToCheck.size(); j++) {
            double oldUnitPrice = exportReceiptDetailToCheck.get(j).getOriginalUnitPrice();
            // cộng tổng tiền của phiếu xuất trước khi thay đổi
            totalPriceExportedBeforeUpdate += exportReceiptDetailToCheck.get(j).getOriginalUnitPrice() *  exportReceiptDetailToCheck.get(j).getActualQuantity();
            // cập nhật
            exportReceiptDetailToCheck.get(j).setOriginalUnitPrice(exportPriceModel.getExportPrice());
            exportReceiptDetailToCheck.get(j).setDisplayUnitPrice(exportPriceModel.getExportPrice());
            exportReceiptDetailToCheck.get(j).setExportPriceId(exportPriceModel.getId());
            // cập nhật xong
            if(oldUnitPrice != exportReceiptDetailToCheck.get(j).getOriginalUnitPrice()) {
                totalPriceExportedAfterUpdate += exportReceiptDetailToCheck.get(j).getOriginalUnitPrice() *  exportReceiptDetailToCheck.get(j).getActualQuantity();
            }
            else {
                totalPriceExportedBeforeUpdate -= exportReceiptDetailToCheck.get(j).getOriginalUnitPrice() *  exportReceiptDetailToCheck.get(j).getActualQuantity();
            }
            // TH phát hiện thêm mới
            if(exportReceiptDetailToCheck.get(j).getId() == exportReceiptDetailModel.getId()
                    && isNew) {
                checkNew = j;
                // TH sản phẩm này được thêm mới ==> tổng tiền trc là = 0
                totalPriceExportedBeforeUpdate -= exportReceiptDetailToCheck.get(j).getOriginalUnitPrice() *  exportReceiptDetailToCheck.get(j).getActualQuantity();
                // kiểm tra xem ngày xuât hiện tại so với
            }
        }
        // TH chuyển từ năm lớn sang năm bé ==> cần cập nhật
        if(toYear < fromYear && !isNew) {
            exportReceiptDetailModel.setOriginalUnitPrice(exportPriceModel.getExportPrice()); // pre (i)
            exportReceiptDetailModel.setTotalPrice(exportPriceModel.getExportPrice() * exportReceiptDetailModel.getActualQuantity());
            if(!oldInventoryDetailModels.isEmpty()) {
                InventoryDetailModel inventory = oldInventoryDetailModels.get(0);
                inventory.setQuantity(inventory.getQuantity() - exportReceiptDetailModel.getActualQuantity());
                inventory.setTotalPrice(inventory.getTotalPrice() - exportReceiptDetailModel.getTotalPrice());
            }
        }
        int quantityChanged = 0;
        if(checkNew >= 0) { // TH thêm mới sp vào phiếu xuất
            exportReceiptDetailToCheck.remove(checkNew);
            exportReceiptDetailService.save(List.of(exportReceiptDetailModel), exportReceiptId);
            quantityChanged = exportReceiptDetailModel.getActualQuantity();
        }
        // cập nhật lại tồn kho. Nếu update ==> chỉ có tổng tiền thay đổi
        // Nếu thêm mới ==> cả số lượng + tổng tiền thay đổi
        int i = fromYear == toYear ? 0 : 1;
        for(; i < oldInventoryDetailModels.size(); i++) {
            InventoryDetailModel inventory = oldInventoryDetailModels.get(i);
            inventory.setQuantity(inventory.getQuantity() - quantityChanged);
            inventory.setTotalPrice(inventory.getTotalPrice() + totalPriceExportedBeforeUpdate - totalPriceExportedAfterUpdate);
        }
        inventoryDetailService.update(oldInventoryDetailModels);

        exportReceiptDetailService.update(exportReceiptDetailToCheck);
    }

    public boolean updateExportReceiptDate(LocalDateTime fromDate, LocalDateTime toDate, long exportReceiptId, boolean isNew, List<ExportReceiptDetailModel> newExportReceiptDetailModels) {

        List<ExportReceiptDetailModel> exportReceiptDetailModels;
        if(isNew) { // TH thêm mới
            exportReceiptDetailModels = newExportReceiptDetailModels;
        } else { // TH cập nhật
            exportReceiptDetailModels = exportReceiptDetailService.findAllByExportReceipt(exportReceiptId);
        }
        // danh các sản phầm cần cập nhật lại của phiếu xuất
        for (ExportReceiptDetailModel exportReceiptDetailModel : exportReceiptDetailModels) {
            ExportPriceModel exBeforeNewDate;
            List<ExportPriceModel> exsToUpdate;
            if (fromDate.isBefore(toDate)) {
                exBeforeNewDate = exportPriceService.findByProductIdAndBeforeTime(exportReceiptDetailModel.getProductId(), fromDate);
                exsToUpdate = exportPriceService.findAllByProductIdAndAfterTime(exportReceiptDetailModel.getProductId(), fromDate);
            } else {
                exBeforeNewDate = exportPriceService.findByProductIdAndBeforeTime(exportReceiptDetailModel.getProductId(), toDate);
                exsToUpdate = exportPriceService.findAllByProductIdAndAfterTime(exportReceiptDetailModel.getProductId(), toDate);
            }
            if(exBeforeNewDate != null) {
                exsToUpdate.add(0, exBeforeNewDate);
            } else {
                throw new StockUnderFlowException("Cập nhập ngày xuất thất bại do không đủ số lượng tồn kho");
            }
            for (int i = 0; i < exsToUpdate.size(); i++) {
                List<ExportReceiptDetailModel> exportReceiptDetailToCheck;
                ExportPriceModel pre = exsToUpdate.get(i);
                ExportPriceModel next =  i == exsToUpdate.size() - 1 ? null : exsToUpdate.get(i + 1);
                if (i < exsToUpdate.size() - 1) {
                    exportReceiptDetailToCheck = exportReceiptDetailService.findByRangeTime(
                            exportReceiptDetailModel.getProductId(), exsToUpdate.get(i).getExportTime(), exsToUpdate.get(i + 1).getExportTime()
                    );
                } else {
                    exportReceiptDetailToCheck = exportReceiptDetailService.findByRangeTime(
                            exportReceiptDetailModel.getProductId(), exsToUpdate.get(i).getExportTime(), LocalDateTime.now()
                    );
                }
                if (i == 0) {
                    if (fromDate.isBefore(toDate)) {
                        for(int j = 0; j < exportReceiptDetailToCheck.size(); j++) {
                            if(exportReceiptDetailToCheck.get(j).getId() == exportReceiptDetailModel.getId()) {
                                exportReceiptDetailToCheck.remove(j);
                                break;
                            }
                        }
                    } else {
                        exportReceiptDetailModel.setExportPriceId(pre.getId());
                        exportReceiptDetailToCheck.add(exportReceiptDetailModel);
                    }
                }
                if (fromDate.isBefore(toDate)) {
                    if(toDate.isAfter(pre.getExportTime()) && (next == null || toDate.isBefore(next.getExportTime()))) {
                        exportReceiptDetailModel.setExportPriceId(exsToUpdate.get(i).getId());
                        exportReceiptDetailToCheck.add(exportReceiptDetailModel);
                    }
                } else if(fromDate.isAfter(pre.getExportTime()) && (next == null || fromDate.isBefore(next.getExportTime()))) {
                    for (ExportReceiptDetailModel exportReceiptDetailModel1 : exportReceiptDetailToCheck) {
                        if (exportReceiptDetailModel1.getId() == exportReceiptDetailModel.getId()) {
                            exportReceiptDetailToCheck.remove(exportReceiptDetailModel1);
                            break;
                        }
                    }
                }
                checkAndUpdateExportPriceWhenChangeDate(pre, next, exportReceiptDetailToCheck);
                int fromYear = fromDate.getYear();
                int toYear = toDate.getYear();
                updateExportReceiptDetail(exportReceiptDetailToCheck, exportReceiptDetailModel, pre, exportReceiptId, isNew, fromYear, toYear);
            }
            exportPriceService.update(exsToUpdate);
        }
        return true;
    }

    public void updateExportReceipt(ExportReceiptModel newExportReceipt, ExportReceiptModel oldExportReceipt, List<ExportReceiptDetailModelTable> exportReceiptDetailModelTables, HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap) throws DaoException {
        try {
            if (!Objects.equals(newExportReceipt.getCreateAt(), oldExportReceipt.getCreateAt())) {
                LocalDateTime fromDate = LocalDateTime.parse(oldExportReceipt.getCreateAt(), formatter);
                LocalDateTime toDate = LocalDateTime.parse(newExportReceipt.getCreateAt(), formatter);
                if (!updateExportReceiptDate(fromDate, toDate, oldExportReceipt.getId(), false, null)) {
                    AlertUtils.alert("Không đủ điều kiện cập nhập phiếu nhập", "ERROR", "Lỗi", "Lỗi cập nhập ngày phiếu nhập");
                }
                newExportReceipt.setCreatedAtTs(toDate);
                exportReceiptService.update(newExportReceipt);
                exportReceiptService.commit();
                return;
            }
            List<ExportReceiptDetailModel> exportReceiptDetailModels = GenericConverterBetweenModelAndTableData.convertToListModel(
                    exportReceiptDetailModelTables, ExportReceiptDetailModelTableMapper.INSTANCE::fromViewModelToModel);
            // thêm mới export detail trong TH chỉnh sửa phiếu xuất có add thêm sản phẩm mới
            List<ExportReceiptDetailModel> newExportReceiptDetails = exportReceiptDetailModels.stream().filter(ep -> ep.getId() == -1)
                    .collect(Collectors.toList());
            exportReceiptDetailService.save(newExportReceiptDetails, oldExportReceipt.getId());

            List<Long> productIds = exportReceiptDetailModelTables.stream().map(ExportReceiptDetailModelTable::getProductId).collect(Collectors.toList());
            int academicYear = getYearOfExportReceipt(oldExportReceipt.getCreateAt());
            // cập nhật tồn kho
            updateInventory(exportReceiptDetailModels, academicYear, productIds, changeQuantityByProductMap);
            // cập nhật giá xuất
            LocalDateTime exportDate = LocalDateTime.parse(oldExportReceipt.getCreateAt(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            updateExportPrice(productIds, exportReceiptDetailModels, exportDate, changeQuantityByProductMap, changeTotalPriceByProductMap);

            // cập nhật danh sách chi tiết phiếu xuất
            exportReceiptDetailService.update(exportReceiptDetailModels);
            // commit
            exportReceiptService.commit();
        } catch (Exception e) {
            exportReceiptService.rollback();
            throw e;
        }
    }

    public boolean deleteExportReceipt(ExportReceiptModelTable exportReceiptModel) throws DaoException {
        try {
            // lấy danh sách chi tiết phiếu xuất
            List<ExportReceiptDetailModel> exportReceiptDetailModels = exportReceiptDetailService.findAllByExportReceipt(exportReceiptModel.getId());
            HashMap<Long, Integer> changeQuantityByProductMap = new HashMap<>();
            HashMap<Long, Double> changeTotalPriceByProductMap = new HashMap<>();
            List<Long> productIds = new ArrayList<>();
            exportReceiptDetailModels.forEach(exportReceiptDetailModel -> {
                changeQuantityByProductMap.put(exportReceiptDetailModel.getProductId(), (-1) * exportReceiptDetailModel.getActualQuantity());
                changeTotalPriceByProductMap.put(exportReceiptDetailModel.getProductId(), (-1) * exportReceiptDetailModel.getActualQuantity() * exportReceiptDetailModel.getOriginalUnitPrice());
                productIds.add(exportReceiptDetailModel.getProductId());
            });
            // cập nhật tồn kho
            int academicYear = getYearOfExportReceipt(exportReceiptModel.getCreateAt());
            LocalDateTime exportDate = LocalDateTime.parse(exportReceiptModel.getCreateAt(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            updateInventory(exportReceiptDetailModels, academicYear, productIds, changeQuantityByProductMap);

            // cập nhật lại đơn giá
            updateExportPrice(productIds, exportReceiptDetailModels, exportDate, changeQuantityByProductMap, changeTotalPriceByProductMap);
            List<Long> exportReceiptIds = new ArrayList<>();
            exportReceiptIds.add(exportReceiptModel.getId());
            exportReceiptService.deleteByIds(exportReceiptIds);

            return true;
        } catch (Exception e) {
            exportReceiptService.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    private int getYearOfExportReceipt(String createAt) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime date = LocalDateTime.parse(createAt, formatter);
            return date.getYear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public int findQuantityInStockByProductIdAndAcademicYear(long productId, int academicYear) throws DaoException {
        int[] yearsToTry = {academicYear, academicYear - 1};

        for (int year : yearsToTry) {
            try {
                return inventoryDetailService.findQuantityInStockByProductIdAndAcademicYear(productId, year);
            } catch (CanNotFoundException e) {
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


            double newUnitPrice = Math.round((totalPriceInStock + totalPriceImported) / (quantityImported + quantityInStock));
            exportPriceModel.setExportPrice(newUnitPrice);
            exportPriceModel.setTotalPriceInStock(totalPriceInStock);
            return exportPriceModel;
        } catch (ArithmeticException e) {
            e.printStackTrace();
            throw new DivisionByZeroException("Số lượng tồn kho và số lượng nhập không hợp lệ, vui lòng kiểm tra lại.");
        }
    }
}
