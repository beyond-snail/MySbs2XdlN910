package com.zfsbs.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.ALog;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.core.action.FyBat;
import com.zfsbs.core.action.ZfQbAction;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.FailureData;
import com.zfsbs.model.FyMicropayRequest;
import com.zfsbs.model.FyMicropayResponse;
import com.zfsbs.model.FyQueryRequest;
import com.zfsbs.model.FyQueryResponse;
import com.zfsbs.model.FyRefundResponse;
import com.zfsbs.model.MemberTransAmountResponse;
import com.zfsbs.model.SbsPrinterData;
import com.zfsbs.model.TransUploadRequest;
import com.zfsbs.model.TransUploadResponse;
import com.zfsbs.model.ZfQbResponse;
import com.zfsbs.myapplication.MyApplication;

import org.litepal.crud.DataSupport;

import java.util.concurrent.ExecutionException;

import static com.zfsbs.common.CommonFunc.startAction;
import static com.zfsbs.common.CommonFunc.startResultAction;
import static com.zfsbs.config.Constants.PAY_FY_ALY;
import static com.zfsbs.config.Constants.PAY_FY_WX;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_ALY;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_QB;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_WX;
import static com.zfsbs.config.Constants.REQUEST_CASH;

public class ZfPayActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "ZfPayActivity";

    private TextView tOrderAmount;
    private TextView tPayAmount;
    private TextView tPayPointAmount;
    private TextView tPayCouponAmount;
    private LinearLayout btnPayflot;
    private LinearLayout btnCash;
    private LinearLayout btnAly;
    private LinearLayout btnWx;
    private LinearLayout btnQb;
    private Button btnPrint;
    private Button btnPrintfinish;
    private Button btnNopayAmount;
    private Button btnQuery;
    private Button btnQueryEnd;

    private LinearLayout ll_payType;
    private LinearLayout ll_payFinish;
    private LinearLayout ll_no_pay_amount;
    private LinearLayout ll_payQuery;

    private LinearLayout ll_pointAmount;
    private LinearLayout ll_couponAmount;

    //    private int amount;
    private int orderAmount;

    private FyBat fybat;
    private ZfQbAction qbpay;

    private MemberTransAmountResponse memberTransAmount;

    private int app_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_type);
//        AppManager.getAppManager().addActivity(this);

        app_type = (int) SPUtils.get(this, Config.APP_TYPE, Config.DEFAULT_APP_TYPE);

        initView();
//        initData();
        getData();
        addListenster();

    }

    private void initView() {


        tOrderAmount = (TextView) findViewById(R.id.id_orderAmount);
        tPayAmount = (TextView) findViewById(R.id.id_payAmount);
        tPayPointAmount = (TextView) findViewById(R.id.id_pointAmount);
        tPayCouponAmount = (TextView) findViewById(R.id.id_coupon_amount);

        btnPayflot = (LinearLayout) findViewById(R.id.pay_flot);
        btnCash = (LinearLayout) findViewById(R.id.pay_cash);
        btnAly = (LinearLayout) findViewById(R.id.pay_aly);
        btnWx = (LinearLayout) findViewById(R.id.pay_wx);
        btnQb = (LinearLayout) findViewById(R.id.pay_qb);
        btnPrint = (Button) findViewById(R.id.id_print);
        btnPrintfinish = (Button) findViewById(R.id.id_finish);
        btnNopayAmount = (Button) findViewById(R.id.id_no_pay_amount);
        btnQuery = (Button) findViewById(R.id.id_query);
        btnQueryEnd = (Button) findViewById(R.id.id_terminal_query_sure);


        ll_payType = (LinearLayout) findViewById(R.id.ll_pay_type);
        ll_payFinish = (LinearLayout) findViewById(R.id.ll_pay_finish);
        ll_payQuery = (LinearLayout) findViewById(R.id.ll_pay_query);
        ll_no_pay_amount = (LinearLayout) findViewById(R.id.ll_no_pay_amount);

        ll_pointAmount = (LinearLayout) findViewById(R.id.id_ll_pointAmount);
        ll_couponAmount = (LinearLayout) findViewById(R.id.id_ll_coupon_amount);

        if (!Constants.isUsedQb ) {
            btnQb.setVisibility(View.INVISIBLE);
            btnCash.setVisibility(View.INVISIBLE);
        }




    }


    private void getData() {
        MemberTransAmountResponse getMemberData = CommonFunc.recoveryMemberInfo(this);
        if (getMemberData != null) {
            tOrderAmount.setText(StringUtils.formatIntMoney(getMemberData.getTradeMoney()));
            tPayAmount.setText(StringUtils.formatIntMoney(getMemberData.getRealMoney()));
            tPayPointAmount.setText(StringUtils.formatIntMoney(getMemberData.getPointCoverMoney()));
            tPayCouponAmount.setText(StringUtils.formatIntMoney(getMemberData.getCouponCoverMoney()));
            if (getMemberData.getRealMoney() == 0) {
                ll_no_pay_amount.setVisibility(View.VISIBLE);
                ll_payType.setVisibility(View.GONE);
            }
        }


        fybat = new FyBat(this, listener1);
        qbpay = new ZfQbAction(this);

        if (!StringUtils.isEmpty(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo())) {
            printerData.setPhoneNo(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo());
        }


    }


    private FyBat.FYPayResultEvent listener1 = new FyBat.FYPayResultEvent() {
        @Override
        public void onSuccess(FyMicropayResponse data) {

            setFySmPay1(data);
        }

        @Override
        public void onSuccess(FyQueryResponse data) {
            //先判断本地数据是否存在，防止从华尔街平台拿到的是上一笔成功的交易
            SbsPrinterData datas = DataSupport.findLast(SbsPrinterData.class);
//            LogUtils.e("AuthCode",datas.getAuthCode());
//            LogUtils.e("getMchnt_order_no",data.getMchnt_order_no());
            if (!StringUtils.isEmpty(datas.getAuthCode()) && datas.getAuthCode().equals(data.getMchnt_order_no())) {
                ToastUtils.CustomShow(ZfPayActivity.this, "请确认消费者交易成功。");
                return;
            }
            setFySmPayQurey1(data);
        }

        @Override
        public void onSuccess(FyRefundResponse data) {

        }

        @Override
        public void onFailure(int statusCode, String error_msg, String type, String query_amount) {
            showLayoutEndQuery();
        }

        @Override
        public void onFailure(FyMicropayRequest data) {
            showLayoutEndQuery();
            if (data.getType().equals(Constants.PAY_FY_ALY)) {
                setFyPayFailureQuery(data.getOutOrderNum(), data.getAmount() + "", data.getType(), true, Constants.PAY_WAY_ALY, Constants.FY_FAILURE_PAY);
            } else if (data.getType().equals(Constants.PAY_FY_WX)) {
                setFyPayFailureQuery(data.getOutOrderNum(), data.getAmount() + "", data.getType(), true, Constants.PAY_WAY_WX, Constants.FY_FAILURE_PAY);
            }

        }

        @Override
        public void onFailure(FyQueryRequest data) {
            showLayoutEndQuery();
            if (data == null) {
                ToastUtils.CustomShow(ZfPayActivity.this, "请求数据为空，无法查询末笔");
                return;
            }
            if (data.getOrder_type().equals(Constants.PAY_FY_ALY)) {
                setFyQueryFailureQuery(data.getOutOrderNum(), data.getOrder_type(), data.getMchnt_order_no(), true, Constants.PAY_WAY_ALY, Constants.FY_FAILURE_QUERY);
            } else if (data.getOrder_type().equals(Constants.PAY_FY_WX)) {
                setFyQueryFailureQuery(data.getOutOrderNum(), data.getOrder_type(), data.getMchnt_order_no(), true, Constants.PAY_WAY_WX, Constants.FY_FAILURE_QUERY);
            }

        }


        @Override
        public void onLogin() {
            AppManager.getAppManager().finishAllActivity();
            if (Config.OPERATOR_UI_BEFORE) {
                CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
            } else {
                CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
            }
        }
    };


    private void showLayoutEndQuery() {
        ll_payType.setVisibility(View.GONE);
        ll_payQuery.setVisibility(View.VISIBLE);
    }


    private void addListenster() {
        btnPayflot.setOnClickListener(this);
        btnCash.setOnClickListener(this);
        btnAly.setOnClickListener(this);
        btnWx.setOnClickListener(this);
        btnQb.setOnClickListener(this);
        btnPrint.setOnClickListener(this);
        btnPrintfinish.setOnClickListener(this);
        btnNopayAmount.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
        btnQueryEnd.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startAction(this, InputAmountActivity.class, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_print:
                if (app_type == Config.APP_SBS) {
                    Gson gson = new Gson();
                    TransUploadRequest data = gson.fromJson(printerData.getTransUploadData(), TransUploadRequest.class);
                    LogUtils.e(data.toString());
                    getPrinterData(data);//(printerData.getRequest());
                }
                break;
            case R.id.id_finish:
            case R.id.id_terminal_query_sure: {
                startAction(this, InputAmountActivity.class, true);
            }
            break;
            case R.id.pay_flot:
                payflot1();

                break;
            case R.id.pay_cash: {
                Bundle bundle = new Bundle();
                bundle.putString("amount", tPayAmount.getText().toString());
                startResultAction(this, ZfPayCashActivity.class, bundle, REQUEST_CASH);
            }
            break;
            case R.id.pay_aly:
                payBat(Constants.PAY_WAY_ALY);
                break;
            case R.id.pay_wx:
                payBat(Constants.PAY_WAY_WX);
                break;
            case R.id.pay_qb:
                startResultAction(ZfPayActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_QB);
                break;
            case R.id.id_no_pay_amount:
                setNoPayAmount1();
                break;
            case R.id.id_query:
                setLastQuerySend1();

                break;
            default:
                break;
        }
    }


    private void setNoPayAmount1() {

        setCashPrintData1(0);

        if (app_type == Config.APP_SBS) {

            //设置流水上送参数
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), "", ""
            );

            //打印订单号与流水上送统一
            printerData.setClientOrderNo(request.getClientOrderNo());

            //流水上送
            transUploadAction1(request);

        }

    }


    private void setLastQuerySend1() {

        switch (CommonFunc.recoveryFailureInfo(this).getPay_type()) {
            case Constants.PAY_WAY_QB:
                ZfQbQuery();
                break;
            case Constants.PAY_WAY_ALY:
            case Constants.PAY_WAY_WX:
                if (CommonFunc.recoveryFailureInfo(this).getFaiureType() == Constants.FY_FAILURE_PAY) {
                    ZfFyPayQuery();
                } else if (CommonFunc.recoveryFailureInfo(this).getFaiureType() == Constants.FY_FAILURE_QUERY) {
                    ZfFyQuery();
                }
                break;

        }

    }


    private void payBat(int type) {
        String sm_type = MyApplication.getInstance().getLoginData().getScanPayType();

        //富友扫码
        if (!StringUtils.isEmpty(sm_type) && StringUtils.isEquals(sm_type, Constants.SM_TYPE_FY)) {

            switch (type) {
                case Constants.PAY_WAY_ALY:
                    startResultAction(ZfPayActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_ALY);
                    break;
                case Constants.PAY_WAY_WX:
                    startResultAction(ZfPayActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_WX);
                    break;
            }

            return;
        }


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
//                setFlotPrintData1(transInfo);
//
//                //判断是否是商博士
//                if (app_type == Config.APP_SBS) {
//
//                    //设置流水上送需要上送的参数
//                    TransUploadRequest request = CommonFunc.setTransUploadData(printerData,
//                            CommonFunc.recoveryMemberInfo(ZfPayActivity.this),
//                            CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()),
//                            printerData.getVoucherNo(), printerData.getReferNo());
//
//                    //打印的订单号与流水上送的统一
//                    printerData.setClientOrderNo(request.getClientOrderNo());
//
//                    //流水上送
//                    transUploadAction1(request);
//                } else if (app_type == Config.APP_Richer_e) {
//                    //设置流水上送需要上送的参数
//                    TransUploadRequest request = CommonFunc.setTransUploadData(printerData,
//                            CommonFunc.recoveryMemberInfo(ZfPayActivity.this),
//                            CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()),
//                            printerData.getVoucherNo(), printerData.getReferNo());
//
//                    //打印的订单号与流水上送的统一
//                    printerData.setClientOrderNo(request.getClientOrderNo());
//
//                    Richer_transUploadAction(request);
//                } else if (app_type == Config.APP_YXF) {
//                    if (!StringUtils.isEmpty(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo())) {
//                        printerData.setPhoneNo(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo());
////                        printerData.setYxf(true);
//                        sendYxf(printerData);
//                    } else {
//                        printerData.setApp_type(app_type);
//                        printerData.setClientOrderNo(CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()));
//                        PrinterDataSave();
//                        Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
//                        showLayout();
//                    }
//                } else if (app_type == Config.APP_HD) {
//                    boolean isMember = (boolean) SPUtils.get(ZfPayActivity.this, Config.isHdMember, false);
//                    if (isMember) {
//                        //设置流水上送需要上送的参数
//                        TransUploadRequest request = CommonFunc.setTransUploadData(printerData,
//                                CommonFunc.recoveryMemberInfo(ZfPayActivity.this),
//                                CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()),
//                                printerData.getVoucherNo(), printerData.getReferNo());
//                        printerData.setMember(isMember);
//                        //打印的订单号与流水上送的统一
//                        printerData.setClientOrderNo(request.getClientOrderNo());
//
//                        //流水上送
//                        transUploadAction2(request);
//                    }else {
//                        printerData.setApp_type(app_type);
//                        printerData.setMember(isMember);
//                        printerData.setClientOrderNo(CommonFunc.getNewClientSn(ZfPayActivity.this, printerData.getPayType()));
//                        PrinterDataSave();
//                        Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
//                        showLayout();
//                    }
//                }
//            }
//
//            @Override
//            public void failed(String error) {
////                ToastUtils.CustomShow(ZfPayActivity.this, error);
//                final CommonDialog confirmDialog = new CommonDialog(ZfPayActivity.this, error);
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
     * 现金
     *
     * @param oddChangeAmout
     */
    private void payCash1(int oddChangeAmout) {

        //设置打印信息
        setCashPrintData1(oddChangeAmout);

        if (app_type == Config.APP_SBS) {
            //设置流水上送参数
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), "", ""
            );

            //打印订单号与流水上送统一
            printerData.setClientOrderNo(request.getClientOrderNo());

            //流水上送
            transUploadAction1(request);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CASH:
                int receiveAmount = data.getBundleExtra("bundle").getInt("receiveAmount");
                int oddChangeAmout = data.getBundleExtra("bundle").getInt("oddChangeAmout");

                payCash1(oddChangeAmout);

                break;
            case REQUEST_CAPTURE_WX:
                String result_wx = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
                LogUtils.e("result", result_wx);
                FyWxPay1(result_wx);
                break;
            case REQUEST_CAPTURE_ALY:
                String result_aly = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
                LogUtils.e("result", result_aly);
                FyAlyPay1(result_aly);
                break;
            case REQUEST_CAPTURE_QB:
                String result_qb = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
                LogUtils.e("result", result_qb);
                ZfQbPay1(result_qb);

                break;
            default:
                break;
        }
    }


    private void FyWxPay1(String code) {
        printerData.setPayType(Constants.PAY_WAY_WX);
        printerData.setClientOrderNo(CommonFunc.getNewClientSn(this, printerData.getPayType()));
        fybat.pay1(code, PAY_FY_WX, printerData.getClientOrderNo(), CommonFunc.recoveryMemberInfo(this).getRealMoney());
    }


    private void FyAlyPay1(String code) {
        printerData.setPayType(Constants.PAY_WAY_ALY);
        printerData.setClientOrderNo(CommonFunc.getNewClientSn(this, printerData.getPayType()));
        fybat.pay1(code, PAY_FY_ALY, printerData.getClientOrderNo(), CommonFunc.recoveryMemberInfo(this).getRealMoney());
    }


    private void ZfQbPay1(String result_qb) {

        if (StringUtils.isEmpty(result_qb)) {
            ToastUtils.CustomShow(this, "获取扫码信息为空");
            return;
        }
        int sid = MyApplication.getInstance().getLoginData().getSid();
        final String orderNo = CommonFunc.getNewClientSn(this, Constants.PAY_WAY_QB);
        final String time = StringUtils.getFormatCurTime();
        final String traceNum = StringUtils.getFormatCurTime() + StringUtils.createRandomNumStr(5);
        this.qbpay.qbAction1(sid, orderNo, CommonFunc.recoveryMemberInfo(this).getRealMoney() + "", time, traceNum, result_qb, new ActionCallbackListener<ZfQbResponse>() {
            @Override
            public void onSuccess(ZfQbResponse data) {
                //流水上送
                setQbPay1(data, orderNo, time, traceNum);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {
                ToastUtils.CustomShow(ZfPayActivity.this, s + "#" + error_msg);
                showLayoutEndQuery();
                //设置末笔查询数据
                setQbFailureQuery(orderNo, time, traceNum, Constants.PAY_WAY_QB);
            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });

    }


    /**
     * 钱包末笔查询
     */
    private void ZfQbQuery() {
        CommonFunc.ZfQbFailQuery(this, new ActionCallbackListener<ZfQbResponse>() {
            @Override
            public void onSuccess(ZfQbResponse data) {

                FailureData failureData = CommonFunc.recoveryFailureInfo(ZfPayActivity.this);
                //流水上送
                setQbPay1(data, failureData.getOrderNo(),
                        failureData.getTime(), failureData.getTraceNum());
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {
                ToastUtils.CustomShow(ZfPayActivity.this, s + "#" + error_msg);
            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }


    /**
     * 富友扫码支付异常处理
     */
    private void ZfFyPayQuery() {
        fybat.terminalQuery1(CommonFunc.recoveryFailureInfo(this).getOrder_type(), CommonFunc.recoveryFailureInfo(this).getAmount(), true,
                CommonFunc.recoveryFailureInfo(this).getOutOrderNo());
    }

    /**
     * 富友扫码查询异常处理
     */
    private void ZfFyQuery() {

        fybat.query1(this, CommonFunc.recoveryFailureInfo(this).getOrder_type(), CommonFunc.recoveryFailureInfo(this).getOrderNo(),
                CommonFunc.recoveryFailureInfo(this).getOutOrderNo());
    }


    /**
     * 设置钱包异常查询
     *
     * @param orderNo
     * @param time
     * @param payWayQb
     */
    private void setQbFailureQuery(String orderNo, String time, String traceNum, int payWayQb) {
        FailureData data = new FailureData();
        data.setPay_type(payWayQb);
        data.setOrderNo(orderNo);
        data.setTime(time);
        data.setTraceNum(traceNum);
        data.setStatus(true);
        CommonFunc.setBackFailureInfo(this, data);
    }

    /**
     * 设置富友SM异常查询
     *
     * @param amount
     * @param type
     * @param isStatus
     */
    private void setFyPayFailureQuery(String outOrderNum, String amount, String type, boolean isStatus, int payWay, int failureType) {

        boolean isMember = (boolean) SPUtils.get(this, Config.isHdMember, false);

        FailureData data = new FailureData();
        data.setOutOrderNo(outOrderNum);
        data.setAmount(amount);
        data.setOrder_type(type);
        data.setStatus(isStatus);
        data.setPay_type(payWay);
        data.setFaiureType(failureType);
        data.setApp_type(app_type);
        data.setMember(isMember);
        CommonFunc.setBackFailureInfo(this, data);

    }

    /**
     * 设置富友查询异常查询数据
     *
     * @param type
     * @param order_no
     * @param isStatus
     * @param payWay
     */
    private void setFyQueryFailureQuery(String outOrderNum, String type, String order_no, boolean isStatus, int payWay, int failureType) {
        boolean isMember = (boolean) SPUtils.get(this, Config.isHdMember, false);
        FailureData data = new FailureData();
        data.setOutOrderNo(outOrderNum);
        data.setOrder_type(type);
        data.setStatus(isStatus);
        data.setPay_type(payWay);
        data.setOrderNo(order_no);
        data.setFaiureType(failureType);
        data.setApp_type(app_type);
        data.setMember(isMember);
        CommonFunc.setBackFailureInfo(this, data);
    }

    /**
     * 设置收钱吧查询异常查询数据
     *
     * @param type
     * @param order_no
     * @param isStatus
     * @param payWay
     */
    private void setSqbFailureQuery(String outOrderNum, String type, String order_no, boolean isStatus, int payWay, int failureType) {
        FailureData data = new FailureData();
        data.setOutOrderNo(outOrderNum);
        data.setOrder_type(type);
        data.setStatus(isStatus);
        data.setPay_type(payWay);
        data.setOrderNo(order_no);
        data.setFaiureType(failureType);
        data.setApp_type(app_type);
        CommonFunc.setBackFailureInfo(this, data);
    }


    private void setQbPay1(ZfQbResponse data, String orderNo, String time, String traceNum) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
        printerData.setMerchantNo(data.getGroupId());
        printerData.setTerminalId(StringUtils.getSerial());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setDateTime(time);
        printerData.setClientOrderNo(orderNo);
        printerData.setTransNo(traceNum);
        printerData.setAuthCode(data.getSystemOrderNo());
        printerData.setDateTime(StringUtils.formatTime(time));
        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
        printerData.setAmount(StringUtils.formatIntMoney(CommonFunc.recoveryMemberInfo(this).getRealMoney()));
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setPayType(Constants.PAY_WAY_QB);

        if (app_type == Config.APP_SBS) {
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), printerData.getTransNo(), printerData.getAuthCode()
            );
            //这个地方保持和支付的时候一直
            request.setClientOrderNo(orderNo);
            transUploadAction1(request);
        }


    }


    private void setCashPrintData1(int oddChangeAmout) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getMerchantNo());
        printerData.setTerminalId(StringUtils.getSerial());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setDateTime(StringUtils.getCurTime());
        printerData.setAmount(StringUtils.formatIntMoney(CommonFunc.recoveryMemberInfo(this).getTradeMoney()));
        printerData.setReceiveAmount(StringUtils.formatIntMoney(CommonFunc.recoveryMemberInfo(this).getRealMoney()));
        printerData.setOddChangeAmout(StringUtils.formatIntMoney(oddChangeAmout));
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setPayType(Constants.PAY_WAY_CASH);

    }


//    protected void setFlotPrintData1(ComTransInfo transInfo) {
//        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
//        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getMerchantNo());//(transInfo.getMid());
//        printerData.setTerminalId(transInfo.getTid());
//        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
//        printerData.setAcquirer(transInfo.getAcquirerCode());
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
//        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
//        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
//        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
//        printerData.setAmount(StringUtils.formatIntMoney(transInfo.getTransAmount()));
//        printerData.setPayType(Constants.PAY_WAY_FLOT);
//    }





    private void setFySmPay1(FyMicropayResponse data) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getFyMerchantName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
        printerData.setTerminalId(StringUtils.getTerminalNo(StringUtils.getSerial()));
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setTransNo(data.getTransaction_id());
        printerData.setAuthCode(data.getMchnt_order_no());
        printerData.setDateTime(StringUtils.formatTime(data.getTxn_begin_ts()));
        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
        printerData.setAmount(StringUtils.formatStrMoney(data.getTotal_amount()));
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setScanPayType(MyApplication.getInstance().getLoginData().getScanPayType());
        if (data.getOrder_type().equals(Constants.PAY_FY_ALY)) {
            printerData.setPayType(Constants.PAY_WAY_ALY);
        } else if (data.getOrder_type().equals(Constants.PAY_FY_WX)) {
            printerData.setPayType(Constants.PAY_WAY_WX);
        }

        if (app_type == Config.APP_SBS) {
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), printerData.getTransNo(), printerData.getAuthCode()
            );
            printerData.setClientOrderNo(request.getClientOrderNo());
            transUploadAction1(request);
        }

    }


    private void setFySmPayQurey1(FyQueryResponse data) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getFyMerchantName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
        printerData.setTerminalId(StringUtils.getTerminalNo(StringUtils.getSerial()));
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setTransNo(data.getTransaction_id());
        printerData.setAuthCode(data.getMchnt_order_no());
        printerData.setDateTime(StringUtils.getCurTime());
        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
        printerData.setAmount(StringUtils.formatStrMoney(data.getOrder_amt()));
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setScanPayType(MyApplication.getInstance().getLoginData().getScanPayType());
        if (data.getOrder_type().equals(Constants.PAY_FY_ALY)) {
            printerData.setPayType(Constants.PAY_WAY_ALY);
        } else if (data.getOrder_type().equals(Constants.PAY_FY_WX)) {
            printerData.setPayType(Constants.PAY_WAY_WX);
        }

        if (app_type == Config.APP_SBS) {
            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
                    CommonFunc.getNewClientSn(this, printerData.getPayType()), printerData.getTransNo(), printerData.getAuthCode()
            );
            printerData.setClientOrderNo(request.getClientOrderNo());
            transUploadAction1(request);
        }

    }


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
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
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
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);
                showLayout();

                setTransUpLoadData(request);
                // 设置当前交易流水需要上送
                printerData.setUploadFlag(true);
                printerData.setApp_type(app_type);
                // 保存打印的数据，不保存图片数据
                PrinterDataSave();
                // 打印
//                Printer.print(printerData, ZfPayActivity.this);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
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
//            Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);

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
