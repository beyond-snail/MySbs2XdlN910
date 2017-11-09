package com.zfsbs.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.tool.utils.activityManager.AppManager;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.ShiftRoom;
import com.zfsbs.myapplication.MyApplication;

import static com.zfsbs.common.CommonFunc.startAction;

public class ShiftRoomActivity extends BaseActivity {

    private RelativeLayout btnShitRoom;
    private RelativeLayout btnShitRoomDay;
    private RelativeLayout btnShitRoomRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_room);
//        AppManager.getAppManager().addActivity(this);

        btnShitRoom = (RelativeLayout) findViewById(R.id.id_ll_shiftroom);
        btnShitRoomDay = (RelativeLayout) findViewById(R.id.id_ll_shiftroom_day);
        btnShitRoomRecord = (RelativeLayout) findViewById(R.id.id_ll_shiftroom_record);


        btnShitRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getShiftRoom();
            }
        });

        btnShitRoomDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getShiftRoomDay();
            }
        });

        btnShitRoomRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAction(ShiftRoomActivity.this, ShiftRoomRecordActivity.class, false);
            }
        });

    }




    /**
     * 班接
     */
    private void getShiftRoom() {

        int sid = MyApplication.getInstance().getLoginData().getSid();
        final long start_time = StringUtils.getdate2TimeStamp((String) SPUtils.get(this, Constants.SHIFT_ROOM_TIME, Constants.DEFAULT_SHIFT_ROOM_TIME));
        LogUtils.e("start_time=", (String) SPUtils.get(this, Constants.SHIFT_ROOM_TIME, Constants.DEFAULT_SHIFT_ROOM_TIME));
        final long end_time = StringUtils.getdate2TimeStamp(StringUtils.getCurTime());
        LogUtils.e("end_time", StringUtils.getCurTime());
        this.sbsAction.shift_room(this, sid, start_time, end_time, new ActionCallbackListener<ShiftRoom>() {
            @Override
            public void onSuccess(final ShiftRoom data) {
                LogUtils.e("onSucess"+data.toString());


                Bundle bundle = new Bundle();
                bundle.putSerializable("ShiftRoom", data);
                bundle.putLong("start_time", start_time);
                bundle.putLong("end_time", end_time);
                bundle.putInt("type", Constants.PRINTER_SHIFT_ROOM);
                startAction(ShiftRoomActivity.this, ShiftRoomShowActivity.class, bundle, false);

            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(ShiftRoomActivity.this, errorEvent+message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ShiftRoomActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ShiftRoomActivity.this, OperatorLoginActivity1.class, false);
                }

            }
        });
    }

    /**
     * 班接
     */
    private void getShiftRoomDay() {

        int sid = MyApplication.getInstance().getLoginData().getSid();
        final long start_time = StringUtils.getdate2TimeStamp(StringUtils.formatTime(StringUtils.getCurDate()+"000000"));
        LogUtils.e("start_time=", StringUtils.formatTime(StringUtils.getCurDate()+"000000"));
        final long end_time = StringUtils.getdate2TimeStamp(StringUtils.getCurTime());
        LogUtils.e("end_time", StringUtils.getCurTime());
        this.sbsAction.shift_room(this, sid, start_time, end_time, new ActionCallbackListener<ShiftRoom>() {
            @Override
            public void onSuccess(final ShiftRoom data) {
                LogUtils.e("onSuccess"+data.toString());

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("ShiftRoom", data);
//                        bundle.putLong("start_time", start_time);
//                        bundle.putLong("end_time", end_time);
//                        bundle.putInt("type", Constants.PRINTER_SHIFT_ROOM_DAY);
//                        startAction(ShiftRoomActivity.this, ShiftRoomShowActivity.class, bundle, false);
//                    }
//                });

                Bundle bundle = new Bundle();
                bundle.putSerializable("ShiftRoom", data);
                bundle.putLong("start_time", start_time);
                bundle.putLong("end_time", end_time);
                bundle.putInt("type", Constants.PRINTER_SHIFT_ROOM_DAY);
                startAction(ShiftRoomActivity.this, ShiftRoomShowActivity.class, bundle, false);

            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(ShiftRoomActivity.this, errorEvent+message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ShiftRoomActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ShiftRoomActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }
}
