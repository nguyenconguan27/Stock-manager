package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.IExportPriceDao;
import com.manager.stock.manager_stock.dao.impl.ExportPriceDaoImpl;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ExportPriceModel;
import com.manager.stock.manager_stock.model.dto.ExportPriceAndProductCodeAndProductName;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndExportTimeAndExportPrice;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndPrice;
import com.manager.stock.manager_stock.service.IExportPriceService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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
        if(exportPriceModels==null || exportPriceModels.isEmpty()) return;
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
    public ExportPriceModel findByProductAndLastTime(Long productId, LocalDateTime time) {
        return null;
    }

    @Override
    public ExportPriceModel findByProductAndMinTime(Long productId, LocalDateTime time) {
        return null;
    }

    @Override
    public ExportPriceModel findAllByProductAndMinTime(Long productId, LocalDateTime time) {
        return null;
    }

    @Override
    public void update(List<ExportPriceModel> exportPriceModels) {
        exportPriceDao.update(exportPriceModels);
    }

    @Override
    public void update(ExportPriceModel exportPriceModel) {
        List<ExportPriceModel> exportPriceModels = new ArrayList<>();
        exportPriceModels.add(exportPriceModel);
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
    public ExportPriceIdAndPrice findExportPriceByProductAndLastTime(long productId) {
        return null;
    }

    @Override
    public ExportPriceIdAndPrice findExportPriceByProductAndLastTime(long productId, LocalDateTime exportDate) throws DaoException{
        return exportPriceDao.findExportPriceIdAndPriceByProductAndLastTime(productId, exportDate);
    }



    @Override
    public long save(ExportPriceModel exportPriceModel) {
        return exportPriceDao.save(exportPriceModel);
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
    public ExportPriceModel findByProductIdAndMinTimeByDate(long productId, LocalDateTime minTime, LocalDateTime oldImportDate) {
        return exportPriceDao.findByProductIdAndMinTimeByDate(productId, minTime, oldImportDate);
    }

    @Override
    public List<ExportPriceModel> findAllExportPriceInfoByProductAndBetweenImportDates(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime importDate, long productId) throws DaoException {
        return exportPriceDao.findAllExportPriceInfoByProductAndBetweenImportDates(startDate, endDate, importDate, productId);
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
    public List<ExportPriceModel> findAllExportPriceByProductAndCreateAtAndOrderByAsc(long productId, int currentYear) {
        LocalDateTime startCurrentYear = LocalDateTime.of(currentYear, 1, 1, 0, 0, 0);
        return exportPriceDao.findAllByProductIdAndAfterTime(productId, startCurrentYear);
    }

    @Override
    public void updateExportTimeByImportReceipt(long importReceiptId, LocalDateTime exportTime) {
        exportPriceDao.updateExportTimeByImportReceiptId(importReceiptId, exportTime);
    }

    @Override
    public void deleteByImportReceipt(long importReceiptId) {
        exportPriceDao.deleteByImportReceipt(importReceiptId);
    }

    @Override
    public void deleteByImportReceiptAndProduct(long importReceiptId, Set<Long> productIds) {
        exportPriceDao.deleteByImportReceiptAndProduct(importReceiptId, productIds);
    }

    @Override
    public void updateQuantityImportedAndTotalPriceImportedByProductAndImportReceipt(int quantityImported, double totalPriceImported, long importReceiptId, long productId) {
        exportPriceDao.updateQuantityImportedAndTotalPriceImportedByProductAndImportReceipt(quantityImported, totalPriceImported, importReceiptId, productId);
    }

    @Override
    public ExportPriceIdAndPrice findInventoryByExportTimeAndProduct(long productId, LocalDateTime exportDate) {
        return exportPriceDao.findInventoryByExportTimeAndProduct(productId, exportDate);
    }

    @Override
    public void updateExportPriceByImportTimeAndProduct(long newQuantityInStock, double newTotalPriceInStock, LocalDateTime importDateTime, long productId) throws DaoException{
        exportPriceDao.updateExportPriceByImportTimeAndProduct(newQuantityInStock, newTotalPriceInStock, importDateTime, productId);
    }

    @Override
    public ExportPriceModel findByProductIdAndBeforeTime(long productId, LocalDateTime time) {
        List<ExportPriceModel> exportPriceModels = exportPriceDao.findByProductIdAndBeforeTime(productId, time);
        if(exportPriceModels == null || exportPriceModels.isEmpty()) {
            return null;
        }
        return exportPriceModels.get(0);
    }

    @Override
    public ExportPriceModel findLastByProductIdAndYear(long productId, int year) {
        List<ExportPriceModel> exportPriceModels = exportPriceDao.findLastByProductIdAndYear(productId, year);
        if(exportPriceModels == null || exportPriceModels.isEmpty()) {
            return null;
        }
        return exportPriceModels.get(0);
    }

    @Override
    public List<ExportPriceModel> findAllByProductIdAndAfterTime(long productId, LocalDateTime time) {
        return exportPriceDao.findAllByProductIdAndAfterTime(productId, time);
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
