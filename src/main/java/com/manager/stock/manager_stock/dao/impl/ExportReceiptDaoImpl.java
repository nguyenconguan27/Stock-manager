package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IExportReceiptDao;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ExportReceiptModel;
import com.manager.stock.manager_stock.model.dto.ExportReceiptIdAndCreateDate;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptDaoImpl extends AbstractDao<ExportReceiptModel> implements IExportReceiptDao {
    private static ExportReceiptDaoImpl instance;

    private ExportReceiptDaoImpl() {}

    public static ExportReceiptDaoImpl getInstance() {
        if (instance == null) {
            instance = new ExportReceiptDaoImpl();
        }
        return instance;
    }

    @Override
    public List<ExportReceiptIdAndCreateDate> findExportReceiptIdAndCreatedAtByProductIdsAndYearAndMinCreatedAt(List<Long> productIds, int academicYear, LocalDateTime minCreateAt) {
        String idsStr = productIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "select er.id, er.create_at from export_receipt_detail erd \n" +
                    "inner join export_receipt er on\n" +
                    "erd.export_receipt_id = er.id \n" +
                    "where erd.product_id in (" + idsStr + ") \n" +
                    "and er.academic_year = ? \n" +
                    "and to_date(er.create_at, '%d/%m/%Y') >= ?";
        List<Object> parameters = new ArrayList<>(productIds);
        parameters.add(academicYear);
        parameters.add(minCreateAt);
        return query(sql, rs -> new ExportReceiptIdAndCreateDate(
                rs.getLong("id"),
                rs.getString("create_at"),
                rs.getDouble("total_quantity")
        ), parameters.toArray());
    }

    @Override
    public List<ProductIdAndActualQuantityAndTotalPriceOfReceipt> findProductIdAndTotalPriceAndTotalQuantityByExportReceipt(List<Long> exportReceiptIds) throws DaoException {
        String exportReceiptIdsStr = exportReceiptIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "select product_id, sum(erd.actual_quantity) as actual_quantity_by_product,\n" +
                "\t\t\t\t\tsum(erd.total_price) as total_price_by_product\t\t\n" +
                "from export_receipt_detail erd \n" +
                "where erd.export_receipt_id in (" + exportReceiptIdsStr +")\n" +
                "group by product_id ;";
        List<Object> parameters = new ArrayList<>(exportReceiptIds);
        return query(sql, rs -> new ProductIdAndActualQuantityAndTotalPriceOfReceipt(
                rs.getLong("product_id"),
                rs.getInt("actual_quantity_by_product"),
                rs.getDouble("total_price_by_product")

        ), parameters.toArray());
    }
}
