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
import java.util.List;

public class UnacceptedRidesActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "UnacceptedRidesActivity";
    private RecyclerView UnacceptedRidesView;
    private RideRequestAdapter adapter;
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
        adapter = new RideRequestAdapter(rideRequestList);
        UnacceptedRidesView.setAdapter(adapter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        loadRideRequests();
    }

    private void loadRideRequests() {
        DatabaseReference userRideRequestsRef = firebaseDatabase.getReference("users/" + userId + "/ride_requests");
        Log.d(DEBUG_TAG, "loading ride requests...");

        userRideRequestsRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Log.d(DEBUG_TAG, "Snapshot exists...");
                for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                    Log.d(DEBUG_TAG, "Entering loop...");
                    String rideRequestId = requestSnapshot.getKey();
                    Boolean isAccepted = requestSnapshot.getValue(Boolean.class);

                    if (rideRequestId != null && Boolean.FALSE.equals(isAccepted)) {
                        DatabaseReference rideRequestRef = firebaseDatabase.getReference("rideRequests/" + rideRequestId);
                        rideRequestRef.get().addOnSuccessListener(rideSnapshot -> {
                            if (rideSnapshot.exists()) {
                                Log.d(DEBUG_TAG, "ride snapshot exists...");
                                RideRequest rideRequest = rideSnapshot.getValue(RideRequest.class);

                                if (rideRequest != null) {
                                    rideRequestList.add(rideRequest);
                                    adapter.notifyItemInserted(rideRequestList.size() - 1);
                                }
                            }
                        });
                    }
                }
            }

            Log.d(DEBUG_TAG, "Finished loading ride requests");
            Toast.makeText(this, "Unaccepted Ride Requests Loaded", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Unable to load Ride Requests", Toast.LENGTH_SHORT).show();
            Log.e(DEBUG_TAG, "Failed to load ride requests", e);
        });
    }
}