/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package omi.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import omi.app.util.HttpRequest;

@SpringBootApplication
@RestController
@ComponentScan("omi.app")
@Configuration
@EnableAutoConfiguration
public class HelloworldApplication {
	
	private Gson gson;
	
	@RequestMapping("/")
	public String home() {
		return "Hello World!";
	}

  /**
   * (Optional) App Engine health check endpoint mapping.
   * @see <a href="https://cloud.google.com/appengine/docs/flexible/java/how-instances-are-managed#health_checking"></a>
   * If your app does not handle health checks, a HTTP 404 response is interpreted
   *     as a successful reply.
   */
	@RequestMapping("/_ah/health")
	public String healthy() {
		// Message body required though ignored
		return "Still surviving.";
	}
  
	@Bean
	public Gson getVsmGson(){
	if(this.gson == null) {
		GsonBuilder builder = new GsonBuilder();
		this.gson = builder.setPrettyPrinting().create();
	}
	
	return this.gson;
	}

	public static void main(String[] args) throws Exception {
		HttpRequest.trustAllServer();
		SpringApplication.run(HelloworldApplication.class, args);
	}
  
	
}
