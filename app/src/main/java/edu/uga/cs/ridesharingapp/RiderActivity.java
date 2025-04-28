package edu.uga.cs.ridesharingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiderActivity extends AppCompatActivity
        implements CreateRequestDialogFragment.AddRideRequestDialogListener,
        AcceptOfferDialogFragment.AcceptDriveOfferDialogListener {
    private static final String DEBUG_TAG = "RiderActivity";
    private static final int RequestCost = 50;


    private TextView Points;
    private Button createRequestButton;
    private Button viewUnacceptedRequestsButton;
    private Button viewAcceptedRequestsButton;
    private RecyclerView availableOffersView;
    private String userID;

    private int userPoints = 0;
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
        userPoints = UserInfo.getInt("UserPoints");

        Points = findViewById(R.id.Points);
        Log.e("RiderActivity", "user points: " + userPoints);
        createRequestButton = findViewById(R.id.CreateRequestButton);
        viewUnacceptedRequestsButton = findViewById(R.id.ViewUnacceptedRequestsButton);
        viewAcceptedRequestsButton = findViewById(R.id.ViewAcceptedRequestsButton);
        availableOffersView = findViewById(R.id.AvailableOffersView);


        Points.setText(Integer.toString(userPoints));
        createRequestButton.setOnClickListener(v -> {


            if(userPoints < RequestCost){
                int needed = RequestCost - userPoints;
                Toast.makeText(this, "You need " + RequestCost + " points to make a request. You are " + needed + " points short.", Toast.LENGTH_LONG).show();
                return;
            }
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

        viewAcceptedRequestsButton.setOnClickListener(v -> {
            Intent acceptedIntent = new Intent(RiderActivity.this, AcceptedRideRequestsActivity.class);
            Bundle uInfo = new Bundle();
            String UId = UserInfo.getString("UserID");
            uInfo.putString("UserID", UId);
            acceptedIntent.putExtras(uInfo);
            startActivity(acceptedIntent);
        });

        availableOffersView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AcceptDriveOfferAdapter(driveOffers,this );
        availableOffersView.setAdapter(adapter);
        loadAvailableOffers();
    }

    private void loadAvailableOffers () {
        FirebaseDatabase.getInstance().getReference("ride_offers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                driveOffers.clear();
                for(DataSnapshot child : snapshot.getChildren()){
                    DriveOffer offer = child.getValue(DriveOffer.class);
                    if(userID.equals(offer.getCreatorid())){
                        continue;
                    }
                    if (offer.isAccepted()) {
                        continue;
                    }
                    if (offer != null){
                        offer.setKey(child.getKey());
                        driveOffers.add(offer);
                    }
                }

                Collections.sort(driveOffers, (a, b) ->
                        Long.compare(b.getDate(), a.getDate())
                );

                adapter.notifyDataSetChanged();    // ← tell the RecyclerView to refresh
                Log.d(DEBUG_TAG, "Loaded " + driveOffers.size() + " requests");

                Log.e(DEBUG_TAG, "RideRequestsList: " + driveOffers.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RiderActivity.this, "Failed to load requests: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(DEBUG_TAG, "firebase error, " +error.toException());
            }
        });

    }

    @Override
    public void addRideRequest(RideRequest rideRequest) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rrReference = firebaseDatabase.getReference("ride_requests");

        DatabaseReference newRRReference = rrReference.push();
        String genKey = newRRReference.getKey();
        rideRequest.setKey(genKey);
        rideRequest.setCreatorid(userID);

        newRRReference.setValue(rideRequest)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Ride Request Created", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ride Request Failed to Create", Toast.LENGTH_SHORT).show();
                    Log.e(DEBUG_TAG, "Error Storing in Firebase", e);
                });

        String uRefPath = "users/" + userID + "/created_ride_requests/" + genKey;
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

        offerRef.get()
                .addOnSuccessListener(snapshot -> {
                    DriveOffer latestOffer = snapshot.getValue(DriveOffer.class);
                    if (latestOffer != null) {
                        driveOffer.setAccepted(latestOffer.isAccepted());
                        driveOffer.setCreatorid(latestOffer.getCreatorid());
                        driveOffer.setRiderid(latestOffer.getRiderid());
                    }

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("accepted", true);
                    updates.put("riderid", userID);

                    offerRef.updateChildren(updates)
                            .addOnSuccessListener(aVoid -> {
                                String driverId = driveOffer.getCreatorid();
                                String offerId = driveOffer.getKey();
                                if (driverId != null && !driverId.isEmpty()) {
                                    DatabaseReference driverOfferRef = firebaseDatabase.getReference("users")
                                            .child(driverId)
                                            .child("created_ride_offers")
                                            .child(offerId);

                                    driverOfferRef.setValue(true)
                                            .addOnSuccessListener(aVoid2 -> {
                                                Toast.makeText(this, "Offer Accepted!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e(DEBUG_TAG, "Error updating driver's ride_offers list", e);
                                            });
                                } else {
                                    Log.e(DEBUG_TAG, "Driver ID is null or empty for the offer");
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(DEBUG_TAG, "Error updating ride offer", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(DEBUG_TAG, "Failed to fetch latest offer", e);
                });
    }
}