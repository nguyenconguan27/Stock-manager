package com.manager.stock.manager_stock.screen.transaction.presenter;

import com.manager.stock.manager_stock.exception.CanNotFoundException;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.exception.DivisionByZeroException;
import com.manager.stock.manager_stock.exception.StockUnderFlowException;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptDetailModelMapper;
import com.manager.stock.manager_stock.model.*;
import com.manager.stock.manager_stock.model.dto.ExportReceiptIdAndCreateDate;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;
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

    private ImportReceiptPresenter() {
        importReceiptService = ImportReceiptServiceImpl.getInstance();
        importReceiptDetailService = ImportReceiptDetailServiceImpl.getInstance();
        inventoryDetailService = InventoryDetailServiceImpl.getInstance();
        exportPriceService = ExportPriceServiceImpl.getInstance();
        exportReceiptService = ExportReceiptServiceImpl.getInstance();
        receiptTransactionService = ReceiptTransactionServiceImpl.getInstance();
        exportReceiptDetailService = ExportReceiptDetailServiceImpl.getInstance();
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

    public void saveImportReceipt(ImportReceiptModel importReceiptModel, ObservableList<ImportReceiptDetailModelTable> importReceiptDetailModelsTable, HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap) throws DaoException{
        try {
            int academicYear = getYearOfImportReceipt(importReceiptModel.getCreateAt());
            importReceiptModel.setAcademicYear(academicYear);
            long importReceiptId = importReceiptService.save(importReceiptModel);
            importReceiptModel.setId(importReceiptId);
            List<ImportReceiptDetailModel> importReceiptDetailModels = GenericConverterBetweenModelAndTableData.convertToListModel(
                    importReceiptDetailModelsTable, ImportReceiptDetailModelMapper.INSTANCE::fromViewModelToModel);
//        try {
            System.out.println("Id of import receipt: " + importReceiptId);
            importReceiptDetailService.save(importReceiptDetailModels, importReceiptId);
//        }
            // rollback receipt(Xóa receipt khi lưu thành công) khi thêm mới chi tiết bị lỗi
//        catch (Exception e) {
//            importReceiptService.delete(importReceiptId);
//            throw e;
//        }
            // cập nhật tồn kho
//        try {
//            updateOrSaveInventoryDetail(academicYear, importReceiptDetailModels, changeQuantityByProductMap, changeTotalPriceByProductMap);
            LocalDateTime importDate = LocalDateTime.parse(importReceiptModel.getCreateAt(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            updateInventory(academicYear, importReceiptDetailModels, changeQuantityByProductMap, changeTotalPriceByProductMap, true, importDate, importReceiptId);
//        }
//        catch (Exception e) {
//            System.out.println("Cập nhật tồn kho lỗi rồi ==> rollback phiếu nhập và chi tiết phiếu nhập.");
//            e.printStackTrace();
//        }
//        return importReceiptModel;
            importReceiptService.commit();
        }
        catch (Exception e) {
            importReceiptService.rollback();
            System.out.println("Rollback khi thêm mới phiếu nhập thất bại.");
            e.printStackTrace();
        }
    }

    public void updateImportReceipt(ImportReceiptModel importReceiptModel, ImportReceiptModel oldImportReceiptModel, List<ImportReceiptDetailModelTable> importReceiptDetailModelTables, HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap, Set<Long> receiptDetailIds) throws DaoException {
        try {
            // cập nhật thông tin của phiếu nhập
            importReceiptService.update(importReceiptModel);
            // xóa đi những chi tiết phiếu nhập đã bị xóa
            if(!receiptDetailIds.isEmpty()) {
                importReceiptDetailService.deleteByIds(receiptDetailIds);
            }

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
            importReceiptService.commit();
        }
        catch (Exception e) {
            importReceiptService.rollback();
            System.out.println("Rollback khi cập nhật phiếu nhập thất bại.");
            e.printStackTrace();
        }
    }

    // cập nhật hoặc thêm mới tồn kho và đơn giá
    private void updateInventory(int academicYear, List<ImportReceiptDetailModel> importReceiptDetailModels, HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap, boolean isInsert, LocalDateTime importDate, long importReceiptId) throws DaoException {
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
                // lấy tồn kho đầu năm(của năm trước) của sản phẩm
                inventoryDetailModel = inventoryDetailModelMapPreviousYear.getOrDefault(productId, null);
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
                    System.out.println("Cập nhật giá xuất mới: " + productId + " chưa từng được nhập(không có tồn kho).");
                    inventoryDetailModel.setAcademicYear(academicYear);
                    inventoryDetailModel.setQuantity(actualQuantity);
                    inventoryDetailModel.setTotalPrice(totalPrice);
                    inventoryDetailModelsToInsert.add(inventoryDetailModel);
                }
                else {
                    // tồn kho đầu năm của sản phẩm đang xét(product id)
                    // đây là lần đầu tiên nhập hàng trong năm
                    System.out.println("Cập nhật giá xuất mới: " + productId + " đã tồn tại trong tồn kho của năm ngoái (tồn kho đầu năm của năm nhập hàng).");
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
                    throw new StockUnderFlowException("Số lượng tồn kho không đủ cho các phiếu xuất sau này, không thể chỉnh suwarrr với số lượng như hiện tại.");
                }
                // cập nhật lại giá xuất theo đúng số lượng nhập thêm vào
                System.out.println("Cập nhật giá xuất mới: " + productId + " đã tồn tại trong tồn kho của năm nay.");
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
    private void updateExportPrice(List<Long> productIds, List<ImportReceiptDetailModel> importReceiptDetailModels, LocalDateTime importDate, HashMap<Long, Integer> changeQuantityByProductMap, long importReceiptId) throws DaoException {
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
    private ExportPriceModel calculateUnitPriceOfProduct(long productId, int quantityInStock, double totalPriceInStock, double totalPriceImported, int quantityImported, ExportPriceModel exportPriceModel, long importReceiptId, LocalDateTime importDate) {
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

    public boolean deleteImportReceipt(ImportReceiptModelTable importReceiptModelTable) throws DaoException, StockUnderFlowException{
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

            // cập nhật tồn kho + giá xuất thành công ==> xóa phiếu nhập
            importReceiptService.delete(importReceiptModelTable.getId());

            importReceiptService.commit();
            return true;
        }
        catch (Exception e) {
            importReceiptService.rollback();
            e.printStackTrace();
        }
        return false;
    }

    public int findQuantityInStockByProductIdAndAcademicYear(long productId, int academicYear) throws DaoException{
        int[] yearsToTry = { academicYear, academicYear - 1 };
        for (int year : yearsToTry) {
            try {
                System.out.println(year);
                return inventoryDetailService.findQuantityInStockByProductIdAndAcademicYear(productId, year);
            } catch (CanNotFoundException e) {
                System.out.println(String.format("Quantity not found for productId={%d} in academicYear={%d}: {%s}", productId, year, e.getMessage()));
            }
        }
        return 0;
    }
}
