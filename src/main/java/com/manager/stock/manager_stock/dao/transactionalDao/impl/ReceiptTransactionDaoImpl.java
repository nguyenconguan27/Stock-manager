package com.manager.stock.manager_stock.dao.transactionalDao.impl;

import com.manager.stock.manager_stock.dao.IExportReceiptDao;
import com.manager.stock.manager_stock.dao.IImportReceiptDao;
import com.manager.stock.manager_stock.dao.IInventoryDetailDao;
import com.manager.stock.manager_stock.dao.impl.AbstractDao;
import com.manager.stock.manager_stock.dao.impl.ExportReceiptDaoImpl;
import com.manager.stock.manager_stock.dao.impl.ImportReceiptDaoImpl;
import com.manager.stock.manager_stock.dao.impl.InventoryDetailDaoImpl;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.dao.transactionalDao.IReceiptTransactionDao;
import com.manager.stock.manager_stock.model.InventoryDetailModel;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ReceiptTransactionDaoImpl extends AbstractDao<Void> implements IReceiptTransactionDao {

    private static ReceiptTransactionDaoImpl instance;
    private final IExportReceiptDao exportReceiptDao;
    private final IImportReceiptDao importReceiptDao;
    private final IInventoryDetailDao inventoryDetailDao;

    private ReceiptTransactionDaoImpl() {
        exportReceiptDao = ExportReceiptDaoImpl.getInstance();
        importReceiptDao = ImportReceiptDaoImpl.getInstance();
        inventoryDetailDao = InventoryDetailDaoImpl.getInstance();
    }

    public static ReceiptTransactionDaoImpl getInstance() {
        if(instance == null) {
            instance = new ReceiptTransactionDaoImpl();
        }
        return instance;
    }

    @Override
    public void deleteImportReceiptWithExportsAndUpdateInventory(long importReceiptId, List<Long> exportReceiptIds, List<InventoryDetailModel> inventoryDetailModels) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            logger.info("Start deleting import receipt (ID: {}) in transaction", importReceiptId);
            importReceiptDao.deleteByIdWithTransaction(importReceiptId, connection);
            logger.info("Deleted import receipt (ID: {}) in transaction", importReceiptId);

            if (!exportReceiptIds.isEmpty()) {
                logger.info("Start deleting export receipts (IDs: {}) in transaction", exportReceiptIds);
                exportReceiptDao.deleteByIdsWithTransaction(exportReceiptIds, connection);
                logger.info("Deleted export receipts (IDs: {}) in transaction", exportReceiptIds);
            }

            logger.info("Start updating inventory details in transaction. IDs: {}",
                    inventoryDetailModels.stream()
                            .map(InventoryDetailModel::getId)
                            .collect(Collectors.toList())
            );

            inventoryDetailDao.updateWithTransaction(inventoryDetailModels, connection);
            logger.info("Updated inventory details in transaction.");

            connection.commit();
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    logger.warn("Transaction rolled back due to error");
                } catch (SQLException rollbackEx) {
                    logger.error("Failed to rollback transaction", rollbackEx);
                }
            }
            logger.error("Failed to delete import/export receipts", e);
            throw new DaoException("Lỗi khi kết nối với hệ thống, vui lòng thử lại sau.");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    logger.warn("Failed to close connection", closeEx);
                }
            }
        }
    }
}
