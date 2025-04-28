package edu.uga.cs.ridesharingapp;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditOfferDialogFragment extends DialogFragment {
    public static final int SAVE = 1, DELETE = 2;
    private TextView textViewDate;
    private TextView textViewTime;
    private EditText editTextStart;
    private EditText editTextEnd;
    private Calendar calendar;
    int position;
    String key;
    long date;
    static String startLocation;
    String endLocation;





    public static EditOfferDialogFragment newInstance(int position, String key, long date, String startLoc, String endLoc) {
        EditOfferDialogFragment editOfferDialogFragment = new EditOfferDialogFragment();

        Bundle args = new Bundle();
        args.putString("key", key);
        args.putLong("date", date);
        args.putString("startLocation", startLoc);
        args.putString("endLocation", endLoc);
        editOfferDialogFragment.setArguments(args);

        return editOfferDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle saved) {
        key = getArguments().getString("key");
        date = getArguments().getLong("date");
        startLocation = getArguments().getString("startLocation");
        endLocation = getArguments().getString("endLocation");

        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View layout = inflater.inflate(R.layout.fragment_createrequestdialog, requireActivity().findViewById(R.id.root));

        textViewDate = layout.findViewById(R.id.textViewDate);
        textViewTime = layout.findViewById(R.id.textViewTime);
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = dateFormat.format(calendar.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeString = timeFormat.format(calendar.getTime());
        textViewDate.setText(dateString);
        textViewTime.setText(timeString);

        textViewDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Format and show selected date
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        textViewDate.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.show();
        });

        textViewTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (view, hourOfDay, minute) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        textViewTime.setText(sdf.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
            );

            timePickerDialog.show();
        });

        editTextStart = layout.findViewById(R.id.editTextStart);
        editTextEnd = layout.findViewById(R.id.editTextEnd);

        editTextStart.setText(startLocation);
        editTextEnd.setText(endLocation);

        builder.setView(layout);
        builder.setPositiveButton(R.string.update_item, new UpdateRideRequestListener());
        builder.setNegativeButton(R.string.cancel_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNeutralButton(R.string.delete_item, new DeleteRideRequestListener());

        builder.setTitle("Edit Ride Request");

        return builder.create();
    }


    private class UpdateRideRequestListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            long dateTimeTimestamp = calendar.getTimeInMillis();
            String startLoc = editTextStart.getText().toString();
            String endLoc = editTextEnd.getText().toString();

            DriveOffer driveOffer = new DriveOffer(dateTimeTimestamp, startLoc, endLoc);
            driveOffer.setKey(key);
            EditOfferDialogListener editOfferDialogListener = (EditOfferDialogListener) requireActivity();
            editOfferDialogListener.updateOffer(position, driveOffer, SAVE);

            dismiss();
        }
    }

    private class DeleteRideRequestListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            DriveOffer driveOffer = new DriveOffer(date, startLocation, endLocation);
            driveOffer.setKey(key);
            EditOfferDialogListener editOfferDialogListener = (EditOfferDialogListener) requireActivity();
            editOfferDialogListener.updateOffer(position, driveOffer, DELETE);


            dismiss();
        }
    }


    public interface EditOfferDialogListener {
        void updateOffer(int position, DriveOffer offer, int action);
    }
}
