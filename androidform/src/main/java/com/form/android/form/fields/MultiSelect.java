package com.form.android.form.fields;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.form.android.R;
import com.form.android.form.helper.AbstractWidget;
import com.form.android.form.helper.FormWrapper;
import com.form.android.listeners.OnRenderSubForm;
import com.form.android.ui.WidgetLayoutParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Map;

/**
 *  @MultiSelect widget is used to inflate the fields for the Multi-Checkbox / Multi-Select with the label
 *  and tick box if checked.
 */

public class MultiSelect extends AbstractWidget implements OnRenderSubForm {

    // Member variables.
    private final String ELEMENT_TAG = "MultiSelect_";
    private Context mContext;
    private TextView tvLabel;
    private ArrayAdapter<String> checkBoxAdapter;
    protected JSONObject jsonObjectOptions;
    protected ArrayList<AppCompatCheckedTextView> mCheckedTextViewList = new ArrayList<>();
    private ArrayList<AbstractWidget> mFormWidgetList;
    private Map<String, AbstractWidget> mFormWidgetMap;
    private int elementOrder = 0;
    private String mFieldName;
    private FormWrapper mFormWrapper;
    private boolean isChecked;
    private String keyName;

    /**
     * Public constructor to inflate form field For the multi checkbox items.
     *
     * @param context      Context of calling class.
     * @param property     Property of the field.
     * @param options      Object with multi options.
     * @param label        Label of the field.
     * @param hasValidator True if the field has validation (Compulsory field).
     * @param description  Description of the field.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public MultiSelect(Context context, FormWrapper formWrapper, String property, JSONObject options, String label, boolean hasValidator,
                       String description, ArrayList<AbstractWidget> widgets, Map<String, AbstractWidget> map) {
        super(context, property, hasValidator, context.getResources().getString(R.string.widget_error_msg));

        // Initializing member variables.
        mContext = context;
        jsonObjectOptions = options;
        tvLabel = new TextView(mContext);

        tvLabel.setTypeface(Typeface.DEFAULT_BOLD);
        tvLabel.setText(label);
        tvLabel.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_6dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0);
        _layout.addView(tvLabel);
        this.mFormWidgetList = widgets;
        this.mFormWidgetMap = map;
        this.mFieldName = property;
        this.mFormWrapper = formWrapper;

        // Showing description text view if description is not empty.
        if (description != null && !description.isEmpty()) {
            TextView tvDescription = new TextView(mContext);
            tvDescription.setText(description);
            tvDescription.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0);
            _layout.addView(tvDescription);
        }
        LinearLayout linearLayout = new LinearLayout(mContext);
        if (FormWrapper.getLayoutType() == 2) {
            LinearLayout.LayoutParams lLayoutParams = WidgetLayoutParams.getFullWidthLayoutParams();
            lLayoutParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.dimen_20dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.dimen_10dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.dimen_20dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.dimen_10dp));
            linearLayout.setLayoutParams(lLayoutParams);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(tvLabel);
            linearLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bottom_border_rounded_corner));

        } else {
            tvLabel.setTypeface(Typeface.DEFAULT_BOLD);
            _layout.addView(tvLabel);
        }

        JSONArray propertyNames = options.names();
        String name, p;
        checkBoxAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item);

        // Adding check boxes.
        try {
            for (int i = 0; i < options.length(); i++) {
                name = propertyNames.getString(i);
                p = options.getString(name);

                // Adding checked text view.
                AppCompatCheckedTextView checkedTextView = new AppCompatCheckedTextView(mContext);
                checkedTextView.setText(p);
                checkedTextView.setId(i);
                checkedTextView.setGravity(Gravity.CENTER);
                checkedTextView.setPadding(0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                        0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_11dp));
                checkedTextView.setCheckMarkDrawable(getCheckMarkDrawable(mContext));
                mCheckedTextViewList.add(checkedTextView);
                checkBoxAdapter.add(name);
                checkedTextView.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                        mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                        mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                        mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp));

                // Adding bottom line divider.
                View view = new View(mContext);
                view.setBackgroundResource(R.color.light_gray);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        mContext.getResources().getDimensionPixelSize(R.dimen.padding_1dp));
                view.setLayoutParams(layoutParams);
                if (FormWrapper.getLayoutType() == 2) {
                    linearLayout.addView(checkedTextView);
                    if (i < (options.length() -1)) linearLayout.addView(view);
                } else {
                    _layout.addView(checkedTextView);
                    _layout.addView(view);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        _layout.setTag(ELEMENT_TAG + mFieldName);

        // Listener to mark check box as checked/unchecked.
        if (mCheckedTextViewList != null && mCheckedTextViewList.size() > 0) {
            for (int i = 0; i < mCheckedTextViewList.size(); i++) {
                final int finalI = i;
                mCheckedTextViewList.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvLabel.setError(null);
                        try {
                            boolean checked = mCheckedTextViewList
                                    .get(finalI).isChecked();
                            isChecked = !checked;
                            renderSubForm(checkBoxAdapter.getItem(finalI));
                            mCheckedTextViewList.get(finalI).setChecked(!checked);
                        } catch (Exception e) {
                            e.printStackTrace();
                            mCheckedTextViewList.get(finalI).setChecked(!mCheckedTextViewList
                                    .get(finalI).isChecked());
                        }
                    }
                });
            }
        }
    }

    @Override
    public String getValue() {

        String returnValues = "";

        for (int i = 0; i < mCheckedTextViewList.size(); i++) {

            int arrayLength = (mCheckedTextViewList.size()) - 1;

            if (mCheckedTextViewList.get(i).isChecked()) {
                AppCompatCheckedTextView checkBox = mCheckedTextViewList.get(i);
                String value = checkBoxAdapter.getItem(checkBox.getId());
                if (i < arrayLength) {
                    returnValues += value + ",";
                } else {
                    returnValues += value;
                }
            }
        }

        return returnValues;
    }

    @Override
    public void setValue(String value) {
        try {
            if (value != null && !value.isEmpty()) {

                Object json = new JSONTokener(value).nextValue();
                /* If Values are coming in form of JsonObject */
                if (json instanceof JSONObject) {
                    JSONObject valuesObject = (JSONObject) json;
                    if (valuesObject != null && valuesObject.length() != 0) {
                        JSONArray valueIds = valuesObject.names();

                        for (int i = 0; i < valueIds.length(); i++) {

                            String checkBoxId = valueIds.optString(i);
                            String checkBoxName = valuesObject.optString(checkBoxId);
                            String item = jsonObjectOptions.optString(checkBoxName);

                            if (item != null && !item.isEmpty()) {
                                AppCompatCheckedTextView checkBox = mCheckedTextViewList.get(checkBoxAdapter.getPosition(checkBoxName));
                                checkBox.setChecked(true);
                            }
                        }
                    }
                }
                /* If Values are coming in form of JsonArray */
                else if (json instanceof JSONArray) {
                    JSONArray valuesArray = (JSONArray) json;
                    for (int i = 0; i < valuesArray.length(); i++) {

                        String checkBoxName = valuesArray.optString(i);
                        String item = jsonObjectOptions.optString(checkBoxName);

                        if (item != null && !item.isEmpty()) {
                            AppCompatCheckedTextView checkBox = mCheckedTextViewList.get(checkBoxAdapter.getPosition(checkBoxName));
                            checkBox.setChecked(true);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        tvLabel.setError(errorMessage);
        tvLabel.setFocusable(true);
        tvLabel.requestFocus();
    }


    /**
     * Method to get List Drawable, which will set onto checked text view.
     *
     * @param context Context of calling class.
     * @return Returns the List Drawable.
     */
    public static StateListDrawable getCheckMarkDrawable(Context context) {

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_done_24dp).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.colorPrimary),
                PorterDuff.Mode.SRC_ATOP));
        StateListDrawable sld = new StateListDrawable();
        sld.addState(new int[]{android.R.attr.state_checked, android.R.attr.state_focused}, drawable);
        sld.addState(new int[]{-android.R.attr.state_checked, android.R.attr.state_focused}, new ColorDrawable(Color.WHITE));
        sld.addState(new int[]{-android.R.attr.state_checked}, new ColorDrawable(Color.WHITE));
        sld.addState(new int[]{android.R.attr.state_checked}, drawable);
        return sld;
    }


    @Override
    public void renderSubForm(String key) {
        JSONObject formObject = FormWrapper.getFormSchema().optJSONObject("fields");
        updateWidgetOrder();

        JSONArray subFormArray = formObject.optJSONArray(mFieldName + "_" + key);
        if (subFormArray != null && isChecked) {
            for (int i = 0; i < subFormArray.length(); ++i) {
                JSONObject fieldsObject = subFormArray.optJSONObject(i);
                if (fieldsObject != null) {
                    String name = fieldsObject.optString("name");
                    String label = fieldsObject.optString("label");
                    AbstractWidget mFormWidget = mFormWrapper.getWidget(mContext, name, fieldsObject, label, false, null, mFormWidgetList,
                            mFormWidgetMap);
                    if (fieldsObject.has(FormWrapper.SCHEMA_KEY_HINT))
                        mFormWidget.setHint(fieldsObject.optString(FormWrapper.SCHEMA_KEY_HINT));
                    try {
                        mFormWidgetList.add(elementOrder + i + 1, mFormWidget);
                        FormWrapper.setRegistryByProperty(mFieldName, FormWrapper.getFormSchema().optJSONObject(mFieldName), (!key.equals("")) ? mFieldName + "_" + key : null, "MultiCheckbox", elementOrder + i + 1);
                    } catch (IndexOutOfBoundsException e) {
                        Log.d("Exception  Adding", e.getMessage());
                    }
                    mFormWidgetMap.put(name, mFormWidget);
                }
            }
        } else {
            keyName = mFieldName + "_" + key;
            clearSubForm(mFieldName);
        }
        mFormWrapper.resetFormWrapper();

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
        JSONObject multiChild = FormWrapper.getRegistryByProperty(name, "multiChild", "multicheckbox");
        if (multiChild != null) {
            int order = multiChild.optInt(keyName, -1);
            if (order > 0) {
                mFormWidgetList.remove(order);
            }
            multiChild.remove(keyName);
            multiChild = FormWrapper.updateMultiChild(multiChild, -1, order);
            FormWrapper.setMultiChild(name, "multiChild", multiChild);
        }
    }
}
