package com.manager.stock.manager_stock.utils;

import com.manager.stock.manager_stock.model.ProductModel;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Bounds;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import java.text.Normalizer;
import java.util.Locale;

/**
 * @author Trọng Hướng
 */
public class ProductAutoComplete {
    private final TextField field;
    private final PopupControl popup = new PopupControl();
    private final ListView<ProductModel> list = new ListView<>();
    private final FilteredList<ProductModel> filtered;
    private final BooleanProperty programmatic = new SimpleBooleanProperty(false);

    private final StringConverter<ProductModel> conv = new StringConverter<>() {
        @Override public String toString(ProductModel p) {
            return p == null ? "" : p.getCode() + " - " + p.getName();
        }
        @Override public ProductModel fromString(String s) { return null; }
    };

    private final ObjectProperty<ProductModel> value = new SimpleObjectProperty<>(null);
    public ReadOnlyObjectProperty<ProductModel> valueProperty() { return value; }
    public ProductModel getValue() { return value.get(); }

    public ProductAutoComplete(TextField field, ObservableList<ProductModel> all) {
        this.field = field;
        this.filtered = new FilteredList<>(all, p -> true);

        list.setItems(filtered);
        list.setCellFactory(v -> new ListCell<>() {
            @Override protected void updateItem(ProductModel it, boolean empty) {
                super.updateItem(it, empty);
                setText(empty || it == null ? null : conv.toString(it));
            }
        });
        list.setPrefHeight(220);

        // cấu hình popup
        popup.setAutoHide(true);
        popup.setAutoFix(true);
        popup.setPrefWidth(250);
        popup.getScene().setRoot(list);
        popup.setHideOnEscape(true);

        field.textProperty().addListener((o, ov, nv) -> {
            if (programmatic.get()) return; // tránh lặp khi commit
            String q = normalizeString(nv);

            if (nv == null || nv.isBlank()) {
                // người dùng xóa hết -> show ALL, clear value & selection
                value.set(null);
                filtered.setPredicate(p -> true);
                list.getSelectionModel().clearSelection();
                showBelow();
                return;
            }

            filtered.setPredicate(p ->
                    normalizeString(p.getCode() + " - " + p.getName()).contains(q)
            );
            list.getSelectionModel().clearSelection();
            if (!filtered.isEmpty()) showBelow(); else popup.hide();
        });

        field.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            switch (e.getCode()) {
                case DOWN -> { if (!popup.isShowing()) showBelow(); list.getSelectionModel().selectNext(); list.scrollTo(list.getSelectionModel().getSelectedIndex()); e.consume(); }
                case UP   -> { if (!popup.isShowing()) showBelow(); list.getSelectionModel().selectPrevious(); list.scrollTo(list.getSelectionModel().getSelectedIndex()); e.consume(); }
                case ENTER-> { commit(list.getSelectionModel().getSelectedItem()); e.consume(); }
                case ESCAPE -> { popup.hide(); e.consume(); }
                default -> { /* gõ bình thường: chữ, số, SPACE... -> không commit */ }
            }
        });

        field.focusedProperty().addListener((obs, oldF, newF) -> {
            if (newF) {
                if (field.getText().isBlank()) {
                    filtered.setPredicate(p -> true);
                    list.getSelectionModel().clearSelection();
                }
                showBelow();
            } else popup.hide();
        });

        field.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            refreshAndShow();
        });


        list.setOnMouseClicked(me -> {
            if (me.getClickCount() == 1) commit(list.getSelectionModel().getSelectedItem());
        });
        field.focusedProperty().addListener((obs, fOld, fNew) -> { if (!fNew) popup.hide(); });
    }

    private void showBelow() {
        if (field.getScene() == null) return;
        Bounds b = field.localToScreen(field.getBoundsInLocal());
        if (!popup.isShowing()) popup.show(field, b.getMinX(), b.getMaxY());
        else popup.setX(b.getMinX()); // cập nhật vị trí nếu cần
    }

    private void commit(ProductModel p) {
        if (p == null) { popup.hide(); return; }
        value.set(p);
        programmatic.set(true);
        field.setText(conv.toString(p));    // ghi text theo item đã chọn
        field.positionCaret(field.getText().length());
        programmatic.set(false);
        popup.hide();
    }

    private static String normalizeString(String s) {
        if (s == null) return "";
        String t = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return t.toLowerCase(Locale.ROOT);
    }

    private void refreshAndShow() {
        String q = normalizeString(field.getText());
        filtered.setPredicate(p -> normalizeString(p.getCode() + " - " + p.getName()).contains(q));
        if (!filtered.isEmpty()) {
            list.getSelectionModel().clearSelection();
            list.setPrefHeight(Math.min(filtered.size(), 15) * 28 + 4);
            showBelow();
        } else {
            popup.hide();
        }
    }

}
