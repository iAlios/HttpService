package com.alios.httpservices.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class PackageUtils {

	private static final String SU_BIN = "/system/bin/su";
	private static final String SU_XBIN = "/system/xbin/su";

	private static final String SU_C = "su -c ";
	private static final String PMINSTALL_R = "'pm install -r ";
	private static final String PM_UNINSTALL = "'pm uninstall ";

	public static final String UNKNOW = "Unknown";

	public static final boolean installApp(String path) {
		return silentInstall(path);
	}

	public static final boolean uninstallApp(String packageName) {
		return silentUninstall(packageName);
	}

	public static final boolean checkSuExit() {
		return (exe("ls " + SU_BIN, "su") || exe("ls " + SU_XBIN, "su")) ? true
				: false;
	}

	/**
	 * 静默安装
	 */
	public static final boolean silentUninstall(String packageName) {
		// su -c 'pm uninstall packageName'
		return exe(SU_C + PM_UNINSTALL + packageName + "'", "Success");
	}

	/**
	 * 静默安装
	 */
	public static final boolean silentInstall(String apkPathName) {
		// su -c 'pm install -r apkPathName'
		return exe(SU_C + PMINSTALL_R + apkPathName + "'", "Success");
	}

	/**
	 * 高級菜单中，勾选root后，执行此函数
	 * 
	 * @return
	 */
	public static final boolean getRooted() {
		if (!checkSuExit()) {
			return false;
		}
		return exe(SU_C + "ls /data/", "system");
	}

	public static final boolean exe(String sh, String compare) {
		ProcessBuilder pb = new ProcessBuilder();

		// 设置shell的当前目录
		pb.directory(new File("/"));

		Process process = null;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			process = pb.start();

			// 获取输入流，可以通过它获取SHELL的输出。
			in = new BufferedReader(new InputStreamReader(
					process.getInputStream()));

			// 获取输出流，可以通过它向SHELL发送命令。
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					process.getOutputStream())), true);
			MLog.d("process exe cmd: " + sh);

			// 弹出superuser等授权管理
			out.println(sh);
			out.println("exit");

			String line = "";
			while ((line = in.readLine()) != null) {
				MLog.d("cmd result: " + line);
				if (line.endsWith(compare)) {
					return true;
				}
			}

		} catch (Exception e) {
			MLog.e("exception: " + e.getMessage(), e);
			return false;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				process.destroy();
			} catch (Exception e) {
				MLog.e("exception: " + e.getMessage(), e);
				return false;
			}
		}
		return false;
	}

	public static final void killProcess(int id) {
		android.os.Process.killProcess(id);
	}

	/**
	 * 判断是否已经安装了某包
	 */
	public static final boolean hasInstalled(Context context, String packageName) {
		PackageManager cPackageManager = context.getPackageManager();
		try {
			cPackageManager.getPackageInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
			return false;
		}
	}

	/**
	 * 判断某服务是否已经启动
	 */
	public static final boolean hasServiceStarted(Context context,
			String serviceName) {
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> currentService = mActivityManager
				.getRunningServices(100);
		for (int i = 0; i < currentService.size(); i++) {
			if (serviceName
					.equals(currentService.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取当前应用的版本号
	 * 
	 * @param context
	 * @return
	 */
	public static final String getCurrentVersionCode(Context context) {
		try {
			PackageInfo localPackageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);

			return Integer.toString(localPackageInfo.versionCode);
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
		}
		return UNKNOW;
	}

	/**
	 * 获取当前应用的版本名称
	 * 
	 * @param context
	 * @return
	 */
	public static final String getCurrentVersionName(Context context) {
		try {
			PackageInfo localPackageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);

			return localPackageInfo.versionName;
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
		}
		return UNKNOW;
	}

	/**
	 * 获取当前应用的名称
	 * 
	 * @param context
	 * @return
	 */
	public static final String getAppName(Context context) {
		PackageManager localPackageManager = context.getPackageManager();
		ApplicationInfo localApplicationInfo;
		try {
			localApplicationInfo = localPackageManager.getApplicationInfo(
					context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException localNameNotFoundException) {
			localApplicationInfo = null;
		}
		return localApplicationInfo != null ? String
				.valueOf(localPackageManager
						.getApplicationLabel(localApplicationInfo)) : UNKNOW;
	}

}
