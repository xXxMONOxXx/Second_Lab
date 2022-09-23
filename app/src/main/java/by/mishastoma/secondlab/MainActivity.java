package by.mishastoma.secondlab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import by.mishastoma.secondlab.guessnum.GuessConditions;
import by.mishastoma.secondlab.guessnum.GuessNum;

public class MainActivity extends AppCompatActivity {

    private GuessNum guessNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            guessNum = new GuessNum();
        } catch (GuessNumberException e) {

        }
        updateAttempts();
        TextView showHint = findViewById(R.id.show_hint);
        showHint.setVisibility(View.INVISIBLE);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.edit_num);
                String inputStr = input.getText().toString();
                if (inputStr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalid_input),
                            Toast.LENGTH_SHORT).show();
                } else {
                    int inputNumber = Integer.parseInt(inputStr);
                    GuessConditions condition = guessNum.takeAGuess(inputNumber);
                    switch (condition) {
                        case WIN:
                            correctAnswer();
                            break;
                        case LOSE:
                            loseCondition();
                            break;
                        case GENERATED_NUMBER_BIGGER:
                            biggerCondition();
                            break;
                        case GENERATED_NUMBER_LOWER:
                            lowerCondition();
                            break;
                    }
                }
                updateAttempts();
            }
        };
        Button btnGuess = findViewById(R.id.btn_guess);
        btnGuess.setOnClickListener(clickListener);
    }

    public void restart(View v) {
        EditText editNum = findViewById(R.id.edit_num);
        editNum.getText().clear();
        TextView showHint = findViewById(R.id.show_hint);
        showHint.setVisibility(View.INVISIBLE);
        TextView showMsg = findViewById(R.id.show_msg);
        showMsg.setText(getResources().getString(R.string.show_msg_label));
        Button btnGuess = findViewById(R.id.btn_guess);
        btnGuess.setEnabled(true);
        guessNum.restart();
        updateAttempts();
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.restart_success),
                Toast.LENGTH_SHORT).show();
    }

    private void lowerCondition() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.number_lower),
                Toast.LENGTH_SHORT).show();
    }

    private void biggerCondition() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.number_bigger),
                Toast.LENGTH_SHORT).show();
    }

    private void loseCondition() {
        Button btnGuess = findViewById(R.id.btn_guess);
        btnGuess.setEnabled(false);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.you_lose),
                Toast.LENGTH_SHORT).show();
    }

    private void correctAnswer() {
        Button btnGuess = findViewById(R.id.btn_guess);
        btnGuess.setEnabled(false);
        TextView showMsg = findViewById(R.id.show_msg); //// TODO: 23.09.2022
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.you_win),
                Toast.LENGTH_SHORT).show();
    }

    private void updateAttempts() {
        TextView attempts = findViewById(R.id.show_attempts_left);
        attempts.setText(Integer.toString(guessNum.getAttempts()));
    }
}