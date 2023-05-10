package com.zcunsoft.tracking.report.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class EntireStatResponseData {

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp start7Time;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp start30Time;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Timestamp endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp user_report_bydate_updatetime;

    private Long total_active_users_7;
    private Long total_active_users_30;
    private Long total_new_users_7;
    private Long total_new_users_30;
    private List<String> times;

    private String latestDay;

    private Long active_users_30_latest;
    private Long active_users_30_sum;
    private Float active_users_30_avg;
    private List<Long> active_users_30;

    private Float duration_per_user_30_latest;
    private Float duration_per_user_30_avg;
    private List<Float> duration_per_user_30;

    private Long new_pv_30_latest;
    private Long new_pv_30_sum;
    private Float new_pv_30_avg;
    private List<Long> new_pv_30;

    private Float duration_per_time_30_latest;
    private Float duration_per_time_30_avg;
    private List<Float> duration_per_time_30;

    private Long new_users_30_latest;
    private Long new_users_30_sum;
    private Float new_users_30_avg;
    private List<Long> new_users_30;

    private Long old_users_30_latest;
    private Long old_users_30_sum;
    private Float old_users_30_avg;
    private List<Long> old_users_30;

    private Float new_users_percent_30_latest;
    private Float new_users_percent_30_avg;
    private List<Float> new_users_percent_30;

    private Long retention_users_d7_latest;
    private Long retention_users_d7_sum;
    private Float retention_users_d7_avg;
    private List<Long> retention_users_d7;

    private Long retention_users_w1_latest;
    private Long retention_users_w1_sum;
    private Float retention_users_w1_avg;
    private List<Long> retention_users_w1;

}
