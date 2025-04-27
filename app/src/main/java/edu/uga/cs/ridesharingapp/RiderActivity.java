package edu.uga.cs.ridesharingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiderActivity extends AppCompatActivity
        implements CreateRequestDialogFragment.AddRideRequestDialogListener,
        AcceptOfferDialogFragment.AcceptDriveOfferDialogListener {
    private static final String DEBUG_TAG = "RiderActivity";
    private Button createRequestButton;
    private Button viewUnacceptedRequestsButton;
    private Button viewAcceptedRequestsButton;
    private RecyclerView availableOffersView;
    private String userID;
    private List<DriveOffer> driveOffers = new ArrayList<>();
    private AcceptDriveOfferAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rider);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        Bundle UserInfo = intent.getExtras();
        userID = UserInfo.getString("UserID");

        createRequestButton = findViewById(R.id.CreateRequestButton);
        viewUnacceptedRequestsButton = findViewById(R.id.ViewUnacceptedRequestsButton);
        viewAcceptedRequestsButton = findViewById(R.id.ViewAcceptedRequestsButton);
        availableOffersView = findViewById(R.id.AvailableOffersView);

        createRequestButton.setOnClickListener(v -> {
            CreateRequestDialogFragment dialogFragment = new CreateRequestDialogFragment();
            dialogFragment.show(getSupportFragmentManager(), "CreateRequestDialog");
        });

        viewUnacceptedRequestsButton.setOnClickListener(v -> {
            Intent unacceptedIntent = new Intent(RiderActivity.this, UnacceptedRidesActivity.class);
            Bundle uInfo = new Bundle();
            String UId = UserInfo.getString("UserID");
            uInfo.putString("UserID", UId);
            unacceptedIntent.putExtras(uInfo);
            startActivity(unacceptedIntent);
        });

        availableOffersView.setLayoutManager(new LinearLayoutManager(this));
        loadAvailableOffers();
    }

    private void loadAvailableOffers () {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference offersRef = database.getReference("ride_offers");

        offersRef.get().addOnSuccessListener(snapshot -> {
            driveOffers = new ArrayList<>();

            if (snapshot.exists()) {
                for (DataSnapshot offerSnapshot : snapshot.getChildren()) {
                    DriveOffer offer = offerSnapshot.getValue(DriveOffer.class);

                    if (offer != null && !offer.isAccepted() && !offer.getDriverid().equals(userID)) {
                        driveOffers.add(offer);
                        Log.d(DEBUG_TAG, "Added offer: " + offer.getKey());
                    }
                }
            }

            adapter = new AcceptDriveOfferAdapter(driveOffers, RiderActivity.this);
            availableOffersView.setAdapter(adapter);
            Collections.sort(driveOffers, (offer1, offer2) -> Long.compare(offer1.getDate(), offer2.getDate()));
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Log.e(DEBUG_TAG, "Failed to load drive offers", e);
        });

    }

    @Override
    public void addRideRequest(RideRequest rideRequest) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rrReference = firebaseDatabase.getReference("ride_requests");

        DatabaseReference newRRReference = rrReference.push();
        String genKey = newRRReference.getKey();
        rideRequest.setKey(genKey);
        rideRequest.setRiderid(userID);

        newRRReference.setValue(rideRequest)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Ride Request Created", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ride Request Failed to Create", Toast.LENGTH_SHORT).show();
                    Log.e(DEBUG_TAG, "Error Storing in Firebase", e);
                });

        String uRefPath = "users/" + userID + "/ride_requests/" + genKey;
        DatabaseReference uReference = firebaseDatabase.getReference(uRefPath);

        uReference.setValue(rideRequest.isAccepted())
                .addOnSuccessListener(aVoid -> {
                    Log.d(DEBUG_TAG, "Added the ride request to the requests list of " + userID + " with accepted=" + rideRequest.isAccepted());
                })
                .addOnFailureListener(e -> {
                    Log.e(DEBUG_TAG, "Error storing in Firebase", e);
                });
    }

    @Override
    public void acceptDriveOffer(int position, DriveOffer driveOffer) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference offerRef = firebaseDatabase.getReference("ride_offers").child(driveOffer.getKey());

        Map<String, Object> updates = new HashMap<>();
        updates.put("accepted", true);
        updates.put("riderid", userID);

        offerRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Offer Accepted!", Toast.LENGTH_SHORT).show();
                    driveOffers.remove(position);

                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, driveOffers.size());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to accept offer", Toast.LENGTH_SHORT).show();
                    Log.e(DEBUG_TAG, "Error updating ride offer", e);
                });
    }
}