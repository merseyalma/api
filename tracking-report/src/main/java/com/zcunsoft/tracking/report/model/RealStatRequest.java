package com.zcunsoft.tracking.report.model;

import lombok.Data;

@Data
public class RealStatRequest {

    private String channel;

    private String projectName;

    private String appVersion;

    private String downloadChannel;
}
