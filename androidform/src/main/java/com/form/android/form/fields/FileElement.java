package com.form.android.form.fields;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.form.android.R;
import com.form.android.form.helper.AbstractWidget;
import com.form.android.form.helper.FormWrapper;
import com.form.android.listeners.OnRequestMediaListener;

import org.json.JSONObject;

/**
 * @FileElement is used to inflate the fields for the attachment picker (Music, Photo, Video etc.),
 */

public class FileElement extends AbstractWidget implements View.OnClickListener {

    private final String ELEMENT_TAG = "FILE_";
    private Context mContext;
    private View mConfigFieldView;
    private EditText etFieldValue;
    private TextView tvLabel, tvError;
    private String mFileType, mLabel, mFieldName;
    private Drawable mDrawableIcon;
    private OnRequestMediaListener mOnRequestMediaListener;
    private JSONObject joProperty;


    /**
     * Public constructor to inflate form field for attachment(music, photo, video etc.) picker.
     *
     * @param context    Context of calling class.
     * @param name       Property of the field.
     * @param joProperty JSONObject Of the field.
     */
    public FileElement(Context context, final String name, JSONObject joProperty) {
        super(context, name, joProperty.optBoolean("required", false), joProperty.optString("error", context.getResources().getString(R.string.widget_error_msg)));

        // Initializing member variables.
        this.mContext = context;
        this.mFieldName = name;
        this.joProperty = joProperty;
        this.mLabel = joProperty.optString("label");
        this.mFileType = joProperty.optString("fileType", "photo");
        this.mFileType = mFileType == null ? "photo" : mFileType;
        try {
            mOnRequestMediaListener = (OnRequestMediaListener) mContext;
        } catch (ClassCastException cce) {
            cce.printStackTrace();
        }
        // Inflate the field view layout.
        inflateView();
    }

    /**
     * Method to inflate view according to field type.
     */
    private void inflateView() {

        // Inflate the field view layout.
        if (FormWrapper.getLayoutType() == 2) {
            mConfigFieldView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.element_type_select_2, null);
        } else {
            mConfigFieldView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.element_type_select_1, null);
        }
        setFileTypeDrawable(mFileType);
        getViews();
        mConfigFieldView.setTag(mFieldName);
        _layout.addView(mConfigFieldView);
    }

    /**
     * Method to set drawable according to file type
     *
     * @param fileType String
     */
    private void setFileTypeDrawable(String fileType) {
        switch (fileType) {
            case "photo":
                mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_photo_camera_white_24dp);
                break;
            case "video":
                mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_video_file_24dp);
                break;
            default:
                mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_attach_file_24dp);
        }
    }

    /**
     * Method to get views from the form layout and set data in views..
     */
    private void getViews() {

        // Getting label, description and field value views.
        tvLabel = mConfigFieldView.findViewById(R.id.view_label);
        if (FormWrapper.getLayoutType() != 2) {
            tvLabel.setTypeface(Typeface.DEFAULT_BOLD);
        }
        tvLabel.setText(mLabel != null ? mLabel : getDisplayText());
        TextView tvDescription = mConfigFieldView.findViewById(R.id.view_description);
        etFieldValue = mConfigFieldView.findViewById(R.id.field_value);
        tvError = mConfigFieldView.findViewById(R.id.error_view);
        // Showing the attachment picker/date picker options.
        etFieldValue.setVisibility(View.VISIBLE);
        etFieldValue.setTag(ELEMENT_TAG + mFieldName);
        // Showing the right drawable icon on the field value view.
        if (mDrawableIcon != null) {
            mDrawableIcon.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.light_gray),
                    PorterDuff.Mode.SRC_ATOP));
            etFieldValue.setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawableIcon, null);
            etFieldValue.setCompoundDrawablePadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_6dp));
        }

        // Showing description for the picker.
        if (joProperty != null && joProperty.has("description") && joProperty.optString("description") != null) {
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(joProperty.optString("description"));
            tvDescription.setPadding(0, 0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp));
        } else {
            tvDescription.setVisibility(View.GONE);
        }

        // Setting up click listener on form view.
        etFieldValue.setOnClickListener(this);
        mConfigFieldView.findViewById(R.id.form_main_view).setOnClickListener(this);


    }


    @Override
    public void setHint(String value) {

        // Showing the hint as a label.
        if (value != null && !value.isEmpty()) {
            tvLabel.setText(value);
            etFieldValue.setHint(value);
        }
    }

    @Override
    public String getValue() {
        return "";
    }

    @Override
    public void setValue(String value) {
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
    public void onClick(View v) {

        // Hiding error view when the attachment option is clicked.
        tvError.setError(null);
        tvError.setVisibility(View.GONE);
        // Perform action on onClick according to inflated view type.
        if (mOnRequestMediaListener != null) {
            mOnRequestMediaListener.onRequestMedia(mFileType, mFieldName);
        }
    }

}
