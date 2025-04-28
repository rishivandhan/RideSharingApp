package edu.uga.cs.ridesharingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
import java.util.List;

public class AcceptedRideRequestsActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "AcceptedRideRequestsActivity";
    private RecyclerView RiderAcceptedRequestsView;
    private RecyclerView RiderAcceptedOffersView;
    private FirebaseDatabase firebaseDatabase;
    private String userId;
    private List<RideRequest> acceptedRideRequests = new ArrayList<>();
    private List<DriveOffer> acceptedDriveOffers = new ArrayList<>();
    private AcceptedRideRequestAdapter acceptedRideRequestAdapter;
    private AcceptedDriveOfferAdapter acceptedDriveOfferAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accepted_ride_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        Bundle UserInfo = intent.getExtras();
        userId = UserInfo.getString("UserID");

        RiderAcceptedRequestsView = findViewById(R.id.RiderAcceptedRequestsView);
        RiderAcceptedOffersView = findViewById(R.id.RiderAcceptedOffersView);
        firebaseDatabase = FirebaseDatabase.getInstance();

        RiderAcceptedRequestsView.setLayoutManager(new LinearLayoutManager(this));
        RiderAcceptedOffersView.setLayoutManager(new LinearLayoutManager(this));

        acceptedRideRequestAdapter = new AcceptedRideRequestAdapter(acceptedRideRequests, this);
        acceptedDriveOfferAdapter = new AcceptedDriveOfferAdapter(acceptedDriveOffers, this);

        RiderAcceptedRequestsView.setAdapter(acceptedRideRequestAdapter);
        RiderAcceptedOffersView.setAdapter(acceptedDriveOfferAdapter);

        loadAcceptedRideRequests();
        loadAcceptedDriveOffers();
    }

    private void loadAcceptedRideRequests() {
        DatabaseReference requestsRef = firebaseDatabase.getReference("ride_requests");

        requestsRef.get().addOnSuccessListener(snapshot -> {
            acceptedRideRequests.clear();
            for (DataSnapshot rideSnapshot : snapshot.getChildren()) {
                RideRequest rideRequest = rideSnapshot.getValue(RideRequest.class);

                if (rideRequest != null && rideRequest.isAccepted() && userId.equals(rideRequest.getCreatorid())) {
                    acceptedRideRequests.add(rideRequest);
                }
            }
            Collections.sort(acceptedRideRequests, (r1, r2) -> Long.compare(r1.getDate(), r2.getDate()));
            acceptedRideRequestAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e(DEBUG_TAG, "Failed to load accepted ride requests", e));
    }

    private void loadAcceptedDriveOffers() {
        DatabaseReference offersRef = firebaseDatabase.getReference("ride_offers");

        offersRef.get().addOnSuccessListener(snapshot -> {
            acceptedDriveOffers.clear();
            for (DataSnapshot offerSnapshot : snapshot.getChildren()) {
                DriveOffer driveOffer = offerSnapshot.getValue(DriveOffer.class);

                if (driveOffer != null && driveOffer.isAccepted() && userId.equals(driveOffer.getRiderid())) {
                    acceptedDriveOffers.add(driveOffer);
                }
            }
            Collections.sort(acceptedDriveOffers, (r1, r2) -> Long.compare(r1.getDate(), r2.getDate()));
            acceptedDriveOfferAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e(DEBUG_TAG, "Failed to load accepted drive offers", e));
    }
}