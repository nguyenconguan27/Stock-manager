package com.manager.stock.manager_stock.service;

import com.manager.stock.manager_stock.model.DetailModel;
import com.manager.stock.manager_stock.model.ReceiptModel;

import java.sql.SQLException;
import java.util.List;

public interface ComputService {
    void compute(ReceiptModel receipt, List<? extends DetailModel> details, String action) throws SQLException;
}
