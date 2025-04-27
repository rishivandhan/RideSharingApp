package edu.uga.cs.ridesharingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnacceptedRidesActivity extends AppCompatActivity
        implements EditRequestDialogFragment.EditRideRequestDialogListener{
    private static final String DEBUG_TAG = "UnacceptedRidesActivity";
    private RecyclerView UnacceptedRidesView;
    private EditRideRequestAdapter adapter;
    private List<RideRequest> rideRequestList = new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_unaccepted_rides);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        Bundle UserInfo = intent.getExtras();
        userId = UserInfo.getString("UserID");

        UnacceptedRidesView = findViewById(R.id.UnacceptedRidesView);
        UnacceptedRidesView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EditRideRequestAdapter(UnacceptedRidesActivity.this, rideRequestList );
        UnacceptedRidesView.setAdapter(adapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        loadRideRequests();
    }

    @Override
    public void updateRideRequest(int position, RideRequest rideRequest, int action) {
        if (action == EditRequestDialogFragment.SAVE)
        {
            adapter.notifyDataSetChanged();
            DatabaseReference dRef = firebaseDatabase.getReference().child("ride_requests")
                    .child(rideRequest.getKey());

            dRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().setValue(rideRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Ride Request Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Toast.makeText(getApplicationContext(), "Failed to Update " + rideRequest.getKey(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else if (action == EditRequestDialogFragment.DELETE)
        {
            rideRequestList.remove(position);
            adapter.notifyDataSetChanged();
            DatabaseReference dRef = firebaseDatabase.getReference().child("ride_requests")
                    .child(rideRequest.getKey());

            dRef.addListenerForSingleValueEvent( new ValueEventListener() {
                @Override
                public void onDataChange( @NonNull DataSnapshot dataSnapshot ) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Ride Request Deleted", Toast.LENGTH_SHORT).show();                        }
                    });
                }

                @Override
                public void onCancelled( @NonNull DatabaseError databaseError ) {
                    Toast.makeText(getApplicationContext(), "Failed to delete Ride Request at " + rideRequest.getKey(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadRideRequests() {
        DatabaseReference userRideRequestsRef = firebaseDatabase.getReference("users/" + userId + "/created_ride_requests");
        Log.d(DEBUG_TAG, "loading ride requests...");

        userRideRequestsRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Log.d(DEBUG_TAG, "Snapshot exists...");
                List<String> rideRequestIds = new ArrayList<>();

                for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                    String rideRequestId = requestSnapshot.getKey();
                    Boolean isAccepted = requestSnapshot.getValue(Boolean.class);

                    if (rideRequestId != null && Boolean.FALSE.equals(isAccepted)) {
                        rideRequestIds.add(rideRequestId);
                    }
                }

                if (!rideRequestIds.isEmpty()) {
                    final int totalRequests = rideRequestIds.size();
                    final int[] requestsLoaded = {0};

                    for (String rideRequestId : rideRequestIds) {
                        DatabaseReference rideRequestRef = firebaseDatabase.getReference("ride_requests/" + rideRequestId);
                        rideRequestRef.get().addOnSuccessListener(rideSnapshot -> {
                            if (rideSnapshot.exists()) {
                                Log.d(DEBUG_TAG, "ride snapshot exists...");
                                RideRequest rideRequest = rideSnapshot.getValue(RideRequest.class);

                                if (rideRequest != null) {
                                    rideRequestList.add(rideRequest);
                                }
                            }

                            requestsLoaded[0]++;
                            if (requestsLoaded[0] == totalRequests) {
                                Log.d(DEBUG_TAG, "Finished loading ride requests");
                                Collections.sort(rideRequestList, (r1, r2) -> Long.compare(r1.getDate(), r2.getDate()));
                                adapter.notifyDataSetChanged();
                                Toast.makeText(this, "Unaccepted Ride Requests Loaded", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> {
                            requestsLoaded[0]++;
                            if (requestsLoaded[0] == totalRequests) {
                                Log.d(DEBUG_TAG, "Finished loading ride requests (with some failures)");
                                adapter.notifyDataSetChanged();
                                Toast.makeText(this, "Unaccepted Ride Requests Loaded", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "No unaccepted ride requests", Toast.LENGTH_SHORT).show();
                }

            } else {
                Log.d(DEBUG_TAG, "No ride requests for this user");
                Toast.makeText(this, "No ride requests found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Unable to load Ride Requests", Toast.LENGTH_SHORT).show();
            Log.e(DEBUG_TAG, "Failed to load ride requests", e);
        });
    }
}