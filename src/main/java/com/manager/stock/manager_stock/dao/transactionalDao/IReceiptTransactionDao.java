package com.manager.stock.manager_stock.dao.transactionalDao;

import com.manager.stock.manager_stock.model.InventoryDetailModel;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IReceiptTransactionDao {
    void deleteImportReceiptWithExportsAndUpdateInventory(long importReceiptId, List<Long> exportReceiptIds, List<InventoryDetailModel> inventoryDetailModels);
}
