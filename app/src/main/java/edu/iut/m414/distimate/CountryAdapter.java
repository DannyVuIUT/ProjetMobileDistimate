package edu.iut.m414.distimate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import edu.iut.m414.distimate.data.Country;
import edu.iut.m414.distimate.data.CountryList;

public class CountryAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private NumberFormat numberFormat;
    private List<CountryAdapterListener> listeners;

    public CountryAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listeners = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return CountryList.size();
    }

    @Override
    public Object getItem(int position) {
        return CountryList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ConstraintLayout layoutItem;

        if (convertView == null) {
            layoutItem = (ConstraintLayout) inflater.inflate(R.layout.country_layout, parent, false);
        } else {
            layoutItem = (ConstraintLayout) convertView;
        }

        Country currentCountry = CountryList.get(position);

        ImageView countryImage = layoutItem.findViewById(R.id.imageCountry);
        ImageView countryFlag = layoutItem.findViewById(R.id.imageFlag);
        TextView countryName = layoutItem.findViewById(R.id.countryName);
        TextView countryArea = layoutItem.findViewById(R.id.countryArea);
        TextView countryCities = layoutItem.findViewById(R.id.countryCities);

        countryFlag.setImageResource(currentCountry.getFlag());
        countryImage.setImageResource(currentCountry.getImage());
        countryName.setText(context.getString(currentCountry.getNameId()));
        countryArea.setText("" + currentCountry.getArea());
        countryCities.setText("" + currentCountry.getCitiesCount());

        layoutItem.setTag(position);

        layoutItem.setOnClickListener((view) -> {
            int itemPosition = (Integer)view.getTag();
            sendListener(itemPosition);
        });

        return layoutItem;
    }

    public void addListener(CountryAdapterListener listener){
        listeners.add(listener);
    }

    private void sendListener(int position){
        for(CountryAdapterListener listener : listeners){
            listener.onClickCountry(position);
        }
    }
}
