package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.IExportPriceDao;
import com.manager.stock.manager_stock.dao.impl.ExportPriceDaoImpl;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ExportPriceModel;
import com.manager.stock.manager_stock.service.IExportPriceService;

import java.util.List;

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
    public double findExportPriceByProductIdAndLastTime(long productId) throws DaoException {
        return exportPriceDao.findExportPriceByProductIdAndExportTime(productId);
    }

    @Override
    public void save(List<ExportPriceModel> exportPriceModels) throws DaoException {
        exportPriceDao.save(exportPriceModels);
    }
}
