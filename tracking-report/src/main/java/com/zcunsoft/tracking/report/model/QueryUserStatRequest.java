package com.zcunsoft.tracking.report.model;

import lombok.Data;

@Data
public class QueryUserStatRequest {
    private int pageNum;
    private int pageSize;
    private String channel;
    private String startTime;
    private String endTime;
    private String projectName;
    private String downloadChannel;
    private String appVersion;

}
