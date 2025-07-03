package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.GenericDao;
import com.manager.stock.manager_stock.dao.IImportReceiptDao;
import com.manager.stock.manager_stock.dao.IImportReceiptDetailDao;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ImportReceiptDetailMapperResultSet;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;

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
                "join product p on p.id = ir.id \n" +
                "WHERE import_receipt_id = ?";
        return query(sql, new ImportReceiptDetailMapperResultSet(), importReceiptId);
    }

    @Override
    public long save(List<ImportReceiptDetailModel> importReceiptDetailModels, long importReceiptId) throws DaoException {
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
}
