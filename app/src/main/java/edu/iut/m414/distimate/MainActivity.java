package edu.iut.m414.distimate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import edu.iut.m414.distimate.data.CountryList;
import edu.iut.m414.distimate.util.DataManager;
import edu.iut.m414.distimate.util.VibrationManager;

/**
 * ----------------------------------------------------------------------
 * Acitivté permettant de choisir le pays dans lequel on souhaite jouer
 * utilisant un fichier JSON,
 * un adapter,
 * une classe Singleton,
 * et une technologie non-vue (la vibration du téléphone)
 * ----------------------------------------------------------------------
 */
public class MainActivity extends AppCompatActivity implements CountryAdapterListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ----------------------------------------------------------------------
        // Utilisation/chargement d'un fichier JSON dans CountryList
        // ----------------------------------------------------------------------
        // Utilisation du pattern Singleton dans CountryList
        // ----------------------------------------------------------------------
        CountryList.constructCountries(getApplicationContext());

        // ----------------------------------------------------------------------
        // Utilisation d'un adapter : CountryAdapter (affiche les différents pays
        // lors du lancement de l'application)
        // ----------------------------------------------------------------------
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
            // ----------------------------------------------------------------------
            // Utilisation du pattern Singleton et de plusieurs activités
            // ----------------------------------------------------------------------
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra(DataManager.KEY_COUNTRY, position);
            startActivity(intent);
            // ----------------------------------------------------------------------
            // Tecgnologie non vue : vibration du téléphone
            // (Voir classe VibrationManager dans le package 'util')
            // ----------------------------------------------------------------------
            VibrationManager.vibrate(this);
            finish();
        });
        builder.show();
    }
}