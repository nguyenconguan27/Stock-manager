package com.manager.stock.manager_stock.updbfile;

import com.upfileservice.UpfileService;

public class Main {

    public static void main(String[] args) throws Exception {
        UpfileService upFileService = new UpfileService();
        upFileService.upfile();
    }
}