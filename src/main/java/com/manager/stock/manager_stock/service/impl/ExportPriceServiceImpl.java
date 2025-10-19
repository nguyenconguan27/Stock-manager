package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.IExportPriceDao;
import com.manager.stock.manager_stock.dao.impl.ExportPriceDaoImpl;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ExportPriceModel;
import com.manager.stock.manager_stock.model.dto.ExportPriceAndProductCodeAndProductName;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndExportTimeAndExportPrice;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndPrice;
import com.manager.stock.manager_stock.service.IExportPriceService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class ExportPriceServiceImpl implements IExportPriceService {
    private static ExportPriceServiceImpl instance;
    private final IExportPriceDao exportPriceDao;

    private ExportPriceServiceImpl() {
        exportPriceDao = ExportPriceDaoImpl.getInstance();
    }

    public static ExportPriceServiceImpl getInstance() {
        if (instance == null) {
            instance = new ExportPriceServiceImpl();
        }
        return instance;
    }

    @Override
    public void save(List<ExportPriceModel> exportPriceModels) throws DaoException {
        exportPriceDao.save(exportPriceModels);
    }

    @Override
    public HashMap<Long, List<ExportPriceModel>> findAllByProductAndMinTime(List<Long> productIds, LocalDateTime minTime) {
        List<ExportPriceModel> exportPriceModels = exportPriceDao.findAllByProductAndMinTime(productIds, minTime);
        return exportPriceModels.stream()
                .collect(Collectors.groupingBy(
                        ExportPriceModel::getProductId,
                        HashMap::new,
                        Collectors.toList()
                ));
    }

    @Override
    public void update(List<ExportPriceModel> exportPriceModels) {
        exportPriceDao.update(exportPriceModels);
    }

    @Override
    public void updateExportPriceByProductIdAndImportDate(long quantityImport, double totalPriceDifference, double totalPriceImport, LocalDateTime exportTime, long productId) throws DaoException {
        exportPriceDao.updateExportPriceByProductIdAndImportDate(quantityImport, totalPriceDifference, totalPriceImport, exportTime, productId);
    }

    @Override
    public void updateExportPriceAfterImportCorrectionByProductIdAndImportDate(double totalPriceChanged, long totalQuantityChange, LocalDateTime oldImportDate, long productId) {
        exportPriceDao.updateExportPriceAfterImportCorrectionByProductIdAndImportDate(totalPriceChanged, totalQuantityChange, oldImportDate, productId);
    }

    @Override
    public ExportPriceIdAndPrice findExportPriceByProductAndLastTime(long productId) throws DaoException{
        return exportPriceDao.findExportPriceIdAndPriceByProductAndLastTime(productId);
    }

    @Override
    public HashMap<Long, Double> findPriceById(List<Long> ids) {
        List<ExportPriceIdAndPrice> exportPriceIdAndPrices = exportPriceDao.findAllById(ids);
        return exportPriceIdAndPrices.stream()
                .collect(Collectors.toMap(
                        ExportPriceIdAndPrice::exportPriceId,
                        ExportPriceIdAndPrice::price,
                        (existing, replacement) -> replacement,
                        HashMap::new
                ));
    }

    @Override
    public ExportPriceAndProductCodeAndProductName findProductHaveMaxPriceByGroup(long productGroupId) throws DaoException{
        return exportPriceDao.findProductHaveMaxPriceByGroup(productGroupId);
    }

    @Override
    public ExportPriceAndProductCodeAndProductName findProductHaveMinPriceByGroup(long productGroupId) throws DaoException{
        return exportPriceDao.findProductHaveMinPriceByGroup(productGroupId);
    }

    @Override
    public ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndMaxTimeByImportDate(long productId, LocalDateTime maxTime, LocalDateTime oldImportDate) throws DaoException {
        return exportPriceDao.findByProductIdAndMaxTimeByImportDate(productId, maxTime, oldImportDate);
    }

    @Override
    public ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndMinTimeByImportDate(long productId, LocalDateTime minTime, LocalDateTime oldImportDate) throws DaoException {
        return exportPriceDao.findByProductIdAndMinTimeByImportDate(productId, minTime, oldImportDate);
    }

    @Override
    public ExportPriceIdAndExportTimeAndExportPrice findByProductIdAndImportDate(long productId, LocalDateTime importDate) {
        return exportPriceDao.findByProductIdAndImportDate(productId, importDate);
    }

    @Override
    public ExportPriceModel findByProductAndImportDate(long productId, LocalDateTime importDate) {
        return exportPriceDao.findOneByProductIdAndImportDate(productId, importDate);
    }

    @Override
    public List<LocalDateTime> findAllExportTimeByProductAndBetweenImportDates(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime importDate, long productId) throws DaoException {
        return exportPriceDao.findAllExportTimeByProductAndBetweenImportDates(startDate, endDate, importDate, productId);
    }

    @Override
    public List<LocalDateTime> findAllExportTimeByProductAndMoreThanImportDate(LocalDateTime startDate, LocalDateTime importDate, long productId) {
        return exportPriceDao.findAllExportTimeByProductAndMoreThanImportDate(startDate, importDate, productId);
    }

    @Override
    public long calculateTotalQuantityImportAndQuantityInStockByImportDateAndProduct(long productId, LocalDateTime importDate) {
        return exportPriceDao.calculateTotalQuantityImportAndQuantityInStockByImportDateAndProduct(productId, importDate);
    }

    @Override
    public void updateExportPriceByImportTimeAndProduct(long newQuantityInStock, double newTotalPriceInStock, LocalDateTime importDateTime, long productId) throws DaoException{
        exportPriceDao.updateExportPriceByImportTimeAndProduct(newQuantityInStock, newTotalPriceInStock, importDateTime, productId);
    }

    @Override
    public void commit() {
        exportPriceDao.commit();
    }

    @Override
    public void rollback() {
        exportPriceDao.rollback();
    }
}
