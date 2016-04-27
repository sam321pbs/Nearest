package com.example.sammengistu.nearest.dialogs;

import com.example.sammengistu.nearest.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View titleDialog = getActivity().getLayoutInflater()
            .inflate(R.layout.sort_dialog, null);

        mTimeRadioButton = (RadioButton) titleDialog.findViewById(R.id.show_by_time);
        mDistanceRadioButton = (RadioButton) titleDialog.findViewById(R.id.show_by_distance);

        mDistanceRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mTimeRadioButton.setChecked(false);
                }
            }
        });

        mTimeRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mDistanceRadioButton.setChecked(false);
                }
            }
        });


        return new AlertDialog.Builder(getActivity())
            .setView(titleDialog)
            .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (mDistanceRadioButton.isChecked()){
                        sendResult(SELECTED_SORT_METHOD_SEND_CODE, SELECTED_SORT_METHOD_DISTANCE);
                    } else {
                        sendResult(SELECTED_SORT_METHOD_SEND_CODE, SELECTED_SORT_METHOD_TIME);
                    }

                }
            })
            .create();
    }

    private void sendResult(int resultCode, int sortInt) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(SELECTED_SORT_METHOD, sortInt);

        getTargetFragment()
            .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}