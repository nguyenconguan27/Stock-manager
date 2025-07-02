package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.utils.CreateTopBarOfReceiptUtil;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;

/**
 * @author Trọng Hướng
 */
public class AddOrUpdateReceiptScreen extends VBox {

    private final ImportReceiptPresenter importReceiptPresenter;

    public AddOrUpdateReceiptScreen() {
        importReceiptPresenter = ImportReceiptPresenter.getInstance();
        HBox topBar = CreateTopBarOfReceiptUtil.createTopBar();
        VBox formAddNew = createFormAddNew();

        getChildren().addAll(topBar, formAddNew);
    }

    private VBox createFormAddNew() {
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(15));

        // Cột 1
        formGrid.add(new Label("Ngày vào sổ *"), 0, 0);
        formGrid.add(new DatePicker(), 1, 0);

        formGrid.add(new Label("Số chứng từ *"), 0, 1);
        formGrid.add(new TextField("00004"), 1, 1);

        formGrid.add(new Label("Loại tiền *"), 0, 2);
        HBox currencyBox = new HBox(5);
        ComboBox<String> currencyType = new ComboBox<>();
        currencyType.getItems().addAll("VND", "USD");
        currencyType.setValue("VND");

        TextField exchangeRate = new TextField("1,00");
        exchangeRate.setPrefWidth(70);

        currencyBox.getChildren().addAll(currencyType, exchangeRate);
        formGrid.add(currencyBox, 1, 2);

        formGrid.add(new Label("Nội dung *"), 0, 3);
        formGrid.add(new TextField("nhập kho thành phẩm tháng 4"), 1, 3, 3, 1);

        formGrid.add(new Label("Đối tác"), 0, 4);
        HBox partnerBox = new HBox(5);
        ComboBox<String> partnerCombo = new ComboBox<>();
        partnerCombo.setPromptText("Lựa chọn");
        Button searchPartner = new Button("🔍");
        Button addPartner = new Button("+");
        partnerBox.getChildren().addAll(partnerCombo, searchPartner, addPartner);
        formGrid.add(partnerBox, 1, 4, 3, 1);

        // Cột 2
        formGrid.add(new Label("Ngày chứng từ *"), 2, 0);
        formGrid.add(new DatePicker(), 3, 0);

        formGrid.add(new Label("Trạng thái"), 2, 1);
        TextField status = new TextField("1060");
        formGrid.add(status, 3, 1);

        formGrid.add(new Label("Số đơn mua"), 2, 2);
        HBox orderBox = new HBox(5);
        ComboBox<String> orderCombo = new ComboBox<>();
        orderCombo.setPromptText("Lựa chọn");
        Button searchOrder = new Button("🔍");
        Button clearOrder = new Button("✖");
        orderBox.getChildren().addAll(orderCombo, searchOrder, clearOrder);
        formGrid.add(orderBox, 3, 2);

        Button cancel = new Button("Cancel");
        VBox form = new VBox(10, formGrid, cancel);
        form.setPadding(new Insets(10));
        form.setStyle("-fx-background-color: #f2f9ff;");

        cancel.setOnMouseClicked(e -> {
            ImportReceiptScreen importReceiptScreen = new ImportReceiptScreen();
            importReceiptScreen.showTable();
            ScreenNavigator.navigateTo(importReceiptScreen);
        });
        return form;
    }

    private void createTableProductOfNewReceipt() {

    }
}
