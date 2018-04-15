package com.vladdrake.googlegettoken;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@SpringBootApplication
public class Application implements ApplicationRunner {

	private final GoogleConfiguration googleConfiguration;

	private final HttpTransport httpTransport;

	private final JsonFactory jsonFactory;

	private final LocalServerReceiver localServerReceiver;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, googleConfiguration.getClientId(), googleConfiguration.getClientSecret(), googleConfiguration.getScopes()).build();
			Credential credentials = new AuthorizationCodeInstalledApp(flow, localServerReceiver).authorize(googleConfiguration.getUserId());
			System.out.println("================= TOKEN INFO =================");
			System.out.println(String.format("access token: %s", credentials.getAccessToken()));
			System.out.println(String.format("refresh token: %s", credentials.getRefreshToken()));
			System.out.println(String.format("expires in: %d", credentials.getExpiresInSeconds()));
			System.out.println(String.format("expires on: %s", new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date(credentials.getExpirationTimeMilliseconds()))));
			System.out.println(String.format("token url: %s", credentials.getTokenServerEncodedUrl()));
			System.out.println("================= TOKEN INFO =================");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Getter
	@Setter
	@Configuration
	@ConfigurationProperties("google")
	public static class GoogleConfiguration {
		private Set<String> scopes;

		private String clientId;

		private String clientSecret;

		private String userId;

		private int localServerPort = 8080;

		@Bean
		public HttpTransport httTransport() {
			return new NetHttpTransport();
		}

		@Bean
		public JsonFactory jsonFactory() {
			return new JacksonFactory();
		}

		@Bean
		public LocalServerReceiver localServerReceiver() {
			return new LocalServerReceiver.Builder().setPort(getLocalServerPort()).build();
		}
	}
}
