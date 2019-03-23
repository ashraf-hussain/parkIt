package com.project.parkit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import butterknife.BindView;
import butterknife.ButterKnife;

class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    Context context;
    View mWindow;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_subtitle)
    TextView tvSubtitle;
    @BindView(R.id.tv_haha)
    TextView tvHaha;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_xx)
    TextView tvXx;
    @BindView(R.id.btn_pay_reserve)
    Button btnPayReserve;
    private static final String TAG = MyInfoWindowAdapter.class.getName();

    public MyInfoWindowAdapter(Context context) {
        context = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
        ButterKnife.bind(this, mWindow);

    }

    @Override
    public View getInfoWindow(Marker marker) {

        tvTitle.setText(marker.getTitle());
        tvSubtitle.setText(marker.getSnippet());

        ParkingModel infoWindowData = (ParkingModel) marker.getTag();


//        tvHaha.setText(infoWindowData.getCostPerMinute() + "");
//        tvTime.setText(infoWindowData.getMaxReserveTimeMins());
//        tvXx.setText(infoWindowData.getMinReserveTimeMins());

//        Log.d(TAG, "getInfoWindow: " + infoWindowData.getMaxReserveTimeMins());
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return mWindow;
    }
}
