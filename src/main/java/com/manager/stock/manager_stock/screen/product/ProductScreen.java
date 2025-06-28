package com.manager.stock.manager_stock.screen.product;

import com.manager.stock.manager_stock.model.ProductModel;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * @author Trọng Hướng
 */
public class ProductScreen extends VBox {
    private final TableView<ProductModel> table;

    public ProductScreen() {
        this.setPadding(new Insets(20));
        this.setSpacing(10);

        Label label = new Label("Danh sách sản phẩm");
        label.setStyle("-fx-font-size: 18px;");

        table = new TableView<>();
        TableColumn<ProductModel, String> idCol = new TableColumn<>("Mã");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<ProductModel, String> nameCol = new TableColumn<>("Tên");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ProductModel, Integer> qtyCol = new TableColumn<>("Số lượng");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        table.getColumns().addAll(idCol, nameCol, qtyCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        this.getChildren().addAll(label, table);
    }

    public void showProducts(List<ProductModel> products) {
        table.getItems().setAll(products);
    }
}
