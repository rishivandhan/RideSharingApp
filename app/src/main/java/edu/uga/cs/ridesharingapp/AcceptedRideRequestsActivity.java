package edu.uga.cs.ridesharingapp;

import static edu.uga.cs.ridesharingapp.LoginFragment.DEBUG_TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class AcceptedRideRequestsActivity extends AppCompatActivity
        implements ConfirmRideRequestDialogFragment.ConfirmRideRequestDialogListener {
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

                if (rideRequest != null && rideRequest.isAccepted() && userId.equals(rideRequest.getCreatorid()) && Boolean.FALSE.equals(rideRequest.isRiderConfirm())) {
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

                if (driveOffer != null && driveOffer.isAccepted() && userId.equals(driveOffer.getRiderid()) && Boolean.FALSE.equals(driveOffer.isRiderConfirm())) {
                    acceptedDriveOffers.add(driveOffer);
                }
            }
            Collections.sort(acceptedDriveOffers, (r1, r2) -> Long.compare(r1.getDate(), r2.getDate()));
            acceptedDriveOfferAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e(DEBUG_TAG, "Failed to load accepted drive offers", e));
    }

    @Override
    public void confirmRideRequest(int position, RideRequest rideRequest) {
        DatabaseReference requestRef = firebaseDatabase.getReference("ride_requests").child(rideRequest.getKey());

        requestRef.get()
                .addOnSuccessListener(snapshot -> {
                    RideRequest latestRequest = snapshot.getValue(RideRequest.class);
                    if (latestRequest != null) {
                        // Update all fields of rideRequest except key
                        rideRequest.setAccepted(latestRequest.isAccepted());
                        rideRequest.setCreatorid(latestRequest.getCreatorid());
                        rideRequest.setDriverid(latestRequest.getDriverid());
                        rideRequest.setDriverConfirm(latestRequest.isDriverConfirm());
                        rideRequest.setRiderConfirm(true);
                    }

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("riderConfirm", true);

                    requestRef.updateChildren(updates)
                            .addOnSuccessListener(aVoid -> {
                                if (rideRequest.isRiderConfirm() && rideRequest.isDriverConfirm())
                                {
                                    String riderId = rideRequest.getCreatorid();
                                    String driverId = rideRequest.getDriverid();

                                    if (riderId != null && driverId != null)
                                    {
                                        DatabaseReference riderRef = firebaseDatabase.getReference("users").child(riderId);
                                        DatabaseReference driverRef = firebaseDatabase.getReference("users").child(driverId);

                                        DatabaseReference riderRequestRef = riderRef.child("created_ride_requests").child(rideRequest.getKey());
                                        riderRequestRef.removeValue()
                                                .addOnSuccessListener(aVoid2 -> Log.d(DEBUG_TAG, "Ride request removed from rider's created_ride_requests"))
                                                .addOnFailureListener(e -> Log.e(DEBUG_TAG, "Failed to remove ride request from rider", e));

                                        riderRef.child("points").get()
                                                .addOnSuccessListener(riderSnapshot -> {
                                                    Long riderPoints = riderSnapshot.getValue(Long.class);
                                                    if (riderPoints != null) {
                                                        riderRef.child("points").setValue(riderPoints - 50);
                                                    }
                                                })
                                                .addOnFailureListener(e -> Log.e(DEBUG_TAG, "Failed to fetch rider points", e));

                                        driverRef.child("points").get()
                                                .addOnSuccessListener(driverSnapshot -> {
                                                    Long driverPoints = driverSnapshot.getValue(Long.class);
                                                    if (driverPoints != null) {
                                                        driverRef.child("points").setValue(driverPoints + 50);
                                                    }
                                                })
                                                .addOnFailureListener(e -> Log.e(DEBUG_TAG, "Failed to fetch driver points", e));
                                    } else {
                                        Log.e(DEBUG_TAG, "riderId or driverId is null");
                                    }
                                }

                                Toast.makeText(this, "Offer Accepted!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to confirm request", Toast.LENGTH_SHORT).show();
                                Log.e(DEBUG_TAG, "Error updating ride request", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(DEBUG_TAG, "Failed to fetch latest ride request", e);
                });
    }
}