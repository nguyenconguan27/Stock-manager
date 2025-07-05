package com.manager.stock.manager_stock.dao;

import com.manager.stock.manager_stock.model.InventoryDetailModel;

import java.sql.Connection;
import java.util.List;

/**
 * @author Trọng Hướng
 */public interface IInventoryDetailDao {
     List<InventoryDetailModel> findAllByAcademicYearAndProductId(List<Long> productIds, int academicYear);
     void save(List<InventoryDetailModel> inventoryDetailModels);
     void update(List<InventoryDetailModel> inventoryDetailModels);
     void updateWithTransaction(List<InventoryDetailModel> inventoryDetailModels, Connection connection);
}
