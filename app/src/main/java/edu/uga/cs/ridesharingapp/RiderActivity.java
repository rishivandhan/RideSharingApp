package edu.uga.cs.ridesharingapp;

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

public class RiderActivity extends AppCompatActivity
        implements CreateRequestDialogFragment.AddRideRequestDialogListener {
    private static final String DEBUG_TAG = "RiderActivity";
    private Button createRequestButton;
    private Button viewUnacceptedRequestsButton;
    private Button viewAcceptedRequestsButton;
    private RecyclerView availableOffersView;

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
        // TODO: add riderid to the object after implementation of user id intent transfer

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("ride_requests");

        DatabaseReference newReference = reference.push();
        String genKey = newReference.getKey();
        rideRequest.setKey(genKey);

        newReference.setValue(rideRequest)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Ride Request Created", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ride Request Failed to Create", Toast.LENGTH_SHORT).show();
                    Log.e(DEBUG_TAG, "Error Storing in Firebase", e);
                });
    }
}