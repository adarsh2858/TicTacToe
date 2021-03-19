package adarsh.helloworld.tictactoe;

import android.os.Bundle;
import android.widget.TextView;

public class SinglePlayerActivity extends TicTacToeBoard {

    TextView mScoreBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        mScoreBoard = findViewById(R.id.score_board);
        setOnClickListeners();
    }

    public void resetBoard() {
        super.resetBoard();
        mScoreBoard.setText(R.string.current_score);
    }
}
