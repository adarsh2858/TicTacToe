package adarsh.helloworld.tictactoe;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TicTacToeBoard extends AppCompatActivity implements View.OnClickListener {
    protected final Button[][] buttons = new Button[3][3];

    private final String player1 = "X";
    private final String player2 = "O";

    protected int roundCount = 0;
    protected int player1Points = 0;
    protected boolean player1Turn = true;

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

    public void resetBoard() {
        roundCount = 0;
        player1Turn = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
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
        Toast.makeText(this, "Computer Wins", Toast.LENGTH_SHORT).show();
    }

    public void draw() {
        Toast.makeText(this, "It's a Draw", Toast.LENGTH_SHORT).show();
    }
}
