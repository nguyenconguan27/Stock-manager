package com.manager.stock.manager_stock.mapper.modelMapperResultSet;

import com.manager.stock.manager_stock.model.ProductGroup;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ProductGroupMapperResultSet implements RowMapper<ProductGroup> {
        @Override
        public ProductGroup mapRow(ResultSet resultSet) throws SQLException {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            ProductGroup pg = new ProductGroup();
            for(int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                switch (columnName) {
                    case "id":
                        pg.setId(resultSet.getLong(columnName));
                        break;
                    case "name":
                        pg.setName(resultSet.getString(columnName));
                }
            }
            return pg;
        }
}
