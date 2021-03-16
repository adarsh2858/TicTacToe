package adarsh.helloworld.tictactoe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 491;
    private static final String USERS = "users";
    private static final String TAG = "RegisterActivity";
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private EditText mFullName, mEmail, mPassword, mPhone, mLoginEmail, mLoginPassword;
    private Button mRegisterButton, mLoginButton;
    private User newUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference(USERS);
        mFullName = findViewById(R.id.full_name);
        mPhone = findViewById(R.id.phone);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRegisterButton = findViewById(R.id.btn_register);

        mLoginEmail = findViewById(R.id.login_email);
        mLoginPassword = findViewById(R.id.login_password);
        mLoginButton = findViewById(R.id.btn_login);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String fullName = mFullName.getText().toString();
                String phoneNo = mPhone.getText().toString();

                if (TextUtils.isEmpty(fullName)) {
                    mFullName.setError("Full Name is required.");
                } else if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required.");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmail.setError("Enter valid email address.");
                } else if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required.");
                } else {
                    newUser = new User(email, fullName, phoneNo, password);
                    createAccount(email, fullName, phoneNo, password);
                }
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mLoginEmail.getText().toString();
                String password = mLoginPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    mLoginEmail.setError("Email is required.");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mLoginEmail.setError("Enter valid email address.");
                } else if (TextUtils.isEmpty(password)) {
                    mLoginPassword.setError("Password is required.");
                } else {
                    signIn(email, password);
                }
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn(mGoogleSignInClient);
                        break;
                }
            }
        });

    }

    private void signIn(GoogleSignInClient mGoogleSignInClient) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("FIREBASE WITH GOOGLE", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Firebase+google failed", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("credential success", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("CREDENTIAL FAILURE", "signInWithCredential:failure", task.getException());
                            updateUI(null, null);
                        }

                        // ...
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully. show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GOOGLE SIGN IN", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null, null);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)
            updateUI(currentUser, null);
    }

    public void createAccount(final String email, final String fullName, final String phoneNumber, final String password) {
        Log.d(TAG, "Create account Method");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Create a new user with a first, middle, and last name
                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put("email", email);
                            newUser.put("password", password);
                            newUser.put("fullName", fullName);
                            newUser.put("phoneNumber", phoneNumber);

                            // Add a new document with a generated ID
//                            db.collection("users")
//                                    .add(newUser)
//                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                        @Override
//                                        public void onSuccess(DocumentReference documentReference) {
//                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.w(TAG, "Error adding document", e);
//                                        }
//                                    });

                            // Add a new document with the email as the ID
                            db.collection("users")
                                    .document(email)
                                    .set(newUser);

                            updateUI(user, email);
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Register Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signIn(String email, String password) {
        final String finalEmail = email;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Get a reference to our users

                            // Attach a listener to read all the data at our user reference
//                            ref.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    User user = dataSnapshot.getValue(User.class);
//                                    System.out.println(dataSnapshot.getValue());
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//                                    System.out.println("The read failed: " + databaseError.getCode());
//                                }
//                            });
                            updateUI(user, finalEmail);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Login Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user, String finalEmail) {
        Log.d(TAG, "Update UI Method");

        String keyId = mDatabase.push().getKey();
        mDatabase.child(keyId).setValue(newUser);

        mDatabase.orderByChild("email").equalTo(finalEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String email = data.child("email").getValue().toString();
                    String fullName = data.child("fullName").getValue().toString();
                    String phone = data.child("phone").getValue().toString();
                    Toast.makeText(LoginActivity.this, "Welcome " + fullName, Toast.LENGTH_SHORT).show();

                    // Storing data into SharedPreferences
                    SharedPreferences mPrefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);

                    // Creating an Editor object to edit(write to the file)
                    //set variables of 'myObject', etc.
                    User.setSingleInstance(email, fullName, phone);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(User.getInstance());

                    // Storing the key and its value as the data fetched from edittext
                    prefsEditor.putString("User", json);

                    // Once the changes have been made,
                    // we need to commit to apply those changes made,
                    // otherwise, it will throw an error
                    prefsEditor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        startActivity(new Intent(getApplicationContext(), MainActivity.class));

        Toast.makeText(this, "Update UI", Toast.LENGTH_SHORT).show();
    }
}
