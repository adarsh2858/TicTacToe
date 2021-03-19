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
