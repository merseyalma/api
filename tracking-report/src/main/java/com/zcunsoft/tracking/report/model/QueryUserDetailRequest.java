package com.zcunsoft.tracking.report.model;

import lombok.Data;

@Data
public class QueryUserDetailRequest {
    private String type;
    private int pageNum;
    private int pageSize;
    private String downloadChannel;
    private String statDate;
    private String projectName;
    private String channel;
    private String appVersion;
    private String distinctId;
}
