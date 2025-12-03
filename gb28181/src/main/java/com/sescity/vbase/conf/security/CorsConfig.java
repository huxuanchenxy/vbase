package com.sescity.vbase.conf.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "spring.cors", ignoreInvalidFields = true)
public class CorsConfig {

  private List<String> allowedOrigins;

  private Boolean allowCredential;

  public List<String> getAllowedOrigins() {
    return allowedOrigins;
  }

  public void setAllowedOrigins(List<String> allowedOrigins) {
    this.allowedOrigins = allowedOrigins;
  }

  public Boolean isAllowCredential() {
    return allowCredential;
  }

  public void setAllowCredential(Boolean allowCredential) {
    this.allowCredential = allowCredential;
  }
}
