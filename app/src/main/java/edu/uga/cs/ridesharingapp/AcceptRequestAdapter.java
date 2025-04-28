package edu.uga.cs.ridesharingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AcceptRequestAdapter extends RecyclerView.Adapter<AcceptRequestAdapter.RequestViewHolder> {

    private final List<RideRequest> rideRequests;
    private final Context context;

    public AcceptRequestAdapter(Context context, List<RideRequest> rideRequests) {
        this.context = context;
        this.rideRequests = rideRequests;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_drive_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        RideRequest request = rideRequests.get(position);
        holder.bind(request);

        holder.itemView.setOnClickListener(v -> {
            AcceptRequestDialogFragment dialog = AcceptRequestDialogFragment.newInstance(
                    position,
                    request.getKey(),
                    request.getDate(),
                    request.getStartLocation(),
                    request.getEndLocation()
            );
            dialog.show(
                    ((AppCompatActivity) context).getSupportFragmentManager(),
                    "AcceptRequestDialog"
            );
        });
    }

    @Override
    public int getItemCount() {
        return rideRequests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        private final TextView startView;
        private final TextView endView;
        private final TextView dateView;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            startView = itemView.findViewById(R.id.DrivertextViewStartLoc);
            endView   = itemView.findViewById(R.id.DrivertextViewEndLoc);
            dateView  = itemView.findViewById(R.id.DrivertextViewDateView);
        }

        public void bind(RideRequest request) {
            startView.setText(request.getStartLocation());
            endView.setText(request.getEndLocation());
            dateView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    .format(new Date(request.getDate())));
        }
    }
}
