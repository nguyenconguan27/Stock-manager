package com.manager.stock.manager_stock.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.util.Optional;

/**
 * @author Trọng Hướng
 */
public class AlertUtils {
    public static void alert(String message, String alertType, String alertTitle, String alertHeader) {
        Alert alert = new Alert(Alert.AlertType.valueOf(alertType));
        alert.setTitle(alertTitle);
        alert.setHeaderText(alertHeader);

        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(400);
        alert.getDialogPane().setContent(label);

        alert.showAndWait();
    }


    public static boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

}
