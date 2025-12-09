package com.manager.stock.manager_stock;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Trọng Hướng
 */
public class Test2 {
    public static void main(String[] args) {
        String newDate = "02/11/2025 11:30:22";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(newDate, formatter);
        LocalDateTime now = LocalDateTime.now();

        System.out.println(date.isBefore(now));
    }
}
