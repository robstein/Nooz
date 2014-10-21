package com.example.matt.myfirstapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;



/**
 * A simple {@link DialogFragment} subclass.
 */
public class NotificationDialogFragment extends DialogFragment {

    public boolean clickedOK;

    private String message;
    private DialogInterface.OnClickListener clickListener;

    public NotificationDialogFragment() {
        message = "You need to set the message";
        clickedOK = false;
        clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clickedOK = true;
            }
        };
    }

    public NotificationDialogFragment(DialogInterface.OnClickListener inputListener) {
        clickListener = inputListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton(R.string.ok, clickListener);
        return builder.create();
    }

    public void setMessage(String newMessage) {
        message = newMessage;
    }

    public void setClickListener(DialogInterface.OnClickListener inputListener) {
        clickListener = inputListener;
    }
}
