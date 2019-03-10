package com.form.android.form.fields;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.form.android.R;
import com.form.android.adaptors.OptionAdapter;
import com.form.android.form.helper.AbstractWidget;
import com.form.android.form.helper.FormWrapper;
import com.form.android.listeners.OnRenderSubForm;
import com.form.android.model.Option;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @Select is used to inflate the view for the radio buttons and spinner item which contains the multi-options.
 * Bottom sheet view is used to show the options.
 */

public class Select extends AbstractWidget implements View.OnClickListener, OnRenderSubForm {


    //Member variables.
    private final String ELEMENT_TAG = "Select_";
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private EditText etFieldValue;
    private TextView tvError;
    private List<Option> mOptionsItemList = new ArrayList<>();
    private OptionAdapter mSheetAdapter;
    private BottomSheetDialog mBottomSheetDialog;
    private String mFieldName, mFieldValue = "";
    private JSONObject jsonObjectOptions, joProperty;
    private ArrayList<AbstractWidget> mFormWidgetList;
    private boolean mIsOptionWithIcon = false;
    private int elementOrder = 0;
    private FormWrapper mFormWrapper;


    /**
     * Public constructor to inflate form field For the radio button items.
     *
     * @param context            Context of calling class.
     * @param formWrapper        Forms container instance
     * @param property           Property of the field.
     * @param options            Object with multi options.
     * @param label              Label of the field.
     * @param hasValidator       True if the field has validation (Compulsory field).
     * @param description        Description of the field.
     * @param jsonObjectProperty Json object of the selected property.
     */
    public Select(final Context context, FormWrapper formWrapper, String property, JSONObject options, String label,
                  boolean hasValidator, String description, JSONObject jsonObjectProperty) {
        super(context, property, hasValidator, jsonObjectProperty.optString("error", context.getResources().getString(R.string.widget_error_msg)));

        // Initializing member variables.
        mContext = context;
        mFieldName = property;
        jsonObjectOptions = options;
        this.joProperty = jsonObjectProperty;
        this.mFormWrapper = formWrapper;

        initializeConstructorValues(label, description);

    }

    /**
     * Public constructor to inflate form field For the Spinner items.
     *
     * @param context            Context of calling class.
     * @param formWrapper        Forms container instance
     * @param property           Property of the field.
     * @param options            Object with multi options.
     * @param label              Label of the field.
     * @param hasValidator       True if the field has validation (Compulsory field).
     * @param description        Description of the field.
     * @param jsonObjectProperty Json object of the selected property.
     * @param isOptionWithIcon   True if need to show options with the icon.
     */
    public Select(final Context context, FormWrapper formWrapper, String property, JSONObject options, String label,
                  boolean hasValidator, String description, JSONObject jsonObjectProperty, boolean isOptionWithIcon) {

        super(context, property, hasValidator, jsonObjectProperty.optString("error", context.getResources().getString(R.string.widget_error_msg)));

        // Initializing member variables.
        this.mContext = context;
        this.mFieldName = property;
        this.jsonObjectOptions = options;
        this.joProperty = jsonObjectProperty;
        this.mFormWrapper = formWrapper;
        this.mFormWidgetList = mFormWrapper.getElementWidgets();
        this.mIsOptionWithIcon = isOptionWithIcon;

        initializeConstructorValues(label, description);
    }


    /**
     * Method to initialize the constructor values and  inflate the field view accordingly.
     *
     * @param label       Label of the field.
     * @param description Description of the field.
     */
    private void initializeConstructorValues(String label, String description) {
        // Initializing member variables.
        mLayoutInflater = ((Activity) mContext).getLayoutInflater();

        // Fetching all keys from json object and adding data into list.
        Iterator<?> keys = jsonObjectOptions.keys();
        if (keys != null) {
            while (keys.hasNext()) {
                String key = (String) keys.next();
                mOptionsItemList.add(new Option(getLabelFromKey(key), key));
            }
        }


        // Setting up the adapter.
        setAdapter();
        // Inflate the field view layout.
        View configFieldView;
        if (FormWrapper.getLayoutType() == 2) {
            configFieldView = mLayoutInflater.inflate(R.layout.element_type_select_2, null);
        } else {
            configFieldView = mLayoutInflater.inflate(R.layout.element_type_select_1, null);
        }
        getViews(configFieldView, label, description);
        _layout.addView(configFieldView);
        configFieldView.setTag(mFieldName);

        // Setting up the value in the field value view when it is coming in response.
        if (joProperty != null && joProperty.length() > 0
                && jsonObjectOptions != null && jsonObjectOptions.length() > 0 && etFieldValue != null
                && joProperty.optString("value") != null && !joProperty.optString("value").isEmpty()) {
            String value = joProperty.optString("value");
            etFieldValue.setText(jsonObjectOptions.optString(value));
            mFieldValue = value;
        }

    }

    /**
     * Method to set adapter for the list items.
     */
    private void setAdapter() {
        if (!mIsOptionWithIcon) {
            mSheetAdapter = new OptionAdapter(mContext, mOptionsItemList);
        }
        mSheetAdapter.setOnItemClickListener(new OptionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Option item, int position) {
                mBottomSheetDialog.dismiss();
                etFieldValue.setText(item.getName());
                tvError.setError(null);
                tvError.setVisibility(View.GONE);
                // Not performing any action when the same option is selected.
                if (!mFieldValue.equals(item.getKey())) {
                    mFieldValue = item.getKey();

                    // Performing view creation on multi option selection for the specific modules.
                    if (joProperty != null
                            && (joProperty.optString("type").equals(FormWrapper.SCHEMA_KEY_SELECT)
                            || joProperty.optString("type").equals(FormWrapper.SCHEMA_KEY_SELECT_UPPER))) {

                        if (joProperty.optBoolean("hasSubForm", false)) {
                            renderSubForm(item.getKey());
                        }
                    }
                }
            }
        });
    }

    /**
     * Method to get views from the form layout and set data in views..
     *
     * @param configFieldView View which is inflated.
     * @param label           Label of the field.
     * @param description     Description of the field.
     */
    private void getViews(View configFieldView, String label, String description) {

        // Getting label, description and field value views.
        TextView tvLabel = configFieldView.findViewById(R.id.view_label);
        if (FormWrapper.getLayoutType() != 2) {
            tvLabel.setTypeface(Typeface.DEFAULT_BOLD);
        }
        TextView tvDescription = configFieldView.findViewById(R.id.view_description);
        etFieldValue = configFieldView.findViewById(R.id.field_value);
        etFieldValue.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        tvError = configFieldView.findViewById(R.id.error_view);

        // Setting up click listener on form view.
        etFieldValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renderOptions();
            }
        });
        configFieldView.findViewById(R.id.form_main_view).setOnClickListener(this);

        etFieldValue.setTag(ELEMENT_TAG + mFieldName);
        // Setting up data in views.
        tvLabel.setText(label);

        // Showing description field if it is coming in response.
        if (description != null && !description.isEmpty()) {
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(description);
        } else {
            tvDescription.setVisibility(View.GONE);
        }


    }

    private void renderOptions() {
        View inflateView = mLayoutInflater.inflate(R.layout.list_options, null);
        RecyclerView recyclerView = inflateView.findViewById(R.id.recycler_view);

        recyclerView.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(mSheetAdapter);
        mSheetAdapter.setDefaultKey(mFieldValue);
        mBottomSheetDialog = new BottomSheetDialog(mContext);
        mBottomSheetDialog.setContentView(inflateView);
        mBottomSheetDialog.show();
    }

    /**
     * Method to get label associated with the key.
     *
     * @param key Key of the object.
     * @return Returns the label.
     */
    private String getLabelFromKey(String key) {
        return jsonObjectOptions.optString(key);
    }

    @Override
    public String getValue() {
        // Returning field value.
        return mFieldValue;
    }

    @Override
    public void setValue(String value) {

        // Showing the field value when it is coming in response.
        if (value != null && etFieldValue != null && jsonObjectOptions != null
                && jsonObjectOptions.length() > 0) {
            etFieldValue.setText(getLabelFromKey(value));
            mFieldValue = value;
        }
    }

    @Override
    public void setErrorMessage(String errorMessage) {

        // Showing error message on error view.
        if (tvError != null && errorMessage != null) {
            tvError.setVisibility(View.VISIBLE);
            tvError.requestFocus();
            tvError.setError(errorMessage);
        }
    }

    @Override
    public void onClick(View view) {

    }


    @Override
    public void renderSubForm(String key) {
        JSONObject formObject = FormWrapper.getFormSchema().optJSONObject("fields");
        if (formObject == null) {
            return;
        }
        AbstractWidget mFormWidget;
        // setting the current order of the widget
        updateWidgetOrder();
        clearSubForm(mFieldName);
        // Update this widget order after deleting it's child form
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
                    mFormWidget = mFormWrapper.getWidget(mContext, name, fieldsObject, label, false,
                            null, mFormWidgetList, mFormWrapper.getWidgetsMap());
                    if (fieldsObject.has(FormWrapper.SCHEMA_KEY_HINT))
                        mFormWidget.setHint(fieldsObject.optString(FormWrapper.SCHEMA_KEY_HINT));
                    try {
                        mFormWrapper.getElementWidgets().add(elementOrder + i + appendValue, mFormWidget);

                    } catch (IndexOutOfBoundsException e) {
                        Log.d("Exception  Adding", e.getMessage());
                    }
                    mFormWrapper.getWidgetsMap().put(name, mFormWidget);
                }
            }
        }
        mFormWrapper.resetFormWrapper();
        FormWrapper.setRegistryByProperty(mFieldName, FormWrapper.getFormSchema().optJSONObject(mFieldName),
                (!key.equals("")) ? mFieldName + "_" + key : null, "multiOptions", 0);
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
        //TODO, Need To Optimize The Code
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
