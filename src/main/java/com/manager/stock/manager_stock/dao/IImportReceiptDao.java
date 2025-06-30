package com.manager.stock.manager_stock.dao;

import com.manager.stock.manager_stock.model.ImportReceiptModel;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IImportReceiptDao {
    List<ImportReceiptModel> findAll();
}
