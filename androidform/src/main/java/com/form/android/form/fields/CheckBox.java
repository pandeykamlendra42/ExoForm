package com.form.android.form.fields;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.form.android.R;
import com.form.android.form.helper.AbstractWidget;
import com.form.android.form.helper.FormWrapper;
import com.form.android.listeners.OnRenderSubForm;
import com.form.android.ui.WidgetLayoutParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * CheckBox is used to inflate the fields for the Check box element.
 */

public class CheckBox extends AbstractWidget implements View.OnClickListener, OnRenderSubForm {

    // Member variables.
    private final String ELEMENT_TAG = "CheckBox_";
    private Context mContext;
    private AppCompatCheckedTextView checkedTextView;
    private ArrayList<AbstractWidget> mFormWidgetList;
    private Map<String, AbstractWidget> mFormWidgetMap;
    private int elementOrder = 0;
    private String mFieldName;
    private FormWrapper mFormWrapper;
    private JSONObject joProperty;

    /**
     * Public constructor to inflate form field For the checkbox items.
     *
     * @param context               Context of calling class.
     * @param property              Property of the field.
     * @param label                 Label of the field.
     * @param hasValidator          True if the field has validation (Compulsory field).
     * @param defaultValue          Default value of the field.
     * @param _widget               List of FormWidget.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public CheckBox(Context context, FormWrapper formWrapper, JSONObject element, String property, String label, boolean hasValidator,
                    int defaultValue, ArrayList<AbstractWidget> _widget, Map<String, AbstractWidget> map) {
        super(context, property, hasValidator, element.optString("error", context.getResources().getString(R.string.widget_error_msg)));

        // Initializing member variables.
        mContext = context;
        this.mFormWidgetList = _widget;
        this.mFormWidgetMap = map;
        this.mFieldName = property;
        this.joProperty = element;
        this.mFormWrapper = formWrapper;

        checkedTextView = new AppCompatCheckedTextView(mContext);
        checkedTextView.setText(label);
        checkedTextView.setGravity(Gravity.CENTER);
        checkedTextView.setPadding(0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_11dp));
        checkedTextView.setCheckMarkDrawable(getCheckMarkDrawable(mContext));

        checkedTextView.setTag(ELEMENT_TAG + mFieldName);
        checkedTextView.setId(R.id.checkbox);
        checkedTextView.setChecked(defaultValue != 0);
        if (FormWrapper.getLayoutType() == 2) {
            checkedTextView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bottom_border_rounded_corner));
            checkedTextView.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.dimen_15dp));
            LinearLayout.LayoutParams layoutParams = WidgetLayoutParams.getFullWidthLayoutParams();
            layoutParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.dimen_15dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.dimen_10dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.dimen_15dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp));
            checkedTextView.setLayoutParams(layoutParams);

        } else {
            checkedTextView.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp));
        }
        _layout.addView(checkedTextView);

        if (FormWrapper.getLayoutType() != 2) {
            // Adding bottom line divider.
            View view = new View(mContext);
            view.setBackgroundResource(R.color.light_gray);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_1dp));
            view.setLayoutParams(layoutParams);
            _layout.addView(view);
        }

        // Applying click listener on the check box to mark checkbox as checked/unchecked.
        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkedTextView.setError(null);
                if (joProperty.optBoolean("hasSubForm", false)) {
                    if (checkedTextView.isChecked()){
                        renderSubForm("0");
                    } else {
                        renderSubForm("1");
                    }
                }
                checkedTextView.setChecked(!checkedTextView.isChecked());
            }
        });

    }

    /**
     * Method to get List Drawable, which will set onto checked text view.
     *
     * @param context Context of calling class.
     * @return Returns the List Drawable.
     */
    public static StateListDrawable getCheckMarkDrawable(Context context) {

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_check_box_24dp).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorPrimary),
                PorterDuff.Mode.SRC_ATOP));
        Drawable drawableUnChecked = ContextCompat.getDrawable(context, R.drawable.ic_check_box_outline_24dp).mutate();
        StateListDrawable sld = new StateListDrawable();
        sld.addState(new int[]{android.R.attr.state_checked, android.R.attr.state_focused}, drawable);
        sld.addState(new int[]{-android.R.attr.state_checked, android.R.attr.state_focused}, drawableUnChecked);
        sld.addState(new int[]{-android.R.attr.state_checked}, drawableUnChecked);
        sld.addState(new int[]{android.R.attr.state_checked}, drawable);
        return sld;
    }


    @Override
    public String getValue() {
        return String.valueOf(checkedTextView.isChecked() ? "1" : "0");
    }

    @Override
    public void setValue(String value) {
        checkedTextView.setChecked(value.equals("1"));
    }

    @Override
    public void setErrorMessage(String errorMessage) {

        // Showing error message.
        if (!checkedTextView.isChecked() && errorMessage != null) {
            checkedTextView.setError(errorMessage);
            checkedTextView.setFocusable(true);
            checkedTextView.requestFocus();
        }
    }

    @Override
    public void onClick(final View v) {

    }


    @Override
    public void renderSubForm(String key) {
        JSONObject formObject = FormWrapper.getFormSchema().optJSONObject("fields");
        if (formObject == null) {
            return;
        }
        updateWidgetOrder();
        clearSubForm(mFieldName);
        JSONArray subFormArray = formObject.optJSONArray(mFieldName+"_"+key);
        if(subFormArray != null) {
            for (int i = 0; i < subFormArray.length(); ++i) {
                JSONObject fieldsObject = subFormArray.optJSONObject(i);
                if (fieldsObject != null) {
                    String name = fieldsObject.optString("name");
                    String label = fieldsObject.optString("label");

                    AbstractWidget mFormWidget = mFormWrapper.getWidget(mContext, name, fieldsObject, label, false, null, mFormWidgetList,
                            mFormWidgetMap);
                    if (fieldsObject.has(FormWrapper.SCHEMA_KEY_HINT))
                        mFormWidget.setHint(fieldsObject.optString(FormWrapper.SCHEMA_KEY_HINT));
                    try{
                        mFormWidgetList.add(elementOrder+i+1, mFormWidget);
                    }catch (IndexOutOfBoundsException e){
                        Log.d("Exception  Adding",e.getMessage());
                    }
                    mFormWidgetMap.put(name, mFormWidget);
                }
            }
        }
        mFormWrapper.resetFormWrapper();
        FormWrapper.setRegistryByProperty(mFieldName,FormWrapper.getFormSchema().optJSONObject(mFieldName),(!key.equals("")) ? mFieldName+"_"+key : null, "Checkbox", 0);
    }

    @Override
    public void updateWidgetOrder() {
        for (int i = 0; i < mFormWidgetList.size(); i++) {
            if (mFormWidgetList.get(i).getPropertyName().equals(mFieldName)) {
                elementOrder = i;
                break;
            }
        }
    }

    @Override
    public void clearSubForm(String name) {
        JSONObject formObject = FormWrapper.getFormSchema().optJSONObject("fields");
        String child = FormWrapper.getRegistryByProperty(name,"child");
        if(child != null && !child.trim().equals("") && formObject.optJSONArray(child) != null){
            JSONArray subFormArray = formObject.optJSONArray(child);
            for (int i = subFormArray.length()-1; i >= 0 ; --i) {
                if(subFormArray.optJSONObject(i) != null && subFormArray.optJSONObject(i).optBoolean("hasSubForm",false)){
                    clearSubForm(subFormArray.optJSONObject(i).optString("name"));
                }
                try{
                    mFormWidgetList.remove(elementOrder + i + 1);
                }catch (IndexOutOfBoundsException e){
                    Log.d("Exception Removing ",e.getMessage());
                }
            }
        }
    }
}
