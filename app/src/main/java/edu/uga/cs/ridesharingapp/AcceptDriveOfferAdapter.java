package edu.uga.cs.ridesharingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AcceptDriveOfferAdapter extends RecyclerView.Adapter<AcceptDriveOfferAdapter.OfferViewHolder> {
    private List<DriveOffer> driveOffers;
    Context context;

    public AcceptDriveOfferAdapter(List<DriveOffer> driveOffers, Context context) {
        this.driveOffers = driveOffers;
        this.context = context;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_drive_request, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        DriveOffer offer = driveOffers.get(position);
        holder.bind(offer);

        holder.itemView.setOnClickListener(v -> {
            AcceptOfferDialogFragment dialogFragment = AcceptOfferDialogFragment.newInstance(
                    position, // position of the offer in the list
                    offer.getKey(), // offer key
                    offer.getDate(), // offer date
                    offer.getStartLocation(), // offer start location
                    offer.getEndLocation() // offer end location
            );

            // Show the dialog (ensure you are passing the correct fragment manager)
            FragmentManager fragmentManager = ((RiderActivity) context).getSupportFragmentManager();
            dialogFragment.show(fragmentManager, "AcceptOfferDialog");
        });
    }

    @Override
    public int getItemCount() {
        return driveOffers.size();
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
