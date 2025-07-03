package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.ImportReceiptModel;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IImportReceiptService {
    List<ImportReceiptModel> findAll();
    long save(ImportReceiptModel importReceiptModel);
    void update(ImportReceiptModel importReceiptModel);
    void delete(long id);
}
