package com.manager.stock.manager_stock.mapper.modelMapperResultSet;

import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptDetailMapperResultSet implements RowMapper<ImportReceiptDetailModel> {
    @Override
    public ImportReceiptDetailModel mapRow(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        ImportReceiptDetailModel importReceiptDetailModel = new ImportReceiptDetailModel();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnLabel(i).toLowerCase();
            switch (columnName) {
                case "id":
                    importReceiptDetailModel.setId(resultSet.getLong(columnName));
                    break;
                case "import_receipt_id":
                    importReceiptDetailModel.setImportReceiptId(resultSet.getLong(columnName));
                    break;
                case "product_id":
                    importReceiptDetailModel.setProductId(resultSet.getLong(columnName));
                    break;
                case "planned_quantity":
                    importReceiptDetailModel.setPlannedQuantity(resultSet.getInt(columnName));
                    break;
                case "actual_quantity":
                    importReceiptDetailModel.setActualQuantity(resultSet.getInt(columnName));
                    break;
                case "unit_price":
                    importReceiptDetailModel.setUnitPrice(resultSet.getDouble(columnName));
                    break;
                case "total_price":
                    importReceiptDetailModel.setTotalPrice(resultSet.getDouble(columnName));
                    break;
                case "product_name":
                    importReceiptDetailModel.setProductName(resultSet.getString(columnName));
                    break;
                case "product_code":
                    importReceiptDetailModel.setProductCode(resultSet.getString(columnName));
                    break;
                case "unit":
                    importReceiptDetailModel.setUnit(resultSet.getString(columnName));
                    break;
            }
        }
        importReceiptDetailModel.setTotalPrice(importReceiptDetailModel.getUnitPrice() * importReceiptDetailModel.getActualQuantity());
        return importReceiptDetailModel;
    }
}
