package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.IExportReceiptDetailDao;
import com.manager.stock.manager_stock.dao.impl.ExportReceiptDetailDaoImpl;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ExportReceiptDetailModel;
import com.manager.stock.manager_stock.service.IExportReceiptDetailService;

import java.net.DatagramPacket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptDetailServiceImpl implements IExportReceiptDetailService {
    private final IExportReceiptDetailDao exportReceiptDetailDao;
    private static ExportReceiptDetailServiceImpl instance;

    private ExportReceiptDetailServiceImpl() {
        exportReceiptDetailDao = ExportReceiptDetailDaoImpl.getInstance();
    }
    public static ExportReceiptDetailServiceImpl getInstance() {
        if (instance == null) {
            instance = new ExportReceiptDetailServiceImpl();
        }
        return instance;
    }

    @Override
    public List<ExportReceiptDetailModel> findAllByExportReceipt(long exportReceiptId) throws DaoException {
        return exportReceiptDetailDao.findAllByExPortReceipt(exportReceiptId);
    }

    @Override
    public Map<Long, List<ExportReceiptDetailModel>> findAllByProductAndMinTime(List<Long> productIds, LocalDateTime minTime) {
        List<ExportReceiptDetailModel> exportReceiptDetailModels = exportReceiptDetailDao.findAllByProductAndMinTime(productIds, minTime);
        return exportReceiptDetailModels.stream().collect(Collectors.groupingBy(ExportReceiptDetailModel::getProductId));
    }

    @Override
    public List<Long> save(List<ExportReceiptDetailModel> exportReceiptDetailModels) {
        return exportReceiptDetailDao.save(exportReceiptDetailModels);
    }

    @Override
    public void delete(List<Long> ids) throws DaoException {
        exportReceiptDetailDao.delete(ids);
    }
}
