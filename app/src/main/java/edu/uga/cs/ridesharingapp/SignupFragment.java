package edu.uga.cs.ridesharingapp;

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

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance() {
        SignupFragment fragment = new SignupFragment();
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
        return inflater.inflate(R.layout.fragment_signup, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //backend control for login credentials must be added here

        EditText EmailEditText = view.findViewById(R.id.SignupEmail);
        EditText PasswordEditText = view.findViewById(R.id.pswdedit);
        EditText PasswordRetypeEditText = view.findViewById(R.id.pswdRetype);
        Button SignupButton = view.findViewById(R.id.SignupButton);

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = EmailEditText.getText().toString();
                String Password = PasswordEditText.getText().toString();
                String RetypePassword = PasswordRetypeEditText.getText().toString();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                if(email.isEmpty() || Password.isEmpty() || RetypePassword.isEmpty()){
                    Toast.makeText(getActivity(), "Please fill all of the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!Password.equals(RetypePassword)){
                    Toast.makeText(getActivity(),"passwords do not match", Toast.LENGTH_SHORT).show();
                }

                mAuth.createUserWithEmailAndPassword(email, Password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("Account Creation", "Account Created Successfully");
                                    Toast.makeText(getActivity(), "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();

//                                    Intent intent = new Intent(getActivity(), UserActivity.class);
//                                    intent.putExtra("CurrentUser", user);
//                                    startActivity(intent);


                                } else {
                                    Exception exception = task.getException();
                                    if (exception instanceof FirebaseAuthUserCollisionException) {
                                        // Email already in use
                                        Toast.makeText(getActivity(), "This email is already registered.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Some other error
                                        Toast.makeText(getActivity(), "Authentication failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    Log.e("Account Creation", "createUserWithEmail:failure", exception);
                                }
                            }
                        });
            }
        });





    }
}