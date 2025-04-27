package edu.uga.cs.ridesharingapp;

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
import java.util.List;

public class UnacceptedRideOffers extends AppCompatActivity {
    private static final String DEBUG_TAG = "UnacceptedRideOffersActivity";
    private RecyclerView unacceptedOffers;
    private DriveOfferAdapter adapter;
    private List<DriveOffer> driveOfferList = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_unaccepted_ride_offers);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        Bundle UserInfo = intent.getExtras();
        userId = UserInfo.getString("UserID");


        unacceptedOffers = findViewById(R.id.ViewUnacceptedOffers);
        unacceptedOffers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DriveOfferAdapter(UnacceptedRideOffers.this, driveOfferList );
        unacceptedOffers.setAdapter(adapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        loadRideOffers();


    }

    private void loadRideOffers() {
        DatabaseReference userRideRequestsRef = firebaseDatabase.getReference("users/" + userId + "/created_ride_offers");
        Log.d(DEBUG_TAG, "loading ride offers...");

        userRideRequestsRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Log.d(DEBUG_TAG, "Snapshot exists...");
                List<String> rideOfferIds = new ArrayList<>();

                for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                    String riderOfferId = requestSnapshot.getKey();
                    Boolean isAccepted = requestSnapshot.getValue(Boolean.class);

                    if (riderOfferId != null && Boolean.FALSE.equals(isAccepted)) {
                        rideOfferIds.add(requestSnapshot.getKey());
                    }
                }

                if (!rideOfferIds.isEmpty()) {
                    final int totalRideOffers = rideOfferIds.size();
                    final int[] OffersLoaded = {0};
                    driveOfferList.clear();

                    for (String rideOfferId : rideOfferIds) {
                        DatabaseReference rideOfferRef = firebaseDatabase.getReference("ride_offers/" + rideOfferId);
                        rideOfferRef.get().addOnSuccessListener(rideSnapshot -> {
                            if (rideSnapshot.exists()) {
                                Log.d(DEBUG_TAG, "ride snapshot exists...");
                                DriveOffer driveOffer = rideSnapshot.getValue(DriveOffer.class);

                                if (driveOffer != null) {
                                    driveOfferList.add(driveOffer);
                                }
                            }

                            OffersLoaded[0]++;
                            if (OffersLoaded[0] == totalRideOffers) {
                                Log.d(DEBUG_TAG, "Finished loading ride requests");
                                Collections.sort(driveOfferList, (r1, r2) -> Long.compare(r1.getDate(), r2.getDate()));
                                adapter.notifyDataSetChanged();
                                Toast.makeText(this, "Unaccepted Ride Offers List Loaded", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            OffersLoaded[0]++;
                            if (OffersLoaded[0] == totalRideOffers) {
                                Log.d(DEBUG_TAG, "Finished loading ride requests (with some failures)");
                                adapter.notifyDataSetChanged();
                                Toast.makeText(this, "Unaccepted Ride Offers List Loaded", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "No unaccepted offers requests", Toast.LENGTH_SHORT).show();
                }

            } else {
                Log.d(DEBUG_TAG, "No ride offers for this user");
                Toast.makeText(this, "No ride offers found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Unable to load Ride offers", Toast.LENGTH_SHORT).show();
            Log.e(DEBUG_TAG, "Failed to load ride offers", e);
        });
    }


}