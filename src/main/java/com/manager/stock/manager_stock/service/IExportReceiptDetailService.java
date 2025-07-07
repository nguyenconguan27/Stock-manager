package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IExportReceiptDetailService {
    List<ExportReceiptDetailModel> findAllByExportReceipt(long exportReceiptId);
}
