package com.project.parkit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

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
    TextView tvCost;
    @BindView(R.id.tv_max_time)
    TextView tvMaxTime;
    @BindView(R.id.tv_min_time)
    TextView tvMinTime;
    @BindView(R.id.btn_pay_reserve)
    Button btnPayReserve;
    private static final String TAG = MyInfoWindowAdapter.class.getName();

    List<ParkingModel> parkingModelList;
    ParkingModel parkingModel;

    public MyInfoWindowAdapter(Context context, List<ParkingModel> parkingModelList) {
        this.context = context;
        this.parkingModelList = parkingModelList;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
        ButterKnife.bind(this, mWindow);


        tvSubtitle.setText(marker.getSnippet());


        //ParkingModel infoWindowData = (ParkingModel) marker.getTag();


        for (int i = 0; i < parkingModelList.size(); i++) {

            parkingModel = parkingModelList.get(5);
            tvTitle.setText(parkingModel.getName());
            tvCost.setText   (parkingModel.getCostPerMinute() + "");
            tvMaxTime.setText(parkingModel.getMaxReserveTimeMins()+"");
            tvMinTime.setText(parkingModel.getMinReserveTimeMins()+"");
            Log.d(TAG, "getInfoWindow: " + parkingModel.getName());

        }


        btnPayReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
