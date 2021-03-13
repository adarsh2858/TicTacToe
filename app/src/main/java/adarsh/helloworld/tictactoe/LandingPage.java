package adarsh.helloworld.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class LandingPage extends AppCompatActivity {

    private static final String TAG = "Landing Page Activity";
    Button DoublePlayerButton, MultiplayerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        handleDynamicLinks();

        DoublePlayerButton = findViewById(R.id.btn_2_player);
        MultiplayerButton = findViewById(R.id.btn_multiplayer);

        DoublePlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        MultiplayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MultiplayerActivity.class));
            }
        });
    }

    private void handleDynamicLinks() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(getIntent())
            .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                @Override
                public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                    // Get deep link from result (may be null if no link is found)
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        System.out.println("HELLO WORLD pending - " + pendingDynamicLinkData);
                        deepLink = pendingDynamicLinkData.getLink();
                        Intent intent = getIntent();
                        Uri data = intent.getData();
                        System.out.println("HELLO WORLD - " + deepLink);
                        System.out.println("HELLO WORLD - " + intent);
                        System.out.println("HELLO WORLD DATA - " + data);
                    }
                    // Handle the deep link. For example, open the linked
                    // content, or apply promotional credit to the user's
                    // account.
                    // ...

                    // ...
                }
            })
            .addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "getDynamicLink:onFailure", e);
                }
            });
    }
}
