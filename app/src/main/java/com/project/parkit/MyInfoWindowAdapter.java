package com.project.parkit;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    @BindView(R.id.tv_join_all)
    TextView tvJoinAll;

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

        Log.d(TAG, "getInfoWindowTag: " + marker.getTitle());

        for (int i = 0; i < parkingModelList.size(); i++) {
//            if (!parkingModelList.get(i).getIsReserved()){
//                Log.d(TAG, "getInfoWindowReserved: "+parkingModelList.get(i).getIsReserved() );
//                marker.remove();
//            }else {
//                Log.d(TAG, "getInfoWindowReservedElse: "+parkingModelList.get(i).getIsReserved() );
//
//            }

            parkingModel = parkingModelList.get(i);
            marker.getZIndex();
            Log.d(TAG, "getInfoWindowIndex: "+marker.getZIndex()+"");
            tvTitle.setText(marker.getTitle());
            tvJoinAll.setText(marker.getSnippet());
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
