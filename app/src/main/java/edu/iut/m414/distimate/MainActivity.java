package edu.iut.m414.distimate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import edu.iut.m414.distimate.data.CountryList;
import edu.iut.m414.distimate.util.DataManager;
import edu.iut.m414.distimate.util.VibrationManager;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.country);
        builder.setMessage(getString(R.string.you_selected) + getString(CountryList.get(position).getNameId()));
        builder.setNegativeButton(R.string.cancel,null);
        builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra(DataManager.COUNTRY, position);
            startActivity(intent);
            VibrationManager.vibrate(this);
            finish();
        });
        builder.show();
    }
}