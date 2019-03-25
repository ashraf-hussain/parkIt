package com.project.parkit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.project.parkit.common.AppConstant;
import com.project.parkit.common.BaseActivity;
import com.project.parkit.common.SetupRetrofit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReservationDetailActivity extends BaseActivity {
    @BindView(R.id.tb_title)
    TextView tbTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_open_spot)
    TextView tvOpenSpot;
    @BindView(R.id.tv_cost)
    TextView tvCost;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_date)
    LinearLayout llDate;
    @BindView(R.id.tv_time)
    TextView tvMaxTime;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.tv_reserve_time)
    TextView tvReserveTime;
    @BindView(R.id.tv_reserve_selection_time)
    TextView tvReserveSelectionTime;
    @BindView(R.id.sb_time)
    SeekBar sbTime;
    @BindView(R.id.tv_cost_per_min)
    TextView tvCostPerMin;
    @BindView(R.id.btn_pay_to_reserve)
    Button btnPayToReserve;
    @BindView(R.id.pb)
    ProgressBar progressBar;
    private int reservationId;
    private Calendar calendar;
    private int mHour, mMinute;
    private DatePickerDialog.OnDateSetListener date;
    String seekbarValue;

    private static final String TAG = ReservationDetailActivity.class.getName();

    @Override
    protected int getLayout() {
        return R.layout.activity_reservation_detail;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void init() {
        calendar = Calendar.getInstance();
        sbTime.setProgress(0);
        notificationBarSetup();
        datePickerAction();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            reservationId = (int) bundle.getInt(AppConstant.RESERVATION_DETAIL, 0);
            Log.d(TAG, "RID: " + reservationId + "");
            reserveSpotDetail(reservationId);
            tvDate.setText(DashboardActivity.reserveDate);
            tvMaxTime.setText(DashboardActivity.reserveDate);
        }
        seekbarAction();
    }


    //ReserveD spot detail
    private void reserveSpotDetail(final int reservationId) {
        progressBar.setVisibility(View.VISIBLE);

        SetupRetrofit setupRetrofit = new SetupRetrofit();
        Retrofit retrofit = setupRetrofit.getRetrofit();

        ParkingApiInterface parkingApiInterface = retrofit.create(ParkingApiInterface.class);
        parkingApiInterface.getReservationDetail(reservationId).enqueue(new Callback<ParkingModel>() {
            @Override
            public void onResponse(Call<ParkingModel> call, Response<ParkingModel> response) {
                Log.d(TAG, "onResponseReserveDetail: " + response.code() + "");
                Log.d(TAG, "onResponseReserveUrlDetail: " + response.raw().request().url() + "");
                progressBar.setVisibility(View.GONE);

                if (response.code() == 200) {

                    tvCostPerMin.setText("$" + response.body().getCostPerMinute() + "/min");
                    tvCost.setText(response.body().getCostPerMinute() + "/min");
                    tvTitle.setText(response.body().getName());
                    tvReserveSelectionTime.setText(response.body().getMaxReserveTimeMins() + "" + " min");
                }


            }

            @Override
            public void onFailure(Call<ParkingModel> call, Throwable t) {
                Log.d(TAG, "onFailureReserveDetail: " + t);
                progressBar.setVisibility(View.GONE);

            }
        });
    }


    //Update reserved spot
    private void updateReserveSpot(final ReservationBody reservationBody, final int markerId) {
        progressBar.setVisibility(View.VISIBLE);

        SetupRetrofit setupRetrofit = new SetupRetrofit();
        Retrofit retrofit = setupRetrofit.getRetrofit();

        ParkingApiInterface parkingApiInterface = retrofit.create(ParkingApiInterface.class);
        parkingApiInterface.updateReserveSpot(reservationBody, markerId).enqueue(new Callback<ReservationBody>() {
            @Override
            public void onResponse(Call<ReservationBody> call, Response<ReservationBody> response) {
                Log.d(TAG, "onResponseReserve: " + response.code() + "");
                Log.d(TAG, "onResponseReserveUrl: " + response.raw().request().url() + "");
                progressBar.setVisibility(View.GONE);

                if (response.code() == 200) {
                    showUpdatedSuccessDialog(markerId);
                }
                if (response.code() == 400) {


                    // Toast.makeText(MainActivity.this, "Minimum time is more the reservation", Toast.LENGTH_SHORT).show();
                    snackBar("Reservation time is less than minimum required time.");

                }
                if (response.code() == 404) {
                    snackBar("Page not found");
                }
            }

            @Override
            public void onFailure(Call<ReservationBody> call, Throwable t) {
                Log.d(TAG, "onFailureReserve: " + t);
                progressBar.setVisibility(View.GONE);

            }
        });
    }


    // Updated Success Dialog
    public void showUpdatedSuccessDialog(final int id) {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_success_message);


        Button btnCheck = (Button) dialog.findViewById(R.id.btn_check);

        TextView btnClose = (TextView) dialog.findViewById(R.id.btn_close);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReservationDetailActivity.this, ReservationDetailActivity.class);
                intent.putExtra(AppConstant.RESERVATION_DETAIL, id);
                startActivity(intent);
                finish();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();

    }


    //TimePicker
    private void timePickerAction() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        String time;
                        if (hourOfDay >= 0 && hourOfDay < 12) {
                            time = hourOfDay + ":" + minute + " AM";
                        } else {
                            if (hourOfDay == 12) {
                                time = hourOfDay + ":" + minute + " PM";
                            } else {
                                hourOfDay = hourOfDay - 12;
                                time = hourOfDay + ":" + minute + " PM";
                            }
                        }


                        tvMaxTime.setText(time);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    //DatePicker
    private void datePickerAction() {
        //DatePicker
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }

            private void updateDate() {
                String dateFormat = "MM-dd-yy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
                tvDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
    }


    //NotificationBar setup
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void notificationBarSetup() {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }


    @OnClick({R.id.toolbar, R.id.ll_date, R.id.ll_time, R.id.btn_pay_to_reserve})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar:
                Intent intent = new Intent(ReservationDetailActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.ll_date:
                new DatePickerDialog(ReservationDetailActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.ll_time:
                timePickerAction();
                break;
            case R.id.btn_pay_to_reserve:

                if (!seekbarValue.equalsIgnoreCase("")) {
                    Log.d(TAG, "onViewClicked: " +seekbarValue);
                    ReservationBody reservationBody = new ReservationBody(Integer.parseInt(seekbarValue));
                    updateReserveSpot(reservationBody, reservationId);
                } else {
                    tvReserveSelectionTime.setTextColor(Color.RED);
                    tvReserveSelectionTime.setText("Required !");


                }
                break;
        }
    }

    public void seekbarAction() {
        sbTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                seekbarValue = String.valueOf(i);

                tvReserveSelectionTime.setText(seekbarValue + "mins");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void snackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(llDate, message, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        snackbar.show();

    }


}

