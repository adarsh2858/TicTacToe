package adarsh.helloworld.tictactoe;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SinglePlayerActivity extends AppCompatActivity {

    TextView mScoreBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        mScoreBoard = findViewById(R.id.score_board);
    }
}
