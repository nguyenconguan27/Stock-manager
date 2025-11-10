package com.manager.stock.manager_stock.screen.transaction;

import com.browniebytes.javafx.control.DateTimePicker;
import com.manager.stock.manager_stock.exception.CanNotFoundException;
import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.exception.StockUnderFlowException;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ExportReceiptDetailModelTableMapper;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ExportReceiptModelTableMapper;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptDetailModelMapper;
import com.manager.stock.manager_stock.mapper.viewModelMapper.ImportReceiptModelMapper;
import com.manager.stock.manager_stock.model.*;
import com.manager.stock.manager_stock.model.dto.ExportPriceIdAndPrice;
import com.manager.stock.manager_stock.model.tableData.ExportReceiptDetailModelTable;
import com.manager.stock.manager_stock.model.tableData.ExportReceiptModelTable;
import com.manager.stock.manager_stock.model.tableData.ImportReceiptDetailModelTable;
import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.screen.transaction.presenter.ExportReceiptPresenter;
import com.manager.stock.manager_stock.screen.transaction.presenter.ImportReceiptPresenter;
import com.manager.stock.manager_stock.utils.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Trọng Hướng
 */
public class AddOrUpdateExportReceiptScreen extends BaseAddOrUpdateReceiptScreen<ExportReceiptModelTable, ExportReceiptDetailModelTable> {
    private TextField tfReceiver, tfReceiveAddress, tfReason;

    public AddOrUpdateExportReceiptScreen(ExportReceiptModelTable exportReceiptModelTable) {
        super(exportReceiptModelTable);
    }

    @Override
    protected VBox createFormAddNew(ExportReceiptModelTable model) {
        // === Form bên trái ===
        GridPane leftForm = new GridPane();
        leftForm.setHgap(10);
        leftForm.setVgap(10);
        leftForm.setPadding(new Insets(15));

        leftForm.add(new Label("Ngày tạo *"), 0, 0);
        dateTimePicker = new DateTimePicker(LocalDateTime.now(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        leftForm.add(dateTimePicker, 1, 0);

        leftForm.add(new Label("Mã hóa đơn *"), 0, 1);
        tfInvoiceNumber = new TextField();
        leftForm.add(tfInvoiceNumber, 1, 1);

        leftForm.add(new Label("Người nhận *"), 0, 2);
        tfReceiver = new TextField();
        leftForm.add(tfReceiver, 1, 2);

        // === Form bên phải ===
        GridPane rightForm = new GridPane();
        rightForm.setHgap(10);
        rightForm.setVgap(10);
        rightForm.setPadding(new Insets(15));

        rightForm.add(new Label("Địa chỉ nhận *"), 0, 0);
        tfReceiveAddress = new TextField();
        rightForm.add(tfReceiveAddress, 1, 0);

        rightForm.add(new Label("Lý do xuất *"), 0, 1);
        tfReason = new TextField();
        rightForm.add(tfReason, 1, 1);

        rightForm.add(new Label("Kho xuất *"), 0, 2);
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
            tfReceiver.setText(model.getReceiver() != null ? model.getReceiver() : "");
            tfReceiveAddress.setText(model.getReceiveAddress() != null ? model.getReceiveAddress() : "");
            tfReason.setText(model.getReason() != null ? model.getReason() : "");
            tfWareHouse.setText(model.getWareHouse() != null ? model.getWareHouse() : "");

            // Lấy danh sách receiptDetail
            totalPriceOfReceipt = model.getTotalPrice();
            totalPriceLabel.setText(FormatMoney.format(totalPriceOfReceipt));
            ExportReceiptPresenter exportReceiptPresenter = ExportReceiptPresenter.getInstance();
            List<ExportReceiptDetailModel> exportReceiptDetailModels = exportReceiptPresenter.findAllExportReceiptDetailByExportReceipt(model.getId());
            List<ExportReceiptDetailModelTable> importReceiptDetailModelTablesByReceipt = GenericConverterBetweenModelAndTableData.convertToList(exportReceiptDetailModels
                    , ExportReceiptDetailModelTableMapper.INSTANCE::toViewModel);
            productDetails.setAll(importReceiptDetailModelTablesByReceipt);

            // tính tổng số lượng
            int totalQuantity = exportReceiptDetailModels.stream().mapToInt(ExportReceiptDetailModel::getActualQuantity)
                    .sum();
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

        Label lbUnitPrice = new Label("Đơn giá *");
        TextField tfUnitPrice = new TextField();
        tfUnitPrice.setPrefWidth(100);
        tfUnitPrice.setEditable(false);
        VBox unitPriceCol = new VBox(5, lbUnitPrice, tfUnitPrice);

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

        HBox productInputs = new HBox(20, productCol, plannedQtyCol, actualQtyCol, unitPriceCol, inventoryCol);
        productInputs.setAlignment(Pos.CENTER_LEFT);

        HBox productBox = new HBox(35, productInputs, spacer, buttonBox);
        productBox.setPadding(new javafx.geometry.Insets(0, 15, 0, 15));
        productBox.setAlignment(Pos.CENTER_LEFT);

        ExportReceiptPresenter exportReceiptPresenter = ExportReceiptPresenter.getInstance();
        List<ProductModel> products = exportReceiptPresenter.loadAllProduct();
        allProducts.setAll(products);
        ProductAutoComplete ac = new ProductAutoComplete(tfProduct, allProducts);
        AtomicReference<ProductModel> selected = new AtomicReference<>();
        ac.valueProperty().addListener((obs, oldP, newP) -> {
            if(newP != null) {
                System.out.println("Chọn sản phẩm: " + newP.getCode());
                LocalDateTime exportDate = dateTimePicker.dateTimeProperty().get();
                if(exportDate == null) {
                    AlertUtils.alert("Vui lòng chọn ngày xuất trước khi chọn sản phẩm.", "WARNING", "Cảnh báo", "Cảnh báo");
                    return;
                }
                ExportPriceIdAndPrice ep = exportReceiptPresenter.findExportPriceIdAndPriceByProductAndLastTime(newP.getId(), exportDate);
                if (ep.exportPriceId() == -1) {
                    AlertUtils.alert("Sản phẩm này không có đơn giá, vui lòng nhập đơn giá cho sản phẩm.", "WARNING", "Cảnh báo", "Không có đơn giá.");
                    tfUnitPrice.clear();
                    tfUnitPrice.setUserData(null);
                    return;
                }

                tfUnitPrice.setText(String.valueOf(ep.price()));
                tfUnitPrice.setUserData(ep.exportPriceId());

                LocalDateTime createAtStr = dateTimePicker.dateTimeProperty().get();
                int academicYear = createAtStr.getYear();
                int quantityInStock = exportReceiptPresenter.findQuantityInStockByProductIdAndAcademicYear(newP.getId(), academicYear);
                tfInventory.setText(String.valueOf(quantityInStock));
                selected.set(newP);
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
                double unitPrice = Double.parseDouble(tfUnitPrice.getText());
                long exportPriceId = Long.parseLong(tfUnitPrice.getUserData().toString());
                System.out.println(selectedProduct);
                addProductToTableProductOfReceipt(selectedProduct, actualQuantity, plannedQuantity, exportPriceId, unitPrice);
                tfActualQty.clear();
                tfPlannedQty.clear();
                tfUnitPrice.clear();
                tfProduct.clear();
                tfInventory.clear();
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                AlertUtils.alert("Vui lòng nhập đúng định dạng số cho số lượng và đơn giá.", "WARNING", "Cảnh báo", "Lỗi định dạng");
            }
        });

        return root;
    }

    @Override
    protected VBox createTableItemDetailByReceipt(ExportReceiptModelTable receiptModelTable) {
        TableView<ExportReceiptDetailModelTable> productTable = new TableView<>();
        productTable.setEditable(true);
        TableColumn<ExportReceiptDetailModelTable, Number> colIndex = new TableColumn<>("STT");
        colIndex.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(productTable.getItems().indexOf(cellData.getValue()) + 1)
        );
        TableColumn<ExportReceiptDetailModelTable, String> colProductId = CreateColumnTableUtil.createColumn("Mã SP", ExportReceiptDetailModelTable::productCodeProperty);
        TableColumn<ExportReceiptDetailModelTable, String> colProductName = CreateColumnTableUtil.createColumn("Tên SP", ExportReceiptDetailModelTable::productNameProperty);

        TableColumn<ExportReceiptDetailModelTable, Number> colPlannedQty = new TableColumn<>("SL theo CT");
        colPlannedQty.setCellValueFactory(data -> data.getValue().plannedQuantityProperty());
        colPlannedQty.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        colPlannedQty.setOnEditCommit(event -> {
            ExportReceiptDetailModelTable row = event.getRowValue();
            row.plannedQuantityProperty().set(event.getNewValue().intValue());
            int newValue = event.getNewValue().intValue();

            row.setPlannedQuantity(newValue);
        });

        TableColumn<ExportReceiptDetailModelTable, Number> colActualQty = new TableColumn<>("SL thực tế");
        colActualQty.setCellValueFactory(data -> data.getValue().actualQuantityProperty());
        colActualQty.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
        colActualQty.setOnEditCommit(event -> {
            ExportReceiptDetailModelTable row = event.getRowValue();

            int newValue = event.getNewValue().intValue();
            int oldValue = row.getActualQuantity();
            int changeQuantity = newValue - oldValue;
            double changeTotalPrice = changeQuantity * row.getOriginalUnitPrice();

            if(row.getId() != null) {
                int changeQuantityByProduct = changeQuantityByProductMap.getOrDefault(row.getProductId(), 0);
                changeQuantityByProductMap.put(row.getProductId(), changeQuantity + changeQuantityByProduct);
                double changeTotalPriceByProduct = changeTotalPriceByProductMap.getOrDefault(row.getProductId(), 0.0);
                changeTotalPriceByProductMap.put(row.getProductId(), changeTotalPriceByProduct + changeTotalPrice);
                changeIdsOfReceiptDetails.add(row.getId());
            }
            double newTotal = newValue * row.getOriginalUnitPrice();
            row.setTotalPrice(newTotal);
            row.setDisplayTotalPriceFormat(FormatMoney.format(newTotal));
            row.actualQuantityProperty().set(event.getNewValue().intValue());
            totalPriceOfReceipt += changeQuantity * row.getDisplayUnitPrice();
            System.out.println("Số lượng thay đổi: " + changeQuantity);
            int totalQuantity = Integer.parseInt(totalQuantityLabel.getText()) + changeQuantity;
            totalQuantityLabel.setText(totalQuantity + "");
            totalPriceLabel.setText(FormatMoney.format(totalPriceOfReceipt));
            productTable.refresh();
        });

        TableColumn<ExportReceiptDetailModelTable, String> colUnitPrice = new TableColumn<>("Đơn giá");
        colUnitPrice.setCellValueFactory(data -> data.getValue().displayUnitPriceFormatProperty());

        TableColumn<ExportReceiptDetailModelTable, String> colTotalPrice = new TableColumn<>("Thành tiền");
        colTotalPrice.setCellValueFactory(data -> data.getValue().displayTotalPriceFormatProperty());
        TableColumn<ExportReceiptDetailModelTable, Void> colAction = new TableColumn<>("Thao tác");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("🗑");
            private final HBox pane = new HBox(5, btnDelete);
            {
                btnDelete.setStyle("-fx-background-color: #f08080; -fx-cursor: hand;");
                pane.setAlignment(Pos.CENTER);

                btnDelete.setOnAction(event -> {
                    ExportReceiptDetailModelTable item = getTableView().getItems().get(getIndex());
                    totalPriceOfReceipt -= item.getTotalPrice();
                    totalPriceLabel.setText(FormatMoney.format(totalPriceOfReceipt));
                    int totalQuantity = Integer.parseInt(totalQuantityLabel.getText()) - item.getActualQuantity();
                    totalQuantityLabel.setText(totalQuantity + "");
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
                colIndex, colProductId, colProductName, colPlannedQty, colActualQty,
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
            protected void updateItem(ExportReceiptDetailModelTable item, boolean empty) {
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
            try {
                ExportReceiptPresenter presenter = ExportReceiptPresenter.getInstance();
                if(dateTimePicker.dateTimeProperty() == null || dateTimePicker.dateTimeProperty().get() == null) {
                    AlertUtils.alert("Vui lòng chọn ngày nhập hàng.", "WARNING", "Cảnh báo", "Thiếu thông tin");
                    return;
                }
                String createAtStr = dateTimePicker.dateTimeProperty().get().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
                String invoiceNumber = tfInvoiceNumber.getText().trim();
                String receiver = tfReceiver.getText().trim();
                String receiveAddress = tfReceiveAddress.getText().trim();
                String reason = tfReason.getText().trim();
                String wareHouseName = tfWareHouse.getText().trim();
                if(invoiceNumber.isEmpty()) {
                    AlertUtils.alert("Vui lòng nhập số hóa đơn.", "WARNING", "Cảnh báo", "Thiếu thông tin");
                    return;
                }
                ExportReceiptModel exportReceiptModel = new ExportReceiptModel(
                        receiptModelTable != null ? receiptModelTable.getId() : -1,
                        invoiceNumber,
                        createAtStr,
                        receiver,
                        receiveAddress,
                        reason,
                        wareHouseName,
                        totalPriceOfReceipt,
                        ""
                );
                if(productDetails.isEmpty()) {
                    AlertUtils.alert("Phiếu nhập này chưa có sản phẩm nào, vui lòng chọn ít nhất 1 sản phẩm.", "WARNING", "Cảnh báo", "Thiếu thông tin");
                    return;
                }
                // thêm mới hóa đơn nhập
                if(receiptModelTable == null) {
                    // changeQuantityByProductMap: số lượng sản phẩm thay đổi
                    // changeTotalPriceByProductMap: tổng tiền thay đổi
                    presenter.save(exportReceiptModel, productDetails, changeQuantityByProductMap, changeTotalPriceByProductMap);
                    AlertUtils.alert("Thêm mới phiếu xuất thành công.", "INFORMATION", "Thành công", "Thành công");
                }
                // Cập nhật hóa đơn xuất
                else {
                    ExportReceiptModel oldExportReceiptModel = ExportReceiptModelTableMapper.INSTANCE.fromViewModelToModel(receiptModelTable);
                    presenter.updateExportReceipt(exportReceiptModel, oldExportReceiptModel, productDetails, changeQuantityByProductMap, changeTotalPriceByProductMap);
                    AlertUtils.alert("Cập nhật phiếu nhập thành công.", "INFORMATION", "Thành công", "Thành công");
                }
                ExportReceiptScreen exportReceiptScreen = new ExportReceiptScreen();
                exportReceiptScreen.showTable();
                ScreenNavigator.navigateTo(exportReceiptScreen);
            }
            catch (DaoException | StockUnderFlowException | CanNotFoundException exception) {
                AlertUtils.alert(exception.getMessage(), "ERROR", "Lỗi khi thực hiện thao tác với phiếu nhập.", "Lỗi khi thực hiện lưu phiếu nhập.");
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button cancelBtn = new Button("Cancel");
        AddCssStyleForBtnUtil.addCssStyleForBtn(cancelBtn);
        cancelBtn.setOnMouseClicked(e -> {
            ExportReceiptScreen exportReceiptScreen = new ExportReceiptScreen();
            exportReceiptScreen.showTable();
            ScreenNavigator.navigateTo(exportReceiptScreen);
        });

        actionRow.getChildren().addAll(saveBtn, cancelBtn);
        actionRow.setStyle("-fx-padding: 5; -fx-background-color: #e1f0f7; -fx-border-color: #c1dfee; -fx-border-width: 1px;");

        // create total price
        HBox totalPriceRow = new HBox(10);
        totalPriceRow.setStyle("-fx-padding: 5; -fx-background-color: #e1f0f7; -fx-border-color: #c1dfee; -fx-border-width: 1px; ");
        Label totalPriceLabelTitle = new Label("Tổng cộng: ");
        Label totalQuantityLabelTitle = new Label(" SL thực: ");
        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setPrefHeight(20);
//        separator.setStyle("-fx-background-color: #c1dfee;");
        totalPriceRow.getChildren().addAll(totalPriceLabelTitle, totalPriceLabel, separator, totalQuantityLabelTitle, totalQuantityLabel);
        styleLabel(totalPriceLabelTitle);
        styleLabel(totalQuantityLabelTitle);
        styleLabel(totalPriceLabel);
        styleLabel(totalQuantityLabel);

        box.getChildren().addAll(totalPriceRow, actionRow);
        return box;
    }

    private void addProductToTableProductOfReceipt(ProductModel product, int actualQuantity, int plannedQuantity, long exportPriceId, double unitPrice) {
        ExportReceiptDetailModelTable productExists = productDetails.stream()
                .filter(p -> (p.getProductId().equals(product.getId())))
                .findFirst()
                .orElse(null);
        if(actualQuantity < 0 || plannedQuantity < 0) {
            AlertUtils.alert("Số lượng nhập không được phép nhỏ hơn 0, vui lòng nhập lại.", "WARNING", "Cảnh báo", "Nhập dữ liệu sai");
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
            productExists.setDisplayTotalPriceFormat(FormatMoney.format(totalPriceCurrent));
            changeIdsOfReceiptDetails.add(productExists.getId());
            //
            int changeQuantityByProduct = changeQuantityByProductMap.getOrDefault(productExists.getProductId(), 0);
            changeQuantityByProductMap.put(productExists.getProductId(), changeQuantityByProduct + actualQuantity);

            double changeTotalPriceByProduct = changeTotalPriceByProductMap.getOrDefault(productExists.getProductId(), 0.0);
            changeTotalPriceByProductMap.put(productExists.getProductId(), changeTotalPriceByProduct + currentTotalPrice);
        }
        // thêm mới
        else {
            try {
                productDetails.add(new ExportReceiptDetailModelTable(
                        -1,
                        0,
                        product.getId(),
                        plannedQuantity,
                        actualQuantity,
                        currentTotalPrice,
                        unitPrice,
                        product.getName(),
                        FormatMoney.format(unitPrice),
                        FormatMoney.format(currentTotalPrice),
                        product.getCode(),
                        exportPriceId,
                        unitPrice
                ));
                changeQuantityByProductMap.put(product.getId(), actualQuantity);
                changeTotalPriceByProductMap.put(product.getId(), currentTotalPrice);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        int totalQuantity = Integer.parseInt(totalQuantityLabel.getText()) + actualQuantity;
        totalQuantityLabel.setText(totalQuantity + "");
        productTable.refresh();
    }
}
