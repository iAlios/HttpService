package com.alios.httpservices.service;

import java.io.File;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import com.alios.httpservices.service.net.HttpServer;
import com.alios.httpservices.utils.MLog;

public class HttpServices extends Service {

	private File mFileRoot = null;

	private int mPort = ResourceManager.DEFAULT_PORT;

	private HttpServer mHttpServer;

	private static final int RETRY_TIMES = 3;

	private static final String ASSET_WWW_ROOT = "wwwroot";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mFileRoot = Environment.getExternalStorageDirectory();
		mPort = ResourceManager.getInstance().getDefaultPort();
		startHttpServer(RETRY_TIMES);
	}

	private void startHttpServer(int retry) {
		if (mHttpServer == null) {
			mHttpServer = new HttpServer(getAssets(), mPort, ASSET_WWW_ROOT,
					mFileRoot);
		}
		try {
			mHttpServer.start();
		} catch (IOException e) {
			MLog.d("!!! can not start the http server: "
					+ e.getLocalizedMessage(), e);
			if (retry > 1) {
				startHttpServer(retry - 1);
			}
		}
	}

	private void stopHttpServer() {
		if (mHttpServer != null) {
			mHttpServer.stop();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopHttpServer();
	}

}
