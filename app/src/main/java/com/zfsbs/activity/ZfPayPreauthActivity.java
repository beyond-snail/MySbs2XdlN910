package com.zfsbs.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.ALog;
import com.tool.utils.utils.AlertUtils;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.TransUploadRequest;
import com.zfsbs.model.TransUploadResponse;
import com.zfsbs.myapplication.MyApplication;

import java.util.concurrent.ExecutionException;

import static com.zfsbs.common.CommonFunc.startAction;

public class ZfPayPreauthActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "ZfPayActivity";

    private TextView tOrderAmount;
    private TextView tPayAmount;
    private TextView tPayPointAmount;
    private TextView tPayCouponAmount;

//    private LinearLayout btnPayflot;
//    private LinearLayout btnCash;
//    private LinearLayout btnAly;
//    private LinearLayout btnWx;
//    private LinearLayout btnQb;


    private LinearLayout btnPreauth;
    private LinearLayout btnAuthCancel;
    private LinearLayout btnAuthComplete;
    private LinearLayout btnVoidAuthComplete;



    private Button btnPrint;
    private Button btnPrintfinish;
    private Button btnNopayAmount;
    private Button btnQuery;
    private Button btnQueryEnd;

    private LinearLayout ll_payType;
    private LinearLayout ll_payFinish;
    private LinearLayout ll_payQuery;


    private int amount;


    private int app_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_preauth_type);

        app_type = (int) SPUtils.get(this, Config.APP_TYPE, Config.DEFAULT_APP_TYPE);

        amount = getIntent().getIntExtra("amount", 0);


        initView();
        addListenster();

    }

    private void initView() {


        tOrderAmount = (TextView) findViewById(R.id.id_orderAmount);
        tPayAmount = (TextView) findViewById(R.id.id_payAmount);
        tPayPointAmount = (TextView) findViewById(R.id.id_pointAmount);
        tPayCouponAmount = (TextView) findViewById(R.id.id_coupon_amount);

        tOrderAmount.setText(StringUtils.formatIntMoney(amount));
        tPayAmount.setText(StringUtils.formatIntMoney(amount));


        btnPreauth = (LinearLayout) findViewById(R.id.pay_preauth);
        btnAuthCancel = (LinearLayout) findViewById(R.id.pay_authCancel);
        btnAuthComplete = (LinearLayout) findViewById(R.id.pay_authComplete);
        btnVoidAuthComplete = (LinearLayout) findViewById(R.id.pay_voidAuthComplete);


        btnPrint = (Button) findViewById(R.id.id_print);
        btnPrintfinish = (Button) findViewById(R.id.id_finish);
        btnNopayAmount = (Button) findViewById(R.id.id_no_pay_amount);
        btnQuery = (Button) findViewById(R.id.id_query);
        btnQueryEnd = (Button) findViewById(R.id.id_terminal_query_sure);


        ll_payType = (LinearLayout) findViewById(R.id.ll_pay_type);
        ll_payFinish = (LinearLayout) findViewById(R.id.ll_pay_finish);
        ll_payQuery = (LinearLayout) findViewById(R.id.ll_pay_query);

    }





    private void addListenster() {

        btnPreauth.setOnClickListener(this);
        btnAuthCancel.setOnClickListener(this);
        btnAuthComplete.setOnClickListener(this);
        btnVoidAuthComplete.setOnClickListener(this);


        btnPrint.setOnClickListener(this);
        btnPrintfinish.setOnClickListener(this);
        btnNopayAmount.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
        btnQueryEnd.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CommonFunc.startAction(this, InputAmountActivity2.class, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_print:

//                    Gson gson = new Gson();
//                    TransUploadRequest data = gson.fromJson(printerData.getTransUploadData(), TransUploadRequest.class);
//                    LogUtils.e(data.toString());
//                    getPrinterData(data);

                break;
            case R.id.id_finish:
            case R.id.id_terminal_query_sure: {
                startAction(this, InputAmountActivity2.class, true);
            }
                break;
            case R.id.pay_preauth:
                preauth();
                break;
            case R.id.pay_authCancel:
                authCancel();
                break;
            case R.id.pay_authComplete:
                authComplete();
                break;
            case R.id.pay_voidAuthComplete:
                voidAuthComplete();
                break;
            default:
                break;
        }
    }


    /**
     * 预授权
     */
    private void preauth() {
        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();
//        PayCommon.PreAuth(this, amount, mid, tid, new PayCommon.ComTransResult<ComTransInfo>() {
//            @Override
//            public void success(ComTransInfo transInfo) {
//                ToastUtils.CustomShow(ZfPayPreauthActivity.this, "预授权成功");
//                setFlotPrintData1(transInfo, Constants.PAY_WAY_PREAUTH);
//
//                showLayout();
//
//                // 保存打印的数据，不保存图片数据
//                PrinterDataSave();
//                // 打印
//                Printer.print(printerData, ZfPayPreauthActivity.this);
//            }
//
//            @Override
//            public void failed(String error) {
//                final CommonDialog confirmDialog = new CommonDialog(ZfPayPreauthActivity.this, error);
//                confirmDialog.show();
//                confirmDialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
//                    @Override
//                    public void doConfirm() {
//
//                    }
//                });
//            }
//        });
    }


    /**
     * 预授权撤销
     */
    private void authCancel(){

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_set_password, null);
        LinearLayout ll_authcode = (LinearLayout) view.findViewById(R.id.id_ll_authCode);
        ll_authcode.setVisibility(View.VISIBLE);
        LinearLayout ll_date = (LinearLayout) view.findViewById(R.id.id_ll_date);
        ll_date.setVisibility(View.VISIBLE);
        final EditText etName = (EditText) view.findViewById(R.id.et_name);
        final EditText etOldDate = (EditText) view.findViewById(R.id.et_Ic_no);
        AlertUtils.alertSetPassword(this, "输入授权码", "确认",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
                        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();
                        String authCode = etName.getText().toString().trim();
                        String oldDate = etOldDate.getText().toString().trim();
                        if (StringUtils.isBlank(authCode)){
                            ToastUtils.CustomShow(ZfPayPreauthActivity.this, "授权码不可为空");
                            return;
                        }
                        if (StringUtils.isBlank(oldDate)){
                            ToastUtils.CustomShow(ZfPayPreauthActivity.this, "输入日期");
                            return;
                        }
//                        PayCommon.AuthCancel(ZfPayPreauthActivity.this, amount, mid, tid, authCode, oldDate, new PayCommon.ComTransResult<ComTransInfo>() {
//                            @Override
//                            public void success(ComTransInfo transInfo) {
//                                ToastUtils.CustomShow(ZfPayPreauthActivity.this, "预授权撤销成功");
//                                setFlotPrintData1(transInfo, Constants.PAY_WAY_AUTHCANCEL);
//
//                                showLayout();
//
//                                // 保存打印的数据，不保存图片数据
//                                PrinterDataSave();
//                                // 打印
//                                Printer.print(printerData, ZfPayPreauthActivity.this);
//                            }
//
//                            @Override
//                            public void failed(String error) {
//                                final CommonDialog confirmDialog = new CommonDialog(ZfPayPreauthActivity.this, error);
//                                confirmDialog.show();
//                                confirmDialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
//                                    @Override
//                                    public void doConfirm() {
//
//                                    }
//                                });
//                            }
//                        });
                    }

                }, view);


    }


    /**
     * 预授权完成
     */
    private void authComplete(){
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_set_password, null);
        LinearLayout ll_authcode = (LinearLayout) view.findViewById(R.id.id_ll_authCode);
        ll_authcode.setVisibility(View.VISIBLE);
        LinearLayout ll_date = (LinearLayout) view.findViewById(R.id.id_ll_date);
        ll_date.setVisibility(View.VISIBLE);
        final EditText etName = (EditText) view.findViewById(R.id.et_name);
        final EditText etOldDate = (EditText) view.findViewById(R.id.et_Ic_no);

        AlertUtils.alertSetPassword(this, "输入原交易信息", "确认",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
                        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();
                        String authCode = etName.getText().toString().trim();
                        String oldDate = etOldDate.getText().toString().trim();
                        if (StringUtils.isBlank(authCode)){
                            ToastUtils.CustomShow(ZfPayPreauthActivity.this, "授权码不可为空");
                            return;
                        }
                        if (StringUtils.isBlank(oldDate)){
                            ToastUtils.CustomShow(ZfPayPreauthActivity.this, "输入日期");
                            return;
                        }
//                        PayCommon.AuthComplete(ZfPayPreauthActivity.this, amount, mid, tid, authCode, oldDate, new PayCommon.ComTransResult<ComTransInfo>() {
//                            @Override
//                            public void success(ComTransInfo transInfo) {
//                                ToastUtils.CustomShow(ZfPayPreauthActivity.this, "预授权完成成功");
//                                setFlotPrintData1(transInfo, Constants.PAY_WAY_AUTHCOMPLETE);
//                                showLayout();
//
//                                // 保存打印的数据，不保存图片数据
//                                PrinterDataSave();
//                                // 打印
//                                Printer.print(printerData, ZfPayPreauthActivity.this);
//                            }
//
//                            @Override
//                            public void failed(String error) {
//                                final CommonDialog confirmDialog = new CommonDialog(ZfPayPreauthActivity.this, error);
//                                confirmDialog.show();
//                                confirmDialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
//                                    @Override
//                                    public void doConfirm() {
//
//                                    }
//                                });
//                            }
//                        });
                    }

                }, view);

    }

    private void voidAuthComplete(){

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_set_password, null);
        LinearLayout ll_trace_no = (LinearLayout) view.findViewById(R.id.id_ll_trace_no);
        ll_trace_no.setVisibility(View.VISIBLE);
        final EditText ettrace = (EditText) view.findViewById(R.id.et_trace);
        AlertUtils.alertSetPassword(this, "输入原交易信息", "确认",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
                        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();

                        if (StringUtils.isBlank(ettrace.getText().toString().trim())){
                            ToastUtils.CustomShow(ZfPayPreauthActivity.this, "原交易流水不可为空");
                            return;
                        }
                        int trace_no = Integer.parseInt(ettrace.getText().toString());
//                        PayCommon.VoidAuthComplete(ZfPayPreauthActivity.this, amount, mid, tid,  trace_no, new PayCommon.ComTransResult<ComTransInfo>() {
//                            @Override
//                            public void success(ComTransInfo transInfo) {
//                                ToastUtils.CustomShow(ZfPayPreauthActivity.this, "预授权完成撤销成功");
//                                setFlotPrintData1(transInfo, Constants.PAY_WAY_VOID_AUTHCOMPLETE);
//                                showLayout();
//
//                                // 保存打印的数据，不保存图片数据
//                                PrinterDataSave();
//                                // 打印
//                                Printer.print(printerData, ZfPayPreauthActivity.this);
//                            }
//
//                            @Override
//                            public void failed(String error) {
//                                final CommonDialog confirmDialog = new CommonDialog(ZfPayPreauthActivity.this, error);
//                                confirmDialog.show();
//                                confirmDialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
//                                    @Override
//                                    public void doConfirm() {
//
//                                    }
//                                });
//                            }
//                        });
                    }

                }, view);


    }


    /**
     * 刷卡
     */
    private void payflot1() {

        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();
//        PayCommon.sale(this, CommonFunc.recoveryMemberInfo(this).getRealMoney(), mid, tid, new PayCommon.ComTransResult<ComTransInfo>() {
//            @Override
//            public void success(ComTransInfo transInfo) {
//                //设置打印的信息
////                setFlotPrintData1(transInfo);
//
//                //判断是否是商博士
//                if (app_type == Config.APP_SBS) {
//
//                    //设置流水上送需要上送的参数
//                    TransUploadRequest request = CommonFunc.setTransUploadData(printerData,
//                            CommonFunc.recoveryMemberInfo(ZfPayPreauthActivity.this),
//                            CommonFunc.getNewClientSn(ZfPayPreauthActivity.this, printerData.getPayType()),
//                            printerData.getVoucherNo(), printerData.getReferNo());
//
//                    //打印的订单号与流水上送的统一
//                    printerData.setClientOrderNo(request.getClientOrderNo());
//
//                    //流水上送
//                    transUploadAction1(request);
//                }
//            }
//
//            @Override
//            public void failed(String error) {
//                final CommonDialog confirmDialog = new CommonDialog(ZfPayPreauthActivity.this, error);
//                confirmDialog.show();
//                confirmDialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
//                    @Override
//                    public void doConfirm() {
//
//                    }
//                });
//            }
//        });


    }









//    protected void setFlotPrintData1(ComTransInfo transInfo, int type) {
//        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
//        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getMerchantNo());//(transInfo.getMid());
//        printerData.setTerminalId(transInfo.getTid());
//        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
//        printerData.setAcquirer(transInfo.getAcquirerCode());
//        printerData.setAuthNo(transInfo.getAuthCode());
//        printerData.setIssuer(transInfo.getIssuerCode());
//        printerData.setCardNo(StringUtils.formatCardNo(transInfo.getPan()));
//        printerData.setTransType(transInfo.getTransType() + "");
//        printerData.setExpDate(transInfo.getExpiryDate());
//        printerData.setBatchNO(StringUtils.fillZero(transInfo.getBatchNumber() + "", 6));
//        printerData.setVoucherNo(StringUtils.fillZero(transInfo.getTrace() + "", 6));
//        printerData.setDateTime(
//                StringUtils.formatTime(StringUtils.getCurYear() + transInfo.getTransDate() + transInfo.getTransTime()));
//        printerData.setAuthNo(transInfo.getAuthCode());
//        printerData.setReferNo(transInfo.getRrn());
//        printerData.setOrderAmount(amount);
//        printerData.setAmount(StringUtils.formatIntMoney(transInfo.getTransAmount()));
//        printerData.setPayType(type);
//    }





    /**
     * 保存数据
     */
    private void PrinterDataSave() {

        CommonFunc.ClearFailureInfo(this);
        CommonFunc.PrinterDataDelete();
        printerData.setStatus(true);
        if (printerData.save()) {
            LogUtils.e("打印数据存储成功");
        } else {
            LogUtils.e("打印数据存储失败");
        }
    }


    private void getPrinterData(final TransUploadRequest request) {

        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show("获取打印信息...");
        this.sbsAction.getPrinterData(this, request.getSid(), request.getClientOrderNo(), new ActionCallbackListener<TransUploadResponse>() {

            @Override
            public void onSuccess(TransUploadResponse data) {
                setTransUpdateResponse(data, dialog, false);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                dialog.dismiss();
                ToastUtils.CustomShow(ZfPayPreauthActivity.this, errorEvent + "#" + message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayPreauthActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayPreauthActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }


    /**
     * 流水上送
     *
     * @param request
     */
    private void transUploadAction1(final TransUploadRequest request) {
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show("正在上传交易流水...");
        dialog.setCancelable(false);
        this.sbsAction.transUpload(this, request, new ActionCallbackListener<TransUploadResponse>() {
            @Override
            public void onSuccess(TransUploadResponse data) {

                setTransUpLoadData(request);
                // 设置流水返回的数据
                setTransUpdateResponse(data, dialog, true);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                dialog.dismiss();
                ToastUtils.CustomShow(ZfPayPreauthActivity.this, errorEvent + "#" + message);
                showLayout();

                setTransUpLoadData(request);
                // 设置当前交易流水需要上送
                printerData.setUploadFlag(true);
                printerData.setApp_type(app_type);
                // 保存打印的数据，不保存图片数据
                PrinterDataSave();
                // 打印
//                Printer.print(printerData, ZfPayPreauthActivity.this);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayPreauthActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayPreauthActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }







    private void showLayout() {
        ll_payType.setVisibility(View.GONE);
        ll_payFinish.setVisibility(View.VISIBLE);
    }

    /**
     * 将流水上送的数据转成字串保存在打印的对象中
     * 不管成功失败，流水上送的数据保存下来
     *
     * @param request
     */
    private void setTransUpLoadData(TransUploadRequest request) {
        Gson gson = new Gson();
        String data = gson.toJson(request);
//        LogUtils.e(data);
        ALog.json(data);
        printerData.setTransUploadData(data);
    }


    /**
     * 用来返回主线程 打印小票
     */
    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Bitmap point_bitmap = bundle.getParcelable("point_bitmap");
            Bitmap title_bitmap = bundle.getParcelable("title_bitmap");
            printerData.setPoint_bitmap(point_bitmap);
            printerData.setCoupon_bitmap(title_bitmap);

            showLayout();

            // 打印
//            Printer.getInstance(ZfPayPreauthActivity.this).print(printerData, ZfPayPreauthActivity.this);

        }


    };

    protected void setTransUpdateResponse(final TransUploadResponse data, final LoadingDialog dialog, boolean flag) {
        printerData.setPoint_url(data.getPoint_url());
        printerData.setPoint(data.getPoint());
        printerData.setPointCurrent(data.getPointCurrent());
        printerData.setCoupon(data.getCoupon());
        printerData.setTitle_url(data.getTitle_url());
        printerData.setMoney(data.getMoney());
        printerData.setBackAmt(data.getBackAmt());
        printerData.setApp_type(app_type);
        if (flag) {
            // 保存打印的数据，不保存图片数据
            PrinterDataSave();
        }

        //开启线程下载二维码图片
        new Thread(new Runnable() {

            @Override
            public void run() {

                Bitmap point_bitmap = null;
                Bitmap title_bitmap = null;
                if (!StringUtils.isEmpty(data.getPoint_url())) {
                    try {
                        point_bitmap = Glide.with(getApplicationContext())
                                .load(data.getPoint_url())
                                .asBitmap()
                                .centerCrop()
                                .into(200, 200).get();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                if (!StringUtils.isEmpty(data.getCoupon())) {

                    try {
                        title_bitmap = Glide.with(getApplicationContext())
                                .load(data.getCoupon())
                                .asBitmap()
                                .centerCrop()
                                .into(200, 200).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }


                dialog.dismiss();

                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putParcelable("point_bitmap", point_bitmap);
                bundle.putParcelable("title_bitmap", title_bitmap);
                msg.setData(bundle);
                mhandler.sendMessage(msg);

            }
        }).start();

    }


}
