package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.ImportReceiptModel;

import java.util.List;
import java.util.Optional;

/**
 * @author Trọng Hướng
 */
public interface IImportReceiptService {
    List<ImportReceiptModel> findAllByAcademicYear(Optional<Integer> academicYear);
    List<ImportReceiptModel> findAllByProductAndYear(long productId, int year);
    ImportReceiptModel findById(long id);
    long save(ImportReceiptModel importReceiptModel);
    void update(ImportReceiptModel importReceiptModel);
    void delete(long id);
    void commit();
    void rollback();
}
