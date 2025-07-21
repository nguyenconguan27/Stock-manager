package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IExportReceiptDetailDao;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ExportReceiptDetailMapperResultSet;
import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;

import java.net.DatagramPacket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptDetailDaoImpl extends AbstractDao<ExportReceiptDetailModel> implements IExportReceiptDetailDao {
    private static ExportReceiptDetailDaoImpl instance;

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
                "\tp.\"name\" as product_name,\n" +
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
                "where er.create_at >= ?\n" +
                "and erd.product_id in (" + productIdsStr + ")";
        return query(sql, new ExportReceiptDetailMapperResultSet(), minTime);
    }

    @Override
    public List<Long> save(List<ExportReceiptDetailModel> exportReceiptDetailModels, long exportReceiptId) {
        String sql = "INSERT INTO export_receipt_detail(id, export_receipt_id, product_id, planned_quantity, actual_quantity, export_price_id, unit_price) " +
                    " OVERRIDING SYSTEM VALUE" +
                    " values(?, ?, ?, ?, ?, ?, ?)" +
                    " RETURNING *;";
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
                    exportReceiptDetailModel.getUnitPrice()
            });
            ids.add(id);
        }
        save(sql, parameters);
        return ids;
    }

    @Override
    public void delete(List<Long> ids) throws DaoException {
        String idsStr = ids.stream().map(Object::toString).collect(Collectors.joining(","));
        String sql = "DELETE FROM export_receipt_detail WHERE id IN (" + idsStr + ")";
        delete(sql);
    }

    @Override
    public void update(List<ExportReceiptDetailModel> exportReceiptDetailModels) {
        String sql = "UPDATE export_receipt_detail set actual_quantity = ?, message = ?, status = ?" +
                     " WHERE id = ?";
        List<Object[]> parameters = new ArrayList<>();
        for (ExportReceiptDetailModel exportReceiptDetailModel : exportReceiptDetailModels) {
            parameters.add(new Object[]{
               exportReceiptDetailModel.getActualQuantity(),
               exportReceiptDetailModel.getMessage(),
               exportReceiptDetailModel.getStatus(),
               exportReceiptDetailModel.getId()
            });
        }
        save(sql, parameters);
    }
}
