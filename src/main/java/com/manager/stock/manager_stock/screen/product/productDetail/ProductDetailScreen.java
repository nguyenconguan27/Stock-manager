package com.manager.stock.manager_stock.screen.product.productDetail;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProductDetailScreen {
    private final ProductDetailPresenter productDetailPresenter;

    public ProductDetailScreen() {
        productDetailPresenter = ProductDetailPresenter.getInstance();
    }
    public void showPopup(Stage parentStage) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(parentStage); // Gắn với cửa sổ cha

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");
        layout.getChildren().add(new Label("Đây là popup mở từ class khác"));

        Scene scene = new Scene(layout, 250, 100);
        popupStage.setScene(scene);
        popupStage.setTitle("Popup");
        popupStage.showAndWait();
    }
}
