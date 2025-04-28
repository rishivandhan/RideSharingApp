package edu.uga.cs.ridesharingapp;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditOfferDialogFragment extends DialogFragment {
    public static final int SAVE = 1, DELETE = 2;

    // arguments
    private String key;
    private long date;
    private String startLoc, endLoc;
    private int position;

    private TextView dateView, timeView;
    private EditText startEdit, endEdit;
    private Calendar calendar;

    public interface EditOfferDialogListener {
        void updateOffer(int position, DriveOffer offer, int action);
    }

    public static EditOfferDialogFragment newInstance(int pos, String key, long date,
                                                      String start, String end) {
        EditOfferDialogFragment f = new EditOfferDialogFragment();
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        b.putString("key", key);
        b.putLong("date", date);
        b.putString("start", start);
        b.putString("end", end);
        f.setArguments(b);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle saved) {
        Bundle args = getArguments();
        position   = args.getInt("pos");
        key        = args.getString("key");
        date       = args.getLong("date");
        startLoc   = args.getString("start");
        endLoc     = args.getString("end");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View layout = requireActivity().getLayoutInflater()
                .inflate(R.layout.fragment_createrequestdialog, null);

        // find & pre-fill views
        dateView = layout.findViewById(R.id.textViewDate);
        timeView = layout.findViewById(R.id.textViewTime);
        startEdit = layout.findViewById(R.id.editTextStart);
        endEdit   = layout.findViewById(R.id.editTextEnd);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        dateView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(calendar.getTime()));
        timeView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(calendar.getTime()));
        startEdit.setText(startLoc);
        endEdit.setText(endLoc);

        // date & time pickers (same as your request fragment)…
        // (omitted for brevity; copy your code from EditRequestDialogFragment)

        builder.setView(layout)
                .setTitle("Edit Ride Offer")
                .setPositiveButton(R.string.update_item,
                        (d, i) -> {
                            long ts = calendar.getTimeInMillis();
                            DriveOffer o = new DriveOffer(ts,
                                    startEdit.getText().toString(),
                                    endEdit.getText().toString());
                            o.setKey(key);
                            ((EditOfferDialogListener)requireActivity())
                                    .updateOffer(position, o, SAVE);
                        })
                .setNeutralButton(R.string.delete_item,
                        (d, i) -> {
                            DriveOffer o = new DriveOffer(date, startLoc, endLoc);
                            o.setKey(key);
                            ((EditOfferDialogListener)requireActivity())
                                    .updateOffer(position, o, DELETE);
                        })
                .setNegativeButton(R.string.cancel_item, null);

        return builder.create();
    }
}
