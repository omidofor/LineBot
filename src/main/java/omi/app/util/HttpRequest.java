package omi.app.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class HttpRequest {
	
	private final static String GET = "GET";
	private final static String POST = "POST";
	
	private String method;
	private String url;
	private Map<String, String> headers = new LinkedHashMap<>();
	private String param;
	
	public static void trustAllServer() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}
				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			}
		};
	
		// Install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	
		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
	
		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}
	
	public void Get() {
		this.method = GET;
	}
	
	public void Post() {
		this.method = POST;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public void addHeader(String key, String value) {
		this.headers.put(key, value);
	}
	
	public void setParam(Map<String, String> params) {
		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> entry : params.entrySet()) {
			if(sb.length() > 0) {
				sb.append("&");
			}
			sb.append(String.format("%s=%s", entry.getKey(), entry.getValue()));
		}
		this.param = sb.toString();
	}
	
	public void setParam(String param) {
		this.param = param;
	}
	
	public HttpResponse perform() throws IOException {
		String targetUrl = this.url;
		if(this.method.equals(HttpRequest.GET)) {
			targetUrl = String.format("%s?%s", targetUrl, this.param);
		}
		URL url = new URL(targetUrl);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setRequestMethod(this.method);
		for(Entry<String, String> entry : this.headers.entrySet()) {
			con.setRequestProperty(entry.getKey(), entry.getValue());
		}
		
		
		if(this.param != null && !this.param.trim().isEmpty() && !this.method.equals(HttpRequest.GET)) {
			con.setDoOutput(true);
			try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"))){
				bw.write(this.param);
			}
		}
		
		HttpResponse response = new HttpResponse();
		response.setResponseCode(con.getResponseCode());
		try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"))){
			StringBuilder sb = new StringBuilder();
			String str;
			while((str = br.readLine()) != null) {
				if(sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(str);
			}
			response.setBody(sb.toString());
		}
		response.setHeaders(con.getHeaderFields());
		return response;
	}
}
