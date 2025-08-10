package com.manager.stock.manager_stock.screen.product.productDetail;

import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.interfaceActionHandler.TopBarActionHandler;
import com.manager.stock.manager_stock.model.ProductGroup;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.model.dto.ExportPriceAndProductCodeAndProductName;
import com.manager.stock.manager_stock.model.dto.ProductIdAndCodeAndNameAndQuantityInStock;
import com.manager.stock.manager_stock.screen.productGroup.ProductGroupPresenter;
import com.manager.stock.manager_stock.utils.AddCssStyleForBtnUtil;
import com.manager.stock.manager_stock.utils.AlertUtils;
import com.manager.stock.manager_stock.utils.CreateTopBarOfReceiptUtil;
import com.manager.stock.manager_stock.utils.FormatMoney;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductDetailScreen extends VBox{

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

    TextField tfId, tfQuantity, tfName, tfUnit, tfUniPrice, tfTotal;
    Button btnSave, btnCancel;

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
                this.getChildren().add(indexOldNode, newStatisticWrapper);
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
        tfQuantity = new TextField();
        tfQuantity.setStyle(inputStyle);
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
        tfUniPrice = new TextField();
        tfUniPrice.setStyle(inputStyle);
        rightForm.add(labelUnitPrice, 0, 1);
        rightForm.add(tfUniPrice, 1, 1);

        Label labelTotal = new Label("Thành tiền *");
        labelTotal.setStyle(labelStyle);
        tfTotal = new TextField();
        tfTotal.setStyle(inputStyle);
        tfTotal.setEditable(false);

        tfQuantity.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int newQuantity = newValue.isEmpty() ? 0 : Integer.parseInt(newValue.trim());
                double unitPrice = tfUniPrice.getText().isEmpty() ? 0 : Double.parseDouble(tfUniPrice.getText().trim());

                double totalPrice = newQuantity * unitPrice;
                tfTotal.setText(FormatMoney.format(totalPrice));
            }
            catch (NumberFormatException e) {
                AlertUtils.alert("Vui lòng nhập số lượng là số nguyên dương", "WARNING",
                        "Cảnh báo", "Cảnh báo");
            }
        });

        tfUniPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double newUnitPrice = newValue.isEmpty() ? 0 : Double.parseDouble(newValue.trim());
                int quantity = tfQuantity.getText().isEmpty() ? 0 : Integer.parseInt(tfQuantity.getText().trim());

                double totalPrice = quantity * newUnitPrice;
                tfTotal.setText(FormatMoney.format(totalPrice));
            }
            catch (NumberFormatException e) {
                AlertUtils.alert("Vui lòng nhập đơn giá là số", "WARNING",
                        "Cảnh báo", "Cảnh báo");
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
            newProduct.setUnitPrice(Integer.parseInt(tfUniPrice.getText()));
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
                    productDetailPresenter.update(newProduct, groupId, false);
                }
                else {
                    productDetailPresenter.update(newProduct, groupId, true);
                }
                alert.setContentText("Sửa vật tư thành công!");
            }
            updateData(newProduct);
            alert.showAndWait();
        });

        btnCancel = new Button("Cancel");
        AddCssStyleForBtnUtil.addCssStyleForBtn(btnCancel);
        btnCancel.setOnAction(event -> {});
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
                .filter(p -> p.getGroupId() == productGroupSelected.getId())
                .collect(Collectors.toList());
        int totalProducts = groupProducts.size();

        ExportPriceAndProductCodeAndProductName productHaveMaxExportPrice;
        ExportPriceAndProductCodeAndProductName productHaveMinExportPrice;
        List<ProductIdAndCodeAndNameAndQuantityInStock> lowStockProducts;
        List<ProductIdAndCodeAndNameAndQuantityInStock> maxQuantityInStock;

        try {
            productHaveMaxExportPrice = productDetailPresenter.findMaxExportPriceByGroup(productGroupSelected.getId());
            productHaveMinExportPrice = productDetailPresenter.findMinExportPriceByGroup(productGroupSelected.getId());
            lowStockProducts = productDetailPresenter.findListProductHaveMinQuantityInStockByGroup(productGroupSelected.getId());
            maxQuantityInStock = productDetailPresenter.findListProductHaveMaxQuantityInStockByGroup(productGroupSelected.getId());
        } catch (DaoException e) {
            AlertUtils.alert(e.getMessage(), "ERROR", "Lỗi", "Lỗi khi thống kê");
            return new ScrollPane(); // tránh lỗi NullPointerException
        }

        VBox leftColumn = new VBox(15);
        leftColumn.getChildren().addAll(
                createStyledInfo("• Nhóm sản phẩm hiện tại: ", productGroupSelected.getName()),
                createStyledInfo("• Tổng số sản phẩm: ", totalProducts + " sản phẩm"),
                createStyledInfo(
                        "• Giá cao nhất: ",
                        (productHaveMaxExportPrice != null)
                                ? String.format("%s - %s - %.2f",
                                productHaveMaxExportPrice.productCode(),
                                productHaveMaxExportPrice.productName(),
                                productHaveMaxExportPrice.exportPrice())
                                : "UNKNOWN - UNKNOWN - 0.00"
                ),
                createStyledInfo(
                        "• Giá thấp nhất: ",
                        (productHaveMinExportPrice != null)
                                ? String.format("%s - %s - %.2f",
                                productHaveMinExportPrice.productCode(),
                                productHaveMinExportPrice.productName(),
                                productHaveMinExportPrice.exportPrice())
                                : "UNKNOWN - UNKNOWN - 0.00"
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
//                        "-fx-border-width: 1;" +
//                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
//                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 4, 0, 1, 1);"
        );
        VBox.setVgrow(columns, Priority.ALWAYS);      // Cho phần columns co giãn
        VBox.setVgrow(content, Priority.ALWAYS);      // Cho VBox chính co giãn

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
        });
        VBox.setVgrow(this, Priority.ALWAYS);
        this.setStyle("-fx-background-color: #e0f2f7;");
        HBox actionRow = new HBox(10);
        actionRow.getChildren().addAll(btnSave, btnCancel);
        actionRow.setStyle("-fx-padding: 10; -fx-background-color: #e1f0f7;");
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
        comboBox.getSelectionModel().select(productGroup);
        tfId.setText(productData.getCode());
        tfName.setText(productData.getName());
        tfQuantity.setText(productData.getQuantity() + "");
        tfUnit.setText(productData.getUnit());
        tfUniPrice.setText(currencyFormat.format(productData.getUnitPrice()) + "");
        tfTotal.setText(currencyFormat.format(productData.getUnitPrice() * productData.getQuantity())+ "");
    }
}
