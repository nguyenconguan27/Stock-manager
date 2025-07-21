package com.manager.stock.manager_stock.mapper.modelMapperResultSet;

import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptDetailMapperResultSet implements RowMapper<ExportReceiptDetailModel> {
    @Override
    public ExportReceiptDetailModel mapRow(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        ExportReceiptDetailModel exportReceiptDetailModel = new ExportReceiptDetailModel();
        for(int i = 1; i <= columnCount; i++){
            String columnName = metaData.getColumnName(i);
            switch (columnName){
                case "id":
                    exportReceiptDetailModel.setId(resultSet.getLong(columnName));
                    break;
                case "export_receipt_id":
                    exportReceiptDetailModel.setExportReceiptId(resultSet.getLong(columnName));
                    break;
                case "product_id":
                    exportReceiptDetailModel.setProductId(resultSet.getLong(columnName));
                    break;
                case "planned_quantity":
                    exportReceiptDetailModel.setPlannedQuantity(resultSet.getInt(columnName));
                    break;
                case "actual_quantity":
                    exportReceiptDetailModel.setActualQuantity(resultSet.getInt(columnName));
                    break;
                case "unit_price":
                    exportReceiptDetailModel.setUnitPrice(resultSet.getDouble(columnName));
                    break;
                case "product_name":
                    exportReceiptDetailModel.setProductName(resultSet.getString(columnName));
                    break;
                case "product_code":
                    exportReceiptDetailModel.setProductCode(resultSet.getString(columnName));
                    break;
            }
        }
        exportReceiptDetailModel.setTotalPrice(exportReceiptDetailModel.getUnitPrice() * exportReceiptDetailModel.getActualQuantity());
        return exportReceiptDetailModel;
    }
}
