package com.manager.stock.manager_stock.dao.transactionalDao.impl;

import com.manager.stock.manager_stock.dao.impl.AbstractDao;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.dao.transactionalDao.IReceiptTransactionDao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ReceiptTransactionDao extends AbstractDao<Void> implements IReceiptTransactionDao {

    @Override
    public void deleteImportAndExportReceipts(long importReceiptId, List<Long> exportReceiptIds) {
        if (exportReceiptIds == null || exportReceiptIds.isEmpty()) {
            throw new DaoException("ExportReceiptIds không được rỗng.");
        }

        String sqlDeleteImportReceipt = "DELETE FROM import_receipt WHERE id = ?";
        String placeholders = exportReceiptIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sqlDeleteExportReceipt = "DELETE FROM export_receipt WHERE id IN (" + placeholders + ")";

        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            logger.info("Start deleting import receipt with id {}", importReceiptId);
            deleteWithinTransaction(sqlDeleteImportReceipt, connection, importReceiptId);
            logger.info("Deleted import receipt with id {}", importReceiptId);

            logger.info("Start deleting export receipts with ids {}", exportReceiptIds);
            deleteWithinTransaction(sqlDeleteExportReceipt, connection, exportReceiptIds.toArray());
            logger.info("Deleted export receipts with ids {}", exportReceiptIds);

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
