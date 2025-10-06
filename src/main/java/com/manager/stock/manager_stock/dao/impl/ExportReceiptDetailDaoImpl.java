package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IExportReceiptDetailDao;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ExportReceiptDetailMapperResultSet;
import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;

import java.net.DatagramPacket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptDetailDaoImpl extends AbstractDao<ExportReceiptDetailModel> implements IExportReceiptDetailDao {
    private static ExportReceiptDetailDaoImpl instance;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private ExportReceiptDetailDaoImpl() {}

    public static ExportReceiptDetailDaoImpl getInstance() {
        if (instance == null) {
            instance = new ExportReceiptDetailDaoImpl();
        }
        return instance;
    }

    @Override
    public List<ExportReceiptDetailModel> findAllByExPortReceipt(long exportReceiptId) {
        String sql = "select\n" +
                "\terd.*,\n" +
                "\tp.code as product_code,\n" +
                "\tp.name as product_name, p.unit,\n" +
                "\tep.export_price as export_price \n" +
                "from\n" +
                "\texport_receipt_detail erd\n" +
                "join export_receipt er on\n" +
                "\terd.export_receipt_id = er.id\n" +
                "join product p on\n" +
                "\tp.id = erd.product_id\n" +
                "join export_price ep on\n" +
                "\tep.id = erd.export_price_id \n" +
                "where\n" +
                "\ter.id = ?";
        return query(sql, new ExportReceiptDetailMapperResultSet(), exportReceiptId);
    }

    @Override
    public List<ExportReceiptDetailModel> findAllByProductAndMinTime(List<Long> productIds, LocalDateTime minTime) {
        String productIdsStr = productIds.stream().map(Object::toString).collect(Collectors.joining(","));
        String sql = "select erd.* from export_receipt_detail erd \n" +
                "join export_receipt er on\n" +
                "erd.export_receipt_id = er.id \n" +
                "where PARSEDATETIME(er.create_at,'dd/MM/yyyy HH:mm:ss') >= ?\n" +
                "and erd.product_id in (" + productIdsStr + ")";
        return query(sql, new ExportReceiptDetailMapperResultSet(), minTime);
    }

    @Override
    public List<Long> save(List<ExportReceiptDetailModel> exportReceiptDetailModels, long exportReceiptId) {
        String sql = "INSERT INTO export_receipt_detail(id, export_receipt_id, product_id, planned_quantity, actual_quantity, export_price_id, original_unit_price) " +
                " OVERRIDING SYSTEM VALUE" +
                " values(?, ?, ?, ?, ?, ?, ?);";
        List<Long> ids = new ArrayList<>();
        List<Object[]> parameters = new ArrayList<>();
        for (ExportReceiptDetailModel exportReceiptDetailModel : exportReceiptDetailModels) {
            long id = System.nanoTime();
            parameters.add(new Object[]{
                    id,
                    exportReceiptId,
                    exportReceiptDetailModel.getProductId(),
                    exportReceiptDetailModel.getPlannedQuantity(),
                    exportReceiptDetailModel.getActualQuantity(),
                    exportReceiptDetailModel.getExportPriceId(),
                    exportReceiptDetailModel.getOriginalUnitPrice()
            });
            ids.add(id);
        }
        save(sql, parameters);
        return ids;
    }

    // import date: Ngày của đơn giá cũ - dùng để lọc ra những phiếu xuất cần cập nhật
    // newImportDate: Ngày mới của phiếu nhập - dùng để cập nhật những phiến xuất đằng sau ngày của phiếu nhập đó thôi
    @Override
    public void updateExportPriceByImportDate(LocalDateTime importDate, long exportPriceId, LocalDateTime newImportDate, long newExportPrice) throws DaoException {
        String sql = "update DB.EXPORT_RECEIPT_DETAIL set DB.EXPORT_RECEIPT_DETAIL.EXPORT_PRICE_ID = ?, DB.EXPORT_RECEIPT_DETAIL.ORIGINAL_UNIT_PRICE = ?\n" +
                "    where DB.EXPORT_RECEIPT_DETAIL.EXPORT_PRICE_ID in\n" +
                "    (select DB.EXPORT_PRICE.id from DB.EXPORT_PRICE\n" +
                "    join DB.EXPORT_RECEIPT_DETAIL on\n" +
                "    DB.EXPORT_RECEIPT_DETAIL.EXPORT_PRICE_ID  = DB.EXPORT_PRICE.id\n" +
                "    join DB.EXPORT_RECEIPT on\n" +
                "    DB.EXPORT_RECEIPT.id = DB.EXPORT_RECEIPT_DETAIL.EXPORT_RECEIPT_ID\n" +
                "    where DB.EXPORT_PRICE.EXPORT_TIME = ? " +
                "    and CAST(PARSEDATETIME(DB.EXPORT_RECEIPT.CREATE_AT, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP) >= CAST(PARSEDATETIME(?, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP));";
        List<Object[]> parameters = new ArrayList<>();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        parameters.add(new Object[]{
           exportPriceId, newExportPrice, formatter1.format(importDate), formatter.format(newImportDate)
        });
        save(sql, parameters);
    }

    @Override
    public void delete(List<Long> ids) throws DaoException {
        String idsStr = ids.stream().map(Object::toString).collect(Collectors.joining(","));
        String sql = "DELETE FROM export_receipt_detail WHERE id IN (" + idsStr + ")";
        delete(sql);
    }

    @Override
    public void update(List<ExportReceiptDetailModel> exportReceiptDetailModels) {
        String sql = "UPDATE export_receipt_detail set actual_quantity = ?" +
                " WHERE id = ?";
        List<Object[]> parameters = new ArrayList<>();
        for (ExportReceiptDetailModel exportReceiptDetailModel : exportReceiptDetailModels) {
            parameters.add(new Object[]{
                exportReceiptDetailModel.getActualQuantity(),
                exportReceiptDetailModel.getId()
            });
        }
        save(sql, parameters);
    }

    @Override
    public double calculateTotalPriceByImportDate(LocalDateTime importDate, LocalDateTime newImportDate) {

        String sql = "select sum(DB.EXPORT_RECEIPT_DETAIL.ORIGINAL_UNIT_PRICE * DB.EXPORT_RECEIPT_DETAIL.ACTUAL_QUANTITY) as total_price from DB.EXPORT_RECEIPT_DETAIL \n" +
                " join DB.EXPORT_PRICE on\n" +
                " DB.EXPORT_RECEIPT_DETAIL.EXPORT_PRICE_ID  = DB.EXPORT_PRICE.id\n" +
                " join DB.EXPORT_RECEIPT on\n" +
                " DB.EXPORT_RECEIPT.id = DB.EXPORT_RECEIPT_DETAIL.EXPORT_RECEIPT_ID\n" +
                " where DB.EXPORT_PRICE.EXPORT_TIME = ? " +
                " and CAST(PARSEDATETIME(DB.EXPORT_RECEIPT.CREATE_AT, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP) >= CAST(PARSEDATETIME(?, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP);";
        List<Long> totalPrices = query(sql, rs -> rs.getLong("TOTAL_PRICE"), importDate, formatter.format(newImportDate));
        if(totalPrices.isEmpty()){
            return -1;
        }
        return totalPrices.get(0);
    }

    @Override
    public double calculateTotalPriceByImportDateAndBetweenDates(LocalDateTime importDate, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "select sum(DB.EXPORT_RECEIPT_DETAIL.ORIGINAL_UNIT_PRICE * DB.EXPORT_RECEIPT_DETAIL.ACTUAL_QUANTITY) as total_price from DB.EXPORT_RECEIPT_DETAIL \n" +
                " join DB.EXPORT_PRICE on\n" +
                " DB.EXPORT_RECEIPT_DETAIL.EXPORT_PRICE_ID  = DB.EXPORT_PRICE.id\n" +
                " join DB.EXPORT_RECEIPT on\n" +
                " DB.EXPORT_RECEIPT.id = DB.EXPORT_RECEIPT_DETAIL.EXPORT_RECEIPT_ID\n" +
                " where DB.EXPORT_PRICE.EXPORT_TIME = ? " +
                " and CAST(PARSEDATETIME(DB.EXPORT_RECEIPT.CREATE_AT, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP) >= CAST(PARSEDATETIME(?, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP); " +
                " and CAST(PARSEDATETIME(DB.EXPORT_RECEIPT.CREATE_AT, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP) < CAST(PARSEDATETIME(?, 'dd/MM/yyyy HH:mm:ss') AS TIMESTAMP);";
        List<Long> totalPrices = query(sql, rs -> rs.getLong("TOTAL_PRICE"), importDate, formatter.format(startDate), formatter.format(endDate));
        if(totalPrices.isEmpty()){
            return -1;
        }
        return totalPrices.get(0);
    }
}