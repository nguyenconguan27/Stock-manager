package com.manager.stock.manager_stock.utils;

import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

/**
 * @author Trọng Hướng
 */
public class CreateLoadingUtil {
    public static StackPane createLoading() {
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        StackPane loadingPane = new StackPane(loadingIndicator);

        loadingPane.setPrefHeight(400);
        loadingPane.setPrefWidth(800);
        loadingPane.setAlignment(Pos.CENTER);
        return loadingPane;
    }
}
