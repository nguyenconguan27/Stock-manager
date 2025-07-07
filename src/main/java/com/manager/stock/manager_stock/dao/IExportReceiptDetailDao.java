package com.manager.stock.manager_stock.dao;

import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IExportReceiptDetailDao {
    List<ExportReceiptDetailModel> findAllByExPortReceipt(long exportReceiptId);
}
