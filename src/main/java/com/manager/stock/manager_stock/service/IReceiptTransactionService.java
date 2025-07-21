package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.InventoryDetailModel;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IReceiptTransactionService {
    void deleteImportReceiptWithExportsAndUpdateInventory(long importReceiptId, List<Long> exportReceiptIds, List<InventoryDetailModel> inventoryDetailModels);
}
