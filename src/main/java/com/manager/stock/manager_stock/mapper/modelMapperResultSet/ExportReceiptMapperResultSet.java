package com.manager.stock.manager_stock.mapper.modelMapperResultSet;

import com.manager.stock.manager_stock.model.ExportReceiptModel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptMapperResultSet implements RowMapper<ExportReceiptModel> {
    @Override
    public ExportReceiptModel mapRow(ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        ExportReceiptModel exportReceiptModel = new ExportReceiptModel();
        for(int i = 1; i <= columnCount; i++){
            String columnName = resultSetMetaData.getColumnName(i);
            switch (columnName){
                case "id":
                    exportReceiptModel.setId(resultSet.getInt(columnName));
                    break;
                case "invoice_number":
                    exportReceiptModel.setInvoiceNumber(resultSet.getString(columnName));
                    break;
                case "create_at":
                    exportReceiptModel.setCreateAt(resultSet.getString(columnName));
                    break;
                case "receiver":
                    exportReceiptModel.setReceiver(resultSet.getString(columnName));
                    break;
                case "receive_address":
                    exportReceiptModel.setReceiveAddress(resultSet.getString(columnName));
                    break;
                case "reason":
                    exportReceiptModel.setReason(resultSet.getString(columnName));
                    break;
                case "warehouse":
                    exportReceiptModel.setWareHouse(resultSet.getString(columnName));
                    break;
                case "total_price_receipt":
                    exportReceiptModel.setTotalPrice(resultSet.getDouble(columnName));
                    break;
                case "total_price_in_word":
                    exportReceiptModel.setTotalPriceInWord(resultSet.getString(columnName));
                    break;
                case "academic_year":
                    exportReceiptModel.setAcademicYear(resultSet.getInt(columnName));
                    break;
            }
        }
        return exportReceiptModel;
    }
}
