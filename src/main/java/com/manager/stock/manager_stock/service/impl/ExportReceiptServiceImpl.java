package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.IExportReceiptDao;
import com.manager.stock.manager_stock.dao.impl.ExportReceiptDaoImpl;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ExportReceiptModel;
import com.manager.stock.manager_stock.model.dto.ExportReceiptIdAndCreateDate;
import com.manager.stock.manager_stock.model.dto.ProductIdAndActualQuantityAndTotalPriceOfReceipt;
import com.manager.stock.manager_stock.service.IExportReceiptService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * @author Trọng Hướng
 */
public class ExportReceiptServiceImpl implements IExportReceiptService {
    private static ExportReceiptServiceImpl instance = null;
    private final IExportReceiptDao exportReceiptDao;

    private ExportReceiptServiceImpl() {
        exportReceiptDao = ExportReceiptDaoImpl.getInstance();
    }

    public static IExportReceiptService getInstance() {
        if (instance == null) {
            instance = new ExportReceiptServiceImpl();
        }
        return instance;
    }

    @Override
    public List<ExportReceiptIdAndCreateDate> findExportReceiptIdAndCreatedAtByProductIdsAndYearAndMinCreatedAt(List<Long> productIds, int academicYear, String minCreateAt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
//        LocalDateTime localDateTime = LocalDateTime.parse(minCreateAt, formatter);
        LocalDate localDate = LocalDate.parse(minCreateAt, formatter);
        return exportReceiptDao.findExportReceiptIdAndCreatedAtByProductIdsAndYearAndMinCreatedAt(productIds, academicYear, localDate);
    }

    @Override
    public List<ProductIdAndActualQuantityAndTotalPriceOfReceipt> findProductIdAndTotalPriceAndTotalQuantityByExportReceipt(List<Long> exportReceiptIds) throws DaoException {
        return exportReceiptDao.findProductIdAndTotalPriceAndTotalQuantityByExportReceipt(exportReceiptIds);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        exportReceiptDao.deleteByIds(ids);
    }

    @Override
    public List<ExportReceiptModel> findAllByAcademicYear(int academicYear) throws DaoException {
        return exportReceiptDao.findAllByAcademicYear(academicYear);
    }
}
