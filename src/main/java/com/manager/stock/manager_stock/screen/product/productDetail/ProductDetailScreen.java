package com.manager.stock.manager_stock.screen.product.productDetail;

import com.manager.stock.manager_stock.exception.DaoException;
import com.manager.stock.manager_stock.interfaceActionHandler.TopBarActionHandler;
import com.manager.stock.manager_stock.model.ProductGroup;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.model.dto.ExportPriceAndProductCodeAndProductName;
import com.manager.stock.manager_stock.model.dto.ProductIdAndCodeAndNameAndQuantityInStock;
import com.manager.stock.manager_stock.screen.productGroup.ProductGroupPresenter;
import com.manager.stock.manager_stock.utils.AlertUtils;
import com.manager.stock.manager_stock.utils.CreateTopBarOfReceiptUtil;
import com.manager.stock.manager_stock.utils.FormatMoney;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
    private VBox statisticNode = new VBox();

    TextField tfId, tfQuantity, tfName, tfUnit, tfUniPrice, tfTotal;
    Button btnSave;

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
                VBox newStatisticNode = createStatisticProductByGroup();
                if (statisticNode == null) {
                    // Nếu chưa add statisticNode trước đó, thì add luôn
                    this.getChildren().add(newStatisticNode);
                } else {
                    int index = this.getChildren().indexOf(statisticNode);
                    if (index != -1) {
                        this.getChildren().set(index, newStatisticNode);
                    } else {
                        this.getChildren().add(newStatisticNode);
                    }
                }
                statisticNode = newStatisticNode;
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
            VBox newStatisticNode = createStatisticProductByGroup();
            int indexOldNode = this.getChildren().indexOf(statisticNode);
            if(indexOldNode != -1){
                this.getChildren().add(indexOldNode, newStatisticNode);
                statisticNode = newStatisticNode;
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
        root.setStyle("-fx-background-color: #e1f0f7;");
        root.setPadding(new Insets(10));

        return root;
    }

    public void initButton() {
        btnSave = new Button("Save");
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
    }

    private VBox createStatisticProductByGroup() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-color: #f0f8ff;");

        // Lọc sản phẩm theo nhóm
        List<ProductModel> groupProducts = productDatas.stream()
                .filter(p -> p.getGroupId() == productGroupSelected.getId())
                .collect(Collectors.toList());

        int totalProducts = groupProducts.size();
        ExportPriceAndProductCodeAndProductName productHaveMaxExportPrice, productHaveMinExportPrice;
        List<ProductIdAndCodeAndNameAndQuantityInStock> lowStockProducts, maxQuantityInStock;
        try {
            // tìm sản phẩm có giá xuất lớn nhất
            productHaveMaxExportPrice = productDetailPresenter.findMaxExportPriceByGroup(productGroupSelected.getId());
            // tìm sản phẩm có giá xuất nhỏ nhất
            productHaveMinExportPrice = productDetailPresenter.findMinExportPriceByGroup(productGroupSelected.getId());
            // liệt kê danh sách 5 sản phẩm tồn kho lớn nhất
            maxQuantityInStock = productDetailPresenter.findListProductHaveMaxQuantityInStockByGroup(productGroupSelected.getId());
            // liệt kê danh sách 5 sản phẩm sắp hết hàng
            lowStockProducts = productDetailPresenter.findListProductHaveMinQuantityInStockByGroup(productGroupSelected.getId());
        }
        catch (DaoException e) {
            AlertUtils.alert(e.getMessage(), "ERROR", "Lỗi", "Lỗi");
            return root;
        }

        root.getChildren().add(new Label("📊 Thống kê nhanh"));
        root.getChildren().add(new Label("• Nhóm sản phẩm hiện tại: " + productGroupSelected.getName()));
        root.getChildren().add(new Label("• Tổng số sản phẩm: " + totalProducts + " sản phẩm"));

        root.getChildren().add(new Label(String.format("• Giá cao nhất: %s - %s - %f",
                                            productHaveMaxExportPrice.productCode(),
                                            productHaveMaxExportPrice.productName(),
                                            productHaveMaxExportPrice.exportPrice())));

        root.getChildren().add(new Label(String.format("• Giá thấp nhất: %s - %s - %f",
                                            productHaveMinExportPrice.productCode(),
                                            productHaveMinExportPrice.productName(),
                                            productHaveMinExportPrice.exportPrice())));

        root.getChildren().add(new Label("• Danh sách 5 sản phẩm sắp hết hàng:"));
        if (lowStockProducts.isEmpty()) {
            root.getChildren().add(new Label("   - Không có sản phẩm nào sắp hết hàng."));
        } else {
            for (ProductIdAndCodeAndNameAndQuantityInStock p : lowStockProducts) {
                root.getChildren().add(new Label("   - " + p.productCode() + ": " + p.quantityInStock() + " cái (tồn kho < 5)"));
            }
        }

        root.getChildren().add(new Label("• Danh sách 5 sản phẩm tồn kho nhiều nhất: "));
        for (ProductIdAndCodeAndNameAndQuantityInStock p : maxQuantityInStock) {
            root.getChildren().add(new Label("   - " + p.productCode() + ": " + p.quantityInStock() + " cái (tồn kho < 5)"));
        }

        return root;
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
        System.out.println("Product group selected: " + productGroupSelected);
//        statisticNode = createStatisticProductByGroup();
        this.getChildren().addAll(topBar, formDetail, statisticNode, btnSave);
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
