package com.example.sammengistu.nearest.dialogs;

import com.example.sammengistu.nearest.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
    OnMyDialogResult mDialogResult; // the callback

    private EditText mTypedTitleEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View titleDialog = getActivity().getLayoutInflater()
            .inflate(R.layout.address_title_dialog, null);

        mTypedTitleEditText = (EditText) titleDialog.findViewById(R.id.type_title_edit_text);

        return new AlertDialog.Builder(getActivity())
            .setView(titleDialog)
            .setPositiveButton("Ok", new OnClickOkListener())
            .create();
    }

    private class OnClickOkListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (mTypedTitleEditText.getText().toString().equals("")) {
                mDialogResult.finish(mTypedTitleEditText.getText().toString());
            } else {
                mDialogResult.finish("");
            }
        }
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }


    public interface OnMyDialogResult{
        void finish(String result);
    }

//    private void sendResult(int resultCode) {
//        if (getTargetFragment() == null) {
//            return;
//        }
//
//        Intent intent = new Intent();
//        intent.putExtra(TYPED_TITLE_STRING, mTypedTitle);
//
//        getTargetFragment()
//            .onActivityResult(getTargetRequestCode(), resultCode, intent);
//    }
}
