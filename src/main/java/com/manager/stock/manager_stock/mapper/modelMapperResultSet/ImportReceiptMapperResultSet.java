package com.manager.stock.manager_stock.mapper.modelMapperResultSet;

import com.manager.stock.manager_stock.model.ImportReceiptModel;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptMapperResultSet implements RowMapper<ImportReceiptModel>{
    @Override
    public ImportReceiptModel mapRow(ResultSet resultSet) throws SQLException {
        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();
            ImportReceiptModel importReceiptModel = new ImportReceiptModel();
            for(int i=1;i<=columnCount;i++){
                String columnName = resultSetMetaData.getColumnName(i);
                switch(columnName){
                    case "id":
                        importReceiptModel.setId(resultSet.getLong(columnName));
                        break;
                    case "invoice_number":
                        importReceiptModel.setInvoiceNumber(resultSet.getString(columnName));
                        break;
                    case "create_at":
                        importReceiptModel.setCreateAt(resultSet.getString(columnName));
                        break;
                    case "delivered_by":
                        importReceiptModel.setDeliveredBy(resultSet.getString(columnName));
                        break;
                    case "invoice":
                        importReceiptModel.setInvoice(resultSet.getString(columnName));
                        break;
                    case "company_name":
                        importReceiptModel.setCompanyName(resultSet.getString(columnName));
                        break;
                    case "warehouse_name":
                        importReceiptModel.setWarehouseName(resultSet.getString(columnName));
                        break;
                    case "total_price":
                        importReceiptModel.setTotalPrice(resultSet.getDouble(columnName));
                        break;
                    case "total_price_in_word":
                        importReceiptModel.setTotalPriceInWord(resultSet.getString(columnName));
                        break;
                    case "academic_year":
                        importReceiptModel.setAcademicYear(resultSet.getInt(columnName));
                        break;
                }
            }
            return importReceiptModel;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
