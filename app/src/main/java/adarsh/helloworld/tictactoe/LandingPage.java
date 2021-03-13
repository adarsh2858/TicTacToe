package adarsh.helloworld.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LandingPage extends AppCompatActivity {

    Button DoublePlayerButton, MultiplayerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

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
}
