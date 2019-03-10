package com.form.android.listeners;

/**
 * This is all about handling sub-forms
 */
public interface OnRenderSubForm {

    /**
     * Rendering the sub-form of the respective parent element having
     * selected key as current value.
     *
     * @param key
     */
    void renderSubForm(String key);

    /**
     * Updating the current order of the widget
     */
    void updateWidgetOrder();

    /**
     * Removing the sub-form widgets on any change in the parent element{@name}.
     *
     * @param name
     */
    void clearSubForm(String name);
}
