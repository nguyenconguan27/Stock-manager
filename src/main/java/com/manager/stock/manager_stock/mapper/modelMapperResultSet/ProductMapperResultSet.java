package com.manager.stock.manager_stock.mapper.modelMapperResultSet;

import com.manager.stock.manager_stock.model.ProductModel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ProductMapperResultSet implements RowMapper<ProductModel>{
    @Override
    public ProductModel mapRow(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        ProductModel productModel = new ProductModel();
        for(int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            switch (columnName) {
                case "id":
                    productModel.setId(resultSet.getLong(columnName));
                    break;
                case "name":
                    productModel.setName(resultSet.getString(columnName));
                    break;
                case "code":
                    productModel.setCode(resultSet.getString(columnName));
                    break;
                case "quantity":
                    productModel.setQuantity(resultSet.getInt(columnName));
                    break;
                case "unit":
                    productModel.setUnit(resultSet.getString(columnName));
                    break;
                case "export_price":
                    productModel.setUnitPrice(resultSet.getInt(columnName));
                    break;
                case "group_id":
                    productModel.setGroupId(resultSet.getLong(columnName));
                    break;
            }
        }
        return productModel;
    }
}
