package edu.iut.m414.distimate;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.iut.m414.distimate.util.DataManager;

public class GameResultActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_game_result);

            Intent intent = getIntent();
            if (intent != null) {
                int countryIndex = intent.getIntExtra(DataManager.SCORE, 0);

            }
        }

        public void returnToMenu(){
            Intent intent = new Intent(GameResultActivity.this, MainActivity.class);
            startActivity(intent);
        }
}
