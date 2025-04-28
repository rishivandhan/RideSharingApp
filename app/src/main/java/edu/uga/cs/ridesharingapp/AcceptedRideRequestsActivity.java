package edu.uga.cs.ridesharingapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class AcceptedRideRequestsActivity extends AppCompatActivity {
    private RecyclerView RiderAcceptedRequestsView;
    private RecyclerView RiderAcceptedOffersView;

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

        RiderAcceptedRequestsView = findViewById(R.id.RiderAcceptedRequestsView);
        RiderAcceptedOffersView = findViewById(R.id.RiderAcceptedOffersView);
    }
}