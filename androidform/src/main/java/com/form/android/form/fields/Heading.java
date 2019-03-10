package com.form.android.form.fields;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.form.android.R;
import com.form.android.form.helper.AbstractWidget;
import com.form.android.form.helper.FormWrapper;
import com.form.android.ui.WidgetLayoutParams;

import org.json.JSONObject;

/**
 * @Heading is used for rendering Heading / Dummy element which is specially for lengthy form
 * to categories their fields.
 */
public class Heading extends AbstractWidget {

    private final String ELEMENT_TAG = "HEADING_";
    public static final LinearLayout.LayoutParams viewParams = WidgetLayoutParams.
            getCustomWidthHeightLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
    protected TextView _label;
    protected View view;

    /**
     * Public constructor to inflate the headings.
     *
     * @param context
     * @param property
     * @param label
     * @param jsonObject
     */
    public Heading(final Context context, final String property, String label, final JSONObject jsonObject) {
        super(context, property, false, null);

        LinearLayout.LayoutParams layoutParams = WidgetLayoutParams.getFullWidthLayoutParams();
        layoutParams.setMargins(context.getResources().getDimensionPixelSize(R.dimen.dimen_5dp),
                0, context.getResources().getDimensionPixelSize(R.dimen.dimen_5dp),
                context.getResources().getDimensionPixelSize(R.dimen.dimen_12dp));
        layoutParams.gravity = Gravity.CENTER;


        // Adding bottom line divider.
        View dividerView = new View(context);
        dividerView.setBackgroundResource(R.color.light_gray);
        LinearLayout.LayoutParams dividerLayoutParams = WidgetLayoutParams.getCustomWidthHeightLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                context.getResources().getDimensionPixelSize(R.dimen.padding_1dp));
        dividerView.setLayoutParams(dividerLayoutParams);
        addView(context, label);
        _label.setTag(ELEMENT_TAG + property);

    }

    /**
     * Method to add label view.
     *
     * @param context Context of the class.
     * @param label   Label to be shown.
     */
    public void addView(Context context, String label) {
        _label = new TextView(context);
        _label.setText(label);
        _label.setTypeface(null, Typeface.BOLD);
        _label.setLayoutParams(FormWrapper.defaultLayoutParams);
        _label.setPadding(context.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                context.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                context.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                context.getResources().getDimensionPixelSize(R.dimen.padding_5dp));


        view = new View(context);
        view.setLayoutParams(viewParams);

        _layout.addView(_label);
        _layout.addView(view);
    }
}
