package edu.uga.cs.ridesharingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.exceptions.ClearCredentialException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.Executors;

public class UserActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "UserActivity";

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CredentialManager credentialManager;
    private User user;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;




        });



        TextView textView = findViewById( R.id.userID );
        Button LogoutButton = findViewById(R.id.LogoutButton);
        Button DriverButton = findViewById(R.id.DriverButton);
        Button RiderButton = findViewById(R.id.RiderButton);


        Intent intent = getIntent();
        Bundle UserInfo = intent.getExtras();

        String UserID = UserInfo.getString("UserID");
        String UserEmail = UserInfo.getString("UserEmail");
        int points = UserInfo.getInt("UserPoints");
        boolean driver = UserInfo.getBoolean("UserDriver");
        boolean rider = UserInfo.getBoolean("UserRider");

        credentialManager = CredentialManager.create(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();



        User user = new User(UserID, UserEmail, points, driver, rider); //make a new instance of the User Object



        Log.d("Signed in user ID", "Current User Signed in UID is: " + user.getID());
        Log.d("Signed in user", "Current signed in user is: " + user.getEmail());
        Log.d("rider status", "rider is set to: " + user.getRider());
        Log.d("driver status", "rider is set to: " + user.getDriver());

        textView.setText(user.getEmail());


        DriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.getRider()){
                    user.setDriver(true);
                }

                Intent intent = new Intent(UserActivity.this, DriverActivity.class);
                Bundle uInfo = new Bundle();

                String UId = UserInfo.getString("UserID");
                int UserPoints = UserInfo.getInt("UserPoints");

                uInfo.putString("UserID", UId);
                uInfo.putInt("UserPoints", UserPoints);

                intent.putExtras(uInfo);
                startActivity(intent);



            }
        });

        RiderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Need to transfer the user object to the new activity so that the user id can be accessed
                Intent intent = new Intent(UserActivity.this, RiderActivity.class);
                Bundle uInfo = new Bundle();

                String UId = UserInfo.getString("UserID");
                int UserPoints = UserInfo.getInt("UserPoints");

                uInfo.putString("UserID", UId);
                uInfo.putInt("UserPoints", UserPoints);

                intent.putExtras(uInfo);
                startActivity(intent);
            }
        });


        //Logout button feature
        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                Log.d("sign out Success", "Signed Out Successfully");
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }



    //sign out method
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // When a user signs out, clear the current user credential state from all credential providers.
        ClearCredentialStateRequest clearRequest = new ClearCredentialStateRequest();
        credentialManager.clearCredentialStateAsync(
                clearRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(@NonNull Void result) {
                        Log.d("Logout", "Logout status succeeded");
                    }
                    @Override
                    public void onError(@NonNull ClearCredentialException e) {
                        Log.d("Logout", "Logout failed for some reason");
                    }
                });
    }


}