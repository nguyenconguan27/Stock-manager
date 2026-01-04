package com.manager.stock.manager_stock.screen.product.productDetail;

import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.interfaceActionHandler.TopBarActionHandler;
import com.manager.stock.manager_stock.model.ProductGroup;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.model.dto.ExportPriceAndProductCodeAndProductName;
import com.manager.stock.manager_stock.model.dto.ProductIdAndCodeAndNameAndQuantityInStock;
import com.manager.stock.manager_stock.reportservice.ExportAll;
import com.manager.stock.manager_stock.screen.ScreenNavigator;
import com.manager.stock.manager_stock.screen.product.productList.ProductScreen;
import com.manager.stock.manager_stock.screen.productGroup.ProductGroupPresenter;
import com.manager.stock.manager_stock.utils.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class ProductDetailScreen extends VBox {

    private final Logger logger = LoggerFactory.getLogger(ProductDetailScreen.class);
    final ProductDetailPresenter productDetailPresenter;
    final ProductGroupPresenter productGroupPresenter;
    NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);
    ObservableList<ProductGroup> productGroupData = FXCollections.observableArrayList();
    ProductModel productData;
    private ComboBox<ProductGroup> comboBox;
    private ProductGroup productGroupSelected;
    private ObservableList<ProductModel> productDatas;
    private VBox statisticWrapper = new VBox();

    TextField tfId, tfQuantity, tfName, tfUnit, tfUnitPrice, tfTotal;
    Button btnSave, btnCancel;
    private int selectedYear;

    private void initProductGroup() {
        comboBox = new ComboBox<>();
        //init data lần đầu hiển thị
        productGroupData.addListener((ListChangeListener<? super ProductGroup>) change -> {
            if (!productGroupData.isEmpty() && comboBox.getSelectionModel().isEmpty()) {
                comboBox.getSelectionModel().selectFirst();
            }
        });
        comboBox.setItems(productGroupData);

        // hiển thị drop down
        comboBox.setCellFactory(cb -> new ListCell<>(){
            @Override
            protected void updateItem(ProductGroup item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });
        // hiển thị action được chọn
        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ProductGroup item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });
        // action khi chọn item
        comboBox.setOnAction(event -> {
            ProductGroup selected = comboBox.getSelectionModel().getSelectedItem();
        });
    }

    public VBox detailForm() {
        // === Style chung cho input fields ===
        String inputStyle = ""
                + "-fx-background-color: white;"
                + "-fx-border-color: #c1dfee;"
                + "-fx-border-radius: 4;"
                + "-fx-background-radius: 4;"
                + "-fx-padding: 6 8;"
                + "-fx-font-size: 13px;"
                + "-fx-pref-width: 200px;";

        String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #33536d;";

        // === Nhóm sản phẩm ===
        comboBox = new ComboBox<>();
        comboBox.setPrefWidth(300);
        comboBox.setPrefHeight(20);
        comboBox.setMaxHeight(100);
        comboBox.setStyle(inputStyle);

        productGroupData.addListener((ListChangeListener<? super ProductGroup>) change -> {
            if (!productGroupData.isEmpty()) {
                comboBox.getSelectionModel().selectFirst();
                productGroupSelected = comboBox.getSelectionModel().getSelectedItem();

                // Tạo thống kê ban đầu khi có group
                ScrollPane newStatisticNode = createStatisticProductByGroup();
                VBox newStatisticWrapper = new VBox(newStatisticNode);
                newStatisticWrapper.setStyle("-fx-background-color: #e6f4fb;");
                VBox.setVgrow(newStatisticWrapper, Priority.ALWAYS);
                if (statisticWrapper == null) {
                    // Nếu chưa add statisticNode trước đó, thì add luôn
                    this.getChildren().add(statisticWrapper);
                } else {
                    int index = this.getChildren().indexOf(statisticWrapper);
                    if (index != -1) {
                        this.getChildren().set(index, newStatisticWrapper);
                    } else {
                        this.getChildren().add(newStatisticWrapper);
                    }
                }
                statisticWrapper = newStatisticWrapper;
            }
        });

        comboBox.setItems(productGroupData);

        comboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(ProductGroup item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(ProductGroup item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getName());
            }
        });

        comboBox.setOnAction(event -> {
            productGroupSelected = comboBox.getSelectionModel().getSelectedItem();
            ScrollPane newStatisticNode = createStatisticProductByGroup();
            VBox newStatisticWrapper = new VBox(newStatisticNode);
            newStatisticWrapper.setStyle("-fx-background-color: #e6f4fb;");
            VBox.setVgrow(newStatisticWrapper, Priority.ALWAYS);
            int indexOldNode = this.getChildren().indexOf(statisticWrapper);
            if(indexOldNode != -1){
                this.getChildren().set(indexOldNode, newStatisticWrapper);
                statisticWrapper = newStatisticWrapper;
            }
        });

        Label groupLabel = new Label("Chọn nhóm sản phẩm *");
        groupLabel.setStyle(labelStyle);
        VBox comboBoxGroup = new VBox(5, groupLabel, comboBox);
        comboBoxGroup.setPadding(new Insets(10, 15, 0, 15));

        // === Form bên trái ===
        GridPane leftForm = new GridPane();
        leftForm.setHgap(10);
        leftForm.setVgap(10);
//        leftForm.setPadding(new Insets(15));

        Label labelId = new Label("Mã vật tư *");
        labelId.setStyle(labelStyle);
        tfId = new TextField();
        tfId.setStyle(inputStyle);
        leftForm.add(labelId, 0, 0);
        leftForm.add(tfId, 1, 0);

        Label labelName = new Label("Tên vật tư *");
        labelName.setStyle(labelStyle);
        tfName = new TextField();
        tfName.setStyle(inputStyle);
        leftForm.add(labelName, 0, 1);
        leftForm.add(tfName, 1, 1);

        Label labelQuantity = new Label("Số lượng *");
        labelQuantity.setStyle(labelStyle);
        tfQuantity = new TextField("0");
        tfQuantity.setStyle(inputStyle);
        tfQuantity.setDisable(true);
        leftForm.add(labelQuantity, 0, 2);
        leftForm.add(tfQuantity, 1, 2);

        // === Form bên phải ===
        GridPane rightForm = new GridPane();
        rightForm.setHgap(10);
        rightForm.setVgap(10);
        rightForm.setPadding(new Insets(15));

        Label labelUnit = new Label("Đơn vị tính *");
        labelUnit.setStyle(labelStyle);
        tfUnit = new TextField();
        tfUnit.setStyle(inputStyle);
        rightForm.add(labelUnit, 0, 0);
        rightForm.add(tfUnit, 1, 0);

        Label labelUnitPrice = new Label("Đơn giá xuất *");
        labelUnitPrice.setStyle(labelStyle);
        tfUnitPrice = new TextField("0");
        tfUnitPrice.setDisable(true);
        tfUnitPrice.setStyle(inputStyle);
        rightForm.add(labelUnitPrice, 0, 1);
        rightForm.add(tfUnitPrice, 1, 1);

        Label labelTotal = new Label("Thành tiền *");
        labelTotal.setStyle(labelStyle);
        tfTotal = new TextField();
        tfTotal.setDisable(true);
        tfTotal.setStyle(inputStyle);
        tfTotal.setEditable(false);

        tfQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int newQuantity = newValue.isEmpty() ? 0 : Integer.parseInt(newValue.trim());
                double unitPrice = tfUnitPrice.getText().isEmpty() ? 0 : Double.parseDouble(tfUnitPrice.getText().trim());

                double totalPrice = newQuantity * unitPrice;
                tfTotal.setText(FormatMoney.format(totalPrice));
            }
            catch (NumberFormatException e) {
                AlertUtils.alert("Vui lòng nhập số lượng là số nguyên dương", "WARNING",
                        "Cảnh báo", "Cảnh báo");
                tfQuantity.setText("0");
            }
        });

        tfUnitPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double newUnitPrice = newValue.isEmpty() ? 0 : Double.parseDouble(newValue
                        .replaceAll("[^\\d,.-]", "").replace(".", "").replace(",", "."));
                int quantity = tfQuantity.getText().isEmpty() ? 0 : Integer.parseInt(tfQuantity.getText().trim());

                double totalPrice = quantity * newUnitPrice;
                tfTotal.setText(FormatMoney.format(totalPrice));
                tfUnitPrice.setText(FormatMoney.format(newUnitPrice));
            }
            catch (NumberFormatException e) {
                AlertUtils.alert("Vui lòng nhập đơn giá là số", "WARNING",
                        "Cảnh báo", "Cảnh báo");
                tfUnitPrice.setText("0");
            }
        });

        tfUnitPrice.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                double unit = parseViCurrency(tfUnitPrice.getText());
                tfUnitPrice.setText(FormatMoney.format(unit));
            }
        });

        rightForm.add(labelTotal, 0, 2);
        rightForm.add(tfTotal, 1, 2);

        // === Gộp form trái phải ===
        HBox formColumns = new HBox(30, leftForm, rightForm);
        formColumns.setPadding(new Insets(10));
        HBox.setHgrow(leftForm, Priority.ALWAYS);
        HBox.setHgrow(rightForm, Priority.ALWAYS);

        // === Giao diện tổng thể ===
        VBox root = new VBox(20, comboBoxGroup, formColumns);
        root.setStyle("-fx-background-color: #e1f0f7; -fx-border-width: 0");
        root.setPadding(new Insets(10));
        return root;
    }

    public void initButton() {
        btnSave = new Button("Save");
        AddCssStyleForBtnUtil.addCssStyleForBtn(btnSave);
        btnSave.setOnAction(event -> {
            long groupId = comboBox.getSelectionModel().getSelectedItem().getId();
            ProductModel newProduct = new ProductModel();
            newProduct.setCode(tfId.getText());
            newProduct.setName(tfName.getText());
            newProduct.setQuantity(Integer.parseInt(tfQuantity.getText()));
            newProduct.setUnit(tfUnit.getText());
            newProduct.setUnitPrice(FormatMoney.parseMoneyToInt(tfUnitPrice.getText()));
            newProduct.setQuantity(Integer.parseInt(tfQuantity.getText()));
            newProduct.setGroupId(comboBox.getSelectionModel().getSelectedItem().getId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            ProductModel productByCode = productDetailPresenter.getByCode(newProduct.getCode());
            if(productData == null) {
                newProduct.setId(System.currentTimeMillis());
                if(productByCode != null) {
                    alert.setContentText("Mã vật tư trùng!");
                }
                else {
                    productDetailPresenter.add(newProduct, groupId);
                    alert.setContentText("Thêm vật tư thành công!");
                }
            }
            else {
                newProduct.setId(productData.getId());
                if(productByCode == null) {
                    productDetailPresenter.update(newProduct, groupId, true);
                }
                else {
                    productDetailPresenter.update(newProduct, groupId, false);
                }
                alert.setContentText("Sửa vật tư thành công!");
            }
            updateData(newProduct);
            alert.showAndWait();
        });

        btnCancel = new Button("Cancel");
        AddCssStyleForBtnUtil.addCssStyleForBtn(btnCancel);
        btnCancel.setOnAction(event -> {
            ProductScreen productScreen = new ProductScreen();
            productScreen.showProducts();
            ScreenNavigator.navigateTo(productScreen);
        });
    }

    private ScrollPane createStatisticProductByGroup() {
        VBox root = new VBox();
        root.setPadding(new Insets(20));
        root.setSpacing(20);

        Label title = new Label("📊 Thống kê nhanh theo nhóm sản phẩm");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #003366;");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);

        List<ProductModel> groupProducts = productDatas.stream()
                .filter(p -> p.getGroupId() == (productGroupSelected != null ? productGroupSelected.getId() : -1))
                .collect(Collectors.toList());
        int totalProducts = groupProducts.size();

        ExportPriceAndProductCodeAndProductName productHaveMaxExportPrice;
        ExportPriceAndProductCodeAndProductName productHaveMinExportPrice;
        List<ProductIdAndCodeAndNameAndQuantityInStock> lowStockProducts;
        List<ProductIdAndCodeAndNameAndQuantityInStock> maxQuantityInStock;

        try {
            productHaveMaxExportPrice = productGroupSelected == null ? new ExportPriceAndProductCodeAndProductName(0.0, "UNKNOWN", "UNKNOWN") : productDetailPresenter.findMaxExportPriceByGroup(productGroupSelected.getId());
            productHaveMinExportPrice = productGroupSelected == null ? new ExportPriceAndProductCodeAndProductName(0.0, "UNKNOWN", "UNKNOWN") : productDetailPresenter.findMinExportPriceByGroup(productGroupSelected.getId());
            lowStockProducts = productGroupSelected == null ? new ArrayList<>() : productDetailPresenter.findListProductHaveMinQuantityInStockByGroup(productGroupSelected.getId());
            maxQuantityInStock = productGroupSelected == null ? new ArrayList<>() : productDetailPresenter.findListProductHaveMaxQuantityInStockByGroup(productGroupSelected.getId());
        } catch (DaoException e) {
            AlertUtils.alert(e.getMessage(), "ERROR", "Lỗi", "Lỗi khi thống kê");
            return new ScrollPane();
        }

        VBox leftColumn = new VBox(15);
        leftColumn.getChildren().addAll(
                createStyledInfo("• Nhóm sản phẩm hiện tại: ", productGroupSelected == null ? "UNKNOWN" : productGroupSelected.getName()),
                createStyledInfo("• Tổng số sản phẩm: ", totalProducts + " sản phẩm"),
                createStyledInfo(
                        "• Giá cao nhất: ",
                        (productHaveMaxExportPrice != null)
                                ? String.format("%s - %s - %s",
                                productHaveMaxExportPrice.productCode(),
                                productHaveMaxExportPrice.productName(),
                                FormatMoney.format(productHaveMaxExportPrice.exportPrice()))
                                : "UNKNOWN - UNKNOWN - 0đ"
                ),
                createStyledInfo(
                        "• Giá thấp nhất: ",
                        (productHaveMinExportPrice != null)
                                ? String.format("%s - %s - %s",
                                productHaveMinExportPrice.productCode(),
                                productHaveMinExportPrice.productName(),
                                FormatMoney.format(productHaveMinExportPrice.exportPrice()))
                                : "UNKNOWN - UNKNOWN - 0đ"
                )
        );

        VBox rightColumn = new VBox(15);
        Label lowStockTitle = createSubTitle("• Danh sách 5 sản phẩm sắp hết hàng:");
        VBox lowStockBox = new VBox(5);
        if (lowStockProducts == null || lowStockProducts.isEmpty()) {
            lowStockBox.getChildren().add(createStyledLabel("   - Không có sản phẩm nào sắp hết hàng."));
        } else {
            for (var p : lowStockProducts) {
                String line = String.format("   - %s: %d cái (tồn kho < 5)", p.productCode(), p.quantityInStock());
                lowStockBox.getChildren().add(createStyledLabel(line));
            }
        }

        Label maxStockTitle = createSubTitle("• Danh sách 5 sản phẩm tồn kho nhiều nhất:");
        VBox maxStockBox = new VBox(5);
        if (maxQuantityInStock == null || maxQuantityInStock.isEmpty()) {
            maxStockBox.getChildren().add(createStyledLabel("   - Không có sản phẩm nào tồn kho nhiều."));
        } else {
            for (var p : maxQuantityInStock) {
                String line = String.format("   - %s: %d cái", p.productCode(), p.quantityInStock());
                maxStockBox.getChildren().add(createStyledLabel(line));
            }
        }

        rightColumn.getChildren().addAll(lowStockTitle, lowStockBox, maxStockTitle, maxStockBox);

        // Cột trái và phải
        HBox columns = new HBox();
        columns.setSpacing(0);
        columns.setAlignment(Pos.TOP_LEFT);

        StackPane leftWrapper = new StackPane(leftColumn);
        leftWrapper.setPadding(new Insets(0, 20, 0, 0));
        leftWrapper.setStyle("-fx-border-color: transparent #c0e0eb transparent transparent; -fx-border-width: 0 1 0 0;");
        HBox.setHgrow(leftWrapper, Priority.ALWAYS);

        StackPane rightWrapper = new StackPane(rightColumn);
        rightWrapper.setPadding(new Insets(0, 0, 0, 20));
        HBox.setHgrow(rightWrapper, Priority.ALWAYS);

        columns.getChildren().addAll(leftWrapper, rightWrapper);

        VBox content = new VBox(25, title, columns);
        content.setPadding(new Insets(25));
        content.setStyle(
                "-fx-background-color: #e0f2f7;" +
                        "-fx-border-color: #c0e0eb;" +
                        "-fx-background-radius: 10;"
        );
        VBox.setVgrow(columns, Priority.ALWAYS);
        VBox.setVgrow(content, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-width: 1px 0px 0px 0px; -fx-border-color: #c0e0eb;");
        return scrollPane;
    }

    private TextFlow createStyledInfo(String title, String value) {
        Text titleText = new Text(title);
        titleText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #0a4c8a;");

        Text valueText = new Text(value);
        valueText.setStyle("-fx-font-size: 16px; -fx-fill: black;"); // hoặc màu khác nếu muốn

        return new TextFlow(titleText, valueText);
    }

    private Label createStyledLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 15px; -fx-text-fill: #333333;");
        label.setWrapText(true);
        return label;
    }

    private Label createSubTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0a4c8a;");
        label.setWrapText(true);
        return label;
    }

    public ProductDetailScreen(ObservableList<ProductModel> datas) {
        productDetailPresenter = ProductDetailPresenter.getInstance();
        productGroupPresenter = ProductGroupPresenter.getInstance();
        currencyFormat.setMinimumFractionDigits(0);
        productDatas = datas;
//        initProductGroup();
        initButton();
        VBox formDetail = detailForm();
        HBox topBar = CreateTopBarOfReceiptUtil.createTopBar(new TopBarActionHandler() {
            @Override
            public void onAdd() {

            }

            @Override
            public void onEdit() {

            }

            @Override
            public void onDelete() {

            }

            @Override
            public void onReload() {

            }

            @Override
            public void onPrint() {

            }

            @Override
            public void onExport() {

            }

            @Override
            public void onExportAll() {
                try {
                    File file = ChoosesFolderOutput.choosesFolderFile("Tong_hop");
                    if(file == null) return;
                    String outputPath = file.getAbsolutePath();
                    ExportAll exportService = new ExportAll(selectedYear);
                    exportService.exportTotal(outputPath);
                    // gọi hàm tạo file xlsx
                    AlertUtils.alert("Xuất file thành công:\n" + file.getAbsolutePath(),
                            "INFORMATION", "Thành công", "Xuất dữ liệu");
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertUtils.alert("Có lỗi khi xuất file: " + e.getMessage(),
                            "ERROR", "Lỗi", "Xuất dữ liệu thất bại");
                }
            }

            @Override
            public void onSelectYear(int year) {
                selectedYear = year;
            }
        });
        VBox.setVgrow(this, Priority.ALWAYS);
        this.setStyle("-fx-background-color: #e0f2f7;");
        HBox actionRow = new HBox(10);
        actionRow.getChildren().addAll(btnSave, btnCancel);
        actionRow.setStyle("-fx-padding: 10; -fx-background-color: #e1f0f7;");
        ScrollPane newStatisticNodeInit = createStatisticProductByGroup();
        VBox newStatisticWrapperInit = new VBox(newStatisticNodeInit);
        newStatisticWrapperInit.setStyle("-fx-background-color: #e6f4fb;");
        VBox.setVgrow(newStatisticWrapperInit, Priority.ALWAYS);
        statisticWrapper = newStatisticWrapperInit;
        this.getChildren().addAll(topBar, formDetail, actionRow, statisticWrapper);
    }

    public void showProduct(long pid) {
        List<ProductGroup> productGroups = productGroupPresenter.loadProductGroupData();
        productGroupData.addAll(productGroups);
        if(pid != -1 ) {
            updateData(productDetailPresenter.getProductDetail(pid));
        }
    }

    public void updateData(ProductModel productData) {
        this.productData = productData;
        ProductGroup productGroup = null;
        for(ProductGroup pg: comboBox.getItems()) {
            if(productData.getGroupId() == pg.getId()) {
                productGroup = pg;
                break;
            }
        }
        productGroupSelected = productGroupPresenter.getById(productData.getGroupId());
        comboBox.getSelectionModel().select(productGroup);
        tfId.setText(productData.getCode());
        tfName.setText(productData.getName());
        tfQuantity.setText(productData.getQuantity() + "");
        tfUnit.setText(productData.getUnit());
        tfUnitPrice.setText(productData.getUnitPrice() + "");
        tfTotal.setText(FormatMoney.format(productData.getUnitPrice() * productData.getQuantity())+ "");
    }

    public static double parseViCurrency(String s) {
        if (s == null || s.isBlank()) return 0;
        Locale viVN = new Locale("vi", "VN");
        NumberFormat nf = NumberFormat.getCurrencyInstance(viVN);
        nf.setCurrency(Currency.getInstance("VND")); // VND không có phần thập phân

        // Nhiều hệ thống chèn NBSP trước ký hiệu tiền tệ: "12.345 ₫"
        s = s.replace('\u00A0', ' ').trim();

        try {
            Number n = nf.parse(s);
            return n.doubleValue();
        } catch (ParseException e) {
            // Fallback: loại bỏ mọi ký tự không phải số/dấu âm
            String digits = s.replaceAll("[^\\d-]", "");
            if (digits.isEmpty() || "-".equals(digits)) return 0;
            return Double.parseDouble(digits);
        }
    }

}
