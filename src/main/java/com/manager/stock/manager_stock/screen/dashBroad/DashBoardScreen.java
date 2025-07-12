package com.manager.stock.manager_stock.screen.dashBroad;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author Trọng Hướng
 */
public class DashBoardScreen extends VBox {

    public DashBoardScreen() {
        this.setPadding(new Insets(20));
        this.setSpacing(20);

        Label label = new Label("Màn hình chính");
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");


        this.getChildren().addAll(label);
    }
}
