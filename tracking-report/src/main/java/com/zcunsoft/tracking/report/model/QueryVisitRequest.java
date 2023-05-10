package com.zcunsoft.tracking.report.model;

import lombok.Data;

@Data
public class QueryVisitRequest {
    private int pageNum;
    private int pageSize;
    private String channel;
    private String startTime;
    private String endTime;
    private String projectName;
}
