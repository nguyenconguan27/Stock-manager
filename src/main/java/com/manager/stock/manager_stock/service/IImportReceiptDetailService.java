package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IImportReceiptDetailService {
    List<ImportReceiptDetailModel> findAllByImportReceiptId(long importReceiptId);
    long save(List<ImportReceiptDetailModel> importReceiptDetailModels, long importReceiptId);
    void update(List<ImportReceiptDetailModel> importReceiptDetailModels);
}
