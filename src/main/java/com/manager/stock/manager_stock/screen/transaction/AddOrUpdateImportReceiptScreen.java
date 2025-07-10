package com.manager.stock.manager_stock.screen.transaction;

import com.browniebytes.javafx.control.DateTimePicker;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptDetailModelMapper;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptModelMapper;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptModelTable;
import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.screen.transaction.presenter.ImportReceiptPresenter;
import com.manager.stock.manager_stock.utils.*;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class AddOrUpdateImportReceiptScreen extends BaseAddOrUpdateReceiptScreen<ImportReceiptModelTable, ImportReceiptDetailModelTable> {

    private final ImportReceiptPresenter importReceiptPresenter = ImportReceiptPresenter.getInstance();
    protected TextField tfInvoice, tfDeliveredBy, tfCompanyName;

    public AddOrUpdateImportReceiptScreen(ImportReceiptModelTable importReceiptModelTable) {
        super(importReceiptModelTable);
//        importReceiptPresenter = ImportReceiptPresenter.getInstance();
    }

    @Override
    protected VBox createFormAddNew(ImportReceiptModelTable model) {
        // === Form bên trái ===
        GridPane leftForm = new GridPane();
        leftForm.setHgap(10);
        leftForm.setVgap(10);
        leftForm.setPadding(new Insets(15));

        leftForm.add(new Label("Ngày tạo *"), 0, 0);
        dateTimePicker = new DateTimePicker(LocalDateTime.now(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        leftForm.add(dateTimePicker, 1, 0);

        leftForm.add(new Label("Số hóa đơn *"), 0, 1);
        tfInvoiceNumber = new TextField();
        leftForm.add(tfInvoiceNumber, 1, 1);

        leftForm.add(new Label("Mã hóa đơn *"), 0, 2);
        tfInvoice = new TextField();
        leftForm.add(tfInvoice, 1, 2);

        // === Form bên phải ===
        GridPane rightForm = new GridPane();
        rightForm.setHgap(10);
        rightForm.setVgap(10);
        rightForm.setPadding(new Insets(15));

        rightForm.add(new Label("Người giao hàng *"), 0, 0);
        tfDeliveredBy = new TextField();
        rightForm.add(tfDeliveredBy, 1, 0);

        rightForm.add(new Label("Tên công ty *"), 0, 1);
        tfCompanyName = new TextField();
        rightForm.add(tfCompanyName, 1, 1);

        rightForm.add(new Label("Kho *"), 0, 2);
        tfWareHouse = new TextField();
        rightForm.add(tfWareHouse, 1, 2);

        if (model != null) {
            if (model.getCreateAt() != null && !model.getCreateAt().isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//                    dateTimePicker.setValue(LocalDate.parse(model.getCreateAt(), formatter));
                    dateTimePicker.setTime(LocalDateTime.parse(model.getCreateAt(), formatter));
                } catch (Exception e) {
                    System.err.println("Lỗi định dạng ngày: " + model.getCreateAt());
                }
            }
            tfInvoiceNumber.setText(model.getInvoiceNumber() != null ? model.getInvoiceNumber() : "");
            tfInvoice.setText(model.getInvoice() != null ? model.getInvoice() : "");
            tfDeliveredBy.setText(model.getDeliveredBy() != null ? model.getDeliveredBy() : "");
            tfCompanyName.setText(model.getCompanyName() != null ? model.getCompanyName() : "");
            tfWareHouse.setText(model.getWarehouseName() != null ? model.getWarehouseName() : "");

            // Lấy danh sách receiptDetail
            totalPriceOfReceipt = model.getTotalPrice();
            totalPriceLabel.setText(FormatMoney.format(totalPriceOfReceipt));
            ImportReceiptPresenter presenter = ImportReceiptPresenter.getInstance();
            List<ImportReceiptDetailModel> importReceiptDetailModels = presenter.loadImportReceiptDetailList(model.getId());
            List<ImportReceiptDetailModelTable> importReceiptDetailModelTablesByReceipt = GenericConverterBetweenModelAndTableData.convertToList(importReceiptDetailModels, ImportReceiptDetailModelMapper.INSTANCE::toViewModel);
            productDetails.setAll(importReceiptDetailModelTablesByReceipt);
        }

        // === Gộp 2 form vào 1 hàng ngang ===
        HBox formColumns = new HBox(30, leftForm, rightForm);
        HBox.setHgrow(leftForm, Priority.ALWAYS);
        HBox.setHgrow(rightForm, Priority.ALWAYS);

        // === Form chọn sản phẩm ===
        Label lbProduct = new Label("Chọn sản phẩm *");
        ComboBox<ProductModel> cbProduct = new ComboBox<>();
        cbProduct.setPromptText("Tìm theo mã hoặc tên sản phẩm");
        cbProduct.setEditable(true);
        VBox productCol = new VBox(5, lbProduct, cbProduct);

        Label lbPlannedQty = new Label("Số lượng CT *");
        TextField tfPlannedQty = new TextField();
        tfPlannedQty.setPrefWidth(100);
        VBox plannedQtyCol = new VBox(5, lbPlannedQty, tfPlannedQty);

        Label lbActualQty = new Label("Số lượng thực *");
        TextField tfActualQty = new TextField();
        tfActualQty.setPrefWidth(100);
        VBox actualQtyCol = new VBox(5, lbActualQty, tfActualQty);

        Label lbUnitPrice = new Label("Đơn giá *");
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

        // === Logic xử lý ComboBox sản phẩm ===
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
                filteredProducts = new FilteredList<>(allProducts, p -> true);
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

        // === Giao diện tổng thể ===
        VBox root = new VBox(20, formColumns, productBox);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #e1f0f7;");

        // === Nút thêm sản phẩm ===
        AddCssStyleForBtnUtil.addCssStyleForBtn(btnAddProduct);
        btnAddProduct.setOnMouseClicked((e) -> {
            try {
                ProductModel selectedProduct = cbProduct.getSelectionModel().getSelectedItem();
                int actualQuantity = Integer.parseInt(tfActualQty.getText());
                int plannedQuantity = Integer.parseInt(tfPlannedQty.getText());
                long unitPrice = Long.parseLong(tfUnitPrice.getText());

                addProductToTableProductOfReceipt(selectedProduct, actualQuantity, plannedQuantity, unitPrice);
            } catch (NumberFormatException ex) {
                AlertUtils.alert("Vui lòng nhập đúng định dạng số cho số lượng và đơn giá.", "WARNING", "Cảnh báo", "Lỗi định dạng");
            }
        });

        return root;
    }

    @Override
    protected VBox createTableItemDetailByReceipt(ImportReceiptModelTable oldImportReceiptModelTable) {
        TableView<ImportReceiptDetailModelTable> productTable = new TableView<>();
        productTable.setEditable(true);

        TableColumn<ImportReceiptDetailModelTable, String> colProductId = CreateColumnTableUtil.createColumn("Mã SP", ImportReceiptDetailModelTable::codeProperty);
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

        TableColumn<ImportReceiptDetailModelTable, String> colUnitPrice = new TableColumn<>("Đơn giá");
        colUnitPrice.setCellValueFactory(data -> data.getValue().unitPriceFormatProperty());

        TableColumn<ImportReceiptDetailModelTable, String> colTotalPrice = new TableColumn<>("Thành tiền");
        colTotalPrice.setCellValueFactory(data -> data.getValue().totalPriceFormatProperty());
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
                    totalPriceOfReceipt -= item.getTotalPrice();
                    totalPriceLabel.setText(FormatMoney.format(totalPriceOfReceipt));
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
            if(dateTimePicker.dateTimeProperty() == null || dateTimePicker.dateTimeProperty().get() == null) {
                AlertUtils.alert("Vui lòng chọn ngày nhập hàng.", "WARNING", "Cảnh báo", "Thiếu thông tin");
                return;
            }
            String createAtStr = dateTimePicker.dateTimeProperty().get().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            String invoiceNumber = tfInvoiceNumber.getText().trim();
            String deliveredBy = tfDeliveredBy.getText().trim();
            String invoice = tfInvoice.getText().trim();
            String companyName = tfCompanyName.getText().trim();
            String wareHouseName = tfWareHouse.getText().trim();
            if(invoiceNumber.isEmpty()) {
                AlertUtils.alert("Vui lòng nhập số hóa đơn.", "WARNING", "Cảnh báo", "Thiếu thông tin");
                return;
            }
            if(deliveredBy.isEmpty()) {AlertUtils.alert("Vui lòng nhập người giao hàng.", "WARNING", "Cảnh báo", "Thiếu thông tin"); return;}
            if(invoice.isEmpty()) {AlertUtils.alert("Vui lòng nhập mã hóa đơn.", "WARNING", "Cảnh báo", "Thiếu thông tin"); return;}
            if(companyName.isEmpty()) {AlertUtils.alert("Vui lòng nhập tên công ty.", "WARNING", "Cảnh báo", "Thiếu thông tin"); return;}
            if(wareHouseName.isEmpty()) {AlertUtils.alert("Vui lòng nhập kho.", "WARNING", "Cảnh báo", "Thiếu thông tin"); return;}
            ImportReceiptModel importReceiptModel = new ImportReceiptModel(
                    oldImportReceiptModelTable != null ? oldImportReceiptModelTable.getId() : null,
                    invoiceNumber,
                    createAtStr,
                    deliveredBy,
                    invoice,
                    companyName,
                    wareHouseName,
                    totalPriceOfReceipt,
                    FormatMoney.formatMoneyToWord((long)totalPriceOfReceipt)
            );
            ImportReceiptPresenter presenter = ImportReceiptPresenter.getInstance();
            if(productDetails.isEmpty()) {
                AlertUtils.alert("Phiếu nhập này chưa có sản phẩm nào, vui lòng chọn ít nhất 1 sản phẩm.", "WARNING", "Cảnh báo", "Thiếu thông tin");
                return;
            }
            try {
                System.out.println("Save import receipt: " + importReceiptModel);
                // thêm mới hóa đơn nhập
                if(oldImportReceiptModelTable == null) {
                    presenter.saveImportReceipt(importReceiptModel, productDetails, changeQuantityByProductMap, changeTotalPriceByProductMap);
                    AlertUtils.alert("Thêm mới phiếu nhập thành công.", "INFORMATION", "Thành công", "Thành công");
                }
                // Cập nhật hóa đơn nhập
                else {
                    List<ImportReceiptDetailModelTable> newProductDetails = productDetails.stream()
                                    .filter(importReceiptDetailModelTable -> changeIdsOfReceiptDetails.contains(importReceiptDetailModelTable.getId()) || importReceiptDetailModelTable.getId() == -1)
                                    .collect(Collectors.toList());
                    ImportReceiptModel oldImportReceiptModel = ImportReceiptModelMapper.INSTANCE.fromViewModelToModel(oldImportReceiptModelTable);
                    presenter.updateImportReceipt(importReceiptModel, oldImportReceiptModel, newProductDetails, changeQuantityByProductMap, changeTotalPriceByProductMap);
                    AlertUtils.alert("Cập nhật phiếu nhập thành công.", "INFORMATION", "Thành công", "Thành công");
                }
                ImportReceiptScreen importReceiptScreen = new ImportReceiptScreen();
                importReceiptScreen.showTable();
                ScreenNavigator.navigateTo(importReceiptScreen);
            }
            catch (DaoException exception) {
                AlertUtils.alert(exception.getMessage(), "ERROR", "Lỗi khi thực hiện thao tác với phiếu nhập.", "Lỗi khi thực hiện lưu phiếu nhập.");
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

        // create total price
        HBox totalPriceRow = new HBox(10);
        totalPriceRow.setStyle("-fx-padding: 5; -fx-background-color: #e1f0f7; -fx-border-color: #c1dfee; -fx-border-width: 1px; ");
        Label totalPriceLabelTitle = new Label("Tổng cộng: ");
        totalPriceRow.getChildren().addAll(totalPriceLabelTitle, totalPriceLabel);
        styleLabel(totalPriceLabelTitle);
        styleLabel(totalPriceLabel);

        box.getChildren().addAll(totalPriceRow, actionRow);
        return box;
    }

    private void addProductToTableProductOfReceipt(ProductModel product, int actualQuantity, int plannedQuantity, long unitPrice) {
        ImportReceiptDetailModelTable productExists = productDetails.stream()
                        .filter(p -> (p.getProductId().equals(product.getId()) && p.getUnitPrice() == unitPrice))
                        .findFirst()
                        .orElse(null);
        if(actualQuantity < 0 || plannedQuantity < 0) {
            AlertUtils.alert("Số lượng nhập không được phép nhỏ hơn 0, vui lòng nhập lại.", "WARNING", "Cảnh báo", "Nhập dữ liệu sai");
            return;
        }
        if(unitPrice < 0) {
            AlertUtils.alert("Đơn giá không được phép nhỏ hơn 0, vui lòng nhập lại.", "WARNING", "Cảnh báo", "Nhập dữ liệu sai");
            return;
        }
        double currentTotalPrice = actualQuantity * unitPrice;
        totalPriceOfReceipt += currentTotalPrice;
        totalPriceLabel.setText(FormatMoney.format(totalPriceOfReceipt));
        if(productExists != null) {
            int actualQuantityCurrent = productExists.getActualQuantity() + actualQuantity;
            double totalPriceCurrent = productExists.getTotalPrice() + currentTotalPrice;
            productExists.setActualQuantity(actualQuantityCurrent);
            productExists.setTotalPrice(totalPriceCurrent);
            productExists.setTotalPriceFormat(FormatMoney.format(totalPriceCurrent));
            changeIdsOfReceiptDetails.add(productExists.getId());
            //
            int changeQuantityByProduct = changeQuantityByProductMap.getOrDefault(productExists.getProductId(), 0);
            changeQuantityByProductMap.put(productExists.getProductId(), changeQuantityByProduct + actualQuantity);

            double changeTotalPriceByProduct = changeTotalPriceByProductMap.getOrDefault(productExists.getProductId(), 0.0);
            changeTotalPriceByProductMap.put(productExists.getProductId(), changeTotalPriceByProduct + currentTotalPrice);
        }
        else {
            productDetails.add(new ImportReceiptDetailModelTable(
                    -1,
                    0,
                    product.getId(),
                    plannedQuantity,
                    actualQuantity,
                    unitPrice,
                    currentTotalPrice,
                    product.getName(),
                    FormatMoney.format(unitPrice),
                    FormatMoney.format(currentTotalPrice),
                    product.getCode()
            ));
            changeQuantityByProductMap.put(product.getId(), actualQuantity);
            changeTotalPriceByProductMap.put(product.getId(), currentTotalPrice);
        }
        productTable.refresh();
    }

}
