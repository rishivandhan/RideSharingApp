package edu.uga.cs.ridesharingapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateRequestDialogFragment extends DialogFragment {
    private TextView textViewDate;
    private TextView textViewTime;
    private EditText editTextStart;
    private EditText editTextEnd;
    private Calendar calendar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        final View layout = inflater.inflate(R.layout.fragment_createrequestdialog,
                requireActivity().findViewById(R.id.root));

        textViewDate = layout.findViewById(R.id.textViewDate);
        textViewTime = layout.findViewById(R.id.textViewTime);
        calendar = Calendar.getInstance();

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

        builder.setView(layout);
        builder.setPositiveButton(R.string.create_item, new AddRideRequestListener());
        builder.setNegativeButton(R.string.cancel_item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        return builder.create();
    }

    private class AddRideRequestListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            long dateTimeTimestamp = calendar.getTimeInMillis();
            String startLoc = editTextStart.getText().toString();
            String endLoc = editTextEnd.getText().toString();

            RideRequest rideRequest = new RideRequest(dateTimeTimestamp, startLoc, endLoc);
            AddRideRequestDialogListener addRideRequestDialogListener = (AddRideRequestDialogListener) requireActivity();
            addRideRequestDialogListener.addRideRequest(rideRequest);

            dismiss();
        }
    }

    public interface AddRideRequestDialogListener {
        void addRideRequest (RideRequest jobLead);
    }
}
