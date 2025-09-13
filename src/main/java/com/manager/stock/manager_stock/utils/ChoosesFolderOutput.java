package com.manager.stock.manager_stock.utils;

import javafx.stage.FileChooser;

import java.io.File;

/**
 * @author Trọng Hướng
 */
public class ChoosesFolderOutput {
    public static File choosesFolderFile(String baseFileName) {
        // 1) Mở hộp thoại chọn nơi lưu + tên file
        FileChooser fc = new FileChooser();
        fc.setTitle("Chọn nơi lưu file xuất");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Workbook (*.xlsx)", "*.xlsx")
        );
        fc.setInitialFileName(baseFileName + "_" + java.time.LocalDate.now() + ".xlsx");

        // Lấy cửa sổ hiện hành làm owner an toàn
        javafx.stage.Window owner = null;
        for (javafx.stage.Window w : javafx.stage.Window.getWindows()) {
            if (w.isShowing()) { owner = w; break; }
        }

        File file = fc.showSaveDialog(owner);
        if (file == null) {
            return null;
        }

        // Đảm bảo có phần mở rộng .xlsx nếu người dùng không gõ
        String path = file.getAbsolutePath();
        if (!path.toLowerCase().endsWith(".xlsx")) {
            file = new File(path + ".xlsx");
        }

        // 2) Hỏi ghi đè nếu file đã tồn tại
        if (file.exists()) {
            boolean ok = AlertUtils.confirm("File đã tồn tại. Bạn có muốn ghi đè?");
            if (!ok) return null;
        }
        return file;
    }
}
