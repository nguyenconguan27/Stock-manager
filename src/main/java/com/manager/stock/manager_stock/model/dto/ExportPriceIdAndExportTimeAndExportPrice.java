package com.manager.stock.manager_stock.model.dto;

import java.time.LocalDateTime;

/**
 * @author Trọng Hướng
 */
public record ExportPriceIdAndExportTimeAndExportPrice(long exportPriceId, LocalDateTime exportTime, long exportPrrice) {
}
