package com.alios.httpservices.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtils {

	public static final int ip2Int(String ip) {
		String[] ints = ip.split("[.]");
		int result = Integer.valueOf(ints[0]);
		result *= 256;
		result += Integer.valueOf(ints[1]);
		result *= 256;
		result += Integer.valueOf(ints[2]);
		result *= 256;
		result += Integer.valueOf(ints[3]);
		return result;
	}

	public static final String int2Ip(int code) {
		int i4 = code % 256;
		code /= 256;
		int i3 = code % 256;
		code /= 256;
		int i2 = code % 256;
		code /= 256;
		int i1 = code % 256;
		return i1 + "." + i2 + "." + i3 + "." + i4;
	}

	/**
	 * 判断网络是否可用
	 */
	public static boolean hasNetWork(Context context) {
		ConnectivityManager con = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo workinfo = con.getActiveNetworkInfo();
		if (workinfo == null || !workinfo.isAvailable()) {
			return false;
		}
		return true;
	}

	/**
	 * 判断WIFI网络是否可用
	 */
	public static boolean hasWIFIWork(Context context) {
		ConnectivityManager con = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo workinfo = con.getActiveNetworkInfo();
		if (workinfo == null || !workinfo.isAvailable()) {
			return false;
		}
		if (workinfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	public static final byte[] ip2ByteArray(String ip) {
		String[] ints = ip.split("[.]");
		byte[] addr = new byte[ints.length];
		for (int index = 0; index < ints.length; index++) {
			addr[index] = (byte) (Integer.valueOf(ints[index]) & 0xFF);
		}
		return addr;
	}

	public static final String ip2ByteArray(byte[] codes, int offset, int len) {
		StringBuffer sBuffer = new StringBuffer();
		for (int index = 0; index < len; index++) {
			sBuffer.append(codes[offset + index] & 0xFF);
			if (index < len - 1) {
				sBuffer.append(".");
			}
		}
		return sBuffer.toString();
	}

	public static final String getLocalIpAddress() {
		try {
			// 遍历网络接口
			Enumeration<NetworkInterface> infos = NetworkInterface
					.getNetworkInterfaces();
			while (infos.hasMoreElements()) {
				// 获取网络接口
				NetworkInterface niFace = infos.nextElement();
				Enumeration<InetAddress> enumIpAddr = niFace.getInetAddresses();
				while (enumIpAddr.hasMoreElements()) {
					InetAddress mInetAddress = enumIpAddr.nextElement();
					if (!mInetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(mInetAddress
									.getHostAddress())) {
						return mInetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			MLog.d("can not get the right ip address: "
					+ e.getLocalizedMessage(), e);
		}
		return null;
	}
}
