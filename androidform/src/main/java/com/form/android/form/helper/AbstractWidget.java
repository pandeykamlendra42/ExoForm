package com.form.android.form.helper;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Core widget to configure form elements.
 */
public abstract class AbstractWidget {

    private String _property;
    private boolean isRequired;
    private String _displayText, validationError;
    protected LinearLayout _layout;

    /**
     * To initiate core widget
     * @param context
     * @param name
     * @param required
     */
    public AbstractWidget(Context context, String name, boolean required, String validationMessage) {

        _layout = new LinearLayout(context);
        _layout.setLayoutParams(FormWrapper.defaultLayoutParams);
        _layout.setOrientation(LinearLayout.VERTICAL);

        _property = name;
        isRequired = required;
        _displayText = name.replace("_", " ");
        _displayText = toTitleCase(_displayText);
        validationError = validationMessage;
    }


    /**
     * return LinearLayout containing this widget's view elements
     */
    public View getView() {
        return _layout;
    }

    /**
     * toggles the visibility of this widget
     *
     * @param value
     */
    public void setVisibility(int value) {
        _layout.setVisibility(value);
    }


    /**
     * returns value of this widget as String
     */
    public String getValue() {
        return "";
    }

    /**
     * sets value of this widget, method should be overridden in sub-class
     *
     * @param value
     */
    public void setValue(String value) {
    }

    /**
     * sets the hint for the widget, method should be overriden in sub-class
     */
    public void setHint(String value) {
        // -- override
    }

    /**
     * returns the un-modified name of the property this widget represents
     */
    public String getPropertyName() {
        return _property;
    }

    /**
     * returns a title case version of this property
     *
     * @return
     */
    public String getDisplayText() {
        return _displayText;
    }

    /**
     * Formatting property name and modifies
     *
     * @param s
     * @return
     */
    public String toTitleCase(String s) {
        char[] chars = s.trim().toLowerCase().toCharArray();
        boolean found = false;

        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i])) {
                found = false;
            }
        }

        return String.valueOf(chars);
    }

    /**
     * Sets validation error message to the element.
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) { }

    /**
     * Checks required fields.
     * @return
     */
    public boolean isRequired() {
        return isRequired;
    }

    /**
     * Sets the fields required state.
     * @param isRequired
     */
    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public String getValidationError() {
        return validationError;
    }
}
