package com.manager.stock.manager_stock.utils;

import com.manager.stock.manager_stock.model.tableData.ImportReceiptModelTable;
import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.screen.transaction.AddOrUpdateReceiptScreen;
import com.manager.stock.manager_stock.screen.transaction.ImportReceiptScreen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.function.Supplier;

/**
 * @author Trọng Hướng
 */
public class CreateTopBarOfReceiptUtil {

    public static HBox createTopBar(Supplier<ImportReceiptModelTable> importReceiptModelSelected) {
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

        for (Button btn : new Button[]{btnAdd, btnEdit, btnDelete, btnReload, btnPrint, btnExport}) {
            ((ImageView)btn.getGraphic()).setFitWidth(16);
            ((ImageView)btn.getGraphic()).setPreserveRatio(true);

            AddCssStyleForBtnUtil.addCssStyleForBtn(btn);
            String btnType = btn.getText();
            btn.setOnMouseClicked((e) -> {
                switch (btnType) {
                    case "Thêm":
                        AddOrUpdateReceiptScreen addReceiptScreen = new AddOrUpdateReceiptScreen(null);
                        ScreenNavigator.navigateTo(addReceiptScreen);
                        break;
                    case "Sửa":
                        try {
                            System.out.println("Chỉnh sửa hóa đơn");
                            if(importReceiptModelSelected != null) {
                                System.out.println(importReceiptModelSelected.get());
                                AddOrUpdateReceiptScreen updateReceiptScreen = new AddOrUpdateReceiptScreen(importReceiptModelSelected.get());
                                ScreenNavigator.navigateTo(updateReceiptScreen);
                            }
                            else {
                                System.out.println("Không có hóa đơn nào được chọn.");
                            }
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case "Xóa":
                        System.out.println("Xóa hóa đơn");
                        break;
                    case "Tải lại":
                        System.out.println("Tải lại toàn bộ hóa đơn");
                        break;
                    case "In":
                        System.out.println("In hóa đơn");
                        break;
                    case "Xuất":
                        System.out.println("Xuất hóa đơn");
                        break;
                }
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
