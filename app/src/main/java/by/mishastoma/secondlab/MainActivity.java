package by.mishastoma.secondlab;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import by.mishastoma.secondlab.guessnum.GuessConditions;
import by.mishastoma.secondlab.guessnum.GuessNum;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_INPUT_SIZE = 5;

    private int selectedDifficulty;

    private GuessNum guessNum;

    private int currentDifficulty = GuessNum.DEFAULT_LEVEL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            guessNum = new GuessNum();
        } catch (GuessNumberException e) {
            Log.e("FATAL", "Invalid number size");
            fatalErrorCondition();
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
                    if (inputStr.length() > MAX_INPUT_SIZE) {
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
                            case OUTSIDE_BOUNDS:
                                showHint.setText(getHint());
                                showHint.setVisibility(View.VISIBLE);
                                break;
                        }
                        updateAttempts();
                    }
                }
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

    public void setSelectedDifficulty(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                try {
                    currentDifficulty = selectedDifficulty + 2;
                    guessNum = new GuessNum(currentDifficulty);
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.change_level_msg) + Integer.toString(currentDifficulty),
                            Toast.LENGTH_SHORT).show();
                    restart(v);
                } catch (GuessNumberException e) {
                    Log.e("FATAL", e.toString());
                    fatalErrorCondition();
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                selectedDifficulty = currentDifficulty;
            }
        });

        builder.setSingleChoiceItems(R.array.diff_array, selectedDifficulty, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position of the selected item
                selectedDifficulty = which;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
        TextView showMsg = findViewById(R.id.show_msg);
        showMsg.setText(getResources().getString(R.string.guessed));
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.you_win),
                Toast.LENGTH_SHORT).show();
    }

    private void updateAttempts() {
        TextView attempts = findViewById(R.id.show_attempts_left);
        attempts.setText(Integer.toString(guessNum.getAttempts()));
    }

    private void fatalErrorCondition() {
        Toast errorToast = Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.fatal_error_msg), Toast.LENGTH_LONG);
        errorToast.show();
        finish();
        System.exit(0);
    }

    private String getHint() {
        String hint;
        switch (currentDifficulty) {
            case 2:
                hint = getResources().getString(R.string.show_hint_label_2);
                break;
            case 3:
                hint = getResources().getString(R.string.show_hint_label_3);
                break;
            case 4:
                hint = getResources().getString(R.string.show_hint_label_4);
                break;
            default:
                hint = getResources().getString(R.string.invalid_select);
                break;
        }
        return hint;
    }
}