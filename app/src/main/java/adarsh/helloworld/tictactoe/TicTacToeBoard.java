package adarsh.helloworld.tictactoe;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TicTacToeBoard extends AppCompatActivity implements View.OnClickListener {
    private final Button[][] buttons = new Button[3][3];

    private final String player1 = "X";
    private final String player2 = "O";

    private boolean player1Turn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tic_tac_toe_board);
    }

    public void setOnClickListeners() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String btn = "btn_" + i + j;
                // How to use getResource() and similar approach to fetch the button
                int resID = getResources().getIdentifier(btn, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals(""))
            return;

        if (player1Turn)
            ((Button) v).setText(player1);
        else
            ((Button) v).setText(player2);

        player1Turn = !player1Turn;
    }
}
