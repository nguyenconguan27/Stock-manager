package com.manager.stock.manager_stock.mapper.modelMapperResultSet;

import com.manager.stock.manager_stock.model.ExportPriceModel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Trọng Hướng
 */
public class ExportPriceMapperResultSet implements RowMapper<ExportPriceModel>{
    @Override
    public ExportPriceModel mapRow(ResultSet resultSet) throws SQLException {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            ExportPriceModel exportPriceModel = new ExportPriceModel();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                switch (columnName) {
                    case "id":
                        exportPriceModel.setId(resultSet.getLong(i));
                        break;
                    case "product_id":
                        exportPriceModel.setProductId(resultSet.getLong(i));
                        break;
//                    case "export_time":
//                        exportPriceModel.setExportTime(resultSet.getLong(i));
//                        break;
                    case "export_price":
                        exportPriceModel.setExportPrice(resultSet.getDouble(i));
                        break;
                    case "quantity_in_stock":
                        exportPriceModel.setQuantityInStock(resultSet.getInt(columnName));
                        break;
                    case "quantity_imported":
                        exportPriceModel.setQuantityImported(resultSet.getInt(columnName));
                        break;
                    case "total_price_import":
                        exportPriceModel.setTotalImportPrice(resultSet.getDouble(columnName));
                        break;
                    case "total_price_in_stock":
                        exportPriceModel.setTotalPriceInStock(resultSet.getDouble(columnName));
                        break;
                }
            }
            return exportPriceModel;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
