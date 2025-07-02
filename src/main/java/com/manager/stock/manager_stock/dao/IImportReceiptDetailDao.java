package com.manager.stock.manager_stock.dao;

import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IImportReceiptDetailDao {
    List<ImportReceiptDetailModel> findAllByImportReceiptId(long importReceiptId);
    int save(List<ImportReceiptDetailModel> importReceiptDetailModels, long importReceiptId);
}
