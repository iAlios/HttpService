package com.alios.httpservices.service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import com.alios.httpservices.utils.PackageUtils;

public final class ResourceManager {

	private static final ResourceManager INSTANCE = new ResourceManager();

	private TimeChangedBroadcast mTimeChangedReceiver = new TimeChangedBroadcast();

	private SharedPreferences mSharedPreferences = null;

	private static final String SHARED_PREF_NAME = ".default_name";
	private static final String KEY_DEFAULT_PORT = "DEFAULT_PORT";

	static final int DEFAULT_PORT = 9090;

	private ResourceManager() {
		super();
	}

	public static final ResourceManager getInstance() {
		return INSTANCE;
	}

	public void enterApplication(Context context) {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_TICK);
		context.getApplicationContext().registerReceiver(mTimeChangedReceiver,
				filter);
		mSharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,
				Context.MODE_PRIVATE);
		if (!PackageUtils.hasServiceStarted(context,
				HttpServices.class.getName())) {
			Intent intent = new Intent();
			intent.setClass(context, HttpServices.class);
			context.startService(intent);
		}
	}

	public int getDefaultPort() {
		return mSharedPreferences.getInt(KEY_DEFAULT_PORT, DEFAULT_PORT);
	}

	public void exitApplication(Context context) {
		unregisterReceiver(context);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	private void unregisterReceiver(Context context) {
		context.getApplicationContext()
				.unregisterReceiver(mTimeChangedReceiver);
	}

}
