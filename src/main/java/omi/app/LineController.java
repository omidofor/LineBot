package omi.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import omi.app.util.HttpRequest;
import omi.app.util.HttpResponse;

@RestController
@RequestMapping("line")
public class LineController {
	
	@Value("${access.token}")
	private String token;
	@Value("${forward.url}")
	private String forwardUrl;
	
	
	@RequestMapping(method=RequestMethod.POST, path = "/message_bak")
	public String message_bak(HttpServletRequest request, Gson gson) throws IOException {
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("method", request.getMethod());
		data.put("path", request.getPathInfo());
		Map<String, String> param = new LinkedHashMap<>();
		for(Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
			param.put(entry.getKey(), String.join(", ", entry.getValue()));
		}
		data.put("param", param);
		Map<String, String> header = new LinkedHashMap<>();
		Enumeration<String> e = request.getHeaderNames(); 
		while(e.hasMoreElements()) {
			String key = e.nextElement();
			header.put(key, request.getHeader(key));
		}

		JsonArray events = null;
		try(BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"))){
			StringBuilder sb = new StringBuilder();
			String str;
			while((str = br.readLine()) != null) {
				if(sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(str);
			}
			String json = sb.toString();
			events = gson.fromJson(json, JsonObject.class).getAsJsonArray("events");
			data.put("raw", json);
		}
		
		for(int index=0;index<events.size();index++) {
			JsonObject event = events.get(index).getAsJsonObject();
			if("message".equals(event.get("type").getAsString())) {
				JsonObject message = event.get("message").getAsJsonObject();
				if("text".equals(message.get("type").getAsString())) {
					String replyToken = event.get("replyToken").getAsString();
					String text = message.get("text").getAsString();
					this.replyMessage(replyToken, String.format("「%s」", text));
				}
			}
		}
		

		data.put("header", header);
		String result = gson.toJson(data);
		System.out.println(result);
		return "ok"; 
	}
	
	@RequestMapping(method=RequestMethod.POST, path = "/message_bak")
	public void message(@RequestBody String body, @RequestHeader HttpHeaders headers) throws IOException {
		HttpRequest request = new HttpRequest();
		request.Post();
		request.setParam(body);
		for(Entry<String, List<String>> entry : headers.entrySet()) {
			request.addHeader(entry.getKey(), entry.getValue().get(0));
		}
		HttpResponse response = request.perform();
		System.out.printf("Return code: %s\nBody: %s\n", response.getResponseCode(), response.getBody());
	}


	private void replyMessage(String replyToken, String message) throws IOException {
		URL url = new URL("https://api.line.me/v2/bot/message/reply");
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", String.format("Bearer %s", this.token));
		
		JsonArray messageAry = new JsonArray();
		JsonObject messageElement = new JsonObject();
		messageElement.addProperty("type", "text");
		messageElement.addProperty("text", message);
		messageAry.add(messageElement);
		
		JsonObject content = new JsonObject();
		content.addProperty("replyToken", replyToken);
		content.add("messages", messageAry);
		String contentString = content.toString();
		
		con.setDoOutput(true);
		try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"))){
			bw.write(contentString);
		}
		
		int responseCode = con.getResponseCode();
		System.out.printf("Code: %s, Content: %s\n", responseCode, contentString);
	}

}

