package com.example.sammengistu.nearest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;

/**
 * Created by SamMengistu on 4/21/16.
 */
public class TypeTitleDialog extends DialogFragment {

    public static final int TYPED_TITLE = 11;
    public static final String TYPED_TITLE_STRING = "Typed Title";

    private String mTypedTitle = "";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View titleDialog = getActivity().getLayoutInflater()
            .inflate(R.layout.address_title_dialog, null);

        final EditText typedTitleEditText = (EditText)titleDialog.findViewById(R.id.type_title_edit_text);


        return new AlertDialog.Builder(getActivity())
            .setView(titleDialog)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mTypedTitle = typedTitleEditText.getText().toString();
                    sendResult(TYPED_TITLE);
                }
            })
            .create();
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(TYPED_TITLE_STRING, mTypedTitle);

        getTargetFragment()
            .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
