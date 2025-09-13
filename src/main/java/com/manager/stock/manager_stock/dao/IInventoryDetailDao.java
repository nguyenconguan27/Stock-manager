package com.manager.stock.manager_stock.dao;

import com.manager.stock.manager_stock.model.InventoryDetailModel;
import com.manager.stock.manager_stock.model.dto.ProductIdAndCodeAndNameAndQuantityInStock;

import java.sql.Connection;
import java.util.List;

/**
 * @author Trọng Hướng
 */public interface IInventoryDetailDao {
     List<InventoryDetailModel> findAllByAcademicYearAndProductId(List<Long> productIds, int academicYear);
     void save(List<InventoryDetailModel> inventoryDetailModels);
     void update(List<InventoryDetailModel> inventoryDetailModels);
     void updateWithTransaction(List<InventoryDetailModel> inventoryDetailModels, Connection connection);
     int findQuantityInStockByProductIdAndAcademicYear(long productId, int academicYear);
     List<ProductIdAndCodeAndNameAndQuantityInStock> findProductHaveMaxQuantityByProductGroup(long productGroupId);
     List<ProductIdAndCodeAndNameAndQuantityInStock> findProductHaveMinQuantityByProductGroup(long productGroupId);
}
