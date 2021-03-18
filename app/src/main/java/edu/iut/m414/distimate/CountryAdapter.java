package edu.iut.m414.distimate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.text.NumberFormat;

import edu.iut.m414.distimate.data.CountryList;

public class CountryAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private NumberFormat numberFormat;

    public CountryAdapter(Context context){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
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
        return null;
    }
}
