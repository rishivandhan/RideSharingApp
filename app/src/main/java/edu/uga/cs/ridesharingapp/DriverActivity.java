package edu.uga.cs.ridesharingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DriverActivity extends AppCompatActivity {

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


        Button CreateRide = findViewById(R.id.CreateRideButton);

        CreateRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverFormFragment driverFragment = DriverFormFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.DriverFormContainer, driverFragment).commit();
            }
        });



    }
}