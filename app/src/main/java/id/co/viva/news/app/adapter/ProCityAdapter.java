package id.co.viva.news.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.model.City;
import id.co.viva.news.app.model.Province;

/**
 * Created by reza on 22/12/14.
 */
public class ProCityAdapter extends BaseAdapter {

    private ArrayList<Province> provinceArrayList;
    private ArrayList<City> cityArrayList;
    private Context context;
    private String mType;

    public ProCityAdapter(ArrayList<Province> provinceArrayList, ArrayList<City> cityArrayList,
                          Context context, String mType) {
        this.context = context;
        this.provinceArrayList = provinceArrayList;
        this.cityArrayList = cityArrayList;
        this.mType = mType;
    }

    @Override
    public int getCount() {
        if(mType.equals(Constant.ADAPTER_PROVINCE)) {
            return provinceArrayList.size();
        } else if(mType.equals(Constant.ADAPTER_CITY)) {
            return cityArrayList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(mType.equals(Constant.ADAPTER_PROVINCE)) {
            return provinceArrayList.get(position);
        } else if(mType.equals(Constant.ADAPTER_CITY)) {
            return cityArrayList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context)
                .inflate(R.layout.item_spin_procity, viewGroup, false);

        TextView nama = (TextView) rootView.findViewById(R.id.text_item_name);
        TextView id_propinsi = (TextView) rootView.findViewById(R.id.text_item_id_propinsi);
        TextView id_kabupaten = (TextView) rootView.findViewById(R.id.text_item_id_kabupaten);

        if(mType.equals(Constant.ADAPTER_PROVINCE)) {
            Province province = provinceArrayList.get(position);
            nama.setText(province.getNama());
            id_propinsi.setText(province.getId_propinsi());
            id_kabupaten.setText(province.getId_kabupaten());
        }

        if(mType.equals(Constant.ADAPTER_CITY)) {
            City city = cityArrayList.get(position);
            nama.setText(city.getNama());
            id_propinsi.setText(city.getId_propinsi());
            id_kabupaten.setText(city.getId_kabupaten());
        }

        return rootView;
    }

}
