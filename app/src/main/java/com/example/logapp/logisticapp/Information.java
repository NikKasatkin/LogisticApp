package com.example.logapp.logisticapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Information extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
    }
    public void onButtonMAIN(View view){

        Intent intent = new Intent(this, Add_Information.class);
        startActivity(intent);

    }
    public void onButtonjust(View view){

        Toast.makeText(getApplicationContext(), "просто кнопка, прозапас", Toast.LENGTH_LONG).show();

    }
}
