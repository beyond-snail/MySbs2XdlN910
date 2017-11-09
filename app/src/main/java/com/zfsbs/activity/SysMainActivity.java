package com.zfsbs.activity;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;


public class SysMainActivity extends BaseActivity implements OnClickListener {

	private Button btnCashier;
	private Button btnAppStore;
	private Button btnMyApp;
	private Button btnTAccount;
	private Button btnDataReport;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sys_main);
//		AppManager.getAppManager().addActivity(this);
		initView();
		addLinstener();
	}

	private void initView() {
		btnCashier = (Button) findViewById(R.id.id_cashier);
		btnAppStore = (Button) findViewById(R.id.id_appstore);
		btnMyApp = (Button) findViewById(R.id.id_myapp);
		btnTAccount = (Button) findViewById(R.id.id_tAccount);
		btnDataReport = (Button) findViewById(R.id.id_data_report);
	}

	private void addLinstener() {
		btnCashier.setOnClickListener(this);
		btnAppStore.setOnClickListener(this);
		btnMyApp.setOnClickListener(this);
		btnTAccount.setOnClickListener(this);
		btnDataReport.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_cashier:
			CommonFunc.startAction(this, SaleMainActivity.class, false);
			
			
			break;
		case R.id.id_appstore:

			break;
		case R.id.id_myapp:

			break;
		case R.id.id_tAccount:

			break;
		case R.id.id_data_report:

			break;

		default:
			break;
		}
	}

	

	
}
