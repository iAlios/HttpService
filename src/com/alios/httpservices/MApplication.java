package com.alios.httpservices;

import android.app.Application;

import com.alios.httpservices.service.ResourceManager;
import com.alios.httpservices.utils.MLog;

public class MApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		MLog.init(this);
		ResourceManager.getInstance().registerReceiver(this);
	}

}
