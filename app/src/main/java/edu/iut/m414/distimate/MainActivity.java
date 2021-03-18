package edu.iut.m414.distimate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import edu.iut.m414.distimate.data.CountryList;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CountryList.constructCountries(getApplicationContext());
    }

}