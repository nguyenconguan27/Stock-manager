package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IInventoryDetailDao;
import com.manager.stock.manager_stock.exception.CanNotFoundException;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.InventoryDetailMapperResultSet;
import com.manager.stock.manager_stock.model.InventoryDetailModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class InventoryDetailDaoImpl extends AbstractDao<InventoryDetailModel> implements IInventoryDetailDao {
    private static InventoryDetailDaoImpl instance;
    public static InventoryDetailDaoImpl getInstance() {
        if (instance == null) {
            instance = new InventoryDetailDaoImpl();
        }
        return instance;
    }

    @Override
    public List<InventoryDetailModel> findAllByAcademicYearAndProductId(List<Long> productIds, int academicYear) throws DaoException {
        String sql = "select * from inventory_detail where academic_year = ? and product_id in (";
        for(int i = 0; i < productIds.size(); i++) {
            sql += productIds.get(i);
            if(i <  productIds.size()-1) {
                sql += ",";
            }
        }
        sql += " )";
        return query(sql, new InventoryDetailMapperResultSet(), academicYear);
    }

    @Override
    public void save(List<InventoryDetailModel> inventoryDetailModels) throws DaoException {
        String sql = "INSERT INTO inventory_detail(product_id, quantity, total_price, academic_year) " +
                "values(?, ?, ?, ?)";
        List<Object[]> parameters = new ArrayList<>();
        for(InventoryDetailModel inventoryDetailModel : inventoryDetailModels){
            parameters.add(new Object[]{
                inventoryDetailModel.getProductId(),
                inventoryDetailModel.getQuantity(),
                inventoryDetailModel.getTotalPrice(),
                inventoryDetailModel.getAcademicYear()
            });
        }
        save(sql, parameters);
    }

    @Override
    public void update(List<InventoryDetailModel> inventoryDetailModels) {
        String sql = "UPDATE inventory_detail set quantity = ?, total_price = ? where id = ?";
        List<Object[]> parameters = new ArrayList<>();
        for(InventoryDetailModel inventoryDetailModel : inventoryDetailModels){
            parameters.add(new Object[]{
                inventoryDetailModel.getQuantity(),
                inventoryDetailModel.getTotalPrice(),
                inventoryDetailModel.getId()
            });
        }
        save(sql, parameters);
    }
}
