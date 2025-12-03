package com.sescity.vbase.gb28181.bean;


import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 系统监控的服务器
 */
@Schema(description = "系统监控的服务器")
public class MonitorServer {

	/**
	 * 设备国标编号
	 */
	@Schema(description = "ip")
	private String ip;

	/**
	 * 设备名
	 */
	@Schema(description = "类型")
	private String type;
	
	/**
	 * 运维链接
	 */
	@Schema(description = "运维链接")
	private String opsLink;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOpsLink() {
		return opsLink;
	}

	public void setOpsLink(String opsLink) {
		this.opsLink = opsLink;
	}
}
