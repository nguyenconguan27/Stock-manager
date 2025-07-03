package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IExportPriceDao;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.modelMapperResultSet.ExportPriceMapperResultSet;
import com.manager.stock.manager_stock.model.ExportPriceModel;
import com.manager.stock.manager_stock.service.impl.ExportPriceServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ExportPriceDaoImpl extends AbstractDao<ExportPriceModel> implements IExportPriceDao {
    private static ExportPriceDaoImpl instance;
    private ExportPriceDaoImpl(){}
    public static ExportPriceDaoImpl getInstance() {
        if (instance == null) {
            instance = new ExportPriceDaoImpl();
        }
        return instance;
    }

    @Override
    public double findExportPriceByProductIdAndExportTime(long productId) {
        try {
            String sql = "select export_price \n" +
                    "FROM export_price\n" +
                    "WHERE product_id = ?\n" +
                    "ORDER BY export_time DESC\n" +
                    "LIMIT 1;";

            List<ExportPriceModel> exportPriceModels = query(sql, new ExportPriceMapperResultSet(), productId);
            if(exportPriceModels.isEmpty()){
                return -1;
            }
            return exportPriceModels.get(0).getExportPrice();
        }
        catch (DaoException e) {
            throw new DaoException(e.getMessage());
        }
    }

    @Override
    public void save(ExportPriceModel exportPriceModel) throws DaoException {
        String sql = "INSERT INTO export_price(product_id, export_time, export_price, quantity_in_stock, quantity_imported, import_price) " +
                    "values (?, ?, ?, ?, ?, ?)";
        List<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[] {
            exportPriceModel.getProductId(),
            System.currentTimeMillis(),
            exportPriceModel.getExportPrice(),
            exportPriceModel.getQuantityInStock(),
            exportPriceModel.getQuantityImported(),
            exportPriceModel.getImportPrice()
        });
        save(sql, parameters);
    }
}
