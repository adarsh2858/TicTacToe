package adarsh.helloworld.tictactoe;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class SinglePlayerActivity extends TicTacToeBoard {

    TextView mScoreBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player);

        mScoreBoard = findViewById(R.id.score_board);
        setOnClickListeners();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        updatePointsTextViews();
    }

    public void updatePointsTextViews() {
        mScoreBoard.setText(getString(R.string.current_score, player1Points));
    }

    public void resetBoard() {
        updatePointsTextViews();
        super.resetBoard();
    }

    public void resetGame() {
        player1Points = 0;
        updatePointsTextViews();
    }
}
