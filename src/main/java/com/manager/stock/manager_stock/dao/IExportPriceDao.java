package com.manager.stock.manager_stock.dao;

import com.manager.stock.manager_stock.model.ExportPriceModel;

/**
 * @author Trọng Hướng
 */
public interface IExportPriceDao {
    double findExportPriceByProductIdAndExportTime(long productId);
    void save(ExportPriceModel exportPriceModel);
}
