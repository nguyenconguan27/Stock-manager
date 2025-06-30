package com.manager.stock.manager_stock.dao.impl;

import com.manager.stock.manager_stock.dao.IImportReceiptDao;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.screen.transaction.ImportReceiptPresenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ImportReceiptDaoImpl implements IImportReceiptDao {
    private final Logger logger = LoggerFactory.getLogger(ImportReceiptPresenter.class);
    private static ImportReceiptDaoImpl instance;

    private ImportReceiptDaoImpl() {

    }

    public static IImportReceiptDao getInstance() {
        if(instance == null) {
            instance = new ImportReceiptDaoImpl();
        }
        return instance;
    }

    @Override
    public List<ImportReceiptModel> findAll() {
        try {
            logger.debug("Start find all import receipt.");
            return Arrays.asList(
                    new ImportReceiptModel(
                            1L,
                            "PN001",
                            "2024-06-01",
                            "Nguyễn Văn A",
                            "HD001",
                            "Công ty TNHH Xây Dựng An Phát",
                            "Kho Hà Nội",
                            12500000,
                            "Mười hai triệu năm trăm nghìn đồng"
                    ),
                    new ImportReceiptModel(
                            2L,
                            "PN002",
                            "2024-06-05",
                            "Trần Thị B",
                            "HD002",
                            "Công ty TNHH Thép Việt",
                            "Kho Hải Phòng",
                            8500000,
                            "Tám triệu năm trăm nghìn đồng"
                    ),
                    new ImportReceiptModel(
                            3L,
                            "PN003",
                            "2024-06-10",
                            "Lê Văn C",
                            "HD003",
                            "Công ty Cổ phần Gạch Ngói",
                            "Kho Đà Nẵng",
                            15800000,
                            "Mười lăm triệu tám trăm nghìn đồng"
                    )
            );
        }
        catch (Exception e) {

        }
        return List.of();
    }
}
