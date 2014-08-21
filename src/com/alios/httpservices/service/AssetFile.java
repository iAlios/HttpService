package com.alios.httpservices.service;

public class AssetFile {

	public boolean isDir = false;

	public String mPath = null;

	public AssetFile(boolean isDir, String cPath) {
		super();
		this.isDir = isDir;
		this.mPath = cPath;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof AssetFile) {
			AssetFile temp = (AssetFile) o;
			if (temp.isDir == this.isDir) {
				if (this.mPath == null) {
					return temp.mPath == null;
				} else {
					return this.mPath.equals(temp.mPath);
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "isDir: " + isDir +" path: " + mPath;
	}

}
