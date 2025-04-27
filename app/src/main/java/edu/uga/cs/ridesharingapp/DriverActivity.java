package edu.uga.cs.ridesharingapp;

import static edu.uga.cs.ridesharingapp.LoginFragment.DEBUG_TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class DriverActivity extends AppCompatActivity implements CreateRequestDialogFragment.AddRideOfferDialogListener, AcceptRequestDialogFragment.AcceptRideRequestDialogListener {
    private Button createRidebutton;
    private Button ViewUnacceptedRideButton;
    private Button ViewAcceptedRideButton;
    private RecyclerView availableRideOffers;
    private String userID;
    private EditRideRequestAdapter adapter;
    List<RideRequest> rideRequests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_driver);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        Bundle userInfo = intent.getExtras();
        userID = userInfo.getString("UserID");

        createRidebutton = findViewById(R.id.CreateRideButton);
        ViewUnacceptedRideButton = findViewById(R.id.ViewUncOfferButton);
        ViewAcceptedRideButton = findViewById(R.id.ViewAccRideButton);
        availableRideOffers = findViewById(R.id.AvailableRideRequestsView);


        adapter = new EditRideRequestAdapter(this, rideRequests);



        availableRideOffers.setLayoutManager(new LinearLayoutManager(this));
        availableRideOffers.setAdapter(adapter);
        loadAvailableRides();




        createRidebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateRequestDialogFragment requestFragment = new CreateRequestDialogFragment();
                requestFragment.setDriverMode(true);
                requestFragment.show(getSupportFragmentManager(), "CreateDriverDialog");

            }
        });

        ViewUnacceptedRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent UnaccceptedIntent = new Intent(DriverActivity.this, UnacceptedRideOffers.class);
                Bundle uInfo = new Bundle();
                String UId = userInfo.getString("UserID");
                uInfo.putString("UserID", UId);
                UnaccceptedIntent.putExtras(uInfo);
                startActivity(UnaccceptedIntent);
            }
        });

        ViewAcceptedRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }


    public void loadAvailableRides() {
        FirebaseDatabase.getInstance().getReference("ride_requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rideRequests.clear();
                for(DataSnapshot child : snapshot.getChildren()){
                    RideRequest req = child.getValue(RideRequest.class);
                    if(userID.equals(req.getCreatorid())){
                        continue;
                    }
                    if (req != null){
                        req.setKey(child.getKey());
                        rideRequests.add(req);
                    }
                }

                Collections.sort(rideRequests, (a, b) ->
                        Long.compare(b.getDate(), a.getDate())
                );

                adapter.notifyDataSetChanged();    // ← tell the RecyclerView to refresh
                Log.d(DEBUG_TAG, "Loaded " + rideRequests.size() + " requests");

                Log.e(DEBUG_TAG, "RideRequestsList: " + rideRequests.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DriverActivity.this, "Failed to load requests: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(DEBUG_TAG, "firebase error, " +error.toException());
            }
        });
    }

    @Override
    public void addRideOffer(DriveOffer rideOffer) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rrReference = firebaseDatabase.getReference("ride_offers");

        DatabaseReference newRRReference = rrReference.push();
        String genKey = newRRReference.getKey();
        rideOffer.setKey(genKey);
        rideOffer.setCreatorid(userID);

        newRRReference.setValue(rideOffer)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Ride Offer Created", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ride Request Failed to Create", Toast.LENGTH_SHORT).show();
                    Log.e(DEBUG_TAG, "Error Storing in Firebase", e);
                });

        String uRefPath = "users/" + userID + "/created_ride_offers/" + genKey;
        DatabaseReference uReference = firebaseDatabase.getReference(uRefPath);

        uReference.setValue(rideOffer.isAccepted())
                .addOnSuccessListener(aVoid -> {
                    Log.d(DEBUG_TAG, "Added the ride request to the requests list of " + userID + " with accepted=" + rideOffer.isAccepted());
                })
                .addOnFailureListener(e -> {
                    Log.e(DEBUG_TAG, "Error storing in Firebase", e);
                });
    }


    @Override
    public void AcceptRideOffer(int position, RideRequest rideRequest) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference offerRef = firebaseDatabase.getReference("ride_requests").child(rideRequest.getKey());

        Map<String, Object> updates = new HashMap<>();
        updates.put("accepted", true);
        updates.put("driverid", userID);

        offerRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    String riderid = rideRequest.getCreatorid();
                    if (riderid != null && !riderid.isEmpty()) {
                        DatabaseReference driverOfferRef = firebaseDatabase.getReference("users")
                                .child(riderid)
                                .child("created_ride_requests")
                                .child(rideRequest.getKey());

                        driverOfferRef.setValue(true)
                                .addOnSuccessListener(aVoid2 -> {
                                    Toast.makeText(this, "Offer Accepted!", Toast.LENGTH_SHORT).show();
                                    rideRequests.remove(position);

                                    adapter.notifyItemRemoved(position);
                                    adapter.notifyItemRangeChanged(position, rideRequests.size());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(DEBUG_TAG, "Error updating driver's ride_offers list", e);
                                });
                    } else {
                        Log.e(DEBUG_TAG, "Driver ID is null or empty for the offer");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to accept offer", Toast.LENGTH_SHORT).show();
                    Log.e(DEBUG_TAG, "Error updating ride offer", e);
                });
    }

}