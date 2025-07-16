package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IExportReceiptDao;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ExportReceiptMapperResultSet;
import com.manager.stock.manager_stock.model.ExportReceiptModel;
import com.manager.stock.manager_stock.model.dto.ExportReceiptIdAndCreateDate;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;

import java.sql.Connection;
import java.time.LocalDate;
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
    public List<ExportReceiptIdAndCreateDate> findExportReceiptIdAndCreatedAtByProductIdsAndYearAndMinCreatedAt(List<Long> productIds, int academicYear, LocalDate minCreateAt) {
        productIds.add(0L);
        String idsStr = productIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String sql = "select er.id, er.create_at from export_receipt_detail erd \n" +
                    "inner join export_receipt er on\n" +
                    "erd.export_receipt_id = er.id \n" +
                    "where erd.product_id in (" + idsStr + ") \n" +
                    "and er.academic_year = ? \n" +
                    "and to_date(er.create_at, '%d/%m/%Y') >= ?";
        System.out.println(minCreateAt);
        return query(sql, rs -> new ExportReceiptIdAndCreateDate(
                rs.getLong("id"),
                rs.getString("create_at"),
                rs.getDouble("total_quantity")
        ), academicYear, minCreateAt);
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

    @Override
    public void deleteByIds(List<Long> ids) throws DaoException {
        String idsStr = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
        String sql = "DELETE FROM export_receipt where id in (" + idsStr + ")";
        delete(sql);
    }

    @Override
    public void deleteByIdsWithTransaction(List<Long> ids, Connection connection) throws DaoException{
        String idsStr = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "DELETE FROM export_receipt where id in (" + idsStr + ")";

        List<Object> parameters = new ArrayList<>(ids);
        deleteWithinTransaction(sql, connection, parameters.toArray());
    }

    @Override
    public List<ExportReceiptModel> findAllByAcademicYear(int academicYear) {
        String sql = "SELECT er.*, sum(ep.export_price * erd.actual_quantity) as total_price_receipt from export_receipt er \n" +
                "join export_receipt_detail erd on\n" +
                "er.id = erd.export_receipt_id \n" +
                "join export_price ep on\n" +
                "ep.id = erd.export_price_id \n" +
                "where academic_year = ?\n" +
                "group by er.id;";
        return query(sql, new ExportReceiptMapperResultSet(), academicYear);
    }

    @Override
    public long save(ExportReceiptModel exportReceiptModel) {
        String sql = "INSERT INTO export_receipt(invoice_number, create_at, receiver, receive_address, reason, warehouse, academic_year) " +
                    "values (?, ?, ?, ?, ?, ?, ?)";
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[] {
            exportReceiptModel.getInvoiceNumber(),
            exportReceiptModel.getCreateAt(),
            exportReceiptModel.getReceiver(),
            exportReceiptModel.getReceiveAddress(),
            exportReceiptModel.getReason(),
            exportReceiptModel.getWareHouse(),
            exportReceiptModel.getAcademicYear()
        });
        return save(sql, parameters);
    }

    @Override
    public void update(ExportReceiptModel exportReceiptModel) {
        String sql = "UPDATE export_receipt invoice_number = ?, create_at = ?, receiver = ?, receive_address = ?, " +
                        "reason = ?, warehouse = ? where id = ?";
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[] {
                exportReceiptModel.getInvoiceNumber(),
                exportReceiptModel.getCreateAt(),
                exportReceiptModel.getReceiver(),
                exportReceiptModel.getReceiveAddress(),
                exportReceiptModel.getReason(),
                exportReceiptModel.getWareHouse(),
                exportReceiptModel.getId()
        });
        save(sql, parameters);
    }
}
