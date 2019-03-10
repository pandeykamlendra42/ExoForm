package com.form.android.utils;

import android.text.InputType;

import java.util.HashMap;
import java.util.Map;

/**
 * A class which has all possible element/input types for the any kind of form
 * which will be render by this.
 */

public class InputTypeUtil {
    public static final int TYPE_CLASS_TEXT_AREA = 81;
    public static final int TYPE_CLASS_TEXT_LOCATION = 911;
    public static final int TYPE_CLASS_DATE_TIME = 912;
    public static final int TYPE_CLASS_DATE = 913;
    public static final int TYPE_CLASS_TIME = 914;
    private static final Map<String, Integer> inputTypes = new HashMap<String, Integer>() {
        {
            put("text", InputType.TYPE_CLASS_TEXT);
            put("textarea", TYPE_CLASS_TEXT_AREA);
            put("text_area", TYPE_CLASS_TEXT_AREA);
            put("numberpassword", InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            put("number_password", InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            put("numberPassword", InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            put("text_password", InputType.TYPE_TEXT_VARIATION_PASSWORD);
            put("textpassword", InputType.TYPE_TEXT_VARIATION_PASSWORD);
            put("textPassword", InputType.TYPE_TEXT_VARIATION_PASSWORD);
            put("number", InputType.TYPE_CLASS_NUMBER);
            put("phone", InputType.TYPE_CLASS_PHONE);
            put("datetime", InputType.TYPE_CLASS_DATETIME);
            put("date_time", InputType.TYPE_CLASS_DATETIME);
            put("dateTime", InputType.TYPE_CLASS_DATETIME);
            put("location", TYPE_CLASS_TEXT_LOCATION);
            put("date", TYPE_CLASS_DATE);
            put("time", TYPE_CLASS_TIME);
        }
    };

    /**
     * Method to get input/element actual constant
     *
     * @param inputType
     * @return int input/element type constant
     */
    public static int getInputType(String inputType) {
        Integer etType = inputTypes.get(inputType.toLowerCase());
        return (etType != null) ? etType : InputType.TYPE_CLASS_TEXT;
    }


}
