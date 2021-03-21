package edu.iut.m414.distimate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import edu.iut.m414.distimate.data.DistanceQuestion;
import edu.iut.m414.distimate.util.Utilities;

public class DistanceQuestionAnswerAdapter extends BaseAdapter {
    private final DistanceQuestion[] questionAnswers;
    private final Context context;
    private final LayoutInflater inflater;

    public DistanceQuestionAnswerAdapter(Context context, DistanceQuestion[] questionAnswers) {
        this.questionAnswers = questionAnswers;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return questionAnswers.length;
    }

    @Override
    public DistanceQuestion getItem(int position) {
        return questionAnswers[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layoutItem;

        if (convertView == null) {
            layoutItem = (LinearLayout) inflater.inflate(R.layout.distance_question_answer_layout, parent, false);
        } else {
            layoutItem = (LinearLayout) convertView;
        }

        TextView fromCityText = layoutItem.findViewById(R.id.answerFromCityText);
        TextView toCityText = layoutItem.findViewById(R.id.answerToCityText);
        TextView actualDistanceText = layoutItem.findViewById(R.id.answerActualDistanceText);

        DistanceQuestion currentQuestion = questionAnswers[position];

        fromCityText.setText(currentQuestion.getFrom());
        toCityText.setText(currentQuestion.getTo());
        actualDistanceText.setText(String.format(
                layoutItem.getContext().getString(R.string.distance),
                Utilities.formatNumber(currentQuestion.getActualDistance(), context)));

        return layoutItem;
    }
}
