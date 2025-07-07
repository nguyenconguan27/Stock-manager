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
        String sql = "SELECT * from import_receipt where academic_year = ? group by academic_year, id ;";
        return query(sql, new ImportReceiptMapperResultSet(), academicYear);
    }

    @Override
    public long save(ImportReceiptModel importReceiptModel) throws DaoException {
        String sql = "INSERT INTO import_receipt (invoice_number, create_at, delivered_by, invoice, company_name, warehouse_name, total_price, total_price_in_word, academic_year) " +
                    " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                importReceiptModel.getAcademicYear()
        });
        return save(sql, parameters);
    }

    @Override
    public void update(ImportReceiptModel importReceiptModel) throws DaoException {
        String sql = "UPDATE import_receipt set invoice_number = ?, create_at = ?, delivered_by = ?, " +
                    "invoice = ?, company_name = ?, warehouse_name = ?, total_price = ?, total_price_in_word = ? " +
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
                importReceiptModel.getId()
        });
        save(sql, parameters);
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
}
