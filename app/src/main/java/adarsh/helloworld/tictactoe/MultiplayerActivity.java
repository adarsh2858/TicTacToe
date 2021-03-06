package adarsh.helloworld.tictactoe;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class MultiplayerActivity extends AppCompatActivity {

    private static final String TAG = "Multiplayer Activity";
    final ArrayList<User> usersList = new ArrayList<User>();
    TextView userName;
    Button inviteButton;
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private User currentUser;
    private ProgressBar spinner;

    public static Uri generateContentLink() {
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://play.google.com/store"))
                .setDomainUriPrefix("https://adarshenterprises.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().setMinimumVersion(1).build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .setSocialMetaTagParameters(new DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle("Example of a Dynamic Link")
                        .setDescription("This link works whether the app is installed or not!")
                        .setImageUrl(Uri.parse("https://res.cloudinary.com/dj2xpmtn5/image/upload/v1614188994/gt1eyo6l4qfmk3twzipq.jpg"))
                        .build())
                .buildDynamicLink();

        return dynamicLink.getUri();
    }

    private void showPopup() {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        //        myButton.setText("Push Me");
        LinearLayout linearLayout = (LinearLayout) popupView.findViewById(R.id.btn_layout);

        for (int i = 0; i < usersList.size(); i++) {
            Button userButton = new Button(getApplicationContext());
            userButton.setText(Html.fromHtml("<b><big>" + usersList.get(i).getFullName() + "</big></b>" + "<br />" +
                    "<small>" + usersList.get(i).getEmail() + "</small>" + "<br />"));
            linearLayout.addView(userButton);
        }

        Button cancelButton = new Button(getApplicationContext());
        cancelButton.setText(R.string.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        linearLayout.addView(cancelButton);

        // show the popup window
        // which view you pass in it doesn't matter, it is only used for the window token
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
        spinner.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        createNotificationChannel();

        userName = findViewById(R.id.user_name);
        inviteButton = findViewById(R.id.btn_invite);
        mAuth = FirebaseAuth.getInstance();
        spinner = (ProgressBar)findViewById(R.id.progressBar);

        if (mAuth.getCurrentUser() != null) {
            // Retrieving the value using its keys the file name
            // must be same in both saving and retrieving the data
            SharedPreferences mPrefs = getSharedPreferences("MySharedPref", MODE_PRIVATE);

            // The value will be default as empty string because
            // for the very first time when the app is opened, there is nothing to show
            Gson gson = new Gson();
            String json = mPrefs.getString("User", "");
            User.setSingleInstance(gson.fromJson(json, User.class));
            currentUser = User.getInstance();

            // We can then use the data
            userName.setText(currentUser.getFullName());

            DocumentReference docRef = db.collection("users").document(currentUser.getEmail());
            try {
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            } catch (NullPointerException e) {
                System.out.println("Null Pointer Exception occurred in docRef listener.");
                e.printStackTrace();
            }
        }

        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                usersList.clear();

                // Create an explicit intent for an Activity in your app, Create an Intent for the activity you want to start
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                // Create the TaskStackBuilder and add the intent, which inflates the back stack
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());

                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(intent);

                // Get the PendingIntent containing the entire back stack
                PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CHANNEL_ID")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                // notificationId is a unique int for each notification that you must define
                int notificationId = 1;
                notificationManager.notify(notificationId, builder.build());

                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot taskResults = null;
                                    try {
                                        taskResults = task.getResult();
                                    } catch (NullPointerException e) {
                                        System.out.println("Null Pointer Exception occurred in docRef listener.");
                                        e.printStackTrace();
                                    }

                                    for (QueryDocumentSnapshot document : taskResults) {
//                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Map<String, Object> documentData = document.getData();
                                        // looping over keys
//                                        Log.d(TAG, " DOCUMENT DATA - " + documentData);

                                        User newUser = new User();
                                        for (String name : documentData.keySet()) {
                                            // search  for value
                                            Object value = documentData.get(name);
                                            if (name.equals("email")) {
                                                newUser.setEmail(value.toString());
                                            } else if (name.equals("fullName"))
                                                newUser.setFullName(value.toString());
                                        }

                                        usersList.add(newUser);
//                                        System.out.println(usersList);
                                    }
                                    startActivity(new Intent(getApplicationContext(), PopupActivity.class));
//                                    showPopup();

                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });

//                onShareClicked();
            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "getString(R.string.channel_name)";
            String description = "getString(R.string.channel_description)";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void onShareClicked() {
//        Uri link = MultiplayerActivity.generateContentLink();
        Uri link = Uri.parse("https://adarshenterprises.page.link/tictactoe");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, link.toString());

        startActivity(Intent.createChooser(intent, "Share Link"));
    }
}
