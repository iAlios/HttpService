package com.alios.httpservices.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alios.httpservices.utils.MLog;
import com.alios.httpservices.utils.PackageUtils;

public class TimeChangedBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_TIME_TICK.equals(action)) {
			MLog.d("++++++++++++++++++++++   TimeChangedBroadcast   +++++++++++++++++++");
			if (!PackageUtils.hasServiceStarted(context,
					HttpServices.class.getName())) {
				MLog.d("++++++   TimeChangedBroadcast   +++++  start Http Service...  ++");
				Intent intentService = new Intent();
				intentService.setClass(context, HttpServices.class);
				context.startService(intentService);
			}
		}
	}

}
