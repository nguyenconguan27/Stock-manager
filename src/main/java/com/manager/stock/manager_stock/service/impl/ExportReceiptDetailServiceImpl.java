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
    public List<Long> save(List<ExportReceiptDetailModel> exportReceiptDetailModels, long exportReceiptId) {
        return exportReceiptDetailDao.save(exportReceiptDetailModels, exportReceiptId);
    }

    @Override
    public List<ExportReceiptDetailModel> findByRangeTime(long productId, LocalDateTime start, LocalDateTime end) {
        return exportReceiptDetailDao.findByRangeTime(productId, start, end);
    }

//    @Override
//    public double calculateTotalPriceByProductAndTimeRange(LocalDateTime exportPriceTime, LocalDateTime startDate, LocalDateTime endDate, long productId) {
//        return exportReceiptDetailDao.calculateTotalPriceByProductAndTimeRange(exportPriceTime, startDate, endDate, productId);
//    }

    @Override
    public void delete(List<Long> ids) throws DaoException {
        exportReceiptDetailDao.delete(ids);
    }

    @Override
    public void update(List<ExportReceiptDetailModel> exportReceiptDetailModels) {
        exportReceiptDetailDao.update(exportReceiptDetailModels);
    }



    @Override
    public void updateExportReceiptDetailPriceByProductAndTimeRange(long newExportPriceId, double newOriginalUnitPrice, LocalDateTime exportPriceTime, long productId, LocalDateTime startDate, LocalDateTime endDate) {
        exportReceiptDetailDao.updateExportReceiptDetailPriceByProductAndTimeRange(newExportPriceId, newOriginalUnitPrice, exportPriceTime, productId, startDate, endDate);
    }

    @Override
    public void updateUnitPriceOriginByProductAndTimeRangeAndExceptDate(double newOriginalUnitPrice, LocalDateTime exportPriceTime, long productId, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime exceptDate) {
        exportReceiptDetailDao.updateUnitPriceOriginByProductAndTimeRangeAndExceptDate(newOriginalUnitPrice, exportPriceTime, productId, startDate, endDate, exceptDate);
    }

    @Override
    public double calculateTotalPriceByProductAndTimeRange(LocalDateTime exportPriceTime, LocalDateTime startDate, LocalDateTime endDate, long productId) throws DaoException {
        return exportReceiptDetailDao.calculateTotalPriceByProductAndTimeRange(exportPriceTime, startDate, endDate, productId);
    }

    @Override
    public double calculateTotalPriceByProductAndTimeRangeAndExceptDate(LocalDateTime exportPriceTime, LocalDateTime startDate, LocalDateTime endDate, long productId, LocalDateTime exceptDate) throws DaoException {
        return exportReceiptDetailDao.calculateTotalPriceByProductAndTimeRangeAndExceptDate(exportPriceTime, startDate, endDate, productId, exceptDate);
    }

    @Override
    public long calculateActualQuantityByProductBetweenImportDates(LocalDateTime startDate, LocalDateTime endDate, long productId) {
        return exportReceiptDetailDao.calculateActualQuantityByProductBetweenImportDates(startDate, endDate, productId);
    }

    @Override
    public long calculateTotalQuantityByProductAndTimeRange(LocalDateTime exportPriceTime, LocalDateTime startDate, LocalDateTime endDate, long productId) {
        return exportReceiptDetailDao.calculateTotalQuantityByProductAndTimeRange(exportPriceTime, startDate, endDate, productId);
    }

    @Override
    public void save(ExportReceiptDetailModel detailModel, long receiptId) {
        exportReceiptDetailDao.save(detailModel, receiptId);
    }
}
