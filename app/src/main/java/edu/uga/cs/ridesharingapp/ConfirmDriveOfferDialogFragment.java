package edu.uga.cs.ridesharingapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ConfirmDriveOfferDialogFragment extends DialogFragment {
    int position;
    long date;
    String key;
    String startLocation;
    String endLocation;

    public static ConfirmDriveOfferDialogFragment newInstance (int position, long date, String key, String startLocation, String endLocation) {
        ConfirmDriveOfferDialogFragment confirmDriveOfferDialogFragment = new ConfirmDriveOfferDialogFragment();
        Bundle args = new Bundle();

        args.putInt("position", position);
        args.putLong("date", date);
        args.putString("key", key);
        args.putString("startLocation", startLocation);
        args.putString("endLocation", endLocation);

        confirmDriveOfferDialogFragment.setArguments(args);
        return  confirmDriveOfferDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        position = getArguments().getInt("position");
        date = getArguments().getLong("date");
        key = getArguments().getString("key");
        startLocation = getArguments().getString("startLocation");
        endLocation = getArguments().getString("endLocation");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to confirm that this ride is complete?");
        builder.setPositiveButton("Confirm", new ConfirmClickListener());
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        return builder.create();
    }

    private class ConfirmClickListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            DriveOffer driveOffer = new DriveOffer(date, startLocation, endLocation);
            driveOffer.setKey(key);
            ConfirmDriveOfferDialogListener confirmDriveOfferDialogListener = (ConfirmDriveOfferDialogListener) requireActivity();
            confirmDriveOfferDialogListener.confirmDriveOffer(position, driveOffer);

            dismiss();
        }
    }

    public interface ConfirmDriveOfferDialogListener {
        void confirmDriveOffer (int position, DriveOffer driveOffer);
    }
}
