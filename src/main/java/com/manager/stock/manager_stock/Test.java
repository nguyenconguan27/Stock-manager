package com.manager.stock.manager_stock;

/**
 * @author Trọng Hướng
 */
public class Test {
    public static void main(String[] args) {
        long number = 1530;
        while (number > 0) {
            System.out.println((int) (number % 1000));
            number /= 1000;
        }
    }
}
