package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    String result = "";


    public class DownloadData extends AsyncTask<String , Void ,String> {
        @Override
        protected String doInBackground(String... urls) {
            String result ="";
            URL url;
            HttpURLConnection urlConnection;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }
            catch (Exception e) {
                e.printStackTrace();
                return result;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.i("JSON" , s);
            TextView textViewResult = (TextView) findViewById(R.id.textViewResult);


            try {
                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather info" , weatherInfo);

                JSONArray jsonArray = new JSONArray(weatherInfo);
                String message = "";

                for (int i =0; i < jsonArray.length(); i++){
                    JSONObject object = jsonArray.getJSONObject(i);

                    String main =  object.getString("main");
                    String description =  object.getString("description");

                    if (!main.equals("") && !description.equals("")){

                        message = main +" : " + description +"\r\n";
                    }
                    else Toast.makeText(MainActivity.this, "something wrong", Toast.LENGTH_SHORT).show();

                }
                if (!message.equals(""))
                    textViewResult.setText(message);


            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "something wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClick(View view){
        DownloadData  data = new DownloadData();
        EditText text = (EditText) findViewById(R.id.editText);

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(text.getWindowToken(), 0);

        try {
            result = data.execute("https://openweathermap.org/data/2.5/weather?q="+ text.getText().toString() +"&appid=439d4b804bc8187953eb36d2a8c26a02").get();

        } catch (ExecutionException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "something wrong", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "something wrong", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}