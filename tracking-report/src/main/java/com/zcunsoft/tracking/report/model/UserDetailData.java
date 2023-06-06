package com.zcunsoft.tracking.report.model;


import com.zcunsoft.tracking.report.entity.clickhouse.UserSpecificList;
import lombok.Data;

import java.util.List;

@Data
public class UserDetailData {
    private String url;
    private String event;
    private String log_time;
    private String client_ip;
    private Long event_duration;
    private String title;
}
