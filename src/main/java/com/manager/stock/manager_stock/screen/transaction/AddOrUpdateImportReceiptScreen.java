package com.manager.stock.manager_stock.screen.transaction;

import com.browniebytes.javafx.control.DateTimePicker;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.exception.InvalidException;
import com.manager.stock.manager_stock.exception.StockUnderFlowException;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptDetailModelMapper;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptModelMapper;
import com.manager.stock.manager_stock.model.ImportReceiptDetailModel;
import com.manager.stock.manager_stock.model.ImportReceiptModel;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptModelTable;
import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.screen.transaction.presenter.ImportReceiptPresenter;
import com.manager.stock.manager_stock.screen.transaction.presenter.InventoryReceiptService;
import com.manager.stock.manager_stock.utils.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.util.converter.NumberStringConverter;
import org.apache.xmlbeans.impl.xb.xsdschema.ImportDocument;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class AddOrUpdateImportReceiptScreen extends BaseAddOrUpdateReceiptScreen<ImportReceiptModelTable, ImportReceiptDetailModelTable> {

    protected TextField tfInvoice, tfDeliveredBy, tfCompanyName;
    public AddOrUpdateImportReceiptScreen(ImportReceiptModelTable importReceiptModelTable) {
        super(importReceiptModelTable);
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

        leftForm.add(new Label("Số phiếu nhập kho *"), 0, 2);
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
                    System.out.println(model.getCreateAt());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    LocalDateTime createAt = LocalDateTime.parse(model.getCreateAt().trim(), formatter);
                    dateTimePicker.setTime(createAt);
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

            int totalQuantity = importReceiptDetailModels.stream().mapToInt(ImportReceiptDetailModel::getActualQuantity).sum();
            totalQuantityLabel.setText(totalQuantity + "");
        }

        // === Gộp 2 form vào 1 hàng ngang ===
        HBox formColumns = new HBox(30, leftForm, rightForm);
        HBox.setHgrow(leftForm, Priority.ALWAYS);
        HBox.setHgrow(rightForm, Priority.ALWAYS);

        // === Form chọn sản phẩm ===
        TextField tfProduct = new TextField();
        tfProduct.setPrefWidth(250);
        tfProduct.setPromptText("Tìm theo mã hoặc tên sản phẩm");
        VBox productCol = new VBox(5, new Label("Chọn sản phẩm *"), tfProduct);

        Label lbPlannedQty = new Label("Số lượng CT *");
        TextField tfPlannedQty = new TextField();
        tfPlannedQty.setPrefWidth(100);
        VBox plannedQtyCol = new VBox(5, lbPlannedQty, tfPlannedQty);

        Label lbActualQty = new Label("Số lượng thực *");
        TextField tfActualQty = new TextField();
        tfActualQty.setPrefWidth(100);
        VBox actualQtyCol = new VBox(5, lbActualQty, tfActualQty);

        Label lbUnitPrice = new Label("Đơn giá gốc*");
        TextField tfUnitPrice = new TextField("0");
        tfUnitPrice.setPrefWidth(100);
        VBox unitPriceCol = new VBox(5, lbUnitPrice, tfUnitPrice);
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*(\\.\\d*)?") ? change : null;
        };
        tfUnitPrice.setTextFormatter(new TextFormatter<>(filter));

        Label lbVat = new Label("VAT(%) *");
        TextField tfVat = new TextField("0");
        tfVat.setPrefWidth(100);
        VBox vatCol = new VBox(5, lbVat, tfVat);
        tfVat.setTextFormatter(new TextFormatter<>(filter));

        Label lbUnitPriceAfterVat = new Label("Đơn giá sau vat *");
        TextField tfUnitPriceAfterVat = new TextField("0");
        tfUnitPriceAfterVat.setEditable(false);
        tfUnitPriceAfterVat.setPrefWidth(100);
        VBox unitPriceAfterVat = new VBox(5, lbUnitPriceAfterVat, tfUnitPriceAfterVat);

        // khi thay doi don gia
        tfUnitPrice.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                double price = Double.parseDouble(newVal);
                double vat = Double.parseDouble(tfVat.getText());
                System.out.println("Thuế: " + (price * vat/100));
                double unitPriceTmp = price + (price * vat/100);
                System.out.println("Đơn giá sau thuế: " + unitPriceTmp);
                tfUnitPriceAfterVat.setText(String.valueOf(unitPriceTmp));
            }
        });

        // khi thay doi vat
        tfVat.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                double vat = Double.parseDouble(newVal);
                double price = Double.parseDouble(tfUnitPrice.getText());

                System.out.println("Thuế: " + (price * vat/100));
                double unitPriceTmp = price + (price * vat/100);
                System.out.println("Đơn giá sau thuế: " + unitPriceTmp);
                tfUnitPriceAfterVat.setText(String.valueOf(unitPriceTmp));
            }
        });

        Label lbInventory = new Label("Tồn kho");
        TextField tfInventory = new TextField();
        tfInventory.setPrefWidth(100);
        tfInventory.setEditable(false);
        VBox inventoryCol = new VBox(5, lbInventory, tfInventory);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnAddProduct = new Button("Thêm");
        btnAddProduct.setPrefWidth(80);
        btnAddProduct.setPrefHeight(30);
        btnAddProduct.setStyle("-fx-background-color: #e1f0f7; -fx-text-fill: #33536d; -fx-border-width: 1; -fx-border-color: #c1dfee;");

        VBox buttonBox = new VBox(btnAddProduct);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

        HBox productInputs = new HBox(35, productCol, plannedQtyCol, actualQtyCol, unitPriceCol, vatCol, unitPriceAfterVat, inventoryCol);
        productInputs.setAlignment(Pos.CENTER_LEFT);

        HBox productBox = new HBox(20, productInputs, spacer, buttonBox);
        productBox.setPadding(new Insets(0, 15, 0, 15));
        productBox.setAlignment(Pos.CENTER_LEFT);

        ImportReceiptPresenter importReceiptPresenter = ImportReceiptPresenter.getInstance();
        List<ProductModel> products = importReceiptPresenter.loadAllProduct();
        allProducts.setAll(products);

        ProductAutoComplete ac = new ProductAutoComplete(tfProduct, allProducts);
        AtomicReference<ProductModel> selected = new AtomicReference<>();
        ac.valueProperty().addListener((obs, oldP, newProduct) -> {
            if (newProduct != null) {
                LocalDateTime createAtStr = dateTimePicker.dateTimeProperty().get();
                int academicYear = createAtStr.getYear();
                int quantityInStock = importReceiptPresenter.findQuantityInStockByProductIdAndAcademicYear(newProduct.getId(), academicYear);
                tfInventory.setText(String.valueOf(quantityInStock));
                selected.set(newProduct);
            }
        });

        // === Giao diện tổng thể ===
        VBox root = new VBox(20, formColumns, productBox);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #e1f0f7;");

        // === Nút thêm sản phẩm ===
        AddCssStyleForBtnUtil.addCssStyleForBtn(btnAddProduct);
        btnAddProduct.setOnMouseClicked((e) -> {
            try {
                ProductModel selectedProduct = selected.get();
                int actualQuantity = Integer.parseInt(tfActualQty.getText());
                int plannedQuantity = Integer.parseInt(tfPlannedQty.getText());
                double vat = Double.parseDouble(tfVat.getText());
                long unitPrice = Long.parseLong(tfUnitPrice.getText());
                addProductToTableProductOfReceipt(selectedProduct, actualQuantity, plannedQuantity, unitPrice, vat);

                tfActualQty.clear();
                tfPlannedQty.clear();
                tfUnitPrice.clear();
                tfProduct.clear();
                tfInventory.clear();
                tfVat.clear();
                tfUnitPriceAfterVat.clear();
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

            int oldValue = row.getPlannedQuantity();
            int newValue = event.getNewValue().intValue();

            row.setPlannedQuantity(newValue);

        });

        TableColumn<ImportReceiptDetailModelTable, Number> colActualQty = new TableColumn<>("SL thực tế");
        colActualQty.setCellValueFactory(data -> data.getValue().actualQuantityProperty());
        colActualQty.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        colActualQty.setOnEditCommit(event -> {
            ImportReceiptDetailModelTable row = event.getRowValue();
            int newValue = event.getNewValue().intValue();
            int oldValue = row.getActualQuantity();
            int changeQuantity = newValue - oldValue;
//            double unitPrice =  + row.getUnitPrice() * (row.getVat() / 100);

            double unitPrice = (row.getUnitPrice() * row.getVat() / 100) + row.getUnitPrice();
            double changeTotalPrice = changeQuantity * unitPrice;

            if(row.getId() != null) {
                int changeQuantityByProduct = changeQuantityByProductMap.getOrDefault(row.getProductId(), 0);
                changeQuantityByProductMap.put(row.getProductId(), changeQuantity + changeQuantityByProduct);
                double changeTotalPriceByProduct = changeTotalPriceByProductMap.getOrDefault(row.getProductId(), 0.0);
                changeTotalPriceByProductMap.put(row.getProductId(), changeTotalPriceByProduct + changeTotalPrice);
                changeIdsOfReceiptDetails.add(row.getId());
            }
            double newTotal = newValue * unitPrice;
            row.setTotalPrice(newTotal);
            row.setTotalPriceFormat(FormatMoney.format(newTotal));
            row.actualQuantityProperty().set(event.getNewValue().intValue());
            totalPriceOfReceipt += changeTotalPrice;
            totalPriceLabel.setText(FormatMoney.format(totalPriceOfReceipt));
            int totalQuantity = Integer.parseInt(totalQuantityLabel.getText()) + changeQuantity;
            totalQuantityLabel.setText(totalQuantity + "");
            productTable.refresh();
        });

        TableColumn<ImportReceiptDetailModelTable, String> colUnitPrice = new TableColumn<>("Đơn giá gốc");
        TableColumn<ImportReceiptDetailModelTable, String> colVat = new TableColumn<>("VAT");
        TableColumn<ImportReceiptDetailModelTable, String> colUnitPriceAfterVat = new TableColumn<>("Đơn giá sau VAT");
        colUnitPrice.setCellValueFactory(data -> data.getValue().unitPriceFormatProperty());

        colVat.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVat() + "%"));
        colUnitPriceAfterVat.setCellValueFactory(data -> data.getValue().unitPriceAfterVatProperty());

        colUnitPrice.setCellFactory(TextFieldTableCell.forTableColumn());
        colVat.setCellFactory(TextFieldTableCell.forTableColumn());
        colUnitPrice.setOnEditCommit(event -> {
            ImportReceiptDetailModelTable row = event.getRowValue();
            String newValueStr = event.getNewValue();
            double vat = row.getVat();
            double newValue = 0;
            try {
                newValue = FormatMoney.parseFlexibleMoney(newValueStr);
            }
            catch (InvalidException e) {
                AlertUtils.alert(e.getMessage(), "ERROR", "Lỗi định dạng", "Lỗi định dạng");
                return;
            }
            // tính lại đơn giá cũ
            double changeUnitPrice = (newValue - row.getUnitPrice()) * (1 + (vat / 100));
            System.out.println("Tong tien thay doi: " + changeUnitPrice);
            double changeTotalPrice = changeUnitPrice * row.getActualQuantity();

//            if(row.getId() != null) {
//                double changeTotalPriceByProduct = changeTotalPriceByProductMap.getOrDefault(row.getProductId(), 0.0);
//                changeTotalPriceByProductMap.put(row.getProductId(), changeTotalPriceByProduct + changeTotalPrice);
//                changeIdsOfReceiptDetails.add(row.getId());
//            }

            double newTotal = (newValue + (newValue * (vat / 100))) * row.getActualQuantity();
            row.setTotalPrice(newTotal);
            row.setTotalPriceFormat(FormatMoney.format(newTotal));
            row.unitPriceProperty().set(newValue);
            row.unitPriceFormatProperty().set(FormatMoney.format(newValue));
            row.setUnitPriceAfterVat(FormatMoney.format((newValue + (newValue * (vat / 100)))));
            totalPriceOfReceipt += changeTotalPrice;
            totalPriceLabel.setText(FormatMoney.format(totalPriceOfReceipt));

            productTable.refresh();
        });
        // cho phép thay doi vat khi chỉnh sửa
        colVat.setOnEditCommit(event -> {
            ImportReceiptDetailModelTable row = event.getRowValue();
            String newValueStr = event.getNewValue();
            // gia tri vat moi
            double newValue = 0;
            try {
                newValue = Double.parseDouble(newValueStr.replace("%", ""));
            }
            catch (InvalidException e) {
                AlertUtils.alert(e.getMessage(), "ERROR", "Lỗi định dạng", "Lỗi định dạng");
                return;
            }
            // gia tri vat cu
            double oldValue = row.getVat();
            // tinh tong tien thay doi
            double changeUnitPrice = ((newValue - oldValue) / 100) * row.getUnitPrice();
            System.out.println("Đơn gia thay doi: " + changeUnitPrice);
            double changeTotalPrice = changeUnitPrice * row.getActualQuantity();
            System.out.println("Tong tien thay doi: " + changeTotalPrice);
//            if(row.getId() != null) {
//                double changeTotalPriceByProduct = changeTotalPriceByProductMap.getOrDefault(row.getProductId(), 0.0);
//                changeTotalPriceByProductMap.put(row.getProductId(), changeTotalPriceByProduct + changeTotalPrice);
//                changeIdsOfReceiptDetails.add(row.getId());
//            }
            // tong tien moi
            // tính lại đơn giá mới
            double unitPriceNew = row.getUnitPrice() + (newValue / 100 * row.getUnitPrice());
            System.out.println("Don gia moi: " + unitPriceNew);
            double newTotal = unitPriceNew * row.getActualQuantity();
            System.out.println("Tổng tiền mới: " + newTotal);
            row.setTotalPrice(newTotal);
            row.setTotalPriceFormat(FormatMoney.format(newTotal));
            row.setUnitPriceAfterVat(FormatMoney.format(unitPriceNew));
            row.setVat(newValue);
            totalPriceOfReceipt += changeTotalPrice;
            totalPriceLabel.setText(FormatMoney.format(totalPriceOfReceipt));

            productTable.refresh();
        });

        TableColumn<ImportReceiptDetailModelTable, String> colTotalPrice = new TableColumn<>("Thành tiền");
        colTotalPrice.setCellValueFactory(data -> data.getValue().totalPriceFormatProperty());
        TableColumn<ImportReceiptDetailModelTable, Void> colAction = new TableColumn<>("Thao tác");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("🗑");
            private final HBox pane = new HBox(5, btnDelete);
            {
                btnDelete.setStyle("-fx-background-color: #f08080; -fx-cursor: hand;");
                pane.setAlignment(Pos.CENTER);

                btnDelete.setOnAction(event -> {
                    ImportReceiptDetailModelTable item = getTableView().getItems().get(getIndex());
                    totalPriceOfReceipt -= item.getTotalPrice();
                    totalPriceLabel.setText(FormatMoney.format(totalPriceOfReceipt));
                    int totalQuantity = Integer.parseInt(totalQuantityLabel.getText()) - item.getActualQuantity();
                    totalQuantityLabel.setText(totalQuantity + "");
                    receiptDetailIdsDeleted.add(item.getId());
                    if(item.getId() != null) {
                        int changeQuantityByProduct = changeQuantityByProductMap.getOrDefault(item.getProductId(), 0);
                        double changeTotalPriceByProduct = changeTotalPriceByProductMap.getOrDefault(item.getProductId(), 0.0);
                        changeQuantityByProduct -= item.getActualQuantity();
                        changeTotalPriceByProduct -= item.getTotalPrice();
                        changeQuantityByProductMap.put(item.getProductId(), changeQuantityByProduct);
                        changeTotalPriceByProductMap.put(item.getProductId(), changeTotalPriceByProduct);
                    }
                    item.setActualQuantity(0);
                    item.setTotalPrice(0);
                    productDetailsToDelete.add(item);
//                    item.setIsDeleted(true);
                    getTableView().getItems().remove(item);
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
                colUnitPrice, colVat, colUnitPriceAfterVat, colTotalPrice, colAction
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
            if(invoice.isEmpty()) {AlertUtils.alert("Vui lòng nhập số phiếu nhập kho.", "WARNING", "Cảnh báo", "Thiếu thông tin"); return;}
            ImportReceiptModel importReceiptModel = new ImportReceiptModel (
                    oldImportReceiptModelTable != null ? oldImportReceiptModelTable.getId() : -1,
                    invoiceNumber,
                    createAtStr,
                    deliveredBy,
                    invoice,
                    companyName,
                    wareHouseName,
                    totalPriceOfReceipt,
                    FormatMoney.formatMoneyToWord((long)totalPriceOfReceipt)
            );
            InventoryReceiptService inventoryReceiptService = InventoryReceiptService.getInstance();
            if(productDetails.isEmpty()) {
                AlertUtils.alert("Phiếu nhập này chưa có sản phẩm nào, vui lòng chọn ít nhất 1 sản phẩm.", "WARNING", "Cảnh báo", "Thiếu thông tin");
                return;
            }
            try {
                inventoryReceiptService.solveImportReceipt(importReceiptModel, productDetails, oldImportReceiptModelTable == null ? importReceiptModel.getCreateAt() : oldImportReceiptModelTable.getCreateAt(), productDetailsToDelete);
                String messageAlert = importReceiptModel.getId() == -1 ? "Thêm mới phiếu nhập thành công." : "Cập nhật phiếu nhập thành công.";
                AlertUtils.alert(messageAlert, "INFORMATION", "Thành công", "Thành công");
//                }

                ImportReceiptScreen importReceiptScreen = new ImportReceiptScreen();
                System.out.println("Select year: " + ConstVariableUtils.selectYear.getValue());
                importReceiptScreen.showTable(LocalDateTime.now().getYear());
                ScreenNavigator.navigateTo(importReceiptScreen);
            }
            catch (DaoException | StockUnderFlowException exception) {
                AlertUtils.alert(exception.getMessage(), "ERROR", "Lỗi khi thực hiện thao tác với phiếu nhập.", "Lỗi khi thực hiện lưu phiếu nhập.");
                AddOrUpdateImportReceiptScreen refreshScreen = new AddOrUpdateImportReceiptScreen(oldImportReceiptModelTable);
                ScreenNavigator.navigateTo(refreshScreen);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        Button cancelBtn = new Button("Cancel");
        AddCssStyleForBtnUtil.addCssStyleForBtn(cancelBtn);
        cancelBtn.setOnMouseClicked(e -> {
            ImportReceiptScreen importReceiptScreen = new ImportReceiptScreen();
            importReceiptScreen.showTable(Integer.parseInt(ConstVariableUtils.selectYear.getValue()));
            ScreenNavigator.navigateTo(importReceiptScreen);
        });

        actionRow.getChildren().addAll(saveBtn, cancelBtn);
        actionRow.setStyle("-fx-padding: 5; -fx-background-color: #e1f0f7; -fx-border-color: #c1dfee; -fx-border-width: 1px;");

        // create total price
        HBox totalPriceRow = new HBox(10);
        totalPriceRow.setStyle("-fx-padding: 5; -fx-background-color: #e1f0f7; -fx-border-color: #c1dfee; -fx-border-width: 1px; ");
        Label totalPriceLabelTitle = new Label("Tổng cộng: ");
        Label totalQuantityLabelTitle = new Label("Số lượng thực: ");
        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setPrefHeight(20);
        totalPriceRow.getChildren().addAll(totalPriceLabelTitle, totalPriceLabel, separator, totalQuantityLabelTitle, totalQuantityLabel);
        styleLabel(totalPriceLabelTitle);
        styleLabel(totalPriceLabel);
        styleLabel(totalQuantityLabelTitle);
        styleLabel(totalQuantityLabel);

        box.getChildren().addAll(totalPriceRow, actionRow);
        return box;
    }

    private void addProductToTableProductOfReceipt(ProductModel product, int actualQuantity, int plannedQuantity, long unitPrice, double vat) {
        ImportReceiptDetailModelTable productExists = productDetails.stream()
                        .filter(p -> (p.getProductId().equals(product.getId()) && p.getUnitPrice() == unitPrice))
                        .findFirst()
                        .orElse(null);
        if(actualQuantity < 0 || plannedQuantity < 0) {
            AlertUtils.alert("Số lượng nhập không được phép nhỏ hơn 0, vui lòng nhập lại.", "WARNING", "Cảnh báo", "Nhập dữ liệu sai");
            return;
        }
//        if(unitPrice < 0) {
//            AlertUtils.alert("Đơn giá không được phép nhỏ hơn 0, vui lòng nhập lại.", "WARNING", "Cảnh báo", "Nhập dữ liệu sai");
//            return;
//        }
//        double unitAfterVat = unitPrice * (vat / 100.0);

        double unitPriceAfterVAT = unitPrice + (unitPrice * vat / 100);
        double currentTotalPrice = actualQuantity * unitPriceAfterVAT;
        totalPriceOfReceipt += currentTotalPrice;
        totalPriceLabel.setText(FormatMoney.format(totalPriceOfReceipt));
        int changeQuantityByProduct = changeQuantityByProductMap.getOrDefault(product.getId(), 0);
        double changeTotalPriceByProduct = changeTotalPriceByProductMap.getOrDefault(product.getId(), 0.0);
        if(productExists != null) {
            int actualQuantityCurrent = productExists.getActualQuantity() + actualQuantity;
            double totalPriceCurrent = productExists.getTotalPrice() + currentTotalPrice;
            productExists.setActualQuantity(actualQuantityCurrent);
            productExists.setTotalPrice(totalPriceCurrent);
            productExists.setTotalPriceFormat(FormatMoney.format(totalPriceCurrent));
            if(productExists.getId() != null) {
                changeIdsOfReceiptDetails.add(productExists.getId());
                changeQuantityByProductMap.put(productExists.getProductId(), changeQuantityByProduct + actualQuantity);
                changeTotalPriceByProductMap.put(productExists.getProductId(), changeTotalPriceByProduct + currentTotalPrice);
            }
        }
        else {
            ImportReceiptDetailModelTable importReceiptDetailModelTable = new ImportReceiptDetailModelTable(
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
                    product.getCode(),
                    vat,
                    FormatMoney.format(unitPriceAfterVAT)
            );
            importReceiptDetailModelTable.setIsNew(true);
            productDetails.add(importReceiptDetailModelTable);
            changeQuantityByProductMap.put(product.getId(), changeQuantityByProduct + actualQuantity);
            changeTotalPriceByProductMap.put(product.getId(), changeTotalPriceByProduct + currentTotalPrice);
        }
        int totalQuantity = Integer.parseInt(totalQuantityLabel.getText()) + actualQuantity;
        totalQuantityLabel.setText(totalQuantity + "");
        productTable.refresh();
    }

}
