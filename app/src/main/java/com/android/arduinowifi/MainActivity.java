package com.android.arduinowifi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Switch switchButton = (Switch) findViewById(R.id.switchButton);
        SharedPreferences sharedPreferences = getSharedPreferences("com.android.arduinowifi", MODE_PRIVATE); //ganti sesuai package name
        switchButton.setChecked(sharedPreferences.getBoolean("NameOfThingsToSave", false));
        switchButton.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    SharedPreferences.Editor editor = getSharedPreferences("com.android.arduinowifi", MODE_PRIVATE).edit();//ganti sesuai package name
                    editor.putBoolean("NameOfThingsToSave", false);
                    editor.apply();
                    switchButton.setChecked(true);
                    new Background_get().execute("update?api_key=QR25KLS6KCM4GOY2&field1=1"); //ganti sesuai WriteAPIKey ThingSpeak
                    Toast.makeText(MainActivity.this, "On!", Toast.LENGTH_LONG).show();
                    new CountDownTimer(15000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            switchButton.setEnabled(false);
                        }
                        public void onFinish() {
                            switchButton.setEnabled(true);
                        }
                    }.start();
                }else{
                    SharedPreferences.Editor editor = getSharedPreferences("com.android.arduinowifi", MODE_PRIVATE).edit();//ganti sesuai package name
                    editor.putBoolean("NameOfThingToSave", false);
                    editor.apply();
                    new Background_get().execute("update?api_key=QR25KLS6KCM4GOY2&field1=0"); //ganti sesuai WriteAPIKey ThingSpeak
                    Toast.makeText(MainActivity.this, "Off!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class Background_get extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                /* Change the IP to the IP you set in the arduino sketch
                 */
                URL url = new URL("http://api.thingspeak.com/" + params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    result.append(inputLine).append("\n");
                in.close();
                connection.disconnect();
                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
