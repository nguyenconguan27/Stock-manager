package com.manager.stock.manager_stock;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Trọng Hướng
 */
public class Test2 {
    public static void main(String[] args) {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Lấy LocalDateTime rồi trừ đi 3 năm
        LocalDateTime threeYearsAgo = now.toLocalDateTime().minusYears(3);

        // Chuyển ngược lại về Timestamp
        Timestamp result = Timestamp.valueOf(threeYearsAgo);
        System.out.println(threeYearsAgo);
    }
}
