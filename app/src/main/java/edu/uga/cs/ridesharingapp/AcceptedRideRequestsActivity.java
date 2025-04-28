package edu.uga.cs.ridesharingapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AcceptedRideRequestsActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "AcceptedRideRequestsActivity";
    private RecyclerView RiderAcceptedRequestsView;
    private RecyclerView RiderAcceptedOffersView;
    private FirebaseDatabase firebaseDatabase;
    private String userId;
    private List<RideRequest> acceptedRideRequests = new ArrayList<>();
    private List<DriveOffer> acceptedDriveOffers = new ArrayList<>();

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

        // TODO: create RecyclerViewAdapters
    }
}