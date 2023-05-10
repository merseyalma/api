package com.zcunsoft.tracking.report.model;

import lombok.Data;

@Data
public class QueryCityNewUsersRequest {
    private int pageNum;
    private int pageSize;
    private String channel;
    private int past = 3;
    private String projectName;
    private String appVersion;
    private String downloadChannel;
}
