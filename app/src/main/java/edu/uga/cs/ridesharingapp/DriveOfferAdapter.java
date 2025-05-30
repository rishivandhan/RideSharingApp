package edu.uga.cs.ridesharingapp;

import android.content.Context;
import android.util.Log;
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

public class DriveOfferAdapter extends RecyclerView.Adapter<DriveOfferAdapter.DriveOfferViewHolder> {
    private List<DriveOffer> driveOffers;
    private Context context;

    public DriveOfferAdapter(Context context, List<DriveOffer> driveOffers) {
        this.context = context;
        this.driveOffers = driveOffers;
    }

    @NonNull
    @Override
    public DriveOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_drive_request, parent, false);
        return new DriveOfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DriveOfferViewHolder holder, int position) {
        DriveOffer driveOffer = driveOffers.get(position);
        holder.bind(driveOffer);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditOfferDialogFragment editOfferDialogFragment = EditOfferDialogFragment.newInstance(
                        holder.getAdapterPosition(),
                        driveOffer.getKey(),
                        driveOffer.getDate(),
                        driveOffer.getStartLocation(),
                        driveOffer.getEndLocation()
                );
                editOfferDialogFragment.show(((AppCompatActivity)context).getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return driveOffers.size();
    }

    static class DriveOfferViewHolder extends RecyclerView.ViewHolder {
        private static final String DEBUG_TAG = "DriveOfferViewHolder";
        private TextView DrivertextViewStart;
        private TextView DrivertextViewEnd;
        private TextView DrivertextViewDate;

        public DriveOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            DrivertextViewStart = itemView.findViewById(R.id.DrivertextViewStartLoc);
            DrivertextViewEnd = itemView.findViewById(R.id.DrivertextViewEndLoc);
            DrivertextViewDate = itemView.findViewById(R.id.DrivertextViewDateView);
        }

        public void bind(DriveOffer driveOffer) {
            Log.d(DEBUG_TAG, "Binding...");
            DrivertextViewStart.setText(driveOffer.getStartLocation());
            DrivertextViewEnd.setText(driveOffer.getEndLocation());
            DrivertextViewDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date(driveOffer.getDate())));
        }
    }
}
