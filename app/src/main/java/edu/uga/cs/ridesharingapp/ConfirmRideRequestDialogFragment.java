package edu.uga.cs.ridesharingapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ConfirmRideRequestDialogFragment extends DialogFragment {
    int position;
    long date;
    String key;
    String startLocation;
    String endLocation;

    public static ConfirmRideRequestDialogFragment newInstance (int position, long date, String key, String startLocation, String endLocation) {
        ConfirmRideRequestDialogFragment confirmRideRequestDialogFragment = new ConfirmRideRequestDialogFragment();
        Bundle args = new Bundle();

        args.putInt("position", position);
        args.putLong("date", date);
        args.putString("key", key);
        args.putString("startLocation", startLocation);
        args.putString("endLocation", endLocation);

        confirmRideRequestDialogFragment.setArguments(args);
        return  confirmRideRequestDialogFragment;
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
            RideRequest rideRequest = new RideRequest(date, startLocation, endLocation);
            rideRequest.setKey(key);
            ConfirmRideRequestDialogListener confirmRideRequestDialogListener = (ConfirmRideRequestDialogListener) requireActivity();
            confirmRideRequestDialogListener.confirmRideRequest(position, rideRequest);

            dismiss();
        }
    }

    public interface ConfirmRideRequestDialogListener {
        void confirmRideRequest (int position, RideRequest rideRequest);
    }
}
