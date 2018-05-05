package omi.app.util;

import java.util.List;
import java.util.Map;

public class HttpResponse {
	
	private int responseCode;
	private Map<String, List<String>> headers;
	private String body;


	public int getResponseCode() {
		return responseCode;
	}


	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}


	public Map<String, List<String>> getHeaders() {
		return headers;
	}


	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}


	public String getBody() {
		return body;
	}


	public void setBody(String body) {
		this.body = body;
	}
	
	

}