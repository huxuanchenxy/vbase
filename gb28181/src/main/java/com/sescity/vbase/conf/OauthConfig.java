package com.sescity.vbase.conf;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "config", ignoreInvalidFields = true)
public class OauthConfig {

	  private String     oauthServer;
    private String     adminUsername;
    private String     adminPassword;
    private String     clientId; 

	public String getOauthServer() {
		return oauthServer;
	}

	public void setOauthServer(String oauthServer) {
		this.oauthServer = oauthServer;
	}

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
    
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
