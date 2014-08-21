package com.alios.httpservices.service.net;

import java.util.Map;

import com.alios.httpservices.service.net.NanoHTTPD.Response;

public class UploadFileResponse extends Response {
	private final String uri;
	private final Map<String, String> headers;

	public UploadFileResponse(Map<String, String> headers, String uri) {
		super(null);
		this.headers = headers;
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
}