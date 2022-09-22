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

public class MainActivity extends AppCompatActivity {

    private static final int LOWEST_NUMBER = 10;

    private static final int BIGGEST_NUMBER = 99;

    private static final int MAX_ATTEMPTS = 5;

    private static final int MIN_ATTEMPTS = 1;

    private int generatedNumber = 0;

    private int attemptsLeft = 0;

    private static int generateNum(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        generatedNumber = generateNum(LOWEST_NUMBER, BIGGEST_NUMBER);
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
                    if (inputNumber != generatedNumber) {
                        wrongAnswerCondition(inputNumber);
                    } else {
                        correctAnswer();
                    }
                }

            }
        };
        Button btnGuess = findViewById(R.id.btn_guess);
        btnGuess.setOnClickListener(clickListener);
        updateAttempts(MAX_ATTEMPTS);
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
        generatedNumber = generateNum(LOWEST_NUMBER, BIGGEST_NUMBER);
        updateAttempts(MAX_ATTEMPTS);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.restart_success),
                Toast.LENGTH_SHORT).show();
    }

    private void wrongAnswerCondition(int inputNumber) {
        if (attemptsLeft == MIN_ATTEMPTS) {
            Button btnGuess = findViewById(R.id.btn_guess);
            btnGuess.setEnabled(false);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.you_lose),
                    Toast.LENGTH_SHORT).show();
        } else {
            if(inputNumber < LOWEST_NUMBER || inputNumber > BIGGEST_NUMBER){
                TextView showHint = findViewById(R.id.show_hint);
                showHint.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.wrong_answer),
                        Toast.LENGTH_SHORT).show();
            }
            else{
                TextView showHint = findViewById(R.id.show_hint);
                showHint.setVisibility(View.INVISIBLE);
                if(inputNumber < generatedNumber){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.number_lower),
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.number_bigger),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        updateAttempts(attemptsLeft - 1);
    }

    private void correctAnswer() {
        Button btnGuess = findViewById(R.id.btn_guess);
        btnGuess.setEnabled(false);
        TextView showMsg = findViewById(R.id.show_msg);
        showMsg.setText(getResources().getString(R.string.guessed) + Integer.toString(generatedNumber));
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.you_win),
                Toast.LENGTH_SHORT).show();
    }

    private void updateAttempts(int newAttempts) {
        attemptsLeft = newAttempts;
        TextView attempts = findViewById(R.id.show_attempts_left);
        attempts.setText(Integer.toString(attemptsLeft));
    }
}