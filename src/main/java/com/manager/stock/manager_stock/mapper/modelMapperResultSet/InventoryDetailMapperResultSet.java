package com.manager.stock.manager_stock.mapper.modelMapperResultSet;

import com.manager.stock.manager_stock.model.InventoryDetailModel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Trọng Hướng
 */
public class InventoryDetailMapperResultSet implements RowMapper<InventoryDetailModel> {
    @Override
    public InventoryDetailModel mapRow(ResultSet resultSet) throws SQLException {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            InventoryDetailModel inventoryDetailModel = new InventoryDetailModel();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i).toLowerCase();
                switch (columnName) {
                    case "id":
                        inventoryDetailModel.setId(resultSet.getLong(columnName));
                        break;
                    case "product_id":
                        inventoryDetailModel.setProductId(resultSet.getLong(columnName));
                        break;
                    case "quantity":
                        inventoryDetailModel.setQuantity(resultSet.getInt(columnName));
                        break;
                    case "total_price":
                        inventoryDetailModel.setTotalPrice(resultSet.getDouble(columnName));
                        break;
                    case "academic_year":
                        inventoryDetailModel.setAcademicYear(resultSet.getInt(columnName));
                        break;
                }
            }
            return inventoryDetailModel;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
