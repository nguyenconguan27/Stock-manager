package com.manager.stock.manager_stock.utils;

import javafx.scene.control.Button;

/**
 * @author Trọng Hướng
 */
public class AddCssStyleForBtnUtil {

    public static void addCssStyleForBtn(Button btn){
        String buttonBaseStyle =
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #34536e;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 5px 10px;" +
                        "-fx-cursor: hand;" +
                        "-fx-alignment: center;" +
                        "-fx-graphic-text-gap: 8px;" +
                        "-fx-background-radius: 4px;" +
                        "-fx-border-color: #d0d0d0;" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 4px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);";

        String buttonHoverStyle =
                "-fx-background-color: #d1eff7;" +
                        "-fx-border-color: #b0d0d8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);";

        String buttonPressedStyle =
                "-fx-background-color: #c3e8f0;" +
                        "-fx-border-color: #90b0b8;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);" +
                        "-fx-translate-y: 1px;";
        btn.setStyle(buttonBaseStyle);

        btn.setOnMouseEntered(e -> btn.setStyle(buttonBaseStyle + buttonHoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(buttonBaseStyle));

        btn.setOnMousePressed(e -> btn.setStyle(buttonBaseStyle + buttonPressedStyle));
        btn.setOnMouseReleased(e -> {
            if (btn.isHover()) {
                btn.setStyle(buttonBaseStyle + buttonHoverStyle);
            } else {
                btn.setStyle(buttonBaseStyle);
            }
        });
    }
}
