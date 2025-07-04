package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IImportReceiptDetailDao;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ImportReceiptDetailMapperResultSet;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;

import java.util.*;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptDetailDaoImpl extends AbstractDao<ImportReceiptDetailModel> implements IImportReceiptDetailDao {

    private static ImportReceiptDetailDaoImpl instance;
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
        String sql = "SELECT ird.*, p.code as product_code, p.\"name\" as product_name FROM import_receipt_detail ird\n" +
                "join import_receipt ir on ird.import_receipt_id = ir.id \n" +
                "join product p on p.id = ird.product_id \n" +
                "WHERE import_receipt_id = ?";
        return query(sql, new ImportReceiptDetailMapperResultSet(), importReceiptId);
    }

    @Override
    public long save(List<ImportReceiptDetailModel> importReceiptDetailModels, long importReceiptId) throws DaoException {
        try {
            String sql = "INSERT INTO import_receipt_detail (import_receipt_id, product_id, planned_quantity, actual_quantity, unit_price, total_price, product_name)" +
                    " values (?, ?, ?, ?, ?, ?, ?)";
            List<Object[]> parameters = new ArrayList<>();
            for(ImportReceiptDetailModel importReceiptDetailModel : importReceiptDetailModels){
                parameters.add(new Object[]{
                        importReceiptId,
                        importReceiptDetailModel.getProductId(),
                        importReceiptDetailModel.getPlannedQuantity(),
                        importReceiptDetailModel.getActualQuantity(),
                        importReceiptDetailModel.getUnitPrice(),
                        importReceiptDetailModel.getTotalPrice(),
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
        String sql = "UPDATE import_receipt_detail set planned_quantity = ?, actual_quantity = ?, unit_price = ?, total_price = ?" +
                    " where id = ?";
        List<Object[]> parameters = new ArrayList<>();
        for(ImportReceiptDetailModel importReceiptDetailModel : importReceiptDetailModels){
            parameters.add(new Object[]{
                importReceiptDetailModel.getPlannedQuantity(),
                importReceiptDetailModel.getActualQuantity(),
                importReceiptDetailModel.getUnitPrice(),
                importReceiptDetailModel.getTotalPrice(),
                importReceiptDetailModel.getId()
            });
        }
        save(sql, parameters);
    }

    @Override
    public List<ProductIdAndActualQuantityAndTotalPriceOfReceipt> findAllProductIdByImportReceipt(long importReceiptId) throws DaoException{
        String sql = "select product_id, actual_quantity, total_price from import_receipt_detail ird  where ird.import_receipt_id = ?;";
        return query(sql, rs -> new ProductIdAndActualQuantityAndTotalPriceOfReceipt(
                rs.getLong("product_id"),
                rs.getInt("actual_quantity"),
                rs.getDouble("total_price")
        ), importReceiptId);
    }

    @Override
    public void deleteByImportReceipt(long importReceiptId) throws DaoException {
        String sql = "DELETE from import_receipt_detail where import_receipt_id = ?";
        delete(sql, importReceiptId);
    }
}
