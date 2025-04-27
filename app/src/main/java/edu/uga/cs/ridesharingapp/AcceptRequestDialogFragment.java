package edu.uga.cs.ridesharingapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class AcceptRequestDialogFragment extends DialogFragment {
    int position;
    long date;
    String key;
    String startLocation;
    String endLocation;

    public static AcceptRequestDialogFragment newInstance (int position, String key, long date, String startLocation, String endLocation) {
        AcceptRequestDialogFragment AcceptedRequestDialogFragment = new AcceptRequestDialogFragment();
        Bundle args = new Bundle();

        args.putInt("position", position);
        args.putString("key", key);
        args.putLong("date", date);
        args.putString("startLocation", startLocation);
        args.putString("endLocation", endLocation);

        AcceptedRequestDialogFragment.setArguments(args);
        return AcceptedRequestDialogFragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        position = getArguments().getInt("position");
        key = getArguments().getString("key");
        date = getArguments().getLong("date");
        startLocation = getArguments().getString("startLocation");
        endLocation = getArguments().getString("endLocation");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to accept this ride offer?");
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
            AcceptRideRequestDialogListener AcceptRideRequestDialogListener = (AcceptRideRequestDialogListener) requireActivity();
            AcceptRideRequestDialogListener.AcceptRideOffer(position, rideRequest);

            dismiss();
        }
    }

    public interface AcceptRideRequestDialogListener {
        void AcceptRideOffer (int position, RideRequest rideRequest);
    }
}
