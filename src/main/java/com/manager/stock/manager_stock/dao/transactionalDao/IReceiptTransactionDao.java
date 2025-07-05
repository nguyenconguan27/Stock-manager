package com.manager.stock.manager_stock.dao.transactionalDao;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IReceiptTransactionDao {
    void deleteImportAndExportReceipts(long importReceiptId, List<Long> exportReceiptIds);
}
