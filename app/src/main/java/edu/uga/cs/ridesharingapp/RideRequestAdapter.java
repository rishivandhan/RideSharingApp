package edu.uga.cs.ridesharingapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RideRequestAdapter extends RecyclerView.Adapter<RideRequestAdapter.RideRequestViewHolder> {
    private List<RideRequest> rideRequests;

    public RideRequestAdapter(List<RideRequest> rideRequests) {
        this.rideRequests = rideRequests;
    }

    @NonNull
    @Override
    public RideRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_ride_request, parent, false);
        return new RideRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideRequestViewHolder holder, int position) {
        RideRequest rideRequest = rideRequests.get(position);
        holder.bind(rideRequest);
    }

    @Override
    public int getItemCount() {
        return rideRequests.size();
    }

    static class RideRequestViewHolder extends RecyclerView.ViewHolder {
        private static final String DEBUG_TAG = "RideRequestViewHolder";
        private TextView textViewStart;
        private TextView textViewEnd;
        private TextView textViewDate;

        public RideRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStart = itemView.findViewById(R.id.textViewStartLoc);
            textViewEnd = itemView.findViewById(R.id.textViewEndLoc);
            textViewDate = itemView.findViewById(R.id.textViewDateView);
        }

        public void bind(RideRequest rideRequest) {
            Log.d(DEBUG_TAG, "Binding...");
            textViewStart.setText(rideRequest.getStartLocation());
            textViewEnd.setText(rideRequest.getEndLocation());
            textViewDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date(rideRequest.getDate())));
        }
    }
}
