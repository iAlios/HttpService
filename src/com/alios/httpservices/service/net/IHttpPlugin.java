package com.alios.httpservices.service.net;

import java.io.File;
import java.util.Map;

import com.alios.httpservices.service.net.NanoHTTPD.Response;

public interface IHttpPlugin {
	void initialize(Map<String, String> commandLineOptions);

	boolean canServeUri(String uri, File rootDir);

	Response serveFile(String uri, Map<String, String> headers, File file,
			String mimeType);
}
