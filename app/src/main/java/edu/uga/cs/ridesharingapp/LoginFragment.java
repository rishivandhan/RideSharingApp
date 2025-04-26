package edu.uga.cs.ridesharingapp;

import static android.provider.Telephony.BaseMmsColumns.MESSAGE_TYPE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    public static final String MESSAGE_TYPE = "edu.uga.cs.ridesharingapp.MESSAGE_TYPE";
    public static final String DEBUG_TAG = "LoginFragment";

    private User userOBJ;


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        EditText login = view.findViewById(R.id.LoginEmail);
        EditText password = view.findViewById(R.id.LoginPassword);
        Button LoginButton = view.findViewById(R.id.LoginButton);



        LoginButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                String Login = login.getText().toString();
                String Password = password.getText().toString();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();


                mAuth.signInWithEmailAndPassword(Login, Password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                   Log.d("Login Creation", "Login Success");
                                   Toast.makeText(getActivity(), "Login Success.", Toast.LENGTH_SHORT).show();
                                   FirebaseUser user = mAuth.getCurrentUser();


                                    //start activity with the logged in user
                                    Intent intent = new Intent(getActivity(), UserActivity.class);

                                    String CurrentUser = user.getEmail();
                                    String UserID = user.getUid();
                                    Bundle UserInfo = new Bundle();

                                    DatabaseReference userRef = FirebaseDatabase.getInstance()
                                            .getReference("users")
                                            .child(UserID);
                                    userRef.get().addOnSuccessListener(snapshot -> {
                                        if (snapshot.exists()) {
                                            String id = snapshot.child("id").getValue(String.class);
                                            String email = snapshot.child("email").getValue(String.class);
                                            Integer points = snapshot.child("points").getValue(Integer.class);

                                            if (id != null && email != null && points != null) {
                                                userOBJ = new User(id, email, points, false, false);
                                                Log.d(DEBUG_TAG, "User loaded: " + id + ", " + email + ", " + points);
                                                UserInfo.putString("UserID", UserID);
                                                UserInfo.putString("UserEmail", userOBJ.getEmail());
                                                UserInfo.putInt("UserPoints", userOBJ.getPoints());
                                                UserInfo.putBoolean("UserRider", userOBJ.getRider());
                                                UserInfo.putBoolean("UserDriver", userOBJ.getDriver());
                                                intent.putExtras(UserInfo);
                                                startActivity(intent);
                                            } else {
                                                Log.e(DEBUG_TAG, "Missing fields in user object");
                                            }
                                        } else {
                                            Log.e(DEBUG_TAG, "User does not exist");
                                        }
                                    }).addOnFailureListener(e -> {
                                        Log.e(DEBUG_TAG, "Failed to load user", e);
                                    });


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.d("Account Creation", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getActivity(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                //backend control for login credentials must be added here
//                Intent intent = new Intent(getActivity(), UserActivity.class);
//                startActivity(intent);


            }
        });

    }
}