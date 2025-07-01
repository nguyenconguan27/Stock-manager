package com.manager.stock.manager_stock.utils;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import java.util.function.Function;

/**
 * @author Trọng Hướng
 */
public class CreateColumnTableUtil {
    public static <T, R> TableColumn<T, R> createColumn(String title, Function<T, ObservableValue<R>> propertyFunc) {
        TableColumn<T, R> col = new TableColumn<>(title);
        col.setCellValueFactory(cell -> propertyFunc.apply(cell.getValue()));
        col.setCellFactory(column -> new TableCell<T, R>() {
            @Override
            protected void updateItem(R item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });
        return col;
    }
}
