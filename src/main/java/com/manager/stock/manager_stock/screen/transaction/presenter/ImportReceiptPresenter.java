package com.manager.stock.manager_stock.screen.transaction.presenter;

import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.exception.DivisionByZeroException;
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
                    System.out.println(importReceiptDetailModel);
                   importReceiptDetailModel.setUnitPriceFormat(FormatMoney.format(importReceiptDetailModel.getUnitPrice()));
                   importReceiptDetailModel.setTotalPriceFormat(FormatMoney.format(importReceiptDetailModel.getTotalPrice()));
                });
        return importReceiptDetailModels;
    }

    public List<ProductModel> loadAllProduct() {
        return productService.getAllProducts();
    }

    public void saveImportReceipt(ImportReceiptModel importReceiptModel, ObservableList<ImportReceiptDetailModelTable> importReceiptDetailModelsTable, HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap) throws DaoException{
        int academicYear = getYearOfImportReceipt(importReceiptModel.getCreateAt());
        importReceiptModel.setAcademicYear(academicYear);
        long importReceiptId = importReceiptService.save(importReceiptModel);
        importReceiptModel.setId(importReceiptId);
        List<ImportReceiptDetailModel> importReceiptDetailModels = GenericConverterBetweenModelAndTableData.convertToListModel(importReceiptDetailModelsTable, ImportReceiptDetailModelMapper.INSTANCE::fromViewModelToModel);
        try {
            System.out.println("Id of import receipt: " + importReceiptId);
            importReceiptDetailService.save(importReceiptDetailModels, importReceiptId);
        }
        // rollback receipt(Xóa receipt khi lưu thành công) khi thêm mới chi tiết bị lỗi
        catch (Exception e) {
            importReceiptService.delete(importReceiptId);
            throw e;
        }
        // cập nhật tồn kho
        try {
//            updateOrSaveInventoryDetail(academicYear, importReceiptDetailModels, changeQuantityByProductMap, changeTotalPriceByProductMap);
            LocalDateTime importDate = LocalDateTime.parse(importReceiptModel.getCreateAt(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            updateInventory(academicYear, importReceiptDetailModels, changeQuantityByProductMap, changeTotalPriceByProductMap, true, importDate);
        }
        catch (Exception e) {
            System.out.println("Cập nhật tồn kho lỗi rồi ==> rollback phiếu nhập và chi tiết phiếu nhập.");
            e.printStackTrace();
        }
//        return importReceiptModel;
    }

    public void updateImportReceipt(ImportReceiptModel importReceiptModel, ImportReceiptModel oldImportReceiptModel, List<ImportReceiptDetailModelTable> importReceiptDetailModelTables, HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap) throws DaoException {
        // cập nhật thông tin của phiếu nhập
        importReceiptService.update(importReceiptModel);

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(importReceiptModel.getCreateAt(), formatter);
        int year = date.getYear();
        try {
            List<ImportReceiptDetailModel> importReceiptDetailModelsOverNewAndEdit = new ArrayList<>(newImportReceiptDetailModel);
            importReceiptDetailModelsOverNewAndEdit.addAll(editImportReceiptDetailModel);
//            updateOrSaveInventoryDetail(year, importReceiptDetailModelsOverNewAndEdit, changeQuantityByProductMap, changeTotalPriceByProductMap);
        }
        catch (Exception e) {
            System.out.println("Cập nhật tồn kho lỗi rồi ==> rollback phiếu nhập và chi tiết phiếu nhập.");
            e.printStackTrace();
        }
    }

    // cập nhật hoặc thêm mới tồn kho và đơn giá
    private void updateInventory(int academicYear, List<ImportReceiptDetailModel> importReceiptDetailModels, HashMap<Long, Integer> changeQuantityByProductMap, HashMap<Long, Double> changeTotalPriceByProductMap, boolean isInsert, LocalDateTime importDate) throws DaoException {
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
                            productId, 0, 0, totalPrice, actualQuantity, null
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
                            productId, inventoryDetailModel.getQuantity(), inventoryDetailModel.getTotalPrice(), totalPrice, actualQuantity, null
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
                // cập nhật lại giá xuất theo đúng số lượng nhập thêm vào
                System.out.println("Cập nhật giá xuất mới: " + productId + " đã tồn tại trong tồn kho của năm nay.");
                // thêm phiếu nhâập ==> tạo mới đơn giá xuất
                if(isInsert) {
                    // trường hợp thêm mới phiếu nhập ==> thêm mới đơn giá
                    exportPriceModelsToInsert.add(calculateUnitPriceOfProduct(
                            productId,
                            inventoryDetailModel.getQuantity(),
                            inventoryDetailModel.getTotalPrice(),
                            totalPrice, actualQuantity, null
                    ));
                }
                // sửa phiếu nhập
                else {
                    updateExportPrice(productIds, importReceiptDetailModels, importDate, changeQuantityByProductMap);
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
    private void updateExportPrice(List<Long> productIds, List<ImportReceiptDetailModel> importReceiptDetailModels, LocalDateTime importDate, HashMap<Long, Integer> changeQuantityByProductMap) {
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
            // trường hợp add thêm sản phẩm vào phiếu nhâp và sau đó chưa từng nhập thêm sản phẩm này ==> thêm mới đơn giá cho sản phẩm này
            if(exportPriceModels == null) {
                exportPriceModels = new ArrayList<>();

            }
            else {
                // cập nhật lại toàn bộ đơn giá cho sản phẩm hiện tại
                int quantityDiffFromPrevReceipt = 0;
                double totalPriceDiffFromPrevReceipt = 0;
                for(int i = 0; i < exportPriceModels.size(); i++) {
                    ExportPriceModel exportPriceModel = exportPriceModels.get(i);
                    // số lượng và thành tiền thay đổi của sản phẩm trong hóa đơn nhập đang chỉnh sửa này so với ban đầu
                    if(i == 0) {
                        quantityDiffFromPrevReceipt = importReceiptDetailModel.getActualQuantity() - exportPriceModel.getQuantityImported();
                        totalPriceDiffFromPrevReceipt = importReceiptDetailModel.getTotalPrice() - exportPriceModel.getTotalImportPrice();
                        // tính lại đơn giá của hóa đơn nhập cùng ngày
                        ExportPriceModel exportPriceModelToUpdate = calculateUnitPriceOfProduct(
                                productId,
                                exportPriceModel.getQuantityInStock(),
                                exportPriceModel.getTotalPriceInStock(),
                                importReceiptDetailModel.getTotalPrice(), // thanhf tieenf nhaap
                                importReceiptDetailModel.getActualQuantity(), // soos luoqwngj nhaap,
                                exportPriceModel
                        );
                        exportPriceModelsToUpdate.add(exportPriceModelToUpdate);
                        exportPriceModelByIdMap.put(exportPriceModel.getId(), exportPriceModelToUpdate);
                    }
                    else {
                        int newQuantityInStock = exportPriceModel.getQuantityInStock() + quantityDiffFromPrevReceipt;
                        double newTotalPriceInStock = exportPriceModel.getTotalPriceInStock() + totalPriceDiffFromPrevReceipt;
                        ExportPriceModel exportPriceModelToUpdate = calculateUnitPriceOfProduct(
                                productId,
                                newQuantityInStock,
                                newTotalPriceInStock,
                                exportPriceModel.getTotalImportPrice(),
                                exportPriceModel.getQuantityImported(),
                                exportPriceModel
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
                ExportPriceModel exportPriceModel = exportPriceModelByIdMap.getOrDefault(exportReceiptDetailModel.getUnitPriceId(), null);
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
    private ExportPriceModel calculateUnitPriceOfProduct(long productId, int quantityInStock, double totalPriceInStock, double totalPriceImported, int quantityImported, ExportPriceModel exportPriceModel) {
        try {
            if(exportPriceModel == null) {
                exportPriceModel = new ExportPriceModel();
                exportPriceModel.setProductId(productId);
                exportPriceModel.setExportTime(System.currentTimeMillis());
            }
            exportPriceModel.setQuantityInStock(quantityInStock);
            exportPriceModel.setQuantityImported(quantityImported);
            exportPriceModel.setTotalImportPrice(totalPriceImported);
            // tính giá xuất mới cho sản phẩm
            System.out.println(String.format("Giá xuất mới dựa trên tham số: Số lượng trong kho = %d, số lượng nhập = %d, Thành tien trong kho = %f, Thành tiền nhập = %f",
                                    quantityInStock, quantityImported, totalPriceInStock, totalPriceImported));

            double newUnitPrice = Math.round((totalPriceInStock + totalPriceImported) / (quantityImported + quantityInStock));
            exportPriceModel.setExportPrice(newUnitPrice);
//            exportPriceModel.setQuantityInStock(quantityInStock);
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

    public boolean deleteImportReceipt(ImportReceiptModelTable importReceiptModelTable) {
        // lấy ra danh sách sản phẩm có trong hóa đơn nhập cần xóa
        List<ProductIdAndActualQuantityAndTotalPriceOfReceipt> productIdAndActualQuantityAndTotalPriceOfImportReceipts = new ArrayList<>();
        int academicYear = getYearOfImportReceipt(importReceiptModelTable.getCreateAt());
        if (academicYear <= 0) {
            AlertUtils.alert("Không xác định được năm học của phiếu nhập, vui lòng kiểm tra lại ngày tạo.", "ERROR", "Lỗi dữ liệu", "");
            return false;
        }
        try {
            productIdAndActualQuantityAndTotalPriceOfImportReceipts = importReceiptDetailService.findAllProductIdByImportReceipt(importReceiptModelTable.getId());
        }
        catch (DaoException e) {
            AlertUtils.alert(e.getMessage(), "ERROR", "Lỗi", "Lỗi hệ thống.");
            return false;
        }

        // lấy ra danh sách hóa đơn xuất có chứa sản phẩm trong danh sách của hóa đơn nhập, tính từ ngày của hóa đơn nhập
        List<Long> productIdsByImportReceipt = productIdAndActualQuantityAndTotalPriceOfImportReceipts.stream()
                .map(ProductIdAndActualQuantityAndTotalPriceOfReceipt::productId)
                .collect(Collectors.toList());
        List<ExportReceiptIdAndCreateDate> exportReceiptIdAndCreateDates = exportReceiptService.findExportReceiptIdAndCreatedAtByProductIdsAndYearAndMinCreatedAt(
                productIdsByImportReceipt, academicYear, importReceiptModelTable.getCreateAt());
        // cập nhật lại tồn kho
        // lấy danh sách tồn kho theo sản phẩm trong hóa đơn nhập
        HashMap<Long, InventoryDetailModel> inventoryDetailModels = inventoryDetailService.findAllByAcademicYearAndProductId(academicYear, productIdsByImportReceipt);
        List<InventoryDetailModel> inventoryDetailModelsToUpdate = new ArrayList<>();
        HashMap<Long, Integer> actualQuantityInImportReceiptByProductIdMap = productIdAndActualQuantityAndTotalPriceOfImportReceipts
                .stream()
                .collect(Collectors.toMap(
                        ProductIdAndActualQuantityAndTotalPriceOfReceipt::productId,
                        ProductIdAndActualQuantityAndTotalPriceOfReceipt::actualQuantity,
                        (existing, replacement) -> replacement,
                        HashMap::new
                ));

        HashMap<Long, Double> totalPriceInImportReceiptByProductIdMap = productIdAndActualQuantityAndTotalPriceOfImportReceipts
                .stream()
                .collect(Collectors.toMap(
                        ProductIdAndActualQuantityAndTotalPriceOfReceipt::productId,
                        ProductIdAndActualQuantityAndTotalPriceOfReceipt::totalPrice,
                        (existing, replacement) -> replacement,
                        HashMap::new
                ));
        List<Long> exportReceiptIds = exportReceiptIdAndCreateDates.stream()
                .map(ExportReceiptIdAndCreateDate::id)
                .collect(Collectors.toList());
        // trường hợp tính từ khi hóa đơn nhập đang chọn được tạo cho đến thời điểm thực hiện xóa cho có hóa đơn xuất nào
        if(exportReceiptIdAndCreateDates.isEmpty()) {
            System.out.println("Cập nhật tồn kho khi xóa phiếu nhập không có phiếu xuất");
            // Cập nhật lại tồn kho và xóa chi tiết hóa đơn nhập và hóa đơn nhập
            for(InventoryDetailModel inventoryDetailModel : inventoryDetailModels.values()) {
                int newQuantity = inventoryDetailModel.getQuantity() - actualQuantityInImportReceiptByProductIdMap.get(inventoryDetailModel.getProductId());
                double newTotalPrice = inventoryDetailModel.getTotalPrice() - totalPriceInImportReceiptByProductIdMap.get(inventoryDetailModel.getProductId());
                inventoryDetailModel.setQuantity(newQuantity);
                inventoryDetailModel.setTotalPrice(newTotalPrice);
                inventoryDetailModelsToUpdate.add(inventoryDetailModel);
            }
        }
        // truường hợp phiếu nhập có liên quan đến phiếu xuất
        else {
            System.out.println("Cập nhật tồn kho khi phiếu nhập có phiếu xuất");
            // gửi thông báo alert confirm: có danh sách hóa đơn xuất rồi, cần xóa trước khi hóa đơn nhập được xóa
            StringBuilder messageConfirm = new StringBuilder();
            messageConfirm.append("Không thể xóa phiếu nhập có số phiếu là: " + importReceiptModelTable.getInvoice())
                    .append(" do một vài sản phẩm đã có trong phiếu xuất các ngày: ")
                    .append(exportReceiptIdAndCreateDates.stream()
                            .map(ExportReceiptIdAndCreateDate::createDate)
                            .collect(Collectors.joining(", ")));
            messageConfirm.append(", Bạn đồng ý xóa các phiếu xuất trước khi xóa phiếu nhập này không?.");

            boolean isConfirmDeleteExportReceipt = AlertUtils.confirm(messageConfirm.toString());
            if(!isConfirmDeleteExportReceipt) return false;

            // lấy ra danh sách tổng số lượng và tổng thành tiền của toàn bộ phiếu xuất và chia theo product
            ExportReceiptPresenter presenter = ExportReceiptPresenter.getInstance();
            HashMap<Long, ProductIdAndActualQuantityAndTotalPriceOfReceipt> productIdAndActualQuantityAndTotalPriceOfExportReceiptMap
                    = presenter.findProductIdAndActualQuantityAndTotalPriceOfReceipt(exportReceiptIds);

            for (InventoryDetailModel inventoryDetailModel : inventoryDetailModels.values()) {
                int newQuantity = inventoryDetailModel.getQuantity() + productIdAndActualQuantityAndTotalPriceOfExportReceiptMap.get(inventoryDetailModel.getProductId()).actualQuantity()
                        - actualQuantityInImportReceiptByProductIdMap.get(inventoryDetailModel.getProductId());

                double newTotalPrice = inventoryDetailModel.getTotalPrice() + productIdAndActualQuantityAndTotalPriceOfExportReceiptMap.get(inventoryDetailModel.getProductId()).totalPrice()
                        - totalPriceInImportReceiptByProductIdMap.get(inventoryDetailModel.getProductId());

                inventoryDetailModel.setQuantity(newQuantity);
                inventoryDetailModel.setTotalPrice(newTotalPrice);
                inventoryDetailModelsToUpdate.add(inventoryDetailModel);
            }
        }

        // xóa phiếu nhập và phiếu xuất nếu có
        try {
            receiptTransactionService.deleteImportReceiptWithExportsAndUpdateInventory(importReceiptModelTable.getId(), exportReceiptIds, inventoryDetailModelsToUpdate);
//            inventoryDetailService.update(inventoryDetailModelsToUpdate);
        }
        catch (DaoException e) {
            AlertUtils.alert(e.getMessage(), "ERROR", "Lỗi", "Lỗi hệ thống");
        }
        return true;
    }
}
