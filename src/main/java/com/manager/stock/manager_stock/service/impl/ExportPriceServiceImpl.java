package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.IExportPriceDao;
import com.manager.stock.manager_stock.dao.impl.ExportPriceDaoImpl;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ExportPriceModel;
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
    public void commit() {
        exportPriceDao.commit();
    }

    @Override
    public void rollback() {
        exportPriceDao.rollback();
    }
}
