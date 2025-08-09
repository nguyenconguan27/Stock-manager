package com.manager.stock.manager_stock.screen.product.productDetail;

import com.manager.stock.manager_stock.interfaceActionHandler.TopBarActionHandler;
import com.manager.stock.manager_stock.model.ProductGroup;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.screen.productGroup.ProductGroupPresenter;
import com.manager.stock.manager_stock.utils.CreateTopBarOfReceiptUtil;
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
import java.util.List;
import java.util.Locale;

public class ProductDetailScreen extends VBox{

    private final Logger logger = LoggerFactory.getLogger(ProductDetailScreen.class);
    final ProductDetailPresenter productDetailPresenter;
    final ProductGroupPresenter productGroupPresenter;
    NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.US);
    ObservableList<ProductGroup> productGroupData = FXCollections.observableArrayList();
    ProductModel productData;
    private ComboBox<ProductGroup> comboBox;

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
        comboBox.setStyle(inputStyle);

        productGroupData.addListener((ListChangeListener<? super ProductGroup>) change -> {
            if (!productGroupData.isEmpty() && comboBox.getSelectionModel().isEmpty()) {
                comboBox.getSelectionModel().selectFirst();
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
            ProductGroup selected = comboBox.getSelectionModel().getSelectedItem();
            // TODO: xử lý khi chọn nhóm
        });

        Label groupLabel = new Label("Nhóm sản phẩm *");
        groupLabel.setStyle(labelStyle);
        VBox comboBoxGroup = new VBox(5, groupLabel, comboBox);
        comboBoxGroup.setPadding(new Insets(10, 15, 0, 15));

        // === Form bên trái ===
        GridPane leftForm = new GridPane();
        leftForm.setHgap(10);
        leftForm.setVgap(10);
        leftForm.setPadding(new Insets(15));

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
            System.out.println("textfield changed from " + oldValue + " to " + newValue);
        });

        tfUniPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("textfield changed from " + oldValue + " to " + newValue);
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

    public ProductDetailScreen() {
        productDetailPresenter = ProductDetailPresenter.getInstance();
        productGroupPresenter = ProductGroupPresenter.getInstance();
        currencyFormat.setMinimumFractionDigits(0);
//        initProductGroup();
        initButton();
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

        this.getChildren().addAll(topBar, detailForm(), btnSave);
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
