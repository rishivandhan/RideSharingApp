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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RiderActivity extends AppCompatActivity
        implements CreateRequestDialogFragment.AddRideRequestDialogListener {
    private static final String DEBUG_TAG = "RiderActivity";
    private Button createRequestButton;
    private Button viewUnacceptedRequestsButton;
    private Button viewAcceptedRequestsButton;
    private RecyclerView availableOffersView;
    private String userID;

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

        String uRefPath = "users/" + userID + "/ride_requests";
        DatabaseReference uReference = firebaseDatabase.getReference(uRefPath);
        DatabaseReference newUReference = uReference.push();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("requestid", genKey);

        newUReference.setValue(hashMap)
                .addOnSuccessListener(aVoid -> {
                    Log.d(DEBUG_TAG, "Added the ride request to the requests list of " + userID + ".");
                })
                .addOnFailureListener(e -> {
                    Log.e(DEBUG_TAG, "Error Storing in Firebase", e);
                });
    }
}