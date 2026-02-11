package com.manager.stock.manager_stock.utils;

import java.math.BigDecimal;

/**
 * @author Trọng Hướng
 */
public class ExportRoundingAllocator {
    private BigDecimal expectedTotal;
    private BigDecimal printedTotal = BigDecimal.ZERO;

    public ExportRoundingAllocator(BigDecimal expectedTotal) {
        this.expectedTotal = expectedTotal;
    }

    public BigDecimal adjustReceipt(BigDecimal receiptCalculatedTotal, boolean isLastReceipt) {
        if(!isLastReceipt) {
            printedTotal = printedTotal.add(receiptCalculatedTotal);
            return receiptCalculatedTotal;
        }
        BigDecimal remaining = expectedTotal.subtract(printedTotal);

        printedTotal = printedTotal.add(remaining);

        return remaining;
    }

    public  BigDecimal getExpectedTotal() {return expectedTotal;}

}
