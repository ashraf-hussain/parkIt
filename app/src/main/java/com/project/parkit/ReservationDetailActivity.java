package com.project.parkit;

import android.content.Intent;
import android.util.Log;

import com.project.parkit.common.AppConstant;
import com.project.parkit.common.BaseActivity;

public class ReservationDetailActivity extends BaseActivity {
    private int reservationId;

    private static final String TAG = ReservationDetailActivity.class.getName();
    @Override
    protected int getLayout() {
        return R.layout.activity_reservation_detail;
    }

    @Override
    protected void init() {

        Intent intent = getIntent();
        if(intent.getExtras() == null) {
            reservationId = (int) intent.getIntExtra(AppConstant.RESERVATION_DETAIL,0);
            Log.d(TAG, "RID: "+reservationId+"");
        }
    }
}
