package com.sescity.vbase.utils.oauth;

public class LoginEntity {
  public static class Column {
    public static final String userName = "userName";
    public static final String password = "password";
    public static final String newPassword = "newPassword";
  }

  public LoginEntity(String userName, String password, String code) {
    this.userName = userName;
    this.password = password;
    this.code = code;
  }

  private String userName;

  private String password;

  private String newPassword;

  private String code;

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }
}
