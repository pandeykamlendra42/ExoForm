package com.form.android.listeners;

/**
 * A listener to handle media element events at view level.
 */
public interface OnRequestMediaListener {
    /**
     * Requesting for media when picker type element has been clicked.
     *
     * @param mediaType
     * @param fieldName
     */
    void onRequestMedia(String mediaType, String fieldName);
}
