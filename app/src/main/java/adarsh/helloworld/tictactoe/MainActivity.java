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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[][] buttons = new Button[3][3];

    private final String player1 = "X";
    private final String player2 = "O";

    private TextView textViewPlayer1;
    private TextView textViewPlayer2;

    private int player1Points = 0;
    private int player2Points = 0;

    private boolean player1Turn = true;

    private int roundCount = 0;

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
            loginButton.setText("Sign Out");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String btn = "btn_" + i + j;
                // How to use getResource() and similar approach to fetch the button
                int resID = getResources().getIdentifier(btn, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }

        resetButton = findViewById(R.id.btn_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
    }

    public boolean checkForWin() {
        String[][] fields = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                fields[i][j] = buttons[i][j].getText().toString();
            }
        }
        for (int i = 0; i < 3; i++) {
            if (!fields[i][0].equals("")
                    && fields[i][0].equals(fields[i][2])
                    && fields[i][0].equals(fields[i][1]))
                return true;
        }
        for (int i = 0; i < 3; i++) {
            if (!fields[0][i].equals("")
                    && fields[0][i].equals(fields[2][i])
                    && fields[0][i].equals(fields[1][i]))
                return true;
        }
        if (!fields[0][0].equals("")
                && fields[0][0].equals(fields[1][1])
                && fields[0][0].equals(fields[2][2]))
            return true;
        if (!fields[0][2].equals("")
                && fields[0][2].equals(fields[1][1])
                && fields[0][2].equals(fields[2][0]))
            return true;
        return false;
    }

    public void player1Wins() {
        player1Points++;
        Toast.makeText(this, "Player 1 Wins", Toast.LENGTH_SHORT).show();
    }

    public void player2Wins() {
        player2Points++;
        Toast.makeText(this, "Player 2 Wins", Toast.LENGTH_SHORT).show();
    }

    public void draw() {
        Toast.makeText(this, "It's a Draw", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        roundCount++;
        if (player1Turn)
            ((Button) v).setText(player1);
        else
            ((Button) v).setText(player2);

        if (checkForWin()) {
            if (player1Turn)
                player1Wins();
            else
                player2Wins();
            resetBoard();
        } else if (roundCount == 9) {
            draw();
            resetBoard();
        } else {
            player1Turn = !player1Turn;
        }
    }

    public void updatePointsTextViews() {
        textViewPlayer1.setText("Player 1: " + player1Points);
        textViewPlayer2.setText("Player 2: " + player2Points);
    }

    public void resetBoard() {
        updatePointsTextViews();
        roundCount = 0;
        player1Turn = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
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
        else
            mAuth.signOut();
    }
}
