package com.example.sammengistu.nearest.dialogs;

import com.example.sammengistu.nearest.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by SamMengistu on 4/26/16.
 */
public class SortDialog extends DialogFragment {

    public static final int SELECTED_SORT_METHOD_SEND_CODE = 12;
    public static final String SELECTED_SORT_METHOD = "Sort method";
    public static final int SELECTED_SORT_METHOD_DISTANCE = 0;
    public static final int SELECTED_SORT_METHOD_TIME = 1;

    private RadioButton mTimeRadioButton;
    private RadioButton mDistanceRadioButton;
    private TextView mTimeTextView;
    private TextView mDistanceTextView;

    public interface SortListener {
        public void onDialogPositiveClick(DialogFragment dialog, boolean showDistance);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    SortListener mListener;


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SortListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View titleDialog = getActivity().getLayoutInflater()
            .inflate(R.layout.sort_dialog, null);

        mTimeRadioButton = (RadioButton) titleDialog.findViewById(R.id.show_by_time);
        mDistanceRadioButton = (RadioButton) titleDialog.findViewById(R.id.show_by_distance);
        mTimeTextView = (TextView) titleDialog.findViewById(R.id.show_by_time_text_view);
        mDistanceTextView = (TextView) titleDialog.findViewById(R.id.show_by_distance_text_view);

        mTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimeRadioButton.setChecked(true);
                if (mTimeRadioButton.isChecked()) {
                    mDistanceRadioButton.setChecked(false);
                }
            }
        });

        mDistanceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDistanceRadioButton.setChecked(true);
                if (mDistanceRadioButton.isChecked()) {
                    mTimeRadioButton.setChecked(false);
                }
            }
        });

        mDistanceRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mTimeRadioButton.setChecked(false);
                }
            }
        });

        mTimeRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mDistanceRadioButton.setChecked(false);
                }
            }
        });


        return new AlertDialog.Builder(getActivity())
            .setView(titleDialog)
            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    // Send the positive button event back to the host activity
                    mListener.onDialogPositiveClick(SortDialog.this, mDistanceRadioButton.isChecked());


                }
            })
            .setOnCancelListener(null)
            .create();
    }
}