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

public class AcceptedDriveOfferAdapter extends RecyclerView.Adapter<AcceptedDriveOfferAdapter.OfferViewHolder> {
    private List<DriveOffer> acceptedDriveOffers;
    Context context;

    public AcceptedDriveOfferAdapter (List<DriveOffer> acceptedDriveOffers, Context context) {
        this.acceptedDriveOffers = acceptedDriveOffers;
        this.context = context;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_drive_request, parent, false);
        return new AcceptedDriveOfferAdapter.OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        DriveOffer offer = acceptedDriveOffers.get(position);
        holder.bind(offer);
    }

    @Override
    public int getItemCount() {
        return acceptedDriveOffers.size();
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {
        private TextView startLocationView;
        private TextView endLocationView;
        private TextView dateView;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            startLocationView = itemView.findViewById(R.id.DrivertextViewStartLoc);
            endLocationView = itemView.findViewById(R.id.DrivertextViewEndLoc);
            dateView = itemView.findViewById(R.id.DrivertextViewDateView);
        }

        public void bind(DriveOffer offer) {
            startLocationView.setText(offer.getStartLocation());
            endLocationView.setText(offer.getEndLocation());
            dateView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(offer.getDate())));
        }
    }
}
