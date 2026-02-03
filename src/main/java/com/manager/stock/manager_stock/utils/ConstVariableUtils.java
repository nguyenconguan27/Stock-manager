package com.manager.stock.manager_stock.utils;

import javafx.scene.control.ComboBox;

import java.time.Year;

/**
 * @author Trọng Hướng
 */
public class ConstVariableUtils {
    public static final ComboBox<String> selectYear;
    static {
        selectYear = new ComboBox<>();
        int currentYear = Year.now().getValue();

        for (int y = 2025; y <= currentYear; y++) {
            selectYear.getItems().add(String.valueOf(y));
        }

        selectYear.setValue(String.valueOf(currentYear));
    }
}
