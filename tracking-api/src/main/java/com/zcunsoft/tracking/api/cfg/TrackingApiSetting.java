package com.zcunsoft.tracking.api.cfg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * The Class LogCollectorSetting.
 *
 * @author yuhao
 */
@ConfigurationProperties("trackingapi")
public class TrackingApiSetting {

	private int threadCount = 4;

	@Value("#{'${trackingapi.access-control-allow-origin}'.split(',')}")
	private List<String> accessControlAllowOrigin;

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public List<String> getAccessControlAllowOrigin() {
		return accessControlAllowOrigin;
	}

	public void setAccessControlAllowOrigin(List<String> accessControlAllowOrigin) {
		this.accessControlAllowOrigin = accessControlAllowOrigin;
	}
}
