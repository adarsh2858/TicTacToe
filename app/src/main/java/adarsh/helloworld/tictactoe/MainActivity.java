package adarsh.helloworld.tictactoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends TicTacToeBoard implements View.OnClickListener {

    private TextView textViewPlayer1;
    private TextView textViewPlayer2;

    private int player2Points = 0;

    private Button resetButton, loginButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);

        loginButton = findViewById(R.id.btn_login);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null)
            loginButton.setText(R.string.sign_out);

        setOnClickListeners();

        resetButton = findViewById(R.id.btn_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
    }

    public void player2Wins() {
        player2Points++;
        Toast.makeText(this, "Player 2 Wins", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    public void updatePointsTextViews() {
        textViewPlayer1.setText(getString(R.string.player_1_points, player1Points));
        textViewPlayer2.setText(getString(R.string.player_2_points, player2Points));
    }

    public void resetBoard() {
        updatePointsTextViews();
        super.resetBoard();
    }

    public void resetGame() {
        resetBoard();
        player1Points = 0;
        player2Points = 0;
        updatePointsTextViews();
        Toast.makeText(this, "Game Reset", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("roundCount", roundCount);
        outState.putInt("player1Points", player1Points);
        outState.putInt("player2Points", player2Points);
        outState.putBoolean("player1Turn", player1Turn);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        roundCount = savedInstanceState.getInt("roundCount");
        player1Points = savedInstanceState.getInt("player1Points");
        player2Points = savedInstanceState.getInt("player2Points");
        player1Turn = savedInstanceState.getBoolean("player1Turn");
        updatePointsTextViews();
    }

    public void login(View view) {
        if (mAuth.getCurrentUser() == null)
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        else{
            mAuth.signOut();
            User.setSingleInstance(null);
            Toast.makeText(MainActivity.this, "Signed Out Successfully", Toast.LENGTH_SHORT).show();
            loginButton.setText(R.string.login);
        }
    }
}
