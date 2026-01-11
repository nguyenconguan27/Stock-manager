package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IImportReceiptDetailDao;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ImportReceiptDetailMapperResultSet;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptDetailDaoImpl extends AbstractDao<ImportReceiptDetailModel> implements IImportReceiptDetailDao {

    private static ImportReceiptDetailDaoImpl instance;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private ImportReceiptDetailDaoImpl() {

    }

    public static ImportReceiptDetailDaoImpl getInstance() {
        if (instance == null) {
            instance = new ImportReceiptDetailDaoImpl();
        }
        return instance;
    }

    @Override
    public List<ImportReceiptDetailModel> findAllByImportReceiptId(long importReceiptId) {

        String sql = "SELECT ird.*, p.code as product_code, p.unit, p.name as product_name FROM import_receipt_detail ird\n" +
                "join import_receipt ir on ird.import_receipt_id = ir.id \n" +
                "join product p on p.id = ird.product_id \n" +
                "WHERE import_receipt_id = ?";
        return query(sql, new ImportReceiptDetailMapperResultSet(), importReceiptId);
    }

    @Override
    public long save(List<ImportReceiptDetailModel> importReceiptDetailModels, long importReceiptId) throws DaoException {
        try {
            String sql = "INSERT INTO import_receipt_detail (import_receipt_id, product_id, planned_quantity, actual_quantity, unit_price, product_name)" +
                    " values (?, ?, ?, ?, ?, ?)";
            List<Object[]> parameters = new ArrayList<>();
            for(ImportReceiptDetailModel importReceiptDetailModel : importReceiptDetailModels){
                parameters.add(new Object[]{
                        importReceiptId,
                        importReceiptDetailModel.getProductId(),
                        importReceiptDetailModel.getPlannedQuantity(),
                        importReceiptDetailModel.getActualQuantity(),
                        importReceiptDetailModel.getUnitPrice(),
                        importReceiptDetailModel.getProductName()
                });
            }
            return save(sql, parameters);
        }
        catch (Exception e) {
            throw new DaoException("Đã xảy ra lỗi hệ thống, vui lòng thử lại sau.");
        }
    }

    @Override
    public void update(List<ImportReceiptDetailModel> importReceiptDetailModels) throws DaoException {
        String sql = "UPDATE import_receipt_detail set planned_quantity = ?, actual_quantity = ?, unit_price = ?" +
                    " where id = ?";
        List<Object[]> parameters = new ArrayList<>();
        for(ImportReceiptDetailModel importReceiptDetailModel : importReceiptDetailModels){
            parameters.add(new Object[]{
                importReceiptDetailModel.getPlannedQuantity(),
                importReceiptDetailModel.getActualQuantity(),
                importReceiptDetailModel.getUnitPrice(),
                importReceiptDetailModel.getId()
            });
        }
        save(sql, parameters);
    }

    @Override
    public long calculateTotalQuantityImportedByProduct(long productId, LocalDateTime startDate, LocalDateTime endTime, LocalDateTime oldImportDate) throws DaoException{
        String sql = "select sum(DB.IMPORT_RECEIPT_DETAIL.ACTUAL_QUANTITY) as total_quantity_imported from DB.IMPORT_RECEIPT_DETAIL\n" +
                "join DB.IMPORT_RECEIPT on DB.IMPORT_RECEIPT.id = DB.IMPORT_RECEIPT_DETAIL.IMPORT_RECEIPT_ID\n" +
                "join DB.PRODUCT on DB.PRODUCT.id = DB.IMPORT_RECEIPT_DETAIL.PRODUCT_ID\n" +
                "where DB.PRODUCT.id = ?\n" +
                "and CAST(PARSEDATETIME(DB.IMPORT_RECEIPT.CREATE_AT, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP) >= CAST(PARSEDATETIME(?, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP)\n" +
                "and CAST(PARSEDATETIME(DB.IMPORT_RECEIPT.CREATE_AT, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP) < CAST(PARSEDATETIME(?, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP)\n" +
                "and CAST(PARSEDATETIME(DB.IMPORT_RECEIPT.CREATE_AT, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP) != CAST(PARSEDATETIME(?, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP)\n;";
        List<Long> totalQuantityImported = query(sql, rs -> rs.getLong("TOTAL_QUANTITY_IMPORTED"), productId, formatter.format(startDate), formatter.format(endTime), formatter.format(oldImportDate));
        if(totalQuantityImported.isEmpty()) {
            return 0;
        }
        return totalQuantityImported.get(0);
    }

    @Override
    public void save(ImportReceiptDetailModel detailModel, long receiptId) {
        String sqlQuery = "select * from import_receipt_detail where id = ?";
        String sqlUpdate = "UPDATE import_receipt_detail set planned_quantity = ?, actual_quantity = ?, unit_price = ?" +
                " where id = ?";
        String sqlInsert = "INSERT INTO import_receipt_detail (import_receipt_id, product_id, planned_quantity, actual_quantity, unit_price, product_name)" +
                " values (?, ?, ?, ?, ?, ?)";
        List<ImportReceiptDetailModel> importReceiptDetailModels = query(sqlQuery, new ImportReceiptDetailMapperResultSet(),detailModel.getId());
        List<Object[]> parameters = new ArrayList<>();
        if(importReceiptDetailModels.isEmpty()) {
            parameters.add(new Object[]{
                    receiptId,
                    detailModel.getProductId(),
                    detailModel.getPlannedQuantity(),
                    detailModel.getActualQuantity(),
                    detailModel.getUnitPrice(),
                    detailModel.getProductName()
            });
            save(sqlInsert, parameters);
        } else {
            parameters.add(new Object[]{
                    detailModel.getPlannedQuantity(),
                    detailModel.getActualQuantity(),
                    detailModel.getUnitPrice(),
                    detailModel.getId()
            });
            save(sqlUpdate, parameters);
        }
    }

    @Override
    public double calculateTotalPriceImportedByProduct(long productId, LocalDateTime startDate, LocalDateTime endTime, LocalDateTime oldImportDate) throws DaoException {
        String sql = "select sum(DB.IMPORT_RECEIPT_DETAIL.ACTUAL_QUANTITY * DB.IMPORT_RECEIPT_DETAIL.ACTUAL_QUANTITY) as total_price_imported from DB.IMPORT_RECEIPT_DETAIL\n" +
                "join DB.IMPORT_RECEIPT on DB.IMPORT_RECEIPT.id = DB.IMPORT_RECEIPT_DETAIL.IMPORT_RECEIPT_ID\n" +
                "join DB.PRODUCT on DB.PRODUCT.id = DB.IMPORT_RECEIPT_DETAIL.PRODUCT_ID\n" +
                "where DB.PRODUCT.id = ?\n" +
                "and CAST(PARSEDATETIME(DB.IMPORT_RECEIPT.CREATE_AT, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP) >= CAST(PARSEDATETIME(?, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP)\n" +
                "and CAST(PARSEDATETIME(DB.IMPORT_RECEIPT.CREATE_AT, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP) < CAST(PARSEDATETIME(?, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP)\n" +
                "and CAST(PARSEDATETIME(DB.IMPORT_RECEIPT.CREATE_AT, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP) != CAST(PARSEDATETIME(?, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP)\n;";
        List<Double> totalPriceImported = query(sql, rs -> rs.getDouble("TOTAL_PRICE_IMPORTED"), productId, formatter.format(startDate), formatter.format(endTime), formatter.format(oldImportDate));
        if(totalPriceImported.isEmpty()) {
            return 0;
        }
        return totalPriceImported.get(0);
    }

    @Override
    public void update(ImportReceiptDetailModel importReceiptDetailModels) {
        try {
            String sql = "UPDATE import_receipt_detail set unit_price = ?" +
                    " where id = ?";
        }
        catch (Exception e) {

        }
    }

    @Override
    public List<ProductIdAndActualQuantityAndTotalPriceOfReceipt> findAllProductIdByImportReceipt(long importReceiptId) throws DaoException{
        String sql = "select product_id, actual_quantity, total_price from import_receipt_detail ird  where ird.import_receipt_id = ?;";
        return query(sql, rs -> new ProductIdAndActualQuantityAndTotalPriceOfReceipt(
                rs.getLong("PRODUCT_ID"),
                rs.getInt("ACTUAL_QUANTITY"),
                rs.getDouble("TOTAL_PRICE")
        ), importReceiptId);
    }

    @Override
    public void deleteByImportReceipt(long importReceiptId) throws DaoException {
        String sql = "DELETE from import_receipt_detail where import_receipt_id = ?";
        delete(sql, importReceiptId);
    }

    @Override
    public void deleteByIds(Set<Long> ids) throws DaoException {
        String idsStr = ids.stream().map(Object::toString).collect(Collectors.joining(","));
        String sql = "DELETE from import_receipt_detail where id in  (" + idsStr + ")";
        delete(sql, ids);
    }
}
