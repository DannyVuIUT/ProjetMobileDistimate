package edu.iut.m414.distimate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import edu.iut.m414.distimate.data.CountryList;
import edu.iut.m414.distimate.util.DataManager;

public class MainActivity extends AppCompatActivity implements CountryAdapterListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CountryList.constructCountries(getApplicationContext());

        CountryAdapter adapter = new CountryAdapter(this);
        ListView list = findViewById(R.id.listView);
        list.setAdapter(adapter);
        adapter.addListener(this);
    }

    @Override
    public void onClickCountry(int position) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra(DataManager.COUNTRY, position);
        startActivity(intent);
    }
}