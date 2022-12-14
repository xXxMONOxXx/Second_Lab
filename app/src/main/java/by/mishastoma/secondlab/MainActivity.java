package by.mishastoma.secondlab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.actions.NoteIntents;

import java.text.SimpleDateFormat;

import by.mishastoma.secondlab.guessnum.GuessConditions;
import by.mishastoma.secondlab.guessnum.GuessNum;
import by.mishastoma.secondlab.user.User;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int MAX_INPUT_SIZE = 5;

    private int selectedDifficulty;

    private GuessNum guessNum;

    private int currentDifficulty = GuessNum.DEFAULT_LEVEL;

    private NotificationManagerCompat notificationManagerCompat;
    private Notification notification;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.beep_1:
                settings();
                break;
            case R.id.boop_1:
                about();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings_menu_item:
                settings();
                break;
            case R.id.about_menu_item:
                about();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    protected void onDestroy(){
        Log.i(LOG_TAG, "Guess number .onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.i(LOG_TAG, "Guess number .onResume()");
        super.onResume();
        loadElements();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(LOG_TAG, "Guess number .onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        notificationManagerCompat.notify(1,notification);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        Log.i(LOG_TAG, "Guess number .onRestoreInstanceState()");
        super.onRestoreInstanceState(state);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);
        loadElements();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(LOG_TAG, "Guess number .onCreate()");
        User.name = getResources().getString(R.string.default_username);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            guessNum = new GuessNum();
        } catch (GuessNumberException e) {
            Log.e("FATAL", "Invalid number size");
            fatalErrorCondition();
        }
        loadElements();
        TextView v = findViewById(R.id.show_attempts_left);
        registerForContextMenu(v);
        addNotification();
    }

    @Override
    protected void onRestart(){
        Log.i(LOG_TAG, "Guess number .onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStart() {
        Log.i(LOG_TAG, "Guess number .onStart()");
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
                selectedDifficulty = currentDifficulty - 2;
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

    public void settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void share(View v) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String timeStamp = new SimpleDateFormat("dd.MM.yyyy\nHH:mm:ss").format(new java.util.Date());
        sendIntent.putExtra(Intent.EXTRA_TEXT, timeStamp + '\n' + Integer.toString(guessNum.getGeneratedNumber()));
        sendIntent.setType("text/plain");

//        Intent shareIntent = Intent.createChooser(sendIntent, null);
//        startActivity(shareIntent);

        Intent saveNotes = new Intent(NoteIntents.ACTION_CREATE_NOTE).
                putExtra(NoteIntents.EXTRA_NAME, User.name).
                putExtra(NoteIntents.EXTRA_TEXT, timeStamp + '\n' + Integer.toString(guessNum.getGeneratedNumber()));
        if(saveNotes.resolveActivity(getPackageManager()) != null) {
            startActivity(saveNotes);
        }
        else{
            Log.e("FATAL", "Can't find notes.");
        }
    }

    public void loadElements() {
        TextView username = findViewById(R.id.user_name_msg);
        username.setText(User.name);
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

    private void about(){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "First";
            String description = "Second";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("myCh", name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void addNotification() {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "myCh")
                .setSmallIcon(R.drawable.ic_settings_icon)
                .setContentTitle("Guess num")
                .setContentText("GO BACK TO THE GAME!!1")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true);

        notification = builder.build();
        notificationManagerCompat = NotificationManagerCompat.from(this);
    }
}