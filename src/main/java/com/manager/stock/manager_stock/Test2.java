package com.manager.stock.manager_stock;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Trọng Hướng
 */
public class Test2 {
    public static void main(String[] args) {
        try {
            String date = "01/01/2025 21:10:16";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime createAt = LocalDateTime.parse(date, formatter);
            LocalDateTime newCreateAt = LocalDateTime.parse(date, formatter);
            System.out.println("Before " + createAt.isBefore(newCreateAt));
            System.out.println("After " + createAt.isAfter(newCreateAt));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
