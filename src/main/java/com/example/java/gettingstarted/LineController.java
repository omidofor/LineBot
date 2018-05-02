package com.example.java.gettingstarted;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

@RestController
@RequestMapping("line")
public class LineController {
	
	
	@RequestMapping(method=RequestMethod.POST, path = "/message")
	public String message(HttpServletRequest request, Gson gson) {
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
		data.put("header", header);
		String result = gson.toJson(data);
		System.out.println(result);
		return "ok"; 
	}

}
