package com.form.android.form.fields;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.form.android.R;
import com.form.android.form.helper.AbstractWidget;
import com.form.android.form.helper.FormWrapper;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @DateTime widget is used to show render views of date, time & datetime elements
 * in the spinner and calender view.
 */

public class DateTime extends AbstractWidget implements View.OnClickListener {

    // Member Variables.
    private final String ELEMENT_TAG = "DateTime_";
    private Context mContext;
    private View mConfigFieldView;
    private EditText etFieldValue;
    private TextView tvLabel, tvError;
    private String mInputType, mLabel, mFieldName;
    private String formatHourString, hourString, minuteString, yearString, monthString, dateString, strDateTime,
            dateTag;
    private JSONObject joProperty;
    private int mDate, mMonth, mYear;
    private boolean isSpinnerTypePicker;
    private long mMinDate, mMaxDate;

    /**
     * Public constructor to inflate form field for attachment(music, photo, video etc.) picker.
     *
     * @param context    Context of calling class.
     * @param name       Property of the field.
     * @param joProperty JSONObject Of the field.
     */
    public DateTime(Context context, final String name, JSONObject joProperty) {
        super(context, name, joProperty.optBoolean("required", false), joProperty.optString("error", context.getResources().getString(R.string.widget_error_msg)));

        // Initializing member variables.
        this.mContext = context;
        this.mFieldName = name;
        this.joProperty = joProperty;
        this.mLabel = joProperty.optString("label");
        this.mInputType = joProperty.optString("inputType", "datetime");
        // 2018-09-21 08:45
        if (joProperty.has("minDate")) {
            setMinDate(joProperty.optString("minDate"));
        }
        if (joProperty.has("maxDate")) {
            setMaxDate(joProperty.optString("maxDate"));
        }

        this.isSpinnerTypePicker = joProperty.optBoolean("spinnerType", false);
        // Inflate the field view layout.
        inflateView();
    }

    /**
     * Method to inflate view according to field type.
     */
    private void inflateView() {

        // Inflate the field view layout.
        // Inflate the field view layout.
        if (FormWrapper.getLayoutType() == 2) {
            mConfigFieldView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.element_type_select_2, null);
        } else {
            mConfigFieldView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.element_type_select_1, null);
        }
        getViews();
        mConfigFieldView.setTag(mFieldName);
        _layout.addView(mConfigFieldView);
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
        Drawable mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_date_range_black_24dp);
        if (mInputType.equals("time")) {
            mDrawableIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_time_clock_24dp);
        }
        mDrawableIcon.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.light_gray),
                PorterDuff.Mode.SRC_ATOP));
        etFieldValue.setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawableIcon, null);
        etFieldValue.setCompoundDrawablePadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_6dp));

        // Showing description for the picker.
        if (joProperty != null && joProperty.optString("description") != null) {
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
        return etFieldValue.getText().toString();
    }

    @Override
    public void setValue(String value) {
        if (value != null) {
            etFieldValue.setText(value.replace("null", ""));
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
    public void onClick(View v) {
        // Hiding validation error view when the option is clicked.
        tvError.setError(null);
        tvError.setVisibility(View.GONE);
        if (isSpinnerTypePicker) {
            showSpinnerDateTimePicker(mContext, mInputType.equals("time"));
        } else {
            showDateTimeCalender(mContext, mInputType.equals("time"));
        }
    }


    /**
     * Method to show date or time picker according to response.
     *
     * @param context context of calling class.
     */

    public void showDateTimeCalender(Context context, final boolean isTimePicker) {

        mContext = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final DatePicker datePicker = new DatePicker(mContext);
        final TimePicker timePicker = new TimePicker(mContext);

        if (!isTimePicker) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                datePicker.setCalendarViewShown(false);
            }
            // Showing the recently selected value in date picker if the user recently selected.
            if (yearString != null && !yearString.isEmpty() && monthString != null && !monthString.isEmpty()) {
                datePicker.init(Integer.parseInt(yearString), Integer.parseInt(monthString) - 1, Integer.parseInt(dateString), null);
            } else {
                Calendar newCalendar = Calendar.getInstance();
                datePicker.init(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH), null);
            }
            builder.setTitle(mContext.getResources().getString(R.string.date));
            builder.setView(datePicker);
        } else {
            // Showing the recently selected value in time picker if the user recently selected.
            if (hourString != null && !hourString.isEmpty() && minuteString != null && !minuteString.isEmpty()) {
                timePicker.setCurrentHour(Integer.valueOf(hourString));
                timePicker.setCurrentMinute(Integer.valueOf(minuteString));
            }
            builder.setTitle(mContext.getResources().getString(R.string.time));
            builder.setView(timePicker);
        }
        if (mMinDate > 0) {
            datePicker.setMinDate(mMinDate);
        }
        if (mMaxDate > mMinDate) {
            datePicker.setMaxDate(mMaxDate);
        }
        builder.setPositiveButton(mContext.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Adding the date picker value in the text field.
                        if (!isTimePicker) {
                            evaluateDate(datePicker);
                        }

                        // Showing the time picker when the format is not of date type.
                        // and showing it only once.
                        if ((mInputType.equals("datetime")) && !isTimePicker) {
                            showDateTimeCalender(mContext, true);
                        } else if (isTimePicker) {
                            // Adding the time picker value in the text field if it selected.
                            evaluateTime(timePicker);
                        }
                        setValue(strDateTime);
                    }
                });

        builder.setNegativeButton(mContext.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }


    /**
     * This method is used to select date with spinner view.
     *
     * @param context
     */
    public void showSpinnerDateTimePicker(Context context, final boolean isTimePicker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.element_type_date_time_spinner, null);
        final DatePicker date = dialogView.findViewById(R.id.datePicker);
        final TimePicker time = dialogView.findViewById(R.id.timePicker);
        builder.setTitle(mContext.getResources().getString(R.string.date));
        if (isTimePicker) {
            date.setVisibility(View.GONE);
            time.setVisibility(View.VISIBLE);
            // Showing the recently selected value in time picker if the user recently selected.
            if (hourString != null && !hourString.isEmpty() && minuteString != null && !minuteString.isEmpty()) {
                time.setCurrentHour(Integer.valueOf(hourString));
                time.setCurrentMinute(Integer.valueOf(minuteString));
            }
            builder.setTitle(mContext.getResources().getString(R.string.time));
        } else if (mDate != 0) {
            date.init(mYear, mMonth - 1, mDate, null);
        } else {
            Calendar newCalendar = Calendar.getInstance();
            date.init(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH), null);
        }

        if (mMinDate > 0) {
            date.setMinDate(mMinDate);
        }
        if (mMaxDate > mMinDate) {
            date.setMaxDate(mMaxDate);
        }

        builder.setView(dialogView);
        builder.setPositiveButton(context.getResources().getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Showing the time picker when the format is not of date type.
                        // and showing it only once.
                        if (!isTimePicker) {
                            evaluateDate(date);
                        }
                        if (mInputType.equals("datetime") && !isTimePicker) {
                            showSpinnerDateTimePicker(mContext, true);
                        } else if (isTimePicker) {
                            evaluateTime(time);
                        }
                        setValue(strDateTime);
                    }
                });

        builder.setNegativeButton(context.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }

    /**
     * Calculating required date in the format yyyy-MM-dd from date picker.
     *
     * @param datePicker
     */
    private void evaluateDate(DatePicker datePicker) {
        mYear = datePicker.getYear();
        yearString = String.valueOf(mYear);
        mMonth = datePicker.getMonth() + 1;
        mDate = datePicker.getDayOfMonth();
        if (mMonth < 10) {
            monthString = "0" + mMonth;
        } else {
            monthString = "" + mMonth;
        }

        if (mDate < 10) {
            dateString = "0" + mDate;
        } else {
            dateString = "" + mDate;
        }

        strDateTime = yearString + "-" + monthString + "-" + dateString;
        dateTag = strDateTime;
    }

    /**
     * Calculating time in the required format HH:ss from time picker.
     *
     * @param timePicker
     */
    private void evaluateTime(TimePicker timePicker) {
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        if (hour < 10) {
            hourString = "0" + hour;
            formatHourString = "0" + hour;
        } else if (hour > 12) {
            formatHourString = "" + hour;
            hour -= 12;
            hourString = "" + hour;
        } else {
            hourString = "" + hour;
            formatHourString = "" + hour;
        }

        if (minute < 10) {
            minuteString = "0" + minute;
        } else {
            minuteString = "" + minute;
        }

        strDateTime = dateTag + " " + hourString + ":" + minuteString;
    }

    /**
     * Set minimum date for calender
     *
     * @param minDate
     */
    private void setMinDate(String minDate) {
        mMinDate = getDateMillis(minDate);

    }

    /**
     * Set maximum date for calender.
     *
     * @param maxDate
     */
    private void setMaxDate(String maxDate) {
        mMaxDate = getDateMillis(maxDate);

    }

    /**
     * Convert string date to milliseconds
     *
     * @param date
     * @return
     */
    private long getDateMillis(String date) {
        String format = "yyyy/MM/dd";
        if (date.indexOf("-") > 0) {
            format = "yyyy-MM-dd";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date mDate = sdf.parse(date);
            return mDate.getTime();
        } catch (ParseException e) {
            Log.e("Invalid Date Format", date);
            return 0;
        }

    }
}
