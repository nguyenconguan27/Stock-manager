package com.manager.stock.manager_stock.updbfile;

import com.upfileservice.UpfileService;

public class Main {

    public static void main(String[] args) throws Exception {
        // File local muốn đẩy lên
        String localFilePath = "./data/mydb.mv.db";
        String fileName = "mydb.mv.db";

        // Folder ID trên Google Drive bạn muốn upload vào
        String folderId = "1AbCdEfGh1234567890"; // <- Thay bằng ID thật
        UpfileService upFileService = new UpfileService();
        upFileService.upfile();
    }
}