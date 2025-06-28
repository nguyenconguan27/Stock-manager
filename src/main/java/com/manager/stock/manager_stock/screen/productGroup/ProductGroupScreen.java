package com.manager.stock.manager_stock.screen.productGroup;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author Trọng Hướng
 */
public class ProductGroupScreen extends VBox {
    public ProductGroupScreen() {
        this.setPadding(new Insets(20));
        Label label = new Label("Danh sách nhóm sản phẩm");
        label.setStyle("-fx-font-size: 18px;");

        this.getChildren().add(label);
    }
}
