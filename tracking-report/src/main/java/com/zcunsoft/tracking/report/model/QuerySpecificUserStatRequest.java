package com.zcunsoft.tracking.report.model;

import lombok.Data;

@Data
public class QuerySpecificUserStatRequest {
    private int pageNum;
    private int pageSize;
    private String downloadChannel;
    private String startTime;
    private String endTime;
    private String projectName;
    private String channel;
    private String appVersion;
}
