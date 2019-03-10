package com.form.android.listeners;
/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

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
