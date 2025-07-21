package com.manager.stock.manager_stock.screen.product.productDetail;

import com.manager.stock.manager_stock.model.ProductGroup;
import com.manager.stock.manager_stock.model.ProductModel;
import com.manager.stock.manager_stock.screen.productGroup.ProductGroupPresenter;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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


    public HBox detailForm() {
        GridPane leftForm = new GridPane();
        leftForm.setHgap(10);
        leftForm.setVgap(10);
        leftForm.setPadding(new Insets(15));

        leftForm.add(new Label("Mã vật tư *"), 0, 0);
        tfId = new TextField();
        leftForm.add(tfId, 1, 0);

        leftForm.add(new Label("Tên vật tư *"), 0, 1);
        tfName = new TextField();
        leftForm.add(tfName, 1, 1);

        leftForm.add(new Label("Số lượng còn*"), 0, 2);
        tfQuantity = new TextField();
        leftForm.add(tfQuantity, 1, 2);

        // === Form bên phải ===
        GridPane rightForm = new GridPane();
        rightForm.setHgap(10);
        rightForm.setVgap(10);
        rightForm.setPadding(new Insets(15));

        rightForm.add(new Label("Đơn vị tính*"), 0, 0);
        tfUnit = new TextField();
        rightForm.add(tfUnit, 1, 0);

        rightForm.add(new Label("Đơn giá xuất*"), 0, 1);
        tfUniPrice = new TextField();
        rightForm.add(tfUniPrice, 1, 1);


        rightForm.add(new Label("Thành tiền *"), 0, 2);
        tfTotal = new TextField();
        rightForm.add(tfTotal, 1, 2);

        HBox form = new HBox();
        form.getChildren().addAll(leftForm, rightForm);

        return form;
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
        initProductGroup();
        initButton();
        this.getChildren().addAll(detailForm(), comboBox, btnSave);
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
