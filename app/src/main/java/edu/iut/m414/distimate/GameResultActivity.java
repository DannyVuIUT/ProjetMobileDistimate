package edu.iut.m414.distimate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import edu.iut.m414.distimate.data.Game;
import edu.iut.m414.distimate.util.DataManager;
import edu.iut.m414.distimate.util.Utilities;
import edu.iut.m414.distimate.util.VibrationManager;

/**
 * Activité affichant les résultats de la partie venant
 * de se terminer et permettant de revenir à l'activité principale
 */
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

        ListView shownQuestionsList = findViewById(R.id.shownQuestionsList);
        ListAdapter adapter = new DistanceQuestionAnswerAdapter(this, Game.getShownQuestions());
        shownQuestionsList.setAdapter(adapter);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener((view) -> returnToMenu());

        // --------------------------------------------------------
        // Technologie non vue : vibration
        // A la fin de la partie, on fait vibrer le téléphone
        // --------------------------------------------------------
        VibrationManager.vibrate(this, 1000);
    }

    public void returnToMenu() {
        Intent intent = new Intent(GameResultActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        returnToMenu();
    }
}
