package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IExportReceiptDetailDao;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ExportReceiptDetailMapperResultSet;
import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;

import java.util.List;

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
        String sql = "SELECT erd.*, p.code as product_code, p.\"name\" as product_name FROM export_receipt_detail erd\n" +
                "join export_receipt er on erd.export_receipt_id = er.id\n" +
                "join product p on p.id = erd.product_id\n" +
                "WHERE er.id = ?";
        return query(sql, new ExportReceiptDetailMapperResultSet(), exportReceiptId);
    }
}
