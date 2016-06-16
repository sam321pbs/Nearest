package com.example.sammengistu.nearest.dialogs;

import com.example.sammengistu.nearest.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;


public class TakeMeThereDialog extends DialogFragment {

    public static final String ADDRESS_TAKE_ME = "Address take me";
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View titleDialog = getActivity().getLayoutInflater()
            .inflate(R.layout.dialog_get_directions, null);

        TextView textView = (TextView) titleDialog.findViewById(R.id.address_get_direction);
        textView.setText(getArguments().getString(ADDRESS_TAKE_ME));

        return new AlertDialog.Builder(getActivity())
            .setView(titleDialog)
            .setPositiveButton("Take me there", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .setNegativeButton("Cancel", null)
            .setOnCancelListener(null)
            .create();
    }

    public static TakeMeThereDialog newInstance(String address){
        Bundle bundle = new Bundle();
        bundle.putString(ADDRESS_TAKE_ME, address);

        TakeMeThereDialog takeMeThereDialog = new TakeMeThereDialog();
        takeMeThereDialog.setArguments(bundle);

        return takeMeThereDialog;
    }
}
