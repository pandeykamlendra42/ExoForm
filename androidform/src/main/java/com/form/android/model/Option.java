package com.form.android.model;

/**
 * A model for multi-options
 */
public class Option {
    private String name, key, icon;

    /**
     * A basic multi-options with label
     * @param name
     * @param key
     */
    public Option(String name, String key) {
        this.name = name;
        this.key = key;
    }

    /**
     * Multi-options with label & left drawable
     * @param name
     * @param key
     * @param icon
     */
    public Option(String name, String key, String icon) {
        this.name = name;
        this.key = key;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getIcon() {
        return icon;
    }

}
