package edu.uga.cs.ridesharingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AcceptedRideRequestAdapter extends RecyclerView.Adapter<AcceptedRideRequestAdapter.RequestViewHolder> {
    private List<RideRequest> acceptedRideRequests;
    Context context;

    public AcceptedRideRequestAdapter (List<RideRequest> acceptedRideRequests, Context context) {
        this.acceptedRideRequests = acceptedRideRequests;
        this.context = context;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_drive_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        RideRequest request = acceptedRideRequests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return acceptedRideRequests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        private TextView startLocationView;
        private TextView endLocationView;
        private TextView dateView;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            startLocationView = itemView.findViewById(R.id.DrivertextViewStartLoc);
            endLocationView = itemView.findViewById(R.id.DrivertextViewEndLoc);
            dateView = itemView.findViewById(R.id.DrivertextViewDateView);
        }

        public void bind(RideRequest request) {
            startLocationView.setText(request.getStartLocation());
            endLocationView.setText(request.getEndLocation());
            dateView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(request.getDate())));
        }
    }
}
