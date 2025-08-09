package com.manager.stock.manager_stock.utils;

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

    private static final String[] TYPE1 = {
            "", "mươi", "trăm"
    };

    private static final String[] TYPE2 = {
            "", "nghìn", "triệu", "tỷ"
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
        String value = String.valueOf(amount);
        StringBuilder result = new StringBuilder();
        String[] groups = splitToGroups(value);

        for (int index = 0; index < groups.length; index++) {
            String group = groups[index];
            int groupValue = Integer.parseInt(group);
            if (groupValue == 0) continue;
            if (index >= TYPE2.length) return "Số quá lớn";

            StringBuilder part = new StringBuilder();
            int count = 0;

            for (int i = group.length() - 1; i >= 0; i--) {
                char digitChar = group.charAt(i);
                int digit = digitChar - '0';

                if (i == group.length() - 1 && group.length() > 1) {
                    part.insert(0, TYPE2[index] + " ");
                } else if (i == group.length() - 1 && group.length() == 1) {
                    part.insert(0, TYPE2[index] + " ");
                }

                if (digit != 0) {
                    part.insert(0, TYPE0[digit] + " " + TYPE1[count] + " ");
                } else if (count > 0 && part.length() > 0) {
                    part.insert(0, "không " + TYPE1[count] + " ");
                }

                count++;
            }

            result.insert(0, part);
        }

        String resultStr = result.toString().trim()
                .replaceAll("mươi năm", "mươi lăm")
                .replaceAll("mươi một", "mươi mốt")
                .replaceAll("một mươi", "mười")
                .replaceAll("\\s+", " ")
                .trim();

        if (groups.length > 0 && Integer.parseInt(groups[0]) == 0) {
            return resultStr + " đồng";
        }

        return resultStr + " đồng";
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
}
