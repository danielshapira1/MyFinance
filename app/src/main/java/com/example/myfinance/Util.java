package com.example.myfinance;

import android.content.Context;
import android.text.Spanned;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

public class Util {

    // Fixes format: d/MM/yyyy -> 0d/MM/yyyy  or   dd/M/yyyy -> dd/0M/yyyy
    public static String fixDateFormat(String dateAsDdMmYyyy) {

        if (dateAsDdMmYyyy== null) {
            return null;
        }

        String result = dateAsDdMmYyyy;

        if (result.charAt(1) == '/') { // Single-digit days
            result = "0" + result;
        }
        if (result.charAt(4) == '/'){ // Single-digit months
            String temp1 = result.substring(0,3);
            String temp2 = result.substring(3);
            result = temp1 + "0" + temp2;
        }

        return result;
    }


    // Fixes format for lexicographic order: dd/MM/yyyy -> yyyy/MM/dd
    public static String fixDateFormatToLexicographicOrder(String dateAsDdMmYyyy) {

        if (dateAsDdMmYyyy==null) {
            return null;
        }

        String result = null;

        String[] dateArr = dateAsDdMmYyyy.split("/");
        String year = dateArr[2];
        String month = dateArr[1];
        String day = dateArr[0];
        result = year + "/" + month + "/" + day;

        return result;
    }

    public static int[] getColors(Context context){
        return new int[]{ContextCompat.getColor(context, R.color.yellowTest),
                ContextCompat.getColor(context, R.color.blueTest),
                ContextCompat.getColor(context, R.color.peach),
                ContextCompat.getColor(context, R.color.PaleGreen)};
    }

    public static Spanned htmlFormat(String html){
        return HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY);
    }

}
