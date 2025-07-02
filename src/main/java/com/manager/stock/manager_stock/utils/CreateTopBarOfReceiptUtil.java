package com.manager.stock.manager_stock.utils;

import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.screen.transaction.AddOrUpdateReceiptScreen;
import com.manager.stock.manager_stock.screen.transaction.ImportReceiptScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * @author Trọng Hướng
 */
public class CreateTopBarOfReceiptUtil {

    public static HBox createTopBar() {
        Image addIcon = new Image(CreateTopBarOfReceiptUtil.class.getResource("/com/manager/stock/manager_stock/icons/add.png").toExternalForm()); // Or .svg, .jpg, etc.
        Image editIcon = new Image(CreateTopBarOfReceiptUtil.class.getResource("/com/manager/stock/manager_stock/icons/pencil.png").toExternalForm());
        Image deleteIcon = new Image(CreateTopBarOfReceiptUtil.class.getResource("/com/manager/stock/manager_stock/icons/delete.png").toExternalForm());
        Image reloadIcon = new Image(CreateTopBarOfReceiptUtil.class.getResource("/com/manager/stock/manager_stock/icons/reload.png").toExternalForm());
        Image printIcon = new Image(CreateTopBarOfReceiptUtil.class.getResource("/com/manager/stock/manager_stock/icons/printing.png").toExternalForm());
        Image exportIcon = new Image(CreateTopBarOfReceiptUtil.class.getResource("/com/manager/stock/manager_stock/icons/export.png").toExternalForm());

        Button btnAdd = new Button("Thêm", new ImageView(addIcon));
        Button btnEdit = new Button("Sửa", new ImageView(editIcon));
        Button btnDelete = new Button("Xóa", new ImageView(deleteIcon));
        Button btnReload = new Button("Tải lại", new ImageView(reloadIcon));
        Button btnPrint = new Button("In", new ImageView(printIcon));
        Button btnExport = new Button("Xuất", new ImageView(exportIcon));

        String buttonBaseStyle =
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #34536e;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 5px 10px;" +
                        "-fx-cursor: hand;" +
                        "-fx-alignment: center-left;" +
                        "-fx-graphic-text-gap: 8px;" +
                        "-fx-background-radius: 4px;" +
                        "-fx-border-color: #d0d0d0;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 4px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);";

        String buttonHoverStyle =
                "-fx-background-color: #d1eff7;" +
                        "-fx-border-color: #b0d0d8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);";

        String buttonPressedStyle =
                "-fx-background-color: #c3e8f0;" +
                        "-fx-border-color: #90b0b8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);" +
                        "-fx-translate-y: 1px;";

        for (Button btn : new Button[]{btnAdd, btnEdit, btnDelete, btnReload, btnPrint, btnExport}) {
            ((ImageView)btn.getGraphic()).setFitWidth(16);
            ((ImageView)btn.getGraphic()).setPreserveRatio(true);

            btn.setStyle(buttonBaseStyle);

            btn.setOnMouseEntered(e -> btn.setStyle(buttonBaseStyle + buttonHoverStyle));
            btn.setOnMouseExited(e -> btn.setStyle(buttonBaseStyle));

            btn.setOnMousePressed(e -> btn.setStyle(buttonBaseStyle + buttonPressedStyle));
            btn.setOnMouseReleased(e -> {
                if (btn.isHover()) {
                    btn.setStyle(buttonBaseStyle + buttonHoverStyle);
                } else {
                    btn.setStyle(buttonBaseStyle);
                }
            });

            btn.setOnMouseClicked((e) -> {
                AddOrUpdateReceiptScreen addOrUpdateReceiptScreen = new AddOrUpdateReceiptScreen();
                ScreenNavigator.navigateTo(addOrUpdateReceiptScreen);
            });
        }

        // HBox for the top bar
        HBox topBar = new HBox(10, btnAdd, btnEdit, btnDelete, btnReload, btnPrint, btnExport);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(5));

        topBar.setStyle(
                "-fx-background-color: #e0f2f7;" +
                        "-fx-border-color: #c0e0eb;" +
                        "-fx-border-width: 0 0 1px 0;" +
                        "-fx-padding: 11px 5;" +
                        "-fx-alignment: center-left;" +
                        "-fx-spacing: 10px;"
        );

        return topBar;
    }
}
