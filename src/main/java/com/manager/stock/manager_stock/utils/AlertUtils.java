package com.manager.stock.manager_stock.utils;

import javafx.scene.control.Alert;

/**
 * @author Trọng Hướng
 */
public class AlertUtils {
    public static void alert(String message, String alertType, String alertTitle, String alertHeader) {
        Alert alert = new Alert(Alert.AlertType.valueOf(alertType));
        alert.setTitle(alertTitle);
        alert.setHeaderText(alertHeader);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
