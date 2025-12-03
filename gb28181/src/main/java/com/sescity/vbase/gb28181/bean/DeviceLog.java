package com.sescity.vbase.gb28181.bean;

import io.swagger.v3.oas.annotations.media.Schema;

public class DeviceLog {

  public DeviceLog(String deviceId, String logText) {
    this.deviceId = deviceId;
    this.logText = logText;
  }

  private Integer id;
  private String deviceId;
  private String logText;
  private String createTime;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public String getLogText() {
    return logText;
  }

  public void setLogText(String logText) {
    this.logText = logText;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }
}
