package by.mishastoma.secondlab;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import by.mishastoma.secondlab.guessnum.GuessNum;
import by.mishastoma.secondlab.user.User;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        EditText input = findViewById(R.id.editTextTextUserName);
        input.setText(User.name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeUsername(View v) {
        EditText input = findViewById(R.id.editTextTextUserName);
        String username = input.getText().toString();
        User.name = username;
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.changed_name),
                Toast.LENGTH_SHORT).show();
    }

}