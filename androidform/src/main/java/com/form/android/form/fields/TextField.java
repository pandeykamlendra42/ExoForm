package com.form.android.form.fields;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.form.android.R;
import com.form.android.adaptors.AutoCompleterAdapter;
import com.form.android.form.helper.AbstractWidget;
import com.form.android.form.helper.FormWrapper;
import com.form.android.ui.WidgetLayoutParams;
import com.form.android.utils.InputTypeUtil;

import org.json.JSONObject;

/**
 * @TextField is used to inflate the fields for the Edit text with the rich input types
 * like number, phone, location, textarea etc.
 */

public class TextField extends AbstractWidget implements TextWatcher, AdapterView.OnItemClickListener,
        View.OnClickListener {

    public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private final String ELEMENT_TAG = "TextField_";
    private final String LOCATION_ELEMENT_TAG = "TextField_";
    // Member Variables.
    private Context mContext;
    private EditText etFieldValue;
    private AppCompatAutoCompleteTextView actFieldValue;
    private String mFieldName, autoCompleterURL;
    private JSONObject jsonObjectProperty;
    private boolean isAutoComplete;

    /**
     * Public constructor to inflate form field For the edit text.
     *
     * @param context            Context of calling class.
     * @param property           Property of the field.
     * @param jsonObjectProperty Json object of the selected property.
     * @param description        Description of the field.
     * @param hasValidator       True if the field has validation (Compulsory field).
     * @param inputType          Input Type of the field.
     * @param value              Value of the field.
     */
    public TextField(Context context, String property, JSONObject jsonObjectProperty,
                     String description, boolean hasValidator, String inputType, String value) {

        super(context, property, hasValidator, jsonObjectProperty.optString("error", context.getResources().getString(R.string.widget_error_msg)));

        // Initialize member variables.
        mContext = context;
        mFieldName = property;
        this.jsonObjectProperty = jsonObjectProperty;


        // Inflate the field view layout.
        View inflateView;
        // Inflate the field view layout.
        if (FormWrapper.getLayoutType() == 3) {
            inflateView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.element_type_text_field_3, null);
        } else if (FormWrapper.getLayoutType() == 2) {
            inflateView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.element_type_text_field_2, null);
        } else {
            inflateView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.element_type_text_field_1, null);
        }
        getViews(inflateView, description);
        setInputType(inputType);
        inflateView.setTag(mFieldName);
        setValue(value);
        _layout.addView(inflateView);

    }

    /**
     * Method to get views from the form layout and set data in views..
     *
     * @param configFieldView View which is inflated.
     * @param description     Description of the field.
     */
    private void getViews(View configFieldView, String description) {

        // Getting label, description and field value views.
        TextView tvLabel = configFieldView.findViewById(R.id.view_label);
        tvLabel.setTypeface(Typeface.DEFAULT_BOLD);
        TextView tvDescription = configFieldView.findViewById(R.id.view_description);
        etFieldValue = configFieldView.findViewById(R.id.field_value);
        etFieldValue.setTag(ELEMENT_TAG + mFieldName);
        actFieldValue = configFieldView.findViewById(R.id.location_field_value);
        actFieldValue.setVisibility(View.GONE);
        actFieldValue.setTag(LOCATION_ELEMENT_TAG + mFieldName);
        etFieldValue.setVisibility(View.VISIBLE);


        if (FormWrapper.getLayoutType() != 2 && jsonObjectProperty.optString("label") != null && !jsonObjectProperty.optString("label").isEmpty()) {
            tvLabel.setVisibility(View.VISIBLE);
            etFieldValue.setPadding(0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp), 0, 0);
            tvLabel.setText(jsonObjectProperty.optString("label"));
        } else {
            tvLabel.setVisibility(View.GONE);
        }

        // Showing description field if it is coming in response.
        if (description != null && !description.isEmpty()) {
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(description);
        } else {
            tvDescription.setVisibility(View.GONE);
        }

        // Setting up the max length on the edit text if it is coming in response.
        if (jsonObjectProperty.has("maxLength") && jsonObjectProperty.optInt("maxLength") != 0) {
            etFieldValue.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(jsonObjectProperty.optInt("maxLength")) {
                        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                            CharSequence res = super.filter(source, start, end, dest, dstart, dend);
                            if (res != null) {
                                etFieldValue.setError("Limit exceeded! Max number of "
                                        + jsonObjectProperty.optInt("maxLength") + " characters allowed.");
                            }
                            return res;
                        }
                    }
            });
        }
        String hint = jsonObjectProperty.optString("hint");
        if (jsonObjectProperty.optBoolean("autoCompleter", false)) {
            isAutoComplete = true;
            autoCompleterURL = jsonObjectProperty.optString("autoCompleterURL", PLACES_API_BASE
                    + TYPE_AUTOCOMPLETE + OUT_JSON + "?key="
                    + mContext.getResources().getString(R.string.places_api_key));
            if (autoCompleterURL != null) {
                actFieldValue.setVisibility(View.VISIBLE);
                etFieldValue.setVisibility(View.GONE);
                actFieldValue.setHint((hint != null) ? hint : jsonObjectProperty.optString("label"));
                actFieldValue.setAdapter(new AutoCompleterAdapter(mContext, R.layout.element_type_heading, autoCompleterURL));
            }
        }
    }

    /***
     * Method to check the text type and set the input type and max lines accordingly.
     *
     * @param inputType Input Type of the field.
     */
    private void setInputType(String inputType) {

        int type = InputTypeUtil.getInputType(inputType.toLowerCase());
        etFieldValue.setInputType(type);
        switch (type) {
            case InputTypeUtil.TYPE_CLASS_TEXT_AREA:
                etFieldValue.setHeight(mContext.getResources().getDimensionPixelSize(R.dimen.dimen_80dp));
                etFieldValue.setMaxHeight(mContext.getResources().getDimensionPixelSize(R.dimen.dimen_80dp));
                etFieldValue.setSingleLine(false);
                etFieldValue.setGravity(Gravity.START | Gravity.TOP);
                break;
            case InputType.TYPE_NUMBER_VARIATION_PASSWORD:
            case InputType.TYPE_TEXT_VARIATION_PASSWORD:
            case InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD:
                etFieldValue.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
            case InputTypeUtil.TYPE_CLASS_TEXT_LOCATION:
                etFieldValue.setVisibility(View.GONE);
                actFieldValue.setVisibility(View.VISIBLE);
                autoCompleterURL = PLACES_API_BASE
                        + TYPE_AUTOCOMPLETE + OUT_JSON + "?key="
                        + mContext.getResources().getString(R.string.places_api_key);
                actFieldValue.setAdapter(new AutoCompleterAdapter(mContext, R.layout.element_type_heading, autoCompleterURL));
                isAutoComplete = true;
                break;
            default:
                etFieldValue.setGravity(Gravity.CENTER_VERTICAL);
                etFieldValue.setMaxLines(1);
                break;
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence searchText, int start, int before, int count) {

        etFieldValue.setError(null);
        actFieldValue.setError(null);

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public String getValue() {

        if (isAutoComplete) {
            return actFieldValue.getText().toString();
        } else {
            return etFieldValue.getText().toString();
        }
    }

    @Override
    public void setValue(String value) {

        if (isAutoComplete) {
            actFieldValue.setText(value);
        } else {
            WidgetLayoutParams.setEditText(etFieldValue, value);
        }
    }

    @Override
    public void setHint(String hint) {
        // Showing hint on the respective views..
        if (hint != null) {
            if (isAutoComplete) {
                actFieldValue.setHint(hint);
            } else {
                etFieldValue.setHint(hint);
            }
        }
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        // Showing error message on error view.
        if (errorMessage != null) {
            if (isAutoComplete) {
                actFieldValue.requestFocus();
                actFieldValue.setError(errorMessage);
            } else {
                etFieldValue.requestFocus();
                etFieldValue.setError(errorMessage);
            }
        }
    }

    @Override
    public void onClick(View view) {

    }
}
