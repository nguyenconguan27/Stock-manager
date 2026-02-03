package com.manager.stock.manager_stock.utils;

import com.manager.stock.manager_stock.exception.InvalidException;
import org.checkerframework.checker.units.qual.Current;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * @author Trọng Hướng
 */
public class FormatMoney {

    private static final String[] TYPE0 = {
            "không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"
    };

    private static final String[] TYPE2 = {
            "", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ"
    };


    public static String format(double money) {
        Locale locale = new Locale("vi", "VN");
        Currency currency = Currency.getInstance("VND");

        DecimalFormatSymbols df = DecimalFormatSymbols.getInstance(locale);
        df.setCurrency(currency);
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setCurrency(currency);
        return numberFormat.format(money);
    }

    public static String formatMoneyToWord(long amount) {
        if (amount == 0) return "Không đồng";

        String number = String.valueOf(amount);
        String[] groups = splitToGroups(number);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < groups.length; i++) {
            int groupValue = Integer.parseInt(groups[i]);
            if (groupValue == 0) continue;

            String groupText = convertGroupToWord(groups[i], i < groups.length - 1);
            if (!groupText.isEmpty()) {
                result.insert(0, groupText + " " + TYPE2[i] + " ");
            }
        }

        String finalResult = result.toString().trim().replaceAll("\\s+", " ");
        return Character.toUpperCase(finalResult.charAt(0)) + finalResult.substring(1) + " đồng";
    }


    private static String convertGroupToWord(String group, boolean isHigherGroup) {
        while (group.length() < 3) group = "0" + group;

        int tram = group.charAt(0) - '0';
        int chuc = group.charAt(1) - '0';
        int donvi = group.charAt(2) - '0';

        StringBuilder sb = new StringBuilder();

        // HÀNG TRĂM
        if (tram > 0) {
            sb.append(TYPE0[tram]).append(" trăm");
        } else if (isHigherGroup && (chuc > 0 || donvi > 0)) {
            sb.append("không trăm");
        }

        // HÀNG CHỤC
        if (chuc > 1) {
            sb.append(" ").append(TYPE0[chuc]).append(" mươi");
            if (donvi == 1) {
                sb.append(" mốt");
            } else if (donvi == 5) {
                sb.append(" lăm");
            } else if (donvi > 0) {
                sb.append(" ").append(TYPE0[donvi]);
            }
        } else if (chuc == 1) {
            sb.append(" mười");
            if (donvi == 5) {
                sb.append(" lăm");
            } else if (donvi > 0) {
                sb.append(" ").append(TYPE0[donvi]);
            }
        } else { // chuc == 0
            if (donvi > 0) {
                if (tram > 0 || isHigherGroup) {
                    sb.append(" linh");
                }
                sb.append(" ").append(TYPE0[donvi]);
            }
        }

        return sb.toString().trim();
    }

    private static String[] splitToGroups(String number) {
        int len = number.length();
        int groupCount = (int) Math.ceil(len / 3.0);
        String[] groups = new String[groupCount];

        int index = 0;
        for (int i = len; i > 0; i -= 3) {
            int start = Math.max(0, i - 3);
            groups[index++] = number.substring(start, i);
        }
        return groups;
    }

    public static Double parseFlexibleMoney(String moneyStr) {
        if (moneyStr == null || moneyStr.isBlank()) return null;
        String cleaned = moneyStr.trim()
                .toLowerCase()
                .replace("đ", "")
                .replace("vnđ", "")
                .replace("₫", "")
                .replace(" ", "");

        cleaned = cleaned.replace(".", "").replace(",", ".");

        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            throw new InvalidException("Giá trị tiền tệ không hợp lệ, vui lòng điền đúng định dạng (123 ₫, 123₫, 123, 1.234,56 ₫).");
        }
    }

    public static int parseMoneyToInt(String value) {
        return Integer.parseInt(
                value.replace(".", "")
                        .replaceAll("\\D", "")
        );
    }

}
