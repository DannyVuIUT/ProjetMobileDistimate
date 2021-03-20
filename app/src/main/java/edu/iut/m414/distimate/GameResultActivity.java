package edu.iut.m414.distimate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import edu.iut.m414.distimate.util.DataManager;
import edu.iut.m414.distimate.util.Utilities;
import edu.iut.m414.distimate.util.VibrationManager;

public class GameResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);

        Intent intent = getIntent();
        if (intent != null) {
            int score = intent.getIntExtra(DataManager.KEY_SCORE, 0);
            TextView scoreTextView = findViewById(R.id.finalScoreText);
            scoreTextView.setText(Utilities.formatNumber(score, this));
        }

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener((view) -> returnToMenu());

        VibrationManager.vibrate(this, 1000);
    }

    public void returnToMenu() {
        Intent intent = new Intent(GameResultActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        returnToMenu();
    }
}
