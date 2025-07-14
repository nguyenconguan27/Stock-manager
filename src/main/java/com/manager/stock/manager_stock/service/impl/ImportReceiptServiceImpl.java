package com.manager.stock.manager_stock.service.impl;

import com.manager.stock.manager_stock.dao.IExportPriceDao;
import com.manager.stock.manager_stock.dao.IImportReceiptDao;
import com.manager.stock.manager_stock.dao.impl.ExportPriceDaoImpl;
import com.manager.stock.manager_stock.dao.impl.ImportReceiptDaoImpl;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.service.IImportReceiptService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptServiceImpl implements IImportReceiptService {
    private final IImportReceiptDao importReceiptDao;
    private final IExportPriceDao exportPriceDao;
    private static ImportReceiptServiceImpl instance;

    private ImportReceiptServiceImpl() {
        exportPriceDao = ExportPriceDaoImpl.getInstance();
        importReceiptDao = ImportReceiptDaoImpl.getInstance();
    }

    public static ImportReceiptServiceImpl getInstance() {
        if (instance == null) {
            instance = new ImportReceiptServiceImpl();
        }
        return instance;
    }

    @Override
    public List<ImportReceiptModel> findAllByAcademicYear(Optional<Integer> academicYear) throws DaoException{
        return importReceiptDao.findAllByAcademicYear(academicYear.orElse(Calendar.getInstance().get(Calendar.YEAR)));
    }

    @Override
    public long save(ImportReceiptModel importReceiptModel) throws DaoException {
        return importReceiptDao.save(importReceiptModel);
    }

    @Override
    public void update(ImportReceiptModel importReceiptModel) throws DaoException {
        importReceiptDao.update(importReceiptModel);
        // update ngày cho đơn giá theo ngày của phiếu nhập
        LocalDateTime importDate = LocalDateTime.parse(importReceiptModel.getCreateAt(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        exportPriceDao.updateExportTimeByImportReceiptId(importReceiptModel.getId(), importDate);
    }

    @Override
    public void delete(long id) throws  DaoException {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        importReceiptDao.delete(ids);
    }
}
