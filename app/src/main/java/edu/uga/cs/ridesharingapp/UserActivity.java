package edu.uga.cs.ridesharingapp;

import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
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

import java.util.concurrent.Executors;

public class UserActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CredentialManager credentialManager;

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
        Button RiderButton = findViewById(R.id.DriverButton);


        Intent intent = getIntent();
        Bundle UserInfo = intent.getExtras();

        String UserID = UserInfo.getString("UserID");
        String UserEmail = UserInfo.getString("UserEmail");


        credentialManager = CredentialManager.create(this);


        User user = new User(UserID, UserEmail); //make a new instance of the User Object

        Log.d("Signed in user ID", "Current User Signed in UID is: " + user.getID());
        Log.d("Signed in user", "Current signed in user is: " + user.getEmail());


        textView.setText(user.getEmail());


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