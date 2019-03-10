package com.form.android.form.fields;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.form.android.R;
import com.form.android.form.helper.AbstractWidget;
import com.form.android.form.helper.FormWrapper;
import com.form.android.listeners.OnRenderSubForm;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SwitchElement extends AbstractWidget implements OnRenderSubForm {

    private final String ELEMENT_TAG = "SwitchElement_";
    private Context mContext;
    private String mFieldName;
    private Switch sElement;
    private int elementOrder = 0;
    private ArrayList<AbstractWidget> mFormWidgetList;
    private Map<String, AbstractWidget> mFormWidgetMap;
    private FormWrapper mFormWrapper;

    public SwitchElement(Context context, FormWrapper formWrapper, String property, boolean hasValidator, JSONObject joProperty, ArrayList<AbstractWidget> widgets
            , Map<String, AbstractWidget> map) {
        super(context, property, hasValidator, joProperty.optString("error", context.getResources().getString(R.string.widget_error_msg)));

        // Initialize member variables.
        mContext = context;
        mFieldName = property;
        mFormWidgetList = widgets;
        this.mFormWidgetMap = map;
        this.mFormWrapper = formWrapper;

        // Inflate the field view layout.
        View inflateView;
        if (FormWrapper.getLayoutType() == 1) {
            inflateView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.element_type_switch_1, null);
        } else {
            inflateView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.element_type_switch_2, null);
        }
        getViews(inflateView, joProperty);
        inflateView.setTag(mFieldName);
        setValue(joProperty.optString("value", "0"));
        _layout.addView(inflateView);

    }

    private void getViews(View configFieldView, final JSONObject joProperty) {
        TextView tvLabel = configFieldView.findViewById(R.id.view_label);
        tvLabel.setTypeface(Typeface.DEFAULT_BOLD);
        if (joProperty.optString("label") != null && !joProperty.optString("label").isEmpty()) {
            tvLabel.setVisibility(View.VISIBLE);
            tvLabel.setText(joProperty.optString("label"));
        } else {
            tvLabel.setVisibility(View.GONE);
            tvLabel.setPadding(0, 0, 0, 0);
        }
        sElement = configFieldView.findViewById(R.id.sElement);
        sElement.setTag(ELEMENT_TAG + mFieldName);
        sElement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (joProperty.optBoolean("hasSubForm")) {
                    renderSubForm(getValue());
                }
            }
        });

    }

    @Override
    public String getValue() {
        return sElement.isChecked() ? "1" : "0";
    }

    @Override
    public void setValue(String value) {
        if (value != null && !value.isEmpty() && value.equals("1")) {
            sElement.setChecked(true);
        } else {
            sElement.setChecked(false);
        }
    }



    @Override
    public void renderSubForm(String key) {
        JSONObject formObject = FormWrapper.getFormSchema().optJSONObject("fields");
        if (formObject == null) {
            return;
        }
        updateWidgetOrder();
        clearSubForm(mFieldName);
        updateWidgetOrder();
        JSONArray subFormArray = formObject.optJSONArray(mFieldName + "_" + key);
        if (subFormArray != null) {
            String append = FormWrapper.getAttribByProperty(mFieldName, "append", null);
            int appendValue = (append != null && !append.isEmpty()) ? Integer.parseInt(append) : 1;
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
                        mFormWidgetList.add(elementOrder + i + appendValue, mFormWidget);

                    } catch (IndexOutOfBoundsException e) {
                        Log.d("Exception  Adding", e.getMessage());
                    }
                    mFormWidgetMap.put(name, mFormWidget);
                }
            }
        }
        mFormWrapper.resetFormWrapper();
        FormWrapper.setRegistryByProperty(mFieldName, FormWrapper.getFormSchema().optJSONObject(mFieldName), (!key.equals("")) ? mFieldName + "_" + key : null, "multiOptions", 0);

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
        String append = FormWrapper.getAttribByProperty(name, "append", null);
        int appendValue = (append != null && !append.isEmpty()) ? Integer.parseInt(append) : 1;
        JSONObject formObject = FormWrapper.getFormSchema().optJSONObject("fields");
        String child = FormWrapper.getRegistryByProperty(name, "child");


        JSONObject multiChild = FormWrapper.getRegistryByProperty(name, "multiChild", "multicheckbox");
        if (multiChild != null) {
            Iterator<String> keys = multiChild.keys();

            List<Integer> keyList = new ArrayList<>();
            while (keys.hasNext()) {
                String key = keys.next();
                int keyOrder = multiChild.optInt(key);
                keyList.add(keyOrder);
            }
            Collections.sort(keyList);
            for (int i = keyList.size() - 1; i >= 0; i--) {
                int keyOrder = keyList.get(i);
                if (keyOrder > 0 && mFormWidgetList.size() > keyOrder) {
                    mFormWidgetList.remove(keyOrder);
                }
            }
            FormWrapper.setMultiChild(name, "multiChild", multiChild);
        }


        if (child != null && !child.trim().equals("") && formObject.optJSONArray(child) != null) {
            JSONArray subFormArray = formObject.optJSONArray(child);
            for (int i = subFormArray.length() - 1; i >= 0; --i) {
                if (subFormArray.optJSONObject(i) != null && subFormArray.optJSONObject(i).optBoolean("hasSubForm", false)) {
                    clearSubForm(subFormArray.optJSONObject(i).optString("name"));
                }
                try {
                    appendValue = (appendValue == 0) ? -1 : appendValue;
                    mFormWidgetList.remove(elementOrder + i + appendValue);
                } catch (IndexOutOfBoundsException e) {
                    Log.d("Exception Removing ", e.getMessage());
                }
            }
        }
    }
}
