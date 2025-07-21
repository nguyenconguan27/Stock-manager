package com.manager.stock.manager_stock.dao;

import com.manager.stock.manager_stock.model.ImportReceiptModel;

import java.sql.Connection;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IImportReceiptDao {
    List<ImportReceiptModel> findAllByAcademicYear(int academicYear);
    long save(ImportReceiptModel importReceiptModel);
    void update(ImportReceiptModel importReceiptModel);
    void delete(List<Long> ids);
    void deleteByIdWithTransaction(long id, Connection connection);
    void commit();
    void rollback();
}
