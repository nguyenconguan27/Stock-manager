package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IImportReceiptDao;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ImportReceiptMapperResultSet;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.screen.transaction.presenter.ImportReceiptPresenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptDaoImpl extends AbstractDao<ImportReceiptModel> implements IImportReceiptDao {
    private final Logger logger = LoggerFactory.getLogger(ImportReceiptPresenter.class);
    private static ImportReceiptDaoImpl instance;

    private ImportReceiptDaoImpl() {

    }

    public static IImportReceiptDao getInstance() {
        if(instance == null) {
            instance = new ImportReceiptDaoImpl();
        }
        return instance;
    }

    @Override
    public List<ImportReceiptModel> findAllByAcademicYear(int academicYear) throws DaoException {
        String sql = "select ir.*, sum(ird.actual_quantity * ird.unit_price) as total_price_receipt from import_receipt ir \n" +
                "join import_receipt_detail ird on\n" +
                "ir.id = ird.import_receipt_id\n" +
                "where ir.academic_year = ? \n" +
                "group by ir.id;";
        return query(sql, new ImportReceiptMapperResultSet(), academicYear);
    }

    @Override
    public List<ImportReceiptModel> findAllByProductIdAndYear(long productId, int academicYear) {
        String sql = "select ir.*, sum(ird.actual_quantity * ird.unit_price) as total_price_receipt from import_receipt ir \n" +
                "join import_receipt_detail ird on\n" +
                "ir.id = ird.import_receipt_id\n" +
                "where ir.academic_year = ? and ird.product_id = ?\n" +
                "group by ir.id order by PARSEDATETIME(ir.create_at, 'dd/MM/yyyy HH:mm:ss') asc;";
        return query(sql, new ImportReceiptMapperResultSet(), academicYear, productId);
    }

    @Override
    public ImportReceiptModel findById(long id) {
        String sql = "select * from import_receipt where id = ?";
        List<ImportReceiptModel> importReceiptModels = query(sql, new ImportReceiptMapperResultSet(), id);
        if(importReceiptModels.isEmpty()) {
            return null;
        } else {
            return importReceiptModels.get(0);
        }
    }

    @Override
    public long save(ImportReceiptModel importReceiptModel) throws DaoException {
        String sqlSelect = "select * from import_receipt where id = ?";
        List<ImportReceiptModel> importReceiptModels = query(sqlSelect, new ImportReceiptMapperResultSet(), importReceiptModel.getId());
        String sql = "INSERT INTO import_receipt (invoice_number, create_at, delivered_by, invoice, company_name, warehouse_name, total_price, total_price_in_word, academic_year) " +
                    " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        if(importReceiptModels.isEmpty()) {
            List<Object[]> parameters = new ArrayList<>();
            parameters.add(new Object[]{
                    importReceiptModel.getInvoice(),
                    importReceiptModel.getCreateAt(),
                    importReceiptModel.getDeliveredBy(),
                    importReceiptModel.getInvoiceNumber(),
                    importReceiptModel.getCompanyName(),
                    importReceiptModel.getWarehouseName(),
                    importReceiptModel.getTotalPrice(),
                    importReceiptModel.getTotalPriceInWord(),
                    importReceiptModel.getAcademicYear()
            });
            return save(sql, parameters);
        } else {
            update(importReceiptModel);
            return importReceiptModel.getId();
        }
    }

    @Override
    public long update(ImportReceiptModel importReceiptModel) throws DaoException {
        String sql = "UPDATE import_receipt set invoice = ?, create_at = ?, delivered_by = ?, " +
                    "invoice_number = ?, company_name = ?, warehouse_name = ?, total_price = ?, total_price_in_word = ?, academic_year = ? " +
                    " where id = ?";
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{
                importReceiptModel.getInvoiceNumber(),
                importReceiptModel.getCreateAt(),
                importReceiptModel.getDeliveredBy(),
                importReceiptModel.getInvoice(),
                importReceiptModel.getCompanyName(),
                importReceiptModel.getWarehouseName(),
                importReceiptModel.getTotalPrice(),
                importReceiptModel.getTotalPriceInWord(),
                importReceiptModel.getAcademicYear(),
                importReceiptModel.getId()
        });
        return save(sql, parameters);
    }

    @Override
    public void delete(List<Long> ids) {
        String sql = "DELETE from import_receipt where id in (";
        for(int i = 0; i < ids.size(); i++) {
            sql += ids.get(i);
            if(i != ids.size() - 1) {
                sql += ",";
            }
        }
        sql += ")";
        delete(sql);
    }

    @Override
    public void deleteByIdWithTransaction(long id, Connection connection) throws DaoException{
        String sql = "DELETE FROM import_receipt where id = ?";
        deleteWithinTransaction(sql, connection, id);
    }

    @Override
    public void commit() {
        super.commit();
    }

    @Override
    public void rollback() {
        super.rollback();
    }

    @Override
    public void delete(long id) {
        String sqlDetail = "delete from import_receipt_detail where import_receipt_id = ?";
        String sql = "delete from import_receipt where id = ?";
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[] {
                id
        });
        save(sqlDetail, parameters);
        save(sql, parameters);
    }
}
