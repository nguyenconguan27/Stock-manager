package com.manager.stock.manager_stock.screen.transaction;

import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.utils.AddCssStyleForBtnUtil;
import com.manager.stock.manager_stock.utils.AlertUtils;
import com.manager.stock.manager_stock.utils.CreateColumnTableUtil;
import com.manager.stock.manager_stock.utils.CreateTopBarOfReceiptUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class AddOrUpdateReceiptScreen extends VBox {

    private final ImportReceiptPresenter importReceiptPresenter;
    private final TableView<ImportReceiptDetailModelTable> productTable = new TableView<>();
    private boolean productLoaded = false;
    private final ObservableList<ProductModel> allProducts = FXCollections.observableArrayList();
    private FilteredList<ProductModel> filteredProducts = new FilteredList<>(allProducts, p -> true);
    private final ObservableList<ImportReceiptDetailModelTable> productDetails = FXCollections.observableArrayList();

    private TextField tfInvoiceNumber, tfInvoice, tfDeliveredBy, tfCompanyName, tfWareHouse;
    private DatePicker dpCreateAt;

    public AddOrUpdateReceiptScreen() {
        importReceiptPresenter = ImportReceiptPresenter.getInstance();
        HBox topBar = CreateTopBarOfReceiptUtil.createTopBar();
        VBox formAddNew = createFormAddNew();
        getChildren().addAll(topBar, formAddNew, createTableItemDetailByReceipt());
    }

    private VBox createFormAddNew() {
        // === Form bên trái ===
        GridPane leftForm = new GridPane();
        leftForm.setHgap(10);
        leftForm.setVgap(10);
        leftForm.setPadding(new Insets(15));

        leftForm.add(new Label("Ngày tạo *"), 0, 0);
        dpCreateAt = new DatePicker();
        leftForm.add(dpCreateAt, 1, 0);

        // mã hóa đơn
        leftForm.add(new Label("Số hóa đơn *"), 0, 1);
        tfInvoiceNumber = new TextField();
        leftForm.add(tfInvoiceNumber, 1, 1);

        // ghi chú
        leftForm.add(new Label("Mã hóa đơn *"), 0, 2);
        tfInvoice = new TextField();
        leftForm.add(tfInvoice, 1, 2);

        // === Form bên phải ===
        GridPane rightForm = new GridPane();
        rightForm.setHgap(10);
        rightForm.setVgap(10);
        rightForm.setPadding(new Insets(15));

        rightForm.add(new Label("Người giao hàng"), 0, 0);
        tfDeliveredBy = new TextField();
        rightForm.add(tfDeliveredBy, 1, 0);

        rightForm.add(new Label("Tên công ty"), 0, 1);
        tfCompanyName = new TextField();
        rightForm.add(tfCompanyName, 1, 1);

        rightForm.add(new Label("Kho"), 0, 2);
        tfWareHouse = new TextField();
        rightForm.add(tfWareHouse, 1, 2);

        // === Gộp 2 cột vào một hàng ngang ===
        HBox formColumns = new HBox(30, leftForm, rightForm);
        HBox.setHgrow(leftForm, Priority.ALWAYS);
        HBox.setHgrow(rightForm, Priority.ALWAYS);

        // === Form chọn sản phẩm ===
        Label lbProduct = new Label("Chọn sản phẩm *");
        ComboBox<ProductModel> cbProduct = new ComboBox<>();
        cbProduct.setPromptText("Tìm theo mã hoặc tên sản phẩm");
        cbProduct.setEditable(true);
        VBox productCol = new VBox(5, lbProduct, cbProduct);

        Label lbPlannedQty = new Label("Số lượng CT");
        TextField tfPlannedQty = new TextField();
        tfPlannedQty.setPrefWidth(100);
        VBox plannedQtyCol = new VBox(5, lbPlannedQty, tfPlannedQty);

        Label lbActualQty = new Label("Số lượng thực");
        TextField tfActualQty = new TextField();
        tfActualQty.setPrefWidth(100);
        VBox actualQtyCol = new VBox(5, lbActualQty, tfActualQty);

        Label lbUnitPrice = new Label("Đơn giá");
        TextField tfUnitPrice = new TextField();
        tfUnitPrice.setPrefWidth(100);
        VBox unitPriceCol = new VBox(5, lbUnitPrice, tfUnitPrice);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAddProduct = new Button("Thêm");
        btnAddProduct.setPrefWidth(80);
        btnAddProduct.setPrefHeight(30);
        btnAddProduct.setStyle("-fx-background-color: #e1f0f7; -fx-text-fill: #33536d; -fx-border-width: 1; -fx-border-color: #c1dfee;");

        VBox buttonBox = new VBox(btnAddProduct);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

        HBox productInputs = new HBox(20, productCol, plannedQtyCol, actualQtyCol, unitPriceCol);
        productInputs.setAlignment(Pos.CENTER_LEFT);

        HBox productBox = new HBox(20, productInputs, spacer, buttonBox);
        productBox.setPadding(new Insets(0, 15, 0, 15));
        productBox.setAlignment(Pos.CENTER_LEFT);

        cbProduct.setOnMouseClicked(e -> {
            if (!productLoaded) {
                List<ProductModel> products = importReceiptPresenter.loadAllProduct();
                allProducts.setAll(products);
                cbProduct.setItems(allProducts);
                productLoaded = true;
            }
        });

        cbProduct.setItems(filteredProducts);
        cbProduct.setEditable(true);

        cbProduct.setConverter(new StringConverter<ProductModel>() {
            @Override
            public String toString(ProductModel product) {
                if (product == null) return "";
                return product.getId() + " - " + product.getName();
            }

            @Override
            public ProductModel fromString(String string) {
                return allProducts.stream()
                        .filter(p -> (p.getId() + " - " + p.getName()).equals(string))
                        .findFirst().orElse(null);
            }
        });

        cbProduct.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (!productLoaded) {
                List<ProductModel> products = importReceiptPresenter.loadAllProduct();
                allProducts.setAll(products);
                filteredProducts = new FilteredList<>(allProducts, p -> true); // tạo lại
                cbProduct.setItems(filteredProducts);
                productLoaded = true;
            }

            ProductModel selected = cbProduct.getSelectionModel().getSelectedItem();
            if (selected != null && (selected.getId() + " - " + selected.getName()).equals(newText)) {
                return;
            }
            String normalizedInput = normalizeString(newText);
            filteredProducts.setPredicate(product -> {
                String idStr = (product.getId() + "").toLowerCase();
                String name = normalizeString(product.getName());
                return idStr.contains(normalizedInput) || name.contains(normalizedInput);
            });

            if (!cbProduct.isShowing()) cbProduct.show();
        });

        VBox root = new VBox(20, formColumns, productBox);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #e1f0f7;");

        // create css for btn add
        AddCssStyleForBtnUtil.addCssStyleForBtn(btnAddProduct);
        btnAddProduct.setOnMouseClicked((e) -> {
            // add product to product table
            ProductModel selectedProduct = cbProduct.getSelectionModel().getSelectedItem();
            int actualQuantity = Integer.parseInt(tfActualQty.getText());
            int plannedQuantity = Integer.parseInt(tfPlannedQty.getText());
            long unitPrice = Long.parseLong(tfUnitPrice.getText());

            addProductToTableProductOfReceipt(selectedProduct, actualQuantity, plannedQuantity, unitPrice);
        });

        return root;
    }

    private VBox createTableItemDetailByReceipt() {
        TableView<ImportReceiptDetailModelTable> productTable = new TableView<>();
        productTable.setEditable(true);

        TableColumn<ImportReceiptDetailModelTable, String> colProductId = CreateColumnTableUtil.createColumn("Mã SP", ImportReceiptDetailModelTable::productIdProperty);
        TableColumn<ImportReceiptDetailModelTable, String> colProductName = CreateColumnTableUtil.createColumn("Tên SP", ImportReceiptDetailModelTable::productNameProperty);

        TableColumn<ImportReceiptDetailModelTable, Number> colPlannedQty = new TableColumn<>("SL theo CT");
        colPlannedQty.setCellValueFactory(data -> data.getValue().plannedQuantityProperty());
        colPlannedQty.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        colPlannedQty.setOnEditCommit(event -> {
            ImportReceiptDetailModelTable row = event.getRowValue();
            row.plannedQuantityProperty().set(event.getNewValue().intValue());
        });

        TableColumn<ImportReceiptDetailModelTable, Number> colActualQty = new TableColumn<>("SL thực tế");
        colActualQty.setCellValueFactory(data -> data.getValue().actualQuantityProperty());
        colActualQty.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        colActualQty.setOnEditCommit(event -> {
            ImportReceiptDetailModelTable row = event.getRowValue();
            row.actualQuantityProperty().set(event.getNewValue().intValue());

            double newTotal = row.actualQuantityProperty().get() * row.unitPriceProperty().get();
            row.totalPriceProperty().set(newTotal);
            productTable.refresh();
        });

        TableColumn<ImportReceiptDetailModelTable, Number> colUnitPrice = new TableColumn<>("Đơn giá");
        colUnitPrice.setCellValueFactory(data -> data.getValue().unitPriceProperty());
        colUnitPrice.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        colUnitPrice.setOnEditCommit(event -> {
            ImportReceiptDetailModelTable row = event.getRowValue();
            row.unitPriceProperty().set(event.getNewValue().doubleValue());

            double newTotal = row.actualQuantityProperty().get() * row.unitPriceProperty().get();
            row.totalPriceProperty().set(newTotal);
            productTable.refresh();
        });

        TableColumn<ImportReceiptDetailModelTable, Number> colTotalPrice = new TableColumn<>("Thành tiền");
        colTotalPrice.setCellValueFactory(data -> data.getValue().totalPriceProperty());
        colTotalPrice.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        colTotalPrice.setOnEditCommit(event -> {
            ImportReceiptDetailModelTable row = event.getRowValue();
            row.totalPriceProperty().set(event.getNewValue().doubleValue());
        });

        TableColumn<ImportReceiptDetailModelTable, Void> colAction = new TableColumn<>("Thao tác");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnDelete = new Button("🗑");
            private final HBox pane = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.setStyle("-fx-background-color: #ffd966; -fx-cursor: hand;");
                btnDelete.setStyle("-fx-background-color: #f08080; -fx-cursor: hand;");
                pane.setAlignment(Pos.CENTER);

                btnDelete.setOnAction(event -> {
                    ImportReceiptDetailModelTable item = getTableView().getItems().get(getIndex());
                    getTableView().getItems().remove(item);
                });

                btnEdit.setOnAction(event -> {
                    getTableView().edit(getIndex(), colActualQty); // focus edit thực tế
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        productTable.getColumns().addAll(
                colProductId, colProductName, colPlannedQty, colActualQty,
                colUnitPrice, colTotalPrice, colAction
        );

        productTable.setPrefHeight(600);
        productTable.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #c1dfee; -fx-border-width: 1px;");
        productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        productTable.getColumns().forEach(col -> col.setResizable(true));

        Platform.runLater(() -> {
            productTable.lookupAll(".column-header").forEach(node -> {
                node.setStyle("-fx-background-color: #e1f0f7; -fx-pref-height: 35px; " +
                        "-fx-border-width: 0 1px 0 0; -fx-border-color: #c1dfee");
            });
            productTable.lookupAll(".column-header .label").forEach(label -> {
                label.setStyle("-fx-text-fill: #34536e;");
            });
        });

        productTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(ImportReceiptDetailModelTable item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: #2f7a9a; -fx-text-fill: white;");
                } else {
                    setStyle(getIndex() % 2 == 0 ? "-fx-background-color: #ffffff;" : "-fx-background-color: #e0f2f7;");
                }
            }

            {
                selectedProperty().addListener((obs, oldVal, newVal) -> updateItem(getItem(), isEmpty()));
            }
        });

        VBox box = new VBox(productTable);
        box.setSpacing(0);
        box.setPadding(Insets.EMPTY);
        box.setStyle("-fx-padding: 0; -fx-background-insets: 0;");
        productTable.setItems(productDetails);

        // khởi tạo 2 button submit và cancel
        HBox actionRow = new HBox(10);

        Button saveBtn = new Button("Save");
        AddCssStyleForBtnUtil.addCssStyleForBtn(saveBtn);
        saveBtn.setOnMouseClicked(e -> {
            String createAtStr = dpCreateAt.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            ImportReceiptModel importReceiptModel = new ImportReceiptModel(
                    null,
                    tfInvoiceNumber.getText(),
                    createAtStr,
                    tfDeliveredBy.getText(),
                    tfInvoice.getText(),
                    tfCompanyName.getText(),
                    tfWareHouse.getText(),
                    0,
                    null
            );
            ImportReceiptPresenter presenter = ImportReceiptPresenter.getInstance();
            try {
                System.out.println("Save import receipt: " + importReceiptModel);
                presenter.saveImportReceipt(importReceiptModel, productDetails);
                AlertUtils.alert("Thêm mới phiếu nhập thành công.", "INFORMATION", "Thành công", "Thành công");
                ImportReceiptScreen importReceiptScreen = new ImportReceiptScreen();
                importReceiptScreen.showTable();
                ScreenNavigator.navigateTo(importReceiptScreen);
            }
            catch (DaoException exception) {
                AlertUtils.alert(exception.getMessage(), "ERROR", "Lỗi khi thực hiện lưu phiếu nhập.", "Lỗi khi thực hiện lưu phiếu nhập.");
            }
        });

        Button cancelBtn = new Button("Cancel");
        AddCssStyleForBtnUtil.addCssStyleForBtn(cancelBtn);
        cancelBtn.setOnMouseClicked(e -> {
            ImportReceiptScreen importReceiptScreen = new ImportReceiptScreen();
            importReceiptScreen.showTable();
            ScreenNavigator.navigateTo(importReceiptScreen);
        });

        actionRow.getChildren().addAll(saveBtn, cancelBtn);
        actionRow.setStyle("-fx-padding: 5; -fx-background-color: #e1f0f7; -fx-border-color: #c1dfee; -fx-border-width: 1px;");

        box.getChildren().add(actionRow);
        return box;
    }

    private String normalizeString(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }

    private void addProductToTableProductOfReceipt(ProductModel product, int actualQuantity, int plannedQuantity, long unitPrice) {
        ImportReceiptDetailModelTable productExists = productDetails.stream()
                        .filter(p -> (p.getProductId().equals(product.getId()) && p.getUnitPrice() == unitPrice))
                        .findFirst()
                        .orElse(null);
        if(productExists != null) {
            productExists.setActualQuantity(productExists.getActualQuantity() + actualQuantity);
            productExists.setTotalPrice(productExists.getTotalPrice() + (plannedQuantity * unitPrice));
        }
        else {
            productDetails.add(new ImportReceiptDetailModelTable(
                    System.currentTimeMillis(),
                    0,
                    product.getId(),
                    plannedQuantity,
                    actualQuantity,
                    unitPrice,
                    actualQuantity * unitPrice,
                    product.getName())
            );
        }
        productTable.refresh();
    }
}
