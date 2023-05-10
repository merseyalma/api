package com.zcunsoft.tracking.report.model;

import lombok.Data;

@Data
public class LifeCycleStatRequest {

    private int past = 3;

    private String channel;

    private String projectName;

    private String appVersion;

    private String downloadChannel;
}
