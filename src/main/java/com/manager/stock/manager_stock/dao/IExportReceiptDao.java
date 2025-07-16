package com.manager.stock.manager_stock.dao;

import com.manager.stock.manager_stock.model.ExportReceiptModel;
import com.manager.stock.manager_stock.model.dto.ExportReceiptIdAndCreateDate;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;
import com.sun.source.tree.LiteralTree;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public interface IExportReceiptDao {
    List<ExportReceiptIdAndCreateDate> findExportReceiptIdAndCreatedAtByProductIdsAndYearAndMinCreatedAt(List<Long> productIds, int academicYear, LocalDate minCreateAt);
    List<ProductIdAndActualQuantityAndTotalPriceOfReceipt> findProductIdAndTotalPriceAndTotalQuantityByExportReceipt(List<Long> exportReceiptIds);
    void deleteByIds(List<Long> ids);
    void deleteByIdsWithTransaction(List<Long> ids, Connection connection);
    List<ExportReceiptModel> findAllByAcademicYear(int academicYear);
    long save(ExportReceiptModel exportReceiptModel);
    void update(ExportReceiptModel exportReceiptModel);
}
