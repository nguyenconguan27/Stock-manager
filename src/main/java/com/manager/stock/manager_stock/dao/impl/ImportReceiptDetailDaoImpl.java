package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IImportReceiptDao;
import com.manager.stock.manager_stock.dao.IImportReceiptDetailDao;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptDetailDaoImpl implements IImportReceiptDetailDao {

    private static ImportReceiptDetailDaoImpl instance;
    private HashMap<Long, List<ImportReceiptDetailModel>> sampleData = new HashMap<>();
    private ImportReceiptDetailDaoImpl() {
        sampleData.put(1L,
                Arrays.asList(
                        new ImportReceiptDetailModel(1L, 1L, "101", 10, 9, 50000, 9 * 50000, "Sản phẩm 1"),
                        new ImportReceiptDetailModel(2L, 1L, "102", 15, 15, 70000, 15 * 70000, "Sản phẩm 1")
                ));

        sampleData.put(2L,
                Arrays.asList(
                        new ImportReceiptDetailModel(3L, 2L, "103", 20, 18, 45000, 18 * 45000, "Sản phẩm 1"),
                        new ImportReceiptDetailModel(4L, 2L, "104", 10, 10, 40000, 10 * 40000, "Sản phẩm 1")
                )
        );

        sampleData.put(3L,
                Arrays.asList(
                        new ImportReceiptDetailModel(5L, 3L, "105", 5, 5, 100000, 5 * 100000, "Sản phẩm 1"),
                        new ImportReceiptDetailModel(6L, 3L, "106", 8, 8, 75000, 8 * 75000, "Sản phẩm 1")
                )
        );
    }

    public static ImportReceiptDetailDaoImpl getInstance() {
        if (instance == null) {
            instance = new ImportReceiptDetailDaoImpl();
        }
        return instance;
    }

    @Override
    public List<ImportReceiptDetailModel> findAllByImportReceiptId(long importReceiptId) {
        try {
            return sampleData.get(importReceiptId);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }
}
