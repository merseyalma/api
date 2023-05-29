package com.zcunsoft.tracking.report.cfg;

import org.springframework.boot.context.properties.ConfigurationProperties;

// TODO: Auto-generated Javadoc

/**
 * The Class DataRecvSetting.
 */
@ConfigurationProperties("trackingreport")
public class ServiceSetting {

    private int tenantDept;

    public int getTenantDept() {
        return tenantDept;
    }

    public void setTenantDept(int tenantDept) {
        this.tenantDept = tenantDept;
    }
}
