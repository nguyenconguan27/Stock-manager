package com.manager.stock.manager_stock.utils;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class Utils {

    public static <T, R> TableColumn<T, R> createColumn(String title, String property) {
        TableColumn<T, R> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }
}
