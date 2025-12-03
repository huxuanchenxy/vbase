package com.sescity.vbase.conf;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "config", ignoreInvalidFields = true)
public class OauthConfig {

	private String     oauthServer;

	public String getOauthServer() {
		return oauthServer;
	}

	public void setOauthServer(String oauthServer) {
		this.oauthServer = oauthServer;
	}
}
